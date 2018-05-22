# This should print [1, 1, 2, 3, 5, 8]

func fib = (num n) : num {
    var val = 0
    (n lessThanEqual 2) then { val = 1 }
    (n lessThanEqual 2) not then { val = ((n - 1) fib) + ((n - 2) fib) }
    return val
}

1 to 6 map { value fib } print
