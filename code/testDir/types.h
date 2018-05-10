typedef struct {
  double element0;
  List<char> element1;
} TPL_0x2663b858;

// Built in function for toText(tuple[num, text] self) ->text
inline List<char> IDT_0xcc361248 (TPL_0x2663b858 self) {
    ConstList<char>::List output = ConstList<char>::string((char*)"");
    output = ConstList<char>::concat(output, IDT_0xcc361248<char>(self.element0));
    output = ConstList<char>::concat(output, ConstList<char>::string((char*)","));
    output = ConstList<char>::concat(output, IDT_0xcc361248<char>(self.element1));
    return output;
}

// Built in function for at(tuple[num, text] self, num index) ->text
inline List<char> IDT_0xc33 (TPL_0x2663b858 self, double index) {
    switch((int)index) { 
    case 0: return self.element0;
    case 1: return self.element1;
    }
}

// Built in function for create_struct(num element0, text element1) ->tuple[num, text]
inline TPL_0x2663b858 IDT_0x8ef41478 (double element0, List<char> element1) {
    TPL_0x2663b858 output = element0, element1;
    return output;
}
