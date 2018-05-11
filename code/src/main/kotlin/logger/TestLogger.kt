package logger

class TestLogger : Logger() {
    val buffer: StringBuilder = StringBuilder()
    override fun writeLine(text: String) {
        buffer.append(text + "\n")
    }
}
