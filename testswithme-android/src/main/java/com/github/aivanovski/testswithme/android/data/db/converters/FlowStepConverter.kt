package com.github.aivanovski.testswithme.android.data.db.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.github.aivanovski.testswithme.entity.FlowStep
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@ProvidedTypeConverter
class FlowStepConverter {

    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        explicitNulls = false
    }

    @TypeConverter
    fun fromDatabaseValue(value: String?): FlowStep? {
        if (value.isNullOrEmpty()) {
            return null
        }
        return json.decodeFromString(value)
    }

    @TypeConverter
    fun toDatabaseValue(step: FlowStep?): String? {
        if (step == null) {
            return null
        }
        return json.encodeToString(step)
    }
}