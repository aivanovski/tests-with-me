package com.github.aivanovski.testswithme.flow.commands

interface CompositeStepCommand : StepCommand {
    fun getCommands(): List<ExecutableStepCommand<Any>>
}