# should print 9

var x = 0
var y = 42

var foo = (): none {
    x = x + 3
}
var bar = (): none {
    foo
}
var fun = (): none{
    var x = 9

    var foo = (): none {
        x = x + 1
    }

    bar

    y = x
}

fun
y print