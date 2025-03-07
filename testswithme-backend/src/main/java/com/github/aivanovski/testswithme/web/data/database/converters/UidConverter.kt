package com.github.aivanovski.testswithme.web.data.database.converters

import com.github.aivanovski.testswithme.web.entity.Uid
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class UidConverter : AttributeConverter<Uid?, String?> {

    override fun convertToDatabaseColumn(attribute: Uid?): String? {
        return attribute?.toString()
    }

    override fun convertToEntityAttribute(dbData: String?): Uid? {
        return dbData?.let { Uid(it) }
    }
}