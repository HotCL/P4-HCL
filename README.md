# HCL Compiled Language


<a href="http://hcl.codes/teamcity/viewType.html?buildTypeId=P4hcl_Build&guest=1">
<img src="http://hcl.codes/teamcity/app/rest/builds/buildType:(id:P4hcl_Build)/statusIcon"/>
</a>


HCL, HCL Compiled Language, is the new super cool imperative programming language, build on functional principles. The language uses simple principles to make a language that has a very nice learning curve. This is done by making everything english-like, in terms of syntax and structure.

The language was developed by 6 students during their 4th semester of the Software Engineering bachelor at Aalborg University. Development started in Feburary 2018, and version 1.0, which was the project hand-in version, was finished on May 28th 2018.

### Principles:
1. Everything is a function (Even operators)
2. Everything is a variable (Even functions)
3. Everything is infix

This might seem odd if you are used to other programming languages, but following really simple principles makes it intuitive to write a lot of code without consulting documentation all the time.

Endlines in HCL doesn't matter. You do need spaces between declarations and individual statements and expressions, though, so be sure to remember those!
# HYPER COOL LEARNING OF HCL 
## Hello World
To print “Hello World!” in HCL, simply write “Hello world” followed by the print function:
``"Hello World!" print``

It consists of a string (“Hello World!”) which is an argument to the print function. The unusual thing with HCL is that the first argument is always before the function call. The statement is followed by a newline, which is used to seperate statements, like in Python, Ruby or other modern languages.


## Arithmetic
Everything is a function, so `+` is simply defined as a builtin function with two arguments. And because it's simply a function there is no precedense. HCL simply read the expressions from left to right.
In other words:
`1 + 5 * 10 equals 60`
This is because the above line is simply the result of 1 + 5 used as an argument to the function `*`, which is used as a argument to the function `equals`. This might seem weird, but as soon as you get used to deciding exactly what *you* want, it makes development less about majoring in math, and more about majoring in fun! 

Brackets can still be used to control execution order, so it's not impossible to get the mathimatical answer. This would be done like this:
`1 + (5 * 10) equals 50`
This is because the result of 5 * 10 is now the secondary argument to the plus function!

## Types of HCL
HCL tries to simplify types, by not using arcane keywords, but rather intuitive words. has a number of different types:
* **Number**, which are declared as
    * `num creationYear = 2018`
* **Strings**, which are declared as
    * `txt myThought = "HCL is so easy!"`
* **Booleans**, which are declared as
    *  `bool isCool = true`
* **Lists**, which are collections of the previously mentioned types:
    * `list[txt] creators = ["Casper", "Lisby", "Thomas", "Casanova", "Lambæk", "Jonas"]`
* **Tuples**, which are declared as
    * `tuple[txt, num] property =  ("HCLEaseOfUseInPercentage", 100)`
* **Functions**, which are declared as
    * `func [txt, bool] cool? = (txt param1): bool{ (param1 equals "HCL") then { return true } else {return false} }`

All of these can also be declared with var, in which case the type is inferred from the actual assignment. Thus the following is infered as a text-type:
`var IsAString = "HCL"`

In other languages numbers have a lot of different types, intergers, shorts, decimals, floats.
In HCL, there is only the num type, which is represented as decimals.
Don't worry too much about it, just write the number you need, HCL will figure out the rest!

Everything in HCL is immutable. This means that values can't change.
Thus when you declare something using the same name again, it is a new instance that is created instead, using that name.

## Functions
Functions also has a special type called `none`, which is used to explain that a function has no return type. Let's try to create one and use it!

```
func [none] printTruth = () : none { "HCL IS COOL!!!" print }

printTruth
```
Which outputs:
`"HCL IS COOL!!!"` 

As functions are also variables, the type can also be infered here. Let's make a function with multiple parameters:

```
var printMultipleTimes = (txt myText, num times, txt end) : none { 
    0 to times print each {
        myText print
    }
    end print
}

"foo" printMultipleTimes 3 "bar"

```
This outputs: 
```
foo
foo
foo
bar
```

The observant reader (You!!!) will notice that we are using 2 new functions `to`and `each`.
`to` simply gives us a list of all the numbers between two numbers, and each takes a list and a function, and runs the function on each element in the list. So we are just doing a for-loop (continuous executions of the same code) here! 

The cool thing about functions here, is that we are abstracting away all the nasty type-declaration of the input-function of `each`, because why should you have to write what function is needed when `each` already knows what it's expecting! 

No reason to write:
`0 to times each (num element) : None { myText print }` 
when you can simply write:
`0 to times each { myText print }`
When using these implied functions as arguments, the parameter names are defaulting to `value`, `value1`, `value2` etc.


## Full examples

### Queries
An example where we query a list of numbers:
`[2, 4, 6, 8] map { value * 2 } filter { value greaterThan 10 } print`
which prints:
`[12, 16]`

### Naming
HCL doesn't want to restrict you so you can basically use everything as a function name. 
```
var +- = (num val1, num val2): tuple[num,num] {
    return (val1 + val2, val1 - val2)
}
```

or even:
`var 🍆 = 9.1`
Where 9.1 would be the weight of the eggplant in kilos! (Wow that is a big one! 😲😉)


### Fibonacci numbers
A more indepth example is this implementation of a recursive fibnoacci algorithm.
```
var fib = (num n) : num {
    var val = 0
    (n lessThanEqual 2) then { 
        val = 1 
    } else { 
        val = ((n - 1) fib) + ((n - 2) fib) 
    }
    return val
}

1 to 6 map { value fib } print
```

Prints the first 6 fibonacci numbers, in a list:

`[1, 1, 2, 3, 5, 8]`

This example also shows how the basic if-else control structure is done in HCL. No suprise here, that this is also just two functions. `then` is of the type `func[bool, func[none], bool]`, so it takes a boolean and a function in, executes the function if bool is true, and then returns the same value as bool. This is done so it's possible to chain `then` together with the `else` function, that has the exact same type, but only executes the input function, if the input boolean is false!

## Generics

Generics can be a bit more complicated, however this is required to implement some more advanced features. This might not be necessary for the novice, but it's a critical feature for the advanced user, and can be nice to know.

To be able to implement things like `each` and other such functions, HCL uses generics. This means that  `each` is of type `func[list[T],func[T,none],none`. So it takes a list of an unspecificed type, and a function with an input of the same tyep! The `map` function is of a similar type, however it has to return a new list as well! `func[list[T],func[T,T2],list[T2]`. 

This isn't just for builtin functions though. If you want you could make a mapFilter function, perhaps:
```
var mapEach = (list[T] myList, func[T,T2] mapFunc, func[T,None] eachFunc):None {
    myList mapFunc eachFunc
}

[1,2,3] mapEach { value toText } { (value + value) print }
```
Giving:
```
11
22
33
```

