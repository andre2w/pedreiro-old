package com.github.andre2w.pedreiro

class BuildService(
    private val blueprintService: BlueprintService,
    private val scaffoldingService: ScaffoldingService
)
{

    fun build(arguments: Arguments) {
        val tasks = blueprintService.loadBlueprint(arguments.blueprintsFolder)
        scaffoldingService.executeTasks(tasks)
    }
}