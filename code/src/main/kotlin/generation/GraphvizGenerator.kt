package generation

import parser.AbstractSyntaxTree
import parser.AstNode
import parser.AstNode.Command
import parser.AstNode.Type

/**
 * Printer for the Graphviz visualization tool.
 * https://graphviz.gitlab.io/
 */

class GraphvizGenerator : IPrinter {
    override fun generate(ast: AbstractSyntaxTree): String =
            "graph \"test\" {\n" + toLabel(0, "program") + ast.children.visit(0) + "}"

    private var lastId = 1
    private fun getNextId(): Int {
        return lastId++
    }

    private fun toLabel(id: Int, label: String) = "$id;\n$id [label=\"$label\"];\n"
    private fun connectNodes(from: Int, to: Int) = "$to -- $from;\n"

    private fun List<AstNode>.visit(parentId: Int): String {
        return this.joinToString("\n") { it.visit(parentId) }
    }

    private fun AstNode.visit(parentId: Int): String {
        val id = getNextId()
        return "# this is: $id - ${this}\n" + when (this) {
            is Command.Assignment -> toLabel(id, this.identifier.name + "=") +
                    this.expression.visit(id)

            is Command.Declaration -> toLabel(id, "${this.identifier.name}=") +
                    this.type.visit(id) + if (this.expression != null) this.expression.visit(id) else ""

            is Command.Expression.LambdaExpression -> toLabel(id, "Lambda") + this.returnType.visit(id) +
                this.paramDeclarations.visit(id) + this.body.visit(id)

            is Command.Expression.Value.Identifier -> toLabel(id, "Identifier(name=$name)")

            is Command.Expression.Value.Literal.List -> toLabel(id, "Literal: List") +
                    this.elements.joinToString("\n") { it.visit(id) }

            is Command.Expression.Value.Literal.Tuple -> toLabel(id, "Literal: Tuple") +
                    this.elements.joinToString("\n") { it.visit(id) }
            is Command.Expression.LambdaBody -> toLabel(id, "body") + this.commands.visit(id)
            is Command.Return -> toLabel(id, "return") +
                    this.expression.visit(id)

            is Type -> this.visitType(id)

            is Command.Expression.FunctionCall ->
                toLabel(id, "call: " + this.identifier.name) + this.arguments.joinToString("\n") { it.visit(id) }

            is AstNode.ParameterDeclaration -> toLabel(id, "parameter") +
                    this.type.visit(id) + this.identifier.visit(id)

            else -> toLabel(id, this.toString())
        } + connectNodes(id, parentId)
    }

    private fun Type.visitType(id: Int): String = toLabel(id, this.getTypeName()) + when (this) {
        is Type.List -> this.elementType.visit(id)
        is Type.Func -> this.paramTypes.joinToString("\n") { it.visit(id) }
        is Type.Tuple -> this.elementTypes.joinToString("\n") { it.visit(id) }
        else -> toLabel(id, this.getTypeName())
    }

    private fun Type.getTypeName(): String = "TYPE:" + when (this) {
        Type.Number -> "num"
        Type.Text -> "text"
        Type.Bool -> "bool"
        Type.None -> "none"
        is Type.GenericType -> "T"
        is Type.Func -> "func"
        else -> this::class.simpleName.toString()
    }
}
