package builtins

import parser.AstNode
import parser.AstNode.Type
import parser.BuiltinLambdaAttributes
import parser.LambdaExpressionAttributes

private data class Parameter(val identifier: String, val type: Type)

object HclBuiltinFunctions {
    val functions =
            // Operators
            listOf(
                    buildOperatorNumNumToNum("+"),
                    buildOperatorNumNumToNum("-"),
                    buildOperatorNumNumToNum("*"),
                    buildOperatorNumNumToNum("/"),
                    buildModuloOperator(),

                    buildOperatorNumNumToBool("<"),
                    buildOperatorNumNumToBool(">"),
                    buildOperatorNumNumToBool("equals", "=="),
                    buildOperatorNumNumToBool("notEquals", "!="),

                    buildOperatorBoolBoolToBool("and", "&&"),
                    buildOperatorBoolBoolToBool("or", "||"),
            // Control structures
                    buildThenFunction(),
                    buildWhileFunction(),
                    buildEachFunction(),
            // Standard functions
                    buildNotFunction(),
                    buildTextEqualsFunction(),
                    buildTextNotEqualsFunction(),
                    buildTextConcatFunction(),
                    buildNumberToTextFunction(),
                    buildTextToTextFunction(), //Redundant, but no reason for compiler to throw an error
                    buildBoolToTextFunction(),
                    buildGetListLengthFunction(),
                    buildAtListFunction(),
                    buildSubListFunction(),
                    buildListConcatFunction(),
                    buildMapFunction(),
                    buildFilterFunction(),
                    buildDelayMillisFunction(),
            // Read/Write functions for arduino
                    buildWriteDigPinFunction(),
                    buildReadDigPinFunction(),
                    buildWriteAnaPinFunction(),
                    buildReadAnaPinFunction(),
            // Print functions
                    buildPrintFunction<Type.Number>(),
                    buildPrintFunction<Type.Bool>(),
                    buildPrintFunction<Type.Text>(),
                    buildPrintFunction<Type.List>(),
                    buildPrintLineFunction<Type.Number>(),
                    buildPrintLineFunction<Type.Bool>(),
                    buildPrintLineFunction<Type.Text>(),
                    buildPrintLineFunction<Type.List>()
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

private inline fun<reified V, reified H, reified R> buildOperator(functionName: String, operator: String = functionName)
        where V : Type, H : Type, R : Type = buildFunction(
        identifier = functionName,
        parameters = listOf(
                Parameter("leftHand", V::class.objectInstance!!),
                Parameter("rightHand", H::class.objectInstance!!)
        ),
        returnType = R::class.objectInstance!!,
        body = "return leftHand $operator rightHand;"
)
private fun buildModuloOperator() = buildFunction(
        identifier = "mod",
        parameters = listOf(
                Parameter("leftHand", Type.Number),
                Parameter("rightHand", Type.Number)
        ),
        returnType = Type.Number,
        body = "return (long)leftHand % (long)rightHand;"
)
//endregion buildOperator_functions

//region builtInFunctions
private fun buildNotFunction() = buildFunction(
        identifier = "not",
        parameters = listOf(
                Parameter("b", Type.Bool)
        ),
        returnType = Type.Bool,
        body = "return !b;"
)

private fun buildTextEqualsFunction() = buildFunction(
        identifier = "equals",
        parameters = listOf(
                Parameter("leftHand", Type.Text),
                Parameter("rightHand", Type.Text)
        ),
        returnType = Type.Bool,
        body = "return strcmp(leftHand, rightHand) == 0;"
)

private fun buildTextNotEqualsFunction() = buildFunction(
        identifier = "notEquals",
        parameters = listOf(
                Parameter("leftHand", Type.Text),
                Parameter("rightHand", Type.Text)
        ),
        returnType = Type.Bool,
        body = "return strcmp(leftHand, rightHand) != 0;"
)

private fun buildTextConcatFunction() = buildFunction(
        identifier = "+",
        parameters = listOf(
                Parameter("leftHand", Type.Text),
                Parameter("rightHand", Type.Text)
        ),
        returnType = Type.Text,
        body = "char *ret = malloc((strlen(leftHand) + strlen(rightHand) + 1) * sizeof(char));\n" +
               "ret[0] = 0;\n" +
               "strcat(ret, leftHand);\n" +
               "strcat(ret, rightHand);\n" +
               "return ret;"
)

private fun buildNumberToTextFunction() = buildFunction(
        identifier = "toText",
        parameters = listOf(
                Parameter("input", Type.Number)
        ),
        returnType = Type.Bool,
        body = "return ftoa(input, 5);"
)

private fun buildBoolToTextFunction() = buildFunction(
        identifier = "toText",
        parameters = listOf(
                Parameter("input", Type.Bool)
        ),
        returnType = Type.Bool,
        body = "return input ? \"True\" : \"False\";"
)

private fun buildTextToTextFunction() = buildFunction(
        identifier = "toText",
        parameters = listOf(
                Parameter("input", Type.Text)
        ),
        returnType = Type.Bool,
        body = "return input;"
)

private fun buildGetListLengthFunction() = buildFunction(
        identifier = "length",
        parameters = listOf(
                Parameter("list", Type.List(Type.GenericType("T"))) // Don't know if this will work!!!
        ),
        returnType = Type.Number,
        body = "return list.get()->size;"
)

private fun buildAtListFunction() = buildFunction(
        identifier = "at",
        parameters = listOf(
                Parameter("list", Type.List(Type.GenericType("T"))),
                Parameter("rightHand", Type.Number)
        ),
        returnType = Type.GenericType("T"),
        body = "return ConstList<T>::at(list, (unsigned int)rightHand);"
)

private fun buildListConcatFunction() = buildFunction(
        identifier = "+",
        parameters = listOf(
                Parameter("leftHand", Type.List(Type.GenericType("T"))),
                Parameter("rightHand", Type.List(Type.GenericType("T")))
        ),
        returnType = Type.List(Type.GenericType("T")),
        body = "return ConstList<T>::concat(leftHand, rightHand);"
)

private fun buildSubListFunction() = buildFunction(
        identifier = "subList",
        parameters = listOf(
                Parameter("list", Type.List(Type.GenericType("T"))),
                Parameter("startIndex", Type.Number),
                Parameter("length", Type.Number)
        ),
        returnType = Type.List(Type.GenericType("T")),
        body = "return ConstList<T>::sublist(list, (unsigned int)startIndex, (unsigned int)length);"
)

private fun buildWhileFunction() = buildFunction(
        identifier = "while",
        parameters = listOf(
                Parameter("body", Type.Func.ExplicitFunc(listOf(), Type.None)),
                Parameter("condition", Type.Bool)
        ),
        returnType = Type.Bool,
        body = "while (condition) body();"
)

private fun buildThenFunction() = buildFunction(
        identifier = "then",
        parameters = listOf(
                Parameter("condition", Type.Bool),
                Parameter("body", Type.Func.ExplicitFunc(listOf(), Type.None))
        ),
        returnType = Type.Bool,
        body = "if (condition) { body(); }\nreturn condition;"
)

private fun buildEachFunction() = buildFunction(
        identifier = "each",
        parameters = listOf(
                Parameter("list", Type.List(Type.GenericType("T"))),
                Parameter("body", Type.Func.ExplicitFunc(listOf(Type.GenericType("T")), Type.None))
        ),
        returnType = Type.None,
        body = "for (int i = 0; i < list.get()->size; i++) {\n" +
                "body(list.get()->data[i]);\n" +
                "}\n" +
                "return;"
)

private fun buildMapFunction() = buildFunction(
        identifier = "map",
        parameters = listOf(
                Parameter("list", Type.List(Type.GenericType("T"))),
                Parameter("fun", Type.Func.ExplicitFunc(
                        listOf(Type.GenericType("T")),
                        Type.GenericType("T"))
                )
        ),
        returnType = Type.List(Type.GenericType("T")),
        body = "T result[list.get()->size];\n" +
                "for (int i = 0; i < list.get()->size; i++) {\n" +
                "result[i] = fun(list.get()->data[i]);\n" +
                "}\n" +
                "return ConstList<T>::create(result, list.get()->size);"
)

private fun buildFilterFunction() = buildFunction(
        identifier = "filter",
        parameters = listOf(
                Parameter("list", Type.List(Type.GenericType("T"))),
                Parameter("fun",
                        Type.Func.ExplicitFunc(
                                listOf(Type.GenericType("T")),
                                Type.Bool
                        )
                )
        ),
        returnType = Type.List(Type.GenericType("T")),
        body = "T result[list.get()->size];\n" +
                "int index = 0;\n" +
                "for (int i = 0; i < list.get()->size; i++) {\n" +
                "if (fun(list.get()->data[i]))\n" +
                "result[index++] = list.get()->data[i];\n" +
                "}\n" +
                "return ConstList<T>::create(result, index);"
)

private fun buildDelayMillisFunction() = buildFunction(
        identifier = "delayMillis",
        parameters = listOf(Parameter("millis", Type.Number)),
        returnType = Type.None,
        body = "delayMillis(millis);\n" +
                "return;"
)

//region PinFunctions
private fun buildWriteDigPinFunction() = buildFunction(
        identifier = "setDigitalPin",
        parameters = listOf(Parameter("pin", Type.Number), Parameter("val", Type.Bool)),
        returnType = Type.None,
        body = "writeDigPin(pin, val ? 1 : 0);\n" +
                "return;"
)

private fun buildReadDigPinFunction() = buildFunction(
        identifier = "readDigitalPin",
        parameters = listOf(Parameter("pin", Type.Number)),
        returnType = Type.Number,
        body = "return readDigPin(pin);"
)

private fun buildWriteAnaPinFunction() = buildFunction(
        identifier = "setAnalogPin",
        parameters = listOf(
                Parameter("pin", Type.Number),
                Parameter("val", Type.Number)
        ),
        returnType = Type.None,
        body = "writeAnaPin(pin, val);\n" +
                "return;"
)

private fun buildReadAnaPinFunction() = buildFunction(
        identifier = "readAnalogPin",
        parameters = listOf(Parameter("pin", Type.Number)),
        returnType = Type.Number,
        body = "return readAnaPin(pin);"
)
//endregion PinFunctions

//region PrintFunctions
private inline fun<reified T: Type> buildPrintFunction() = buildFunction(
        identifier = "print",
        parameters = listOf(Parameter("input", T::class.objectInstance!!)),
        returnType = Type.None,
        body = "print(toText(input));\n" +
                "return;"
)

private inline fun<reified T: Type> buildPrintLineFunction() = buildFunction(
        identifier = "printLine",
        parameters = listOf(Parameter("input", T::class.objectInstance!!)),
        returnType = Type.None,
        body = "print_line(toText(input));\n" +
                "return;"
)

//endregion PrintFunctions
//endregion builtInFunctions

private fun buildFunction(identifier: String, parameters: List<Parameter>, returnType: Type,
                          body: String, attributes: LambdaExpressionAttributes = BuiltinLambdaAttributes) =
        AstNode.Command.Declaration(returnType, identifier.asIdentifier(),
                AstNode.Command.Expression.LambdaExpression(
                        paramDeclarations = parameters.map {
                            AstNode.ParameterDeclaration(it.type, it.identifier.asIdentifier())
                        },
                        returnType = returnType,
                        attributes = attributes,
                        body = body.asRawCppLambdaBody()
                )
        )

private fun String.asIdentifier() = AstNode.Command.Expression.Value.Identifier(this)
private fun String.asRawCppLambdaBody() =
        AstNode.Command.Expression.LambdaBody(listOf(AstNode.Command.RawCpp(this)))

