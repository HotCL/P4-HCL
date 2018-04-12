package parser.typechecker

import parser.TreeNode
import parser.symboltable.ISymbolTable

interface ITypeChecker: ISymbolTable {
    fun getTypeOfExpression(expr: TreeNode.Command.Expression): TreeNode.Type
    fun checkExpressionTypeMatchesSymbolType(expr: TreeNode.Command.Expression, symbol: String): Boolean
    fun checkExpressionTypesMatch(expr1: TreeNode.Command.Expression, expr2: TreeNode.Command.Expression): Boolean
    val TreeNode.Command.Expression.type: TreeNode.Type
    fun List<TreeNode.Type.Func.ExplicitFunc>.getTypeDeclaration(types: List<TreeNode.Type>):
            TreeNode.Type.Func.ExplicitFunc?
}
