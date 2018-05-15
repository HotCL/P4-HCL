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
    curNum * 1 delayMillis
    2 setDigitalPin true
    curNum * 1 delayMillis
    curNum = oldNum nextFib  curNum
    oldNum = tmpPrev
} loop

