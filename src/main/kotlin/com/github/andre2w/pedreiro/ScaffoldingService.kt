package com.github.andre2w.pedreiro

class ScaffoldingService(
    private val fileSystemHandler: FileSystemHandler,
    private val environment: Environment
) {
    fun executeTasks(tasks: List<CreateFolder>) {
        tasks.forEach{ task ->
            fileSystemHandler.createFolder("${environment.currentDir()}/${task.path}")
        }
    }
}
