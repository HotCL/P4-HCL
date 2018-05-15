# should print [4, 2, 3, 1, 5, 6, 7, 8, 9]

var swap = (list[T] lst, num firstIndex, num secondIndex): list[T] {

    var firstSub = lst subList 0 (firstIndex)
    var secondSub = lst subList (firstIndex + 1) (secondIndex - (firstIndex + 1))
    var lastSub = lst subList (secondIndex + 1) ((lst length) - (secondIndex + 1))
    return firstSub + [(lst at secondIndex),] + secondSub + [(lst at firstIndex)] + lastSub
}

var orgLst = 1 to 9

var newLst = orgLst swap 0 3

newLst print

