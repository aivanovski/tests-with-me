package com.github.aivanovski.testwithme.android.domain.usecases

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testwithme.android.entity.AppVersion
import com.github.aivanovski.testwithme.android.entity.exception.AppException
import com.github.aivanovski.testwithme.android.presentation.screens.flow.model.ExternalAppData

class GetExternalApplicationDataUseCase(
    private val context: Context
) {

    fun getApplicationData(
        packageName: String
    ): Either<AppException, ExternalAppData> = either {
        val info = getPackageInfo(packageName).getOrNull()
        val icon = getIcon(packageName).getOrNull()

        if (info == null) {
            raise(AppException("Failed to get application data: $packageName"))
        }

        ExternalAppData(
            packageName = info.packageName,
            appVersion = AppVersion(info.versionCode, info.versionName),
            icon = icon?.toBitmap(189, 189, config = Bitmap.Config.ARGB_8888)
        )
    }

    private fun getPackageInfo(
        packageName: String
    ): Either<AppException, PackageInfo> = either {
        try {
            context.packageManager.getPackageInfo(packageName, 0)
        } catch (exception: PackageManager.NameNotFoundException) {
            raise(AppException(cause = exception))
        }
    }

    private fun getIcon(
        packageName: String
    ): Either<AppException, Drawable> = either {
        try {
            context.packageManager.getApplicationIcon(packageName)
        } catch (exception: PackageManager.NameNotFoundException) {
            raise(AppException(cause = exception))
        }
    }
}