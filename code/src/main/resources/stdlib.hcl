var @ = (list[T] lst, num idx): T { lst at idx }

var any = (list[T] myList, func[T, bool] compareFunc): bool{
	myList where { value compareFunc } length greaterThan 0
}

var all = (list[T] myList, func[T, bool] compareFunc): bool{
	myList where { value compareFunc } length equals (myList length)
}

func firstIndexWhere = (list[T] lst, func[T, bool] predicate): num {
    var ret = -1
    var idx = 0
    lst forEach {
        value predicate and (ret equals -1) then { ret = idx }
        idx = idx + 1
    }
    return ret
}
