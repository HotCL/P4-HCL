package parserTests

import com.natpryce.hamkrest.MatchResult
import com.natpryce.hamkrest.Matcher
import com.natpryce.hamkrest.assertion.assertThat
import generation.SourceCodePrinter
import lexer.* // ktlint-disable no-wildcard-imports
import org.junit.jupiter.api.Assertions
import parser.AbstractSyntaxTree
import parser.AstNode
import parser.ParserWithoutBuiltins
import kotlin.coroutines.experimental.buildSequence

fun matchesAstChildren(vararg expectedAstChildren: AstNode.Command): Matcher<List<Token>> =
        object : Matcher<List<Token>> {
            override fun invoke(actual: List<Token>): MatchResult {
                val actualAst = ParserWithoutBuiltins(DummyLexer(buildSequence { yieldAll(actual) })).generateAbstractSyntaxTree()
                val expectedAst = AbstractSyntaxTree(expectedAstChildren.toMutableList())
                return if (actualAst == expectedAst) MatchResult.Match
                else MatchResult.Mismatch("Expected AST equal to this:\n$expectedAst\n" +
                        "${SourceCodePrinter().generate(expectedAst)}\n" +
                        "But got this:\n$actualAst\n" +
                        "${SourceCodePrinter().generate(actualAst)}\n")
            }
            override val description: String get() = "was equal to the expected AST"
            override val negatedDescription: String get() = "was not equal to the expected AST"
        }

fun matchesAstWithActualLexer(expected: String): Matcher<String> =
        object : Matcher<String> {
            override fun invoke(actual: String): MatchResult {
                val actualAst = ParserWithoutBuiltins(Lexer(actual)).generateAbstractSyntaxTree()
                val actualAstString = SourceCodePrinter().generate(actualAst)
                return if (actualAstString == expected) MatchResult.Match
                else MatchResult.Mismatch("Expected AST equal to this:\n$expected\n" +
                        "But got this:\n$actualAstString\n")
            }
            override val description: String get() = "was equal to the expected AST"
            override val negatedDescription: String get() = "was not equal to the expected AST"
        }

class DummyLexer(private val tokens: Sequence<Token>) : ILexer {
    override fun getTokenSequence() = buildSequence {
        tokens.forEach {
            yield(PositionalToken(it, -1, -1)) }
    }

    constructor(tokens: List<Token>) : this(buildSequence { yieldAll(tokens) })

    override fun inputLine(lineNumber: Int) = "Dummy lexer does not implement input line"
}

infix fun String.becomes(expectedPrettyPrint: String) = assertThat(this, matchesAstWithActualLexer(expectedPrettyPrint))

fun parseExpectException(content: String) = Assertions.assertThrows(Exception::class.java) {
    ParserWithoutBuiltins(Lexer(content)).generateAbstractSyntaxTree()
}
