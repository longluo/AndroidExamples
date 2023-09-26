package com.myl.mediacodedemo.encode.camera

import android.annotation.TargetApi
import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.myl.utils.DisplayUtils.getDisplayHeight
import com.myl.utils.DisplayUtils.getDisplayWidth
import com.myl.utils.SystemUtils.deviceBrand
import com.myl.utils.SystemUtils.systemModel

/**
 * 判断是否可用Camera2接口，也就是进而判断是否使用CameraX相机库
 */
object CameraApi {
    private const val TAG = "CameraApi"

    /**
     * 判断能否使用Camera2 的API
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun hasCamera2(context: Context?): Boolean {
        if (context == null) {
            return false
        }
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            false
        } else try {
            val manager = (context.getSystemService(Context.CAMERA_SERVICE) as CameraManager)
            val idList = manager.cameraIdList
            var notNull = true
            if (idList.isEmpty()) {
                notNull = false
            } else {
                for (str in idList) {
                    if (str == null || str.trim { it <= ' ' }.isEmpty()) {
                        notNull = false
                        break
                    }
                    val characteristics = manager.getCameraCharacteristics(str)
                    val iSupportLevel =
                        characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)
                    if (iSupportLevel != null
                        && (iSupportLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY
                                || iSupportLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED)
                    ) {
                        notNull = false
                        break
                    }
                }
            }
            notNull
        } catch (ignore: Throwable) {
            false
        }
    }

    /**
     * 判断是否存在前置摄像头
     * @param context
     * @return
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun hasFrontCamera(context: Context): Boolean {
        val brand = deviceBrand
        val model = systemModel
        // 华为折叠屏手机判断是否处于展开状态
        if (brand.contains("HUAWEI") && model.contains("TAH-")) {
            var width = getDisplayWidth(context)
            var height = getDisplayHeight(context)
            if (width < 0 || height < 0) {
                return true
            }
            if (width < height) {
                val temp = width
                width = height
                height = temp
            }
            Log.d(TAG, "hasFrontCamera: $model, width = $width, height = $height")
            if (width * 1.0f / height <= 4.0 / 3.0) {
                return false
            }
        }
        return true
    }
}