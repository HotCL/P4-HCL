var foo = (txt t, num n, txt ending): txt {
    var output = ""
    1 to n forEach {
        output = output + t
    }
    return output + ending
}

"test " foo 5 "end" print
