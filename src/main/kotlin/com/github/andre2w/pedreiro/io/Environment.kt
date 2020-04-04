package com.github.andre2w.pedreiro.io

class Environment {

    fun currentDir(): String = System.getProperty("user.dir")

    fun userHome(): String = System.getProperty("user.home")

}
