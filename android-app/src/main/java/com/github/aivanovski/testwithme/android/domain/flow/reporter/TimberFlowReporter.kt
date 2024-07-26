package com.github.aivanovski.testwithme.android.domain.flow.reporter

import com.github.aivanovski.testwithme.flow.runner.reporter.FlowReporter
import com.github.aivanovski.testwithme.utils.Logger

class TimberFlowReporter(
    logger: Logger
) : FlowReporter(
    logger = logger,
    flowTransformer = ShortNameTransformer()
)