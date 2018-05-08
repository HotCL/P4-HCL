package generation.cpp

import generation.IPrinter
import parser.AbstractSyntaxTree
import parser.AstNode
import parser.Parameter
import parser.buildFunction

/**
 * Should generate a file with all header information. for instance every tuple used should be type-defined
 */
class TypeGenerator: IPrinter {
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
                "${it.cpp} element$i;" }.joinToString("\n  ") +
            "\n} ${this.cpp};\n\n"+generateFunctions()


    private fun AstNode.Type.Tuple.generateFunctions() = CodeGenerator().generate(
            AbstractSyntaxTree(listOf(buildFunction(
                    identifier = "toText",
                    parameters = listOf(
                            Parameter("self", this)
                    ),
                    returnType = AstNode.Type.Text,
                    body = "auto output = ConstList<char>::string((char*)\"\");\n" +
                            this.elementTypes.mapIndexed {index, it ->
                                "output = ConstList<char>::concat(output, toText(self.element$index));\n"+
                                        if(index != this.elementTypes.count() - 1) {
                                            "output = ConstList<char>::concat(output, ConstList<char>::string((char*)\",\"));\n"
                                        } else ""

                            }.joinToString("")+
                            "return output;"
            ),buildFunction(
                    identifier = "at",
                    parameters = listOf(
                            Parameter("self", this),
                                    Parameter("index", AstNode.Type.Number)
                    ),
                    returnType = AstNode.Type.Text,
                    body = "switch(index) { \n"+
                            this.elementTypes.mapIndexed {index, it ->
                                "case $index: return self.element$index;"

                            }.joinToString("\n")+"\n}"
            )
            )))


}
