package com.github.aivanovski.testswithme.web.entity

import com.github.aivanovski.testswithme.web.data.database.converters.SyncItemTypeConverter
import com.github.aivanovski.testswithme.web.data.database.converters.UidConverter
import com.github.aivanovski.testswithme.web.entity.ProcessedSyncItem.DbFields.ENTITY_UID
import com.github.aivanovski.testswithme.web.entity.ProcessedSyncItem.DbFields.IS_SUCCESS
import com.github.aivanovski.testswithme.web.entity.ProcessedSyncItem.DbFields.ITEM_INDEX
import com.github.aivanovski.testswithme.web.entity.ProcessedSyncItem.DbFields.PATH
import com.github.aivanovski.testswithme.web.entity.ProcessedSyncItem.DbFields.SYNC_UID
import com.github.aivanovski.testswithme.web.entity.ProcessedSyncItem.DbFields.TABLE_NAME
import com.github.aivanovski.testswithme.web.entity.ProcessedSyncItem.DbFields.TYPE
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = TABLE_NAME)
data class ProcessedSyncItem(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0,

    @Column(name = ITEM_INDEX)
    val itemIndex: Int,

    @Column(name = SYNC_UID)
    @Convert(converter = UidConverter::class)
    val syncUid: Uid,

    @Column(name = ENTITY_UID)
    @Convert(converter = UidConverter::class)
    val entityUid: Uid?,

    @Column(name = TYPE)
    @Convert(converter = SyncItemTypeConverter::class)
    val type: SyncItemType,

    @Column(name = IS_SUCCESS)
    val isSuccess: Boolean,

    @Column(name = PATH)
    val path: String
) {
    object DbFields {
        const val TABLE_NAME = "ProcessedSyncItems"

        const val ITEM_INDEX = "item_index"
        const val SYNC_UID = "sync_uid"
        const val ENTITY_UID = "entity_uid"
        const val TYPE = "type"
        const val IS_SUCCESS = "is_success"
        const val PATH = "path"

        const val UNSET_INDEX = -1
    }
}