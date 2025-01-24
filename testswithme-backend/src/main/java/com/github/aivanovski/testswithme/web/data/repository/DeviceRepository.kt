package com.github.aivanovski.testswithme.web.data.repository

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.web.data.database.dao.DeviceDao
import com.github.aivanovski.testswithme.web.entity.Device
import com.github.aivanovski.testswithme.web.entity.Uid
import com.github.aivanovski.testswithme.web.entity.exception.AppException
import com.github.aivanovski.testswithme.web.entity.exception.FailedToFindEntityByUidException

class DeviceRepository(
    private val dao: DeviceDao
) {

    fun getByUid(uid: Uid): Either<AppException, Device> =
        either {
            dao.findByUid(uid)
                ?: raise(FailedToFindEntityByUidException(Device::class, uid))
        }

    fun getByUserUid(userUid: Uid): Either<AppException, List<Device>> =
        either {
            dao.getByUserUid(userUid)
        }

    fun add(device: Device): Either<AppException, Device> =
        either {
            dao.add(device)
            dao.findByUid(device.uid)
                ?: raise(FailedToFindEntityByUidException(Device::class, device.uid))
        }
}