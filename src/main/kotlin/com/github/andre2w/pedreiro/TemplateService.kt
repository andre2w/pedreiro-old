package com.github.andre2w.pedreiro

import org.yaml.snakeyaml.Yaml

class TemplateService(
    private val configuration: PedreiroConfiguration,
    private val fileSystemHandler: FileSystemHandler
) {
    fun loadTemplate(templateName: String) : List<Task> {
        val template = fileSystemHandler.readFile("${configuration.templatesFolder}/${templateName}.yml")
        val yaml = Yaml()
        val yamlTemplate = yaml.load<Any>(template)

        return parseTemplate(yamlTemplate)
    }

    private fun parseTemplate(yaml: Any) : List<Task> {
        val result = ArrayList<Task>()


        when (yaml) {
            is List<*> -> (yaml as List<Any>).forEach { item -> result.addAll(parseTemplate(item)) }
            is Map<*, *> -> result.addAll(parseEntry(yaml as Map<String, Any>, ""))
            else -> println(yaml)
        }

        return result
    }

    private fun parseEntry(entry: Map<String, Any>, level: String): List<Task> {
        val path = if (level.isEmpty()) entry["name"].toString() else level + "/" + entry["name"]

        if (entry["type"] == "file") {
            return listOf(CreateFile(path , entry["content"].toString()))
        }

        val result = ArrayList<Task>()

        result.add( CreateFolder(path) )

        entry["children"]?.let { children ->
            (children as List<Map<String,Any>>).forEach { child ->
                result.addAll( parseEntry(child, path) )
            }
        }

        return result
    }
}
