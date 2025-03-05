package com.github.aivanovski.testswithme.web.domain.sync

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.entity.YamlFlow
import com.github.aivanovski.testswithme.flow.yaml.YamlParser
import com.github.aivanovski.testswithme.web.data.file.FileSystemProvider
import com.github.aivanovski.testswithme.web.data.repository.GroupRepository.Companion.ROOT_GROUP_NAME
import com.github.aivanovski.testswithme.web.domain.trees.TreeBuilder
import com.github.aivanovski.testswithme.web.domain.trees.model.TreeEntity
import com.github.aivanovski.testswithme.web.domain.trees.model.TreeNode
import com.github.aivanovski.testswithme.web.domain.trees.traverse
import com.github.aivanovski.testswithme.web.entity.AbsolutePath
import com.github.aivanovski.testswithme.web.entity.RelativePath
import com.github.aivanovski.testswithme.web.entity.Uid
import com.github.aivanovski.testswithme.web.entity.exception.AppException
import com.github.aivanovski.testswithme.web.extensions.getName
import com.github.aivanovski.testswithme.web.extensions.isDirectory
import com.github.aivanovski.testswithme.web.extensions.toRelative

class RepositoryTreeBuilder(
    private val fileSystemProvider: FileSystemProvider
) {

    fun buildRepositoryTree(
        root: RelativePath
    ): Either<AppException, Pair<Map<String, AbsolutePath>, TreeNode>> =
        either {
            val files = fileSystemProvider.listFileTree(root, maxDepth = 8)
                .bind()
                .flatten()

            val rootEntity = TreeEntity.TreeBranch(
                uid = Uid(ROOT_GROUP_NAME),
                parentUid = null,
                name = ROOT_GROUP_NAME
            )

            val entities = mutableListOf<TreeEntity>()
                .apply {
                    add(rootEntity)
                }

            val uidToFileMap = mutableMapOf<Uid, AbsolutePath>()

            for (file in files) {
                val parent = fileSystemProvider.getParent(file.toRelative()).bind()

                val entityUid = Uid(file.path)

                val entity = if (file.isDirectory()) {
                    TreeEntity.TreeBranch(
                        uid = entityUid,
                        parentUid = if (parent.toRelative() != root) {
                            Uid(parent.path)
                        } else {
                            rootEntity.uid
                        },
                        name = file.getName()
                    )
                } else {
                    val yamlFlow = parseFlowFile(file.toRelative()).bind()

                    val flowName = yamlFlow.name.ifEmpty { file.getName() }

                    TreeEntity.TreeLeaf(
                        uid = entityUid,
                        parentUid = Uid(parent.path),
                        name = flowName
                    )
                }

                entities.add(entity)
                uidToFileMap[entityUid] = file
            }

            val tree = TreeBuilder.buildTree(entities)
                .bind()

            val pathToFileMap = mutableMapOf<String, AbsolutePath>()
            for (node in tree.traverse()) {
                val file = uidToFileMap[node.entityUid] ?: continue
                pathToFileMap[node.path] = file
            }

            pathToFileMap to tree
        }

    private fun parseFlowFile(
        path: RelativePath
    ): Either<AppException, YamlFlow> =
        either {
            val content = fileSystemProvider.readBytes(path)
                .map { bytes -> String(bytes) }
                .bind()

            val yamlFlow = YamlParser().parse(content)
                .mapLeft { error -> AppException(cause = error) }
                .bind()

            yamlFlow
        }
}