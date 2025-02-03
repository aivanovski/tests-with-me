package com.github.aivanovski.testswithme.cli.data.argument

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.cli.data.file.FileSystemProvider
import com.github.aivanovski.testswithme.cli.entity.ScreenSize
import com.github.aivanovski.testswithme.cli.entity.exception.ParsingException
import com.github.aivanovski.testswithme.extensions.toIntSafely
import com.github.aivanovski.testswithme.utils.StringUtils
import java.util.LinkedList

class ArgumentParser(
    private val fsProvider: FileSystemProvider
) {

    fun parse(args: List<String>): Either<ParsingException, Arguments> =
        either {
            val queue = LinkedList<String>()
                .apply {
                    addAll(args)
                }

            var filePath: String = StringUtils.EMPTY
            var isPrintHelp = (args.isEmpty())
            var screenSize: ScreenSize? = null

            while (queue.isNotEmpty()) {
                val optionName = queue.poll()
                when (NAME_TO_OPTION_MAP[optionName]) {
                    OptionType.WATCH_FILE -> {
                        filePath = checkFilePath(queue.poll()).bind()
                    }

                    OptionType.HELP -> {
                        isPrintHelp = true
                    }

                    OptionType.SCREEN_SIZE -> {
                        screenSize = parseScreenSize(queue.poll()).bind()
                    }

                    else -> raise(
                        ParsingException("Unable to parse specified argument: $optionName")
                    )
                }
            }

            if (filePath.isEmpty() && !isPrintHelp) {
                raise(ParsingException("Please specify file for test"))
            }

            Arguments(
                filePath = filePath,
                isPrintHelp = isPrintHelp,
                screenSize = screenSize
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

    private fun parseScreenSize(value: String?): Either<ParsingException, ScreenSize> =
        either {
            val optionName = OptionType.SCREEN_SIZE.fullName

            if (value.isNullOrEmpty()) {
                raise(ParsingException("Option $optionName should not be empty"))
            }

            val values = value.split("X", ignoreCase = true)
                .mapNotNull { v -> v.toIntSafely() }

            if (values.size != 2) {
                raise(ParsingException("Failed to parse $optionName value: $value"))
            }

            ScreenSize(
                width = values[0],
                height = values[1]
            )
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