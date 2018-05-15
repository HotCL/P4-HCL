#should return [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
#KEEP_FILES
var swapAtIndex = (list[T] lst, num firstIndex, num secondIndex): list[T]{
    var tmpFirst = [(lst at firstIndex)]
    var tmpSecond = [(lst at secondIndex)]

    var firstSub = lst subList 0 (firstIndex - 1)
    var secondSub = lst subList (firstIndex + 1) (secondIndex - (firstIndex + 1))
    var lastSub = lst subList (secondIndex + 1) ((lst length) - (secondIndex + 1))

    return firstSub + tmpSecond + secondSub + tmpFirst + lastSub
}

var bubbleSort = (list[T] lst, num n): list[T]{
    num i = 0
    num j = 0

    {
        {
            lst at j greaterThan (lst at (j  + 1)) then{
                lst = lst swapAtIndex j (j  + 1)
            }

            j = j + 1
        } while j lessThan (n - (i  - 1))

        j = 0
        i = i + 1
    } while i lessThan (n - 1)

    return lst
}

var lst = [9, 8, 7, 6, 5, 4, 3, 2, 1]
var newList = lst bubbleSort (lst length)
RETURN_CODE = newList