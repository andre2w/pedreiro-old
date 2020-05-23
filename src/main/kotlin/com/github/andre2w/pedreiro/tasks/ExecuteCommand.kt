package com.github.andre2w.pedreiro.tasks

import com.github.andre2w.pedreiro.blueprints.CommandParser
import com.github.andre2w.pedreiro.io.Environment
import com.github.andre2w.pedreiro.io.ProcessExecutor

data class ExecuteCommand(
    val command: String,
    val folder: String,
    private val processExecutor: ProcessExecutor,
    private val commandParser: CommandParser,
    private val environment: Environment
) : Task {
    override fun execute() {
        processExecutor.execute(commandParser.parseCommand(command), "${environment.currentDir()}/$folder")
    }
}