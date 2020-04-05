package com.github.andre2w.pedreiro.blueprints

import com.github.andre2w.pedreiro.arguments.Arguments

class BuildService(
    private val blueprintService: BlueprintService,
    private val scaffoldingService: ScaffoldingService
)
{

    fun build(arguments: Arguments) {
        val tasks = blueprintService.loadBlueprint(arguments.blueprintName)
        scaffoldingService.executeTasks(tasks)
    }
}