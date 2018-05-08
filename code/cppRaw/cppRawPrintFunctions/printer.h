#ifndef PRINTER_H
#define PRINTER_H

#include "ConstList.h"
#include <memory>

/*
 * These should be enough for all types, as long as the print functions in HCL call toText on the input
 */

#ifdef ARDUINO_AVR_UNO
void print(std::shared_ptr<ConstList<char>> str) {
    Serial.begin(9600); //9600 is the baud rate - must be the same rate used for monitor
    while(!Serial);     //Wait for Serial to initialize
    Serial.print(str.get()->data);
    Serial.end();
    return;
}

void print_line(std::shared_ptr<ConstList<char>> str) {
    Serial.begin(9600);
    while(!Serial);
    Serial.println(str.get()->data);
    Serial.end();
    return;
}

#else //NOT ARDUINO_AVR_UNO
#include <iostream>
void print(std::shared_ptr<ConstList<char>> str) {
    std::cout << str.get()->data;
    return;
}

void print_line(std::shared_ptr<ConstList<char>> str) {
    std::cout << str.get()->data << std::endl;
    return;
}
#endif //ARDUINO_AVR_UNO

#endif //PRINTER_H
