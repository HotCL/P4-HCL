import com.natpryce.hamkrest.Matcher
import com.natpryce.hamkrest.and
import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.endsWith
import com.natpryce.hamkrest.startsWith

class  SimpleTest {
    @org.junit.jupiter.api.Test
    fun simpleTest() {
        assert.that("Thomas", startsWith("Tho") and endsWith("s"))
    }

    @org.junit.jupiter.api.Test
    fun simpleTest2() {
        assert.that("Thomas", Matcher(String::isACoolName))
    }
}
fun String.isACoolName(): Boolean = this.contains("Thomas")
