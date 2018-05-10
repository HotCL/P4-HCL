#include <iostream>
#include "delay.h"

int main() {
    std::cout << "1" << std::endl;
    delayMillis(500.0);
    std::cout << "2" << std::endl;
    delayMillis(500.0);
    std::cout << "3" << std::endl;
    delayMillis(500.0);
    std::cout << "4" << std::endl;
    return 0;
}

