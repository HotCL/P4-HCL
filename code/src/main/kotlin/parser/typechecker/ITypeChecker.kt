package parser.typechecker

import parser.AstNode
import parser.symboltable.ISymbolTable

interface ITypeChecker: ISymbolTable {
    fun getTypeOfExpression(expr: AstNode.Command.Expression): ExprResult
    val AstNode.Command.Expression.type: ExprResult
    fun List<AstNode.Type.Func.ExplicitFunc>.getTypeDeclaration(types: List<AstNode.Type>):
            AstNode.Type.Func.ExplicitFunc?
}
