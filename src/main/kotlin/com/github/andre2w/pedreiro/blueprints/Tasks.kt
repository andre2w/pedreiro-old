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
        var isQuotedChar = false

        for (character in command) {
            if (character == ' ' && !inString) {
                args.add(currentWord)
                currentWord = ""
                continue
            }

            if (character == '\\') {
                isQuotedChar = true
                continue
            }

            if (character in delimiters && !isQuotedChar) {
                inString = !inString
                continue
            }

            isQuotedChar = false
            currentWord += character
        }

        args.add(currentWord)

        return args
    }
}