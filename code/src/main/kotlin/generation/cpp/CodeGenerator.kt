package generation.cpp

import com.sun.javafx.css.Declaration
import generation.IPrinter
import parser.AbstractSyntaxTree
import parser.AstNode

/**
 * Generates the "body" of the program with a setup and a loop method and all the logic of the program
 */
class CodeGenerator : IPrinter {
    var indents = 0
    override fun generate(ast: AbstractSyntaxTree): String { indents=-1; return ast.children.format() }

    private fun List<AstNode.Command>.format(): String { indents++; return joinToString("\n") { it.format()}.also{indents--} }

    private val newLine get() = "\n"+"   "*indents

    private fun AstNode.Command.format() =
            when(this) {
                is AstNode.Command.Declaration -> format()
                is AstNode.Command.Assignment -> TODO()
                is AstNode.Command.Expression.Value.Identifier -> TODO()
                is AstNode.Command.Expression.Value.Literal.Number -> "$value"
                is AstNode.Command.Expression.Value.Literal.Text -> "\"$value\""
                is AstNode.Command.Expression.Value.Literal.Bool -> "$value"
                is AstNode.Command.Expression.Value.Literal.Tuple -> TODO()
                is AstNode.Command.Expression.Value.Literal.List -> "[${elements.joinToString { format() }}]"
                is AstNode.Command.Expression.LambdaExpression -> TODO()
                is AstNode.Command.Expression.LambdaBody -> TODO()
                is AstNode.Command.Expression.FunctionCall -> TODO()
                is AstNode.Command.Return -> "return ${this.expression.format()};\n"
                is AstNode.Command.RawCpp -> content+"\n"
            }


    private fun AstNode.Command.Assignment.format(): String  {
        TODO()
    }
    private fun AstNode.Command.Declaration.format(): String =
            when (expression) {
                is AstNode.Command.Expression.LambdaExpression -> expression.formatAsDecl(this)
                else -> ""
            }


    private fun AstNode.Command.Expression.LambdaExpression.formatAsDecl(decl: AstNode.Command.Declaration): String {

        val generics = returnType.getGenerics + this.paramDeclarations.flatMap { it.type.getGenerics }

        return (if(generics.isEmpty()) "" else "template <"+generics.joinToString { "typename ${it.name}" }+">\n")+
                if(attributes.inLine){
                    "${returnType.cpp} ${decl.identifier.name} " +
                            "(${paramDeclarations.format(attributes.modifyParameterName)}) {\n" +
                            body.commands.format()+"}\n"
                }
                else{
                    TODO()
                }
    }

    private fun List<AstNode.ParameterDeclaration>.format(modifyParametersNames: Boolean) = joinToString {
        "${it.type.cpp} ${if(modifyParametersNames)it.identifier.cpp else it.identifier.name}"
    }

    private fun AstNode.Command.Expression.format(): String {
        TODO()
    }

    private fun AstNode.Command.Expression.Value.Literal.Tuple.format(): String {
        TODO()
    }

    private fun AstNode.Command.Expression.Value.Literal.List.format(): String {
        TODO()
    }

    private fun AstNode.Command.Expression.LambdaExpression.format(): String {
        TODO()
    }

    private fun AstNode.Command.Expression.FunctionCall.format(): String {
        TODO()
    }


    private fun AstNode.Type.format(): String {
        TODO()
    }

}