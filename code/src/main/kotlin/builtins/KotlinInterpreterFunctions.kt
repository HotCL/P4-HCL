package builtins

import interpreter.kotlin.* // ktlint-disable no-wildcard-imports
import parser.* // ktlint-disable no-wildcard-imports

/**
 * This object is responsible for adding all built-in function declarations to the AST
 * Only essential functions that cannot be defined in pure HCL, or that will have too
 * much overhead in HCL are defined here
 */
object KotlinInterpreterFunctions {
    val functions =
    // Operators
            listOf(
                    buildOperatorNumNumToNum("+") { it -> plus(it) },
                    buildOperatorNumNumToNum("-") { it -> minus(it) },
                    buildOperatorNumNumToNum("*") { it -> times(it) },
                    buildOperatorNumNumToNum("/") { it -> div(it) },
                    buildOperatorNumNumToNum("mod") { it -> (toInt() % it.toInt()).toDouble() },

                    buildOperatorBoolBoolToBool("and") { it -> and(it) },
                    buildOperatorBoolBoolToBool("or") { it -> or(it) },
                    buildOperatorBoolBoolToBool("equals") { it -> equals(it) },
                    buildOperatorBoolBoolToBool("notEquals") { it -> !equals(it) },
                    buildOperatorNumNumToBool("greaterThanEqual") { it -> this >= it },
                    buildOperatorNumNumToBool("lessThanEqual") { it -> this <= it },
                    buildOperatorNumNumToBool("greaterThan") { it -> this > it },
                    buildOperatorNumNumToBool("lessThan") { it -> this < it },
                    buildOperatorNumNumToBool("equals") { it -> this == it },
                    buildOperatorNumNumToBool("notEquals") { it -> this != it },

                    buildNotFunction(),
                    // Control structures
                    buildThenFunction(),
                    // buildElseTernaryFunction(),
                    buildWhileFunction(),
                    buildEachFunction(),
                    // Standard functions
                    buildNumberToTextFunction(),
                    buildBoolToTextFunction(),
                    buildListToTextFunction(),
                    buildTextToTextFunction(),
                    buildGetListLengthFunction(),
                    buildListEqualsFunction(),
                    buildListNotEqualsFunction(),
                    buildAtListFunction(),
                    buildSubListFunction(),
                    buildListConcatFunction(),
                    buildMapFunction(),
                    buildFilterFunction(),
                    buildAtTextFunction(),
                    buildSubtextText(),
                    buildLengthText(),
                    buildToListFunction(),

                    buildPrintFunctionText(),
                    buildPrintFunctionList(),
                    buildPrintFunction(),

                    buildGenericsEquals(),
                    buildThenElseFunction(),
                    buildThenElseFunction2(),

                    buildTextConcat(),
                    buildTextEquals(),
                    buildTextNotEquals()
            )
}

fun buildOperatorNumNumToNum(identifier: String, func: Double.(Double) -> Double) =
buildKotlinFunction(
        identifier = identifier,
        parameters = listOf(Parameter("KT_l", AstNode.Type.Number), Parameter("KT_h", AstNode.Type.Number)),
        returnType = AstNode.Type.Number,
        body = {
            val (l, h) = it.map { it as KotlinNumber }
            KotlinNumber(func(l.value, h.value))
        }
)

fun buildOperatorBoolBoolToBool(identifier: String, func: Boolean.(Boolean) -> Boolean) =
        buildKotlinFunction(
                identifier = identifier,
                parameters = listOf(Parameter("KT_l", AstNode.Type.Bool), Parameter("KT_h", AstNode.Type.Bool)),
                returnType = AstNode.Type.Bool,
                body = {
                    val (l, h) = it.map { it as KotlinBoolean }
                    KotlinBoolean(func(l.value, h.value))
                }
        )

fun buildOperatorNumNumToBool(identifier: String, func: Double.(Double) -> Boolean) =
        buildKotlinFunction(
                identifier = identifier,
                parameters = listOf(Parameter("KT_l", AstNode.Type.Number), Parameter("KT_h", AstNode.Type.Number)),
                returnType = AstNode.Type.Bool,
                body = {
                    val (l, h) = it.map { it as KotlinNumber }
                    KotlinBoolean(func(l.value, h.value))
                }
        )

private fun buildNotFunction() = buildKotlinFunction(
        identifier = "not",
        parameters = listOf(
                Parameter("KT_input", AstNode.Type.Bool)
        ),
        returnType = AstNode.Type.Bool,
        body = {
            val value = it.first() as KotlinBoolean
            KotlinBoolean(!value.value)
        }
)
// endregion buildOperator_functions

// region builtInFunctions

private fun buildNumberToTextFunction() = buildKotlinFunction(
        identifier = "toText",
        parameters = listOf(
                Parameter("KT_input", AstNode.Type.Number)
        ),
        returnType = AstNode.Type.Text,
        body = {
            val value = it.first() as KotlinNumber
            KotlinText(value.value.toString())
        }
)

private fun buildBoolToTextFunction() = buildKotlinFunction(
        identifier = "toText",
        parameters = listOf(
                Parameter("KT_input", AstNode.Type.Bool)
        ),
        returnType = AstNode.Type.Text,
        body = {
            val value = it.first() as KotlinBoolean
            KotlinText(value.value.toString())
        }
)

private fun buildListToTextFunction() = buildKotlinFunction(
        identifier = "toText",
        parameters = listOf(
                Parameter("KT_input", AstNode.Type.List(AstNode.Type.GenericType("T")))
        ),
        returnType = AstNode.Type.Text,
        body = {
            val value = it.first() as KotlinList
            KotlinText(value.value.toString())
        }
)

private fun buildTextToTextFunction() = buildKotlinFunction(
        identifier = "toText",
        parameters = listOf(
                Parameter("KT_input", AstNode.Type.Text)
        ),
        returnType = AstNode.Type.Text,
        body = {
            val value = it.first() as KotlinText
            KotlinText(value.value)
        }
)

private fun buildGetListLengthFunction() = buildKotlinFunction(
        identifier = "length",
        parameters = listOf(
                Parameter("KT_list", AstNode.Type.List(AstNode.Type.GenericType("T"))) // Don't know if this will work!!!
        ),
        returnType = AstNode.Type.Number,
        body = {
            val value = it.first() as KotlinList
            KotlinNumber(value.value.size.toDouble())
        }
)

private fun buildListEqualsFunction() = buildKotlinFunction(
        identifier = "equals",
        parameters = listOf(
                Parameter("KT_leftHand", AstNode.Type.List(AstNode.Type.GenericType("T"))),
                Parameter("KT_rightHand", AstNode.Type.List(AstNode.Type.GenericType("T")))
        ),
        returnType = AstNode.Type.Bool,
        body = {
            val (l, h) = it.map { it as KotlinList }
            KotlinBoolean(l == h)
        }
)

private fun buildListNotEqualsFunction() = buildKotlinFunction(
        identifier = "notEquals",
        parameters = listOf(
                Parameter("KT_leftHand", AstNode.Type.List(AstNode.Type.GenericType("T"))),
                Parameter("KT_rightHand", AstNode.Type.List(AstNode.Type.GenericType("T")))
        ),
        returnType = AstNode.Type.Bool,
        body = {
            val (l, h) = it.map { it as KotlinList }
            KotlinBoolean(l != h)
        }
)

private fun buildTextEquals() = buildKotlinFunction(
        identifier = "equals",
        parameters = listOf(
                Parameter("KT_leftHand", AstNode.Type.Text),
                Parameter("KT_rightHand", AstNode.Type.Text)
        ),
        returnType = AstNode.Type.Bool,
        body = {
            val (l, h) = it.map { it as KotlinText }
            KotlinBoolean(l == h)
        }
)

private fun buildGenericsEquals() = buildKotlinFunction(
        identifier = "equals",
        parameters = listOf(
                Parameter("KT_leftHand", AstNode.Type.GenericType("T")),
                Parameter("KT_rightHand", AstNode.Type.GenericType("T"))
        ),
        returnType = AstNode.Type.Bool,
        body = {
            val (l, h) = it
            KotlinBoolean(l == h)
        }
)

private fun buildTextNotEquals() = buildKotlinFunction(
        identifier = "notEquals",
        parameters = listOf(
                Parameter("KT_leftHand", AstNode.Type.Text),
                Parameter("KT_rightHand", AstNode.Type.Text)
        ),
        returnType = AstNode.Type.Bool,
        body = {
            val (l, h) = it.map { it as KotlinText }
            KotlinBoolean(l != h)
        }
)

private fun buildTextConcat() = buildKotlinFunction(
        identifier = "+",
        parameters = listOf(
                Parameter("KT_leftHand", AstNode.Type.Text),
                Parameter("KT_rightHand", AstNode.Type.Text)
        ),
        returnType = AstNode.Type.Text,
        body = {
            val (l, h) = it.map { it as KotlinText }
            KotlinText(l.value + h.value)
        }
)

private fun buildAtListFunction() = buildKotlinFunction(
        identifier = "at",
        parameters = listOf(
                Parameter("KT_list", AstNode.Type.List(AstNode.Type.GenericType("T"))),
                Parameter("KT_rightHand", AstNode.Type.Number)
        ),
        returnType = AstNode.Type.GenericType("T"),
        body = {
            val list = it[0] as KotlinList
            val number = it[1] as KotlinNumber
            list.value[(number.value.toInt())]
        }
)

private fun buildAtTextFunction() = buildKotlinFunction(
        identifier = "at",
        parameters = listOf(
                Parameter("KT_list", AstNode.Type.Text),
                Parameter("KT_rightHand", AstNode.Type.Number)
        ),
        returnType = AstNode.Type.Text,
        body = {
            val text = it[0] as KotlinText
            val number = it[1] as KotlinNumber
            KotlinText("${text.value[(number.value.toInt())]}")
        }
)

private fun buildListConcatFunction() = buildKotlinFunction(
        identifier = "+",
        parameters = listOf(
                Parameter("KT_leftHand", AstNode.Type.List(AstNode.Type.GenericType("T"))),
                Parameter("KT_rightHand", AstNode.Type.List(AstNode.Type.GenericType("T")))
        ),
        returnType = AstNode.Type.List(AstNode.Type.GenericType("T")),
        body = {
            val (l, h) = it.map { it as KotlinList }
            KotlinList(l.value + h.value)
        }
)

private fun buildLengthText() =
        buildKotlinFunction(
                identifier = "length",
                parameters = listOf(
                        Parameter("KT_leftHand", AstNode.Type.Text)
                ),
                returnType = AstNode.Type.Number,
                body = {
                    val value = it.first() as KotlinText
                    KotlinNumber(value.value.length.toDouble())
                }
        )

private fun buildSubtextText() =
        buildKotlinFunction(
                identifier = "splitAt",
                parameters = listOf(
                        Parameter("KT_leftHand", AstNode.Type.Text),
                        Parameter("KT_startIndex", AstNode.Type.Number),
                        Parameter("KT_length", AstNode.Type.Number)
                ),
                returnType = AstNode.Type.Text,
                body = {
                    val text = it[0] as KotlinText
                    val startIndex = it[1] as KotlinNumber
                    val length = it[2] as KotlinNumber
                    KotlinText(text.value.substring(startIndex.value.toInt(),
                            startIndex.value.toInt() + length.value.toInt())
                    )
                }
        )

private fun buildSubListFunction() = buildKotlinFunction(
        identifier = "splitAt",
        parameters = listOf(
                Parameter("KT_list", AstNode.Type.List(AstNode.Type.GenericType("T"))),
                Parameter("KT_startIndex", AstNode.Type.Number),
                Parameter("KT_length", AstNode.Type.Number)
        ),
        returnType = AstNode.Type.List(AstNode.Type.GenericType("T")),
        body = {
            val list = it[0] as KotlinList
            val startIndex = it[1] as KotlinNumber
            val length = it[2] as KotlinNumber
            KotlinList(list.value.subList(startIndex.value.toInt(), startIndex.value.toInt() + length.value.toInt()))
        }
)

private fun buildToListFunction() = buildKotlinFunction(
        identifier = "to",
        parameters = listOf(
                Parameter("KT_start", AstNode.Type.Number),
                Parameter("KT_end", AstNode.Type.Number)
        ),
        returnType = AstNode.Type.List(AstNode.Type.Number),
        body = {
            val (l, h) = it.map { it as KotlinNumber }
            KotlinList((l.value.toInt()..h.value.toInt()).map { KotlinNumber(it.toDouble()) })
        }
)

private fun buildWhileFunction() = buildKotlinFunction(
        identifier = "while",
        parameters = listOf(
                Parameter("KT_body", AstNode.Type.Func(listOf(), AstNode.Type.None)),
                Parameter("KT_condition", AstNode.Type.Func(listOf(), AstNode.Type.Bool))
        ),
        returnType = AstNode.Type.None,
        body = {
            val body = it[0] as KotlinLambdaExpression
            val condition = it[1] as KotlinLambdaExpression
            while ((condition.invoke(listOf()) as KotlinBoolean).value) {
                body.invoke(listOf())
            }
            KotlinUnit
        }
)

private fun buildThenFunction() = buildKotlinFunction(
        identifier = "then",
        parameters = listOf(
                Parameter("KT_condition", AstNode.Type.Bool),
                Parameter("KT_body", AstNode.Type.Func(listOf(), AstNode.Type.None))
        ),
        returnType = AstNode.Type.Bool,
        body = {
            val condition = it[0] as KotlinBoolean
            val body = it[1] as KotlinLambdaExpression
            if (condition.value) body.invoke(listOf())
            condition
        }
)

private fun buildThenElseFunction() = buildKotlinFunction(
        identifier = "thenElse",
        parameters = listOf(
                Parameter("KT_condition", AstNode.Type.Bool),
                Parameter("KT_body1", AstNode.Type.Func(listOf(), AstNode.Type.GenericType("T"))),
                Parameter("KT_body2", AstNode.Type.Func(listOf(), AstNode.Type.GenericType("T")))
        ),
        returnType = AstNode.Type.Bool,
        body = {
            val condition = it[0] as KotlinBoolean
            val body1 = it[1] as KotlinLambdaExpression
            val body2 = it[2] as KotlinLambdaExpression
            if (condition.value) body1.invoke(listOf()) else body2.invoke(listOf())
        }
)

private fun buildThenElseFunction2() = buildKotlinFunction(
        identifier = "thenElse",
        parameters = listOf(
                Parameter("KT_condition", AstNode.Type.Bool),
                Parameter("KT_body1", AstNode.Type.Func(listOf(), AstNode.Type.None)),
                Parameter("KT_body2", AstNode.Type.Func(listOf(), AstNode.Type.None))
        ),
        returnType = AstNode.Type.Bool,
        body = {
            val condition = it[0] as KotlinBoolean
            val body1 = it[1] as KotlinLambdaExpression
            val body2 = it[2] as KotlinLambdaExpression
            if (condition.value) body1.invoke(listOf()) else body2.invoke(listOf())
            KotlinUnit
        }
)

private fun buildEachFunction() = buildKotlinFunction(
        identifier = "forEach",
        parameters = listOf(
                Parameter("KT_list", AstNode.Type.List(AstNode.Type.GenericType("T"))),
                Parameter("KT_body", AstNode.Type.Func(listOf(AstNode.Type.GenericType("T")), AstNode.Type.None))
        ),
        returnType = AstNode.Type.None,
        body = {
            val list = it[0] as KotlinList
            val body = it[1] as KotlinLambdaExpression
            list.value.forEach { body.invoke(listOf(it)) }
            KotlinUnit
        }
)

private fun buildMapFunction() = buildKotlinFunction(
        identifier = "map",
        parameters = listOf(
                Parameter("KT_list", AstNode.Type.List(AstNode.Type.GenericType("T"))),
                Parameter("KT_fun", AstNode.Type.Func(
                        listOf(AstNode.Type.GenericType("T")),
                        AstNode.Type.GenericType("T2"))
                )
        ),
        returnType = AstNode.Type.List(AstNode.Type.GenericType("T2")),
        body = {
            val list = it[0] as KotlinList
            val body = it[1] as KotlinLambdaExpression
            KotlinList(list.value.map { body.invoke(listOf(it)) })
        }
)

private fun buildFilterFunction() = buildKotlinFunction(
        identifier = "where",
        parameters = listOf(
                Parameter("KT_list", AstNode.Type.List(AstNode.Type.GenericType("T"))),
                Parameter("KT_fun",
                        AstNode.Type.Func(
                                listOf(AstNode.Type.GenericType("T")),
                                AstNode.Type.Bool
                        )
                )
        ),
        returnType = AstNode.Type.List(AstNode.Type.GenericType("T")),
        body = {
            val list = it[0] as KotlinList
            val body = it[1] as KotlinLambdaExpression
            KotlinList(list.value.filter { (body.invoke(listOf(it)) as KotlinBoolean).value })
        }
)

private fun buildPrintFunction() = buildKotlinFunction(
        identifier = "print",
        parameters = listOf(Parameter("KT_input", AstNode.Type.GenericType("T"))),
        returnType = AstNode.Type.None,
        body = {
            println(it.first())
            KotlinUnit
        }
)

private fun buildPrintFunctionList() = buildKotlinFunction(
        identifier = "print",
        parameters = listOf(Parameter("KT_input", AstNode.Type.List(AstNode.Type.GenericType("T")))),
        returnType = AstNode.Type.None,
        body = {
            println(it.first())
            KotlinUnit
        }
)

private fun buildPrintFunctionText() = buildKotlinFunction(
        identifier = "print",
        parameters = listOf(Parameter("KT_input", AstNode.Type.Text)),
        returnType = AstNode.Type.None,
        body = {
            println(it.first())
            KotlinUnit
        }
)

fun buildKotlinFunction(
    identifier: String,
    parameters: List<Parameter>,
    returnType: AstNode.Type,
    attributes: LambdaExpressionAttributes = BuiltinLambdaAttributes,
    body: KtInterpreter.(List<KotlinHclExpression>) -> KotlinHclExpression
) =
        AstNode.Command.Declaration(returnType, AstNode.Command.Expression.Value.Identifier(identifier, returnType),
                AstNode.Command.Expression.LambdaExpression(
                        paramDeclarations = parameters.map {
                            AstNode.ParameterDeclaration(
                                    it.type,
                                    AstNode.Command.Expression.Value.Identifier(it.identifier, returnType))
                        },
                        returnType = returnType,
                        attributes = attributes,
                        body = AstNode.Command.Expression.LambdaBody(
                                listOf(AstNode.Command.KotlinFunction(body))
                        )
                )
        )
