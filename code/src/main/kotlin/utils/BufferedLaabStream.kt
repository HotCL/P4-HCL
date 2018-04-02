package utils

class BufferedLaabStream<out T>(sequence: Sequence<T>) {
    private val buffer = sequence.toList()
    private var index = 0
    val current: T get() = buffer[index]

    fun hasNext() = index < buffer.size - 1
    fun hasAhead(n: Int) = n + index < buffer.size
    fun hasBehind(n: Int) = 0 <= index - n

    fun moveNext() = moveAhead(1)
    fun moveAhead(indexes: Int): T {
        index += indexes
        return buffer[index]
    }

    fun lookAhead(indexes: Int) = buffer[index + indexes]
    fun lookBehind(indexes: Int) = buffer[index - indexes]

    fun peek() = lookAhead(1)
}