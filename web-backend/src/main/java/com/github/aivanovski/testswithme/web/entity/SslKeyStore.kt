package com.github.aivanovski.testswithme.web.entity

import java.security.KeyStore

data class SslKeyStore(
    val keyStore: KeyStore,
    val alias: String,
    val password: String
)