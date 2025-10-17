package com.example.learnmediacodec

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn_detect_codec_infos).setOnClickListener {
            val intent = Intent(this, DetectCodecInfosActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btn_decode_to_bitmap).setOnClickListener {
            val intent = Intent(this, DecodeUsingBuffersActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btn_decode_to_surface).setOnClickListener {
            val intent = Intent(this, DecodeUsingSurfaceActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btn_encode_using_buffers).setOnClickListener {
            val intent = Intent(this, EncodeUsingBuffersActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btn_encode_using_surface).setOnClickListener {
            val intent = Intent(this, EncodeUsingSurfaceActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btn_encode_using_egl_and_surface).setOnClickListener {
            val intent = Intent(this, EncodeUsingEGLAndSurfaceActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btn_decode_to_texture_oes).setOnClickListener {
            val intent = Intent(this, DecodeToTextureOESActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btn_decode_to_texture_edit_play).setOnClickListener {
            val intent = Intent(this, DecodeEditPlay::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btn_decode_edit_encode).setOnClickListener {
            val intent = Intent(this, DecodeEditEncodeActivity::class.java)
            startActivity(intent)
        }
    }
}