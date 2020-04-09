package com.github.andre2w.pedreiro.arguments

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.delimiter
import kotlinx.cli.multiple


class ArgumentParser {


    fun parse(arguments: Array<String>) : Arguments {
        val argParser = ArgParser("pedreiro")
        val blueprintName by argParser.argument(ArgType.String,
            description = "Name of the template that you want to execute")

        val extraArguments by argParser.option(ArgType.String,"arg", "a")
            .multiple()
            .delimiter(",")

        argParser.parse(arguments)

        val parsedExtraArguments = extraArguments
            .map { arg -> arg.split("=") }
            .fold(HashMap<String, String>()) {
                args, arg -> args[arg[0]] = arg[1]
                args
            }

        return Arguments(blueprintName, parsedExtraArguments)
    }

}
