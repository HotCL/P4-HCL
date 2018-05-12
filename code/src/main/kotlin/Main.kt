import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import com.github.ajalt.clikt.parameters.options.versionOption
import com.github.ajalt.clikt.parameters.types.file
import exceptions.CompilationException
import generation.cpp.ProgramGenerator
import lexer.Lexer
import logger.Logger
import parser.Parser
import utils.compileCpp
import kotlin.system.exitProcess

fun main(args: Array<String>) = HCL().main(args)

class HCL : CliktCommand() {
    init {
        versionOption("Version 0.19")
    }
    private val inputFile by option("-c", "--inputFile", help = "HCL input file to be compiled")
            .file(exists = true, folderOkay = false)
            .prompt("HCL input file to be compiled")

    private val outputFile by option("-o", "--outputFile", help = "Name of compiled program")
            .file(folderOkay = false)
            .prompt("Name of compiled program")

    private val deleteCpp by option("-d", "-deleteCpp",
            help = "Whether to delete generated CPP code after compilation has ended")
            .flag(default = true)

    override fun run() {
        val lexer = Lexer(inputFile.readText())
        val parser = Parser(lexer)
        val logger = Logger()
        val ast = try {
            parser.generateAbstractSyntaxTree()
        } catch (exception: CompilationException) {
            logger.logCompilationError(exception)
            exitProcess(-1)
        }

        val programFiles = ProgramGenerator().generate(ast)
        compileCpp(programFiles, "compiled${inputFile.nameWithoutExtension}", deleteCpp, outputFile.name)
    }
}