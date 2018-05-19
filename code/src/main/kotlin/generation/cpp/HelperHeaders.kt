package generation.cpp

import generation.FilePair

object HelperHeaders {
    val constList = FilePair("ConstList.h", javaClass.classLoader.getResource("ConstList.h").readText())
    val ftoa = FilePair("ftoa.h", javaClass.classLoader.getResource("ftoa.h").readText())
}
