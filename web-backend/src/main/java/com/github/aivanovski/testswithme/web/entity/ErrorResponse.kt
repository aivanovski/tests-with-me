package com.github.aivanovski.testswithme.web.entity

import com.github.aivanovski.testswithme.web.entity.exception.AppException
import io.ktor.http.HttpStatusCode

data class ErrorResponse(
    val status: HttpStatusCode,
    val exception: AppException,
    val message: String?
)