package utils

import generation.FilePair
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

data class CommandResult(val string: String, val returnValue: Int)

fun String.runCommand(workingDir: File = File("./")): CommandResult {
    return try {
        val parts = this.split("\\s".toRegex())
        val proc = ProcessBuilder(*parts.toTypedArray())
            .directory(workingDir)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()

        proc.waitFor(10, TimeUnit.SECONDS)
        CommandResult(proc.inputStream.bufferedReader().readText() +
            proc.errorStream.bufferedReader().readText().let {
                if (it.isNotBlank()) { "Error: $it" } else ""
            }, proc.exitValue())
    } catch (e: IOException) {
        e.printStackTrace()
        CommandResult("IO EXCEPTION!", -1)
    }
}

fun compileCpp(
    files: List<FilePair>,
    dir: String = "testDir",
    keepFiles: Boolean = false,
    outputFile: String = "program"
) {
    File(dir).mkdir()
    try {
        val headerFiles = files.filter { it.fileName.endsWith(".h") }
        val cppFiles = files.filter { it.fileName.endsWith(".cpp") }
        headerFiles.forEach { it.writeFile(dir) }
        cppFiles.forEach {
            it.writeFile(dir)
            "g++ -c ${it.fileName} -o ${it.fileName.removeSuffix(".cpp")} -std=c++14".apply {
                val result = runCommand(File(dir))
                if (result.string.isNotBlank()) println(result.string)
            }
        }
        "g++ ${cppFiles.joinToString(" ") { it.fileName.removeSuffix(".cpp") }} -o $outputFile".apply {
            val result = runCommand(File(dir))
            if (result.string.isNotBlank()) println(result.string)
        }
        val program = File(dir).listFiles().first { it.nameWithoutExtension == outputFile }
        val finalProgram = File(program.name)
        program.copyTo(finalProgram, true)
        finalProgram.setExecutable(true)
    } finally {
        if (!keepFiles) File(dir).deleteRecursively()
    }
}
