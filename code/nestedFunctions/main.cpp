
#include <functional>

#include "ConstList.h"
#include "ftoa.h"

#ifndef ARDUINO_AVR_UNO

#include <iostream>
#include <functional>

#endif

#ifdef _WIN32 //If windows based PC
    #include <windows.h>
#else //If unix based PC
    #include <unistd.h>
#endif //_WIN32


using namespace std;

#include "builtin.h"
#include "types.h"
double IDT_0x29786b9c = 0;
double IDT_0x67 = 0;
double IDT_0x6a = 0;
// Lambda function for name outer(num x, num y) -> func[num, num, bool]
function<bool(double, double)> IDT_0x653207b = [&](double IDT_0x78, double IDT_0x79) {
    // Lambda function for name inner(num z, num h) -> func[num, num, bool]
    function<bool(double, double)> IDT_0x5fb4e56 = [&](double IDT_0x7a, double IDT_0x68) {
        return IDT_0x7c8deeda(IDT_0x7a, IDT_0x2b(IDT_0x68, 10.0));
    };

    return IDT_0x5fb4e56(IDT_0x78, IDT_0x79);
};

bool IDT_0x6b = false;
void setup() { 
    
    #ifdef ARDUINO_AVR_UNO
    Serial.begin(9600); // 9600 is the baud rate - must be the same rate used for monitor
    while(!Serial);     // Wait for Serial to initialize
    #endif
    IDT_0x29786b9c = 0.0;
    IDT_0x67 = 30.0;
    IDT_0x6a = 40.0;
    IDT_0x6b = IDT_0x653207b(IDT_0x67, IDT_0x6a);
    IDT_0x65fb2ad<bool>(IDT_0x6b);
}
void loop() {
    
}
#ifndef ARDUINO_AVR_UNO
int main() {
    setup();
    
    return IDT_0x29786b9c;
}
#endif
