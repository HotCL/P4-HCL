package parser

import exceptions.* // ktlint-disable no-wildcard-imports
import lexer.Token

fun Parser.functionInvokedWithoutArgumentsError(nameOfFunc: String): Nothing {
    throw FunctionInvokedWithoutArgumentsError(setLineNumber(), fileName, setLineIndex(1), setInputLine(), nameOfFunc)
}

fun Parser.identifierIsNotFunctionError(nameOfId: String): Nothing {
    throw IdentifierIsNotFunctionError(setLineNumber(), fileName, setLineIndex(1), setInputLine(), nameOfId)
}

fun Parser.lambdaArgumentNotAcceptedError(): Nothing {
    throw LambdaArgumentNotAcceptedError(setLineNumber(), fileName, setLineIndex(4), setInputLine())
}

fun Parser.funcDeclaredWithoutBodyError(nameOfFunc: String): Nothing {
    throw FuncDeclaredWithoutBodyError(setLineNumber(), fileName, setLineIndex(1), setInputLine(), nameOfFunc)
}

fun Parser.uninitializedImplicitTypeError(nameOfVar: String): Nothing {
    throw UninitializedImplicitTypeError(setLineNumber(), fileName, setLineIndex(1), setInputLine(), nameOfVar)
}

fun Parser.unexpectedTypeError(expectedType: String, actualType: String): Nothing {
    throw UnexpectedTypeError(setLineNumber(), fileName, setLineIndex(1),
            setInputLine(), expectedType, actualType)
}

fun Parser.unexpectedReturnTypeError(expectedType: String, actualType: String): Nothing {
    throw UnexpectedReturnTypeError(setLineNumber(1), fileName, setLineIndex(2),
            setInputLine(2), expectedType, actualType)
}

fun Parser.unexpectedTokenError(token: Token): Nothing {
    throw UnexpectedTokenError(setLineNumber(), fileName, setLineIndex(),
            setInputLine(), token)
}

fun Parser.lackingParenthesis(): Nothing {
    throw LackingParanthesisError(setLineNumber(), fileName, setLineIndex(),
            setInputLine(), current.token)
}

fun Parser.undeclaredError(token: Token.Identifier): Nothing {
    var lineIndex = 0
    while (lookBehind(lineIndex).token != token) lineIndex++
    throw UndeclaredError(setLineNumber(), fileName, setLineIndex(lineIndex), setInputLine(),
            token.value)
}

fun Parser.unknownFunctionOverload(token: Token.Identifier, args: List<AstNode.Type>): Nothing {
    var lineIndex = 0
    while (lookBehind(lineIndex).token != token) lineIndex++
    throw UnknownFunctionOverload(setLineNumber(), fileName, setLineIndex(lineIndex), setInputLine(),
            token.value, args)
}

fun Parser.implicitTypeNotAllowedError(): Nothing {
    throw ImplicitTypeNotAllowed(setLineNumber(), fileName, setLineIndex(1),
            setInputLine())
}

fun Parser.wrongTokenTypeError(name: String, token: Token): Nothing {
    throw WrongTokenTypeError(setLineNumber(), fileName, setLineIndex(1),
            setInputLine(), name, token)
}

fun Parser.initializedFunctionParameterError(): Nothing {
    throw InitializedFunctionParameterError(setLineNumber(), fileName, setLineIndex(1),
            setInputLine())
}

fun Parser.genericPassedFunctionException(): Nothing {
    throw GenericPassedFunctionException(setLineNumber(), fileName, setLineIndex(1),
            setInputLine())
}

fun Parser.alreadyDeclaredException(): Nothing {
    throw AlreadyDeclaredException(setLineNumber(), fileName, setLineIndex(3),
            setInputLine())
}

fun Parser.overloadWithDifferentAmountOfArgumentsException(): Nothing {
    throw OverloadWithDifferentAmountOfArgumentsException(setLineNumber(), fileName, setLineIndex(6),
            setInputLine())
}

fun Parser.error(msg: String, helpText: String = ""): Nothing {
    throw GenericParserException(setLineNumber(), fileName, setLineIndex(0),
        setInputLine(), msg, helpText)
}

val Parser.fileName get() = current.fileName

fun Parser.setLineNumber(shifts: Int = 0): Int {
    return lookBehind(shifts).lineNumber
}
fun Parser.setLineIndex(shifts: Int = 0): Int {
    return lookBehind(shifts).lineIndex
}
fun Parser.setInputLine(shifts: Int = 0): String {
    return lexer.inputLine(lookBehind(shifts).lineNumber,
        lookBehind(shifts).fileName)
}
