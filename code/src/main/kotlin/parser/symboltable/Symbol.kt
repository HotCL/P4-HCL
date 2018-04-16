package parser.symboltable

import parser.TreeNode

class Symbol(private val types: List<TreeNode.Type>) {
    val undeclared get() = types.isEmpty()
    val isFunctions get() = types.firstOrNull() is TreeNode.Type.Func.ExplicitFunc
    val isIdentifier get() = !undeclared && !isFunctions
    val functions get() = types.filter { it is TreeNode.Type.Func.ExplicitFunc }.map {
        it as TreeNode.Type.Func.ExplicitFunc
    }
    val identifier get() = types.first()

    fun handle(funHandler: (List<TreeNode.Type.Func.ExplicitFunc>) -> Unit, idHandler: (TreeNode.Type) -> Unit,
               errorHandler: () -> Unit) {
        when {
            isFunctions -> funHandler(functions)
            undeclared -> errorHandler()
            else -> idHandler(identifier)
        }
    }
}

