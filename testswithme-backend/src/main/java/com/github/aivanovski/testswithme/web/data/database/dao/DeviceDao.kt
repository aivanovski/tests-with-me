package com.github.aivanovski.testswithme.web.data.database.dao

import com.github.aivanovski.testswithme.web.data.database.AppDatabase
import com.github.aivanovski.testswithme.web.entity.Device
import com.github.aivanovski.testswithme.web.entity.Uid

class DeviceDao(
    db: AppDatabase
) : Dao<Device>(
    db = db,
    entityType = Device::class.java,
    entityName = Device::class.java.simpleName
) {

    fun getByUserUid(userUid: Uid): List<Device> {
        val variableName = entityName.first().uppercase()
        val fieldName = Device.DbFields.USER_UID
        return db.execTransaction {
            createQuery(
                "From $entityName $variableName WHERE $variableName.$fieldName = :uid",
                entityType
            )
                .setParameter("uid", userUid.toString())
                .resultList
        }
    }
}