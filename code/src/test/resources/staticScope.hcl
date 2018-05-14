# should return 9

var x
var y

var xPlus = (): none { x = x + 3
}
var call_xPlus = (): none { xPlus }

var f = (): none{
    var x = 9

    var xPlus = (): none {
        x = x + 1
    }

    call_xPlus

    y = x
}

f
RETURN_CODE = y