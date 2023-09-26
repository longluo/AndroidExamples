package com.example.splash.activity

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.splash.databinding.ActivityMainSimpleBinding
import com.example.splash.viewmodel.MyViewModel

class OldMainActivity : AppCompatActivity() {

    private val viewModel: MyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainSimpleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Test for hard work to keep splash screen longer.
        // doSomeHardWork()
    }

    private fun doSomeHardWork() {
        Log.d("Splash", "SplashActivity#doSomeHardWork()")
        val content: View = findViewById(android.R.id.content)

        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    Log.d(
                        "Splash",
                        "SplashActivity#onPreDraw() currentTime:${SystemClock.uptimeMillis()}"
                    )

                    return if (viewModel.isDataReady()) {
                        Log.d("Splash", "SplashActivity#onPreDraw() proceed")
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        Log.d("Splash", "SplashActivity#onPreDraw() suspend")
                        false
                    }
                }
            }
        )
    }
}