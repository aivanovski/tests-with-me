package com.github.aivanovski.testswithme.web.domain.sync

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.web.data.repository.FlowRepository
import com.github.aivanovski.testswithme.web.data.repository.GroupRepository
import com.github.aivanovski.testswithme.web.domain.trees.TreeBuilder
import com.github.aivanovski.testswithme.web.domain.trees.model.TreeEntity
import com.github.aivanovski.testswithme.web.domain.trees.model.TreeNode
import com.github.aivanovski.testswithme.web.entity.Project
import com.github.aivanovski.testswithme.web.entity.exception.AppException

class ProjectTreeBuilder(
    private val groupRepository: GroupRepository,
    private val flowRepository: FlowRepository
) {

    fun buildProjectTree(project: Project): Either<AppException, TreeNode> =
        either {
            val groups = groupRepository.getByProjectUid(project.uid).bind()
            val flows = flowRepository.getFlowsByProjectUid(project.uid).bind()

            val entities = mutableListOf<TreeEntity>()
            for (group in groups) {
                entities.add(
                    TreeEntity.TreeBranch(
                        uid = group.uid,
                        parentUid = group.parentUid,
                        name = group.name
                    )
                )
            }

            for (flow in flows) {
                entities.add(
                    TreeEntity.TreeLeaf(
                        uid = flow.uid,
                        parentUid = flow.groupUid,
                        name = flow.name
                    )
                )
            }

            TreeBuilder.buildTree(
                entities = entities
            ).bind()
        }
}