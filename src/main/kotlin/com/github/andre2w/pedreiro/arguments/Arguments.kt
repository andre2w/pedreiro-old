package com.github.andre2w.pedreiro.arguments

data class Arguments(val blueprintName: String, val extraArgs: Map<String, String> = emptyMap()) {

    fun mergeWith(argsToMerge: Map<String,String>): Arguments {
        val mergedArguments = HashMap<String, String>()

        argsToMerge.forEach { arg -> mergedArguments[arg.key] = arg.value }
        extraArgs.forEach { arg -> mergedArguments[arg.key] = arg.value }

        return Arguments(blueprintName, mergedArguments)
    }
}
