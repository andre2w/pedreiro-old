package com.github.andre2w.pedreiro.arguments

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ArgumentParserShould {

    @Test
    fun `parse argument array into data structure`() {
        val argumentParser = ArgumentParser()
        val argList = arrayOf("baseGradle")

        val arguments = argumentParser.parse(argList)

        assertThat(arguments).isEqualTo(Arguments("baseGradle"))
    }

    @Test
    internal fun `parse -a and --arg to extra arguments`() {
        val argumentParser = ArgumentParser()
        val argList = arrayOf(
            "baseGradle",
            "-a",
            "project_name=pedreiro"
        )

        val arguments = argumentParser.parse(argList)

        val extraArgs = mapOf(
            "project_name" to "pedreiro"
        )
        assertThat(arguments).isEqualTo(Arguments("baseGradle", extraArgs))
    }
}