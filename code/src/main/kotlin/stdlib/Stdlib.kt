package stdlib

object Stdlib {
    fun getStdlibContent() = "stdlib.hcl" to javaClass.classLoader.getResource("stdlib.hcl").readText()
}
