#include "printer.h"
#include "ConstList.h"

int main() {
    print(ConstList<char>::create((char*)"hej", strlen("hej") + 1));
    print_line(ConstList<char>::create((char*)"nej", strlen("nej") + 1));
    return 0;
}

