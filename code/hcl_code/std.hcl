var then = (bool condition, func[T] body): tuple[bool,T] {
	return (condition, body)
}

var else = (tuple[bool, T] thenResult, func[T] body): T {
	T output
	thenResult element0 then { output = thenResult element1 }
	thenResult element0 not then { output = body }
	return output
}

var thenElse = (bool condition, func[T] trueBody, func[T] falseBody): T {
    T output
    condition then {output = trueBody} else { output = falseBody}
    return output
}
