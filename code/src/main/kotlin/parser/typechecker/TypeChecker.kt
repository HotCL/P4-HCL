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
            if(functionDeclaration == null)
                ExprResult.UndeclaredIdentifier
            else {
                val returnType =
                        if(functionDeclaration.returnType !is AstNode.Type.GenericType) functionDeclaration.returnType
                        else (functionDeclaration.paramTypes.zip(argumentTypes).
                                getTypeFromGenericType(functionDeclaration.returnType.name)
                                ?: functionDeclaration.returnType)

                ExprResult.Success(returnType)
            }
        }
        is Expression.LambdaExpression -> ExprResult.Success(AstNode.Type.Func.ExplicitFunc(
                expr.paramDeclarations.map { it.type }, expr.returnType)
        )
    }

    private fun getTypeOfListExpression(list: Expression.Value.Literal.List) = list.elements[0]

    private fun getTypeOfTupleExpression(tuple: Expression.Value.Literal.Tuple) =
            tuple.elements.map { it.type.forceType }

    override fun List<AstNode.Type.Func.ExplicitFunc>.getTypeDeclaration(types: List<AstNode.Type>):
            AstNode.Type.Func.ExplicitFunc? {
        val genericTypes = mutableMapOf<String,AstNode.Type>()

        return this.firstOrNull{
            it.paramTypes.zip(types).all{
                if(it.first == it.second)
                {
                    if(it.first is AstNode.Type.GenericType)
                        TODO("throw ambigous type error")
                    true
                }
                else it.matchGenerics(genericTypes)
            }
        }
    }

    /**
     * Pair = declaredType and argumentType
     * It is certain that argumentType doesn't contain any generics. This is a rule of the language
     */
    private fun List<Pair<AstNode.Type,AstNode.Type>>.getTypeFromGenericType(typeName:String) : AstNode.Type? =
            map { it.getTypeFromGenericType(typeName) }.firstOrNull { it != null }

    private fun Pair<AstNode.Type,AstNode.Type>.getTypeFromGenericType(typeName:String) : AstNode.Type? {

        val declaredType = first
        val argumentType = second
        return when(declaredType){
            is AstNode.Type.GenericType ->
                if(declaredType.name == typeName) argumentType
                else null

            is AstNode.Type.List ->
                    Pair(declaredType.elementType, (argumentType as AstNode.Type.List).elementType).
                            getTypeFromGenericType(typeName)
            // expecting count to be the same, as matchGenerics should be run before using getTypeFromGenericType
            is AstNode.Type.Func.ExplicitFunc ->
                    declaredType.paramTypes.zip((argumentType as AstNode.Type.Func.ExplicitFunc).paramTypes).
                            getTypeFromGenericType(typeName)


            // expecting count to be the same, as matchGenerics should be run before using getTypeFromGenericType
            is AstNode.Type.Tuple ->
                    declaredType.elementTypes.zip((argumentType as AstNode.Type.Tuple).elementTypes).
                            getTypeFromGenericType(typeName)

            else -> null
        }
    }

    /**
     * Pair = declaredType and argumentType
     */
    private fun Pair<AstNode.Type,AstNode.Type>.matchGenerics(genericTypes:MutableMap<String,AstNode.Type>): Boolean{
        val declaredType = first
        val argumentType = second

        return when(declaredType){

            is AstNode.Type.List ->
                if(argumentType is AstNode.Type.List)
                    Pair(declaredType.elementType, argumentType.elementType).matchGenerics(genericTypes)
                else false

            is AstNode.Type.Func.ExplicitFunc ->
                if(argumentType is AstNode.Type.Func.ExplicitFunc &&
                        declaredType.paramTypes.count() == argumentType.paramTypes.count())
                    Pair(declaredType.returnType,argumentType.returnType).matchGenerics(genericTypes) &&
                            declaredType.paramTypes.zip(argumentType.paramTypes).all{ it.matchGenerics(genericTypes) }
                else false

            is AstNode.Type.Tuple ->
                if(argumentType is AstNode.Type.Tuple &&
                        declaredType.elementTypes.count() == argumentType.elementTypes.count())
                    declaredType.elementTypes.zip(argumentType.elementTypes).all{
                        it.matchGenerics(genericTypes)
                    }
                else false
            else -> {
                if(declaredType is AstNode.Type.GenericType)
                {
                    if(genericTypes[declaredType.name] == null)
                        genericTypes[declaredType.name] = argumentType
                    genericTypes[declaredType.name] == argumentType
                }
                else declaredType == argumentType
            }

        }
    }


    override val AstNode.Command.Expression.type get() = getTypeOfExpression(this)
}
