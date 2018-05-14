# should print [7, 4, 5, 1, 4, 3, 2, 9]

swap(T list, num firstIndex, num secondIndex): T{
    var tmpFirst = [(list at firstIndex)]
    var tmpSecond = [(list at secondIndex)]

    var firstSub = list subList 0 (firstIndex - 1)
    var secondSub = list subList (firstIndex + 1) (secondIndex - (firstIndex + 1))
    var lastSub = list subList (second + 1) ((list length) - (second + 1))

    return firstSub + tmpSecond + secondSub + tmpFirst + lastSub
}

var lst = [1, 4, 5, 7, 4, 3, 2, 9]
var newLst = lst swap 0 3

newLst toTxt print

