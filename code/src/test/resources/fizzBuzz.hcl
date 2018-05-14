#should print ["1","2","fizz","4","buzz","fizz","7","8","fizz","buzz","11","fizz","13","14","fizzbuzz"]


var fizzbuzz = (num v, txt t){
    txt toPrint = ""
	(v mod 3 is 0) then toPrint = toPrint + "fizz"
	(v mod 5 is 0) then toPrint = toPrint + "buzz"
    toPrint is "" then toPrint = v toText
    return toPrint
	}

}
1..15 map { value fizzbuzz } print