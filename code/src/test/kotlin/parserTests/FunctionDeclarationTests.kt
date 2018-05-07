package parserTests

import com.natpryce.hamkrest.assertion.assertThat
import exceptions.*
import hclTestFramework.lexer.buildTokenSequence
import hclTestFramework.parser.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import parser.AstNode
import parser.ParserWithoutBuiltins


class FunctionDeclarationTests {

    @Test
    fun canDeclareFunction() {
        assertThat(
            buildTokenSequence {
                    func.squareStart.number.`,`.text.squareEnd.identifier("myFunc").`=`.`(`.number.
                    identifier("myParam1").`)`.colon.text.`{`.text("hey").`}`.newLine
            },
            matchesAstChildren(
                "myFunc" declaredAs func(txt, num) withValue (
                    lambda() returning txt withArgument ("myParam1" asType num) andBody ret(txt("hey"))
                )
            )
        )
    }

    @Test
    fun failOnTypesNotMatchingWithExpression() {
        val lexer = DummyLexer(buildTokenSequence {
            func.squareStart.number.`,`.text.squareEnd.identifier("myFunc").`=`.`(`.text.identifier("myParam1").`)`.
            colon.text.`{`.text("thomas").`}`.newLine
        })
        assertThrows(UnexpectedTypeError::class.java) { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() }
    }

    @Test
    fun failOnReturnTypeNotMatchingWithExpression() {
        val lexer = DummyLexer(buildTokenSequence {
            func.squareStart.number.`,`.text.squareEnd.identifier("myFunc").`=`.`(`.number.identifier("myParam1").`)`.
            colon.number.`{`.number(3.0).`}`.newLine
        })
        assertThrows(UnexpectedTypeError::class.java) { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() }
    }

    @Test
    fun failOnFuncSetToNonFunc() {
        val lexer = DummyLexer(buildTokenSequence {
            func.squareStart.number.`,`.text.squareEnd.identifier("myFunc").`=`.number(5.0).newLine
        })
        assertThrows(UnexpectedTypeError::class.java) { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() }
    }


    @Test
    fun failOnUndeclaredFunction() {
        val lexer = DummyLexer(buildTokenSequence {
            func.squareStart.number.`,`.text.squareEnd.identifier("toString").newLine
        })
        assertThrows(Exception::class.java) { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() }
    }


    @org.junit.jupiter.api.Test
    fun failsOnLackingSeperator() {
        val lexer = DummyLexer(buildTokenSequence {
            func.squareStart.number.text.squareEnd.identifier("myFunc").newLine
        })
        assertThrows(WrongTokenTypeError::class.java) { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() }
    }

    @org.junit.jupiter.api.Test
    fun failsOnEmptyTypeSet() {
        val lexer = DummyLexer(buildTokenSequence {
                func.squareStart.squareEnd.identifier("myFunc")
                //should fail before this token
        })
        assertThrows(UnexpectedTokenError::class.java) { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() }
    }

    @org.junit.jupiter.api.Test
    fun parsesWithSingleTypeParameter() {
        assertThat(
            buildTokenSequence {
                func.squareStart.text.squareEnd.identifier("myFunc").`=`.`(`.`)`.colon.text.`{`.text("wee").`}`.newLine
            },
            matchesAstChildren(
                "myFunc" declaredAs func(txt) withValue (lambda() returning txt withBody ret(txt("wee")))
            )
        )
    }
/*
    @Test
    fun failsOnAssignmentInLambdaParameterDefinition(){
        val lexer = DummyLexer(buildTokenSequence {
                Type.List,
                squareStart,
                number,
                squareEnd,
                identifier("ret+2"),
                `=`,
                squareStart,
                number(),
                squareEnd,
                newLine,
                Type.List,
                squareStart,
                Type.List,
                squareStart,
                number,
                squareEnd,
                squareEnd,
                Identifier,
                `=`,
                squareStart,
                Identifier,
                squareEnd,
                newLine
        ))
        Assertions.assertThrows(InitializedFunctionParameterError::class.java,
                { Parser(lexer).generateAbstractSyntaxTree() })

    }*/

    @org.junit.jupiter.api.Test
    fun parsesWithImplicitFuncType() {
        assertThat(
            buildTokenSequence {
            func.identifier("myFunc").`=`.`(`.`)`.colon.none.`{`.`}`.newLine
            },
            matchesAstChildren("myFunc" declaredAs func(none) withValue body().asExpression)
        )
    }

    @Test
    fun failsOnTooManySeparators() {
        val lexer = DummyLexer(buildTokenSequence {
            func.squareStart.number.`,`.`,`.text.squareEnd.identifier("myFunc").newLine
        })
        Assertions.assertThrows(UnexpectedTokenError::class.java) { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() }
    }
    //endregion FuncTypeDcl

    //region LambdaExpression
    @org.junit.jupiter.api.Test
    fun parsesFunctionWithTwoLambdaParameters() {
        assertThat(
            buildTokenSequence {
                func.identifier("myFunc").`=`.`(`.number.identifier("myParam1").`,`.text.identifier("myParam2").`)`.
                colon.none.`{`.`}`.newLine
            },
            matchesAstChildren(
                "myFunc" declaredAs func(none, listOf(num, txt)) withValue (
                    lambda() withArguments listOf("myParam1" asType num, "myParam2" asType txt)
                )
            )
        )
    }

    @org.junit.jupiter.api.Test
    fun parsesWithFuncParamAndLambdaParam() {
        assertThat(
            buildTokenSequence {
                func.squareStart.number.`,`.none.squareEnd.identifier("myFunc").`=`.`(`.number.
                identifier("myParam1").`)`.colon.none.`{`.`}`.newLine
            },
            matchesAstChildren(
                "myFunc" declaredAs func(none, num) withValue (lambda() withArgument ("myParam1" asType num))
            )
        )
    }

    @Test
    fun failsOnAssignmentInLambdaParameterDefinition(){
        val lexer = DummyLexer(buildTokenSequence {
            func.identifier("myFunc").`=`.`(`.number.identifier("myParam1").`=`.number(5.0).`)`.colon.none.newLine
        })
        Assertions.assertThrows(InitializedFunctionParameterError::class.java,
                { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() })

    }

    @Test
    fun canDeclareFunctionWithFunctionAsParameter() {
        assertThat(
            buildTokenSequence {
                func.squareStart.func.squareStart.text.squareEnd.`,`.text.squareEnd.identifier("myFunc").`=`.`(`.
                func.squareStart.text.squareEnd.identifier("myParam").`)`.colon.text.`{`.text("HEY").`}`.newLine
            },
            matchesAstChildren(
                "myFunc" declaredAs func(txt, func(txt)) withValue (
                    lambda() returning txt withArgument ("myParam" asType func(txt)) withBody ret(txt("HEY"))
                )
            )
        )
    }

    @Test
    fun failsOnOverloadingWithDifferentAmountOfParameters(){
        val lexer = DummyLexer(buildTokenSequence {
            func.identifier("myFunc").`=`.`(`.number.identifier("myParam1").`)`.colon.none.`{`.`}`.newLine.
            func.identifier("myFunc").`=`.`(`.number.identifier("myParam1").`,`.number.identifier("myParam1").`)`.colon.
            none.`{`.`}`.newLine
        })

        Assertions.assertThrows(OverloadWithDifferentAmountOfArgumentsException::class.java,
                { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() })
    }

    @Test
    fun failsOnOverloadingWithSameParameters(){
        val lexer = DummyLexer(buildTokenSequence {
            func.identifier("myFunc").`=`.`(`.number.identifier("myParam1").`,`.number.identifier("myParam1").`)`.colon.
            number.`{`.number(5.0).`}`.newLine.func.identifier("myFunc").`=`.`(`.number.identifier("myParam1").`,`.
            number.identifier("myParam1").`)`.colon.bool.`{`.bool(true).`}`.newLine
        })

        Assertions.assertThrows(AlreadyDeclaredException::class.java,
                { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() })

    }

    @org.junit.jupiter.api.Test
    fun parsesFuncWithBody() {
        assertThat(
            buildTokenSequence {
                func.identifier("myFunc").`=`.`(`.`)`.colon.none.newLine.`{`.number.identifier("myNum").newLine.
                text.identifier("myText").newLine.`}`.newLine
            },
            matchesAstChildren(
                "myFunc" declaredAs func(none) withValue
                        body("myNum" declaredAs num, "myText" declaredAs txt).asExpression
            )
        )
    }
}
