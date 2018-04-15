package parser.typechecker

import parser.AstNode.Command.Expression
import parser.AstNode
import parser.symboltable.ISymbolTable
import parser.symboltable.SymbolTable

class TypeChecker: ITypeChecker, ISymbolTable by SymbolTable() {

    override fun checkExpressionTypeMatchesSymbolType(expr: Expression, symbol: String) =
            retrieveSymbol(symbol).handle({ true }, { it == expr.type }, { false })

    override fun checkExpressionTypesMatch(expr1: Expression, expr2: Expression) = expr1.type == expr2.type

    override fun getTypeOfExpression(expr: Expression): AstNode.Type = when (expr) {
        is Expression.Value.Literal.Number -> AstNode.Type.Number
        is Expression.Value.Literal.Bool -> AstNode.Type.Bool
        is Expression.Value.Literal.List -> AstNode.Type.List(getTypeOfListExpression(expr))
        is Expression.Value.Literal.Text -> AstNode.Type.Text
        is Expression.Value.Literal.Tuple -> AstNode.Type.Tuple(getTypeOfTupleExpression(expr))
        is Expression.Value.Identifier -> retrieveSymbol(expr.name).handle(
                { it.getTypeDeclaration(listOf())?.returnType ?:
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
        is Expression.LambdaExpression -> AstNode.Type.Func.ExplicitFunc(
                expr.paramDeclarations.map { it.type },
                expr.returnType
        )
    }

    private fun getTypeOfListExpression(list: Expression.Value.Literal.List): AstNode.Type {
        val ret = getTypeOfExpression(list.elements[0])
        list.elements.drop(1).forEach {
            if (ret != it.type) throw Exception("List has elements of different types")
        }
        return ret
    }

    private fun getTypeOfTupleExpression(tuple: Expression.Value.Literal.Tuple) =
            tuple.elements.map { it.type }

    override fun List<AstNode.Type.Func.ExplicitFunc>.getTypeDeclaration(types: List<AstNode.Type>) =
            this.firstOrNull{ it.paramTypes == types }

    override val AstNode.Command.Expression.type get() = getTypeOfExpression(this)
}
