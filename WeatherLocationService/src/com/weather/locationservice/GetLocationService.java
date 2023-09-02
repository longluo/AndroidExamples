package com.weather.locationservice;

import com.weather.locationservice.LocationGetter.LocationGetResultListener;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.util.Log;


public class GetLocationService extends Service implements
		LocationGetResultListener {
	public static final String TAG = "GetLocationService";
	public static final String ACTION_GET_LOCATION_COMPLETE = "com.weather.action.get_location";
	public static final String ACTION_AUTO_GET_LOCATION_COMPLETE = "com.weather.action.auto_get_location"; // 自动获取当前位置

	private LocationGetter mLocationGetter = null;
	private boolean mGettingLocation = false;
	private ConnectionChangerReceiver mConnectionChangerListener = null;
	// 是否为自动更新标志位
	private boolean mAutoGetLocation = false;

	/**
	 * Bind
	 * 
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);

		// return START_REDELIVER_INTENT;
	}

	/**
	 * 监听服务启动
	 * 
	 * @see android.app.Service#onStart(android.content.Intent, int)
	 */
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		if (intent == null) {
			stopSelf();// XXX
			return;
		}

		mAutoGetLocation = intent.getBooleanExtra("auto", false);

		if (!mGettingLocation) {
			mGettingLocation = true;

			if (mLocationGetter != null) {
				mLocationGetter.stopGettingLocation();
				mLocationGetter = null;
			}

			mLocationGetter = new LocationGetter(this, this);
			mLocationGetter.startGettingLocation();
		}
	}

	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "onCreate()");
		registerReceiver();
	}

	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroy()");
		unristerReceiver();
		System.exit(0);
	}

	/**
	 * 
	 * Send message to main thread to update the UI.
	 * 
	 * @param flag
	 *            void
	 */
	private void notifyGetLoactionCompelete(int flag, String locateCityName,
			Location location) {
		Log.e(TAG, "notifyGetLoactionCompelete");

		Intent intent = null;
		if (mAutoGetLocation) {
			intent = new Intent(ACTION_AUTO_GET_LOCATION_COMPLETE);

		} else {
			intent = new Intent(ACTION_GET_LOCATION_COMPLETE);
		}
		intent.putExtra("update_success", flag);
		intent.putExtra("cityname", locateCityName);
		sendBroadcast(intent);

		if (mLocationGetter != null) {
			mLocationGetter.stopGettingLocation();
		}

		mLocationGetter = null;
		mGettingLocation = false;

		stopSelf(); // XXX
	}

	/**
	 * 注册消息
	 * 
	 * void
	 */
	private void registerReceiver() {
		try {
			IntentFilter filter = new IntentFilter(
					ConnectivityManager.CONNECTIVITY_ACTION);
			mConnectionChangerListener = new ConnectionChangerReceiver();
			this.registerReceiver(mConnectionChangerListener, filter);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 反注册消�?
	 * 
	 * void
	 */
	private void unristerReceiver() {
		try {
			this.unregisterReceiver(mConnectionChangerListener);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 监听网络连接
	 */
	private class ConnectionChangerReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {

			if (intent != null) {
				String action = intent.getAction();
				if (action != null
						&& action
								.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
					if (!isNetActive() && mGettingLocation) {

						if (mLocationGetter != null) {
							mLocationGetter.scheduleShutdown();
						}
					}
				}
			}

		}
	}

	/**
	 * 是否有可用网�?
	 * 
	 * @return boolean
	 */
	private boolean isNetActive() {
		boolean ret = false;

		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager != null
				&& connectivityManager.getActiveNetworkInfo() != null) {
			ret = connectivityManager.getActiveNetworkInfo().isAvailable();
		}

		return ret;
	}

	public void onLocationResult(int result, String locateCityName,
			Location location) {
		notifyGetLoactionCompelete(result, locateCityName, location);
	}
}
