# should return 9

var x
var y

<<<<<<< HEAD
var xPlus = (): none { x = x + 3
}
var call_xPlus = (): none { xPlus }

var f = (): none{
    var x = 9

    var xPlus = (): none {
        x = x + 1
    }

    call_xPlus
=======
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
>>>>>>> cea8ab9fc2a06f0ec5742fce23b83705dd758dac

    y = x
}

<<<<<<< HEAD
f
RETURN_CODE = y
=======
fun
y print
>>>>>>> cea8ab9fc2a06f0ec5742fce23b83705dd758dac
