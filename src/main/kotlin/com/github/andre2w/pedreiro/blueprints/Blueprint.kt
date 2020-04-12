package com.github.andre2w.pedreiro.blueprints

data class Blueprint(val tasks: List<Task>) {

    companion object {
        fun from(tasks : List<Task>)  = Blueprint(tasks)


    }

}
