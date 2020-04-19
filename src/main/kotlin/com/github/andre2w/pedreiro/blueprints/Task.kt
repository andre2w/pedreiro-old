package com.github.andre2w.pedreiro.blueprints

interface Task

data class CreateFolder(val path: String) : Task

data class CreateFile(val path: String, val content: String) : Task

data class ExecuteCommand(val command: String, val folder: String) : Task

data class Tasks(val tasks: List<Task>) {
    companion object {
        fun from(tasks : List<Task>)  = Tasks(tasks)
    }
}
