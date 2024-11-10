package com.github.aivanovski.testswithme.cli.data.argument

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.cli.data.file.FileSystemProvider
import com.github.aivanovski.testswithme.cli.entity.exception.ParsingException
import com.github.aivanovski.testswithme.utils.StringUtils
import java.util.LinkedList

class ArgumentParser(
    private val fsProvider: FileSystemProvider
) {

    fun parse(args: Array<String>): Either<ParsingException, Arguments> =
        either {
            val queue = LinkedList<String>()
                .apply {
                    addAll(args)
                }

            var filePath: String = StringUtils.EMPTY
            var isPrintHelp = false

            while (queue.isNotEmpty()) {
                when (val option = NAME_TO_OPTION_MAP[queue.poll()]) {
                    OptionType.WATCH_FILE -> {
                        filePath = checkFilePath(queue.poll()).bind()
                    }

                    OptionType.HELP -> {
                        isPrintHelp = true
                    }

                    OptionType.DEBUG -> {
                    }

                    else -> raise(ParsingException("Unable to parse specified argument: $option"))
                }
            }

            if (filePath.isEmpty() && !isPrintHelp) {
                raise(ParsingException("Please specify file for test"))
            }

            Arguments(
                filePath = filePath,
                isPrintHelp = isPrintHelp
            )
        }

    private fun checkFilePath(path: String?): Either<ParsingException, String> =
        either {
            if (path.isNullOrEmpty() || !fsProvider.exists(path)) {
                raise(ParsingException("File doesn't exist: $path"))
            }

            if (fsProvider.isDirectory(path)) {
                raise(ParsingException("Specified file should not be a directory: $path"))
            }

            // TODO: check that file has .yaml extension

            fsProvider.correctPath(path)
                .mapLeft { exception -> ParsingException(cause = exception) }
                .bind()
        }

    companion object {
        private val NAME_TO_OPTION_MAP = OptionType.entries
            .flatMap { option ->
                listOf(
                    option.shortName to option,
                    option.fullName to option
                )
            }
            .toMap()
    }
}