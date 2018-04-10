package parser.typechecker

import parser.TreeNode
import parser.symboltable.ISymbolTable

interface ITypeChecker: ISymbolTable {
    fun getTypeOfExpression(expr: TreeNode.Command.Expression): TreeNode.Type
    fun checkExpressionTypeMatchesSymbolType(expr: TreeNode.Command.Expression, symbol: String): Boolean
}