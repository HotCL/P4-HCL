package hclTestFramework.codegen

import generation.FilePair
import utils.runCommand
import java.io.File

fun compileAndExecuteCpp(files: List<FilePair>) {
    val headerFiles = files.filter { it.fileName.endsWith(".h") }
    val cppFiles = files.filter { it.fileName.endsWith(".cpp") }
    headerFiles.forEach { it.print() }
    cppFiles.forEach {
        it.print()
        "g++ -c ${it.fileName} -o ${it.fileName.removeSuffix(".cpp")} -std=c++1z".apply {
            println(this)
            runCommand()
        }
    }
    println("Linking:")
    "g++ ${cppFiles.joinToString(" ") { it.fileName.removeSuffix(".cpp") }} -o program".apply {
        println(this)
        runCommand()
    }
    println("Running command:")
    "./program".apply {
        println(this)
        runCommand()
    }
}

private fun FilePair.print() = File(fileName).writeText(content)
