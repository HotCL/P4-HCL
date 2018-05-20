package generation.cpp

import generation.IPrinter
import generation.makePretty
import parser.AbstractSyntaxTree
import parser.AstNode
import parser.typechecker.TypeChecker

/**
 * Generates the "body" of the program with a setup and a loop method and all the logic of the program
 */
class CodeGenerator : IPrinter {
    private var indents = 0
    override fun generate(ast: AbstractSyntaxTree): String {
        indents = 0
        return ast.children.format()
    }

    private fun List<AstNode.Command>.format() = joinToString("\n") {
        // generate lists if needed
        val literalLists = it.fetchList()
        (if (literalLists.count() > 0) // TODO MAYBE SHOULD BE CONSTANT?
            literalLists.joinToString("\n") {
                "${it.innerType.cppName} ${it.cppName}[] = {${it.elements.formatToList()}};\n"
            } else "") + it.format()
    }

    // private fun Iterable<AstNode.Command.Expression>.format() = joinToString(",") { it.format() }

    private fun AstNode.Command.format(): String =
            when (this) {
                is AstNode.Command.Declaration -> format()
                is AstNode.Command.Assignment -> format()
                is AstNode.Command.Expression -> (format() + ";\n").indented
                is AstNode.Command.Return -> "return ${this.expression.format()};\n".indented
                is AstNode.Command.RawCpp ->
                    content.split("\n").joinToString("") { (it + "\n").indented }
            }

    private fun AstNode.Command.Assignment.format() =
            " // Assignment for ${identifier.name}\n".indented +
            "${identifier.cppName} = ${expression.format()};\n".indented

    private fun AstNode.Command.Declaration.format(): String = when (expression) {
        is AstNode.Command.Expression.LambdaExpression -> expression.formatAsDecl(this)
        else ->
            "// Declaration for ${identifier.name}\n".indented +
            ("${type.cppName} ${identifier.cppName} = " + (expression?.format() ?: type.defaultValue) + ";").indented
    }

    private val AstNode.Type.defaultValue get(): String = when (this) {
        AstNode.Type.Number -> "0"
        AstNode.Type.Text -> "ConstList<char>::string((char *)\"\")"
        AstNode.Type.Bool -> "false"
        is AstNode.Type.List -> "ConstList<${elementType.cppName}>::create_from_copy(nullptr, 0)"
        is AstNode.Type.Func -> "nullptr"
        is AstNode.Type.Tuple -> "${"create_struct".cppName}(${ this.elementTypes.joinToString { it.defaultValue } })"

        else -> throw TypeHasNoDefaultValue()
    }

    private fun AstNode.Command.Expression.LambdaExpression.formatAsDecl(decl: AstNode.Command.Declaration) =
        templateLine(paramDeclarations.flatMap { it.type.getGenerics } + returnType.getGenerics) +
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
            val type = AstNode.Type.Func(paramDeclarations.map { it.type }, returnType)
            appendln("${type.cppName} ${decl.identifier.cppName} = ".indented + format() + ";")
        }

    private fun List<AstNode.ParameterDeclaration>.format(modifyParametersNames: Boolean) = joinToString {
        "${it.type.cppName} ${if (modifyParametersNames) it.identifier.cppName else it.identifier.name}"
    }

    private fun List<AstNode.Command.Expression>.formatToList() = this.joinToString { it.format() }

    private fun AstNode.Command.Expression.format(): String =
        when (this) {
            is AstNode.Command.Expression.Value.Identifier -> cppName
            is AstNode.Command.Expression.Value.Literal.Number -> "$value"
            is AstNode.Command.Expression.Value.Literal.Text -> "ConstList<char>::string((char *)\"$value\")"
            is AstNode.Command.Expression.Value.Literal.Bool -> "$value"
            is AstNode.Command.Expression.Value.Literal.Tuple ->
                "${"create_struct".cppName}(${this.elements.formatToList()})"
            is AstNode.Command.Expression.Value.Literal.List ->
                "ConstList<${(this.type as AstNode.Type.List).elementType.cppName}>::create_from_copy(${this.cppName}, " +
                        "${this.elements.count()})"
            is AstNode.Command.Expression.LambdaExpression ->
                "[&](${paramDeclarations.format(attributes.modifyParameterName)}) {\n".also { indents++ } +
                    body.commands.format() +
                    ("}".indentedPreDec)
            is AstNode.Command.Expression.LambdaBody -> TODO()
            is AstNode.Command.Expression.FunctionCall ->
                "${identifier.cppName}$genericTemplateArguments(${arguments.formatToList()})"
        }

    private val AstNode.Command.Expression.FunctionCall.genericTemplateArguments get(): String {
        val generics = expectedArgumentTypes.flatMap { it.getGenerics }.toSet()
        return if (generics.isEmpty()) "" else {
            val pairedGenerics = expectedArgumentTypes.zip(arguments.map { it.type })
            val filteredGenerics = pairedGenerics.filter {
                !(it.first is AstNode.Type.GenericType && it.first == it.second)
            }
            if (filteredGenerics.isNotEmpty()) {
                "<" + generics.joinToString {
                    TypeChecker().getTypeFromTypePairs(filteredGenerics, it.name)!!.cppName
                } + ">"
            } else ""
        }
    }

    private fun List<AstNode>.fetchLists(): Set<AstNode.Command.Expression.Value.Literal.List> =
        flatMap { it.fetchList() }.toSet()

    private fun AstNode.fetchList(): Set<AstNode.Command.Expression.Value.Literal.List> = when (this) {
        is AstNode.Command.Expression.FunctionCall -> arguments.fetchLists()
        is AstNode.Command.Assignment -> expression.fetchList()
        is AstNode.Command.Declaration -> type.fetchList() + (this.expression?.fetchList() ?: emptySet())
        is AstNode.Command.Return -> expression.fetchList()
        is AstNode.Command.Expression.Value.Literal.List -> setOf(this)
        else -> emptySet()
    }

    private fun templateLine(generics: List<AstNode.Type.GenericType>) =
        if (generics.isEmpty()) ""
        else "template <" + generics.toSet().joinToString { "typename ${it.name}" } + ">\n"

    private fun AstNode.Command.Expression.LambdaExpression.toComment(decl: AstNode.Command.Declaration) =
        "${decl.identifier.name}(${paramDeclarations.joinToString {
            "${it.type.makePretty()} ${it.identifier.name}" }
        }) -> ${decl.type.makePretty()}"

    private val String.indented get() = "    " * indents + this
    private val String.indentedPostInc get() = indented.also { indents ++ }
    private val String.indentedPreDec get() = (--indents).run { indented }
    private fun buildString(f: StringBuilder.() -> Unit) = StringBuilder().apply(f).toString()
}
