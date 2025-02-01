package com.github.aivanovski.testswithme.android.domain.driver

import android.accessibilityservice.AccessibilityService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.view.accessibility.AccessibilityEvent
import com.github.aivanovski.testswithme.android.NotificationService
import com.github.aivanovski.testswithme.android.di.GlobalInjector.inject
import com.github.aivanovski.testswithme.android.domain.flow.AccessibilityDriverImpl
import com.github.aivanovski.testswithme.android.domain.flow.FlowRunnerManager
import com.github.aivanovski.testswithme.android.domain.flow.UiTreeCollector

class DriverService : AccessibilityService() {

    private val runnerManager: FlowRunnerManager by inject<FlowRunnerManager>()
    private val uiTreeCollector: UiTreeCollector by inject<UiTreeCollector>()

    private var serviceConnection: ServiceConnection? = null

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()

        val driver = AccessibilityDriverImpl(
            context = this,
            service = this,
            uiTreeCollector = uiTreeCollector
        )
        runnerManager.start()

        val serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(
                name: ComponentName?,
                service: IBinder?
            ) {
                runnerManager.onConnectedToNotificationService(driver)
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                runnerManager.onDisconnectedFromNotificationService()
                serviceConnection = null
            }
        }

        val intent = NotificationService.newConnectIntent(this)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

        this.serviceConnection = serviceConnection
    }

    override fun onDestroy() {
        super.onDestroy()
        runnerManager.stop()
        serviceConnection?.let { unbindService(it) }
    }

    override fun onInterrupt() {
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
    }
}