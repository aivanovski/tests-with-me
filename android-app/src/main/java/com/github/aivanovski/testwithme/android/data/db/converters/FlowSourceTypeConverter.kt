package com.github.aivanovski.testwithme.android.data.db.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.github.aivanovski.testwithme.android.entity.FlowSourceType

@ProvidedTypeConverter
class FlowSourceTypeConverter {

    @TypeConverter
    fun fromDatabaseValue(value: String?): FlowSourceType? =
        value?.let { FlowSourceType.fromName(value) }

    @TypeConverter
    fun toDatabaseValue(type: FlowSourceType?): String? = type?.name
}