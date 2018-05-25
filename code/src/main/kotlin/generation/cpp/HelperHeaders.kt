package generation.cpp

import generation.FilePair

/**
 * This object includes C++ header files in the output for functionality that cannot be defined in HCL
 * ConstList: The list data structure used in HCL
 * Ftoa: Short for Float to Array - a toString function for the HCL num type
 */
object HelperHeaders {
    val constList = FilePair("ConstList.h", javaClass.classLoader.getResource("ConstList.h").readText())
    val ftoa = FilePair("ftoa.h", javaClass.classLoader.getResource("ftoa.h").readText())
}
