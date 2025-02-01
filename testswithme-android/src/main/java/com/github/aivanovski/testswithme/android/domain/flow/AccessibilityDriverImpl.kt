package com.github.aivanovski.testswithme.android.domain.flow

import android.accessibilityservice.AccessibilityService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.accessibility.AccessibilityNodeInfo
import arrow.core.Either
import com.github.aivanovski.testswithme.android.extensions.convertToUiNode
import com.github.aivanovski.testswithme.entity.KeyCode
import com.github.aivanovski.testswithme.entity.UiNode
import com.github.aivanovski.testswithme.extensions.toSerializableTree
import com.github.aivanovski.testswithme.flow.driver.Driver
import com.github.aivanovski.testswithme.flow.error.DriverError
import com.github.aivanovski.testswithme.flow.error.DriverError.FailedToFindActivityError
import com.github.aivanovski.testswithme.flow.error.DriverError.FailedToGetUiNodesError
import com.github.aivanovski.testswithme.flow.error.DriverError.FailedToPerformActionError
import com.github.aivanovski.testswithme.flow.error.DriverError.InvalidKeyPressedError

class AccessibilityDriverImpl(
    private val context: Context,
    private val service: AccessibilityService,
    private val uiTreeCollector: UiTreeCollector
) : Driver<AccessibilityNodeInfo> {

    // TODO: UiTree should be added to the test report

    override fun sendBroadcast(
        packageName: String,
        action: String,
        data: Map<String, String>
    ): Either<DriverError, Unit> {
        val intent = Intent()
            .apply {
                for ((key, value) in data.entries) {
                    putExtra(key, value)
                }

                setComponent(
                    ComponentName(
                        packageName,
                        action
                    )
                )
            }

        context.sendBroadcast(intent)

        return Either.Right(Unit)
    }

    override fun launchApp(packageName: String): Either<DriverError, Unit> {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            ?: return Either.Left(FailedToFindActivityError(packageName))

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)

        context.startActivity(intent)

        return Either.Right(Unit)
    }

    override fun getUiTree(): Either<DriverError, UiNode<AccessibilityNodeInfo>> {
        val accessibilityNode = service.rootInActiveWindow
            ?: return Either.Left(FailedToGetUiNodesError)

        val uiNode = accessibilityNode.convertToUiNode()
        uiTreeCollector.setUiTree(uiNode.toSerializableTree())

        return Either.Right(uiNode)
    }

    override fun tapOn(uiNode: UiNode<AccessibilityNodeInfo>): Either<DriverError, Unit> {
        val isPerformed = uiNode.source.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        if (!isPerformed) {
            return Either.Left(FailedToPerformActionError("ACTION_CLICK"))
        }

        return Either.Right(Unit)
    }

    override fun longTapOn(uiNode: UiNode<AccessibilityNodeInfo>): Either<DriverError, Unit> {
        val isPerformed = uiNode.source.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK)
        if (!isPerformed) {
            return Either.Left(FailedToPerformActionError("ACTION_LONG_CLICK"))
        }

        return Either.Right(Unit)
    }

    override fun inputText(
        text: String,
        uiNode: UiNode<AccessibilityNodeInfo>
    ): Either<DriverError, Unit> {
        val bundle = Bundle()
        bundle.putCharSequence(
            AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
            text
        )

        // TODO: add check that node is editable
        val isPerformed = uiNode.source.performAction(
            AccessibilityNodeInfo.ACTION_SET_TEXT,
            bundle
        )
        if (!isPerformed) {
            return Either.Left(FailedToPerformActionError("ACTION_SET_TEXT"))
        }

        return Either.Right(Unit)
    }

    override fun pressKey(key: KeyCode): Either<DriverError, Unit> {
        val action = KEY_ACTIONS[key]
            ?: return Either.Left(InvalidKeyPressedError(key.toString()))

        val isPerformed = service.performGlobalAction(action.id)
        if (!isPerformed) {
            return Either.Left(FailedToPerformActionError(action.name))
        }

        return Either.Right(Unit)
    }

    private data class AccessibilityAction(
        val name: String,
        val id: Int
    )

    companion object {
        private val KEY_ACTIONS = mapOf(
            KeyCode.Back to AccessibilityAction(
                name = "GLOBAL_ACTION_BACK",
                id = AccessibilityService.GLOBAL_ACTION_BACK
            ),

            KeyCode.Home to AccessibilityAction(
                name = "GLOBAL_ACTION_HOME",
                id = AccessibilityService.GLOBAL_ACTION_HOME
            )
        )
    }
}