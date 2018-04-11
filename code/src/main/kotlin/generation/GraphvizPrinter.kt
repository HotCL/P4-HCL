package generation

import parser.AbstractSyntaxTree
import parser.TreeNode
import parser.TreeNode.Command
import parser.TreeNode.Type

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
            is Command.Assignment -> toLabel(id,this.identifier.name +"=")+
                    this.expression.visit(id)
            is Command.Declaration -> toLabel(id,"${this.identifier.name}=")+
                    this.type.visit(id)+if(this.expression != null) this.expression.visit(id) else ""
            is Command.Expression.LambdaExpression -> toLabel(id,"Lambda")+this.returnType.visit(id)+
                this.paramDeclarations.visit(id)+this.body.visit(id)

            is Command.Expression.Value.Literal.List -> toLabel(id,"Literal: List")+
                    this.elements.joinToString("\n") { it.visit(id) }

            is Command.Expression.Value.Literal.Tuple -> toLabel(id,"Literal: Tuple")+
                    this.elements.joinToString("\n") { it.visit(id) }

            is Command.Return -> toLabel(id,"return")+
                    this.expression.visit(id)
            is Type -> this.visitType(id)
            is Command.Expression.FunctionCall ->
                toLabel(id,"call: "+this.identifier.name) + this.parameters.joinToString("\n")
                {it.visit(id) }
            is TreeNode.ParameterDeclaration -> toLabel(id,"parameter")+
                    this.type.visit(id)+this.identifier.visit(id)
            else -> toLabel(id,this.toString())
        }+connectNodes(id,parentId)
    }

    private fun Type.visitType(id: Int): String = toLabel(id,this.getTypeName()) + when(this){
        is Type.List -> this.elementType.visit(id)
        is Type.Func.ExplicitFunc -> this.paramTypes.joinToString("\n") {it.visit(id) }
        is Type.Tuple -> this.elementTypes.joinToString("\n") {it.visit(id) }
        else -> toLabel(id,this.getTypeName())

    }

    private fun Type.getTypeName() : String = "TYPE:"+when(this){
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
