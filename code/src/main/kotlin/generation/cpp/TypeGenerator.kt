package generation.cpp

import generation.IPrinter
import parser.AbstractSyntaxTree
import parser.AstNode

/**
 * Should generate a file with all header information. for instance every tuple used should be type-defined
 */
class TypeGenerator: IPrinter{
    override fun generate(ast: AbstractSyntaxTree): String {
        val tuples = ast.children.fetch()
        return tuples.joinToString("\n\n") { it.format() }
    }

    private fun List<AstNode>.fetch():Set<AstNode.Type.Tuple> = flatMap { it.fetch() }.toSet()

    private fun AstNode.fetch():Set<AstNode.Type.Tuple> = when(this){
        is AstNode.Command.Expression.LambdaExpression -> body.fetch()
        is AstNode.Command.Expression.FunctionCall -> arguments.fetch()
        is AstNode.Command.Assignment -> expression.fetch()
        is AstNode.Command.Declaration -> type.fetch() + (this.expression?.fetch() ?: emptySet())
    //types
        is AstNode.Type.List -> elementType.fetch()
        is AstNode.Type.Func.ExplicitFunc -> paramTypes.fetch() + returnType.fetch()
        is AstNode.Type.Tuple -> setOf(this)
        else -> emptySet()
    }
    private fun AstNode.Type.Tuple.format():String = ""+
            "typedef struct {\n  "+
            elementTypes.mapIndexed {i, it ->
                "${CppNameTranslator.getValidTypeName(it)} element$i;" }.joinToString("\n  ") +
            "\n} ${CppNameTranslator.getValidTypeName(this)};"
}