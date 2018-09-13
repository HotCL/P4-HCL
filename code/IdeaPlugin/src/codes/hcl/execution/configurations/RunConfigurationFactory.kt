package codes.hcl.execution.configurations

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.project.Project
import com.intellij.execution.configurations.ConfigurationType

open class RunConfigurationFactory(type: ConfigurationType): ConfigurationFactory(type) {
    private val factoryName = "HCL Run configuration factory"

    override fun createTemplateConfiguration(project: Project): RunConfiguration {
        return RunConfiguration(project, this, "HCL")
    }
    override fun getName() = factoryName
}
