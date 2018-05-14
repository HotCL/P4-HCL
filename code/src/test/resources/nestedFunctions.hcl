#should print true

var g = 30
var j = 40

var outer = (num x, num y) : bool {

    var inner = (num z, num h) : bool {
        return z lessThan (h + 10)
    }

    return x inner y
}

var k = g outer j

k print