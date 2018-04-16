package generation

import parser.AbstractSyntaxTree
import parser.AstNode.Command
import parser.AstNode.Type

/**
 * Outputs in the syntax of the source language, HCL.
 */
class SourceCodePrinter : IPrinter {
    override fun generateOutput(ast: AbstractSyntaxTree): String = ast.children.visit()

    private fun List<Command>.visit(): String = "${this.joinToString("\n") { it.visit() }}\n"

    private fun Command.visit(): String = when (this) {
        is Command.Assignment -> this.visit()
        is Command.Declaration -> this.visit()
        is Command.Expression -> this.visit()
        is Command.Return -> "return ${this.expression.visit()}"
    }

    private fun Command.Assignment.visit(): String =
            "${this.identifier.name} = ${this.expression.visit()}"

    private fun Command.Declaration.visit(): String =
            "${this.type.visit()} ${this.identifier.name}"+
                    if (this.expression != null)
                        " = ${this.expression.visit()}"
                    else ""


    private fun Command.Expression.visit(): String = when(this){
        is Command.Expression.Value.Identifier -> this.name
        is Command.Expression.Value.Literal.Number -> this.value.toString()
        is Command.Expression.Value.Literal.Text -> "\"${this.value}\""
        is Command.Expression.Value.Literal.Bool -> this.value.toString().capitalize()
        is Command.Expression.Value.Literal.Tuple -> this.visit()
        is Command.Expression.Value.Literal.List -> this.visit()
        is Command.Expression.LambdaExpression -> this.visit()
        is Command.Expression.FunctionCall -> this.visit()
    }

    private fun Command.Expression.Value.Literal.Tuple.visit(): String =
            "(${this.elements.joinToString(",") { it.visit() }})"


    private fun Command.Expression.Value.Literal.List.visit(): String =
            "[${this.elements.joinToString(",") { it.visit() }}]"

    private fun Command.Expression.LambdaExpression.visit(): String =
            "(${this.paramDeclarations.joinToString(", ") { "${it.type.visit()} ${it.identifier.name}" }}):"+
                    " ${this.returnType.visit()} {\n${this.body.visit()}}"


    private fun Command.Expression.FunctionCall.visit(): String =
            if(arguments.count()==0) this.identifier.name
            else
            {
                "${this.arguments[0].visit()} ${this.identifier.name}" +
                        if(arguments.count()>1)
                            " ${this.arguments.subList(1,this.arguments.count()).
                                    joinToString(" " ) { it.visit() }}"
                        else ""

            }

    private fun Type.visit(): String = when(this){
        Type.Number -> "num"
        Type.Text -> "text"
        Type.Bool -> "bool"
        Type.None -> "none"
        Type.Var -> "var"
        is Type.GenericType -> "T"
        is Type.List -> "list[${this.elementType.visit()}]"
        is Type.Func.ExplicitFunc ->
            "func[${this.paramTypes.joinToString(", ") {it.visit() }}, ${this.returnType.visit()}]"
        Type.Func.ImplicitFunc -> "func"
        is Type.Tuple ->
            "tuple[${this.elementTypes.joinToString(", ") {it.visit() }}]"
    }


}
