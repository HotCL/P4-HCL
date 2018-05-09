package generation.cpp

import generation.IPrinter
import generation.makePretty
import parser.AbstractSyntaxTree
import parser.AstNode

/**
 * Generates the "body" of the program with a setup and a loop method and all the logic of the program
 */
class CodeGenerator : IPrinter {
    private var indents = 0
    override fun generate(ast: AbstractSyntaxTree): String {
        indents = 0
        return ast.children.format()
    }

    private fun List<AstNode.Command>.format() = joinToString("\n") { it.format() }

    //private fun Iterable<AstNode.Command.Expression>.format() = joinToString(",") { it.format() }

    private fun AstNode.Command.format(): String =
            when(this) {
                is AstNode.Command.Declaration -> format()
                is AstNode.Command.Assignment -> format()
                is AstNode.Command.Expression -> format()
                is AstNode.Command.Return -> "return ${this.expression.format()};\n".indented
                is AstNode.Command.RawCpp ->
                    content.split("\n").joinToString("") { (it + "\n").indented }
            }

    private fun AstNode.Command.Assignment.format() = "${identifier.cppName} = ${expression.format()};".indented

    private fun AstNode.Command.Declaration.format(): String = when (expression) {
            is AstNode.Command.Expression.LambdaExpression -> expression.formatAsDecl(this)
            else -> "${type.cppName} ${identifier.cppName} = " + (expression?.format() ?: type.defaultValue) + ";"
        }

    private val AstNode.Type.defaultValue get (): String = when (this) {
        AstNode.Type.Number -> "0"
        AstNode.Type.Text -> "\"\""
        AstNode.Type.Bool -> "false"
        is AstNode.Type.List -> "ConstList<${elementType.cppName}>::create(nullptr, 0)"
        is AstNode.Type.Func.ExplicitFunc -> "nullptr"
        is AstNode.Type.Tuple -> "{${ this.elementTypes.joinToString { it.defaultValue } }}"
        is AstNode.Type.GenericType -> "nullptr"
        else -> throw Exception("Unable to determine default value of type: $this")
    }

    private fun AstNode.Command.Expression.LambdaExpression.formatAsDecl(decl: AstNode.Command.Declaration) =
        templateLine(returnType.getGenerics + this.paramDeclarations.flatMap { it.type.getGenerics }) +
        if (attributes.inLine) formatAsDeclInline(decl) else formatAsDeclDefault(decl)

    private fun AstNode.Command.Expression.LambdaExpression.formatAsDeclInline(decl: AstNode.Command.Declaration) =
        buildString {
            appendln("// Built in function for ${toComment(decl)}".indented)
            appendln(("inline ${returnType.cppName} ${decl.identifier.cppName} " +
            "(${paramDeclarations.format(attributes.modifyParameterName)}) {").indentedPostInc)
            append(body.commands.format())
            appendln("}".indentedPreDec)
        }

    private fun AstNode.Command.Expression.LambdaExpression.formatAsDeclDefault(decl: AstNode.Command.Declaration) =
        buildString {
            appendln(("// Lambda function for name ${toComment(decl)}").indented)
            val type =
                    AstNode.Type.Func.ExplicitFunc(paramDeclarations.map { it.type }, returnType)
            appendln("${type.cppName} ${decl.identifier.cppName} = ".indented + format() + ";")
        }

    private fun List<AstNode.ParameterDeclaration>.format(modifyParametersNames: Boolean) = joinToString {
        "${it.type.cppName} ${if (modifyParametersNames) it.identifier.cppName else it.identifier.name}"
    }

    private fun AstNode.Command.Expression.format(): String =
        when (this) {
            is AstNode.Command.Expression.Value.Identifier -> cppName
            is AstNode.Command.Expression.Value.Literal.Number -> "$value"
            is AstNode.Command.Expression.Value.Literal.Text -> "\"$value\""
            is AstNode.Command.Expression.Value.Literal.Bool -> "$value"
            is AstNode.Command.Expression.Value.Literal.Tuple -> "{}"
            is AstNode.Command.Expression.Value.Literal.List ->
                "ConstList<${(this.type as AstNode.Type.List).elementType.cppName}>::create(${this.cppName})"
            is AstNode.Command.Expression.LambdaExpression ->
                "[&](${paramDeclarations.format(attributes.modifyParameterName)}) {\n".also { indents++ } +
                body.commands.format() +
                ("}".indentedPreDec)
            is AstNode.Command.Expression.LambdaBody -> TODO()
            is AstNode.Command.Expression.FunctionCall ->
                "${this.identifier.cppName}(${this.arguments.joinToString { it.format() }})"
        }


    private fun templateLine(generics: List<AstNode.Type.GenericType>) =
        if (generics.isEmpty()) ""
        else "template <" + generics.toSet().joinToString { "typename ${it.name}" } + ">\n"

    private fun AstNode.Command.Expression.LambdaExpression.toComment(decl: AstNode.Command.Declaration) =
            "${decl.identifier.name}(${paramDeclarations.joinToString {
                "${it.type.makePretty()} ${it.identifier.name}" }
            }) ->${decl.type.makePretty()}"

    private val String.indented get () = "    " * indents + this
    private val String.indentedPostInc get () = indented.also { indents ++ }
    private val String.indentedPreDec get () = (--indents).run { indented }
    private fun buildString(f: StringBuilder.() -> Unit) = StringBuilder().apply(f).toString()
}
