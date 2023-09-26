package com.myl.mediacodedemo.encode

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.myl.mediacodedemo.R
import com.myl.mediacodedemo.databinding.ActivityEncodeBinding

class CameraActivity : AppCompatActivity() {

    companion object {
        fun startCameraActivity(context: Context) {
            context.startActivity(Intent(context, CameraActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityEncodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container, CameraFragment()).commit()
        }
    }
}