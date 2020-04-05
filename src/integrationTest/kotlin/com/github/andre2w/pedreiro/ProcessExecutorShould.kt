package com.github.andre2w.pedreiro

import com.github.andre2w.pedreiro.io.ProcessExecutor
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Instant

class ProcessExecutorShould {

    @Test
    fun `execute shell commands in the specified folder`() {
        val processExecutor = ProcessExecutor()
        val userDir = System.getProperty("user.dir")
        val fileName = "test-${Instant.now()}.txt"

        val exitCode = processExecutor.execute(listOf("touch", fileName), userDir)

        val createdFile = "$userDir/$fileName"
        assertThat(Files.exists(Paths.get(createdFile))).isTrue()
        assertThat(exitCode).isEqualTo(0)
        Files.delete(Paths.get(createdFile))
    }
}