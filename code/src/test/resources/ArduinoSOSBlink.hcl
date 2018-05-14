# TEST_DISABLED
num s = 300
num o = 800

func character = (num speed) : none {
    13 setDigital True
    speed delayMillis
    13 setDigital False
    300 delayMillis
}

{
    [1, 2, 3] each { s character }
    [1, 2, 3] each { o character }
    [1, 2, 3] each { s character }
    200 delayMillis
} loop