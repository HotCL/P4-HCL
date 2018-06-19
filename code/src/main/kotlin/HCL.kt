import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.versionOption
import com.github.ajalt.clikt.parameters.types.file
import exceptions.CompilationException
import generation.GraphvizGenerator
import generation.cpp.ProgramGenerator
import interpreter.kotlin.KtInterpreter
import lexer.Lexer
import logger.Logger
import org.jline.reader.LineReaderBuilder
import org.jline.reader.UserInterruptException
import parser.AstNode
import parser.BuiltinLambdaAttributes
import parser.cpp.CppParser
import parser.kotlin.KtParser
import stdlib.Stdlib
import utils.compileCpp
import utils.runCommand
import java.io.File
import kotlin.coroutines.experimental.buildSequence
import kotlin.system.exitProcess

/**
 * Main class responsible for overall compilation of HCL
 */
class HCL : CliktCommand() {
    init {
        versionOption("Version 0.19")
    }

    private val inputFiles by argument("input_files", help = "HCL input files to be compiled")
            .file(exists = true, folderOkay = false).multiple(false)

    private val outputFile by option("-o", "--outputFile", help = "Name of compiled program")
            .file(folderOkay = false)

    private val deleteCpp by option("-d", "--deleteCpp",
            help = "Whether to delete generated CPP code after compilation has ended")
            .flag("-k", "--keepCpp", default = true)

    private val generateGraphviz by option("--genGviz",
            help = "Whether to generated graphviz images for the AST")
            .flag(default = false)

    override fun run() {
        if (inputFiles.isNotEmpty()) {
            val actualOutputFile = outputFile?.name ?: inputFiles.last().nameWithoutExtension

            val lexer = Lexer(
                    mapOf(
                            Stdlib.getStdlibContent()
                    ) + inputFiles.map { it.nameWithoutExtension to it.readText() }
            )
            val parser = if (outputFile != null) CppParser(lexer) else KtParser(lexer)
            val logger = Logger()

            try {
                when (parser) {
                    is KtParser -> exitProcess(KtInterpreter(parser).run())
                    is CppParser -> parser.cppAst().let { ast ->
                        if (generateGraphviz) {
                            val graph = GraphvizGenerator().generate(ast.filter {
                                val decl = it as? AstNode.Command.Declaration ?: return@filter true

                                val lmbdExpr = decl.expression as?
                                        AstNode.Command.Expression.LambdaExpression ?: return@filter true

                                lmbdExpr.attributes != BuiltinLambdaAttributes
                            })
                            File("$actualOutputFile.gviz").writeText(graph)
                            val pngData = "dot -Tpng $actualOutputFile.gviz".runCommand().string
                            File("$actualOutputFile.png").writeBytes(pngData.toByteArray())
                        }

                        val programFiles = ProgramGenerator().generate(ast)
                        compileCpp(programFiles,
                                "compiled${inputFiles.last().nameWithoutExtension}", deleteCpp, actualOutputFile)
                    }
                }
            } catch (exception: CompilationException) {
                logger.logCompilationError(exception)
                exitProcess(-1)
            }
        } else {
            REPL().start()
        }
    }
}
