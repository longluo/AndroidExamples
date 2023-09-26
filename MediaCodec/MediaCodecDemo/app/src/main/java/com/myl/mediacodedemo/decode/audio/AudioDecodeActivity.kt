package com.myl.mediacodedemo.decode.audio

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.myl.mediacodedemo.R
import com.myl.mediacodedemo.databinding.AudioActivityBinding

class AudioDecodeActivity : AppCompatActivity() {


    companion object {
        fun startAudioDecodeActivity(context: Context) {
            context.startActivity(Intent(context, AudioDecodeActivity::class.java))
        }
    }

    private val audioDecoder: AudioDecoder by lazy {
        AudioDecoder()
    }

    private val audioPathAAC by lazy {
        resources.openRawResourceFd(R.raw.bensound_littleidea_aac)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = AudioActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.playAac.setOnClickListener {
            playAac()
        }
    }

    private fun playAac() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (audioDecoder.init(audioPathAAC)) {
                audioDecoder.start()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        audioDecoder.release()
    }
}