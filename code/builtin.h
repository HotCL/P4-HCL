// Built in function for +(num leftHand, num rightHand) ->num
inline double IDT_0x2b (double leftHand, double rightHand) {
    return leftHand + rightHand;
}

// Built in function for -(num leftHand, num rightHand) ->num
inline double IDT_0x2d (double leftHand, double rightHand) {
    return leftHand - rightHand;
}

// Built in function for *(num leftHand, num rightHand) ->num
inline double IDT_0x2a (double leftHand, double rightHand) {
    return leftHand * rightHand;
}

// Built in function for /(num leftHand, num rightHand) ->num
inline double IDT_0x2f (double leftHand, double rightHand) {
    return leftHand / rightHand;
}

// Built in function for and(bool leftHand, bool rightHand) ->bool
inline bool IDT_0x179d7 (bool leftHand, bool rightHand) {
    return leftHand && rightHand;
}

// Built in function for or(bool leftHand, bool rightHand) ->bool
inline bool IDT_0xde3 (bool leftHand, bool rightHand) {
    return leftHand || rightHand;
}

// Built in function for greaterThanEqual(num leftHand, num rightHand) ->bool
inline bool IDT_0xe9db1339 (double leftHand, double rightHand) {
    return leftHand >= rightHand;
}

// Built in function for lessThanEqual(num leftHand, num rightHand) ->bool
inline bool IDT_0xecf8c07a (double leftHand, double rightHand) {
    return leftHand <= rightHand;
}

// Built in function for greaterThan(num leftHand, num rightHand) ->bool
inline bool IDT_0x3724a0bb (double leftHand, double rightHand) {
    return leftHand > rightHand;
}

// Built in function for lessThan(num leftHand, num rightHand) ->bool
inline bool IDT_0x7c8deeda (double leftHand, double rightHand) {
    return leftHand < rightHand;
}

// Built in function for equals(num leftHand, num rightHand) ->bool
inline bool IDT_0xb2c87fbf (double leftHand, double rightHand) {
    return leftHand == rightHand;
}

// Built in function for notEquals(num leftHand, num rightHand) ->bool
inline bool IDT_0x348a6c72 (double leftHand, double rightHand) {
    return leftHand != rightHand;
}

// Built in function for equals(bool leftHand, bool rightHand) ->bool
inline bool IDT_0xb2c87fbf (bool leftHand, bool rightHand) {
    return leftHand == rightHand;
}

// Built in function for notEquals(bool leftHand, bool rightHand) ->bool
inline bool IDT_0x348a6c72 (bool leftHand, bool rightHand) {
    return leftHand != rightHand;
}

// Built in function for not(bool operand) ->bool
inline bool IDT_0x1aad3 (bool operand) {
    return ! operand;
}

// Built in function for then(bool condition, func[none] body) ->bool
inline bool IDT_0x364e1d (bool condition, function<void()> body) {
    if (condition) { body(); }
    return condition;
}

// Built in function for while(func[none] body, func[bool] condition) ->bool
inline bool IDT_0x6bdcb31 (function<void()> body, function<bool()> condition) {
    while (condition()) body();
}

// Built in function for toText(num input) ->text
inline List<char> IDT_0xcc361248 (double input) {
    return ftoa(input, 5);
}

// Built in function for toText(bool input) ->text
inline List<char> IDT_0xcc361248 (bool input) {
    return input ? ConstList<char>::string((char *)"True") : ConstList<char>::string((char *)"False");
}

template <typename T>
// Built in function for toText(list[T] input) ->text
inline List<char> IDT_0xcc361248 (List<T> input) {
    auto output = ConstList<char>::string((char*)"[")
    for(int i = 0; i < input.get()->size; i++) {
       output = ConstList<T>::concat(output,toText(at(input,i));
    }
    return output;
}

template <typename T>
// Built in function for length(list[T] list) ->num
inline double IDT_0xbe0e3ae6 (List<T> list) {
    return list.get()->size;
}

template <typename T>
// Built in function for equals(list[T] leftHand, list[T] rightHand) ->bool
inline bool IDT_0xb2c87fbf (List<T> leftHand, List<T> rightHand) {
    return length(leftHand) == length(rightHand) && memcmp(leftHand.get()->data, rightHand.get()->data, leftHand.get()->size * sizeof(T)) == 0;
}

template <typename T>
// Built in function for notEquals(list[T] leftHand, list[T] rightHand) ->bool
inline bool IDT_0x348a6c72 (List<T> leftHand, List<T> rightHand) {
    return !equals(leftHand,rightHand);
}

template <typename T>
// Built in function for at(list[T] list, num rightHand) ->T
inline T IDT_0xc33 (List<T> list, double rightHand) {
    return ConstList<T>::at(list, (unsigned int)rightHand);
}

template <typename T>
// Built in function for subList(list[T] list, num startIndex, num length) ->list[T]
inline List<T> IDT_0x909cc2fe (List<T> list, double startIndex, double length) {
    return ConstList<T>::sublist(list, (unsigned int)startIndex, (unsigned int)length);
}

template <typename T>
// Built in function for +(list[T] leftHand, list[T] rightHand) ->list[T]
inline List<T> IDT_0x2b (List<T> leftHand, List<T> rightHand) {
    return ConstList<T>::concat(leftHand, rightHand);
}
