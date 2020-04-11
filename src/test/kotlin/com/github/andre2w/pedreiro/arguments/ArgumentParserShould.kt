package com.github.andre2w.pedreiro.arguments

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class ArgumentParserShould {

    @Test
    fun `parse argument array into data structure`() {
        val argumentParser = ArgumentParser()
        val argList = arrayOf("baseGradle")

        val arguments = argumentParser.parse(argList)

        assertThat(arguments).isEqualTo(Arguments("baseGradle"))
    }

    @ParameterizedTest
    @CsvSource(
        "-a, project_name, pedreiro",
        "--arg, project_name, pedreiro"
    )
    fun `parse -a and --arg to extra arguments`(arg : String, argName: String, argValue: String) {
        val argumentParser = ArgumentParser()

        val arguments = argumentParser.parse(arrayOf("baseGradle", arg, "$argName=$argValue"))


        val extraArgs = mapOf(argName to argValue)
        assertThat(arguments).isEqualTo(Arguments("baseGradle", extraArgs))
    }


    @Test
    internal fun `accumulate extra arguments`() {
       val argumentParser = ArgumentParser()

        val arguments = argumentParser.parse(
            arrayOf("baseGradle", "-a","project_name=pedreiro","--arg","project_group=com.github.andre2w")
        )

        val extraArgs = mapOf(
            "project_name" to "pedreiro",
            "project_group" to "com.github.andre2w"
        )
        assertThat(arguments).isEqualTo(Arguments("baseGradle", extraArgs))
    }
}