// Built in function for +(num leftHand, num rightHand) -> num
inline double IDT_0x2b (double leftHand, double rightHand) {
    return leftHand + rightHand;
}

// Built in function for -(num leftHand, num rightHand) -> num
inline double IDT_0x2d (double leftHand, double rightHand) {
    return leftHand - rightHand;
}

// Built in function for *(num leftHand, num rightHand) -> num
inline double IDT_0x2a (double leftHand, double rightHand) {
    return leftHand * rightHand;
}

// Built in function for /(num leftHand, num rightHand) -> num
inline double IDT_0x2f (double leftHand, double rightHand) {
    return leftHand / rightHand;
}

// Built in function for and(bool leftHand, bool rightHand) -> bool
inline bool IDT_0x179d7 (bool leftHand, bool rightHand) {
    return leftHand && rightHand;
}

// Built in function for or(bool leftHand, bool rightHand) -> bool
inline bool IDT_0xde3 (bool leftHand, bool rightHand) {
    return leftHand || rightHand;
}

// Built in function for greaterThanEqual(num leftHand, num rightHand) -> bool
inline bool IDT_0xe9db1339 (double leftHand, double rightHand) {
    return leftHand >= rightHand;
}

// Built in function for lessThanEqual(num leftHand, num rightHand) -> bool
inline bool IDT_0xecf8c07a (double leftHand, double rightHand) {
    return leftHand <= rightHand;
}

// Built in function for greaterThan(num leftHand, num rightHand) -> bool
inline bool IDT_0x3724a0bb (double leftHand, double rightHand) {
    return leftHand > rightHand;
}

// Built in function for lessThan(num leftHand, num rightHand) -> bool
inline bool IDT_0x7c8deeda (double leftHand, double rightHand) {
    return leftHand < rightHand;
}

// Built in function for equals(num leftHand, num rightHand) -> bool
inline bool IDT_0xb2c87fbf (double leftHand, double rightHand) {
    return leftHand == rightHand;
}

// Built in function for notEquals(num leftHand, num rightHand) -> bool
inline bool IDT_0x348a6c72 (double leftHand, double rightHand) {
    return leftHand != rightHand;
}

// Built in function for equals(bool leftHand, bool rightHand) -> bool
inline bool IDT_0xb2c87fbf (bool leftHand, bool rightHand) {
    return leftHand == rightHand;
}

// Built in function for notEquals(bool leftHand, bool rightHand) -> bool
inline bool IDT_0x348a6c72 (bool leftHand, bool rightHand) {
    return leftHand != rightHand;
}

// Built in function for not(bool operand) -> bool
inline bool IDT_0x1aad3 (bool operand) {
    return ! operand;
}

// Built in function for mod(num leftHand, num rightHand) -> num
inline double IDT_0x1a702 (double leftHand, double rightHand) {
    return (long)leftHand % (long)rightHand;
}

// Built in function for then(bool condition, func[none] body) -> bool
inline bool IDT_0x364e1d (bool condition, function<void()> body) {
    if (condition) { body(); }
    return condition;
}

// Built in function for while(func[none] body, func[bool] condition) -> bool
inline bool IDT_0x6bdcb31 (function<void()> body, function<bool()> condition) {
    while (condition()) body();
}

template <typename T>
// Built in function for each(list[T] list, func[T, none] body) -> none
inline void IDT_0x2f6201 (List<T> list, function<void(T)> body) {
    for (int i = 0; i < list.get()->size; i++) {
    body(list.get()->data[i]);
    }
    return;
}

// Built in function for toText(num input) -> text
inline List<char> IDT_0xcc361248 (double input) {
    return ftoa(input, 5);
}

// Built in function for toText(bool input) -> text
inline List<char> IDT_0xcc361248 (bool input) {
    return input ? ConstList<char>::string((char *)"true") : ConstList<char>::string((char *)"false");
}

template <typename T>
// Built in function for toText(list[T] input) -> text
inline List<char> IDT_0xcc361248 (List<T> input) {
    auto output = ConstList<char>::string((char*)"[");
    for(int i = 0; i < input.get()->size; i++) {
       output = ConstList<char>::concat(output, IDT_0xcc361248(ConstList<T>::at(input, i)));
       if (i != input.get()->size - 1)
           output = ConstList<char>::concat(output, ConstList<char>::string((char*)", "));}
    return ConstList<char>::concat(output, ConstList<char>::string((char*)"]\0"));
}

// Built in function for toText(text input) -> text
inline List<char> IDT_0xcc361248 (List<char> input) {
    return input;
}

template <typename T>
// Built in function for length(list[T] list) -> num
inline double IDT_0xbe0e3ae6 (List<T> list) {
    return list.get()->size;
}

template <typename T>
// Built in function for equals(list[T] leftHand, list[T] rightHand) -> bool
inline bool IDT_0xb2c87fbf (List<T> leftHand, List<T> rightHand) {
    return IDT_0xbe0e3ae6(leftHand) == IDT_0xbe0e3ae6(rightHand) && memcmp(leftHand.get()->data, rightHand.get()->data, leftHand.get()->size * sizeof(T)) == 0;
}

template <typename T>
// Built in function for notEquals(list[T] leftHand, list[T] rightHand) -> bool
inline bool IDT_0x348a6c72 (List<T> leftHand, List<T> rightHand) {
    return !IDT_0xb2c87fbf(leftHand,rightHand);
}

template <typename T>
// Built in function for at(list[T] list, num rightHand) -> T
inline T IDT_0xc33 (List<T> list, double rightHand) {
    return ConstList<T>::at(list, (unsigned int)rightHand);
}

template <typename T>
// Built in function for subList(list[T] list, num startIndex, num length) -> list[T]
inline List<T> IDT_0x909cc2fe (List<T> list, double startIndex, double length) {
    return ConstList<T>::sublist(list, (unsigned int)startIndex, (unsigned int)length);
}

template <typename T>
// Built in function for +(list[T] leftHand, list[T] rightHand) -> list[T]
inline List<T> IDT_0x2b (List<T> leftHand, List<T> rightHand) {
    return ConstList<T>::concat(leftHand, rightHand);
}

template <typename T, typename T2>
// Built in function for map(list[T] list, func[T, T2] fun) -> list[T2]
inline List<T2> IDT_0x1a55c (List<T> list, function<T2(T)> fun) {
    T2 result[list.get()->size];
    for (int i = 0; i < list.get()->size; i++) {
    result[i] = fun(list.get()->data[i]);
    }
    return ConstList<T2>::create(result, list.get()->size);
}

template <typename T>
// Built in function for filter(list[T] list, func[T, bool] fun) -> list[T]
inline List<T> IDT_0xb408cb78 (List<T> list, function<bool(T)> fun) {
    T result[list.get()->size];
    int index = 0;
    for (int i = 0; i < list.get()->size; i++) {
       if (fun(list.get()->data[i]))
           result[index++] = list.get()->data[i];
    }
    return ConstList<T>::create(result, index);
}

// Built in function for delayMillis(num millis) -> none
inline void IDT_0xfdb1d1a9 (double millis) {
    #ifdef ARDUINO_AVR_UNO
    delay((int)millis);
    #endif //ARDUINO_AVR_UNO
    #ifdef _WIN32 //If windows based PC
    Sleep((unsigned int)(millis));
    #else //If unix based PC
    usleep(((unsigned int)millis*1000));//convert milliseconds to microseconds
    #endif //_WIN32
    return;
}

// Built in function for at(text list, num rightHand) -> text
inline List<char> IDT_0xc33 (List<char> list, double rightHand) {
    char charArr[2] = {ConstList<char>::at(list, (unsigned int)rightHand),'\0'};
    return ConstList<char>::string(charArr);
}

// Built in function for subText(text leftHand, num startIndex, num length) -> text
inline List<char> IDT_0x90a0578d (List<char> leftHand, double startIndex, double length) {
    return IDT_0x909cc2fe<char>(leftHand,startIndex,length);
}

// Built in function for length(text leftHand) -> num
inline double IDT_0xbe0e3ae6 (List<char> leftHand) {
    return IDT_0xbe0e3ae6<char>(leftHand);
}

// Built in function for to(num start, num end) -> list[num]
inline List<double> IDT_0xe7b (double start, double end) {
    double array[(int)end - (int)start + 1];
    for(int i = 0; i < end - start; i++){
     array[i] = start + i;
    }
    return ConstList<double>::create(array, end - start + 1);
}

// Built in function for setDigitalPin(num pin, bool value) -> none
inline void IDT_0xa2a97a5f (double pin, bool value) {
    #ifdef ARDUINO_AVR_UNO
    pinMode((int)pin, 1);
    digitalWrite((int)pin, value);
    #else
    std::cout << "Set digital pin " << (int)pin << " to output " << (value ? "HIGH" : "LOW") << std::endl;
    return;
    #endif // ARDUINO_AVR_UNOreturn;
}

// Built in function for readDigitalPin(num pin) -> num
inline double IDT_0xbf312693 (double pin) {
    #ifdef ARDUINO_AVR_UNO
    pinMode((int)pin, 0);
    return (double)digitalRead((int)pin);
    #else
    std::cout << "Reading from digital pin " << (int)pin << std::endl;
    return 0;
    #endif // ARDUINO_AVR_UNO
    
}

// Built in function for setAnalogPin(num pin, num value) -> none
inline void IDT_0xf6449c43 (double pin, double value) {
    #ifdef ARDUINO_AVR_UNO
    pinMode(pin, 1);
    analogWrite((int)pin, value);
    #else
    std::cout << "Set analog pin " << (int)pin << " to output " << value << std::endl;
    #endif // ARDUINO_AVR_UNO
    return;
}

// Built in function for readAnalogPin(num pin) -> num
inline double IDT_0x28bc998f (double pin) {
    #ifdef ARDUINO_AVR_UNO
    pinMode((int)pin, 0);
    return analogRead((int)pin);
    #else
    std::cout << "Reading from analog pin " << (int)pin << std::endl;
    return 0;
    #endif // ARDUINO_AVR_UNO
    
}

// Built in function for print(text input) -> none
inline void IDT_0x65fb2ad (List<char> input) {
    #ifdef ARDUINO_AVR_UNO
    Serial.begin(9600); // 9600 is the baud rate - must be the same rate used for monitor
    while(!Serial);     // Wait for Serial to initialize
    Serial.print(input.get()->data);
    Serial.end();
    #else // NOT ARDUINO_AVR_UNO
    std::cout << input.get()->data;
    #endif // ARDUINO_AVR_UNO
    return;
}

template <typename T>
// Built in function for print(list[T] input) -> none
inline void IDT_0x65fb2ad (List<T> input) {
    #ifdef ARDUINO_AVR_UNO
    Serial.print((IDT_0xcc361248<T>(input)).get()->data);
    Serial.end();
    #else // NOT ARDUINO_AVR_UNO
    std::cout << (IDT_0xcc361248<T>(input)).get()->data;
    #endif // ARDUINO_AVR_UNO
    return;
}

template <typename T>
// Built in function for print(T input) -> none
inline void IDT_0x65fb2ad (T input) {
    #ifdef ARDUINO_AVR_UNO
    Serial.print((IDT_0xcc361248(input)).get()->data);
    #else // NOT ARDUINO_AVR_UNO
    std::cout << (IDT_0xcc361248(input)).get()->data;
    #endif // ARDUINO_AVR_UNO
    return;
}

// Built in function for +(text leftHand, text rightHand) -> text
inline List<char> IDT_0x2b (List<char> leftHand, List<char> rightHand) {
    return IDT_0x2b<char>(leftHand, rightHand);
}

// Built in function for equals(text leftHand, text rightHand) -> bool
inline bool IDT_0xb2c87fbf (List<char> leftHand, List<char> rightHand) {
    return IDT_0xb2c87fbf<char>(leftHand, rightHand);
}

// Built in function for notEquals(text leftHand, text rightHand) -> bool
inline bool IDT_0x348a6c72 (List<char> leftHand, List<char> rightHand) {
    return IDT_0x348a6c72<char>(leftHand, rightHand);
}
