package com.github.aivanovski.testwithme.android.data.db.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.github.aivanovski.testwithme.android.entity.StepVerificationType

@ProvidedTypeConverter
class StepVerificationTypeConverter {

    @TypeConverter
    fun fromDatabaseValue(value: String?): StepVerificationType? =
        value?.let { StepVerificationType.fromName(value) }

    @TypeConverter
    fun toDatabaseValue(type: StepVerificationType?): String? = type?.name
}