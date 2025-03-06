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
@Table(name = "TestSources")
data class TestSource(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0,

    @Column(name = "uid")
    @Convert(converter = UidConverter::class)
    val uid: Uid,

    @Column(name = "repository_url")
    val repositoryUrl: String,

    @Column(name = "last_check_timestamp", nullable = true)
    @Convert(converter = TimestampConverter::class)
    val lastCheckTimestamp: Timestamp?,

    @Column(name = "last_commit_hash", nullable = true)
    val lastCommitHash: String?,

    @Column(name = "is_force_sync_flag")
    val isForceSyncFlag: Boolean
)