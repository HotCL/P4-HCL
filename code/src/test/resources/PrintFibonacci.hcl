# This should print 1 1 2 3 5 8
6 fib print

func fib = (num n) : num {
    (n lessThanEqual 2) then { return 1 } then { return (n-1) fib + (n-2) fib }
}

func printFib = (num times) : none {
    num i = 1
    1 to times each {
        value fib print
    }
}