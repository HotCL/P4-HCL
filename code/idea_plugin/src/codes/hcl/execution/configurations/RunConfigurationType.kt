package codes.hcl.execution.configurations

import com.intellij.execution.configurations.ConfigurationType
import com.intellij.icons.AllIcons
import javax.swing.Icon

class RunConfigurationType: ConfigurationType {
    override fun getIcon(): Icon = AllIcons.FileTypes.Htaccess

    override fun getConfigurationTypeDescription() = "HCL Run configuration"
    override fun getId() = "HCL_RUN_CONFIG"
    override fun getDisplayName() = "HCL"
    override fun getConfigurationFactories() = arrayOf(RunConfigurationFactory(this))
}