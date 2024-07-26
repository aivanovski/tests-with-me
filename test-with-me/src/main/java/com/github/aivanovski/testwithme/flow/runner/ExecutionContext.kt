package com.github.aivanovski.testwithme.flow.runner

import com.github.aivanovski.testwithme.flow.driver.Driver
import com.github.aivanovski.testwithme.utils.Logger

data class ExecutionContext<NodeType>(
    val driver: Driver<NodeType>,
    val logger: Logger
)