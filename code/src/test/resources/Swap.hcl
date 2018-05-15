# should print [7, 4, 5, 1, 4, 3, 2, 9]
#KEEP_FILES

var swap = (list[T] lst, num firstIndex, num secondIndex): list[T]{
    var tmpFirst = [(lst at firstIndex)]
    var tmpSecond = [(lst at secondIndex)]

    var firstSub = lst subList 0 (firstIndex - 1)
    var secondSub = lst subList (firstIndex + 1) (secondIndex - (firstIndex + 1))
    var lastSub = lst subList (secondIndex + 1) ((lst length) - (secondIndex + 1))

    return firstSub + tmpSecond + secondSub + tmpFirst + lastSub
}

var orgLst = [1, 4, 5, 7, 4, 3, 2, 9]
var newLst = orgLst swap 0 3

newLst print

