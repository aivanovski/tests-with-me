package com.github.aivanovski.testswithme.web.data.database.converters

import com.github.aivanovski.testswithme.entity.Hash
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class HashConverter : AttributeConverter<Hash?, String?> {

    override fun convertToDatabaseColumn(attribute: Hash?): String? =
        attribute?.let { Hash.formatToString(attribute) }

    override fun convertToEntityAttribute(dbData: String?): Hash? =
        dbData?.let { Hash.fromString(dbData) }
}