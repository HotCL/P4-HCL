var createPerson = (txt name, num age, txt location): tuple[txt, num, txt] { (name, age, location) }
var toTextTuple = (tuple[txt, num, txt] value): txt {
    "Person called: " + (value element0) + \
    " who is " + (value element1 toText) + \
    " years old and lives in " + (value element2)
}
var casper = "Casper" createPerson 20 "Exam"
casper toTextTuple print
