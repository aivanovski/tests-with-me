package com.github.aivanovski.testswithme.web.entity

import com.github.aivanovski.testswithme.web.data.database.converters.TimestampConverter
import com.github.aivanovski.testswithme.web.data.database.converters.UidConverter
import com.github.aivanovski.testswithme.web.entity.TestSource.DbFields.IS_FORCE_SYNC_FLAG
import com.github.aivanovski.testswithme.web.entity.TestSource.DbFields.LAST_CHECK_TIMESTAMP
import com.github.aivanovski.testswithme.web.entity.TestSource.DbFields.LAST_COMMIT_HASH
import com.github.aivanovski.testswithme.web.entity.TestSource.DbFields.REPOSITORY_URL
import com.github.aivanovski.testswithme.web.entity.TestSource.DbFields.TABLE_NAME
import com.github.aivanovski.testswithme.web.entity.TestSource.DbFields.UID
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = TABLE_NAME)
data class TestSource(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0,

    @Column(name = UID)
    @Convert(converter = UidConverter::class)
    val uid: Uid,

    @Column(name = REPOSITORY_URL)
    val repositoryUrl: String,

    @Column(name = LAST_CHECK_TIMESTAMP, nullable = true)
    @Convert(converter = TimestampConverter::class)
    val lastCheckTimestamp: Timestamp?,

    @Column(name = LAST_COMMIT_HASH, nullable = true)
    val lastCommitHash: String?,

    @Column(name = IS_FORCE_SYNC_FLAG)
    val isForceSyncFlag: Boolean
) {
    object DbFields {
        const val TABLE_NAME = "TestSources"

        const val UID = "uid"
        const val REPOSITORY_URL = "repository_url"
        const val LAST_CHECK_TIMESTAMP = "last_check_timestamp"
        const val LAST_COMMIT_HASH = "last_commit_hash"
        const val IS_FORCE_SYNC_FLAG = "is_force_sync_flag"
    }
}