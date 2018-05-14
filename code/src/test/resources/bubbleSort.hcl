#should return [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]

var swapAtIndex = (T list, num firstIndex, num secondIndex): T{
    var tmpFirst = [(list at firstIndex)]
    var tmpSecond = [(list at secondIndex)]

    var firstSub = list subList 0 (firstIndex - 1)
    var secondSub = list subList (firstIndex + 1) (secondIndex - (firstIndex + 1))
    var lastSub = list subList (second + 1) ((list length) - (second + 1))

    return firstSub + tmpSecond + secondSub + tmpFirst + lastSub
}

var bubbleSort = (T list, num n): T{
    num i = 0
    num j = 0

    {
        {
            list at j greaterThan (list at (j  + 1)) then{
                list = list swapAtIndex j (j  + 1)
            }

            j = j + 1
        } while j lessThan (n - (i  - 1))

        j = 0
        i = i + 1
    } while i lessThan (n - 1)

    return list
}

var list = [9, 8, 7, 6, 5, 4, 3, 2, 1]
var newList = list bubbleSort (list length)
RETURN_CODE = newList