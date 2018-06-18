package codes.hcl.execution.configurations

import com.intellij.execution.Executor
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunConfigurationBase
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key

class RunConfiguration(project: Project, factory: ConfigurationFactory, name: String):
RunConfigurationBase(project, factory, name) {
    override fun getState(p0: Executor, p1: ExecutionEnvironment): RunProfileState? =
            RunCommandState(p1)
    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> = RunSettings()
}
