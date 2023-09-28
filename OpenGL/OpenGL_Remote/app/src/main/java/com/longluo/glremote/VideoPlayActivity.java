package com.longluo.glremote;

import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class VideoPlayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video);
    }


    private GLSurfaceView glSurfaceView;

    private SurfaceTexture surfaceTexture;



    private var glTextureProgram: GlTextureProgram? = null
    private var surfaceTexture: SurfaceTexture? = null

    private var surface: Surface? = null

    private val scene = GlScene()
    private val glRect = GlRect()
    private val glRoundRect = GlRoundRect().apply { setCornersPx(200) }
    private val glMesh = Gl2dMesh().also {
        it.setPoints(
                listOf(
                        PointF(0F, 0F),
                        PointF(0F, 1F),
                        PointF(1F, 0F),
                        PointF(0F, -1F),
                        PointF(-1F, 0F),
                        PointF(0.9F, 0.7F),
                        PointF(0.7F, 0.9F),
                        PointF(-0.9F, -0.7F),
                        PointF(-0.7F, -0.9F),
                        PointF(0.9F, -0.7F),
                        PointF(0.7F, -0.9F),
                        PointF(-0.9F, 0.7F),
                        PointF(-0.7F, 0.9F)
                )
        )
    }

    private val glDrawables = listOf(glRect, glRoundRect, glMesh)
    private var glDrawable = 0
    private lateinit var player: SimpleExoPlayer

    private var videoWidth = -1
    private var videoHeight = -1
    private var surfaceWidth = -1
    private var surfaceHeight = -1

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        // Set up the player
        player = SimpleExoPlayer.Builder(this).build()
        val dataSourceFactory = DefaultDataSourceFactory(
                this,
                Util.getUserAgent(this, "Egloo")
        )

//        val videoSource = ProgressiveMediaSource.Factory(dataSourceFactory)
//            // .createMediaSource(Uri.parse("https://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"))
//            .createMediaSource(MediaItem.fromUri("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"))

        val videoSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                // .createMediaSource(Uri.parse("https://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"))
                .createMediaSource(MediaItem.fromUri("https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/1080/Big_Buck_Bunny_1080_10s_30MB.mp4"))

        player.prepare(videoSource)
        player.playWhenReady = true

        // Set up the gl surface view
        glSurfaceView = findViewById(R.id.gl_surface_view)
        glSurfaceView.setZOrderOnTop(true)
        glSurfaceView.holder.setFormat(PixelFormat.RGBA_8888)
        glSurfaceView.setEGLContextFactory(EglContextFactory.GLES2)
        glSurfaceView.setEGLConfigChooser(EglConfigChooser.GLES2)
        glSurfaceView.setRenderer(this)
        glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
        glSurfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {}
            override fun surfaceChanged(
                    holder: SurfaceHolder,
                    format: Int,
                    width: Int,
                    height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                onSurfaceDestroyed()
            }
        })

        // Address the aspect ratio
        player.addVideoListener(object : VideoListener {
            override fun onVideoSizeChanged(
                    width: Int, height: Int,
                    unappliedRotationDegrees: Int,
                    pixelWidthHeightRatio: Float
            ) {
                videoWidth = width
                videoHeight = height
                onVideoOrSurfaceSizeChanged()
            }
        })

        // On touch, change the current drawable.
        glSurfaceView.setOnTouchListener { v, event ->
                glDrawable++
            if (glDrawable > glDrawables.lastIndex) {
                glDrawable = 0
            }
            false
        }
        Toast.makeText(this, "Touch the screen to change shape.", Toast.LENGTH_LONG).show()
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        // Configure GL
        val glTexture = GlTexture()
        glTextureProgram = GlTextureProgram()
        glTextureProgram!!.texture = glTexture

        // Configure the player
        surfaceTexture = SurfaceTexture(glTexture.id)
        surfaceTexture!!.setOnFrameAvailableListener {
            glSurfaceView.requestRender()
        }
        surface = Surface(surfaceTexture!!)
        glSurfaceView.post {
            player.setVideoSurface(surface)
        }
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        scene.setViewportSize(width, height)
        surfaceWidth = width
        surfaceHeight = height
        onVideoOrSurfaceSizeChanged()
    }

    override fun onDrawFrame(gl: GL10) {
        val texture = surfaceTexture ?: return
                val program = glTextureProgram ?: return
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        texture.updateTexImage()
        texture.getTransformMatrix(program.textureTransform)
        scene.draw(program, glDrawables[glDrawable])
    }

    private fun onSurfaceDestroyed() {
        player.setVideoSurface(null)
        glTextureProgram?.release()
        glTextureProgram = null
        surfaceTexture?.release()
        surfaceTexture = null
        surface?.release()
        surface = null
    }

    private fun onVideoOrSurfaceSizeChanged() {
        if (videoWidth == -1 || videoHeight == -1) return
        if (surfaceWidth == -1 || surfaceHeight == -1) return
                val videoRatio = videoWidth.toFloat() / videoHeight
        val surfaceRatio = surfaceWidth.toFloat() / surfaceHeight
        val viewport: RectF
        if (videoRatio > surfaceRatio) {
            // Video is wider. We should collapse height.
            val surfaceRealHeight = surfaceWidth / videoRatio
            val surfaceRealHeightEgloo = surfaceRealHeight / surfaceHeight * 2
            viewport = RectF(
                    -1F, surfaceRealHeightEgloo / 2F,
                    1F, -surfaceRealHeightEgloo / 2F
            )
        } else if (videoRatio < surfaceRatio) {
            // Video is taller. We should collapse width
            val surfaceRealWidth = surfaceHeight * videoRatio
            val surfaceRealWidthEgloo = surfaceRealWidth / surfaceWidth * 2
            viewport = RectF(
                    -surfaceRealWidthEgloo / 2F, 1F,
                    surfaceRealWidthEgloo / 2F, -1F
            )
        } else {
            viewport = RectF(-1F, 1F, 1F, -1F)
        }
        glRect.setRect(viewport)
        glRoundRect.setRect(viewport)
        glMesh.modelMatrix.makeIdentity()
        glMesh.modelMatrix.scaleX(viewport.width() / 2F)
        glMesh.modelMatrix.scaleY(-viewport.height() / 2F)
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.release();
    }
}
