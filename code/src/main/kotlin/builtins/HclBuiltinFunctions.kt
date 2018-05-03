package builtins

import parser.AstNode
import parser.AstNode.Type

private data class Parameter(val identifier: String, val type: Type)

object HclBuiltinFunctions {
    val functions =
            // Operators
            listOf(
                    buildOperatorNumNumToNum("+"),
                    buildOperatorNumNumToNum("plus", "+"),
                    buildOperatorNumNumToNum("-"),
                    buildOperatorNumNumToNum("*"),
                    buildOperatorNumNumToNum("/"),

                    buildOperatorNumNumToBool("<"),
                    buildOperatorNumNumToBool(">"),
                    buildOperatorNumNumToBool("equals", "=="),
                    buildOperatorNumNumToBool("notEquals", "!="),

                    buildOperatorBoolBoolToBool("&&"),
                    buildOperatorBoolBoolToBool("||"),

                    buildPrefixOperator<Type.Bool, Type.Bool>("negated", "!"),
            // Control structures
                    buildThenFunction(),
                    buildWhileFunction(),
            // Standard functions
                    buildTextEqualsFunction(),
                    buildTextNotEqualsFunction(),
                    buildTextConcatFunction(),
                    buildNumberToTextFunction(),
                    buildTextToTextFunction(), //Redundant, but no reason for compiler to throw an error
                    buildBoolToTextFunction(),
                    buildGetListLengthFunction(),
                    buildAtListFunction(),
                    buildSubListFunction(),
                    buildListConcatFunction()
            )
}

//region buildOperator_functions
private fun buildOperatorNumNumToNum(functionName: String, operator: String = functionName) =
        buildOperator<Type.Number, Type.Number, Type.Number>(functionName, operator)

private fun buildOperatorNumNumToBool(functionName: String, operator: String = functionName) =
        buildOperator<Type.Number, Type.Number, Type.Bool>(functionName, operator)

private fun buildOperatorTxtTxtToBool(functionName: String, operator: String = functionName) =
        buildOperator<Type.Text, Type.Text, Type.Bool>(functionName, operator)

private fun buildOperatorBoolBoolToBool(functionName: String, operator: String = functionName) =
        buildOperator<Type.Bool, Type.Bool, Type.Bool>(functionName, operator)

//"Prefix" means it will be prefixed in C++, but postfixed in HCL
private inline fun<reified P, reified R> buildPrefixOperator(functionName: String, operator: String = functionName)
        where P : Type, R : Type = buildFunction(
        identifier = functionName,
        parameters = listOf(
                Parameter("operand", P::class.objectInstance!!)
        ),
        returnType = R::class.objectInstance!!,
        body = "return $operator operand;",
        inLine = true
)

private inline fun<reified V, reified H, reified R> buildOperator(functionName: String, operator: String = functionName)
        where V : Type, H : Type, R : Type = buildFunction(
        identifier = functionName,
        parameters = listOf(
                Parameter("leftHand", V::class.objectInstance!!),
                Parameter("rightHand", H::class.objectInstance!!)
        ),
        returnType = R::class.objectInstance!!,
        body = "return leftHand $operator rightHand;",
        inLine = true
)
//endregion buildOperator_functions

//region builtInFunctions
private fun buildTextEqualsFunction() = buildFunction(
        identifier = "equals",
        parameters = listOf(
                Parameter("leftHand", Type.Text),
                Parameter("rightHand", Type.Text)
        ),
        returnType = Type.Bool,
        body = "return strcmp(leftHand, rightHand) == 0;",
        inLine = false
)

private fun buildTextNotEqualsFunction() = buildFunction(
        identifier = "notEquals",
        parameters = listOf(
                Parameter("leftHand", Type.Text),
                Parameter("rightHand", Type.Text)
        ),
        returnType = Type.Bool,
        body = "return strcmp(leftHand, rightHand) != 0;",
        inLine = false
)

private fun buildTextConcatFunction() = buildFunction(
        identifier = "+",
        parameters = listOf(
                Parameter("leftHand", Type.Text),
                Parameter("rightHand", Type.Text)
        ),
        returnType = Type.Bool,
        body = "char *ret = malloc((strlen(leftHand) + strlen(rightHand) + 1) * sizeof(char));\n" +
               "ret[0] = 0;\n" +
               "strcat(ret, leftHand);\n" +
               "strcat(ret, rightHand);\n" +
               "return ret;",
        inLine = false
)

private fun buildNumberToTextFunction() = buildFunction(
        identifier = "toText",
        parameters = listOf(
                Parameter("input", Type.Number)
        ),
        returnType = Type.Bool,
        body = "return ftoa(input, 5);",
        inLine = false
)

private fun buildBoolToTextFunction() = buildFunction(
        identifier = "toText",
        parameters = listOf(
                Parameter("input", Type.Bool)
        ),
        returnType = Type.Bool,
        body = "return input ? \"True\" : \"False\";",
        inLine = false
)

private fun buildTextToTextFunction() = buildFunction(
        identifier = "toText",
        parameters = listOf(
                Parameter("input", Type.Text)
        ),
        returnType = Type.Bool,
        body = "return input;",
        inLine = false
)

private fun buildGetListLengthFunction() = buildFunction(
        identifier = "length",
        parameters = listOf(
                Parameter("list", Type.List(Type.GenericType("T"))) // Don't know if this will work!!!
        ),
        returnType = Type.Number,
        body = "return list.get()->size;",
        inLine = true
)

private fun buildAtListFunction() = buildFunction(
        identifier = "at",
        parameters = listOf(
                Parameter("list", Type.List(Type.GenericType("T"))),
                Parameter("rightHand", Type.Number)
        ),
        returnType = Type.GenericType("T"),
        body = "return ConstList<T>::at(list, (unsigned int)rightHand);",
        inLine = true
)

private fun buildListConcatFunction() = buildFunction(
        identifier = "+",
        parameters = listOf(
                Parameter("leftHand", Type.List(Type.GenericType("T"))),
                Parameter("rightHand", Type.List(Type.GenericType("T")))
        ),
        returnType = Type.List(Type.GenericType("T")),
        body = "return ConstList<T>::concat(leftHand, rightHand);",
        inLine = true
)

private fun buildSubListFunction() = buildFunction(
        identifier = "subList",
        parameters = listOf(
                Parameter("list", Type.List(Type.GenericType("T"))),
                Parameter("startIndex", Type.Number),
                Parameter("length", Type.Number)
        ),
        returnType = Type.List(Type.GenericType("T")),
        body = "return ConstList<T>::sublist(list, (unsigned int)startIndex, (unsigned int)length);",
        inLine = true
)

private fun buildWhileFunction() = buildFunction(
        identifier = "while",
        parameters = listOf(
                Parameter("body", Type.Func.ExplicitFunc(listOf(), Type.None)),
                Parameter("condition", Type.Bool)
        ),
        returnType = Type.Bool,
        body = "while (condition) { body(); }",
        inLine = true
)

private fun buildThenFunction() = buildFunction(
        identifier = "then",
        parameters = listOf(
                Parameter("condition", Type.Bool),
                Parameter("body", Type.Func.ExplicitFunc(listOf(), Type.None))
        ),
        returnType = Type.Bool,
        body = "if (condition) { body(); } return condition;",
        inLine = true
)
//endregion builtInFunctions

private fun buildFunction(identifier: String, parameters: List<Parameter>, returnType: Type,
                          body: String, inLine: Boolean) =
        AstNode.Command.Declaration(returnType, identifier.asIdentifier(),
                AstNode.Command.Expression.LambdaExpression(
                        paramDeclarations = parameters.map {
                            AstNode.ParameterDeclaration(it.type, it.identifier.asIdentifier())
                        },
                        returnType = returnType,
                        inLine = inLine,
                        body = body.asRawCppLambdaBody()
                )
        )

private fun String.asIdentifier() = AstNode.Command.Expression.Value.Identifier(this)
private fun String.asRawCppLambdaBody() =
        AstNode.Command.Expression.LambdaBody(listOf(AstNode.Command.RawCpp(this)))

