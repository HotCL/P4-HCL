
#ifndef FTOA_H
#define FTOA_H

#include <cstdlib>
#define MAX_STR_LEN 16

char *ftoa(double d, int precision) {
    char *buffer = (char*)malloc(MAX_STR_LEN * sizeof(char));

    // Add numbers before decimal point to string
    long wholePart = (long)d;
    sprintf(buffer,"%i",wholePart);

    // Add numbers after decimal point, if needed
    if (precision > 0) {
        char *endOfString = buffer;

        while (*endOfString != '\0') endOfString++;
        *endOfString++ = '.';

        if (d < 0) {
            d *= -1;
            wholePart *= -1;
        }

        double fraction = d - wholePart;
        int endOfBuffer = atoi(buffer) + MAX_STR_LEN;
        for (; precision > 0 && atoi(endOfString) < endOfBuffer; precision--) {
            fraction *= 10;
            wholePart = (long)fraction;
            *endOfString++ = '0' + (char)wholePart;

            fraction -= wholePart;
        }

        *endOfString = '\0';
    }

    return buffer;
}

#endif //FTOA_H
