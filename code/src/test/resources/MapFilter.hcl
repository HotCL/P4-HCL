# This should print [12.000, 16.000]
# TEST_DISABLED
[2, 4, 6, 8] map { value * 2 } filter { value greaterThan 10 } print
