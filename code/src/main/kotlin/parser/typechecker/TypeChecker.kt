package parser.typechecker


import parser.AstNode.Command.Expression
import parser.AstNode
import parser.symboltable.ISymbolTable
import parser.symboltable.SymbolTable

class TypeChecker: ITypeChecker, ISymbolTable by SymbolTable() {

    override fun checkExpressionTypeMatchesSymbolType(expr: Expression, symbol: String) =
            retrieveSymbol(symbol).handle({ true }, { it == expr.type }, { false })

    override fun checkExpressionTypesMatch(expr1: Expression, expr2: Expression) = expr1.type == expr2.type
    //TODO(delete checkExpressionTypesMatch if never used)

    override fun getTypeOfExpression(expr: Expression): ExprResult = when (expr) {
        is Expression.Value.Literal.Number -> ExprResult.Success(AstNode.Type.Number)
        is Expression.Value.Literal.Bool -> ExprResult.Success(AstNode.Type.Bool)
        is Expression.Value.Literal.List ->
            getTypeOfListExpression(expr).type.let {
                if (it is ExprResult.Success) ExprResult.Success(AstNode.Type.List(it.type)) else it
            }
        is Expression.Value.Literal.Text -> ExprResult.Success(AstNode.Type.Text)
        is Expression.Value.Literal.Tuple ->
            ExprResult.Success(AstNode.Type.Tuple(getTypeOfTupleExpression(expr)))
        is Expression.Value.Identifier -> retrieveSymbol(expr.name).handle(
                /*{ it.getTypeDeclaration(listOf())?.returnType?.let { ExprResult.Success(it) }?:
                    ExprResult.NoEmptyOverloading },*/
                // TODO fix so works with overloading
                { ExprResult.Success(it.first())},
                { ExprResult.Success(it) },
                { ExprResult.UndeclaredIdentifier }
        )
        is Expression.LambdaBody -> {
            val returnStatements = expr.commands.filterIsInstance<AstNode.Command.Return>()
            if (returnStatements.isEmpty()) ExprResult.Success(AstNode.Type.None) else {
                if (returnStatements.all { returnStatements.first().expression.type == it.expression.type } ) {
                    returnStatements.first().expression.type
                } else {
                    ExprResult.BodyWithMultiReturnTypes
                }
            }
        }
        is Expression.FunctionCall -> {
            val functionDeclarationsSymbol = retrieveSymbol(expr.identifier.name)
            if (!functionDeclarationsSymbol.isFunctions) ExprResult.NoFuncDeclarationForArgs
            val functionDeclarations = functionDeclarationsSymbol.functions
            val argumentTypes = expr.arguments.map { it.type.forceType }
            val functionDeclaration = functionDeclarations.getTypeDeclaration(argumentTypes)
            functionDeclaration?.let { ExprResult.Success(functionDeclaration.returnType) } ?:
                                       ExprResult.UndeclaredIdentifier
        }
        is Expression.LambdaExpression -> ExprResult.Success(AstNode.Type.Func.ExplicitFunc(
                expr.paramDeclarations.map { it.type }, expr.returnType)
        )
    }

    private fun getTypeOfListExpression(list: Expression.Value.Literal.List) = list.elements[0]

    private fun getTypeOfTupleExpression(tuple: Expression.Value.Literal.Tuple) =
            tuple.elements.map { it.type.forceType }

    override fun List<AstNode.Type.Func.ExplicitFunc>.getTypeDeclaration(types: List<AstNode.Type>) =
            this.firstOrNull{ it.paramTypes == types }

    override val AstNode.Command.Expression.type get() = getTypeOfExpression(this)
}
