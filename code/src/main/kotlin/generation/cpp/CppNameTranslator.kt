package generation.cpp

import generation.IValidNameTranslator
import parser.AstNode
import kotlin.math.absoluteValue


object CppNameTranslator : IValidNameTranslator{
    override fun getValidIdentifierName(node: AstNode.Command.Expression.Value.Identifier): String {
        return node.getValidName()

    }

    override fun getValidTypeName(node: AstNode.Type): String {
        return node.getValidName()
    }


    private fun AstNode.Type.getValidName():String = when(this){
        AstNode.Type.Number -> "float"
        AstNode.Type.Text -> "char[]"
        AstNode.Type.Bool -> "bool"
        AstNode.Type.None -> "void"
        is AstNode.Type.GenericType -> this.name
        is AstNode.Type.List -> "ConstList<${elementType.getValidName()}>"
        is AstNode.Type.Func.ExplicitFunc ->
            "std::function<${returnType.getValidName()}(${paramTypes.joinToString { getValidName() }})>"
        is AstNode.Type.Tuple -> "TUPLE"+this.elementTypes.joinToString ("_") { it.getValidName().hashCode().absoluteValue.toString(33)}
        else -> TODO("THIS SHOULDNT FUCKIN HAPPEN")
    }


    private fun AstNode.Command.Expression.Value.Identifier.getValidName():String =
            "HCL_0x"+name.hashCode().toString(16)

}