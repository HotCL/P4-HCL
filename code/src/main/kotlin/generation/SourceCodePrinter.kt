package generation

import parser.AbstractSyntaxTree
import parser.AstNode.Command
import parser.AstNode.Type

/**
 * Outputs in the syntax of the source language, HCL.
 */
class SourceCodePrinter : IPrinter {
    override fun generateOutput(ast: AbstractSyntaxTree) = ast.children.format()

    private fun List<Command>.format(): String = "${joinToString("\n") { it.format() }}\n"

    private fun Command.format() = when (this) {
        is Command.Assignment -> format()
        is Command.Declaration -> format()
        is Command.Expression -> format()
        is Command.Return -> "return ${expression.format()}"
    }

    private fun Command.Assignment.format(): String = "${identifier.name} = ${expression.format()}"

    private fun Command.Declaration.format() =
            "${type.format()} ${identifier.name}${expression?.let{ " = ${it.format()}" } ?: ""}"

    private fun Command.Expression.format() = when (this) {
        is Command.Expression.Value.Identifier -> name
        is Command.Expression.Value.Literal.Number -> value.toString()
        is Command.Expression.Value.Literal.Text -> "\"$value\""
        is Command.Expression.Value.Literal.Bool -> value.toString().capitalize()
        is Command.Expression.Value.Literal.Tuple -> format()
        is Command.Expression.Value.Literal.List -> format()
        is Command.Expression.LambdaExpression -> format()
        is Command.Expression.FunctionCall -> format()
        is Command.Expression.LambdaBody -> this.commands.format()
    }

    private fun Command.Expression.Value.Literal.Tuple.format(): String =
            "(${elements.joinToString { it.format() }})"


    private fun Command.Expression.Value.Literal.List.format(): String =
            "[${elements.joinToString { it.format() }}]"

    private fun Command.Expression.LambdaExpression.format(): String =
            "(${paramDeclarations.joinToString { "${it.type.format()} ${it.identifier.name}" }}): " +
            "${returnType.format()} {\n${body.format()}}"

    private fun Command.Expression.FunctionCall.format(): String =
            "${arguments.firstOrNull()?.format()?.let { "$it " } ?: ""}${identifier.name}" +
                    arguments.drop(1).run { if (isEmpty()) "" else  " " +
                            joinToString(" ") { it.format() }
                    }

    private fun Type.format(): String = when(this){
        Type.Number -> "num"
        Type.Text -> "text"
        Type.Bool -> "bool"
        Type.None -> "none"
        Type.Func.ImplicitFunc -> "func"
        Type.Var -> throw Exception("Impossible")
        is Type.Func.ExplicitFunc -> "func[${if (!paramTypes.isEmpty())"${paramTypes.joinToString { it.format() }}, "
                                            else ""}${returnType.format()}]"
        is Type.GenericType -> name
        is Type.List -> "list[${elementType.format()}]"
        is Type.Tuple -> "tuple[${elementTypes.joinToString { it.format() }}]"
    }
}
