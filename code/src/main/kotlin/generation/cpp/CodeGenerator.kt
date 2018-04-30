package generation.cpp

import generation.IPrinter
import parser.AbstractSyntaxTree
import parser.AstNode

/**
 * Generates the "body" of the program with a setup and a loop method and all the logic of the program
 */
class CodeGenerator : IPrinter {
    override fun generate(ast: AbstractSyntaxTree): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun List<AstNode.Command>.format(): String {
        TODO()
    }

    private fun AstNode.Command.format(): String  {
        TODO()
    }

    private fun AstNode.Command.Assignment.format(): String  {
        TODO()
    }
    private fun AstNode.Command.Declaration.format(): String {
        TODO()
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