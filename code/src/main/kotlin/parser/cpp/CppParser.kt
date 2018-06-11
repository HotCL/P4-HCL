package parser.cpp

import builtins.CppBuiltinFunctions
import lexer.ILexer
import parser.AbstractSyntaxTree
import parser.AstNode
import parser.Parser

class CppParser(lexer: ILexer) : Parser(lexer) {
    fun cppAst() = AbstractSyntaxTree().apply {
        // Add builtin functions
        CppBuiltinFunctions.functions.forEach {
            children.add(it)
            enterSymbol(it.identifier.name, it.expression!!.type)
        }
        enterSymbol("RETURN_CODE", AstNode.Type.Number)

        enterSymbol("+", AstNode.Type.Func(listOf(AstNode.Type.Text, AstNode.Type.Text),
                AstNode.Type.Text))

        enterSymbol("loop", AstNode.Type.Func(listOf(AstNode.Type.Func(listOf(), AstNode.Type.None)), AstNode.Type.None))

        children.addAll(commandSequence().toList())
    }
}
