#should return 21



var thenElse = (bool cond, func[T] trueBody, func[T] falseBody): T {
    T output = trueBody
    cond not then { output = falseBody }
    return output
}

var x = 21
var y = 20
num bigNum
bigNum = x greaterThan y thenElse { x } { y }
RETURN_CODE = bigNum