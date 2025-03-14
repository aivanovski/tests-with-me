package com.github.aivanovski.testswithme.web.data.database.converters

import com.github.aivanovski.testswithme.web.entity.SyncItemType
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class SyncItemTypeConverter : AttributeConverter<SyncItemType?, String?> {

    override fun convertToDatabaseColumn(attribute: SyncItemType?): String? {
        return attribute?.name
    }

    override fun convertToEntityAttribute(dbData: String?): SyncItemType? {
        return dbData?.let { SyncItemType.getByName(it) }
    }
}