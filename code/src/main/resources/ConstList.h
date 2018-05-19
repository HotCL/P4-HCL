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
        return ConstList<char>::create_from_copy(txt, strlen(txt));
    }
};

template <typename T>
using List = typename ConstList<T>::List;

#endif //CONSTLIST_H