package com.github.andre2w.pedreiro.blueprints

import com.fasterxml.jackson.databind.JsonNode
import com.github.andre2w.pedreiro.configuration.PedreiroConfiguration
import com.github.andre2w.pedreiro.io.ConsoleHandler
import com.github.andre2w.pedreiro.io.FileSystemHandler
import com.github.andre2w.pedreiro.io.YAMLParser
import org.yaml.snakeyaml.error.MarkedYAMLException

class BlueprintService(
    private val configuration: PedreiroConfiguration,
    private val fileSystemHandler: FileSystemHandler,
    private val consoleHandler: ConsoleHandler
) {

    private val objectMapper = YAMLParser.objectMapper

    fun loadBlueprint(blueprintName: String) : List<Task> {
        val blueprintPath = "${configuration.blueprintsFolder}/${blueprintName}.yml"
        val blueprint = fileSystemHandler.readFile(blueprintPath)
            ?: throw BlueprintParsingException("Failed to load blueprint $blueprintName ($blueprintPath)")

        consoleHandler.print("Creating project from blueprint ($blueprintPath)")

        try {
            val blueprintTree = objectMapper.readTree(blueprint)
            return parseBlueprint(blueprintTree)
        } catch (err: MarkedYAMLException) {
            throw BlueprintParsingException("Failed to load blueprint $blueprintName ($blueprintPath)")
        }
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

        if (tree["type"].asText() == "command") {
            return listOf(
                ExecuteCommand(
                    tree["command"].asText(),
                    level))
        }

        val path = if (level.isEmpty()) tree.get("name").asText() else level + "/" + tree.get("name").asText()

        if (tree["type"].asText() == "file") {
            return listOf(
                CreateFile(
                    path,
                    tree["content"].asText()))
        }

        val result = ArrayList<Task>()

        result.add(CreateFolder(path))

        tree["children"]?.forEach { child ->
            result.addAll( parseEntry(path, child))
        }

        return result
    }
}
