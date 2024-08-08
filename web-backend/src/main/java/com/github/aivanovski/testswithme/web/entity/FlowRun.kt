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
@Table(name = "FlowRuns")
data class FlowRun(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null,

    @Column(name = "uid")
    @Convert(converter = UidConverter::class)
    val uid: Uid,

    @Column(name = "flow_uid")
    @Convert(converter = UidConverter::class)
    val flowUid: Uid,

    @Column(name = "user_uid")
    @Convert(converter = UidConverter::class)
    val userUid: Uid,

    @Column(name = "timestamp")
    @Convert(converter = TimestampConverter::class)
    val timestamp: Timestamp,

    @Column(name = "duration_in_millis")
    val durationInMillis: Long,

    @Column(name = "is_success")
    val isSuccess: Boolean,

    @Column(name = "result")
    val result: String,

    @Column(name = "report_path")
    val reportPath: FsPath,

    @Column(name = "app_version_name")
    val appVersionName: String,

    @Column(name = "app_version_code")
    val appVersionCode: String
) {
    // TODO: Add db fields
}