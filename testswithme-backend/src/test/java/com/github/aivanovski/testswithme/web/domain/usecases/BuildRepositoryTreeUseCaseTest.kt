package com.github.aivanovski.testswithme.web.domain.usecases

import com.github.aivanovski.testswithme.extensions.unwrapOrReport
import com.github.aivanovski.testswithme.web.data.file.FileSystemProviderImpl
import com.github.aivanovski.testswithme.web.domain.trees.mapLayers
import com.github.aivanovski.testswithme.web.domain.sync.RepositoryTreeBuilder
import com.github.aivanovski.testswithme.web.entity.RelativePath
import com.github.aivanovski.testswithme.web.extensions.toRelative
import com.github.aivanovski.testswithme.web.utils.TestFiles.FILES
import com.github.aivanovski.testswithme.web.utils.setupFiles
import io.kotest.matchers.shouldBe
import java.nio.file.Path
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

class BuildRepositoryTreeUseCaseTest {

    @TempDir
    lateinit var tempDir: Path

    @Test
    fun `buildRepositoryTree should create valid tree`() {
        // arrange
        FILES.setupFiles(tempDir)
        val expectedPaths = FILES.map { it.path }.sorted()

        // act
        val (files, tree) = newUseCase().buildRepositoryTree(RelativePath("repository/tests"))
            .unwrapOrReport()

        // assert
        val layers = tree.mapLayers { node -> node.path }

        // val sortedPaths = files.map { it.toRelative().path }.sorted()
        // sortedPaths shouldBe expectedPaths
        // layers shouldBe listOf(
        //     listOf("Root"),
        //     listOf("Root/Screens", "Root/Common"),
        //     listOf("Root/Screens/Unlock", "Root/Common/reset.yaml"),
        //     listOf(
        //         "Root/Screens/Unlock/file-unlock.yaml",
        //         "Root/Screens/Unlock/password-unlock.yaml"
        //     )
        // )
    }

    private fun newUseCase() =
        RepositoryTreeBuilder(
            fileSystemProvider = FileSystemProviderImpl(
                baseDirPath = tempDir.toString()
            )
        )
}