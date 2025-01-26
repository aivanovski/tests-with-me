package com.github.aivanovski.testswithme.cli.presentation.main.command

import com.github.aivanovski.testswithme.cli.presentation.main.Message
import com.github.aivanovski.testswithme.utils.mutableStateFlow
import java.util.Collections.synchronizedList
import java.util.LinkedList
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.reflect.KClass

class MessageQueue {

    // TODO: Refactor, isActive should not be here

    private val messageQueue: MutableList<Message> = synchronizedList(LinkedList())
    private val pendingMessages: Queue<Message> = ConcurrentLinkedQueue()
    private var isActive: Boolean by mutableStateFlow(false)

    fun containsMessageByType(type: KClass<out Message>): Boolean {
        return (messageQueue + pendingMessages)
            .any { m -> type.simpleName == m::class.java.simpleName }
    }

    fun add(message: Message) {
        if (isActive || !message.isRequireActiveState) {
            messageQueue.add(message)
        } else {
            pendingMessages.add(message)
        }
    }

    fun onStateChanged(isActive: Boolean) {
        this.isActive = isActive

        if (isActive) {
            messageQueue.addAll(pendingMessages)
            pendingMessages.clear()
        }
    }

    fun isEmpty(): Boolean = messageQueue.isEmpty()

    fun poll(): Message? = messageQueue.removeFirstOrNull()

    fun push(message: Message) = messageQueue.add(0, message)
}