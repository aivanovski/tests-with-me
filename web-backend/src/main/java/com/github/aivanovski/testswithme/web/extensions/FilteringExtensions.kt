package com.github.aivanovski.testswithme.web.extensions

import com.github.aivanovski.testswithme.web.entity.Flow

fun List<Flow>.filterNotDeleted(): List<Flow> {
    return this.filter { flow -> !flow.isDeleted }
}