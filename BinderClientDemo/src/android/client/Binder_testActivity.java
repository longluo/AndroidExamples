package android.client;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hello.service.IHelloService;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Binder_testActivity extends Activity {
	private static final String TAG = "BinderClient";
	private LinearLayout layout;
	private TextView tv;
	private IHelloService mIHelloService;
	private ServiceConnection conn = new ServiceConnection() {

		@Override
		synchronized public void onServiceConnected(ComponentName name,
				IBinder service) {
			mIHelloService = IHelloService.Stub.asInterface(service);
			Log.i(TAG, "onServiceConnected");
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mIHelloService = null;
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		setContentView(layout);

		Intent intent = new Intent();
		intent.setAction("android.hello.IHelloService");
		bindService(intent, conn, Service.BIND_AUTO_CREATE);

		Button bt1 = new Button(this);
		bt1.setText("RPC:setVal(5)");
		bt1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mIHelloService != null) {
					try {
						Log.i(TAG, "mIHelloService = "
								+ mIHelloService);
						mIHelloService.setVal(5);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			}

		});

		Button bt2 = new Button(this);
		bt2.setText("RPC:getVal()");
		bt2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mIHelloService != null) {
					try {
						Log.i(TAG, "getVal");
						tv.setText("the value from remote service:  "
								+ mIHelloService.getVal());
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			}

		});

		tv = new TextView(this);
		tv.setText("the value from remote service: ... ");

		layout.addView(bt1);
		layout.addView(bt2);
		layout.addView(tv);
	}
}