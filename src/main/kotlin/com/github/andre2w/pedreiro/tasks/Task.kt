package com.github.andre2w.pedreiro.tasks

interface Task {
    fun execute()
}

data class Tasks(val tasks: List<Task>) {
    companion object {
        fun from(tasks : List<Task>)  =
            Tasks(tasks)
    }

    fun forEach(action: (Task) -> Unit) {
        tasks.forEach(action)
    }
}
