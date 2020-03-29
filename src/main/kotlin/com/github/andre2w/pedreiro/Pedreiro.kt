package com.github.andre2w.pedreiro

class Pedreiro(
    private val scaffoldingService: ScaffoldingService,
    private val templateService: TemplateService,
    private val argumentParser: ArgumentParser
) {

    fun execute(arguments: Array<String>) {
        val arguments = argumentParser.parse(arguments)
        val tasks = templateService.loadTemplate(arguments.templateName)
        scaffoldingService.executeTasks(tasks)
    }

}
