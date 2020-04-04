package com.github.andre2w.pedreiro.blueprints

import com.github.andre2w.pedreiro.io.Environment
import com.github.andre2w.pedreiro.io.FileSystemHandler

class ScaffoldingService(
    private val fileSystemHandler: FileSystemHandler,
    private val environment: Environment
) {
    fun executeTasks(tasks: List<Task>) {
        tasks.forEach{ task ->
            when (task) {
                is CreateFolder -> fileSystemHandler.createFolder("${environment.currentDir()}/${task.path}")
                is CreateFile -> fileSystemHandler.createFile("${environment.currentDir()}/${task.path}", task.content)
            }
        }
    }
}
