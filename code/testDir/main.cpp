
#include <functional>

#include "ConstList.h"
#include "ftoa.h"

#ifndef ARDUINO_AVR_UNO

#include <iostream>

#endif

#ifdef _WIN32 //If windows based PC
    #include <windows.h>
#else //If unix based PC
    #include <unistd.h>
#endif //_WIN32


using namespace std;

#include "builtin.h"
#include "types.h"
double IDT_0x29786b9c = 0.0;
TPL_0x2663b858 IDT_0x5958357c = IDT_0x8ef41478(1.0, ConstList<char>::string((char *)"hello"));
void setup() { 
    
    #if ARDUINO_AVR_UNO
    Serial.begin(9600);
    #endif
    IDT_0x29786b9c = IDT_0xff80c0b4<double>(IDT_0x5958357c);
}
void loop() {
    
}
#if !ARDUINO_AVR_UNO
int main() {
    setup();
    
    return IDT_0x29786b9c;
}
#endif
