package com.github.aivanovski.testswithme.android.domain.usecases

import android.os.Build
import com.github.aivanovski.testswithme.android.entity.DeviceInfo

class GetDeviceInfoUseCase {

    fun getDeviceInfo(): DeviceInfo =
        DeviceInfo(
            name = Build.DEVICE,
            model = Build.MODEL,
            manufacturer = Build.MANUFACTURER,
            sdkVersion = Build.VERSION.SDK_INT.toString(),
            hardware = Build.HARDWARE
        )
}