#should print ["1", "2", "fizz", "4", "buzz", "fizz", "7", "8", "fizz", "buzz", "11", "fizz", "13", "14", "fizzbuzz"]

var fizzbuzz = (num v): txt {
    txt toPrint
	(v mod 3 equals 0) then { toPrint = toPrint + "fizz" }
	(v mod 5 equals 0) then { toPrint = toPrint + "buzz" }
    toPrint equals "" then { toPrint = v toText }
    return toPrint


}

1 to 15 map { value fizzbuzz } print

RETURN_CODE = 0
