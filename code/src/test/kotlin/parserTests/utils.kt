package parserTests

import com.natpryce.hamkrest.MatchResult
import com.natpryce.hamkrest.Matcher
import lexer.ILexer
import lexer.PositionalToken
import lexer.Token
import lexer.printTokens
import parser.AbstractSyntaxTree
import parser.Parser
import parser.TreeNode
import kotlin.coroutines.experimental.buildSequence

fun matchesAstChildren(vararg expectedAstChildren: TreeNode.Command): Matcher<List<Token>> =
        object : Matcher<List<Token>> {
            override fun invoke(actual: List<Token>): MatchResult {
                println("Test for code:\n" + printTokens(actual))
                val actualAst = Parser(DummyLexer(buildSequence{ yieldAll(actual) })).generateAbstractSyntaxTree()
                val expectedAst = AbstractSyntaxTree(expectedAstChildren.toMutableList())
                return if (actualAst == expectedAst) MatchResult.Match
                else MatchResult.Mismatch("Expected AST equal to this:\n${expectedAst}\nBut got this:\n${actualAst}")
            }
            override val description: String get() = "was equal to the expected AST"
            override val negatedDescription: String get() = "was not equal to the expected AST"
        }



class DummyLexer(private val tokens: Sequence<Token>): ILexer {
    override fun getTokenSequence() = buildSequence {
        tokens.forEach { yield(PositionalToken(it, -1, -1)) }
    }
    constructor(tokens: List<Token>) : this(buildSequence { yieldAll(tokens) })

    override fun inputLine(lineNumber: Int) = "Dummy lexer does not implement input line"
}
