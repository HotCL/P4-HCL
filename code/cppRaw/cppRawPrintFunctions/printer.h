#ifndef PRINTER_H
#define PRINTER_H

#include "ConstList.h"
#include <memory>

#ifdef ARDUINO_AVR_UNO
void print(std::shared_ptr<ConstList<char>> str) {
    Serial.begin(9600); //9600 is the baud rate - must be the same rate used for monitor
    while(!Serial);     //Wait for Serial to initialize
    Serial.print(str.get()->data);
    Serial.end();
    return;
}

void print(double d) {
    Serial.begin(9600);
    while(!Serial);
    Serial.print(d);
    Serial.end();
    return;
}

void print(bool b) {
    Serial.begin(9600);
    while(!Serial);
    Serial.print(b ? "True" : "False");
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

void print_line(double d) {
    Serial.begin(9600);
    while(!Serial);
    Serial.println(d);
    Serial.end();
    return;
}

void print_line(bool b) {
    Serial.begin(9600);
    while(!Serial);
    Serial.println(b ? "True" : "False");
    Serial.end();
    return;
}
#else //NOT ARDUINO_AVR_UNO
#include <iostream>
void print(std::shared_ptr<ConstList<char>> str) {
    std::cout << str.get()->data;
    return;
}

void print(double d) {
    std::cout << d;
    return;
}

void print(bool b) {
    std::cout << (b ? "True" : "False");
    return;
}

void print_line(std::shared_ptr<ConstList<char>> str) {
    std::cout << str.get()->data << std::endl;
    return;
}

void print_line(double d) {
    std::cout << d << std::endl;
    return;
}

void print_line(bool b) {
    std::cout << (b ? "True" : "False") << std::endl;
    return;
}
#endif //ARDUINO_AVR_UNO

#endif //PRINTER_H
