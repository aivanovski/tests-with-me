package com.github.aivanovski.testwithme.android.data.db.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.github.aivanovski.testwithme.entity.FlowStep
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@ProvidedTypeConverter
class FlowStepConverter {

    @TypeConverter
    fun fromDatabaseValue(value: String?): FlowStep? {
        if (value.isNullOrEmpty()) {
            return null
        }
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun toDatabaseValue(step: FlowStep?): String? {
        if (step == null) {
            return null
        }
        return Json.encodeToString(step)
    }
}