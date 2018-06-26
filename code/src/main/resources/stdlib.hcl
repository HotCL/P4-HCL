# Alternative to function "@"
var @ = (list[T] lst, num idx): T { lst at idx }

# Returns whether a predicate applies to any element in list
var any = (list[T] myList, func[T, bool] compareFunc): bool{
	myList where { value compareFunc } length greaterThan 0
}

# Returns whether a predicate applies to all elements in list
var all = (list[T] myList, func[T, bool] compareFunc): bool{
	myList where { value compareFunc } length equals (myList length)
}

var in = (T element, list[T] myList): bool {
	myList any { value equals element }
}

var notIn = (T element, list[T] myList):bool{
	element in myList not
}

# Get first index where predicate applies
# Returns -1 if it doesn't apply to any element
func firstIndexWhere = (list[T] lst, func[T, bool] predicate): num {
    var ret = -1
    var idx = 0
    lst forEach {
        value predicate and (ret equals -1) then { ret = idx }
        idx = idx + 1
    }
    return ret
}

var changeIdx = (list[T] lst, num index, num newValue): list[T] {
    var firstSub = lst splitAt 0 (index)
    var secondSub = lst splitAt (index + 1) ((lst length) - (index + 1))
    return firstSub + [newValue] + secondSub
}

var withoutLast = (list[T] lst): list[T] {
    lst length greaterThan 0 thenElse { lst splitAt 0 (lst length - 1) } {
        list[T] res
        return res
    }
}

var forEachIndexed = (list[T] lst, func[T, num, none] f): none {
    var index = 0
    lst forEach {
        value f index
        index = index + 1
    }
}

var is = (T l, T r): bool { l equals r }
var isNot = (T l, T r): bool { l equals r not }
