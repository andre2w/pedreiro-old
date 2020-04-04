package com.github.andre2w.pedreiro.blueprints

import com.github.andre2w.pedreiro.arguments.Arguments
import com.github.andre2w.pedreiro.blueprints.BlueprintService
import com.github.andre2w.pedreiro.blueprints.ScaffoldingService

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