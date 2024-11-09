package com.github.aivanovski.testswithme.cli.domain

import com.github.aivanovski.testswithme.cli.domain.printer.OutputPrinter
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY
import java.nio.file.WatchService
import kotlin.io.path.name
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class FileWatcherImpl(
    private val printer: OutputPrinter
) : FileWatcher {

    @Volatile
    private var isActive = false

    @Volatile
    private var watcher: WatchService? = null

    private val scope = CoroutineScope(Dispatchers.Default)

    override fun watch(
        file: Path,
        onContentChanged: (file: Path) -> Unit
    ) {
        val fileName = file.name
        val parent = file.parent

        val watcher = FileSystems.getDefault().newWatchService()
            .apply {
                watcher = this
            }

        parent.register(watcher, ENTRY_MODIFY)

        isActive = true
        scope.launch {
            while (isActive) {
                try {
                    val key = watcher.take()
                    if (key == null) {
                        isActive = false
                        continue
                    }

                    for (event in key.pollEvents()) {
                        val eventKind = event.kind()
                        val eventContext = event.context()

                        val eventFileName = if (eventContext is Path) {
                            eventContext.fileName.toString()
                        } else {
                            null
                        }

                        if (eventKind == ENTRY_MODIFY &&
                            eventFileName == fileName
                        ) {
                            printer.debugLine("File changed: $file")
                            onContentChanged.invoke(file)
                        }
                    }

                    key.reset()
                } catch (exception: InterruptedException) {
                    printer.debugLine("Interrupted...")
                    isActive = false
                    exception.printStackTrace()
                }
            }
        }
    }

    override fun cancel() {
        if (!isActive) {
            return
        }

        isActive = false
        watcher?.close()
        watcher = null
        scope.cancel()
    }
}