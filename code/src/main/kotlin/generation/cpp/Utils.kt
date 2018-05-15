package generation.cpp

import parser.AbstractSyntaxTree
import parser.AstNode
import parser.BuiltinLambdaAttributes

fun AbstractSyntaxTree.genFromFilter(predicate: (AstNode.Command) -> Boolean) =
        CodeGenerator().generate(filter(predicate))

fun AbstractSyntaxTree.genForLoop(): String {
    val loop = children.firstOrNull {
        (it as? AstNode.Command.Expression.FunctionCall)?.let { it.identifier.name == "loop" } ?: false
    }
    return if (loop != null) {
        CodeGenerator().generate(AbstractSyntaxTree(
                ((loop as AstNode.Command.Expression.FunctionCall).arguments.first()
                        as AstNode.Command.Expression.LambdaExpression).body.commands)
        )
    } else ""
}

fun AbstractSyntaxTree.genFromFilterWithMap(
    predicate: (AstNode.Command) -> Boolean,
    mapFunc: (AstNode.Command) -> AstNode.Command
) =
        AbstractSyntaxTree(children.map(mapFunc)).genFromFilter(predicate)

fun AbstractSyntaxTree.builtins() = filter {
    val decl = it as? AstNode.Command.Declaration ?: return@filter false

    val lmbdExpr = decl.expression as?
            AstNode.Command.Expression.LambdaExpression ?: return@filter false

    lmbdExpr.attributes == BuiltinLambdaAttributes
}

fun AbstractSyntaxTree.notBuiltins() = filter { it !in builtins().children }

val AstNode.Type.cppName get() = CppNameTranslator.getValidTypeName(this)

val AstNode.Command.Expression.Value.Literal.List.cppName get() = CppNameTranslator.getValidtListLiteralName(this)

val AstNode.Command.Expression.Value.Identifier.cppName get() = CppNameTranslator.getValidIdentifierName(this)

val AstNode.Command.Expression.cppName get() = CppNameTranslator.getValidIdentifierName(this)
val String.cppName get() =
    CppNameTranslator.getValidIdentifierName(parser.AstNode.Command.Expression.Value.Identifier(this, AstNode.Type.None))

val AstNode.Type.Func.getGeneric get() = paramTypes

val AstNode.Type.getGenerics get(): List<AstNode.Type.GenericType> = when (this) {
    is AstNode.Type.GenericType -> listOf(this)
    is AstNode.Type.List -> this.elementType.getGenerics
    is AstNode.Type.Tuple -> this.elementTypes.flatMap { it.getGenerics }
    is AstNode.Type.Func -> this.paramTypes.flatMap { it.getGenerics } + returnType.getGenerics
    else -> listOf()
}

operator fun String.times (num: Int) = (0 until num).joinToString("") { this }
