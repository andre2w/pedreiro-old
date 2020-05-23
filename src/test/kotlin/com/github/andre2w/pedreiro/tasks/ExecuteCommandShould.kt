package com.github.andre2w.pedreiro.tasks

import com.github.andre2w.pedreiro.blueprints.CommandParser
import com.github.andre2w.pedreiro.io.Environment
import com.github.andre2w.pedreiro.io.ProcessExecutor
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class ExecuteCommandShould {

    @Test
    internal fun `execute command in the proper folder`() {
        val processExecutor = mockk<ProcessExecutor>(relaxUnitFun = true)
        val environment = mockk<Environment>()
        val commandParser = CommandParser()
        val runFolder = "/home/andre/projects"
        val command = listOf("gradle", "wrapper")
        every { processExecutor.execute(command, "$runFolder/test-project") } returns 0
        every { environment.currentDir() } returns runFolder

        val executeCommand = ExecuteCommand(
            "gradle wrapper",
            "test-project",
            processExecutor,
            commandParser,
            environment
        )
        executeCommand.execute()

        verify {
            processExecutor.execute(command, "$runFolder/test-project")
        }
    }
}