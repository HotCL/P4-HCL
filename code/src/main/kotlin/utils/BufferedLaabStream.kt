package utils

class BufferedLaabStream<out T>(sequence: Sequence<T>): IBufferedLaabStream<T> {
    private val buffer = sequence.toList()
    private var index = 0
    override val current: T get() = buffer[index]

    override fun hasNext() = index < buffer.size - 1
    override fun hasAhead(n: Int) = n + index < buffer.size
    override fun hasBehind(n: Int) = 0 <= index - n

    override fun moveNext() = moveAhead(1)
    override fun moveAhead(indexes: Int): T {
        index += indexes
        return buffer[index]
    }

    override fun lookAhead(indexes: Int) = buffer[index + indexes]
    override fun lookBehind(indexes: Int) = buffer[index - indexes]

    override fun peek() = lookAhead(1)
}
