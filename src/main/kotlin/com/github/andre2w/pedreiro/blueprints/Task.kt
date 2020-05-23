package com.github.andre2w.pedreiro.blueprints

import com.github.andre2w.pedreiro.io.Environment
import com.github.andre2w.pedreiro.io.FileSystemHandler

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

data class CreateFile(val path: String, val content: String) : Task {
    override fun execute() {
        TODO("Not yet implemented")
    }
}

data class ExecuteCommand(val command: String, val folder: String) : Task {
    override fun execute() {
        TODO("Not yet implemented")
    }
}

data class Tasks(val tasks: List<Task>) {
    companion object {
        fun from(tasks : List<Task>)  = Tasks(tasks)
    }
}
