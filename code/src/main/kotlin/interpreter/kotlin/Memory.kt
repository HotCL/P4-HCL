package interpreter.kotlin

class Memory(private val memory: MutableList<MutableMap<String, KotlinHclExpression>> = mutableListOf(mutableMapOf())) {

    fun pushScope() {
        memory.add(mutableMapOf())
    }

    fun popScope() {
        memory.removeAt(memory.lastIndex)
    }

    fun assign(id: String, value: KotlinHclExpression) {
        memory.last { it.containsKey(id) }[id] = value
    }

    operator fun set(id: String, value: KotlinHclExpression) {
        memory.last()[id] = value
    }

    operator fun get(id: String): KotlinHclExpression? {
        val element = memory.lastOrNull { it.containsKey(id) }?.get(id) ?: return null
        element as? KotlinLambdaCollection ?: return element
        return KotlinLambdaCollection(memory.flatMap { (it[id] as? KotlinLambdaCollection)?.lambdas
            ?: emptyList<KotlinLambdaExpression>() }.toMutableList())
    }

    fun shallowCopy() = Memory(memory.toList().toMutableList())
}