package generation.cpp

import generation.FilePair

object HelperHeaders {
    val constList = FilePair("ConstList.h",
"""
    #ifndef CONSTLIST_H
#define CONSTLIST_H

#include <cstring>
#include <memory>

template <typename T>
class ConstList {
public:
    T *data;
    unsigned int size{};
    typedef std::shared_ptr<ConstList<T>> List;

    explicit ConstList(unsigned int amount_of_elements) {
        data = new T[amount_of_elements];
        size = amount_of_elements;
    }

    ConstList(T* elements, unsigned int amount_of_elements) : ConstList(amount_of_elements) {
        memcpy(data, elements, size * sizeof(T));
    }

    ConstList(ConstList & other) : ConstList(other.data, other.size) { }

    ~ConstList() {
        delete[] data;
    }

    static inline List create(T *elements, unsigned int amount_of_elements) {
        return std::shared_ptr<ConstList<T>>(new ConstList(elements, amount_of_elements));
    }

    static inline List create_from_copy(T *elements, unsigned int amount_of_elements) {
        auto * ret = new ConstList(amount_of_elements);
        for (int i = 0; i < amount_of_elements; i++) {
            ret->data[i] = elements[i];
        }
        return std::shared_ptr<ConstList<T>>(ret);
    }

    static List concat(List l1, List l2) {
        auto * ret = new ConstList(l1.get()->size + l2.get()->size);
        memcpy(ret->data, l1.get()->data, l1.get()->size * sizeof(T));
        memcpy(ret->data + l1.get()->size, l2.get()->data, l2.get()->size * sizeof(T));
        return std::shared_ptr<ConstList<T>>(ret);
    }

    static List sublist(List l, unsigned int start_idx, unsigned int length) {
        auto * ret = new ConstList(length);
        memcpy(ret->data, l.get()->data + start_idx, length * sizeof(T));
        return std::shared_ptr<ConstList<T>>(ret);
    }

    static T at(List l, unsigned int idx) {
        return l.get()->data[idx];
    }


    static List string(char* txt){
        return ConstList<char>::create(txt, strlen(txt));
    }
};

template <typename T>
using List = typename ConstList<T>::List;

#endif //CONSTLIST_H

"""
            )

    val ftoa = FilePair("ftoa.h",
"""

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
        while(*(endOfString-1) == '0' || *(endOfString-1) == '.'){
            endOfString = endOfString - 1;
        }

        *endOfString = '\0';
    }

    return ConstList<char>::string(buffer);
}

#endif //FTOA_H

"""
            )
}
