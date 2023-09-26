package com.myl.mediacodedemo

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.myl.mediacodedemo.databinding.ActivityMainBinding
import com.myl.mediacodedemo.decode.audio.AudioDecodeActivity
import com.myl.mediacodedemo.decode.video.VideoDecodeActivity
import com.myl.mediacodedemo.encode.CameraActivity
import com.serenegiant.dialog.MessageDialogFragmentV4
import com.serenegiant.system.BuildCheck
import com.serenegiant.system.PermissionCheck

class MainActivity : AppCompatActivity(), MessageDialogFragmentV4.MessageDialogListener {

    private lateinit var activityMainBinding: ActivityMainBinding

    companion object {
        private const val ID_PERMISSION_REASON_AUDIO = R.string.permission_audio_recording_reason
        private const val ID_PERMISSION_REQUEST_AUDIO = R.string.permission_audio_recording_request
        private const val ID_PERMISSION_REASON_NETWORK = R.string.permission_network_reason
        private const val ID_PERMISSION_REQUEST_NETWORK = R.string.permission_network_request
        private const val ID_PERMISSION_REASON_EXT_STORAGE = R.string.permission_ext_storage_reason
        private const val ID_PERMISSION_REQUEST_EXT_STORAGE =
            R.string.permission_ext_storage_request
        private const val ID_PERMISSION_REASON_CAMERA = R.string.permission_camera_reason
        private const val ID_PERMISSION_REQUEST_CAMERA = R.string.permission_camera_request
        private const val ID_PERMISSION_REQUEST_HARDWARE_ID =
            R.string.permission_hardware_id_request
        private const val ID_PERMISSION_REASON_LOCATION = R.string.permission_location_reason
        private const val ID_PERMISSION_REQUEST_LOCATION = R.string.permission_location_request

        /** request code for WRITE_EXTERNAL_STORAGE permission  */
        private const val REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 0x1234

        /** request code for RECORD_AUDIO permission  */
        private const val REQUEST_PERMISSION_AUDIO_RECORDING = 0x2345

        /** request code for CAMERA permission  */
        private const val REQUEST_PERMISSION_CAMERA = 0x3456

        /** request code for INTERNET permission  */
        private const val REQUEST_PERMISSION_NETWORK = 0x4567

        /** request code for READ_PHONE_STATE permission  */
        private const val REQUEST_PERMISSION_HARDWARE_ID = 0x5678

        /** request code for ACCESS_FINE_LOCATION permission  */
        private const val REQUEST_PERMISSION_LOCATION = 0x6789
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        initView()
        checkPermission()
    }

    private fun initView() {
        activityMainBinding.audioDecode.setOnClickListener {
            AudioDecodeActivity.startAudioDecodeActivity(this)
        }
        activityMainBinding.videoDecode.setOnClickListener {
            VideoDecodeActivity.startVideoDecodeActivity(this)
        }
        activityMainBinding.videoEncode.setOnClickListener {
            CameraActivity.startCameraActivity(this)
        }
    }

    private fun checkPermission(): Boolean {
        return (checkPermissionCamera()
                && checkPermissionAudio()
                && checkPermissionWriteExternalStorage())
    }

    private fun checkPermissionWriteExternalStorage(): Boolean {
        if (!PermissionCheck.hasWriteExternalStorage(this)) {
            MessageDialogFragmentV4.showDialog(
                this,
                REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE,
                R.string.permission_title,
                ID_PERMISSION_REQUEST_EXT_STORAGE,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            )
            return false
        }
        return true
    }

    private fun checkPermissionAudio(): Boolean {
        if (!PermissionCheck.hasAudio(this)) {
            MessageDialogFragmentV4.showDialog(
                this,
                REQUEST_PERMISSION_AUDIO_RECORDING,
                R.string.permission_title,
                ID_PERMISSION_REQUEST_AUDIO,
                arrayOf(Manifest.permission.RECORD_AUDIO)
            )
            return false
        }
        return true
    }

    private fun checkPermissionCamera(): Boolean {
        if (!PermissionCheck.hasCamera(this)) {
            MessageDialogFragmentV4.showDialog(
                this,
                REQUEST_PERMISSION_CAMERA,
                R.string.permission_title,
                ID_PERMISSION_REQUEST_CAMERA,
                arrayOf(Manifest.permission.CAMERA)
            )
            return false
        }
        return true
    }

    @SuppressLint("NewApi")
    override fun onMessageDialogResult(
        dialog: MessageDialogFragmentV4, requestCode: Int,
        permissions: Array<String?>, result: Boolean
    ) {
        when (requestCode) {
            REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE, REQUEST_PERMISSION_AUDIO_RECORDING, REQUEST_PERMISSION_CAMERA,
            REQUEST_PERMISSION_NETWORK, REQUEST_PERMISSION_HARDWARE_ID,
            REQUEST_PERMISSION_LOCATION -> {
                if (result) {
                    if (BuildCheck.isMarshmallow()) {
                        requestPermissions(permissions, requestCode)
                        return
                    }
                }
                for (permission in permissions) {
                    checkPermissionResult(
                        requestCode, permission,
                        PermissionCheck.hasPermission(this, permission)
                    )
                }
            }
        }
    }

    private fun checkPermissionResult(
        requestCode: Int,
        permission: String?, result: Boolean
    ) {
        if (!result && permission != null) {
            val sb = StringBuilder()
            if (Manifest.permission.RECORD_AUDIO == permission) {
                sb.append(getString(R.string.permission_audio))
            }
            if (Manifest.permission.WRITE_EXTERNAL_STORAGE == permission) {
                if (sb.isNotEmpty()) {
                    sb.append("\n")
                }
                sb.append(getString(R.string.permission_ext_storage))
            }
            if (Manifest.permission.CAMERA == permission) {
                if (sb.isNotEmpty()) {
                    sb.append("\n")
                }
                sb.append(getString(R.string.permission_camera))
            }
            if (Manifest.permission.INTERNET == permission) {
                if (sb.isNotEmpty()) {
                    sb.append("\n")
                }
                sb.append(getString(R.string.permission_network))
            }
            if (Manifest.permission.ACCESS_FINE_LOCATION == permission) {
                if (sb.isNotEmpty()) {
                    sb.append("\n")
                }
                sb.append(getString(R.string.permission_location))
            }
            Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val n = permissions.size.coerceAtMost(grantResults.size)
        for (i in 0 until n) {
            checkPermissionResult(
                requestCode, permissions[i],
                grantResults[i] == PackageManager.PERMISSION_GRANTED
            )
        }
        checkPermission()
    }
}