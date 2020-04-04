package com.github.andre2w.pedreiro

import kotlin.system.exitProcess

class ConsoleHandler {
    fun print(text: String) {
        println(text)
    }

    fun exitWith(code: Int) {
        exitProcess(code)
    }

}
