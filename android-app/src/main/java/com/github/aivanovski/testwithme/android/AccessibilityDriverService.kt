package com.github.aivanovski.testwithme.android

import android.accessibilityservice.AccessibilityService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.view.accessibility.AccessibilityEvent
import com.github.aivanovski.testwithme.android.data.Settings
import com.github.aivanovski.testwithme.android.di.GlobalInjector.inject
import com.github.aivanovski.testwithme.android.domain.FlowInteractor
import com.github.aivanovski.testwithme.android.domain.flow.AccessibilityDriverImpl
import com.github.aivanovski.testwithme.android.domain.flow.FlowRunner
import com.github.aivanovski.testwithme.entity.Duration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class AccessibilityDriverService : AccessibilityService() {

    private val settings: Settings by inject()
    private val interactor: FlowInteractor by inject()

    private val driver = AccessibilityDriverImpl(this, this)
    private val runner = FlowRunner(settings, interactor, driver)
    private var serviceConnection: ServiceConnection? = null
    private var timerJob: Job? = null
    private val scopeJob = Job()
    private val scope = CoroutineScope(Dispatchers.Main + scopeJob)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("onStartCommand:")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
        Timber.d("onCreate:")

        val serviceConnection = object : ServiceConnection {

            private var service: NotificationService.LocalBinder? = null

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                Timber.d("onServiceConnected: ")
                this.service = (service as NotificationService.LocalBinder)
                    .apply {
                        connect()
                    }

                startFlowIfNeed()
                startCheckTimer()
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                Timber.d("onServiceDisconnected: ")

                service?.connect()
                stopFlowIfNeed()
                cancelCheckTimer()
                serviceConnection = null
            }
        }

        val intent = NotificationService.newConnectIntent(this)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

        this.serviceConnection = serviceConnection
    }

    private fun startFlowIfNeed() {
        val startId = settings.startJobUid
        val isNotificationServiceConnected = (serviceConnection != null)

        Timber.d(
            "startTestIfNeed: lastStartId=%s, isConnected=%s, isRunning=%s",
            startId,
            isNotificationServiceConnected,
            runner.isRunning(),
        )

        if (!isNotificationServiceConnected) {
            return
        }

        runner.runNextFlow()
    }

    private fun stopFlowIfNeed() {
        runner.stop()
    }

    private fun startCheckTimer() {
        timerJob = scope.launch {
            while (true) {
                delay(10_000)

                if (settings.startJobUid != null) {
                    Timber.d("Check from timer")
                    startFlowIfNeed()
                }
            }
        }
    }

    private fun cancelCheckTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("onDestroy:")
        runner.stop()
        scopeJob.cancel()

        serviceConnection?.let { unbindService(it) }
    }

    override fun onInterrupt() {
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
    }

    companion object {

        private val JOB_CHECK_DELAY = Duration.seconds(10)
    }
}