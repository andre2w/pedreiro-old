package com.github.andre2w.pedreiro

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object StartingSimpleProject : Spek({

    describe("The Pedreiro cli") {
        val baseDir = "/home/user/projects"
        val templateName = "baseGradle"

        val (fileSystemHandler, environment, pedreiro) = createPedreiroInstance()

        describe("creating project from a simple template with only folders and files") {
            every { environment.currentDir() } returns baseDir
            every { fileSystemHandler.readFile("/home/user/.pedreiro/${templateName}.yml") } returns Fixtures.simpleTemplate

            pedreiro.execute(arrayOf(templateName))

            it("should create the file structure declared in the template") {

                verify {
                    fileSystemHandler.createFolder("${baseDir}/test/src/main/kotlin")
                    fileSystemHandler.createFolder("${baseDir}/test/src/main/resources")
                    fileSystemHandler.createFile("${baseDir}/build.gradle", Fixtures.build_gradle_content)
               }
            }
        }
    }
})

private fun createPedreiroInstance(): Triple<FileSystemHandler, Environment, Pedreiro> {
    val fileSystemHandler = mockk<FileSystemHandler>(relaxUnitFun = true)
    val environment = mockk<Environment>()
    val scaffoldingService = ScaffoldingService(fileSystemHandler, environment)
    val configuration = PedreiroConfiguration("/home/user/.pedreiro")
    val templateService = TemplateService(configuration, fileSystemHandler)
    val argumentParser = ArgumentParser()
    val pedreiro = Pedreiro(scaffoldingService, templateService, argumentParser)
    return Triple(fileSystemHandler, environment, pedreiro)
}