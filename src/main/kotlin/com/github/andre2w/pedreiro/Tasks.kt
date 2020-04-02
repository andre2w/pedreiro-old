package com.github.andre2w.pedreiro

interface Task

data class CreateFolder(val path: String) : Task

data class CreateFile(val path: String, val content: String) : Task