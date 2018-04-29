package generation.cpp

import generation.IValidNameTranslator
import parser.AstNode


class CppNameTranslator : IValidNameTranslator{
    override fun getValidIdentifierName(node: AstNode.Command.Expression.Value.Identifier): String {
        return node.getValidName()

    }

    override fun getValidTypeName(node: AstNode.Type): String {
        return node.getValidName()
    }


    private fun AstNode.Type.getValidName():String = when(this){
        AstNode.Type.Number -> TODO()
        AstNode.Type.Text -> TODO()
        AstNode.Type.Bool -> TODO()
        AstNode.Type.None -> TODO()
        AstNode.Type.Var -> TODO()
        is AstNode.Type.GenericType -> TODO()
        is AstNode.Type.List -> TODO()
        is AstNode.Type.Func.ExplicitFunc -> TODO()
        AstNode.Type.Func.ImplicitFunc -> TODO()
        is AstNode.Type.Tuple -> TODO()
    }


    private fun AstNode.Command.Expression.Value.Identifier.getValidName():String = TODO()

}