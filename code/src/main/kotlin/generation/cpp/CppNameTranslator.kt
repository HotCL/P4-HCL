package generation.cpp

import generation.IValidNameTranslator
import parser.AstNode

object CppNameTranslator : IValidNameTranslator {
    override fun getValidIdentifierName(node: AstNode.Command.Expression.Value.Identifier) = node.getValidName()
    override fun getValidIdentifierName(node: AstNode.Command.Expression) = node.getValidName()
    override fun getValidTypeName(node: AstNode.Type) = node.getValidName()
    override fun getValidtListLiteralName(node: AstNode.Command.Expression.Value.Literal.List) =
            "LST_IT_0x${node.getValidName()}"

    private fun AstNode.Type.getValidName(): String = when (this) {
        AstNode.Type.Number -> "double"
        AstNode.Type.Text -> "List<char>"
        AstNode.Type.Bool -> "bool"
        AstNode.Type.None -> "void"
        is AstNode.Type.GenericType -> this.name
        is AstNode.Type.List -> "List<${elementType.getValidName()}>"
        is AstNode.Type.Func ->
            "function<${returnType.getValidName()}(${paramTypes.joinToString { it.getValidName() }})>"
        is AstNode.Type.Tuple -> "TPL_0x" + this.elementTypes.joinToString ("_") { it.getValidName() }.hashed
    }

    private fun AstNode.Command.Expression.getValidName(): String = "EX_"+when (this) {

        is AstNode.Command.Expression.Value.Identifier -> getValidName()
        is AstNode.Command.Expression.Value.Literal.Number -> value.toString().hashed
        is AstNode.Command.Expression.Value.Literal.Text -> value.hashed
        is AstNode.Command.Expression.Value.Literal.Bool -> value.toString().hashed
        is AstNode.Command.Expression.Value.Literal.Tuple ->
            elements.joinToString("_") { it.getValidName() }.hashed
        is AstNode.Command.Expression.Value.Literal.List ->
            elements.joinToString("_") { it.getValidName() }.hashed
        is AstNode.Command.Expression.LambdaExpression -> (body.getValidName() + returnType.getValidName()).hashed
        is AstNode.Command.Expression.LambdaBody -> commands.joinToString { it.toString() }.hashed
        is AstNode.Command.Expression.FunctionCall ->
            identifier.getValidName()+"_"+arguments.joinToString("_") { it.getValidName() }.hashed
    }

    private fun AstNode.Command.Expression.Value.Identifier.getValidName() = "IDT_0x" + name.hashed
    private val String.hashed get() = Integer.toHexString(this.hashCode())
}
