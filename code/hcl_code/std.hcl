
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
