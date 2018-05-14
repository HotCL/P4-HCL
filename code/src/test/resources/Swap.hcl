# should print [7, 4, 5, 1, 4, 3, 2, 9]

var swap = (T lst, num firstIndex, num secondIndex): T{
    var tmpFirst = [(lst at firstIndex)]
    var tmpSecond = [(lst at secondIndex)]

    var firstSub = lst subList 0 (firstIndex - 1)
    var secondSub = lst subList (firstIndex + 1) (secondIndex - (firstIndex + 1))
    var lastSub = lst subList (second + 1) ((lst length) - (second + 1))

    return firstSub + tmpSecond + secondSub + tmpFirst + lastSub
}

var lst = [1, 4, 5, 7, 4, 3, 2, 9]
var newLst = lst swap 0 3

newLst toTxt print

