# should print first=1 and last=9

var toUneven = (num start, num stop): list[num] {
    from to stop filter { value mod 2 equals 1 }
}

var last = (list[T] myList): T {
    mylist at (myList length - 1)
}

var first = (list[T] myList): T {
    mylist at 0
}

var numbers = 1 toUneven 10


"first="+(numbers first) + " and last=" + (numbers last) print