package com.example.samplemediacodec;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;

import static android.os.SystemClock.sleep;

public class SampleMediaCodec {

    private Context mContext;

    private Surface mSurface;

    public void play(Context context, Surface surface) {
        mContext = context;
        mSurface = surface;

        new Thread(new Runnable() {
            public void run() {
                try {
                    playTask();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void playTask() throws IOException {
        long counterTime;
        long deltaTime;
        int frameCount;

        /*
        Flow of video playback
        1.  MediaExtractor set source video resource (R.raw.xxx)
        2.  MediaExtractor get video type (In MediaFormat) and select first video track ("video/")
        3.  MediaCodec creates decoder with video type (MediaFormat.KEY_MINE)
        4.  Configure MediaCodec as "decoder" and start()
        5.  Looping if not End-Of-Stream
        6.     Request (De-queue) input buffer from MediaCodec by dequeueInputBuffer()
        7.     Read video data source (SampleData) by MediaExtractor.readSampleData() to input buffer
        8.     if has valid video data,send input buffer to MediaCodec for decode
        9.     otherwise. set BUFFER_FLAG_END_OF_STREAM to MediaCodec, and set eos
        10.    Request (De-queue) output buffer from MediaCodec by dequeueOutputBuffer()
        11.    If video frame is valid in output buffer, render it on surface by releaseOutputBuffer()
        12. End of loop
        13. Release MediaCodec, MediaExtractor
        */

        AssetFileDescriptor afd;
        afd = mContext.getResources().openRawResourceFd(R.raw.clipcanvas_14348_h264_640x360);

        MediaExtractor mediaExtractor = new MediaExtractor();
        mediaExtractor.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getDeclaredLength());

        int numTracks = mediaExtractor.getTrackCount();
        String mine_type = null;
        MediaFormat format = null;

        for (int i = 0; i < numTracks; i++) {
            format = mediaExtractor.getTrackFormat(i);
            mine_type = format.getString(MediaFormat.KEY_MIME);
            if (mine_type.startsWith("video/")) {
                // Must select the track we are going to get data by readSampleData()
                mediaExtractor.selectTrack(i);
                // Set required key for MediaCodec in decoder mode
                // Check http://developer.android.com/reference/android/media/MediaFormat.html
                format.setInteger(MediaFormat.KEY_CAPTURE_RATE, 24);
                format.setInteger(MediaFormat.KEY_PUSH_BLANK_BUFFERS_ON_STOP, 1);
                break;
            }
        }

        // TODO: Check if valid track has been selected by selectTrack()
        MediaCodec decoder = MediaCodec.createDecoderByType(mine_type);
        decoder.configure(format, mSurface, null, 0 /* 0:decoder 1:encoder */);
        decoder.start();

        // Count FPS
        counterTime = System.currentTimeMillis();
        frameCount = 0;

        int timeoutUs = 1000000; // 1 second timeout

        boolean eos = false;
        long playStartTime = System.currentTimeMillis();
        long frameDisplayTime = playStartTime;

        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

        for (; (!eos); ) {
            int inputBufferIndex = decoder.dequeueInputBuffer(timeoutUs);

            if (inputBufferIndex >= 0) {
                ByteBuffer inputBuffer = decoder.getInputBuffer(inputBufferIndex);
                int sampleSize = mediaExtractor.readSampleData(inputBuffer, 0);

                if (sampleSize > 0) {
                    frameDisplayTime = (mediaExtractor.getSampleTime() >> 10) + playStartTime;
                    // Video data is valid, send input buffer to MediaCodec for decode
                    decoder.queueInputBuffer(inputBufferIndex, 0, sampleSize, mediaExtractor.getSampleTime(), 0);
                    mediaExtractor.advance();
                } else {
                    // End-Of-Stream (EOS)
                    decoder.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    eos = true;
                }
            }

            int outputBufferIndex = decoder.dequeueOutputBuffer(bufferInfo, timeoutUs);

            if (outputBufferIndex >= 0) {
                // Frame rate control
                while (frameDisplayTime > System.currentTimeMillis()) {
                    sleep(10);
                }

                // outputBuffer is ready to be processed or rendered.
                decoder.releaseOutputBuffer(outputBufferIndex, true /*true:render to surface*/);

                // Count FPS
                frameCount++;
                deltaTime = System.currentTimeMillis() - counterTime;

                if (deltaTime > 1000) {
                    Log.v("SampleMediaCodec", (((float) frameCount / (float) deltaTime) * 1000) + " fps");
                    counterTime = System.currentTimeMillis();
                    frameCount = 0;
                }
            }
        }

        decoder.stop();
        decoder.release();
        mediaExtractor.release();
    }
}
