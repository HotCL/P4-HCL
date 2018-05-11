package hclCodeTests

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class ReadResources {
    @Throws(IOException::class)
    fun getResourceFiles(path: String): List<String> {
        val filenames = mutableListOf<String>()

        getResourceAsStream(path).use({ stream: InputStream ->
            BufferedReader(InputStreamReader(stream)).use { br ->
                var resource: String?
                do {
                    resource = br.readLine()
                    if (resource != null)
                        filenames.add(resource)
                } while (resource != null)
            }
        })
        return filenames
    }

    fun readResourceFile(file: String) = this::class.java.getResourceAsStream(file)

    private fun getResourceAsStream(resource: String): InputStream {
        val stream: InputStream? = Thread.currentThread().contextClassLoader.getResourceAsStream(resource)
        return stream ?: javaClass.getResourceAsStream(resource)
    }
}