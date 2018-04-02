package logger

import exceptions.ParserException

class TypeError(lineNumber: Int, lineIndex: Int, lineText: String, operator: String, operands: List<String>)
    : ParserException(lineNumber, lineIndex, lineText){
    private val _operator = operator
    private val _operands = operands
    private val _types: String = findTypes()

    override val errorType: String
        get() = "TYPE-ERROR"
    override val errorMessage: String
        get() = "Function '$_operator' not defined for types: $_types."
    override val helpText: String
        get() = "Try casting your types to match eachother."

    private fun findTypes(): String{
        var s = ""
        var count = _operands.count()
        if(count > 0){
            s = "'${_operands[count-1]}'"
            count--
        }
        while (count > 0){
            if (count == 1) {
                s = s + " and '${_operands[count - 1]}'"
                count--
            }
            else{
                s = s + ", '${_operands[count - 1]}'"
                count--
            }
        }
        return s
    }
}
