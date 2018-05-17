# should return 9

num x

var xPlus = (): none { x = x + 3
}
var call_xPlus = (): none { xPlus }

var f = (): none {
    num x = 9

    var xPlus = (): none {
        x = x + 1
    }

    call_xPlus
    RETURN_CODE = x
}

f
