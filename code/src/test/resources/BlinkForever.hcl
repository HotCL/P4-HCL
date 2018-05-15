# TEST_DISABLED
"Blinking" print
{
    2 setDigitalPin true
    1000 delayMillis
    2 setDigitalPin false
    1000 delayMillis
} loop
