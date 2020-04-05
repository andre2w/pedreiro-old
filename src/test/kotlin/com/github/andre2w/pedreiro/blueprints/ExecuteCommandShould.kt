package com.github.andre2w.pedreiro.blueprints

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ExecuteCommandShould {

    @Test
    fun `parse command to list of arguments`() {
        val expectedCommand = listOf(
            "gradle",
            "init",
            "--type",
            "java-application",
            "--test-framework",
            "junit",
            "--dsl",
            "groovy",
            "--project-name",
            "test",
            "--package",
            "com.example.test"
        )
        val command = expectedCommand.joinToString(" ")

        val executeCommand = ExecuteCommand(command, "")
        val parsedCommand = executeCommand.parsedCommand

        assertThat(parsedCommand).isEqualTo(expectedCommand)
    }

    @Test
    fun `parse command with multiple words string enclosed with double quote as argument`() {
        val executeCommand = ExecuteCommand("echo \"test argument parsing\"", "")
        val parsedCommand = executeCommand.parsedCommand

        val expectedCommand = listOf("echo", "test argument parsing")

        assertThat(parsedCommand).isEqualTo(expectedCommand)
    }

    @Test
    fun `parse command with multiple words string enclosed with single quote as argument`() {
        val executeCommand = ExecuteCommand("echo \'test argument parsing\'", "")
        val parsedCommand = executeCommand.parsedCommand

        val expectedCommand = listOf("echo", "test argument parsing")

        assertThat(parsedCommand).isEqualTo(expectedCommand)
    }

    @Test
    fun `parse command with escaped quote`() {
        val executeCommand = ExecuteCommand("echo \'there\\'s a quote in this argument\'", "")
        val parsedCommand = executeCommand.parsedCommand

        val expectedCommand = listOf("echo", "there\'s a quote in this argument")

        assertThat(parsedCommand).isEqualTo(expectedCommand)
    }
}