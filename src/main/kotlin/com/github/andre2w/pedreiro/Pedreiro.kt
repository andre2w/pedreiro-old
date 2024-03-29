package com.github.andre2w.pedreiro

import com.github.andre2w.pedreiro.arguments.ArgumentParser
import com.github.andre2w.pedreiro.arguments.Arguments
import com.github.andre2w.pedreiro.blueprints.*
import com.github.andre2w.pedreiro.configuration.ConfigurationManager
import com.github.andre2w.pedreiro.io.ConsoleHandler
import com.github.andre2w.pedreiro.io.Environment
import com.github.andre2w.pedreiro.io.FileSystemHandler
import com.github.andre2w.pedreiro.io.ProcessExecutor

class Pedreiro(
    private val fileSystemHandler: FileSystemHandler,
    private val environment: Environment,
    private val consoleHandler: ConsoleHandler,
    private val processExecutor: ProcessExecutor
) {

    fun execute(args: Array<String>) {
        consoleHandler.print("Setting up Pedreiro toolbox")

        val argumentParser = ArgumentParser()

        val configurationManager = ConfigurationManager(fileSystemHandler)

        val pedreiroConfiguration =
            configurationManager.loadFrom(environment.userHome() + "/.pedreiro/configuration.yml")

        val blueprintReader = BlueprintReader(fileSystemHandler, pedreiroConfiguration, consoleHandler)
        val commandParser = CommandParser()

        val blueprintService = BlueprintService(
            blueprintReader,
            fileSystemHandler,
            environment,
            processExecutor,
            commandParser
        )

        val scaffoldingService = ScaffoldingService()

        val arguments = argumentParser.parse(args)

        try {
            build(blueprintService, arguments, scaffoldingService)
            consoleHandler.print("Project created. You can start to work now.")
            consoleHandler.exitWith(0)
        } catch (err: BlueprintParsingException) {
            consoleHandler.print(err.message ?: "Error while parsing blueprint")
            consoleHandler.exitWith(1)
        }
    }

    private fun build(
        blueprintService: BlueprintService,
        arguments: Arguments,
        scaffoldingService: ScaffoldingService
    ) {
        val blueprint = blueprintService.loadBlueprint(arguments)
        scaffoldingService.execute(blueprint)
    }

}
