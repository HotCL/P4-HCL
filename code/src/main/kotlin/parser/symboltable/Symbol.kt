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

    fun<T> handle(funHandler: (List<TreeNode.Type.Func.ExplicitFunc>) -> T, idHandler: (TreeNode.Type) -> T,
               errorHandler: () -> T) =
        when {
            isFunctions -> funHandler(functions)
            undeclared -> errorHandler()
            else -> idHandler(identifier)
        }
}

