package builtins

import generation.cpp.cppName
import parser.*
import parser.AstNode.Type


object HclBuiltinFunctions {
    val functions =
    // Operators
            listOf(
                    buildOperatorNumNumToNum("+"),
                    buildOperatorNumNumToNum("-"),
                    buildOperatorNumNumToNum("*"),
                    buildOperatorNumNumToNum("/"),

                    buildOperatorBoolBoolToBool("and", "&&"),
                    buildOperatorBoolBoolToBool("or", "||"),

                    buildOperatorToBool<Type.Number>("greaterThanEqual",">="),
                    buildOperatorToBool<Type.Number>("lessThanEqual","<="),
                    buildOperatorToBool<Type.Number>("greaterThan",">"),
                    buildOperatorToBool<Type.Number>("lessThan","<"),
                    buildOperatorToBool<Type.Number>("equals", "=="),
                    buildOperatorToBool<Type.Number>("notEquals", "!="),
                    buildOperatorToBool<Type.Bool>("equals", "=="),
                    buildOperatorToBool<Type.Bool>("notEquals", "!="),

                    buildPrefixOperator<Type.Bool, Type.Bool>("not", "!"),
                    // Control structures
                    buildThenFunction(),
                    //buildElseTernaryFunction(),
                    buildWhileFunction(),
                    buildEachFunction(),
            // Standard functions
                    buildNumberToTextFunction(),
                    buildBoolToTextFunction(),
                    buildListToTextFunction(),
                    buildGetListLengthFunction(),
                    buildListEqualsFunction(),
                    buildListNotEqualsFunction(),
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
                    /* Kept incase we can't use generics
                    buildPrintFunction<Type.Number>(),
                    buildPrintFunction<Type.Bool>(),
                    buildPrintFunction<Type.Text>(),
                    buildPrintFunction<Type.List>(),
                    buildPrintLineFunction<Type.Number>(),
                    buildPrintLineFunction<Type.Bool>(),
                    buildPrintLineFunction<Type.Text>(),
                    buildPrintLineFunction<Type.List>()
                    */
                    buildPrintFunction()
            )
}

//region buildOperator_functions
private inline fun<reified T: Type> buildOperatorToBool(functionName: String, operator: String = functionName)=
        buildOperator<T, T, Type.Bool>(functionName, operator)


private fun buildOperatorNumNumToNum(functionName: String, operator: String = functionName) =
        buildOperator<Type.Number, Type.Number, Type.Number>(functionName, operator)


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
        body = "return $operator operand;"
)

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
//endregion buildOperator_functions

//region builtInFunctions


private fun buildNumberToTextFunction() = buildFunction(
        identifier = "toText",
        parameters = listOf(
                Parameter("input", Type.Number)
        ),
        returnType = Type.Text,
        body = "return ftoa(input, 5);"
)

private fun buildBoolToTextFunction() = buildFunction(
        identifier = "toText",
        parameters = listOf(
                Parameter("input", Type.Bool)
        ),
        returnType = Type.Text,
        body = "return input ? ConstList<char>::string((char *)\"True\") : ConstList<char>::string((char *)\"False\");"
)

private fun buildListToTextFunction() = buildFunction(
        identifier = "toText",
        parameters = listOf(
                Parameter("input", Type.List(Type.GenericType("T")))
        ),
        returnType = Type.Text,
        body = "auto output = ConstList<char>::string((char*)\"[\");\n" +
                "for(int i = 0; i < input.get()->size; i++) {\n" +
                "   output = ConstList<T>::concat(output, ${"toText".cppName}(ConstList<T>::at(input,i)));\n" +
                "}\n" +
                "return output;"
)

private fun buildGetListLengthFunction() = buildFunction(
        identifier = "length",
        parameters = listOf(
                Parameter("list", Type.List(Type.GenericType("T"))) // Don't know if this will work!!!
        ),
        returnType = Type.Number,
        body = "return list.get()->size;"
)

private fun buildListEqualsFunction() = buildFunction(
        identifier = "equals",
        parameters = listOf(
                Parameter("leftHand", Type.List(Type.GenericType("T"))),
                Parameter("rightHand", Type.List(Type.GenericType("T")))
        ),
        returnType = Type.Bool,
        body = "return length(leftHand) == length(rightHand) && " +
                "memcmp(leftHand.get()->data, rightHand.get()->data, leftHand.get()->size * sizeof(T)) == 0;"
)

private fun buildListNotEqualsFunction() = buildFunction(
        identifier = "notEquals",
        parameters = listOf(
                Parameter("leftHand", Type.List(Type.GenericType("T"))),
                Parameter("rightHand", Type.List(Type.GenericType("T")))
        ),
        returnType = Type.Bool,
        body = "return !equals(leftHand,rightHand);"
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
                Parameter("condition", Type.Func.ExplicitFunc(listOf(), Type.Bool))
        ),
        returnType = Type.Bool,
        body = "while (condition()) body();"
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
private fun buildPrintFunction() = buildFunction(
        identifier = "print",
        parameters = listOf(Parameter("input", Type.GenericType("T"))),
        returnType = Type.None,
        body = "print(toText(input));\n" +
                "return;"
)
/* THESE SHOULDN*T BE NEEDED. generics should work
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
*/

//endregion PrintFunctions
//endregion builtInFunctions


//endregion builtInFunctions
