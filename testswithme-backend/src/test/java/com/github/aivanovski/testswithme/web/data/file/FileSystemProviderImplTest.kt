package com.github.aivanovski.testswithme.web.data.file

import com.github.aivanovski.testswithme.extensions.unwrapOrReport
import com.github.aivanovski.testswithme.web.entity.RelativePath
import com.github.aivanovski.testswithme.web.extensions.toRelative
import com.github.aivanovski.testswithme.web.utils.TestFiles.FILES
import com.github.aivanovski.testswithme.web.utils.setupFiles
import io.kotest.matchers.shouldBe
import java.nio.file.Path
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

class FileSystemProviderImplTest {

    @TempDir
    lateinit var tempDir: Path

    @Test
    fun `listFileTree should work correctly`() {
        // arrange
        FILES.setupFiles(tempDir)

        // act
        val files = newProvider().listFileTree(
            RelativePath("repository/tests"),
            maxDepth = 8
        ).unwrapOrReport()

        // assert
        val relativeFiles = files.flatten()
            .map { file -> file.toRelative().relativePath }
            .sorted()

        relativeFiles shouldBe FILES.map { it.path }.sorted()
    }

    private fun newProvider(): FileSystemProviderImpl =
        FileSystemProviderImpl(baseDirPath = tempDir.toString())
}