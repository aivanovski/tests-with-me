package com.github.aivanovski.testswithme.android.utils

import com.github.aivanovski.testswithme.android.domain.getDescendantNodes
import com.github.aivanovski.testswithme.android.entity.FlowRun
import com.github.aivanovski.testswithme.android.entity.Group
import com.github.aivanovski.testswithme.android.entity.TreeNode
import com.github.aivanovski.testswithme.android.entity.db.FlowEntry

fun findParentGroups(
    targetGroup: Group,
    allGroups: List<Group>
): List<Group> {
    val uidToGroupMap = allGroups.associateBy { group -> group.uid }
    val parents = mutableListOf<Group>()

    var parentUid: String? = targetGroup.parentUid
    while (parentUid != null) {
        val parent = uidToGroupMap[parentUid]

        if (parent != null) {
            parents.add(parent)
        }

        parentUid = parent?.parentUid
    }

    return parents.reversed()
}

fun List<FlowRun>.aggregateByFlowUid(): Map<String, List<FlowRun>> {
    val runs = this
    val flowUidToRunsMap = HashMap<String, MutableList<FlowRun>>()

    for (run in runs) {
        val flowUid = run.flowUid

        val flowRuns = flowUidToRunsMap.getOrDefault(flowUid, mutableListOf())
            .apply {
                add(run)
            }

        flowUidToRunsMap[flowUid] = flowRuns
    }

    for ((_, flowRuns) in flowUidToRunsMap.entries) {
        flowRuns.sortByDescending { run -> run.finishedAt }
    }

    return flowUidToRunsMap
}

fun List<FlowRun>.aggregateRunCountByFlowUid(): Map<String, Int> {
    val runs = this
    val flowUidToRunCountMap = HashMap<String, Int>()

    for (run in runs) {
        val count = flowUidToRunCountMap.getOrDefault(run.flowUid, 0)
        flowUidToRunCountMap[run.flowUid] = count + 1
    }

    return flowUidToRunCountMap
}

fun List<Group>.aggregateFlowCountByGroupUid(): Map<String, Int> {
    val groups = this
    val groupUidToFlowCountMap = HashMap<String, Int>()

    for (group in groups) {
        val count = groupUidToFlowCountMap.getOrDefault(group.uid, 0)
        groupUidToFlowCountMap[group.uid] = count + 1
    }

    return groupUidToFlowCountMap
}

fun List<FlowRun>.aggregateLastRunByFlowUid(): Map<String, FlowRun> {
    val flowUidToLastRunMap = HashMap<String, FlowRun>()
    val runs = this

    for (run in runs) {
        val flowUid = run.flowUid
        val previousRun = flowUidToLastRunMap[flowUid]
        if (previousRun == null || run.finishedAt > previousRun.finishedAt) {
            flowUidToLastRunMap[flowUid] = run
        }
    }

    return flowUidToLastRunMap
}

fun List<FlowEntry>.aggregatePassedFailedAndRemainedFlows(
    runs: List<FlowRun>
): Triple<List<FlowEntry>, List<FlowEntry>, List<FlowEntry>> {
    val flowUidToLastRunMap = runs.aggregateLastRunByFlowUid()
    val flows = this

    val passed = flows
        .filter { flow ->
            val lastRun = flowUidToLastRunMap[flow.uid]
            lastRun?.isSuccess == true
        }

    val failed = flows
        .filter { flow ->
            val lastRun = flowUidToLastRunMap[flow.uid]
            lastRun?.isSuccess == false
        }

    val remained = flows
        .filter { flow -> !flowUidToLastRunMap.containsKey(flow.uid) }

    return Triple(passed, failed, remained)
}

fun TreeNode.aggregateDescendantFlows(flows: List<FlowEntry>): List<FlowEntry> {
    val root = this

    val groupUids = root.getDescendantNodes()
        .map { node -> node.entity.uid }
        .toMutableSet()
        .apply {
            add(root.entity.uid)
        }

    return flows.filter { flow -> flow.groupUid in groupUids }
}

fun TreeNode.aggregateDescendantGroupsAndFlows(
    groups: List<Group>,
    flows: List<FlowEntry>
): Pair<List<Group>, List<FlowEntry>> {
    val root = this

    val groupUids = root.getDescendantNodes()
        .map { node -> node.entity.uid }
        .toSet()

    val descendantGroups = groups.filter { group -> group.uid in groupUids }
    val descendantFlows = flows.filter { flow -> flow.groupUid in groupUids }

    return descendantGroups to descendantFlows
}

fun List<FlowEntry>.toUids(): List<String> {
    return this.map { flow -> flow.uid }
}