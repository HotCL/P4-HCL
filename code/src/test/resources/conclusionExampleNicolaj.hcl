# It should print [1, 4, 9, 16, 25]
func squared = (num v) : num  { return v * v }
1 to 5 map { value squared } print