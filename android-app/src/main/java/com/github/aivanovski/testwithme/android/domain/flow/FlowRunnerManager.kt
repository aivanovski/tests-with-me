package com.github.aivanovski.testwithme.android.domain.flow

import android.content.Context
import android.view.accessibility.AccessibilityNodeInfo
import com.github.aivanovski.testwithme.android.data.settings.OnSettingsChangeListener
import com.github.aivanovski.testwithme.android.data.settings.SettingKey
import com.github.aivanovski.testwithme.android.data.settings.Settings
import com.github.aivanovski.testwithme.android.entity.DriverServiceState
import com.github.aivanovski.testwithme.flow.commands.StepCommand
import com.github.aivanovski.testwithme.flow.driver.Driver
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber

class FlowRunnerManager(
    interactor: FlowRunnerInteractor,
    private val settings: Settings,
    context: Context,
    driver: Driver<AccessibilityNodeInfo>
) : OnSettingsChangeListener {

    private val runner = FlowRunner(context, settings, interactor, driver)
    private var timerJob: Job? = null
    private val scopeJob = Job()
    private val scope = CoroutineScope(Dispatchers.Main + scopeJob)
    private var isConnectedToNotificationService = AtomicBoolean(false)

    fun init() {
        Timber.d("init:")

        driverState.set(DriverServiceState.INITIALIZED)

        scope.launch {
            debugCommandChannel.receiveAsFlow()
                .collectLatest { command ->
                    runner.runOrAddToQueue(command)
                }
        }
    }

    fun stop() {
        Timber.d("stop:")

        driverState.set(DriverServiceState.STOPPED)
        isConnectedToNotificationService.set(false)
        cancelJobCheckingTimer()
        unsubscribeFromSettings()
        scopeJob.cancel()
        stopFlowIfNeed()
    }

    fun onConnectedToNotificationService() {
        Timber.d("onConnectedToNotificationService:")

        isConnectedToNotificationService.set(true)
        driverState.set(DriverServiceState.RUNNING)
        startFlowIfNeed()
        startJobCheckingTimer()
        subscribeToSettings()
    }

    fun onDisconnectedFromNotificationService() {
        Timber.d("onDisconnectedFromNotificationService:")

        if (driverState.get() == DriverServiceState.RUNNING) {
            driverState.set(DriverServiceState.INITIALIZED)
        }

        isConnectedToNotificationService.set(false)
        cancelJobCheckingTimer()
        unsubscribeFromSettings()
        stopFlowIfNeed()
    }

    fun sendCommand(command: StepCommand) {
        runner.runOrAddToQueue(command)
    }

    override fun onSettingChanged(key: SettingKey) {
        Timber.d("onSettingChagned: key=%s", key)

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
        val isConnected = isConnectedToNotificationService.get()

        Timber.d(
            "startFlowIfNeed: lastStartId=%s, isConnected=%s, isRunning=%s",
            startId,
            isConnected,
            runner.isRunning()
        )

        if (!isConnected) {
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

        private val driverState = AtomicReference(DriverServiceState.STOPPED)

        private val debugCommandChannel = Channel<StepCommand>()

        fun sendDebugCommand(command: StepCommand) {
            debugCommandChannel.trySendBlocking(command)
        }

        fun getDriverState(): DriverServiceState = driverState.get()
    }
}