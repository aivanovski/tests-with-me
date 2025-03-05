package com.github.aivanovski.testswithme.web.entity

import com.github.aivanovski.testswithme.web.data.database.converters.SyncItemTypeConverter
import com.github.aivanovski.testswithme.web.data.database.converters.UidConverter
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "ProcessedSyncItems")
data class ProcessedSyncItem(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0,

    @Column(name = "index")
    val index: Int = -1,

    @Column(name = "sync_uid")
    @Convert(converter = UidConverter::class)
    val syncUid: Uid,

    @Column(name = "entity_uid")
    @Convert(converter = UidConverter::class)
    val entityUid: Uid?,

    @Column(name = "type")
    @Convert(converter = SyncItemTypeConverter::class)
    val type: SyncItemType,

    @Column(name = "is_success")
    val isSuccess: Boolean,

    @Column(name = "path")
    val path: String
)