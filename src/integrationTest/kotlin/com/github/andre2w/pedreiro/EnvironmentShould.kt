package com.github.andre2w.pedreiro

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class EnvironmentShould {

    @Test
    internal fun `return folder where application was called from`() {
        val currentFolder = System.getProperty("user.dir")

        val environment = Environment()

        assertThat(environment.currentDir()).isEqualTo(currentFolder)
    }

    @Test
    internal fun `know where is user home folder`() {
        val homeFolder = System.getProperty("user.home")

        val environment = Environment()

        assertThat(environment.userHome()).isEqualTo(homeFolder)
    }
}