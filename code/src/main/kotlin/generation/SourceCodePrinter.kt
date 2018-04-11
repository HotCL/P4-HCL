package generation

import parser.AbstractSyntaxTree
import parser.TreeNode

class SourceCodePrinter : IPrinter {
    override fun generateOutput(ast: AbstractSyntaxTree): String = ast.children.visit()

    private fun List<TreeNode.Command>.visit(): String = "${this.joinToString("\n") { it.visit() }}\n"

    private fun TreeNode.Command.visit(): String = when (this) {
        is TreeNode.Command.Assignment -> this.visit()
        is TreeNode.Command.Declaration -> this.visit()
        is TreeNode.Command.Expression -> this.visit()
        is TreeNode.Command.Return -> "return ${this.expression.visit()}"
    }

    private fun TreeNode.Command.Assignment.visit(): String =
            "${this.identifier.name} = ${this.expression.visit()}"

    private fun TreeNode.Command.Declaration.visit(): String =
            "${this.type.visit()} ${this.identifier.name}"+
                    if (this.expression != null)
                        " = ${this.expression.visit()}"
                    else ""


    private fun TreeNode.Command.Expression.visit(): String = when(this){
        is TreeNode.Command.Expression.Value.Identifier -> this.name
        is TreeNode.Command.Expression.Value.Literal.Number -> this.value.toString()
        is TreeNode.Command.Expression.Value.Literal.Text -> "\"${this.value}\""
        is TreeNode.Command.Expression.Value.Literal.Bool -> this.value.toString().capitalize()
        is TreeNode.Command.Expression.Value.Literal.Tuple -> this.visit()
        is TreeNode.Command.Expression.Value.Literal.List -> this.visit()
        is TreeNode.Command.Expression.LambdaExpression -> this.visit()
        is TreeNode.Command.Expression.FunctionCall -> this.visit()
    }

    private fun TreeNode.Command.Expression.Value.Literal.Tuple.visit(): String =
            "(${this.elements.joinToString(",") { it.visit() }})"


    private fun TreeNode.Command.Expression.Value.Literal.List.visit(): String =
            "[${this.elements.joinToString(",") { it.visit() }}]"

    private fun TreeNode.Command.Expression.LambdaExpression.visit(): String =
            "(${this.paramDeclarations.joinToString(", ") { "${it.type.visit()} ${it.identifier.name}" }}):"+
                    " ${this.returnType.visit()} {\n${this.body.visit()}}"


    private fun TreeNode.Command.Expression.FunctionCall.visit(): String =
            if(parameters.count()==0) this.identifier.name
            else
            {
                "${this.parameters[0].visit()} ${this.identifier.name}" +
                        if(parameters.count()>1)
                            " ${this.parameters.subList(1,this.parameters.count()).
                                    joinToString(" " ) { it.visit() }}"
                        else ""

            }

    private fun TreeNode.Type.visit(): String = when(this){
        TreeNode.Type.Number -> "num"
        TreeNode.Type.Text -> "text"
        TreeNode.Type.Bool -> "bool"
        TreeNode.Type.None -> "none"
        is TreeNode.Type.GenericType -> "T"
        is TreeNode.Type.List -> "list[${this.elementType.visit()}]"
        is TreeNode.Type.Func.ExplicitFunc ->
            "func[${this.paramTypes.joinToString(", ") {it.visit() }}, ${this.returnType.visit()}]"
        TreeNode.Type.Func.ImplicitFunc -> "func"
        is TreeNode.Type.Tuple ->
            "tuple[${this.elementTypes.joinToString(", ") {it.visit() }}]"
    }


}
