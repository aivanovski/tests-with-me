package com.github.aivanovski.testswithme.web.entity

import com.github.aivanovski.testswithme.web.data.database.converters.TimestampConverter
import com.github.aivanovski.testswithme.web.data.database.converters.UidConverter
import com.github.aivanovski.testswithme.web.entity.SyncResult.DbFields.END_TIMESTAMP
import com.github.aivanovski.testswithme.web.entity.SyncResult.DbFields.IS_SUCCESS
import com.github.aivanovski.testswithme.web.entity.SyncResult.DbFields.START_TIMESTAMP
import com.github.aivanovski.testswithme.web.entity.SyncResult.DbFields.TABLE_NAME
import com.github.aivanovski.testswithme.web.entity.SyncResult.DbFields.TEST_SOURCE_UID
import com.github.aivanovski.testswithme.web.entity.SyncResult.DbFields.UID
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = TABLE_NAME)
data class SyncResult(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0,

    @Column(name = UID)
    @Convert(converter = UidConverter::class)
    val uid: Uid,

    @Column(name = TEST_SOURCE_UID)
    @Convert(converter = UidConverter::class)
    val testSourceUid: Uid,

    @Column(name = START_TIMESTAMP)
    @Convert(converter = TimestampConverter::class)
    val startTimestamp: Timestamp,

    @Column(name = END_TIMESTAMP)
    @Convert(converter = TimestampConverter::class)
    val endTimestamp: Timestamp,

    @Column(name = IS_SUCCESS)
    val isSuccess: Boolean
) {
    object DbFields {
        const val TABLE_NAME = "SyncResults"

        const val UID = "uid"
        const val TEST_SOURCE_UID = "test_source_uid"
        const val START_TIMESTAMP = "start_timestamp"
        const val END_TIMESTAMP = "end_timestamp"
        const val IS_SUCCESS = "is_success"
    }
}