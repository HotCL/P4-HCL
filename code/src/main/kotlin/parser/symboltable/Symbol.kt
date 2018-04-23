package parser.symboltable

import parser.AstNode

class Symbol(private val types: List<AstNode.Type>) {
    val undeclared get() = types.isEmpty()
    val isFunctions get() = types.firstOrNull() is AstNode.Type.Func.ExplicitFunc
    val isIdentifier get() = !undeclared && !isFunctions
    val functions get() = types.filter { it is AstNode.Type.Func.ExplicitFunc }.map {
        it as AstNode.Type.Func.ExplicitFunc
    }
    val identifier get() = types.first()

    fun<T> handle(funHandler: (List<AstNode.Type.Func.ExplicitFunc>) -> T, idHandler: (AstNode.Type) -> T,
                  errorHandler: () -> T) =
        when {
            isFunctions -> funHandler(functions)
            undeclared -> errorHandler()
            else -> idHandler(identifier)
        }
}

