package codes.hcl.execution.configurations

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Storage

@Storage("HclRunConfig.xml")
object RunState: PersistentStateComponent<RunState> {
    var currentFile: Boolean = false
    var hclFile: String? = null
    var hclCompiler: String? = null

    override fun getState() = this
    override fun loadState(p0: RunState) {
        currentFile = p0.currentFile
        hclFile = p0.hclFile
        hclCompiler = p0.hclCompiler
    }
}
