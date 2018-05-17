import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
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
import utils.compileCpp
import utils.runCommand
import java.io.File
import kotlin.system.exitProcess

class HCL : CliktCommand() {
    init {
        versionOption("Version 0.19")
    }
    private val inputFile by argument("input_file", help = "HCL input file to be compiled")
            .file(exists = true, folderOkay = false).optional()

    private val outputFile by option("-o", "--outputFile", help = "Name of compiled program")
            .file(folderOkay = false).default(File(inputFile?.nameWithoutExtension ?: "program"))

    private val deleteCpp by option("-d", "--deleteCpp",
            help = "Whether to delete generated CPP code after compilation has ended")
            .flag(default = true)



    override fun run() {
        if (inputFile != null) {
            val lexer = Lexer(inputFile!!.readText())
            val hclParser = Parser(lexer)
            val logger = Logger()
            val ast = try {
                hclParser.generateAbstractSyntaxTree()
            } catch (exception: CompilationException) {
                logger.logCompilationError(exception)
                exitProcess(-1)
            }

            val graph = GraphvizGenerator().generate(ast.filter {
                val decl = it as? AstNode.Command.Declaration ?: return@filter true

                val lmbdExpr = decl.expression as?
                    AstNode.Command.Expression.LambdaExpression ?: return@filter true

                lmbdExpr.attributes != BuiltinLambdaAttributes
            })
            File("$outputFile.gviz").writeText(graph)
            val pngData = "dot -Tpng $outputFile.gviz".runCommand().string


            File("$outputFile.png").writeBytes(pngData.toByteArray())

            val programFiles = ProgramGenerator().generate(ast)
            compileCpp(programFiles, "compiled${inputFile!!.nameWithoutExtension}", deleteCpp, outputFile!!.name)
        } else {
            val content = StringBuilder()
            while (true) {
                print(">>> ")
                val inputLine = readLine()!!
                val lexer = Lexer(content.toString() + "\n" + inputLine)
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
