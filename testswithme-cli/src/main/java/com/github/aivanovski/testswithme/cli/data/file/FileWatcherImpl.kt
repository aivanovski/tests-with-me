package com.github.aivanovski.testswithme.cli.data.file

import com.github.aivanovski.testswithme.utils.mutableStateFlow
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY
import java.nio.file.WatchService
import kotlin.io.path.name
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

class FileWatcherImpl(
    private val onContentChanged: (file: Path) -> Unit
) : FileWatcher {

    var isActive: Boolean by mutableStateFlow(false)
        private set

    private var watcher: WatchService? by mutableStateFlow(null)
    private val scope = CoroutineScope(Dispatchers.Default)

    override fun watch(file: Path) {
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
                            logger.debug("File changed: %s".format(file))
                            onContentChanged.invoke(file)
                        }
                    }

                    key.reset()
                } catch (exception: InterruptedException) {
                    logger.debug("Interrupted...")
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

    companion object {
        private val logger = LoggerFactory.getLogger(FileWatcherImpl::class.java)
    }
}