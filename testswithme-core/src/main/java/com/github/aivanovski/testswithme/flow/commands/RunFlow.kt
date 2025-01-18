package com.github.aivanovski.testswithme.flow.commands

class RunFlow(
    val flowUid: String,
    private val name: String,
    private val commands: List<ExecutableStepCommand<Any>>
) : CompositeStepCommand {

    override fun getCommands(): List<ExecutableStepCommand<Any>> = commands
}