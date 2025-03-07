package com.github.aivanovski.testswithme.web.entity

import com.github.aivanovski.testswithme.web.data.database.converters.TimestampConverter
import com.github.aivanovski.testswithme.web.data.database.converters.UidConverter
import com.github.aivanovski.testswithme.web.entity.FlowRun.DbFields.APP_VERSION_CODE
import com.github.aivanovski.testswithme.web.entity.FlowRun.DbFields.APP_VERSION_NAME
import com.github.aivanovski.testswithme.web.entity.FlowRun.DbFields.DURATION_IN_MILLIS
import com.github.aivanovski.testswithme.web.entity.FlowRun.DbFields.FLOW_UID
import com.github.aivanovski.testswithme.web.entity.FlowRun.DbFields.IS_EXPIRED
import com.github.aivanovski.testswithme.web.entity.FlowRun.DbFields.IS_SUCCESS
import com.github.aivanovski.testswithme.web.entity.FlowRun.DbFields.RESULT
import com.github.aivanovski.testswithme.web.entity.FlowRun.DbFields.TABLE_NAME
import com.github.aivanovski.testswithme.web.entity.FlowRun.DbFields.TIMESTAMP
import com.github.aivanovski.testswithme.web.entity.FlowRun.DbFields.UID
import com.github.aivanovski.testswithme.web.entity.FlowRun.DbFields.USER_UID
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = TABLE_NAME)
data class FlowRun(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null,

    @Column(name = UID)
    @Convert(converter = UidConverter::class)
    val uid: Uid,

    @Column(name = FLOW_UID)
    @Convert(converter = UidConverter::class)
    val flowUid: Uid,

    @Column(name = USER_UID)
    @Convert(converter = UidConverter::class)
    val userUid: Uid,

    @Column(name = TIMESTAMP)
    @Convert(converter = TimestampConverter::class)
    val timestamp: Timestamp,

    @Column(name = DURATION_IN_MILLIS)
    val durationInMillis: Long,

    @Column(name = IS_SUCCESS)
    val isSuccess: Boolean,

    // TODO: should be moved to TextChunk
    @Column(name = RESULT, length = 1024)
    val result: String,

    @Column(name = APP_VERSION_NAME)
    val appVersionName: String,

    @Column(name = APP_VERSION_CODE)
    val appVersionCode: String,

    @Column(name = IS_EXPIRED)
    val isExpired: Boolean
) {
    object DbFields {
        const val TABLE_NAME = "FlowRuns"

        const val UID = "uid"
        const val FLOW_UID = "flow_uid"
        const val USER_UID = "user_uid"
        const val TIMESTAMP = "timestamp"
        const val DURATION_IN_MILLIS = "duration_in_millis"
        const val IS_SUCCESS = "is_success"
        const val RESULT = "result"
        const val APP_VERSION_NAME = "app_version_name"
        const val APP_VERSION_CODE = "app_version_code"
        const val IS_EXPIRED = "is_expired"
    }
}