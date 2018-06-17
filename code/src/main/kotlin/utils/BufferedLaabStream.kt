package utils

/**
 * Implementation of IBufferedLaabStream
 * Allows parser to move through, peek, look ahead, and look behind in token sequence
 */
class BufferedLaabStream<out T>(sequence: Sequence<T>) : IBufferedLaabStream<T> {
    class Buffer<T>(sequence: Sequence<T>) {
        private val buffer = mutableListOf<T>()
        private val iterator = sequence.iterator()
        operator fun get(index: Int): T {
            while (size <= index) buffer.add(iterator.next())
            return buffer[index]
        }

        private val size get() = buffer.size

        fun hasNext(index: Int) = hasAhead(index, 1)
        fun hasAhead(index: Int, n: Int) = try {
            this[index + n]
            true
        } catch (exception: Exception) {
            false
        }
    }
    private val buffer = Buffer(sequence)
    private var index = 0
    override val current: T get() = buffer[index]

    override fun hasNext() = buffer.hasNext(index)
    override fun hasAhead(n: Int) = buffer.hasAhead(index, n)
    override fun hasBehind(n: Int) = 0 <= index - n

    override fun moveNext() = moveAhead(1)
    override fun moveAhead(indexes: Int): T {
        index += indexes
        return buffer[index]
    }

    override fun <U> findElement(getFunc: (T) -> U?, exitCondition: (T) -> Boolean, startAhead: Int): U? {
        var ahead = startAhead
        while (hasAhead(ahead)) {
            val current = lookAhead(ahead++)
            if (exitCondition(current)) break
            val res = getFunc(current)
            if (res != null) return res
        }
        return null
    }

    override fun lookAhead(indexes: Int) = buffer[index + indexes]
    override fun lookBehind(indexes: Int) = buffer[index - indexes]

    override fun peek() = lookAhead(1)
}
