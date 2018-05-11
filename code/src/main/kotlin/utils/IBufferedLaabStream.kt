package utils

interface IBufferedLaabStream<out T> {
    val current: T

    fun hasNext(): Boolean
    fun hasAhead(n: Int): Boolean
    fun hasBehind(n: Int): Boolean

    fun moveNext(): T
    fun moveAhead(indexes: Int): T

    fun lookAhead(indexes: Int): T
    fun lookBehind(indexes: Int): T

    fun peek(): T

    fun <U> findElement(getFunc: (T) -> U?, exitCondition: (T) -> Boolean = { false }, startAhead: Int = 0): U?
}
