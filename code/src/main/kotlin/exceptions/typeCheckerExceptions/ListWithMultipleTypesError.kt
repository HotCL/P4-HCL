package exceptions.typeCheckerExceptions

class ListWithMultipleTypesError(val expected: String, val actual: String): Exception()
