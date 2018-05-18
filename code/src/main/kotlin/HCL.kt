import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.versionOption
import com.github.ajalt.clikt.parameters.types.file
import exceptions.CompilationException
import generation.GraphvizGenerator
import generation.cpp.ProgramGenerator
import lexer.Lexer
import logger.Logger
import parser.AstNode
import parser.BuiltinLambdaAttributes
import parser.Parser
import stdlib.Stdlib
import utils.compileCpp
import utils.runCommand
import java.io.File
import kotlin.system.exitProcess

class HCL : CliktCommand() {
    init {
        versionOption("Version 0.19")
    }
    private val inputFiles by argument("input_file", help = "HCL input file to be compiled")
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
            val hclParser = Parser(lexer)
            val logger = Logger()
            val ast = try {
                hclParser.generateAbstractSyntaxTree()
            } catch (exception: CompilationException) {
                logger.logCompilationError(exception)
                exitProcess(-1)
            }

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
            compileCpp(programFiles, "compiled${inputFiles.last().nameWithoutExtension}", deleteCpp, actualOutputFile)
        } else {
            val content = StringBuilder()
            while (true) {
                print(">>> ")
                val inputLine = readLine()!!
                val lexer = Lexer(mapOf(Stdlib.getStdlibContent(), "CLI" to content.toString() + "\n" + inputLine))
                val hclParser = Parser(lexer)
                val logger = Logger()
                val ast = try {
                    hclParser.generateAbstractSyntaxTree()
                } catch (exception: CompilationException) {
                    logger.logCompilationError(exception)
                    continue
                }

                val lastElement = ast.children.last()
                if (lastElement is AstNode.Command.Expression) {
                    ast.children[ast.children.size - 1] = AstNode.Command.Expression.FunctionCall(
                            AstNode.Command.Expression.Value.Identifier("print", AstNode.Type.None),
                            listOf(lastElement),
                            listOf(lastElement.type)
                    )
                }

                try {
                    val programFiles = ProgramGenerator().generate(ast)
                    val name = "repl"
                    compileCpp(programFiles, "compiled_$name", deleteCpp, name)
                    File(name).setExecutable(true)
                    println("./$name".runCommand().string)
                    content.appendln(inputLine)
                } catch (exception: Exception) {
                    println(exception)
                }
            }
        }
    }
}
