package codes.hcl.execution.configurations

import com.intellij.openapi.options.SettingsEditor
import javax.swing.JComponent
import com.intellij.openapi.ui.LabeledComponent
import javax.swing.JPanel
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.util.Key
import org.jdom.Element
import java.awt.FileDialog
import java.awt.Frame


class RunSettings(path: String?, file: String?): SettingsEditor<RunConfiguration>() {
    private val hclCompiler: LabeledComponent<TextFieldWithBrowseButton> = LabeledComponent<TextFieldWithBrowseButton>()
    .apply {
        label.text = "HCL Compiler"
        component = TextFieldWithBrowseButton().apply {
            text = path ?: ""
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
                    text = file ?: ""
                    addActionListener { _ ->
                        val dialog = FileDialog(null as Frame?, "Select File to Open")
                        dialog.mode = FileDialog.LOAD
                        dialog.isVisible = true
                        component.text = dialog.files.first().absolutePath
                    }
                    location.x = 0
                }
            }

    private val myPanel: JPanel = JPanel().apply {
        add(hclCompiler)
        add(hclFile)
    }
    override fun resetEditorFrom(p0: RunConfiguration) {
    }

    override fun createEditor(): JComponent = myPanel

    override fun applyEditorTo(p0: RunConfiguration) {
        p0.hclCompiler = hclCompiler.component.text
        p0.hclFile = hclFile.component.text
    }
}