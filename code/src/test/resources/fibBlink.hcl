# TEST_DISABLED

var nextFib = (num prev, num cur) : num {
    return prev + cur
}

num oldNum = 0
num curNum  = 1
{
    num tmpPrev = curNum
    "#" + (curNum toText) print
    curNum * 100 delayMillis
    curNum = oldNum nextFib  curNum
    oldNum = tmpPrev
} loop

