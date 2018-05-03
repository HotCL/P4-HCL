package generation.cpp

import generation.IPrinter
import parser.AbstractSyntaxTree
import parser.AstNode

class MainGenerator : IPrinter {
    override fun generate(ast: AbstractSyntaxTree): String {
        return ast.genFromFilter { it.isDecl } +
                "\n\n/*SECOND PART*/\n\n" +
                ast.genFromFilter { !it.isLoop && !it.isDecl} +
                "\n\n/*LAST PART*/\n\n" +
                ast.genFromFilter { it.isLoop }
    }

    private val AstNode.Command.isLoop get() = this is AstNode.Command.Expression.FunctionCall && identifier.name == "loop"
    private val AstNode.Command.isDecl get() = this is AstNode.Command.Declaration
}

