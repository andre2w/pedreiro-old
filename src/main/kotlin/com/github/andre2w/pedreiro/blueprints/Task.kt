package com.github.andre2w.pedreiro.blueprints

import com.github.andre2w.pedreiro.io.Environment
import com.github.andre2w.pedreiro.io.FileSystemHandler
import com.github.andre2w.pedreiro.io.ProcessExecutor

interface Task {
    fun execute()
}

data class CreateFolder(
    val path: String,
    private val fileSystemHandler: FileSystemHandler,
    private val environment: Environment
) : Task {
    override fun execute() {
        fileSystemHandler.createFolder("${environment.currentDir()}/$path")
    }
}

data class CreateFile(
    val path: String,
    val content: String,
    private val fileSystemHandler: FileSystemHandler,
    private val environment: Environment
) : Task {
    override fun execute() {
        fileSystemHandler.createFile("${environment.currentDir()}/$path",content)
    }
}

data class ExecuteCommand(
    val command: String,
    val folder: String,
    private val processExecutor: ProcessExecutor,
    private val commandParser: CommandParser
) : Task {
    override fun execute() {
        processExecutor.execute(commandParser.parseCommand(command), folder)
    }
}

data class Tasks(val tasks: List<Task>) {
    companion object {
        fun from(tasks : List<Task>)  = Tasks(tasks)
    }
}
