package generation

import parser.AbstractSyntaxTree
import parser.TreeNode

class GraphvizPrinter : IPrinter {
    override fun generateOutput(ast: AbstractSyntaxTree): String =
            "graph \"test\" {\n"+toLabel(0,"program")+ast.children.visit(0)+"}"

    private var lastId = 1
    private fun getNextId():Int{
        return lastId++
    }

    private fun toLabel(id: Int, label: String)= "$id;\n$id [label=\"$label\"];\n"
    private fun connectNodes(from: Int, to: Int)= "$to -- $from;\n"

    private fun List<TreeNode>.visit(parentId:Int): String {
        return this.joinToString("\n") { it.visit(parentId) }
    }

    private fun TreeNode.visit(parentId:Int): String {
        val id = getNextId()
        return "# this is: $id - ${this}\n"+when (this) {
            is TreeNode.Command.Assignment -> toLabel(id,this.identifier.name +"=")+
                    this.expression.visit(id)
            is TreeNode.Command.Declaration -> toLabel(id,"${this.identifier.name}=")+
                    this.type.visit(id)+if(this.expression != null) this.expression.visit(id) else ""
            is TreeNode.Command.Expression.LambdaExpression -> toLabel(id,"Lambda")+this.returnType.visit(id)+
                this.paramDeclarations.visit(id)+this.body.visit(id)

            is TreeNode.Command.Expression.Value.Literal.List -> toLabel(id,"Literal: List")+
                    this.elements.joinToString("\n") { it.visit(id) }

            is TreeNode.Command.Expression.Value.Literal.Tuple -> toLabel(id,"Literal: Tuple")+
                    this.elements.joinToString("\n") { it.visit(id) }

            is TreeNode.Command.Return -> toLabel(id,"return")+
                    this.expression.visit(id)
            is TreeNode.Type -> this.visitType(id)
            is TreeNode.Command.Expression.FunctionCall ->
                toLabel(id,"call: "+this.identifier.name) + this.parameters.joinToString("\n")
                {it.visit(id) }
            is TreeNode.ParameterDeclaration -> toLabel(id,"parameter")+
                    this.type.visit(id)+this.identifier.visit(id)
            else -> toLabel(id,this.toString())
        }+connectNodes(id,parentId)
    }

    private fun TreeNode.Type.visitType(id: Int): String = toLabel(id,this.getTypeName()) + when(this){
        is TreeNode.Type.List -> this.elementType.visit(id)
        is TreeNode.Type.Func.ExplicitFunc -> this.paramTypes.joinToString("\n") {it.visit(id) }
        is TreeNode.Type.Tuple -> this.elementTypes.joinToString("\n") {it.visit(id) }
        else -> toLabel(id,this.getTypeName())

    }

    private fun TreeNode.Type.getTypeName() : String = "TYPE:"+when(this){
        TreeNode.Type.Number -> "num"
        TreeNode.Type.Text -> "text"
        TreeNode.Type.Bool -> "bool"
        TreeNode.Type.None -> "none"
        is TreeNode.Type.GenericType -> "T"
        TreeNode.Type.Func.ImplicitFunc -> "func"
        is TreeNode.Type.Func.ExplicitFunc -> "func"
        else -> this::class.simpleName.toString()
    }
}
