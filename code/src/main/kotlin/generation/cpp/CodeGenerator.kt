package generation.cpp

import generation.IPrinter
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

    private fun AstNode.Command.format() =
            when(this) {
                is AstNode.Command.Declaration -> format()
                is AstNode.Command.Assignment -> format()
                is AstNode.Command.Expression.Value.Identifier -> this.cpp
                is AstNode.Command.Expression.Value.Literal.Number -> "$value"
                is AstNode.Command.Expression.Value.Literal.Text -> "\"$value\""
                is AstNode.Command.Expression.Value.Literal.Bool -> "$value"
                is AstNode.Command.Expression.Value.Literal.Tuple -> TODO()
                is AstNode.Command.Expression.Value.Literal.List -> "[${elements.joinToString { format() }}]"
                is AstNode.Command.Expression.LambdaExpression -> TODO()
                is AstNode.Command.Expression.LambdaBody -> TODO()
                is AstNode.Command.Expression.FunctionCall -> format()
                is AstNode.Command.Return -> "return ${this.expression.format()};\n".indented
                is AstNode.Command.RawCpp -> content.split("\n").joinToString("") { (it + "\n").indented }
            }

    private fun AstNode.Command.Assignment.format() = "${identifier.cpp} = ${expression.format()};".indented

    private fun AstNode.Command.Declaration.format(): String = when (expression) {
            is AstNode.Command.Expression.LambdaExpression -> expression.formatAsDecl(this)
            is AstNode.Command.Expression.Value.Literal.List -> TODO()
            is AstNode.Command.Expression.Value.Literal.Tuple -> TODO()
            else -> "${type.cpp} ${identifier.cpp} = " + (expression?.format() ?: type.defaultValue) + ";"
        }

    private val AstNode.Type.defaultValue get (): String = when (this) {
        AstNode.Type.Number -> "0"
        AstNode.Type.Text -> "\"\""
        AstNode.Type.Bool -> "false"
        is AstNode.Type.List -> "ConstList<${elementType.cpp}>::create(nullptr, 0)"
        is AstNode.Type.Func.ExplicitFunc -> "nullptr"
        is AstNode.Type.Tuple -> "{${ this.elementTypes.joinToString { it.defaultValue } }}"
        else -> throw Exception("Unable to determine default value of type: $this")
    }

    private fun AstNode.Command.Expression.LambdaExpression.formatAsDecl(decl: AstNode.Command.Declaration) =
        templateLine(returnType.getGenerics + this.paramDeclarations.flatMap { it.type.getGenerics }) +
        if (attributes.inLine) formatAsDeclInline(decl) else formatAsDeclDefault(decl)

    private fun AstNode.Command.Expression.LambdaExpression.formatAsDeclInline(decl: AstNode.Command.Declaration) =
        buildString {
            appendln("// Built in function for ${decl.identifier.name}".indented)
            appendln(("inline ${returnType.cpp} FUN_${decl.identifier.cpp} " +
            "(${paramDeclarations.format(attributes.modifyParameterName)}) {").indentedPostInc)
            append(body.commands.format())
            appendln("}".indentedPreDec)
        }

    private fun AstNode.Command.Expression.LambdaExpression.formatAsDeclDefault(decl: AstNode.Command.Declaration) =
        buildString {
            appendln("// Lambda function for name ${decl.identifier.name}".indented)
            val type = AstNode.Type.Func.ExplicitFunc(paramDeclarations.map { it.type }, returnType)
            appendln("${type.cpp} FUN_${decl.identifier.cpp} = ".indented + format())
        }

    private fun List<AstNode.ParameterDeclaration>.format(modifyParametersNames: Boolean) = joinToString {
        "${it.type.cpp} ${if (modifyParametersNames) it.identifier.cpp else it.identifier.name}"
    }

    private fun AstNode.Command.Expression.format(): String =
        when (this) {
            is AstNode.Command.Expression.Value.Identifier -> cpp
            is AstNode.Command.Expression.Value.Literal.Number -> "$value"
            is AstNode.Command.Expression.Value.Literal.Text -> "\"$value\""
            is AstNode.Command.Expression.Value.Literal.Bool -> "$value"
            is AstNode.Command.Expression.Value.Literal.Tuple -> TODO()
            is AstNode.Command.Expression.Value.Literal.List -> TODO()
            is AstNode.Command.Expression.LambdaExpression ->
                "[&](${paramDeclarations.format(attributes.modifyParameterName)}) {\n".also { indents++ } +
                body.commands.format() +
                ("}".indentedPreDec)
            is AstNode.Command.Expression.LambdaBody -> TODO()
            is AstNode.Command.Expression.FunctionCall ->
                "FUN_${this.identifier.cpp}(${this.arguments.joinToString { it.format() }})"
        }

    private fun templateLine(generics: List<AstNode.Type.GenericType>) =
        if (generics.isEmpty()) ""
        else "template <" + generics.joinToString { "typename ${it.name}" } + ">\n"

    private val String.indented get () = "    " * indents + this
    private val String.indentedPostInc get () = indented.also { indents ++ }
    private val String.indentedPreDec get () = (--indents).run { indented }
    private fun buildString(f: StringBuilder.() -> Unit) = StringBuilder().apply(f).toString()
}
