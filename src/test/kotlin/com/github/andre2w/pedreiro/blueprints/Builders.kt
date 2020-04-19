package com.github.andre2w.pedreiro.blueprints

internal fun Tasks.Companion.from(vararg tasks: Task) =
    Tasks(tasks.asList())