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
        val templateService = TemplateService(pedreiroConfiguration, fileSystemHandler)
        val scaffoldingService = ScaffoldingService(fileSystemHandler, environment)

        val arguments = argumentParser.parse(arguments)
        build(templateService, arguments, scaffoldingService)
    }

    private fun build(
        templateService: TemplateService,
        arguments: Arguments,
        scaffoldingService: ScaffoldingService
    ) {
        val tasks = templateService.loadTemplate(arguments.templateName)
        scaffoldingService.executeTasks(tasks)
    }

}
