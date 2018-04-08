package utilTests

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import utils.BufferedLaabStream
import kotlin.coroutines.experimental.buildSequence

class BufferedLaabTests {
    @org.junit.jupiter.api.Test
    fun testBufferedLaabStream() {
        fun testSequence() = buildSequence {
            yield("hej")
            yield("med")
            yield("dig")
            yield("der")
            yield("!")
        }
        val bufferedLaab = BufferedLaabStream(testSequence())
        assertThat(bufferedLaab.current, equalTo("hej"))
        assertThat(bufferedLaab.hasBehind(1), equalTo(false))
        assertThat(bufferedLaab.hasAhead(3), equalTo(true))
        assertThat(bufferedLaab.hasAhead(5), equalTo(false))
        assertThat(bufferedLaab.moveAhead(2), equalTo("dig"))
        assertThat(bufferedLaab.lookBehind(1), equalTo("med"))
        assertThat(bufferedLaab.lookAhead(1), equalTo("der"))
        assertThat(bufferedLaab.hasNext(), equalTo(true))
        assertThat(bufferedLaab.moveNext(), equalTo("der"))
        assertThat(bufferedLaab.hasNext(), equalTo(true))
        assertThat(bufferedLaab.moveNext(), equalTo("!"))
        assertThat(bufferedLaab.hasNext(), equalTo(false))
    }
}