package com.github.aivanovski.testswithme.cli.domain

import arrow.atomic.AtomicBoolean
import com.github.aivanovski.testswithme.cli.data.file.FileSystemProvider
import com.github.aivanovski.testswithme.extensions.unwrap
import com.github.aivanovski.testswithme.extensions.unwrapError
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY

class FileWatcherImpl(
    private val fsProvider: FileSystemProvider
) : FileWatcher {

    private val isActive = AtomicBoolean(true)

    override fun watch(
        file: Path,
        onContentChanged: (content: String) -> Unit
    ) {
        val filePath = file.toAbsolutePath().toString()
        val parent = file.parent

        val watcher = FileSystems.getDefault().newWatchService()
        parent.register(watcher, ENTRY_MODIFY)

        while (isActive.get()) {
            try {
                val key = watcher.take()
                if (key == null) {
                    isActive.set(false)
                    continue
                }

                for (event in key.pollEvents()) {
                    val eventKind = event.kind()
                    val eventContext = event.context()

                    if (eventKind == ENTRY_MODIFY &&
                        eventContext is Path &&
                        eventContext.toAbsolutePath().toString() == filePath
                    ) {
                        val readContentResult = fsProvider.read(filePath)
                        if (readContentResult.isRight()) {
                            onContentChanged.invoke(readContentResult.unwrap())
                        } else {
                            readContentResult.unwrapError().printStackTrace()
                        }
                    }
                }

                key.reset()
            } catch (exception: InterruptedException) {
                // TODO: fix message
                println("Interrupted...")
                isActive.set(false)
                exception.printStackTrace()
            }
        }
    }
}