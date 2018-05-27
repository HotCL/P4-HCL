package builtins

import generation.cpp.cppName
import parser.AstNode.Type
import parser.Parameter
import parser.buildFunction

/**
 * This object is responsible for adding all built-in function declarations to the AST
 * Only essential functions that cannot be defined in pure HCL, or that will have too
 * much overhead in HCL are defined here
 */
object HclBuiltinFunctions {
    val functions =
    // Operators
        listOf(
            buildOperatorNumNumToNum("+"),
            buildOperatorNumNumToNum("-"),
            buildOperatorNumNumToNum("*"),
            buildOperatorNumNumToNum("/"),
            buildModuloOperator(),

            buildOperatorBoolBoolToBool("and", "&&"),
            buildOperatorBoolBoolToBool("or", "||"),
            buildOperatorToBool<Type.Number>("greaterThanEqual", ">="),
            buildOperatorToBool<Type.Number>("lessThanEqual", "<="),
            buildOperatorToBool<Type.Number>("greaterThan", ">"),
            buildOperatorToBool<Type.Number>("lessThan", "<"),
            buildOperatorToBool<Type.Number>("equals", "=="),
            buildOperatorToBool<Type.Number>("notEquals", "!="),
            buildOperatorToBool<Type.Bool>("equals", "=="),
            buildOperatorToBool<Type.Bool>("notEquals", "!="),

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
            buildDelayMillisFunction(),
            buildAtTextFunction(),
            buildSubtextText(),
            buildLengthText(),
            buildToListFunction(),

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
            buildPrintFunctionText(),
            buildPrintFunctionList(),
            buildPrintFunction()
        ) + buildTwoParametersTextAsListFunctions(listOf(
            Pair("+", Type.Text),
            Pair("equals", Type.Bool),
            Pair("notEquals", Type.Bool)
        ))
}

// region buildOperator_functions
private inline fun <reified T : Type> buildOperatorToBool(functionName: String, operator: String = functionName) =
    buildOperator<T, T, Type.Bool>(functionName, operator)

private fun buildOperatorNumNumToNum(functionName: String, operator: String = functionName) =
    buildOperator<Type.Number, Type.Number, Type.Number>(functionName, operator)

private fun buildOperatorBoolBoolToBool(functionName: String, operator: String = functionName) =
    buildOperator<Type.Bool, Type.Bool, Type.Bool>(functionName, operator)

private inline fun <reified V, reified H, reified R> buildOperator(
    functionName: String,
    operator: String = functionName
)
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

private fun buildNotFunction() = buildFunction(
        identifier = "not",
        parameters = listOf(
                Parameter("input", Type.Bool)
        ),
        returnType = Type.Bool,
        body = "return !input;"
)
// endregion buildOperator_functions

// region builtInFunctions

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
    body = "return input ? ConstList<char>::string((char *)\"true\") : ConstList<char>::string((char *)\"false\");"
)

private fun buildListToTextFunction() = buildFunction(
    identifier = "toText",
    parameters = listOf(
        Parameter("input", Type.List(Type.GenericType("T")))
    ),
    returnType = Type.Text,
    body = "auto output = ConstList<char>::string((char*)\"[\");\n" +
        "for(int i = 0; i < input.get()->size; i++) {\n" +
        "   output = ConstList<char>::concat(output, ${"toText".cppName}(ConstList<T>::at(input, i)));\n" +
        "   if (i != input.get()->size-1)\n" +
        "       output = ConstList<char>::concat(output, ConstList<char>::string((char*)\", \"));" +
        "}\n" +
        "return ConstList<char>::concat(output, ConstList<char>::string((char*)\"]\"));"
)

private fun buildTextToTextFunction() = buildFunction(
    identifier = "toText",
    parameters = listOf(
        Parameter("input", Type.Text)
    ),
    returnType = Type.Text,
    body = "auto quote = ConstList<char>::string((char *)\"\\\"\");\n" +
        "return ConstList<char>::concat(quote, ConstList<char>::concat(input, quote));"
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
    body = "return ${"length".cppName}(leftHand) == ${"length".cppName}(rightHand) && " +
        "memcmp(leftHand.get()->data, rightHand.get()->data, leftHand.get()->size * sizeof(T)) == 0;"
)

private fun buildListNotEqualsFunction() = buildFunction(
    identifier = "notEquals",
    parameters = listOf(
        Parameter("leftHand", Type.List(Type.GenericType("T"))),
        Parameter("rightHand", Type.List(Type.GenericType("T")))
    ),
    returnType = Type.Bool,
    body = "return !${"equals".cppName}(leftHand,rightHand);"
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

private fun buildAtTextFunction() = buildFunction(
    identifier = "at",
    parameters = listOf(
        Parameter("list", Type.Text),
        Parameter("rightHand", Type.Number)
    ),
    returnType = Type.Text,
    body = "" +
        "char charArr[2] = {ConstList<char>::at(list, (unsigned int)rightHand),'\\0'};\n" +
        "return ConstList<char>::string(charArr);"
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
private fun buildTwoParametersTextAsListFunctions(list: List<Pair<String, Type>>) =
    list.map {
        buildFunction(
            identifier = it.first,
            parameters = listOf(
                Parameter("leftHand", Type.Text),
                Parameter("rightHand", Type.Text)
            ),
            returnType = it.second,
            body = "return ${it.first.cppName}<char>(leftHand, rightHand);"
        )
    }.toTypedArray()

private fun buildLengthText() =
    buildFunction(
        identifier = "length",
        parameters = listOf(
            Parameter("leftHand", Type.Text)
        ),
        returnType = Type.Number,
        body = "return ${"length".cppName}<char>(leftHand);"
    )

private fun buildSubtextText() =
    buildFunction(
        identifier = "splitAt",
        parameters = listOf(
            Parameter("leftHand", Type.Text),
            Parameter("startIndex", Type.Number),
            Parameter("length", Type.Number)
        ),
        returnType = Type.Text,
        body = "return ${"splitAt".cppName}<char>(leftHand,startIndex,length);"
    )

private fun buildSubListFunction() = buildFunction(
    identifier = "splitAt",
    parameters = listOf(
        Parameter("list", Type.List(Type.GenericType("T"))),
        Parameter("startIndex", Type.Number),
        Parameter("length", Type.Number)
    ),
    returnType = Type.List(Type.GenericType("T")),
    body = "return ConstList<T>::sublist(list, (unsigned int)startIndex, (unsigned int)length);"
)

private fun buildToListFunction() = buildFunction(
    identifier = "to",
    parameters = listOf(
        Parameter("start", Type.Number),
        Parameter("end", Type.Number)
    ),
    returnType = Type.List(Type.Number),
    body = "double array[(int)end - (int)start + 1];\n" +
        "for(int i = 0; i <= end - start; i++){\n " +
        "   array[i] = start + i;\n" +
        "}\n" +
        "return ConstList<double>::create_from_copy(array, end - start + 1);"
)

private fun buildWhileFunction() = buildFunction(
    identifier = "while",
    parameters = listOf(
        Parameter("body", Type.Func(listOf(), Type.None)),
        Parameter("condition", Type.Func(listOf(), Type.Bool))
    ),
    returnType = Type.None,
    body = "while (condition()) body();"
)

private fun buildThenFunction() = buildFunction(
    identifier = "then",
    parameters = listOf(
        Parameter("condition", Type.Bool),
        Parameter("body", Type.Func(listOf(), Type.None))
    ),
    returnType = Type.Bool,
    body = "if (condition) { body(); }\nreturn condition;"
)

private fun buildEachFunction() = buildFunction(
    identifier = "forEach",
    parameters = listOf(
        Parameter("list", Type.List(Type.GenericType("T"))),
        Parameter("body", Type.Func(listOf(Type.GenericType("T")), Type.None))
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
        Parameter("fun", Type.Func(
            listOf(Type.GenericType("T")),
            Type.GenericType("T2"))
        )
    ),
    returnType = Type.List(Type.GenericType("T2")),
    body = "T2 result[list.get()->size];\n" +
        "for (int i = 0; i < list.get()->size; i++) {\n" +
        "result[i] = fun(list.get()->data[i]);\n" +
        "}\n" +
        "return ConstList<T2>::create_from_copy(result, list.get()->size);"
)

private fun buildFilterFunction() = buildFunction(
    identifier = "where",
    parameters = listOf(
        Parameter("list", Type.List(Type.GenericType("T"))),
        Parameter("fun",
            Type.Func(
                listOf(Type.GenericType("T")),
                Type.Bool
            )
        )
    ),
    returnType = Type.List(Type.GenericType("T")),
    body = "T result[list.get()->size];\n" +
        "int index = 0;\n" +
        "for (int i = 0; i < list.get()->size; i++) {\n" +
        "   if (fun(list.get()->data[i]))\n" +
        "       result[index++] = list.get()->data[i];\n" +
        "}\n" +
        "return ConstList<T>::create_from_copy(result, index);"
)

private fun buildDelayMillisFunction() = buildFunction(
    identifier = "delay",
    parameters = listOf(Parameter("millis", Type.Number)),
    returnType = Type.None,
    body = "#if defined(ARDUINO_AVR_UNO) || defined(ESP8266)\n" +
        "delay((int)millis);\n" +

        "#else //ARDUINO_AVR_UNO\n" +
        "#ifdef _WIN32 //If windows based PC\n" +

        "Sleep((unsigned int)(millis));\n" +

        "#else // If unix based PC\n" +

        "usleep(((unsigned int)millis*1000)); //convert milliseconds to microseconds\n" +

        "#endif //_WIN32\n" +
        "#endif\n" +
        "return;"
)

// region PinFunctions
private fun buildWriteDigPinFunction() = buildFunction(
    identifier = "setDigitalPin",
    parameters = listOf(Parameter("pin", Type.Number), Parameter("value", Type.Bool)),
    returnType = Type.None,
    body = "#if defined(ARDUINO_AVR_UNO) || defined(ESP8266)\n" +
        "pinMode((int)pin, 1);\n" +
        "digitalWrite((int)pin, value);\n" +
        "#else\n" +
        "std::cout << \"Set digital pin \" << (int)pin << \" to output \" << (value ? \"HIGH\" : \"LOW\") << " +
        "std::endl;\n" +
        "#endif // ARDUINO_AVR_UNO"
)

private fun buildReadDigPinFunction() = buildFunction(
    identifier = "readDigitalPin",
    parameters = listOf(Parameter("pin", Type.Number)),
    returnType = Type.Number,
    body = "#if defined(ARDUINO_AVR_UNO) || defined(ESP8266)\n" +
        "pinMode((int)pin, 0);\n" +
        "return (double)digitalRead((int)pin);\n" +
        "#else\n" +
        "std::cout << \"Reading from digital pin \" << (int)pin << std::endl;\n" +
        "return 0;\n" +
        "#endif // ARDUINO_AVR_UNO\n"
)

private fun buildWriteAnaPinFunction() = buildFunction(
    identifier = "setAnalogPin",
    parameters = listOf(
        Parameter("pin", Type.Number),
        Parameter("value", Type.Number)
    ),
    returnType = Type.None,
    body = "#if defined(ARDUINO_AVR_UNO) || defined(ESP8266)\n" +
        "pinMode(pin, 1);\n" +
        "analogWrite((int)pin, value);\n" +
        "#else\n" +
        "std::cout << \"Set analog pin \" << (int)pin << \" to output \" << value << std::endl;\n" +
        "#endif // ARDUINO_AVR_UNO\n" +
        "return;"
)

private fun buildReadAnaPinFunction() = buildFunction(
    identifier = "readAnalogPin",
    parameters = listOf(Parameter("pin", Type.Number)),
    returnType = Type.Number,
    body = "#if defined(ARDUINO_AVR_UNO) || defined(ESP8266)\n" +
        "pinMode((int)pin, 0);\n" +
        "return analogRead((int)pin);\n" +
        "#else\n" +
        "std::cout << \"Reading from analog pin \" << (int)pin << std::endl;\n" +
        "return 0;\n" +
        "#endif // ARDUINO_AVR_UNO\n"
)
// endregion PinFunctions

// region PrintFunctions
private fun buildPrintFunction() = buildFunction(
    identifier = "print",
    parameters = listOf(Parameter("input", Type.GenericType("T"))),
    returnType = Type.None,
    body = "${"print".cppName}(${"toText".cppName}(input));\n"
)

private fun buildPrintFunctionList() = buildFunction(
    identifier = "print",
    parameters = listOf(Parameter("input", Type.List(Type.GenericType("T")))),
    returnType = Type.None,
    body = "${"print".cppName}(${"toText".cppName}<T>(input));\n"
)

private fun buildPrintFunctionText() = buildFunction(
    identifier = "print",
    parameters = listOf(Parameter("input", Type.Text)),
    returnType = Type.None,
    body = "" +
        "auto val = ConstList<char>::concat(input, ConstList<char>::create((char *)\"\\0\", 2));\n" +
        "#if defined(ARDUINO_AVR_UNO) || defined(ESP8266)\n" +
        "Serial.print(val.get()->data);\n" +
        "#else // NOT ARDUINO_AVR_UNO\n" +
        "std::cout << val.get()->data;\n" +
        "#endif // ARDUINO_AVR_UNO\n" +
        "return;"
)

// endregion PrintFunctions
// endregion builtInFunctions

// endregion builtInFunctions
