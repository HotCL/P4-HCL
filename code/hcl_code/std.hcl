
var @ = :at
var is = :equals

var to (num from, num to):list[num] {
	num i = from
	list[num] output
	{
		output = output + [i]
		i = i + 1
	} while { i < to }

}

var any (list[T] myList, func[T,bool] compareFunc):bool{
	myList filter :compareFunc length greaterThan 0
}

var all (list[T] myList, func[T,bool] compareFunc):bool{
	myList filter :compareFunc length equals myList length
}

var in = (T element, list[T] myList):bool{
	myList any { value is element }
}

var notIn = (T element, list[T] myList):bool{
	element in myList not
}

var then = (bool condition, func[T] body): tuple[bool,T] {
	return (condition,body)
}

var else = (tuple[bool,T] thenResult, func[T] body): T {
	T output
	thenResult at 0 then { output = thenResult at 1 }
	thenResult at 0 not then { output = body }
	return output
}

var thenElse = (bool condition, func[T] trueBody, func[T] falseBody): T {
    condition then {output = trueBody} else { output = falseBody}
}
