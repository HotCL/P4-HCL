package codes.hcl.execution.configurations

import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.process.OSProcessHandler



class RunCommandState(executionEnvironment: ExecutionEnvironment,
                      private val hclCompiler: String,
                      private val hclFile: String):
CommandLineState(executionEnvironment) {
    override fun startProcess(): ProcessHandler {
        val line = GeneralCommandLine()
        line.isRedirectErrorStream = true
        line.exePath = "java"
        line.addParameters("-jar", hclCompiler, hclFile)
        val process = line.createProcess()
        return OSProcessHandler(process, line.commandLineString)
    }
}