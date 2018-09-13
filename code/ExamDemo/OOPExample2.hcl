func getName = (tuple[txt, num, txt] person): txt { person element0 }
func getAge = (tuple[txt, num, txt] person): num { person element1 }
func getCity = (tuple[txt, num, txt] person): txt { person element2 }

func asPerson = (txt name): tuple[txt] { (name,) }
func withAge = (tuple[txt] person, num age): tuple[txt, num] { (person element0, age) }
func livingIn = (tuple[txt, num] person, txt city): tuple[txt, num, txt] { (person element0, person element1, city) }

func withName = (tuple[txt, num, txt] person, txt name): tuple[txt, num, txt] { (name, person element1, person element2) }
func movedTo = (tuple[txt, num, txt] person, txt city): tuple[txt, num, txt] { (person element0, person element1, city) }
func turned = (tuple[txt, num, txt] person, num age): tuple[txt, num, txt] { (person element0, age, person element2) }

var toTextTuple = (tuple[txt, num, txt] value): txt {
    "Person called: " + (value element0) + \
    " who is " + (value element1 toText) + \
    " years old and lives in " + (value element2)
}

# Create person
var myPerson = "Thomas" asPerson withAge 21 livingIn "Aalborg"

myPerson print

# Modify person
myPerson = myPerson withName "Casper"
myPerson = myPerson movedTo "København"
myPerson = myPerson turned 20

myPerson toTextTuple print
