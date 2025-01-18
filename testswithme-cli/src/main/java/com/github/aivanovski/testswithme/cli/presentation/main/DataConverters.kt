package com.github.aivanovski.testswithme.cli.presentation.main

import com.github.aivanovski.testswithme.android.gatewayServerApi.dto.Sha256HashDto
import com.github.aivanovski.testswithme.entity.Hash

fun Hash.toDto(): Sha256HashDto {
    return Sha256HashDto(value)
}