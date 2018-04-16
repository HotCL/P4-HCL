package generation

import parser.AbstractSyntaxTree
import parser.TreeNode
import parser.TreeNode.Command
import parser.TreeNode.Type

/**
 * Printer for the Graphviz visualization tool.
 * https://graphviz.gitlab.io/
 */

class GraphvizPrinter : IPrinter {
    override fun generateOutput(ast: AbstractSyntaxTree) =
            "graph \"test\" {\n"+toLabel(0,"program")+ast.children.format(0)+"}"

    private var lastId = 1
    private fun getNextId():Int{
        return lastId++
    }

    private fun toLabel(id: Int, label: String)= "$id;\n$id [label=\"$label\"];\n"
    private fun connectNodes(from: Int, to: Int)= "$to -- $from;\n"

    private fun List<TreeNode>.format(parentId:Int) = this.joinToString("\n") { it.format(parentId) }

    private fun TreeNode.format(parentId:Int): String {
        val id = getNextId()
        return "# this is: $id - ${this}\n"+when (this) {
            is Command.Assignment -> toLabel(id,"=")+this.identifier.format(id)+
                    this.expression.format(id)

            is Command.Declaration -> toLabel(id,"=")+this.identifier.format(id)+
                    this.type.format(id)+if(this.expression != null) this.expression.format(id) else ""

            is Command.Expression.LambdaExpression -> toLabel(id,"Lambda")+this.returnType.format(id)+
                this.paramDeclarations.format(id)+this.body.format(id)

            is Command.Expression.Value.Literal.List -> toLabel(id,"Literal: List")+
                    this.elements.joinToString("\n") { it.format(id) }

            is Command.Expression.Value.Literal.Tuple -> toLabel(id,"Literal: Tuple")+
                    this.elements.joinToString("\n") { it.format(id) }

            is Command.Return -> toLabel(id,"return")+
                    this.expression.format(id)

            is Type -> this.visitType(id)

            is Command.Expression.FunctionCall ->toLabel(id,"call")+this.identifier.format(id)+
                    this.parameters.joinToString("\n") {it.format(id) }

            is TreeNode.ParameterDeclaration -> toLabel(id,"parameter")+
                    this.type.format(id)+this.identifier.format(id)

            else -> toLabel(id,this.toString())
        }+connectNodes(id,parentId)
    }

    private fun Type.visitType(id: Int) = toLabel(id,this.getTypeName()) + when(this){
        is Type.List -> this.elementType.format(id)
        is Type.Func.ExplicitFunc -> this.paramTypes.joinToString("\n") {it.format(id) }
        is Type.Tuple -> this.elementTypes.joinToString("\n") {it.format(id) }
        else -> toLabel(id,this.getTypeName())

    }

    private fun Type.getTypeName() = "TYPE:"+when(this){
        Type.Number -> "num"
        Type.Text -> "text"
        Type.Bool -> "bool"
        Type.None -> "none"
        is Type.GenericType -> "T"
        Type.Func.ImplicitFunc -> "func"
        is Type.Func.ExplicitFunc -> "func"
        else -> this::class.simpleName.toString()
    }
}
