# TEST_DISABLED
num s = 300
num o = 800

func character = (num speed) : none {
    13 setDigitalPin True
    speed delay
    13 setDigitalPin False
    300 delay
}

{
    [1, 2, 3] forEach { s character }
    [1, 2, 3] forEach { o character }
    [1, 2, 3] forEach { s character }
    200 delay
} loop