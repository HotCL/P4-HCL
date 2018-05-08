package generation.cpp

import generation.IValidNameTranslator
import generation.cpp.CppNameTranslator.getValidName
import parser.AstNode


object CppNameTranslator : IValidNameTranslator{
    override fun getValidIdentifierName(node: AstNode.Command.Expression.Value.Identifier) = node.getValidName()
    override fun getValidTypeName(node: AstNode.Type) = node.getValidName()

    private fun AstNode.Type.getValidName():String = when(this){
        AstNode.Type.Number -> "double"
        AstNode.Type.Text ->  "ConstList<char>"
        AstNode.Type.Bool -> "bool"
        AstNode.Type.None -> "void"
        is AstNode.Type.GenericType -> this.name
        is AstNode.Type.List -> "ConstList<${elementType.getValidName()}>"
        is AstNode.Type.Func.ExplicitFunc ->
            "function<${returnType.getValidName()}(${paramTypes.joinToString { it.getValidName() }})>"
        is AstNode.Type.Tuple -> "TPL_0x" + this.elementTypes.joinToString ("_") { it.getValidName() }.hashed
        else -> TODO("THIS SHOULDN'T EVER HAPPEN")
    }

    private fun AstNode.Command.Expression.Value.Identifier.getValidName() = "IDT_0x" + name.hashed
    private val String.hashed get () = Integer.toHexString(this.hashCode())
}
