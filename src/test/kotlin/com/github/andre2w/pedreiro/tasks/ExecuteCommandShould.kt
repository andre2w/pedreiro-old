package com.github.andre2w.pedreiro.tasks

import com.github.andre2w.pedreiro.blueprints.CommandParser
import com.github.andre2w.pedreiro.io.ProcessExecutor
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class ExecuteCommandShould {

    @Test
    internal fun `execute command in the proper folder`() {
        val processExecutor = mockk<ProcessExecutor>(relaxUnitFun = true)
        val commandParser = CommandParser()
        val runFolder = "/home/andre/projects/test-project"
        val command = listOf("gradle", "wrapper")
        every { processExecutor.execute(command, runFolder) } returns 0
        val executeCommand = ExecuteCommand(
            "gradle wrapper",
            runFolder,
            processExecutor,
            commandParser
        )
        executeCommand.execute()

        verify {
            processExecutor.execute(command, runFolder)
        }
    }
}