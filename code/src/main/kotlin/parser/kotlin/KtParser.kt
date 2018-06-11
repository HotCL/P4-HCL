package parser.kotlin

import builtins.KotlinInterpreterFunctions
import lexer.ILexer
import parser.AstIdentifier
import parser.AstNode
import parser.Parser
import kotlin.coroutines.experimental.buildSequence

class KtParser(lexer: ILexer) : Parser(lexer) {
    override fun commandSequence() = buildSequence {
        KotlinInterpreterFunctions.functions.forEach {
            yield(it)
            enterSymbol(it.identifier.name, it.expression!!.type)
        }
        yield(AstNode.Command.Declaration(
                AstNode.Type.Number,
                AstIdentifier("RETURN_CODE", AstNode.Type.Number),
                AstNode.Command.Expression.Value.Literal.Number(0.0)
                ))
        enterSymbol("RETURN_CODE", AstNode.Type.Number)
        yieldAll(super.commandSequence())
    }
}