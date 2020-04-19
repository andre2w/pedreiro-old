package com.github.andre2w.pedreiro.arguments

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ArgumentsShould {

    @Test
    internal fun `merge with different set of arguments`() {
        val arguments = Arguments("test")

        val mergedArguments : Arguments = arguments.mergeWith(mapOf(
            "version" to "1.2.55",
            "project_name" to "test-project"
        ))

        val expectedArguments = Arguments("test", mapOf(
            "version" to "1.2.55",
            "project_name" to "test-project"
        ))
        assertThat(mergedArguments).isEqualTo(expectedArguments)
    }

    @Test
    internal fun `existing arguments should have priority`() {
        val arguments = Arguments("test", mapOf(
            "version" to "1.2.57"
        ))

        val mergedArguments : Arguments = arguments.mergeWith(mapOf(
            "version" to "1.2.55",
            "project_name" to "test-project"
        ))

        val expectedArguments = Arguments("test", mapOf(
            "version" to "1.2.57",
            "project_name" to "test-project"
        ))
        assertThat(mergedArguments).isEqualTo(expectedArguments)
    }
}