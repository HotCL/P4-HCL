package generation.cpp

import parser.AbstractSyntaxTree
import parser.AstNode
import parser.BuiltinLambdaAttributes

fun AbstractSyntaxTree.genFromFilter(predicate: (AstNode.Command) -> Boolean) =
        CodeGenerator().generate(filter(predicate))

fun AbstractSyntaxTree.builtins() = filter {
    val decl = it as? AstNode.Command.Declaration ?: return@filter false

    val lmbdExpr = decl.expression as?
            AstNode.Command.Expression.LambdaExpression ?: return@filter false

    lmbdExpr.attributes == BuiltinLambdaAttributes
}

fun AbstractSyntaxTree.notBuiltins() = filter { it !in builtins().children }

val AstNode.Type.cpp get() = CppNameTranslator.getValidTypeName(this)

val AstNode.Command.Expression.Value.Literal.List.cpp get() = CppNameTranslator.getValidtListLiteralName(this)

val AstNode.Command.Expression.Value.Identifier.cpp get() = CppNameTranslator.getValidIdentifierName(this)


val AstNode.Type.Func.ExplicitFunc.getGeneric get() = paramTypes;


val AstNode.Type.getGenerics get():List<AstNode.Type.GenericType> = when(this){
    is AstNode.Type.GenericType -> listOf(this)
    is AstNode.Type.List -> this.elementType.getGenerics
    is AstNode.Type.Tuple -> this.elementTypes.flatMap { it.getGenerics }
    is AstNode.Type.Func.ExplicitFunc -> this.paramTypes.flatMap { it.getGenerics } + returnType.getGenerics
    else -> listOf()
}

operator fun String.times (num: Int) = (0 until num).joinToString("") { this }
