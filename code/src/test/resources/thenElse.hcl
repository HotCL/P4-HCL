#should return 5


var > = :greaterThan

var thenElse = (bool cond, func<T> trueBody, func<T> falseBody): T {
    T output
    cond then { output = trueBody }
    cond not then { output = falseBody }
    return output
}

var x = 21
var y = 20
num bigNum
bigNum = x > y thenElse { x } { y }
RETURN_CODE = bigNum