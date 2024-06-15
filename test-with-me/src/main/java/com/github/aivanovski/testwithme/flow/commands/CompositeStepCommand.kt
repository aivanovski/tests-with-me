package com.github.aivanovski.testwithme.flow.commands

interface CompositeStepCommand : StepCommand {
    fun getCommands(): List<ExecutableStepCommand<Any>>
}