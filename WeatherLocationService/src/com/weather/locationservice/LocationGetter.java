package com.weather.locationservice;

import java.util.Timer;
import java.util.TimerTask;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKBusLineResult;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKEvent;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.MKGeocoderAddressComponent;
import com.baidu.mapapi.MKLocationManager;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKSuggestionResult;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

public class LocationGetter {

	public static final String TAG = LocationGetter.class.getSimpleName();

	public static final int MSG_GETCITY_FAILURE = 0x1;
	public static final int MSG_GETCITY_SUCCESS = 0x2;
	public static final int MSG_GETCITY_EXISTED = 0x4;

	public interface LocationGetResultListener {
		public void onLocationResult(int result, String locateCityName,
				Location location);
	}

	private final Context mContext;
	private static final String KEY_LABEL = "FDADD7051EFBDC077D69FDDF7313213D9391321C";
	private BMapManager mMapManager;
	private MKSearch mSearch = null;
	private Location mLcoationInfo = null;
	private final LocationGetResultListener mResultCallback;

	private Timer mTimer = null;

	private HandlerThread mHandlerThread;

	/**
	 * Must be constructed in the main UI thread.
	 * 
	 * @param context
	 * @param dealHandler
	 */
	public LocationGetter(Context context, LocationGetResultListener callback) {
		mContext = context;
		mResultCallback = callback;
	}

	/**
	 * Start to get location and set time out.
	 */
	public void startGettingLocation() {
		Log.i(TAG, "startGettingLocation");

		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}

		mTimer = new Timer();
		mTimer.schedule(new TimerTask() {
			public void run() {
				postDealFailResult();
			}
		}, 50000);

		doGetLocation();
	}

	public void stopGettingLocation() {
		Log.i(TAG, "stopGettingLocation");
		cleanResource();
	}

	/*
	 * 主线程中运行
	 */

	private void doGetLocation() {

		if (mHandlerThread == null) {
			Log.i(TAG, "new HandlerThread(DoGetLacationThread);");

			mHandlerThread = new HandlerThread("DoGetLacationThread");
			mHandlerThread.start();

			Handler handler = new Handler(mHandlerThread.getLooper());
			handler.post(mLocationTask);
		}
	}

	private final Runnable mLocationTask = new Runnable() {

		@Override
		public void run() {

			mManagerExist = true;
			mResultExist = false;

			boolean isSuccess = false;

			if (mMapManager == null) {

				try {
					mMapManager = new BMapManager(mContext);

					if (mMapManager != null) {
						if (mMapManager.init(KEY_LABEL,
								new LocalInitFailureListener())) {
							MKLocationManager mkLocationManager = mMapManager
									.getLocationManager();
							if (mkLocationManager != null) {
								mkLocationManager
										.requestLocationUpdates(mLocationListener);
								if (mMapManager.start()) {
									isSuccess = initMKSearch();
								}
							}
						}
					}
				} catch (Throwable throwable) {
					throwable.printStackTrace();
				}
			}

			/*
			 * 启动线程等到结果产生结果mMapManager
			 */
			new Thread(mRecycleMapManagerTask).start();

			if (!isSuccess) {
				postDealFailResult();
			}
		}
	};

	private final Runnable mRecycleMapManagerTask = new Runnable() {

		@Override
		public void run() {

			Log.i(TAG, "mRecycleMapManagerTask");

			synchronized (mSync) {
				mSync.notifyAll();
				while (!mResultExist) {
					try {
						mSync.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			if (mMapManager != null) {
				try {
					mMapManager.getLocationManager().removeUpdates(
							mLocationListener);
					mMapManager.stop();
					mMapManager.destroy();
				} catch (Throwable throwable) {
					throwable.printStackTrace();
				}
				mMapManager = null;
			}
			mSearch = null;

			Log.i(TAG, "mMapManager destroyed");

			synchronized (mSync) {
				mManagerExist = false;
				mSync.notifyAll();
			}

		}
	};

	/**
	 * Must be in main ui thread.
	 */
	private Handler mUIHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// int what = msg.what;
			// if (what == 1) {
			// // Toast.makeText(mContext, "网络异常", Toast.LENGTH_SHORT).show();
			// scheduleShutdown();
			//
			// } else if (what == 2) {
			// // Toast.makeText(mContext, "没有权限", Toast.LENGTH_SHORT).show();
			// scheduleShutdown();
			// }
			super.handleMessage(msg);
		};
	};

	private class LocalInitFailureListener implements MKGeneralListener {
		@Override
		public void onGetNetworkState(int iError) {
			scheduleShutdown();
		}

		@Override
		public void onGetPermissionState(int iError) {
			if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
				scheduleShutdown();
			}

			Log.e(TAG, "onGetPermissionState:iError=" + iError);
		}
	}

	MKSearchListener mMkSearchListener;

	private final Object mSync = new Object();
	private boolean mResultExist = false;
	private boolean mManagerExist = true;// Wait handlerThread to start.

	/**
	 * Disable all location operation.
	 * <p>
	 * Must not be invoked in the main thread.
	 */
	private void postDealFailResult() {
		waitAndPostRusultToUIHandler(MSG_GETCITY_FAILURE, null, null);
	}

	/**
	 * ShutDown Getter and post failResult.
	 */
	public void scheduleShutdown() {

		new Thread() {
			public void run() {
				postDealFailResult();
			};
		}.start();

	}

	private void cleanResource() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}

		if (mHandlerThread != null) {
			mHandlerThread.quit();

			Log.i(TAG, "mHandlerThread.quit()");

			mHandlerThread = null;
		}
	}

	private void waitAndPostRusultToUIHandler(final int result,
			final String cityName, final Location location) {

		synchronized (mSync) {
			mResultExist = true;
			Log.i(TAG, "mResultExit");
			mSync.notifyAll();

			/*
			 * 等待mBMapManager销毁
			 */
			while (mManagerExist) {
				try {
					mSync.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		if (mResultCallback != null) {
			mUIHandler.post(new Runnable() {
				@Override
				public void run() {
					mResultCallback
							.onLocationResult(result, cityName, location);
				}

			});
		}

	}

	private LocationListener mLocationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			if (location != null) {
				mLcoationInfo = location;
				double latitude = location.getLatitude();
				double longtitude = location.getLongitude();
				if (mSearch != null) {
					mSearch.reverseGeocode(new GeoPoint((int) (latitude * 1e6),
							(int) (longtitude * 1e6)));
				}
			}
		}
	};

	private boolean initMKSearch() {
		if (mSearch == null) {
			mSearch = new MKSearch();
		}
		if (mMkSearchListener == null) {
			mMkSearchListener = new MKSearchListener() {
				public void onGetAddrResult(MKAddrInfo res, int error) {
					if ((error != 0) || (res == null)) {
						waitAndPostRusultToUIHandler(MSG_GETCITY_FAILURE, null,
								null);
						return;
					} else {
						MKGeocoderAddressComponent mkGeocoderAddressComponent = res.addressComponents;
						String city = mkGeocoderAddressComponent.city;
						String district = mkGeocoderAddressComponent.district;

						if ((city != null) && !city.equals("")) {
							String locateAddr = "";
							if ((district == null) || (district.equals(""))) {
								locateAddr = deleteSuffix(city);
							} else {
								locateAddr = filterResult(city, district);
							}

							waitAndPostRusultToUIHandler(MSG_GETCITY_SUCCESS,
									locateAddr, mLcoationInfo);
						}
					}
				}

				public void onGetPoiResult(MKPoiResult res, int type, int error) {

				}

				public void onGetDrivingRouteResult(MKDrivingRouteResult res,
						int error) {

				}

				public void onGetTransitRouteResult(MKTransitRouteResult res,
						int error) {

				}

				public void onGetWalkingRouteResult(MKWalkingRouteResult res,
						int error) {

				}

				@Override
				public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {

				}

				@Override
				public void onGetSuggestionResult(MKSuggestionResult arg0,
						int arg1) {

				}

				@Override
				public void onGetRGCShareUrlResult(String arg0, int arg1) {

				}

			};
		}

		return mSearch.init(mMapManager, mMkSearchListener);
	}

	private String filterResult(String city, String district) {
		boolean isMunicipaty = false;
		String locateAddress = "";
		String xian = mContext.getString(R.string.sheng);
		String qu = mContext.getString(R.string.qu);
		String[] municipalities = mContext.getResources().getStringArray(
				R.array.Municipalities);
		int municipalityLength = municipalities.length;

		String[] specialLocateAddress = mContext.getResources().getStringArray(
				R.array.special_locate_address);
		int specialLocateAddressLength = specialLocateAddress.length;

		String[] upperCitySpecialLocateAddress = mContext.getResources()
				.getStringArray(R.array.upper_city_special_locate_address);

		for (int i = 0; i < municipalityLength; i++) {
			if (municipalities[i].equals(city)) {
				locateAddress = city;
				isMunicipaty = true;
				break;
			}
		}

		if (!isMunicipaty) {
			for (int i = 0; i < specialLocateAddressLength; i++) {
				if (district.startsWith(specialLocateAddress[i])) {
					locateAddress = upperCitySpecialLocateAddress[i];
					isMunicipaty = true;

					break;
				}
			}
		}

		if (!isMunicipaty) {
			if (city.contains(xian)) {
				locateAddress = district;
			} else {
				if (!district.endsWith(qu)) {
					locateAddress = district;
				} else {
					locateAddress = city;
				}
			}
		}

		/**
		 * 当定位出的区域名长度大于2时，才截掉最后一个字 解决长度为2的城市如丰县的定位异常问题
		 */
		if (locateAddress.length() > 2) {
			locateAddress = deleteSuffix(locateAddress);
		}

		return locateAddress;
	}

	private String deleteSuffix(String string) {
		int length = string.length();
		string = string.substring(0, length - 1);
		return string;
	}

}
