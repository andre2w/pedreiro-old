package com.github.andre2w.pedreiro

import com.fasterxml.jackson.databind.JsonNode

class BlueprintService(
    private val configuration: PedreiroConfiguration,
    private val fileSystemHandler: FileSystemHandler,
    private val consoleHandler: ConsoleHandler
) {

    private val objectMapper = YAMLParser.objectMapper

    fun loadBlueprint(blueprintName: String) : List<Task> {
        val blueprintPath = "${configuration.blueprintsFolder}/${blueprintName}.yml"
        val blueprint = fileSystemHandler.readFile(blueprintPath)

        consoleHandler.print("Creating project from blueprint ($blueprintPath)")

        val tree = objectMapper.readTree(blueprint)

        return parseBlueprint(tree)
    }

    private fun parseBlueprint(tree: JsonNode) : List<Task> {
        val result = ArrayList<Task>()

        when {
            tree.isArray -> tree.forEach { node -> result.addAll(parseBlueprint(node)) }
            else -> result.addAll(parseEntry("", tree))
        }

        return result
    }

    private fun parseEntry(level: String, tree: JsonNode): List<Task> {


        val path = if (level.isEmpty()) tree.get("name").asText() else level + "/" + tree.get("name").asText()

        if (tree["type"].asText() == "file") {
            return listOf(CreateFile(path , tree["content"].asText()))
        }

        val result = ArrayList<Task>()

        result.add( CreateFolder(path) )

        tree["children"]?.forEach { child ->
            result.addAll( parseEntry(path, child))
        }

        return result
    }
}
