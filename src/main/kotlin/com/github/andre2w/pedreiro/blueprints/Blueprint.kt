package com.github.andre2w.pedreiro.blueprints

data class Blueprint(val tasks: String, val files: Map<String, String> = emptyMap()) {

    fun fileContentOf(filename: String): String {
        return files.get(filename)!!
    }
}
