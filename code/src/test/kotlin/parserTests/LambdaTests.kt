package parserTests

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import exceptions.UndeclaredError
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class LambdaTests {
    @Test
    fun canParseLambdaInFunctionCallPost() {
        "var apply = (num x, func[num, num] f): num {\n" +
        "   return x f\n" +
        "}\n" +
        "var res = 5 apply { 7 }" +
        "" becomes "" +
        "func[num, func[num, num], num] apply = (num x, func[num, num] f): num {\n" +
        "return x f\n" +
        "}\n" +
        "num res = 5.0 apply (num value): num {\n" +
        "return 7.0\n" +
        "}\n"
    }

    @Test
    fun canParseLambdaInFunctionCallPre() {
        "var apply = (func[num, num] f, num x): num {\n" +
        "   return x f\n" +
        "}\n" +
        "var res = { 11 } apply 7" +
        "" becomes "" +
        "func[func[num, num], num, num] apply = (func[num, num] f, num x): num {\n" +
        "return x f\n" +
        "}\n" +
        "num res = (num value): num {\n" +
        "return 11.0\n" +
        "} apply 7.0\n"
    }

    @Test
    fun canParseLambdaInFunctionCallFirstAndSecond() {
        "var apply = (func[num] f1, func[num] f2): num {\n" +
        "   return f1\n" +
        "}\n" +
        "var res = { 11 } apply { 13 }" +
        "" becomes "" +
        "func[func[num], func[num], num] apply = (func[num] f1, func[num] f2): num {\n" +
        "return f1\n" +
        "}\n" +
        "num res = (): num {\n" +
        "return 11.0\n" +
        "} apply (): num {\n" +
        "return 13.0\n" +
        "}\n"
    }

    @Test
    fun canParseLambdaInFunctionCallThird() {
        "var apply = (num x, bool y, func[num, bool, num] f): num {\n" +
        "   return x f y\n" +
        "}\n" +
        "var res = 5 apply true { 7 }" +
        "" becomes "" +
        "func[num, bool, func[num, bool, num], num] apply = (num x, bool y, func[num, bool, num] f): num {\n" +
        "return x f y\n" +
        "}\n" +
        "num res = 5.0 apply True (num value, bool value2): num {\n" +
        "return 7.0\n" +
        "}\n"
    }

    @Test
    fun failsOnLambdaWithWrongReturnType() {
        val exception = parseExpectException("" +
                "var f = (): num { \"fail\" }"
        )
        assertThat(exception.message, equalTo("Error! Declared return type did not match actual return type"))
    }

    @Test
    fun implicitReturnDoesNotWorksOnMoreExpressions() {
        val exception = parseExpectException("" +
                "var f = (): num { \n" +
                "   var z = 5 \n" +
                "   z\n" +
                "}"
        )
        assertThat(exception.message, equalTo("Error! Declared return type did not match actual return type"))
    }

    @Test
    fun explicitReturnDoesWorksOnMoreExpressions() {
        "var f = (): num { \n" +
        "   var z = 5 \n" +
        "   return z\n" +
        "}" becomes "" +
        "func[num] f = (): num {\n" +
        "num z = 5.0\n" +
        "return z\n" +
        "}\n"
    }

    @Test
    fun failsOnMultipleReturnTypes() {
        // This is more relevant when the lambda is passed to a function, but still works
        val exception = parseExpectException("" +
                "var f = (): num { \n" +
                "   return \"Text\"\n" +
                "   return 7\n" +
                "}"
        )
        assertThat(exception.message, equalTo("Lambda body with multiple return types"))
    }

    @Test
    fun worksOnOverloadedFunctionsWithLambda() {
        "var x = (func[num] f): num { f }\n" +
        "var x = (func[bool] f): bool { f }\n" +
        "var a = { true } x\n" +
        "var b = { 7.0 } x" becomes "" +
        "func[func[num], num] x = (func[num] f): num {\n" +
        "return f\n" +
        "}\n" +
        "func[func[bool], bool] x = (func[bool] f): bool {\n" +
        "return f\n" +
        "}\n" +
        "bool a = (): bool {\n" +
        "return True\n" +
        "} x\n" +
        "num b = (): num {\n" +
        "return 7.0\n" +
        "} x\n"
    }

    @Test
    fun failsOnOverloadedFunctionsWithMoreParamsLambda() {
        assertTrue(parseExpectException("var x = (func[num] f, num y): num { f }\n" +
        "var x = (func[bool] f, txt z): bool { f }\n" +
        "var a = { true } x 5\n" +
        "var b = { 7.0 } x \"hej\"") is UndeclaredError)
    }

    @Test
    fun succeedsOnOverloadOfOtherParamsThanLambda() {
        "var x = (func[num] f, num y): num { f }\n" +
        "var x = (func[num] f, txt z): num { f }\n" +
        "var a = { 3.0 } x 5\n" +
        "var b = { 7.0 } x \"hej\"" becomes "" +
        "func[func[num], num, num] x = (func[num] f, num y): num {\n" +
        "return f\n" +
        "}\n" +
        "func[func[num], text, num] x = (func[num] f, text z): num {\n" +
        "return f\n" +
        "}\n" +
        "num a = (): num {\n" +
        "return 3.0\n" +
        "} x 5.0\n" +
        "num b = (): num {\n" +
        "return 7.0\n" +
        "} x \"hej\"\n"
    }

    @Test
    fun succeedsOnReturningFunction() {
        "var x = (): func[num] { { 7 } }\n" +
        "var a = x\n" +
        "var b = a" becomes "" +
        "func[func[num]] x = (): func[num] {\n" +
        "return (): num {\n" +
        "return 7.0\n" +
        "}\n" +
        "}\n" +
        "func[num] a = x\n" +
        "num b = a\n"
    }

    @Test
    fun succeedsOnReturning2TimesFunction() {
        "var x = (): func[func[num]] { { { 7 } } }\n" +
        "var a = x\n" +
        "var b = a\n" +
        "var c = b" becomes "func[func[func[num]]] x = (): func[func[num]] {\n" +
        "return (): func[num] {\n" +
        "return (): num {\n" +
        "return 7.0\n" +
        "}\n" +
        "}\n" +
        "}\n" +
        "func[func[num]] a = x\n" +
        "func[num] b = a\n" +
        "num c = b"
    }

    @Test
    fun failsOnOverloadedFunctionsWithLambdaTypeNotDeclared() {
        assertTrue(parseExpectException("var x = (func[num] f): num { f }\n" +
        "var x = (func[bool] f): bool { f }\n" +
        "var a = { \"hej\" } x\n" +
        "var b = { 7.0 } x") is UndeclaredError)
    }
}
