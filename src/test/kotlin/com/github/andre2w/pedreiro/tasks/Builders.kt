package com.github.andre2w.pedreiro.tasks

fun Tasks.Companion.from(vararg tasks: Task) =
    Tasks(tasks.asList())