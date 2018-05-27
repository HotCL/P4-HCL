package builtinTests

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import parser.AstNode
import parser.Parser
import parserTests.DummyLexer
import kotlin.test.assertTrue

object TestAllBuiltinsExist : Spek({
    given("HCL Builtin functions") {
        val ast = Parser(DummyLexer(listOf())).generateAbstractSyntaxTree()
        val expectedBuiltinFunctions = listOf(
                "+",
                "-",
                "/",
                "*",
                "equals",
                "notEquals",
                "lessThan",
                "lessThanEqual",
                "greaterThan",
                "greaterThanEqual",
                "and",
                "or",
                "not",
                "toText",
                "then",
                "while",
                "forEach",
                "length",
                "at",
                "splitAt",
                "map",
                "where",
                "delay",
                "setDigitalPin",
                "readDigitalPin",
                "setAnalogPin",
                "readAnalogPin",
                "print"
                // "printLine"
        )
        expectedBuiltinFunctions.forEach { expectedBuiltinFunction ->
            it("should contain the function \"$expectedBuiltinFunction\"") {
                assertTrue {
                    ast.children.any {
                        (it as? AstNode.Command.Declaration)
                                ?.let { it.identifier.name == expectedBuiltinFunction }
                                ?: false
                    }
                }
            }
        }
    }
})
