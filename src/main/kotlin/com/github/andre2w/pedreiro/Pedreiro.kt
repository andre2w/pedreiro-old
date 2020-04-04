package com.github.andre2w.pedreiro

class Pedreiro(
    private val fileSystemHandler: FileSystemHandler,
    private val environment: Environment,
    private val consoleHandler: ConsoleHandler
) {

    fun execute(arguments: Array<String>) {
        val argumentParser = ArgumentParser()
        val configurationManager = ConfigurationManager(fileSystemHandler)
        val pedreiroConfiguration =
            configurationManager.loadConfiguration(environment.userHome() + "/.pedreiro/configuration.yml")
        val blueprintService = BlueprintService(pedreiroConfiguration, fileSystemHandler, consoleHandler)
        val scaffoldingService = ScaffoldingService(fileSystemHandler, environment)

        val arguments = argumentParser.parse(arguments)
        build(blueprintService, arguments, scaffoldingService)

        consoleHandler.print("Project created. You can start to work now.")
        consoleHandler.exitWith(1)
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
