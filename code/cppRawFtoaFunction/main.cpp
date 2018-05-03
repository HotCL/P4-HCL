#include <cstdio>
#include "ftoa.h"
#include <string>
#include <iostream>

void test_ftoa(double d, int precision, std::string expected) {
    std::string str = ftoa(d, precision);

    if (str.compare(expected))
        std::cout << "FAILED!\nExpected: " << expected << "\nResult:\t  " << str << "\n\n";

    else std::cout << "SUCCESS!\nExpected: " << expected << "\nResult:\t  " << str << "\n\n";
}

int main() {
    std::cout << "Test of ftoa function\n\n";

    test_ftoa(4.4732, 5, "4.47320");
    test_ftoa(4.4732, 0, "4");
    test_ftoa(4.4, 10, "4.4000000000");
    test_ftoa(1.12345, 5, "1.12345");
    test_ftoa(40000.25, 3, "40000.250");
    test_ftoa(999.12345, 2, "999.12");
    return 0;
}

