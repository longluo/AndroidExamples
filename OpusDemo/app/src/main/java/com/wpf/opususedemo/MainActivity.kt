/*
 * Coding by Zhonghua. from 18-9-30 上午10:02.
 */

package com.wpf.opususedemo

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.wpf.opususedemo.utils.OpusPlayTask
import com.wpf.opususedemo.utils.OpusRecorderTask
import java.io.File

@RequiresApi(Build.VERSION_CODES.M)
class MainActivity : AppCompatActivity(), View.OnClickListener, OpusPlayTask.OnOpusPlayListener {
    private val TAG = javaClass.simpleName
    val APP_PATH: String = Environment.getExternalStorageDirectory().toString() + File.separator + "opusaudiodemo" + File.separator
    val APP_RECORDER_FILE_PATH: String = APP_PATH + "recorder_file" + File.separator

    private lateinit var recorderBtn: Button
    private lateinit var playBtn: Button
    private lateinit var playPcmBtn: Button


    private var recorderFilePath: String = APP_RECORDER_FILE_PATH + "recorder.ops"
    private var recorderPcmFilePath: String = APP_RECORDER_FILE_PATH + "recorder.pcm"
    private var recorderDecodedPcmFilePath: String = APP_RECORDER_FILE_PATH + "recorderDecoded.pcm"//解码后的PCM文件

    private var opusPlayTask: OpusPlayTask? = null
    private var opusRecorderTask: OpusRecorderTask? = null
    private lateinit var audioManager: AudioManager

    private val permissionRequestCode = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        audioManager = getSystemService(AudioManager::class.java)
        initView()
        initData()
    }

    private fun initData() {
        recorderBtn.setOnClickListener(this)
        playBtn.setOnClickListener(this)
        playPcmBtn.setOnClickListener(this)
    }

    private fun initView() {
        recorderBtn = findViewById(R.id.recorder_btn)
        playBtn = findViewById(R.id.play_btn)
        playPcmBtn = findViewById(R.id.play_pcm_btn)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.recorder_btn -> {
                if (opusRecorderTask == null) {
                    startRecorder()
                } else {
                    stopRecorder()
                }
            }
            R.id.play_btn -> {
                if (opusPlayTask == null) {
                    startPlay()
                } else {
                    stopPlay()
                }
            }
            R.id.play_pcm_btn -> {
                if (opusPlayTask == null) {
                    startPcmPlay()
                } else {
                    stopPlay()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            permissionRequestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startRecorder()
                } else {
                    toast(getString(R.string.permission_denied))
                }
            }
        }
    }


    private fun startRecorder() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
            toast(getString(R.string.record_permission_denied))
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), permissionRequestCode)
            return
        }
        if (opusRecorderTask != null) {
            toast(getString(R.string.play_error))
            return
        }
        recorderBtn.setText(R.string.stop_record)
        playPcmBtn.isEnabled = false
        playBtn.isEnabled = false

        opusRecorderTask = OpusRecorderTask(recorderFilePath, recorderPcmFilePath)
        Thread(opusRecorderTask).start()
    }

    private fun stopRecorder() {
        recorderBtn.setText(R.string.start_record)
        playPcmBtn.isEnabled = true
        playBtn.isEnabled = true
        if (opusRecorderTask != null) {
            opusRecorderTask!!.stop()
            opusRecorderTask = null
        }
    }

    private fun startPlay() {
        if (!File(recorderFilePath).exists() || opusPlayTask != null) {
            toast(getString(R.string.play_opus_error))
            return
        }
        recorderBtn.isEnabled = false
        playPcmBtn.isEnabled = false
        playBtn.setText(R.string.stop_opus)
        opusPlayTask = OpusPlayTask(audioManager, recorderFilePath, recorderDecodedPcmFilePath)
        opusPlayTask!!.onOpusPlayListener = this
        Thread(opusPlayTask).start()
    }

    private fun startPcmPlay() {
        if (!File(recorderPcmFilePath).exists() || opusPlayTask != null) {
            toast(getString(R.string.play_pcm_error))
            return
        }
        recorderBtn.isEnabled = false
        playBtn.isEnabled = false
        playPcmBtn.setText(R.string.stop_pcm)
        opusPlayTask = OpusPlayTask(audioManager, recorderPcmFilePath, null, true)
        opusPlayTask!!.onOpusPlayListener = this
        Thread(opusPlayTask).start()
    }

    private fun stopPlay() {
        playBtn.setText(R.string.play_opus_and_save_pcm)
        playPcmBtn.setText(R.string.play_pcm)
        playPcmBtn.isEnabled = true
        playBtn.isEnabled = true
        recorderBtn.isEnabled = true
        if (opusPlayTask != null) {
            opusPlayTask!!.stop()
            opusPlayTask = null
        }
    }

    override fun onCompere() {
        runOnUiThread {
            playBtn.setText(R.string.play_opus_and_save_pcm)
            playPcmBtn.setText(R.string.play_pcm)
            playPcmBtn.isEnabled = true
            playBtn.isEnabled = true
            recorderBtn.isEnabled = true
        }
        if (opusPlayTask != null) {
            opusPlayTask = null
        }
    }

    fun log(msg: String) {
        Log.d(TAG, msg)
    }

    private var toast: Toast? = null
    private fun toast(msg: String) {
        if (toast != null) {
            toast!!.cancel()
            toast = null
        }
        toast = Toast.makeText(this, msg, Toast.LENGTH_LONG)
        toast!!.show()
    }
}
