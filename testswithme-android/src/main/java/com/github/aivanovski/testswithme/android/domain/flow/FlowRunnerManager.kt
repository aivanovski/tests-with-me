package com.github.aivanovski.testswithme.android.domain.flow

import android.view.accessibility.AccessibilityNodeInfo
import com.github.aivanovski.testswithme.android.data.settings.OnSettingsChangeListener
import com.github.aivanovski.testswithme.android.data.settings.SettingKey
import com.github.aivanovski.testswithme.android.data.settings.Settings
import com.github.aivanovski.testswithme.android.domain.flow.model.DriverServiceState
import com.github.aivanovski.testswithme.android.domain.flow.model.FlowRunnerState
import com.github.aivanovski.testswithme.entity.UiNode
import com.github.aivanovski.testswithme.flow.driver.Driver
import com.github.aivanovski.testswithme.utils.mutableStateFlow
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber

class FlowRunnerManager(
    private val runner: FlowRunner,
    private val settings: Settings
) : OnSettingsChangeListener {

    private val scope = CoroutineScope(Dispatchers.Main)
    private var isConnectedToNotificationService by mutableStateFlow(false)
    private val driverState = MutableStateFlow(DriverServiceState.STOPPED)
    private var timerJob: Job? = null

    fun start() {
        Timber.d("start:")

        driverState.value = DriverServiceState.INITIALIZED
    }

    fun stop() {
        Timber.d("stop:")

        driverState.value = DriverServiceState.STOPPED
        isConnectedToNotificationService = false
        cancelJobCheckingTimer()
        unsubscribeFromSettings()
        stopFlowIfNeed()
    }

    fun onConnectedToNotificationService(driver: Driver<AccessibilityNodeInfo>) {
        Timber.d("onConnectedToNotificationService:")

        runner.onDriverConnected(driver)

        isConnectedToNotificationService = true
        driverState.value = DriverServiceState.RUNNING
        startFlowIfNeed()
        startJobCheckingTimer()
        subscribeToSettings()
    }

    fun onDisconnectedFromNotificationService() {
        Timber.d("onDisconnectedFromNotificationService:")

        runner.onDriverDisconnected()

        driverState.value = DriverServiceState.INITIALIZED
        isConnectedToNotificationService = false
        cancelJobCheckingTimer()
        unsubscribeFromSettings()
        stopFlowIfNeed()
    }

    fun getDriverState(): DriverServiceState =
        driverState.value

    fun getRunnerState(): FlowRunnerState =
        runner.state

    fun getUiTree(): UiNode<Unit>? =
        runner.getUiTreeOrNull()

    fun setCollectUiTreeFlag() =
        runner.setCollectUiTreeFlag()

    override fun onSettingChanged(key: SettingKey) {
        Timber.d("onSettingChanged: key=%s, startJobUid=%s", key, settings.startJobUid)

        if (key == SettingKey.START_JOB_UID && settings.startJobUid != null) {
            scope.launch {
                delay(500)
                Timber.d("Check from settings")
                startFlowIfNeed()
            }
        }
    }

    private fun startJobCheckingTimer() {
        timerJob = scope.launch {
            while (isActive) {
                delay(TIME_CHECK_INTERVAL)

                if (settings.startJobUid != null) {
                    Timber.d("Check from timer")
                    startFlowIfNeed()
                }
            }
        }
    }

    private fun cancelJobCheckingTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun subscribeToSettings() {
        settings.subscribe(this)
    }

    private fun unsubscribeFromSettings() {
        settings.unsubscribe(this)
    }

    private fun startFlowIfNeed() {
        val startId = settings.startJobUid

        Timber.d(
            "startFlowIfNeed: lastStartId=%s, isConnected=%s, isRunning=%s",
            startId,
            isConnectedToNotificationService,
            runner.isRunning()
        )

        if (!isConnectedToNotificationService) {
            return
        }

        // TODO: maybe modify currently running job to launch new job
        if (runner.isIdle()) {
            runner.runNextFlow()
        }
    }

    private fun stopFlowIfNeed() {
        runner.stop()
    }

    companion object {

        private val TIME_CHECK_INTERVAL = TimeUnit.SECONDS.toMillis(10)
    }
}