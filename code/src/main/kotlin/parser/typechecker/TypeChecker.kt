package parser.typechecker

import parser.TreeNode.Command.Expression
import parser.TreeNode
import parser.symboltable.ISymbolTable
import parser.symboltable.SymbolTable

class TypeChecker: ITypeChecker, ISymbolTable by SymbolTable() {

    override fun checkExpressionTypeMatchesSymbolType(expr: Expression, symbol: String) =
            retrieveSymbol(symbol).identifier == getTypeOfExpression(expr)

    override fun checkExpressionTypesMatch(expr1: Expression, expr2: Expression): Boolean =
            getTypeOfExpression(expr1) == getTypeOfExpression(expr2)

    override fun getTypeOfExpression(expr: Expression): TreeNode.Type = when (expr) {
        is Expression.Value.Literal.Number -> TreeNode.Type.Number
        is Expression.Value.Literal.Bool -> TreeNode.Type.Bool
        is Expression.Value.Literal.List -> TreeNode.Type.List(getTypeOfListExpression(expr))
        is Expression.Value.Literal.Text -> TreeNode.Type.Text
        is Expression.Value.Literal.Tuple -> TreeNode.Type.Tuple(getTypeOfTupleExpression(expr))
        is Expression.Value.Identifier -> retrieveSymbol(expr.name).handle(
                { it.getTypeDeclaration(listOf())?.let { it } ?:
                    throw Exception("Can't invoke function, since no overloading with no parameters exist") },
                { it },
                { throw Exception("Undeclared identifier") }
        )
        is Expression.FunctionCall -> {
            val functionDeclarationsSymbol = retrieveSymbol(expr.identifier.name)
            if (!functionDeclarationsSymbol.isFunctions)
                throw Exception("Function with name ${expr.identifier.name} was not defined in symbol table")
            val functionDeclarations = functionDeclarationsSymbol.functions
            val parameterTypes = expr.parameters.map { getTypeOfExpression(it) }
            val functionDeclaration = functionDeclarations.getTypeDeclaration(parameterTypes)
                    ?: throw Exception("No function declaration with provided parameters")
            functionDeclaration.returnType
        }
        is Expression.LambdaExpression -> TreeNode.Type.Func.ExplicitFunc(
                expr.paramDeclarations.map { it.type },
                expr.returnType
        )
    }

    private fun getTypeOfListExpression(list: Expression.Value.Literal.List): TreeNode.Type {
        val ret = getTypeOfExpression(list.elements[0])
        list.elements.drop(1).forEach {
            if (ret != getTypeOfExpression(it)) throw Exception("List has elements of different types")
        }
        return ret
    }

    private fun getTypeOfTupleExpression(tuple: Expression.Value.Literal.Tuple) =
            tuple.elements.map { getTypeOfExpression(it) }

    fun List<TreeNode.Type.Func.ExplicitFunc>.getTypeDeclaration(types: List<TreeNode.Type>) =
            this.firstOrNull{ it.paramTypes == types }

    override val TreeNode.Command.Expression.type get() = getTypeOfExpression(this)
}
