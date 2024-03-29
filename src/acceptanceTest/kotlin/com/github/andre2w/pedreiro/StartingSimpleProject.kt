package com.github.andre2w.pedreiro

import com.github.andre2w.pedreiro.io.ConsoleHandler
import com.github.andre2w.pedreiro.io.Environment
import com.github.andre2w.pedreiro.io.FileSystemHandler
import com.github.andre2w.pedreiro.io.ProcessExecutor
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
        val blueprintPath = "$homeDir/.pedreiro/blueprints/${blueprintName}.yml"

        val fileSystemHandler = mockk<FileSystemHandler>(relaxUnitFun = true)
        val environment = mockk<Environment>(relaxUnitFun = true)
        val consoleHandler = mockk<ConsoleHandler>(relaxUnitFun = true)
        val processExecutor = mockk<ProcessExecutor>(relaxUnitFun = true)

        val pedreiro = Pedreiro(fileSystemHandler, environment, consoleHandler, processExecutor)

        every { fileSystemHandler.isFolder("$homeDir/.pedreiro/blueprints/$blueprintName") } returns false
        describe("creating project from a simple blueprint with only folders and files") {
            every { environment.currentDir() } returns baseDir
            every { environment.userHome() } returns homeDir
            every { fileSystemHandler.readFile(configurationPath) } returns SimpleFixtures.CONFIGURATION
            every { fileSystemHandler.readFile(blueprintPath) } returns SimpleFixtures.SIMPLE_TEMPLATE


            pedreiro.execute(arrayOf(blueprintName))

            it("should create the file structure declared in the blueprint") {
                verify {
                    fileSystemHandler.createFolder("${baseDir}/test/src/main/kotlin")
                    fileSystemHandler.createFolder("${baseDir}/test/src/main/resources")
                    fileSystemHandler.createFile("${baseDir}/test/src/build.gradle", SimpleFixtures.BUILD_GRADLE_CONTENT)
               }
            }

            it ("should print information about template creation and when its done") {
                verify {
                    consoleHandler.print("Creating project from blueprint ($blueprintPath)")
                    consoleHandler.print("Project created. You can start to work now.")
                    consoleHandler.exitWith(0)
                }
            }
        }

        describe("create a project executing a command") {
            val command = listOf(
                "gradle",
                "init",
                "--type",
                "java-application",
                "--test-framework",
                "junit",
                "--dsl",
                "groovy",
                "--project-name",
                "test",
                "--package",
                "com.example.test"
            )
            every { environment.currentDir() } returns baseDir
            every { environment.userHome() } returns homeDir
            every { fileSystemHandler.readFile(configurationPath) } returns SimpleFixtures.CONFIGURATION
            every { fileSystemHandler.readFile(blueprintPath) } returns SimpleFixtures.COMMAND_TEMPLATE
            every { processExecutor.execute(command, "$baseDir/test") } returns 0

            pedreiro.execute(arrayOf(blueprintName))

            it("should create a folder and execute command inside") {
                verify {
                    fileSystemHandler.createFolder("$baseDir/test")
                    processExecutor.execute(command, "$baseDir/test")
                }
            }
        }

        describe("when creating a project from a blueprint that doesn't exists") {
            every { environment.currentDir() } returns baseDir
            every { environment.userHome() } returns homeDir
            every { fileSystemHandler.readFile(configurationPath) } returns SimpleFixtures.CONFIGURATION
            every { fileSystemHandler.readFile(blueprintPath) } returns null
            every { fileSystemHandler.readFile("$homeDir/.pedreiro/blueprints/${blueprintName}.yaml") } returns null

            pedreiro.execute(arrayOf(blueprintName))

            it("should display message saying that template was not found") {
                verify { consoleHandler.print("Failed to read blueprint $blueprintName") }
            }

            it("should exit with status code of 1") {
                verify { consoleHandler.exitWith(1) }
            }
        }

        describe("when creating a project from a invalid blueprint") {
            every { environment.currentDir() } returns baseDir
            every { environment.userHome() } returns homeDir
            every { fileSystemHandler.readFile(configurationPath) } returns SimpleFixtures.CONFIGURATION
            every { fileSystemHandler.readFile(blueprintPath) } returns "INVALID TEMPLATE"

            it("should display message saying that failed to load blueprint") {
                verify { consoleHandler.print("Failed to read blueprint $blueprintName") }
            }

            it("should exit with status code 1") {
                verify { consoleHandler.exitWith(1) }
            }
        }
    }
})

