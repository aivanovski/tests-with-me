package com.github.aivanovski.testswithme.flow.runner

import com.github.aivanovski.testswithme.flow.driver.Driver
import com.github.aivanovski.testswithme.utils.Logger

data class ExecutionContext<NodeType>(
    val driver: Driver<NodeType>,
    val logger: Logger
)