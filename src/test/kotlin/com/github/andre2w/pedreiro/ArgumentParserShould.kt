package com.github.andre2w.pedreiro

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
}