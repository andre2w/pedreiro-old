package com.github.andre2w.pedreiro.blueprints

class CommandParser {
    private val delimiters = listOf('\'', '"')

    fun parseCommand(command: String): ArrayList<String> {
        val args = ArrayList<String>()

        var currentWord = ""
        var inString = false
        var startingQuote = ' '
        var isEscapedChar = false

        for (character in command) {
            if (character == ' ' && !inString) {
                args.add(currentWord)
                currentWord = ""
            } else if (character == '\\') {
                isEscapedChar = true
            } else if (isQuotation(character, isEscapedChar, startingQuote)) {
                inString = !inString
                startingQuote = startingQuoteFrom(startingQuote, character)
            } else {
                isEscapedChar = false
                currentWord += character
            }
        }

        args.add(currentWord)

        return args
    }

    private fun startingQuoteFrom(startingQuote: Char, character: Char): Char =
        if (startingQuote != ' ') {
            ' '
        } else {
            character
        }

    private fun isQuotation(character: Char, isEscapedChar: Boolean, startingQuote: Char) =
        character in delimiters && !isEscapedChar && (character == startingQuote || startingQuote == ' ')
}
