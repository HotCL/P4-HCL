#ifndef TEST_DELAY_H
#define TEST_DELAY_H


#ifdef ARDUINO_AVR_UNO
void delayMillis(double d) {
    delay((int)round(d));
    return;
}
#else //ARDUINO_AVR_UNO
    #include <cmath>
    #ifdef _WIN32 //If windows based PC
    #include <windows.h>
    void delayMillis(double d) {
        Sleep((unsigned int)round(d));
        return;
    }
    #else //If unix based PC
    #include <unistd.h>
    void delayMillis(double d) {
        usleep(((unsigned int)round(d)) * 1000); //convert microseconds to milliseconds
        return;
    }
    #endif //_WIN32

#endif //ARDUINO_AVR_UNO

#endif //TEST_DELAY_H
