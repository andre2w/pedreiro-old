package com.github.andre2w.pedreiro.blueprints

internal fun Blueprint.Companion.from(vararg tasks: Task) =
    Blueprint(tasks.asList())