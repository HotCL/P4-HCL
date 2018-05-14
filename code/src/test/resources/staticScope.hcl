# should print 9

var x = 0
var y = 42

var p = (): none {
    x = x + 3
}
var q = (): none {
    p
}
var  f = (): none{
    var x = 9

    var p = (): none {
        x = x + 1
    }

    q

    y = x
}

f
y print