package me.longluo.audio;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioDeviceCallback;
import android.media.AudioDeviceInfo;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.longluo.audio.util.ColorUtil;
import me.longluo.audio.util.GuiUtil;
import me.longluo.audio.util.ThemeUtil;
import me.longluo.audioinput.AudioCable;
import me.longluo.audioinput.MicInput;
import me.longluo.noisoid.Noisoid;
import me.longluo.noisoid.SineGenerator;
import timber.log.Timber;


public class MainActivity extends Activity {

    private static final String TAG = "luolong";

    private static final int BUFFER_COUNT = 2;
    private static final int NUM_CHANNELS = 2;
    private static final int PLOT_BUFFER_MILLIS = 100;

    private static MicInput micInput = null;
    private static float[][] buffer = null;
    private static int bufferIndex;
    private static int indexInBuffer;
    private static PlotView[] pvPlots;

    private Button mBtnStart;
    private Button mBtnStop;

    private Button mBtn440Hz;
    private Button mBtn2000Hz;

    private static final int[] frequencies = {440, 2000};
    private static int[][] sourceIds;

    private static final int RQ_CODE_SETTINGS = 1;

    private static Noisoid noisoid = null;

    private Settings settings;
    private LinearLayout llMenuOptions;

    private AudioManager mAudioManager;

    private TextView mTvAudioInput;
    private TextView mTvAudioOutput;

    Handler mHandler = new Handler(Looper.getMainLooper()) {


    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.registerAudioDeviceCallback(audioDeviceCallback, mHandler);

        settings = new Settings(getSharedPreferences(
                getPackageName(), Context.MODE_PRIVATE));

        ThemeUtil.setResIdThemeLight(R.style.AppThemeLight);
        ThemeUtil.setResIdThemeDark(R.style.AppThemeDark);
        ThemeUtil.set(this, settings.getTheme());

        initBuffers();
        initGUI();
        initAudioDevices();

        int sampleRate = (int) settings.getSampleRate();

        if (noisoid == null) {
            noisoid = new Noisoid(sampleRate, 10);
            noisoid.start();
            sourceIds = new int[3][2];
            for (int i = 0; i < sourceIds.length; i++) {
                for (int j = 0; j < sourceIds[0].length; j++) {
                    sourceIds[i][j] = -1;
                }
            }
        }
    }

    private void initBuffers() {
        if (buffer != null) {
            return;
        } // already initialized

        // buffer will contain data of both channels (left, right)
        int floats = (int) (PLOT_BUFFER_MILLIS * settings.getSampleRate() * NUM_CHANNELS / 1000);
        buffer = new float[BUFFER_COUNT][floats];
        bufferIndex = 0;
        indexInBuffer = 0;
    }

    private void initAudioDevices() {
        mTvAudioInput = findViewById(R.id.tv_in_microphone_list);
        mTvAudioOutput = findViewById(R.id.tv_out_speaker_list);

        StringBuilder inputStr = new StringBuilder(64);
        String[] inputDevices = getAudioInputDevicesNames(mAudioManager);

        for (String device : inputDevices) {
            inputStr.append(device).append("\n");
        }
        mTvAudioInput.setText(inputStr.toString());

        StringBuilder outputStr = new StringBuilder(64);
        String[] outputDevices = getAudioOutDevicesNames(mAudioManager);

        for (String device : outputDevices) {
            outputStr.append(device).append("\n");
        }
        mTvAudioOutput.setText(outputStr.toString());
    }

    public String[] getAudioInputDevicesNames(AudioManager audioManager) {
        AudioDeviceInfo[] devices = audioManager.getDevices(AudioManager.GET_DEVICES_INPUTS);

        String[] inputStr = new String[devices.length];

        for (int i = 0; i < devices.length; i++) {
            AudioDeviceInfo device = devices[i];
            inputStr[i] = device.getProductName() + "," + device.getId() + "," + device.getType();
        }

        return inputStr;
    }

    public String[] getAudioOutDevicesNames(AudioManager audioManager) {
        AudioDeviceInfo[] devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);

        String[] outputStr = new String[devices.length];

        for (int i = 0; i < devices.length; i++) {
            AudioDeviceInfo device = devices[i];
            outputStr[i] = device.getProductName() + "," + device.getId() + "," + device.getType();
        }

        return outputStr;
    }

    @SuppressLint({"ClickableViewAccessibility", "InflateParams"})
    private void initGUI() {
        // we use our own title bar in "layout_main"
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        FrameLayout flMenuOptions = findViewById(R.id.fl_menu_options);
        llMenuOptions = (LinearLayout) flMenuOptions.getChildAt(0);

        findViewById(R.id.iv_menu).setOnClickListener(view -> {
            if (llMenuOptions.getVisibility() == View.VISIBLE) {
                llMenuOptions.setVisibility(View.GONE);
            } else if (llMenuOptions.getVisibility() == View.GONE) {
                llMenuOptions.setVisibility(View.VISIBLE);
            }
        });

        llMenuOptions.setVisibility(View.GONE);
        flMenuOptions.setOnTouchListener((v, event) -> {
            if (llMenuOptions.getVisibility() == View.VISIBLE) {
                llMenuOptions.setVisibility(View.GONE);
                return true;
            }

            return false;
        });

        GuiUtil.setOnClickListenerToAllButtons(findViewById(R.id.ll_main), view -> {
            llMenuOptions.setVisibility(View.GONE);
            int id = view.getId();

            if (id == R.id.btn_about) {
                startActivity(new Intent(this, AboutActivity.class));
            }

            if (id == R.id.btn_settings) {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivityForResult(intent, RQ_CODE_SETTINGS);
            }
        });

        initPlots();

        mBtnStart = findViewById(R.id.btn_start);
        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStartMic();
            }
        });

        mBtnStop = findViewById(R.id.btn_stop);
        mBtnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStopMic();
            }
        });

        mBtn440Hz = findViewById(R.id.btn_440hz);
        mBtn2000Hz = findViewById(R.id.btn_2000hz);

        View.OnTouchListener otl = (v, event) -> {
            int action = event.getAction();

            if (action == MotionEvent.ACTION_DOWN) {
                onPressed(v);
            } else if (action == MotionEvent.ACTION_UP) {
                onReleased(v);
            }

            return false;
        };

        mBtn440Hz.setTag(0);
        mBtn440Hz.setOnTouchListener(otl);
        mBtn2000Hz.setTag(1);
        mBtn2000Hz.setOnTouchListener(otl);
    }

    private void onPressed(View view) {
        int sampleRate = noisoid.getSampleRate();
        int freqId = (int) view.getTag();
        int id = view.getId();

        SineGenerator source = new SineGenerator(sampleRate, frequencies[freqId]);

        source.setAmplitude(0.8f, 0.8f);
        sourceIds[2][freqId] = source.getId();

        noisoid.addSource(source);
    }

    private void onReleased(View view) {
        int freqId = (int) view.getTag();
        int id = view.getId();

        int sourceId = sourceIds[2][freqId];

        noisoid.removeSource(sourceId);
    }

    @SuppressLint("ClickableViewAccessibility")
    private synchronized void initPlots() {
        LinearLayout llPlots = findViewById(R.id.ll_plots);
        llPlots.removeAllViews();

        LinearLayout.LayoutParams params;
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        params.weight = 1.0f;

        pvPlots = new PlotView[2];
        for (int i = 0; i < pvPlots.length; i++) {
            PlotView plotView = new PlotView(this);
            pvPlots[i] = plotView;
            plotView.setBuffer(buffer[0]);
            plotView.setNumChannels(2);
            plotView.setChannel(i);
            plotView.setBackgroundColor(ColorUtil.getColorBackground(this));
            plotView.setColor(ColorUtil.getColorForeground(this));
            plotView.setLineWidth(3);
            if (i < pvPlots.length - 1) {
                params.setMargins(0, 10, 0, 10);
            } else {
                params.setMargins(0, 10, 0, 0);
            }
            llPlots.addView(plotView, params);
            plotView.setOnTouchListener((v, event) -> {
                llMenuOptions.setVisibility(View.GONE);
                return false;
            });
        }
    }

    synchronized private void updatePlots() {
        for (PlotView plot : pvPlots) {
            // still will draw from old buffer
            plot.invalidate();

            // switch to new buffer:
            plot.setBuffer(buffer[bufferIndex]);
        }
    }

    private void onMicSample(float[] sample) {
        for (float f : sample) {
            buffer[bufferIndex][indexInBuffer++] = f * 50;
        }
        indexInBuffer %= buffer[0].length;

        if (indexInBuffer == 0) { // that buffer full

            // switch buffer:
            bufferIndex++;
            bufferIndex %= buffer.length;

            runOnUiThread(this::updatePlots);
        }
    }

    private void onStartMic() {
        if (micInput != null) {
            return;
        } // already started

        requestPermissions();
    }

    private void onStopMic() {
        if (micInput == null) {
            return;
        } // already stopped
        micInput.stop();

        while (!micInput.isStopped()) {
            try { //noinspection BusyWait
                Thread.sleep(5);
            } catch (Exception e) { /**/ }
        }

        micInput = null;
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT < 23) {
            onPermissionsGranted();
            return;
        }

        String[] permissions = {Manifest.permission.RECORD_AUDIO};

        for (String permission : permissions) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(permissions, 1);
                return;
            }
        }

        onPermissionsGranted();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        int n = permissions.length;
        for (int i = 0; i < n; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                finish();
                return;
            }
        }

        onPermissionsGranted();
    }

    @SuppressLint("MissingPermission")
    private void onPermissionsGranted() {
        int audioSource = MediaRecorder.AudioSource.DEFAULT;

        if (android.os.Build.VERSION.SDK_INT >= 24) {
            audioSource = MediaRecorder.AudioSource.UNPROCESSED;
        }

        int sampleRate = (int) settings.getSampleRate();

        int chFormat = AudioFormat.CHANNEL_IN_STEREO;

        micInput = new MicInput(audioSource, chFormat, sampleRate);

        boolean micInitOK = micInput.init(this);
        if (!micInitOK) {
            micInput = null;
            return;
        }

        micInput.connectOutputTo(new AudioCable() {
            @Override
            public void send(float[] sample) {
                onMicSample(sample);
            }

            @Override
            public void endOfFrame() {
            }

            @Override
            public void endOfStream() {
            }
        });

        micInput.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == RQ_CODE_SETTINGS) {
            recreate();
        }
    }

    @Override
    public void onBackPressed() {
        if (llMenuOptions.getVisibility() == View.VISIBLE) {
            llMenuOptions.setVisibility(View.GONE);
            return;
        }

        if (noisoid != null) {
            noisoid.stop();
            noisoid = null;
        }

        onStopMic();
        buffer = null;

        super.onBackPressed();
        super.finish();
    }

    private void switchAudioOutput(int deviceIdx) {
        AudioDeviceInfo[] outputDevices = mAudioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);

        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
    }


    AudioDeviceCallback audioDeviceCallback = new AudioDeviceCallback() {
        @Override
        public void onAudioDevicesAdded(AudioDeviceInfo[] addedDevices) {
            Timber.d("onAudioDevicesAdded ");

        }

        @Override
        public void onAudioDevicesRemoved(AudioDeviceInfo[] removedDevices) {
            Timber.d("onAudioDevicesRemoved ");

        }
    };

    AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    // resume playback
                    break;

                case AudioManager.AUDIOFOCUS_LOSS:
                    break;
            }
        }
    };

}