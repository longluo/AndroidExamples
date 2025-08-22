package me.longluo.fwk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import me.longluo.fwk.bluetooth.BluetoothActivity;
import me.longluo.fwk.wifi.WifiActivity;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnBluetooth;

    private Button mBtnWifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        mBtnBluetooth = findViewById(R.id.btn_bluetooth);
        mBtnBluetooth.setOnClickListener(this);

        mBtnWifi = findViewById(R.id.btn_wifi);
        mBtnWifi.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.btn_wifi) {
            startActivity(new Intent(this, WifiActivity.class));
        } else if (viewId == R.id.btn_bluetooth) {
            startActivity(new Intent(this, BluetoothActivity.class));
        }
    }
}