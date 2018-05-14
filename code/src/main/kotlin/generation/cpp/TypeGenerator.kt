package generation.cpp

import generation.IPrinter
import parser.AbstractSyntaxTree
import parser.AstNode
import parser.Parameter
import parser.buildFunction

/**
 * Should generate a file with all header information. for instance every tuple used should be type-defined
 */
class TypeGenerator : IPrinter {
    override fun generate(ast: AbstractSyntaxTree): String {
        val tuples = ast.children.fetchTuples()
        // val lists = ast.children.fetchLists()
        return tuples.joinToString("\n\n") { it.format() + "\n" + it.generateFunctions() }
    }

    private fun List<AstNode>.fetchTuples(): Set<AstNode.Type.Tuple> = flatMap { it.fetchTuple() }.toSet()

    private fun AstNode.fetchTuple(): Set<AstNode.Type.Tuple> = when (this) {
        is AstNode.Command.Expression.LambdaExpression -> body.fetchTuple()
        is AstNode.Command.Expression.FunctionCall -> arguments.fetchTuples()
        is AstNode.Command.Assignment -> expression.fetchTuple()
        is AstNode.Command.Declaration -> type.fetchTuple() + (this.expression?.fetchTuple() ?: emptySet())
    // types
        is AstNode.Type.List -> elementType.fetchTuple()
        is AstNode.Type.Func -> paramTypes.fetchTuples() + returnType.fetchTuple()
        is AstNode.Type.Tuple -> setOf(this)
        else -> emptySet()
    }
    private fun AstNode.Type.Tuple.format(): String = "" +
        "typedef struct {\n  " +
        elementTypes.mapIndexed { i, it ->
            "${it.cppName} element$i;" }.joinToString("\n  ") +
        "\n} ${this.cppName};\n\n" // +generateFunctions()

    private fun AstNode.Type.Tuple.generateFunctions() = CodeGenerator().generate(
        AbstractSyntaxTree(listOf(
            buildFunction(
                identifier = "toText",
                parameters = listOf(
                    Parameter("self", this)
                ),
                returnType = AstNode.Type.Text,
                body = "ConstList<char>::List" +
                    " output = ConstList<char>::string((char*)\"\");\n" +
                    elementTypes.mapIndexed { index, _ ->
                        "output = ConstList<char>::concat(output, ${"toText".cppName}" +
                            "(self.element$index));\n" +
                            if (index != this.elementTypes.count() - 1) {
                                "output = ConstList<char>::concat(output, " +
                                    "ConstList<char>::string((char*)\",\"));\n"
                            } else ""
                    }.joinToString("") +
                    "return output;"
            ),
            *this.elementTypes.mapIndexed { index, type -> buildFunction(
                identifier = "element$index",
                parameters = listOf(
                    Parameter("self", this)
                ),
                returnType = type,
                body = "return self.element$index;"

            ) }.toTypedArray(),
            buildFunction(
                identifier = "create_struct",
                parameters = this.elementTypes.mapIndexed { index, it -> Parameter("element$index", it) },
                returnType = this,
                body = "${this.cppName} output = {${this.elementTypes.mapIndexed{
                    index, _ -> "element$index" }.joinToString()}};\nreturn output;"
            )
        )))
}
