
#include <functional>

#include "ConstList.h"
#include "ftoa.h"

using namespace std;

#include "builtin.h"
#include "types.h"
TPL_0x2663b858 IDT_0x74 = {0, ""};
TPL_0xd4805a15 IDT_0x79 = {ConstList<double>::create(nullptr, 0), nullptr};
double IDT_0x63851db = 5.0;
// Lambda function for name plus5(num x) ->func[num, num]
function<double(double)> IDT_0x65d25db = [&](double IDT_0x78) {
    return IDT_0x2b(IDT_0x78, IDT_0x63851db);
};

double IDT_0xee9285f5 = IDT_0x65d25db(IDT_0x63851db);

/* Setup function */

void setup() {
}


/* Loop function */

void loop() {
}
#if !ARDUINO_AVR_UNO
int main() {
   setup();
   while(1) { loop(); }
   return 0;
}
#endif
