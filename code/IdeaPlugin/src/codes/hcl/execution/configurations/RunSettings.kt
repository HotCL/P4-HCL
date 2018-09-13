package codes.hcl.execution.configurations

import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.ui.CheckBoxWithDescription
import javax.swing.JComponent
import com.intellij.openapi.ui.LabeledComponent
import javax.swing.JPanel
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import java.awt.FileDialog
import java.awt.Frame
import javax.swing.JCheckBox


class RunSettings: SettingsEditor<RunConfiguration>() {
    private val hclCompiler: LabeledComponent<TextFieldWithBrowseButton> = LabeledComponent<TextFieldWithBrowseButton>()
    .apply {
        label.text = "HCL Compiler"
        component = TextFieldWithBrowseButton().apply {
            text = RunState.hclCompiler ?: ""
            addActionListener { _ ->
                val dialog = FileDialog(null as Frame?, "Select File to Open")
                dialog.mode = FileDialog.LOAD
                dialog.isVisible = true
                component.text = dialog.files.first().absolutePath
            }
            location.x = 0
        }
    }

    private val hclFile: LabeledComponent<TextFieldWithBrowseButton> = LabeledComponent<TextFieldWithBrowseButton>()
            .apply {
                label.text = "HCL File"
                component = TextFieldWithBrowseButton().apply {
                    text = RunState.hclFile ?: ""
                    addActionListener { _ ->
                        val dialog = FileDialog(null as Frame?, "Select File to Open")
                        dialog.mode = FileDialog.LOAD
                        dialog.isVisible = true
                        component.text = dialog.files.first().absolutePath
                    }
                    location.x = 0
                }
            }

    private val runCurrentFile: LabeledComponent<CheckBoxWithDescription> = LabeledComponent<CheckBoxWithDescription>()
            .apply {
                label.text = "HCL File"
                component = CheckBoxWithDescription(JCheckBox("RunCurrentFile"), "Current file: ")
            }

    private val myPanel: JPanel = JPanel().apply {
        add(hclCompiler)
        add(hclFile)
        add(runCurrentFile)
    }
    override fun resetEditorFrom(p0: RunConfiguration) {
    }

    override fun createEditor(): JComponent = myPanel

    override fun applyEditorTo(p0: RunConfiguration) {
        RunState.hclCompiler = hclCompiler.component.text
        RunState.hclFile = hclFile.component.text
        RunState.currentFile = runCurrentFile.isEnabled
    }
}
