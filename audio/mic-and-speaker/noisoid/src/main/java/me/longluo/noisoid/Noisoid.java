package me.longluo.noisoid;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;

import java.util.Arrays;
import java.util.Vector;


public class Noisoid {

    private boolean tStop;

    private final Vector<Source> sources;

    AudioTrack mAudioTrack;
    private short[] buf;
    private final int sampleRate;
    private final int bufferMillis;
    private final int numChannels;

    public Noisoid(int sampleRate, int bufferMillis) {
        this.sampleRate = sampleRate;
        this.bufferMillis = bufferMillis;
        this.numChannels = 2;
        sources = new Vector<>();
    }

    @SuppressWarnings("unused")
    public int getSampleRate() { return sampleRate; }

    public void start() {
        int bufferSizeInShorts =
                (int) (((long) this.sampleRate * this.bufferMillis) / 1000) * numChannels;
        this.buf = new short[bufferSizeInShorts];

        AudioAttributes.Builder aab = new AudioAttributes.Builder();
        aab.setContentType(AudioAttributes.CONTENT_TYPE_MUSIC);
        if(Build.VERSION.SDK_INT >= 29) {
            aab.setAllowedCapturePolicy(AudioAttributes.ALLOW_CAPTURE_BY_ALL);
        }
        aab.setUsage(AudioAttributes.USAGE_MEDIA);

        AudioFormat.Builder afb = new AudioFormat.Builder()
                .setSampleRate(sampleRate)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO);

        if(Build.VERSION.SDK_INT >= 26) {
            AudioTrack.Builder atb = new AudioTrack.Builder()
                    .setAudioAttributes(aab.build())
                    .setAudioFormat(afb.build())
                    .setBufferSizeInBytes(buf.length*2)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .setPerformanceMode(AudioTrack.PERFORMANCE_MODE_LOW_LATENCY);
            mAudioTrack = atb.build();
        } else {
            mAudioTrack = new AudioTrack(
                    aab.build(),
                    afb.build(),
                    buf.length*2, AudioTrack.MODE_STREAM,
                    AudioManager.AUDIO_SESSION_ID_GENERATE);
        }

        Thread t = new Thread() {

            @Override
            public void run() {
                float[] bufFloat = new float[buf.length];

                tStop = false;
                while (!tStop) {
                    Arrays.fill(bufFloat, 0);

                    synchronized (sources) {
                        for(Source source : sources) {
                            source.readTo(bufFloat, 0, bufFloat.length);
                        }

                        for (int i = 0; i < bufFloat.length; i++) {
                            buf[i] = floatToShort(bufFloat[i]);
                        }
                    }

                    mAudioTrack.write(buf, 0, bufFloat.length);
                }
            }
        };

        t.setPriority(Thread.MAX_PRIORITY);
        t.start();

        mAudioTrack.play();
    }

    public void stop() { tStop = true; }

    static private short floatToShort(float sample) {
        if(sample >  1) { sample =  1; }
        if(sample < -1) { sample = -1; }
        sample *= 32767;
        return (short) sample;
    }

    public void addSource(Source source) { synchronized(sources) { sources.add(source); } }

    public void removeSource(int id) {
        if(id == -1) { return; }

        synchronized(sources) {
            for(Source source : sources) {
                if(source.getId() == id) {
                    sources.remove(source);
                    return;
                }
            }
        }
    }

    static public String about() {
        return BuildConfig.LIBRARY_PACKAGE_NAME + " v" + BuildConfig.VERSION_NAME;
    }
}
