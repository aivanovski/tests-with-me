package com.github.aivanovski.testswithme.web.entity

import com.github.aivanovski.testswithme.web.data.database.converters.TimestampConverter
import com.github.aivanovski.testswithme.web.data.database.converters.UidConverter
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "SyncResults")
data class SyncResult(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0,

    @Column(name = "uid")
    @Convert(converter = UidConverter::class)
    val uid: Uid,

    @Column(name = "test_source_uid")
    @Convert(converter = UidConverter::class)
    val testSourceUid: Uid,

    @Column(name = "start_timestamp")
    @Convert(converter = TimestampConverter::class)
    val startTimestamp: Timestamp,

    @Column(name = "end_timestamp")
    @Convert(converter = TimestampConverter::class)
    val endTimestamp: Timestamp,

    @Column(name = "is_success")
    val isSuccess: Boolean
)