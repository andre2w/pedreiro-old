package com.github.andre2w.pedreiro

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object StartingSimpleProject : Spek({

    describe("The Pedreiro cli") {
        val baseDir = "/home/user/projects"
        val homeDir = "/home/user/pedreiro"
        val blueprintName = "baseGradle"
        val configurationPath = "$homeDir/.pedreiro/configuration.yml"

        val fileSystemHandler = mockk<FileSystemHandler>(relaxUnitFun = true)
        val environment = mockk<Environment>(relaxUnitFun = true)
        val consoleHandler = mockk<ConsoleHandler>(relaxUnitFun = true)
        val blueprintPath = "$homeDir/.pedreiro/blueprints/${blueprintName}.yml"

        val pedreiro = Pedreiro(fileSystemHandler, environment, consoleHandler)

        describe("creating project from a simple blueprint with only folders and files") {
            every { environment.currentDir() } returns baseDir
            every { environment.userHome() } returns homeDir
            every { fileSystemHandler.readFile(configurationPath) } returns Fixtures.CONFIGURATION
            every { fileSystemHandler.readFile(blueprintPath) } returns Fixtures.SIMPLE_TEMPLATE


            pedreiro.execute(arrayOf(blueprintName))

            it("should create the file structure declared in the blueprint") {
                verify {
                    fileSystemHandler.createFolder("${baseDir}/test/src/main/kotlin")
                    fileSystemHandler.createFolder("${baseDir}/test/src/main/resources")
                    fileSystemHandler.createFile("${baseDir}/test/src/build.gradle", Fixtures.BUILD_GRADLE_CONTENT)
               }
            }

            it ("should print print information about template creation and when its done") {
                verify {
                    consoleHandler.print("Creating project from blueprint ($blueprintPath)")
                    consoleHandler.print("Project created. You can start to work now.")
                    consoleHandler.exitWith(0)
                }
            }
        }
    }
})

