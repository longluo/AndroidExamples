/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.exoplayer

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.exoplayer.ExoPlayer
import com.example.exoplayer.databinding.ActivityPlayerBinding

/**
 * A fullscreen activity to play audio or video streams.
 */
class PlayerActivity : AppCompatActivity() {

  private val viewBinding by lazy(LazyThreadSafetyMode.NONE) {
    ActivityPlayerBinding.inflate(layoutInflater)
  }

  private var player: ExoPlayer? = null

  private var playWhenReady = true
  private var currentItem = 0
  private var playbackPosition = 0L

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(viewBinding.root)
  }

  public override fun onStart() {
    super.onStart()
    if (Build.VERSION.SDK_INT > 23) {
      initializePlayer()
    }
  }

  public override fun onResume() {
    super.onResume()
    hideSystemUi()
    if (Build.VERSION.SDK_INT <= 23 || player == null) {
      initializePlayer()
    }
  }

  public override fun onPause() {
    super.onPause()
    if (Build.VERSION.SDK_INT <= 23) {
      releasePlayer()
    }
  }

  public override fun onStop() {
    super.onStop()
    if (Build.VERSION.SDK_INT > 23) {
      releasePlayer()
    }
  }

  private fun initializePlayer() {
    player = ExoPlayer.Builder(this).build().also { exoPlayer ->
      viewBinding.videoView.player = exoPlayer

      // Update the track selection parameters to only pick standard definition tracks
      exoPlayer.trackSelectionParameters =
        exoPlayer.trackSelectionParameters.buildUpon().setMaxVideoSizeSd().build()

      val mediaItem = MediaItem.Builder().setUri(getString(R.string.media_url_dash))
        .setMimeType(MimeTypes.APPLICATION_MPD).build()
      exoPlayer.setMediaItems(listOf(mediaItem), currentItem, playbackPosition)
      exoPlayer.playWhenReady = playWhenReady
      exoPlayer.prepare()
    }
  }

  private fun releasePlayer() {
    player?.let { exoPlayer ->
      playbackPosition = exoPlayer.currentPosition
      currentItem = exoPlayer.currentMediaItemIndex
      playWhenReady = exoPlayer.playWhenReady
      exoPlayer.release()
    }
    player = null
  }

  @SuppressLint("InlinedApi")
  private fun hideSystemUi() {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    WindowInsetsControllerCompat(window, viewBinding.videoView).let { controller ->
      controller.hide(WindowInsetsCompat.Type.systemBars())
      controller.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
  }
}