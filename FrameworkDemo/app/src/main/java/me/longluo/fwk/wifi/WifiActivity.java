package me.longluo.fwk.wifi;

import android.annotation.SuppressLint;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.SoftApConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import me.longluo.fwk.R;
import timber.log.Timber;


public class WifiActivity extends AppCompatActivity {

    private ConnectivityManager mConnectivityManager;

    private WifiManager mWifiManager;

    private TextView mTvWifiState;

    private TextView mTvWifiInfo;

    private Button mBtnWifiOn;

    private Button mBtnWifiOff;

    private TextView mTvWifiApInfo;

    private Button mBtnWifiApOn;

    private Button mBtnWifiApOff;

    private TextView mTvWifiList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wifi);

        mConnectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        initView();
        initListener();
        initData();
    }

    private void initView() {
        mTvWifiState = findViewById(R.id.tv_wifi_state);
        mBtnWifiOn = findViewById(R.id.btn_wifi_on);
        mBtnWifiOff = findViewById(R.id.btn_wifi_off);

        mTvWifiApInfo = findViewById(R.id.tv_wifi_ap_info);
        mBtnWifiApOn = findViewById(R.id.btn_wifi_ap_on);
        mBtnWifiApOff = findViewById(R.id.btn_wifi_ap_off);

        mTvWifiInfo = findViewById(R.id.tv_wifi_info);
        mTvWifiList = findViewById(R.id.tv_wifi_list);
    }

    private void initListener() {
        mBtnWifiOn.setOnClickListener(v -> {
            updateWifiState();
            updateWifiInfo();
            updateWifiList();
        });

        mBtnWifiOff.setOnClickListener(v -> {
            updateWifiState();
            updateWifiInfo();
            updateWifiList();
        });

        mBtnWifiApOn.setOnClickListener(v -> {
            startWifiAp();
            updateWifiApInfo();
        });

        mBtnWifiApOff.setOnClickListener(v -> {
            updateWifiApInfo();
        });
    }

    private void initData() {
        updateWifiState();
        updateWifiInfo();
        updateWifiList();
        updateWifiApInfo();
    }

    private void updateWifiState() {
        boolean isWifiEnabled = mWifiManager.isWifiEnabled();
        Timber.d("updateWifiState: %s", isWifiEnabled);
        mTvWifiState.setText(isWifiEnabled ? "Wifi: ON" : "Wifi: OFF");
    }

    private void updateWifiInfo() {
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        mTvWifiInfo.setText(String.valueOf(wifiInfo));
    }

    private void updateWifiList() {
        @SuppressLint("MissingPermission") List<ScanResult> wifiList = mWifiManager.getScanResults();

        Timber.d("updateWifiList: %s", wifiList.size());

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < wifiList.size(); i++) {
            ScanResult result = wifiList.get(i);
            sb.append(i).append(". ").append(result).append("\n");
        }

        mTvWifiList.setText(sb.toString());
    }

    private void updateWifiApInfo() {
        boolean apState = mWifiManager.isWifiApEnabled();

        Timber.d("updateWifiApInfo: %s", apState);

        mTvWifiApInfo.setText(apState ? "Wifi AP: ON" : "Wifi AP: OFF");
    }

    private void startWifiAp() {
        Timber.d("startWifiAp");

        SoftApConfiguration softApConfig = new SoftApConfiguration.Builder()
                .setSsid("MyTestSoftAp")
                .setPassphrase("test123456", SoftApConfiguration.SECURITY_TYPE_WPA2_PSK)
                .build();

        mWifiManager.setSoftApConfiguration(softApConfig);
        mWifiManager.startTetheredHotspot(null);

        updateWifiApInfo();
    }
}
