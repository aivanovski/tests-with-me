package com.github.aivanovski.testswithme.web.data.database.converters

import com.github.aivanovski.testswithme.web.entity.FsPath
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class FsPathConverter : AttributeConverter<FsPath?, String?> {

    override fun convertToDatabaseColumn(attribute: FsPath?): String? {
        return attribute?.toString()
    }

    override fun convertToEntityAttribute(dbData: String?): FsPath? {
        return dbData?.let { FsPath(it) }
    }
}