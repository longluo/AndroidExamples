package com.binderclient;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.binderserver.IAidlBinder;

public class BinderClientActivity extends Activity implements OnClickListener {
	private static final String TAG = "BinderClientActivity";
	private IAidlBinder mbinder;
	private Button mGetInfo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		mGetInfo = (Button) findViewById(R.id.getInfo);
		mGetInfo.setOnClickListener(this);

		// 注意这里intent要在ServerService进行静态注册。
		Intent intent = new Intent("com.binderserver.ServerService");
		bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
	}

	private ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mbinder = IAidlBinder.Stub.asInterface(service);
			Log.i(TAG, "onServiceConnected");
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mbinder = null;
		}

	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.getInfo:
			if (mbinder == null) {
				Log.d("binderclient", "");
			} else {
				try {
					String print = "The name of this fruit is:   "
							+ mbinder.getFruit().getName() + "\n"
							+ "The color of this fruit is:   "
							+ mbinder.getFruit().getColor() + "\n"
							+ "The number of this fruit is:   "
							+ mbinder.getFruit().getNumber() + "\n"
							+ "The server says:   " + mbinder.getInfo();
					mGetInfo.setText(print);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			break;

		default:
			break;
		}

	}
}
