package com.github.andre2w.pedreiro

class BuildService(
    private val templateService: TemplateService,
    private val scaffoldingService: ScaffoldingService
)
{

    fun build(arguments: Arguments) {
        val tasks = templateService.loadTemplate(arguments.templateName)
        scaffoldingService.executeTasks(tasks)
    }
}