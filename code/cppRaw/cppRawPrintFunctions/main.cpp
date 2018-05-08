#include "printer.h"
#include "ConstList.h"

int main() {
    print(false);
    print(4.4);
    print(ConstList<char>::create((char*)"hej", strlen("hej") + 1));
    print_line(true);
    print_line(2.832);
    print_line(ConstList<char>::create((char*)"nej", strlen("nej") + 1));
    return 0;
}

