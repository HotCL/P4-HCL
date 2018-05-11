package exceptions.typechecker

class ListWithMultipleTypesError(val expected: String, val actual: String) : Exception()
