package utils

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
        CommandResult(proc.inputStream.bufferedReader().readText() + proc.errorStream.bufferedReader().readText().let {
            if (it.isNotBlank()) { "Error: $it" } else ""
        }, proc.exitValue())
    } catch (e: IOException) {
        e.printStackTrace()
        CommandResult("IO EXCEPTION!", -1)
    }
}
