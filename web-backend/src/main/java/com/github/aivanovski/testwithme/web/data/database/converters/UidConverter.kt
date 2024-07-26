package com.github.aivanovski.testwithme.web.data.database.converters

import com.github.aivanovski.testwithme.web.entity.Uid
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class UidConverter : AttributeConverter<Uid?, String?> {

    override fun convertToDatabaseColumn(attribute: Uid?): String? {
        return attribute?.toString()
    }

    override fun convertToEntityAttribute(dbData: String?): Uid? {
        return dbData?.let { Uid(it) }
    }
}