package interpreter.kotlin

import interpreter.IInterpreter
import parser.AstNode
import parser.kotlin.KtParser

class KtInterpreter(val parser: KtParser) : IInterpreter {
    var printExpression: Boolean = false
    override fun run(): Int {
        val memory = Memory()
        parser.commandSequence().forEach { it.handle(memory) }
        return (memory["RETURN_CODE"]!!.evaluate(memory) as KotlinNumber).value.toInt()
    }

    private fun AstNode.Command.handle(memory: Memory) {
        when (this) {
            is AstNode.Command.Declaration -> handle(memory)
            is AstNode.Command.Assignment -> handle(memory)
            is AstNode.Command.Expression -> handle(memory)
            is AstNode.Command.Return -> handle(memory)
        }
    }

    private fun List<AstNode.Type>.matchesArgs (args: List<KotlinHclExpression>): Boolean = zip(args).all {
            when (it.first) {
                AstNode.Type.Number -> it.second is KotlinNumber
                AstNode.Type.Text -> it.second is KotlinText
                AstNode.Type.Bool -> it.second is KotlinBoolean
                AstNode.Type.None -> false
                is AstNode.Type.GenericType -> true
                is AstNode.Type.List -> listOf((it.first as AstNode.Type.List).elementType).matchesArgs(
                            listOf((it.second as? KotlinList)?.value?.let { it.firstOrNull() ?: return@all true }
                                    ?: return@all false))
                is AstNode.Type.Func -> it.let { nonCastedFunc ->
                    val func = nonCastedFunc.first as AstNode.Type.Func
                    val lambda = it.second as? KotlinLambdaExpression ?: return@let false
                    val paramsMatches = func.paramTypes.zip(lambda.args.map { it.type }).all {
                        it.first == it.second || it.first is AstNode.Type.GenericType
                    }
                    return paramsMatches &&
                            (func.returnType == lambda.returnType || func.returnType is AstNode.Type.GenericType)
                }
                is AstNode.Type.Tuple ->
                    (it.first as AstNode.Type.Tuple).elementTypes.matchesArgs(
                            (it.second as? KotlinTuple)?.value ?: return@all false)
            }
        }

    private fun AstNode.Command.KotlinFunction.invokeKotlin(arguments: List<KotlinHclExpression>) = func(arguments)

    fun KotlinLambdaExpression.invoke(arguments: List<KotlinHclExpression>): KotlinHclExpression {
        lambdaMemory.pushScope()

        args.forEachIndexed { index, it ->
            lambdaMemory[it.identifier.name] = (arguments[index] as? KotlinLambdaExpression)?.let {
                    KotlinLambdaCollection(mutableListOf(it))
                } ?: arguments[index]
        }

        body.value.forEach {
            if (it is AstNode.Command.Return) {
                return it.handle(lambdaMemory)
            } else if (it is AstNode.Command.KotlinFunction) {
                return it.invokeKotlin(arguments)
            }
            it.handle(lambdaMemory)
        }.also {
            lambdaMemory.popScope()
        }

        return KotlinUnit
    }

    private fun KotlinFunctionCall.invoke(memory: Memory): KotlinHclExpression {

        val lambdas = try {
            val lam = memory[identifier.value]
            when (lam) {
                is KotlinLambdaCollection -> lam
                is KotlinLambdaExpression -> KotlinLambdaCollection(mutableListOf(lam))
                else -> throw Exception("Unable to invoke $lam")
            }
        } catch (exception: TypeCastException) {
            if (identifier.value.contains("element")) {
                val index = identifier.value.removePrefix("element").toInt()
                return (arguments.value.first().evaluate(memory) as KotlinTuple).value[index]
            }
            throw exception
        }

        val evaluatedArguments = arguments.value.map { it.evaluate(memory) }

        val actualFunction = lambdas.lambdas.lastOrNull { lambda ->
            lambda.args.map { it.type }.matchesArgs(evaluatedArguments)
        } ?: throw Exception("No function call found that matches.. No good")

        return actualFunction.invoke(evaluatedArguments)
    }

    private fun KotlinHclExpression.evaluate(memory: Memory): KotlinHclExpression {
        return when (this) {
            is KotlinFunctionCall -> invoke(memory)
            is KotlinIdentifier -> memory[value]!!.evaluate(memory)
            is KotlinList -> KotlinList(value.map { it.evaluate(memory) })
            is KotlinTuple -> KotlinTuple(value.map { it.evaluate(memory) })
            is KotlinLambdaCollection -> lambdas.first()
            else -> this
        }
    }

    private fun AstNode.Type.defaultValue(): KotlinHclExpression = when (this) {
        AstNode.Type.Number -> KotlinNumber(0.0)
        AstNode.Type.Text -> KotlinText("")
        AstNode.Type.Bool -> KotlinBoolean(false)
        is AstNode.Type.List -> KotlinList(listOf())
        is AstNode.Type.Tuple -> KotlinTuple(elementTypes.map { it.defaultValue() })
        else -> KotlinUnit
    }

    private fun AstNode.Command.Declaration.handle(memory: Memory) {
        val functions = memory[identifier.name] as? KotlinLambdaCollection
        if (functions != null) {
            functions.lambdas.add(expression!!.asKotlinExpression(memory) as KotlinLambdaExpression)
            memory[identifier.name] = functions
        } else {
            val expression = expression?.asKotlinExpression(memory)?.let {
                (it as? KotlinLambdaExpression)?.let { KotlinLambdaCollection(mutableListOf(it)) } ?: it
            } ?: type.defaultValue()
            memory[identifier.name] = (expression as? KotlinFunctionCall)?.evaluate(memory) ?: expression
        }
    }

    private fun AstNode.Command.Assignment.handle(memory: Memory) {
        memory.assign(identifier.name, expression.asKotlinExpression(memory).evaluate(memory))
    }

    private fun AstNode.Command.Expression.handle(memory: Memory) {
        when (this) {
            is AstNode.Command.Expression.FunctionCall -> {
                val expression = asKotlinExpression(memory).evaluate(memory)
                if (printExpression && expression != KotlinUnit) println(expression)
            }
            is AstNode.Command.Expression.Value ->
                if (printExpression) println(asKotlinExpression(memory).evaluate(memory))
        }
    }

    private fun AstNode.Command.Return.handle(memory: Memory):
            KotlinHclExpression = expression.asKotlinExpression(memory).evaluate(memory)
}
