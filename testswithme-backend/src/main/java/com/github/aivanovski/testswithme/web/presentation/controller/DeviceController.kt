package com.github.aivanovski.testswithme.web.presentation.controller

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.extensions.toIntSafely
import com.github.aivanovski.testswithme.web.api.dto.DeviceResponseItemDto
import com.github.aivanovski.testswithme.web.api.response.GetDevicesResponse
import com.github.aivanovski.testswithme.web.data.repository.DeviceRepository
import com.github.aivanovski.testswithme.web.entity.User
import com.github.aivanovski.testswithme.web.entity.exception.AppException

class DeviceController(
    private val deviceRepository: DeviceRepository
) {

    fun getDevices(user: User): Either<AppException, GetDevicesResponse> =
        either {
            val devices = deviceRepository.getByUserUid(user.uid).bind()

            GetDevicesResponse(
                devices = devices.map { device ->
                    DeviceResponseItemDto(
                        id = device.uid.toString(),
                        name = device.name,
                        sdkVersion = device.sdkVersion.toIntSafely()
                    )
                }
            )
        }
}