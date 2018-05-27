# should print first=1 and last=9

var toUneven = (num start, num stop): list[num] {
    start to stop where { value mod 2 equals 1 }
}

var last = (list[T] myList): T {
    myList at (myList length - 1)
}

var first = (list[T] myList): T {
    myList at 0
}

var numbers = 1 toUneven 10


"first=" + (numbers first toText) + " and last=" + (numbers last toText) print
