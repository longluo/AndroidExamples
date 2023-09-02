package android.hello.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class HelloService extends Service {
	private static final String TAG = "HelloService";
	private int mVal = 0;

	@Override
	public IBinder onBind(Intent intent) {
		return myRemoteServiceStub;
	}

	private IHelloService.Stub myRemoteServiceStub = new IHelloService.Stub() {

		@Override
		public void setVal(int val) throws RemoteException {
			Log.i(TAG, "setVal: Val=" + val);
			mVal = val;
		}

		@Override
		public int getVal() throws RemoteException {
			Log.i(TAG, "getVal: mVal=" + mVal);
			return mVal;
		}

	};
}
