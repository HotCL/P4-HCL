package parser

/**
 * Attributes for lambda expressions
 * @param inLine Value to determine whether to take advantage of C++'s inline functionality
 * @param modifyParameterName Value to determine whether the function can be overloaded
 */
open class LambdaExpressionAttributes(val inLine: Boolean, val modifyParameterName: Boolean)
object DefaultLambdaAttributes : LambdaExpressionAttributes(false, true)
object BuiltinLambdaAttributes : LambdaExpressionAttributes(true, false)
