#ifndef ARDUINOPIN_H
#define ARDUINOPIN_H

#ifdef ARDUINO_AVR_UNO
void writeDigPin(double dPin, int value) {
    int pin = (int)round(dPin);
    pinMode(pin, OUTPUT);
    digitalWrite(pin, value);
    return;
}

double readDigPin(double dPin) {
    int pin = (int)round(dPin);
    pinMode(pin, INPUT);
    return (double)digitalRead(pin);
}

void writeAnaPin(double aPin, int value) {
    int pin = (int)round(aPin);
    pinMode(pin, OUTPUT);
    analogWrite(pin, value);
    return;
}

double readAnaPin(double aPin) {
    int pin = (int)round(aPin);
    pinMode(pin, INPUT);
    return analogRead(pin);
}
#else
#include <iostream>
#include <cmath>
/*
 * These functions are only for testing when program is run on PC
 */
void writeDigPin(double dPin, int value) {
    int pin = (int)round(dPin);
    std::cout << "Set digital pin " << pin << " to output " << (value == 0 ? "LOW" : "HIGH") << std::endl;
    return;
}

double readDigPin(double dPin) {
    int pin = (int)round(dPin);
    std::cout << "Reading from digital pin " << pin << std::endl;
    return 0.0; //arbitrary value
}

void writeAnaPin(double aPin, int value) {
    int pin = (int)round(aPin);
    std::cout << "Set analog pin " << pin << " to output " << value << std::endl;
    return;
}

double readAnaPin(double aPin) {
    int pin = (int)round(aPin);
    std::cout << "Reading from analog pin " << pin << std::endl;
    return 0.0; //arbitrary value
}
#endif //ARDUINO_AVR_UNO

#endif //ARDUINOPIN_H
