package com.github.andre2w.pedreiro.blueprints

import com.github.andre2w.pedreiro.tasks.*

class ScaffoldingService {
    fun execute(tasks: Tasks) {
        tasks.forEach(Task::execute)
    }
}
