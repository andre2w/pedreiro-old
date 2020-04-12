package com.github.andre2w.pedreiro.blueprints

import com.github.andre2w.pedreiro.io.Environment
import com.github.andre2w.pedreiro.io.FileSystemHandler
import com.github.andre2w.pedreiro.io.ProcessExecutor

class ScaffoldingService(
    private val fileSystemHandler: FileSystemHandler,
    private val environment: Environment,
    private val processExecutor: ProcessExecutor
) {
    fun executeTasks(tasks: List<Task>) {
        val currentDir = environment.currentDir()

        tasks.forEach { task -> executeTask(task, currentDir) }
    }

    private fun executeTask(task: Task, currentDir: String) {
        when (task) {
            is CreateFolder -> fileSystemHandler.createFolder("$currentDir/${task.path}")
            is CreateFile -> fileSystemHandler.createFile("$currentDir/${task.path}", task.content)
            is ExecuteCommand -> processExecutor.execute(task.parsedCommand, "$currentDir/${task.folder}")
        }
    }
}
