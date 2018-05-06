package parser

open class LambdaExpressionAttributes(val inLine: Boolean, val modifyParameterName: Boolean)
object DefaultLambdaAttributes : LambdaExpressionAttributes(false, true)
object BuiltinLambdaAttributes : LambdaExpressionAttributes(true, false)