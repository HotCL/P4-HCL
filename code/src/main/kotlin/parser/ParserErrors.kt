package parser

import exceptions.* // ktlint-disable no-wildcard-imports
import lexer.Token

fun Parser.unexpectedTypeError(expectedType: String, actualType: String): Nothing {
    throw UnexpectedTypeError(current.lineNumber, current.lineIndex, lexer.inputLine(current.lineNumber), expectedType,
            actualType)
}

fun Parser.unexpectedReturnTypeError(expectedType: String, actualType: String): Nothing {
    throw UnexpectedReturnTypeError(current.lineNumber, current.lineIndex, lexer.inputLine(current.lineNumber),
            expectedType, actualType)
}

fun Parser.unexpectedTokenError(token: Token): Nothing {
    throw UnexpectedTokenError(current.lineNumber, current.lineIndex, lexer.inputLine(current.lineNumber), token)
}

fun Parser.undeclaredError(str: String): Nothing {
    throw UndeclaredError(current.lineNumber, current.lineIndex, lexer.inputLine(current.lineNumber), str)
}

fun Parser.implicitTypeNotAllowedError(): Nothing {
    throw ImplicitTypeNotAllowed(current.lineNumber, current.lineIndex, lexer.inputLine(current.lineNumber))
}

fun Parser.wrongTokenTypeError(name: String, token: Token): Nothing {
    throw WrongTokenTypeError(current.lineNumber, current.lineIndex, lexer.inputLine(current.lineNumber), name, token)
}

fun Parser.initializedFunctionParameterError(): Nothing {
    throw InitializedFunctionParameterError(current.lineNumber, current.lineIndex, lexer.inputLine(current.lineNumber))
}

fun Parser.genericPassedFunctionException(): Nothing {
    throw GenericPassedFunctionException(current.lineNumber, current.lineIndex, lexer.inputLine(current.lineNumber))
}

fun Parser.alreadyDeclaredException(): Nothing {
    throw AlreadyDeclaredException(current.lineNumber, current.lineIndex, lexer.inputLine(current.lineNumber))
}

fun Parser.overloadWithDifferentAmountOfArgumentsException(): Nothing {
    throw OverloadWithDifferentAmountOfArgumentsException(current.lineNumber, current.lineIndex,
            lexer.inputLine(current.lineNumber))
}

fun Parser.error(msg: String, helpText: String = ""): Nothing {
    throw GenericParserException(current.lineNumber, current.lineIndex, lexer.inputLine(current.lineNumber),
            msg, helpText)
}
