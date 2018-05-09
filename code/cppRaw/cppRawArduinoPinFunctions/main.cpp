#include <cstdio>
#include "ftoa.h"
#include <string>
#include <iostream>
#include <memory>
#include "arduinopin.h"

int main() {
    setAnaPinIn(6);
    setAnaPinOut(3, 100);
    setDigPinIn(12);
    setDigPinOut(13, 1);
    return 0;
}

