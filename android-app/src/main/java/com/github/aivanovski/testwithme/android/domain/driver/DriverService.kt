package com.github.aivanovski.testwithme.android.domain.driver

import android.accessibilityservice.AccessibilityService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.view.accessibility.AccessibilityEvent
import com.github.aivanovski.testwithme.android.NotificationService
import com.github.aivanovski.testwithme.android.di.GlobalInjector.inject
import com.github.aivanovski.testwithme.android.domain.flow.AccessibilityDriverImpl
import com.github.aivanovski.testwithme.android.domain.flow.FlowRunnerManager
import org.koin.core.parameter.parametersOf

class DriverService : AccessibilityService() {

    private var serviceConnection: ServiceConnection? = null
    private val driver = AccessibilityDriverImpl(this, this)
    private val runnerManager: FlowRunnerManager by inject(
        params = parametersOf(driver)
    )

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
        runnerManager.init()

        val serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(
                name: ComponentName?,
                service: IBinder?
            ) {
                runnerManager.onConnectedToNotificationService()
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