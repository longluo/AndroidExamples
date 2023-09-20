package com.longluo.devlib.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class NetworkTypeUtils {
    public static final int NETWORK_TYPE_HSPAP = 15;

    public static enum NetworkStatus {
        OFF, MOBILE_4G, MOBILE_3G, MOBILE_2G, WIFI, OTHER;
    }

    public static boolean isThirdGeneration(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
        int netWorkType = telephonyManager.getNetworkType();
        switch (netWorkType) {
            case 1:
            case 2:
            case 4:
                return false;
        }
        return true;
    }

    public static NetworkInfo getAvailableNetWorkInfo(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService("connectivity");
            NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
            if ((activeNetInfo != null) && (activeNetInfo.isAvailable())) {
                return activeNetInfo;
            }
            return null;
        } catch (Exception e) {
        }
        return null;
    }

    public static String getNetWorkType(Context context) {
        if (context == null) {
            return "";
        }
        String netWorkType = "";
        NetworkInfo netWorkInfo = getAvailableNetWorkInfo(context);
        if (netWorkInfo != null) {
            if (netWorkInfo.getType() == 1) {
                netWorkType = "1";
            } else if (netWorkInfo.getType() == 0) {
                TelephonyManager telephonyManager = (TelephonyManager) context
                        .getSystemService("phone");
                switch (telephonyManager.getNetworkType()) {
                    case 1:
                        netWorkType = "2";
                        break;
                    case 2:
                        netWorkType = "3";
                        break;
                    case 3:
                        netWorkType = "4";
                        break;
                    case 8:
                        netWorkType = "5";
                        break;
                    case 9:
                        netWorkType = "6";
                        break;
                    case 10:
                        netWorkType = "7";
                        break;
                    case 4:
                        netWorkType = "8";
                        break;
                    case 5:
                        netWorkType = "9";
                        break;
                    case 6:
                        netWorkType = "10";
                        break;
                    case 7:
                        netWorkType = "11";
                        break;
                    case 15:
                        netWorkType = "12";
                        break;
                    case 13:
                        netWorkType = "13";
                        break;
                    case 11:
                    case 12:
                    case 14:
                    default:
                        netWorkType = "-1";
                }
            }
        }
        return netWorkType;
    }

    public static NetworkStatus getNetworkStatus(Context context) {
        NetworkInfo networkInfo = getAvailableNetWorkInfo(context);
        if (networkInfo == null) {
            return NetworkStatus.OFF;
        }
        int type = networkInfo.getType();
        if (1 == type) {
            return NetworkStatus.WIFI;
        }
        return NetworkStatus.MOBILE_3G;
    }

    public static String getNetworkApnType(Context context) {
        String mApnName = null;
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService("connectivity");
            NetworkInfo info = cm.getActiveNetworkInfo();
            mApnName = info.getTypeName().toLowerCase();
            if ("wifi".equalsIgnoreCase(mApnName)) {
                mApnName = "wifi";
            } else {
                mApnName = info.getExtraInfo().toLowerCase();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mApnName;
    }

    public static boolean isWapApnType(Context context) {
        if ("3gwap".equals(getNetworkApnType(context))) {
            return true;
        }

        return false;
    }
}
