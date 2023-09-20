package com.longluo.devlib.utils;

import java.security.MessageDigest;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

/*
 * @author long.luo
 * @date 2014-09-13 00:10:55
 */
public class DeviceUtils {
    private static final String TAG = DeviceUtils.class.getSimpleName();

    private static String mIMEI = null;
    private static String mMacAddress = null;

    public static String getIMEI(Context context) {
        if (!StringUtils.isEmpty(mIMEI)) {
            return mIMEI;
        }

        String IMEI = getTelephonyManager(context).getDeviceId();

        if ((StringUtils.isEmpty(IMEI)) || ("0".equals(IMEI))) {
            WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                String macAddress = wifiInfo.getMacAddress();
                if (!StringUtils.isEmpty(macAddress)) {
                    IMEI = md5(macAddress);
                }
            }
        }

        if (!StringUtils.isEmpty(IMEI)) {
            mIMEI = IMEI;
        }

        return IMEI;
    }

    public static String getMacAddress(Context context) {
        if (!StringUtils.isEmpty(mMacAddress)) {
            return mMacAddress;
        }

        String macAddress = null;
        WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if ((wifiInfo != null) && (!StringUtils.isEmpty(wifiInfo.getMacAddress()))) {
            macAddress = wifiInfo.getMacAddress().toUpperCase();
            macAddress = !StringUtils.isEmpty(macAddress) ? md5(macAddress).toLowerCase() : "0";
        }

        if (!StringUtils.isEmpty(macAddress)) {
            mMacAddress = macAddress;
        }

        return macAddress;
    }

    public static String md5(String str) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
            byte[] byteArray = messageDigest.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < byteArray.length; i++) {
                if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
                    sb.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
                } else {
                    sb.append(Integer.toHexString(0xFF & byteArray[i]));
                }
            }
            return sb.toString();
        }

        catch (Exception e) {
        }
        return str;
    }

    private static TelephonyManager getTelephonyManager(Context context) {
        return (TelephonyManager) context.getSystemService("phone");
    }

    public static String getUA() {
        return android.os.Build.MODEL;
    }

    public static String getDeviceAndroidVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    public static String getAppVersionName(Context context) {
        String versionName = null;

        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return versionName;
    }

}
