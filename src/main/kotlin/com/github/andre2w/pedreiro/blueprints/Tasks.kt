package com.github.andre2w.pedreiro.blueprints

interface Task

data class CreateFolder(val path: String) : Task

data class CreateFile(val path: String, val content: String) : Task

data class ExecuteCommand(val command: String, val folder: String) : Task {

    val parsedCommand : List<String>
        get() = parseCommand()

    private fun parseCommand(): ArrayList<String> {
        val args = ArrayList<String>()
        val delimiters = listOf('\'', '"')

        var currentWord = ""
        var inString = false
        var startingQuote = ' '
        var isEscapedChar = false

        for (character in command) {
            if (character == ' ' && !inString) {
                args.add(currentWord)
                currentWord = ""
                continue
            }

            if (character == '\\') {
                isEscapedChar = true
                continue
            }

            if (character in delimiters && !isEscapedChar) {
                if (character == startingQuote || startingQuote == ' ') {
                    inString = !inString

                    startingQuote = if (startingQuote != ' ') {
                        ' '
                    } else {
                        character
                    }

                    continue
                }
            }

            isEscapedChar = false
            currentWord += character
        }

        args.add(currentWord)

        return args
    }
}