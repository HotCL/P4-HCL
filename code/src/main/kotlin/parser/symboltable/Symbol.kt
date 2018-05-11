package parser.symboltable

import parser.AstNode

class Symbol(private val types: List<AstNode.Type>) {
    val undeclared get() = types.isEmpty()
    val isFunctions get() = types.firstOrNull() is AstNode.Type.Func
    val isIdentifier get() = !undeclared && !isFunctions
    val functions get() = types.filter { it is AstNode.Type.Func }.map {
        it as AstNode.Type.Func
    }
    val identifier get() = types.first()

    fun <T> handle(
        funHandler: (List<AstNode.Type.Func>) -> T,
        idHandler: (AstNode.Type) -> T,
        errorHandler: () -> T
    ) =
        when {
            isFunctions -> funHandler(functions)
            undeclared -> errorHandler()
            else -> idHandler(identifier)
        }
}
