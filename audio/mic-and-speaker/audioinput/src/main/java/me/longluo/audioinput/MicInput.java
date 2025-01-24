package me.longluo.audioinput;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.os.Build;


public class MicInput {

    private AudioRecord mAudioRecord;
    private Thread t;
    private boolean tStop;
    private boolean paused;

    private int encoding;

    private final int audioSource;
    private final int chFormat;
    private final int numChannels;
    private final int sampleRate;

    private AudioCable output;

    public MicInput(int audioSource, int chFormat, int sampleRate) {
        this.audioSource = audioSource;
        this.chFormat = chFormat;

        if (this.chFormat == AudioFormat.CHANNEL_IN_MONO) {
            this.numChannels = 1;
        } else if (this.chFormat == AudioFormat.CHANNEL_IN_STEREO) {
            this.numChannels = 2;
        } else {
            this.numChannels = 0;
        }

        this.sampleRate = sampleRate;
    }

    public int getAudioSource() {
        return audioSource;
    }

    public int getChFormat() {
        return chFormat;
    }

    public int getNumChannels() {
        return numChannels;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public boolean init(Context context) {

        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                new Exception("permission RECORD_AUDIO not granted").printStackTrace(System.err);
                return false;
            }
        }

        encoding = AudioFormat.ENCODING_PCM_16BIT;

        int minBufSize;
        int bufferSize;
        int state;

        minBufSize = getMinBufSizeInBytes();
        bufferSize = minBufSize * 256;

        // create AudioRecord with biggest accepted buffer size
        // (max. minBufSize*256)

        while (true) {
            mAudioRecord = null;

            try {
                mAudioRecord = new AudioRecord(audioSource, sampleRate, chFormat, encoding, bufferSize);
                // increase buffer size if overrun

                state = mAudioRecord.getState();
            } catch (Exception e) {
                e.printStackTrace(System.err);
                state = AudioRecord.STATE_UNINITIALIZED;
            }

            if (state == AudioRecord.STATE_INITIALIZED) {
                break;
            } // ar init OK

            // else -> bufferSize too big

            if (mAudioRecord != null) {
                mAudioRecord.release();
                mAudioRecord = null;
            }

            bufferSize /= 2;
            if (bufferSize < minBufSize) {
                new Exception("AudioRecord init ERR").printStackTrace(System.err);
                break; // ar init ERR
            }
        }

        return (mAudioRecord != null); // true -> init OK, false -> init failed
    }


    public void connectOutputTo(AudioCable cable) {
        this.output = cable;
    }

    public void start() {
        if (mAudioRecord == null) {
            return;
        }

        //noinspection FieldCanBeLocal
        t = new Thread() {
            private int n;
            private int i;
            private int ch;
            private final float[] sample = new float[numChannels];
            private int read;
            private short[] buf;

            public void run() {

                n = getMinBufSizeInBytes() / 2; // 1 short = 2 bytes
                buf = new short[n];

                mAudioRecord.startRecording();

                tStop = !(mAudioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING);

                while (!tStop) {
                    read = mAudioRecord.read(buf, 0, n); // read max n shorts

                    if (read == 0) {
                        // new audio data not available yet
                        try {
                            //noinspection BusyWait
                            Thread.sleep(0, 250);
                        } catch (Exception e) { /**/ }
                    }

                    if (paused) {
                        continue;
                    }

                    if (output == null) {
                        continue;
                    }

                    try {
                        for (i = 0; i < read; i++) {
                            sample[ch++] = buf[i] / 32768.0f;
                            if (ch >= numChannels) {
                                ch = 0;
                                output.send(sample);
                            }
                        }

                        output.endOfFrame(); // indicator for receiver to start processing
                    } catch (Exception e) { // something wrong with the receiver of the output
                        e.printStackTrace(System.err);
                        outputEndOfStream();
                        break;
                    }
                }

                mAudioRecord.stop();
                mAudioRecord.release();
                mAudioRecord = null;
                t = null;

                outputEndOfStream();
            }
        };

        t.setPriority(Thread.MAX_PRIORITY);
        t.start();
    }

    private void outputEndOfStream() {
        try {
            output.endOfStream();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        paused = false;
    }

    public boolean isPaused() {
        return paused;
    }

    public void stop() {
        paused = false;
        tStop = true;
    }

    public boolean isStopped() {
        return (t == null);
    }

    private int getMinBufSizeInBytes() {
        return AudioRecord.getMinBufferSize(sampleRate, chFormat, encoding);
    }
}