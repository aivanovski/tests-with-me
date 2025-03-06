package com.github.aivanovski.testswithme.web.utils

object TestFiles {

    val FILES = listOf(
        FileEntity.Directory("repository/tests/Screens"),
        FileEntity.Directory("repository/tests/Common"),
        FileEntity.Directory("repository/tests/Screens/Unlock"),

        FileEntity.File("repository/tests/Common/reset.yaml"),
        FileEntity.File("repository/tests/Screens/Unlock/file-unlock.yaml"),
        FileEntity.File("repository/tests/Screens/Unlock/password-unlock.yaml")
    )
}