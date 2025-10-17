package com.example.learnmediacodec

import android.media.MediaCodecList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import java.io.File

class DetectCodecInfosActivity : AppCompatActivity() {
    private val TAG = "DetectCodecInfosActivit"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detect_codec_infos)

        val regularCodes = MediaCodecList(MediaCodecList.REGULAR_CODECS)
        val codecInfos = regularCodes.codecInfos
        val codecJsonStr = MediaClassJsonUtils.toJsonArray(codecInfos).toString()
        val cacheFile = File(externalCacheDir, "codec_infos.json")
        Log.d(TAG, "codec info json file: ${cacheFile.absolutePath}")
        cacheFile.writeText(codecJsonStr)

        val textView = findViewById<android.widget.TextView>(R.id.textview_codec_infos)
        textView.text = "save codec infos to ${cacheFile.absolutePath}"
    }
}