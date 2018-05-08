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
                "<",
                ">",
                "and",
                "or",
                "not",
                "toText",
                "then",
                "while",
                "length",
                "at",
                "subList",
                "map",
                "filter",
                "delayMillis",
                "HIGH",
                "LOW",
                "readDigital",
                "writeAnalog",
                "readAnalog",
                "print",
                "printLine"
        )
        expectedBuiltinFunctions.forEach { expectedBuiltinFunction ->
            it("should contain the function \"$expectedBuiltinFunction\"") {
                assertTrue {
                    ast.children.any {
                        (it as? AstNode.Command.Declaration)?.let { it.identifier.name == expectedBuiltinFunction }
                                                                 ?: false
                    }
                }
            }
        }
    }
})
