# It should print ("Rasmus", 6, "Aabybro")


# Person methods
func createPerson = (txt name, num age, txt city): tuple[txt, num, txt] { (name, age, city) }
func withName = (tuple[txt, num, txt] person, txt name): tuple[txt, num, txt] { (name, person element1, person element2) }
func withAge = (tuple[txt, num, txt] person, num age): tuple[txt, num, txt] { (person element0, age, person element2) }
func incrementAge = (tuple[txt, num, txt] person): tuple[txt, num, txt] { (person element0, person element1 + 1, person element2) }
func movedTo = (tuple[txt, num, txt] person, txt city): tuple[txt, num, txt] { (person element0, person element1, city) }

# Create person
var myPerson = "Thomas" createPerson 21 "Aalborg"

# Modify person
myPerson = myPerson withName "Rasmus"
myPerson = myPerson withAge 5
myPerson = myPerson incrementAge
myPerson = myPerson movedTo "Aabybro"

myPerson print

