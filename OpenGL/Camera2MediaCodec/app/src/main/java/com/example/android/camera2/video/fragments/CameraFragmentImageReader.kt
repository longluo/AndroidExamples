/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.camera2.video.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture.OnFrameAvailableListener
import android.hardware.camera2.*
import android.media.Image
import android.media.ImageReader
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.android.camera.utils.OrientationLiveData
import com.example.android.camera2.video.R
import com.example.android.camera2.video.encoder.BitmapToVideoEncoder
import com.example.android.camera2.video.encoder.YuvToRgbConverter
import kotlinx.android.synthetic.main.fragment_camera.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class CameraFragmentImageReader : Fragment() {


    /** AndroidX navigation arguments */
    private val args: CameraFragmentImageReaderArgs by navArgs()

    /** Detects, characterizes, and connects to a CameraDevice (used for all camera operations) */
    private val cameraManager: CameraManager by lazy {
        val context = requireContext().applicationContext
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    /** [CameraCharacteristics] corresponding to the provided Camera ID */
    private val characteristics: CameraCharacteristics by lazy {
        cameraManager.getCameraCharacteristics(args.cameraId)
    }

    /** File where the recording will be saved */
    private val outputFile: File by lazy { createFile(requireContext(), "mp4") }

    /** [HandlerThread] where all camera operations run */
    private val cameraThread = HandlerThread("CameraThread").apply { start() }

    private var cameraHandler: Handler? = null
    private var mRecordingEnabled // controls button state
            = false

    /** [HandlerThread] where all buffer reading operations run */
    private val imageReaderThread = HandlerThread("imageReaderThread").apply { start() }

    /** [Handler] corresponding to [imageReaderThread] */
    private val imageReaderHandler = Handler(imageReaderThread.looper)

    private var imageReader: ImageReader? = null

    /** Where the camera preview is displayed */
    private lateinit var surfaceView: SurfaceView

    /** Overlay on top of the camera preview */
    private lateinit var overlay: View

    /** Captures frames from a [CameraDevice] for our video recording */
    private lateinit var session: CameraCaptureSession

    /** The [CameraDevice] that will be opened in this fragment */
    private lateinit var camera: CameraDevice

    private val encoderCallback =
        BitmapToVideoEncoder.IBitmapToVideoEncoderCallback { outputFile ->
            Log.d(
                "CameraVideo",
                "File recording completed\n${outputFile.absolutePath}\n__"
            )
        }

    private val encoder = BitmapToVideoEncoder(encoderCallback)

    /** Requests used for preview only in the [CameraCaptureSession] */
    private fun previewRequest(surface: Surface): CaptureRequest =
        session.device.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW).apply {
            addTarget(surface)
            addTarget(imageReader!!.surface)
        }.build()


    /** Live data listener for changes in the device orientation relative to the camera */
    private lateinit var relativeOrientation: OrientationLiveData

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_2_camera, container, false)

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        overlay = view.findViewById(R.id.overlay)
        surfaceView = view.findViewById(R.id.view_finder)

        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder?) {
                initializeCamera()
            }

            override fun surfaceChanged(
                holder: SurfaceHolder?, format: Int, width: Int, height: Int
            ) {
                //TODO
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                //TODO
            }

        })

        cameraHandler = Handler(cameraThread.looper)

        mRecordingEnabled = false

        // Used to rotate the output media to match device orientation
        relativeOrientation = OrientationLiveData(requireContext(), characteristics).apply {
            observe(viewLifecycleOwner, Observer { orientation ->
                Log.d(TAG, "Orientation changed: $orientation")
            })
        }
    }

    /**
     * Begin all camera operations in a coroutine in the main thread. This function:
     * - Opens the camera
     * - Configures the camera session
     * - Starts the preview by dispatching a repeating request
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun initializeCamera() =
        lifecycleScope.launch(Dispatchers.Main) {

            // Open the selected camera
            camera = openCamera(cameraManager, args.cameraId, cameraHandler)

            // Creates list of Surfaces where the camera will output frames
            surfaceView.holder.setFixedSize(args.width, args.height)

            val converter = YuvToRgbConverter(requireContext())

            imageReader = ImageReader.newInstance(
                args.width, args.height, ImageFormat.YUV_420_888, 3
            ).apply {
                setOnImageAvailableListener({ reader ->
//                    Log.d("Camera", "new frame detected")
                    val i: Image? = reader.acquireLatestImage()
                    if (mRecordingEnabled && i != null) {
                        val bitmap = Bitmap.createBitmap(
                            args.width, args.height,
                            Bitmap.Config.ARGB_8888
                        );
                        converter.yuvToRgb(i, bitmap)
                        encoder.queueFrame(bitmap)
                    }
                    i?.close()
                }, imageReaderHandler)
            }

            val targets = listOf(surfaceView.holder.surface, imageReader!!.surface)

            // Start a capture session using our open camera and list of Surfaces where frames will go
            session = createCaptureSession(camera, targets, cameraHandler)

            // Sends the capture request as frequently as possible until the session is torn down or
            //  session.stopRepeating() is called
            session.setRepeatingRequest(
                previewRequest(surfaceView.holder.surface),
                null,
                cameraHandler
            )

            // React to user touching the capture button
            capture_button.setOnClickListener {
                if (mRecordingEnabled) {
                    encoder.stopEncoding()
                } else {
                    encoder.startEncoding(args.width, args.height, outputFile)
                }
                mRecordingEnabled = !mRecordingEnabled
            }
        }

    /** Opens the camera and returns the opened device (as the result of the suspend coroutine) */
    @SuppressLint("MissingPermission")
    private suspend fun openCamera(
        manager: CameraManager,
        cameraId: String,
        handler: Handler? = null
    ): CameraDevice = suspendCancellableCoroutine { cont ->
        manager.openCamera(cameraId, object : CameraDevice.StateCallback() {
            override fun onOpened(device: CameraDevice) = cont.resume(device)

            override fun onDisconnected(device: CameraDevice) {
                Log.w(TAG, "Camera $cameraId has been disconnected")
                requireActivity().finish()
            }

            override fun onError(device: CameraDevice, error: Int) {
                val msg = when (error) {
                    ERROR_CAMERA_DEVICE -> "Fatal (device)"
                    ERROR_CAMERA_DISABLED -> "Device policy"
                    ERROR_CAMERA_IN_USE -> "Camera in use"
                    ERROR_CAMERA_SERVICE -> "Fatal (service)"
                    ERROR_MAX_CAMERAS_IN_USE -> "Maximum cameras in use"
                    else -> "Unknown"
                }
                val exc = RuntimeException("Camera $cameraId error: ($error) $msg")
                Log.e(TAG, exc.message, exc)
                if (cont.isActive) cont.resumeWithException(exc)
            }
        }, handler)
    }

    /**
     * Creates a [CameraCaptureSession] and returns the configured session (as the result of the
     * suspend coroutine)
     */
    private suspend fun createCaptureSession(
        device: CameraDevice,
        targets: List<Surface>,
        handler: Handler? = null
    ): CameraCaptureSession = suspendCoroutine { cont ->

        // Creates a capture session using the predefined targets, and defines a session state
        // callback which resumes the coroutine once the session is configured
        device.createCaptureSession(targets, object : CameraCaptureSession.StateCallback() {

            override fun onConfigured(session: CameraCaptureSession) = cont.resume(session)

            override fun onConfigureFailed(session: CameraCaptureSession) {
                val exc = RuntimeException("Camera ${device.id} session configuration failed")
                Log.e(TAG, exc.message, exc)
                cont.resumeWithException(exc)
            }
        }, handler)
    }


    override fun onPause() {
        super.onPause()
        encoder.stopEncoding()
//        surfaceView.queueEvent { // Tell the renderer that it's about to be paused so it can clean up.
//            mRenderer!!.notifyPausing()
//        }
//        surfaceView.onPause()
    }

    override fun onStop() {
        super.onStop()
        try {
            camera.close()
        } catch (exc: Throwable) {
            Log.e(TAG, "Error closing camera", exc)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraThread.quitSafely()
    }

    companion object {
        private val TAG = CameraFragmentImageReader::class.java.simpleName

        /** Creates a [File] named with the current date and time */
        private fun createFile(context: Context, extension: String): File {
            val sdf = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS", Locale.US)
            return File(context.externalCacheDir, "VID_${sdf.format(Date())}.$extension")
        }
    }

    private val frameListener =
        OnFrameAvailableListener { // The SurfaceTexture uses this to signal the availability of a new frame.  The
            // thread that "owns" the external texture associated with the SurfaceTexture (which,
            // by virtue of the context being shared, *should* be either one) needs to call
            // updateTexImage() to latch the buffer.
            //
            // Once the buffer is latched, the GLSurfaceView thread can signal the encoder thread.
            // This feels backward -- we want recording to be prioritized over rendering -- but
            // since recording is only enabled some of the time it's easier to do it this way.
            //
            // Since GLSurfaceView doesn't establish a Looper, this will *probably* execute on
            // the main UI thread.  Fortunately, requestRender() can be called from any thread,
            // so it doesn't really matter.

            // The SurfaceTexture uses this to signal the availability of a new frame.  The
            // thread that "owns" the external texture associated with the SurfaceTexture (which,
            // by virtue of the context being shared, *should* be either one) needs to call
            // updateTexImage() to latch the buffer.
            //
            // Once the buffer is latched, the GLSurfaceView thread can signal the encoder thread.
            // This feels backward -- we want recording to be prioritized over rendering -- but
            // since recording is only enabled some of the time it's easier to do it this way.
            //
            // Since GLSurfaceView doesn't establish a Looper, this will *probably* execute on
            // the main UI thread.  Fortunately, requestRender() can be called from any thread,
            // so it doesn't really matter.
//            surfaceView.requestRender()
        }
}