

#ifndef FTOA_H
#define FTOA_H

#include <cstdlib>
#define MAX_STR_LEN 16

/*
 * This function returns a char array (string) representation of a floating point value
 * Parameters: d = double to be converted | precision = number of digits after decimal point
 */
List<char> ftoa(double d, int precision) {
    char buffer[MAX_STR_LEN];
    memset(buffer, 0, MAX_STR_LEN);

    // Add digits before decimal point to string
    long characteristic = (long)d;
    sprintf(buffer, "%ld", characteristic);

    // Add digits after decimal point, if needed
    if (precision > 0) {
        char *endOfString = buffer;

        while (*endOfString != '\0') endOfString++;
        *endOfString++ = '.';

        if (d < 0) {
            d *= -1;
            characteristic *= -1;
        }

        double mantissa = d - characteristic;
        char *endOfBuffer = buffer + MAX_STR_LEN;
        for (; precision > 0 && endOfString < endOfBuffer; precision--) {
            mantissa *= 10;
            characteristic = (long)mantissa;
            *endOfString++ = '0' + (char)characteristic; // Append digit to string

            mantissa -= characteristic;
        }

        *endOfString = '\0';
    }

    return ConstList<char>::string(buffer);
}

#endif //FTOA_H

