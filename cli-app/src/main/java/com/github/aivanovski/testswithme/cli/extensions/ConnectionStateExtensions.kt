package com.github.aivanovski.testswithme.cli.extensions

import com.github.aivanovski.testswithme.cli.entity.ConnectionState

fun ConnectionState.isReadyToStartTest(): Boolean {
    return this.isConnected && this.isDriverReady
}