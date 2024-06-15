package com.github.aivanovski.testwithme.android.data.db.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.github.aivanovski.testwithme.android.entity.FlowStatus

@ProvidedTypeConverter
class FlowStatusConverter {

    @TypeConverter
    fun fromDatabaseValue(value: String?): FlowStatus? =
        value?.let { FlowStatus.fromName(value) }

    @TypeConverter
    fun toDatabaseValue(type: FlowStatus?): String? = type?.name
}