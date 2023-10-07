package com.example.sampleopenglplayer;

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
    private final String TAG = SampleMediaCodec.class.getSimpleName();

    // Context for resource file playback
    private Context context;

    // Surface for rendering output buffer (image)
    private Surface surface;

    // Video file path
    private String videoPath = null;

    // MediaExtractor for sampling input video data from source
    MediaExtractor mediaExtractor;

    // MediaCodec used for decode video data provided by MediaExtractor
    MediaCodec decoder;

    // Indicates reach of input video data queue full, ready for render output buffer
    boolean inputBufferFull = false;

    // Indicates reach of end of stream
    boolean endOfStream = false;

    // Main play function
    // Create thread for real playback task without block UI thread
    public void play(Context c, Surface s, String v) {
        context = c;
        surface = s;
        videoPath = v;
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

    // Thread of task that fill up MediaCodec inputbuffer
    private void inputSamplingTask() throws IOException {
        // Available input buffer index
        int inputBufferIndex;
        // TODO: Optimize timeout value to improve performance
        int timeoutDequeueInUs = 0;
        // MediaCodec Input buffer
        ByteBuffer inputBuffer;
        // Input video data size
        int sampleSize;
        // Input video timestamp
        long sampleTime;

        endOfStream = false;
        while (true) {
            inputBufferIndex = decoder.dequeueInputBuffer(timeoutDequeueInUs);
            if (inputBufferIndex >= 0) {
                inputBufferFull = false;
                inputBuffer = decoder.getInputBuffer(inputBufferIndex);
                sampleSize = mediaExtractor.readSampleData(inputBuffer, 0);
                if (sampleSize > 0) {
                    // Video data is valid,send input buffer to MediaCodec for decode
                    sampleTime = mediaExtractor.getSampleTime();
                    decoder.queueInputBuffer(inputBufferIndex, 0, sampleSize, sampleTime, 0);
                    // Advance to next video data
                    if (!mediaExtractor.advance()) {
                        // // End-Of-Stream (EOS). No more data in video source.
                        break;
                    }
                } else {
                    // // End-Of-Stream (EOS). No more data in video source.
                    decoder.queueInputBuffer(inputBufferIndex, 0, 0, 0,
                            MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    break;
                }
            } else {
                inputBufferFull = true;
                sleep(100);
            }
        }
        Log.d(TAG, "End-Of-Stream");
        endOfStream = true;
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
        5. Create thread for fill up input buffer until End-Of-Stream
        6.  Looping until last frame of output buffer
        7. End of loop
        9. Stop MediaCodec
        10. Release MediaCodec, MediaExtractor
        */
        // Play resource video file or video file path
        mediaExtractor = new MediaExtractor();
        if (videoPath == null) {
            AssetFileDescriptor afd;
            afd = context.getResources().openRawResourceFd(R.raw.clipcanvas_14348_h264_640x360);
            mediaExtractor.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getDeclaredLength());
        } else {
            mediaExtractor.setDataSource(videoPath);
        }

        // Find and select first video track. No audio in this example
        int numTracks = mediaExtractor.getTrackCount();
        String mine_type = null;
        MediaFormat format = null;
        int trackSearchIndex;
        for (trackSearchIndex = 0; trackSearchIndex < numTracks; ++trackSearchIndex) {
            format = mediaExtractor.getTrackFormat(trackSearchIndex);
            mine_type = format.getString(MediaFormat.KEY_MIME);
            if (mine_type.startsWith("video/")) {
                // Must select the track we are going to get data by readSampleData()
                mediaExtractor.selectTrack(trackSearchIndex);
                // Set required key for MediaCodec in decoder mode
                // Check http://developer.android.com/reference/android/media/MediaFormat.html
                // TODO: Program codec KEYs with proper value
                format.setInteger(MediaFormat.KEY_CAPTURE_RATE, 24);
                format.setInteger(MediaFormat.KEY_PUSH_BLANK_BUFFERS_ON_STOP, 0);
                break;
            }
        }
        //  Check if valid track has been selected by selectTrack()
        if ((numTracks == 0) || (trackSearchIndex == numTracks)) {
            Log.d(TAG, "No video track found!");
            return;
        }

        // Initial MediaCodec as "decoder" with the MINE_TYPE of the selected track
        decoder = MediaCodec.createDecoderByType(mine_type);
        decoder.configure(format, surface, null, 0 /* 0:decoder 1:encoder */);
        decoder.start();

        // Thread for preparing input buffer without impact FPS
        new Thread(new Runnable() {
            public void run() {
                try {
                    inputSamplingTask();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // Count FPS
        counterTime = -1; // -1: Not initialized
        frameCount = 0;
        // Output buffer index
        int outputBufferIndex;
        // Wait until output buffer available for rendering
        int timeoutDequeueOutUs = -1;
        // Timestamp of last frame rendered
        long lastRenderTime = 0;
        // Time has consumed by loop when output buffer timestamp is eariler ss
        // MediaCodec BufferInfo for output buffer timestamp information, for frame rate control
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        // Main output buffer render loop until last frame
        while (true) {
            // Looping for fill up input buffer
            if (!inputBufferFull && !endOfStream) {
                continue;
            }

            // Wait until output buffer available for rendering with "timeoutDequeueOutUs = -1"
            outputBufferIndex = decoder.dequeueOutputBuffer(bufferInfo, timeoutDequeueOutUs);
            if (outputBufferIndex >= 0) {
                // Frame rate control
                if (lastRenderTime == 0) {
                    lastRenderTime = System.currentTimeMillis();
                } else {
                    long renderTime = lastRenderTime + (bufferInfo.presentationTimeUs / 1000);
                    while (renderTime > System.currentTimeMillis()) {
                        // Loop until  correct render time
                    }
                }

                // outputBuffer is ready to be processed or rendered.
                // If surface is SurfaceTexture, onFrameAvailable() will be called.
                decoder.releaseOutputBuffer(outputBufferIndex, true /*true:render to surface*/);

                // Count FPS
                frameCount++;
                if (counterTime > 0) {
                    deltaTime = System.currentTimeMillis() - counterTime;
                    if (deltaTime > 1000) {
                        Log.d(TAG, (((float) frameCount / (float) deltaTime) * 1000) + " fps");
                        counterTime = System.currentTimeMillis();
                        frameCount = 0;
                    }
                } else {
                    // Initialize FPS start count timestamp in first frame
                    counterTime = System.currentTimeMillis();
                }

                // End up rendering if last frame
                if (bufferInfo.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
                    Log.v(TAG, "Last frame");
                    break;
                }
            } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                Log.d(TAG, "Output buffer format is changed  " + decoder.getOutputFormat());
            } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                // Deprecated in API Level 21
                Log.d(TAG, "Output buffer is changed (deprecated) ");
            } else if (outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                Log.d(TAG, "Output buffer is not ready (" + outputBufferIndex + "), try again");
            } else {
                Log.d(TAG, "Other output buffer error (" + outputBufferIndex + ")");
            }
        }
        Log.v(TAG, "Play complete");
        decoder.stop();
        decoder.release();
        mediaExtractor.release();
    }
}
