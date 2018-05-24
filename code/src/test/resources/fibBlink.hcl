# TEST_DISABLED

var nextFib = (num prev, num cur) : num {
    return prev + cur
}
"starting...\n" print
num oldNum = 0
num curNum  = 1
{
    num tmpPrev = curNum
    2 setDigitalPin false
    "#" + (curNum toText) + "\n" print
    curNum * 1 delay
    2 setDigitalPin true
    curNum * 1 delay
    curNum = oldNum nextFib curNum
    oldNum = tmpPrev
} loop

