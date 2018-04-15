package parser.typechecker

import parser.AstNode

sealed class ExprResult {
    data class Success(val type: AstNode.Type) : ExprResult()
    object NoEmptyOverloading : ExprResult()
    object UndeclaredIdentifier : ExprResult()
    object NoFuncDeclarationForArgs : ExprResult()
    val forceType get() = (this as Success).type
}