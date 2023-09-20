package com.longluo.devlib.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import java.io.File;

public class StorageUtils {

    private static String SDCARD2_PATH = "/mnt/sdcard2";
    private static String SDCARD2_EMULATED = "/storage/sdcard1";
    private static String SDCARD_EX = "/storage/extSdCard";
    private static String SDCARD = "/extSdCard";

    public static boolean checkSdcard(Context context) {
        if (StringUtils.isEmpty(getExternalMemoryPath())) {
            return false;
        }
        if (getAvailableExternalMemorySize() <= 0L) {
            return false;
        }
        return true;
    }

    public static String getRealSdcard2Path() {
        return SDCARD2_PATH;
    }

    public static String getRealSdcard() {
        return SDCARD;
    }

    public static boolean chekSdcard2(Context context) {
        if (StringUtils.isEmpty(getSdcard2MemoryPath())) {
            return false;
        }
        if (getAvailableSDCard2MemorySize() <= 0L) {
            return false;
        }
        return true;
    }

    public static boolean checkExceedSdcardSize(long fileSize) {
        long s = getAvailableExternalMemorySize();
        if (s <= 0L) {
            return false;
        }
        return fileSize >= s;
    }

    public static boolean checkExceedSDCard2Size(long fileSize) {
        long s = getAvailableSDCard2MemorySize();
        if (s <= 0L) {
            return false;
        }
        return fileSize >= s;
    }

    public static String getExternalMemoryPath() {
        String state = Environment.getExternalStorageState();
        if ("mounted".equals(state)) {
            File file = null;
            try {
                file = Environment.getExternalStorageDirectory();
            } catch (Exception e) {
                file = null;
            }
            if (file == null) {
                return "";
            }
            if (file.exists()) {
                return file.getAbsolutePath();
            }
        }
        return "";
    }

    public static String getSdcard2MemoryPath() {
        String path = SDCARD2_PATH;
        File file = new File(path);
        if (!file.exists()) {
            path = SDCARD_EX;
            file = new File(path);
            if (!file.exists()) {
                path = SDCARD2_EMULATED;
                file = new File(path);
                if (!file.exists()) {
                    path = "";
                }
            }
        } else if ((!file.canRead()) || (!file.canWrite())) {
            path = "";
        }
        return path;
    }

    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    public static long getAvailableSDCard2MemorySize() {
        File path = new File(SDCARD2_PATH);
        StatFs stat = null;
        long blockSize = 0L;
        long availableBlocks = 0L;
        if (!path.exists()) {
            path = new File(SDCARD2_EMULATED);
            if (!path.exists()) {
                path = new File(SDCARD_EX);
                if (!path.exists()) {
                    return 0L;
                }
            }
        }
        if ((!path.canRead()) || (!path.canWrite())) {
            return 0L;
        }
        stat = new StatFs(path.getPath());
        blockSize = stat.getBlockSize();
        availableBlocks = stat.getAvailableBlocks();

        return availableBlocks * blockSize;
    }

    public static long getTotalSDCard2MemorySize() {
        long totalExternalMemorySize = 0L;
        File path = new File(SDCARD2_PATH);
        StatFs stat = null;
        long blockSize = 0L;
        long totalBlocks = 0L;
        if (!path.exists()) {
            path = new File(SDCARD2_EMULATED);
            if (!path.exists()) {
                path = new File(SDCARD_EX);
                if (!path.exists()) {
                    return 0L;
                }
            }
        }
        if ((!path.canRead()) || (!path.canWrite())) {
            return 0L;
        }
        stat = new StatFs(path.getPath());
        blockSize = stat.getBlockSize();
        totalBlocks = stat.getBlockCount();

        totalExternalMemorySize = totalBlocks * blockSize;
        return totalExternalMemorySize;
    }

    public static long getAvailableExternalMemorySize() {
        long availableExternalMemorySize = 0L;
        try {
            if (Environment.getExternalStorageState().equals("mounted")) {
                String path = Environment.getExternalStorageDirectory().getPath();
                StatFs stat = new StatFs(path);
                long blockSize = stat.getBlockSize();
                long availableBlocks = stat.getAvailableBlocks();
                availableExternalMemorySize = availableBlocks * blockSize;
            } else if (Environment.getExternalStorageState().equals("removed")) {
                availableExternalMemorySize = 0L;
            }
        } catch (Exception localException) {
        }
        return availableExternalMemorySize;
    }

    public static long getTotalExternalMemorySize() {
        long totalExternalMemorySize = 0L;
        if (Environment.getExternalStorageState().equals("mounted")) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            totalExternalMemorySize = totalBlocks * blockSize;
        } else if (Environment.getExternalStorageState().equals("removed")) {
            totalExternalMemorySize = 0L;
        }

        return totalExternalMemorySize;
    }
}
