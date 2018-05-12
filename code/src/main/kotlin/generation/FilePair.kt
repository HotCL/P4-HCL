package generation

import java.io.File

data class FilePair(val fileName: String, val content: String) {
    fun writeFile(dir: String) = File("$dir/$fileName").writeText(content)
}