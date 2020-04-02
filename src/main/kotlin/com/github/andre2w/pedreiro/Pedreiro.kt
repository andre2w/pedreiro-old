package com.github.andre2w.pedreiro

class Pedreiro(
    private val fileSystemHandler: FileSystemHandler,
    private val environment: Environment
) {

    fun execute(arguments: Array<String>) {
        val argumentParser = ArgumentParser()
        val configurationManager = ConfigurationManager(fileSystemHandler)
        val pedreiroConfiguration =
            configurationManager.loadConfiguration(environment.userHome() + "/.pedreiro/configuration.yml")
        val blueprintService = BlueprintService(pedreiroConfiguration, fileSystemHandler)
        val scaffoldingService = ScaffoldingService(fileSystemHandler, environment)

        val arguments = argumentParser.parse(arguments)
        build(blueprintService, arguments, scaffoldingService)
    }

    private fun build(
        blueprintService: BlueprintService,
        arguments: Arguments,
        scaffoldingService: ScaffoldingService
    ) {
        val tasks = blueprintService.loadBlueprint(arguments.blueprintsFolder)
        scaffoldingService.executeTasks(tasks)
    }

}
