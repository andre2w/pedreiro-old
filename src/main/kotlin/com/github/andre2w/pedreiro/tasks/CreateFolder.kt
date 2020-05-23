package com.github.andre2w.pedreiro.tasks

import com.github.andre2w.pedreiro.io.Environment
import com.github.andre2w.pedreiro.io.FileSystemHandler

data class CreateFolder(
    val path: String,
    private val fileSystemHandler: FileSystemHandler,
    private val environment: Environment
) : Task {
    override fun execute() {
        fileSystemHandler.createFolder("${environment.currentDir()}/$path")
    }
}