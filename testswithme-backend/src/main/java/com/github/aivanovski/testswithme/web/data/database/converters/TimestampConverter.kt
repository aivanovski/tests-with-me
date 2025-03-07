package com.github.aivanovski.testswithme.web.data.database.converters

import com.github.aivanovski.testswithme.web.entity.Timestamp
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class TimestampConverter : AttributeConverter<Timestamp?, String?> {

    override fun convertToDatabaseColumn(attribute: Timestamp?): String? {
        return attribute?.toString()
    }

    override fun convertToEntityAttribute(dbData: String?): Timestamp? {
        return dbData?.let { Timestamp.fromString(dbData) }
    }
}