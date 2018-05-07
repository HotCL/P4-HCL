
#ifndef FTOA_H
#define FTOA_H

#include <cstdlib>
#define MAX_STR_LEN 16

/*
 * This function returns a char array (string) representation of a floating point value
 * Parameters: d = double to be converted | precision = number of digits after decimal point
 */
char *ftoa(double d, int precision) {
    char *buffer = (char*)malloc(MAX_STR_LEN * sizeof(char));

    // Add digits before decimal point to string
    long wholePart = (long)d;
    sprintf(buffer,"%ld",wholePart);

    // Add digits after decimal point, if needed
    if (precision > 0) {
        char *endOfString = buffer;

        while (*endOfString != '\0') endOfString++;
        *endOfString++ = '.';

        if (d < 0) {
            d *= -1;
            wholePart *= -1;
        }

        double fraction = d - wholePart;
        for (; precision > 0 && endOfString < (buffer + MAX_STR_LEN); precision--) {
            fraction *= 10;
            wholePart = (long)fraction;
            *endOfString++ = '0' + (char)wholePart; // Append digit to string

            fraction -= wholePart;
        }

        *endOfString = '\0';
    }

    return buffer;
}

#endif //FTOA_H
