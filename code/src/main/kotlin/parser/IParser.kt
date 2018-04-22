package parser

import lexer.ILexer

interface IParser {
    fun generateAbstractSyntaxTree(): AbstractSyntaxTree
}
