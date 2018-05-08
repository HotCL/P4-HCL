package builtins

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

                    buildOperatorNumNumToBool("<"),
                    buildOperatorNumNumToBool(">"),
                    buildOperatorNumNumToBool("equals", "=="),
                    buildOperatorNumNumToBool("notEquals", "!="),

                    buildOperatorBoolBoolToBool("and", "&&"),
                    buildOperatorBoolBoolToBool("or", "||"),

                    buildPrefixOperator<Type.Bool, Type.Bool>("not", "!"),
            // Control structures
                    buildThenFunction(),
                    //buildElseTernaryFunction(),
                    buildWhileFunction(),
            // Standard functions
                    buildNumberToTextFunction(),
                    buildBoolToTextFunction(),
                    buildListToTextFunction(),
                    buildGetListLengthFunction(),
                    buildListEqualsFunction(),
                    buildListNotEqualsFunction(),
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
                "   output = ConstList<T>::concat(output, ${"toText".cppFun}(ConstList<T>::at(input,i)));\n" +
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



/*private fun buildElseTernaryFunction() = buildFunction(
        identifier = "else",
        parameters = listOf(
                Parameter("then_value", Type.Tuple(listOf(Type.Bool,Type.GenericType("T")))),
                Parameter("body", Type.Func.ExplicitFunc(listOf(), Type.GenericType("T")))
        ),
        returnType = Type.GenericType("T"),
        body = "if (then_value.element0) { return then_value.element1 } else { return body() };"
)*/
//endregion builtInFunctions
