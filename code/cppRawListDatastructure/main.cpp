#include <iostream>
#include "ConstList.h"
#include <cstring>
#include <array>

ConstList<int>::List get_list_1() {
    int elements[] = {2, 4, 6, 8, 10, 12, 14, 16};
    auto list = ConstList<int>::create(elements, 8);
    return list;
}

ConstList<int>::List get_list_2() {
    int elements[] = {4, 8, 12, 16, 20};
    auto list = ConstList<int>::create(elements, 5);
    return list;
}

ConstList<int>::List do_list_concat() {
    auto l1 = get_list_1();
    auto l2 = get_list_2();
    auto l3 = ConstList<int>::concat(l1, l2);
    return l3;
}

ConstList<ConstList<int>::List>::List get_list_of_lists() {
    auto l1 = get_list_1();
    auto l2 = get_list_2();
    ConstList<int>::List l3_elements[] = {l1, l2};
    auto l3 = ConstList<ConstList<int>::List>::create_from_copy(l3_elements, 2);
    return l3;
}

int main() {
    // Concat and proper reference deletion
    auto l3 = do_list_concat();

    // Sublist
    auto l4 = ConstList<int>::sublist(l3, 2, l3.get()->size - 4);
    for (int i = 0; i < l4.get()->size; i++) {
        std::cout << l4.get()->data[i] << std::endl;
    }

    // list index
    std::cout << ConstList<int>::at(l3, 0) << std::endl;

    // list of lists
    auto l5 = get_list_of_lists();
    auto list_at_idx_0 = ConstList<ConstList<int>::List>::at(l5, 0);
    std::cout << ConstList<int>::at(list_at_idx_0, 3) << std::endl;

    // text test
    auto txt = ConstList<char>::string((char*)"hello world!");
    std::cout << "This should be a 'h'= "<< ConstList<char >::at(txt, 0) << std::endl;
    std::cout << "This should be '12'= "<< txt.get()->size << std::endl;
    std::cout << "This should be 'hello world!'= "<< txt.get()->data << std::endl;

}
