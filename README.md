# sielay

## How to run?
Get a fat jar:
```bash
gradle shadowJar
```
Run the fat jar:
```bash
java -jar sielayv2.jar
```
In order to print the help menu:
```bash
java -jar sielayv2.jar --help
```
There are 3 commands as specified in the pdf. I took the liberty of adding a flag -S or --seperator so that the user can specify desired seperator.
The flag is optional and hence can be omitted as well. Default seperator value is :::.
```bash
java -jar sielayv2.jar fill-data my/beautiful/dark/twisted/path -S ;
```
There are known limitations with the seperator though. this project utilizes picocli, which uses a regex based parser and hence inputs such as "?" can't be used since it is a reserved keyword for the regular expression engine. in such cases, pico will raise a failure message.

I tested the program either by intellij (giving custom args parameters at every run) and by fat jar itself. 
Had some a/b testing session with the jar file itself for several hours and I was not able to break it.
Unfortunately I don't have access to a *nix machine so all testing had been done on my win11 machine. 
The code is compiled vis-à-vis Java 17 and gradle 7.

## The overall design
### Interpreter Pattern and do-notations
In declarative languages, statements are not run sequentially but rather the compiler generates an AST where the calculations
are done bottom to top. For example in Haskell:
```haskell
max3chars :: Char -> Char -> Char -> Either ParseDigitError Int
max3chars x y z =
(\a b c -> max a (max b c))
<$> parseDigit x
<*> parseDigit y
<*> parseDigit z
```
This allows the entire code block to depend on one another, and if one fails entire sequence fails at the same time.
It might seem redundant and dangerous at first, but there are several benefits of writing such a code:
1. It allows the programmer to test branches that are known to fail. So for example if you have 3 flatMapped functions
all chained together there would be 6 test branches instead of 2^3=8, since other possible branches are pruned from the execution.
2. It allows more resilient, compact code. 

Weird operators above are shortcuts for fMap and flatMap operations.At the end of 20th century, Haskell programmer found out
that their code is getting bogus with all the flatMap's and map's, and they came up with do-notation. Basically it is a syntactic
sugar for the compiler to automagically write these operators for the programmer, and thus the code becomes more sequential-looking.
In Scala:
```scala
for {
ref     <- Ref.make(SpiderState(seeds, List.empty[E]))
compute <- ZIO.attempt(visitParallelN(seeds, router, processor)(ref, maxNumberOfFibers)).flatten
state   <- ref.get
} yield state.errors
```
As you can see, without the weird for comprehension it exactly looks alike to a Java code block.

Unfortunately Java does not support do-notations, however we have a Control Monad, which is java.utils::Optional.
99% of the codebase for this project depends on it for reasons listed above, Monadic Control Flow is just superior because
of testing and resilience purposes.

Optional is _terminating_ which means if the field is assigned as `Optional::empty` 
the remaining Functor (essentially maps and flatmaps) chains won't be run. For example in this line,
```java
context.readResource(Path.of(SOURCE_FILE_LOCATION))
.flatMap(this::validate)
.flatMap(source -> write(path, source))
.flatMap(contents -> serializer.serialize(new ApplicationContext(contents, seperator)));
```
if one of these Optional returning functions returns empty, the rest would not be run as well. We would have less test cases,
less lines of code (the entire project is actually very small for a Java project) and more resilience
(since a failure will be propagated as top as the programmer wishes)

There are several reasons I chose to do that:
1. Reactive programming was a plus on the job description. What is reactive programming but plain old Functional Paradigm anyway?
2. Unit testing would be super easy with that kind of setup.
3. Doing Scala work for the past 3 years I am used to writing code like this. 

Unfortunately there are several cases where I could've propagated the failure message and collect all of them for better presentation,
alas it would've been such a pain to do that with only Option Monad's and would require some side-effecty code. Either Type would have
done wonders but no good Java implementation is available afaik (there is vavr, but vavr is a terrible library).

But I did not use any System.exit()'s or any flow-breaking goto like spaghetti code crunching statements.

### Alternative Test Cases
One Design Pattern I adore while writing Java code is the Façade Pattern. This pattern turns every single package into a stand-alone
program on its own and exchange messages with other packages via DTO's. This enables proper Separation of Concerns, 
makes the code more testable (every Façade is a unit in itself) and ensures that no otherworldly changes affecting the package itself.
I've not used the Façade in this project, yet the point remains, most packages are somehow independent.

So, the logic for commands resides under the middleware package. CLI framework parses the data and calls each respective object 
for handling the required actions. I've also seperated IO logic from Command's, thus enabling the specific logic for commands
are tested _given that IO objects work properly._ 

Most of the test cases are heavily stubbed and mocked. As explained above, usage of Control Monad's make the code testable with 
less test cases. I think the actual logic is tested properly and does not need any other cases to be handled.

That being said, I did not write any test code for IO and Parsing. IO is not unit testable anyways and CLI framework does 
not necessarily give out tools for testing the behaviour. 


## Pitfalls of shred
shred would not work unless the hardware and the operating system ensures _overwriting_ instead of _overriding_.
Thus, if the hardware and/or the OS does not manipulate the same memory address for the file given, shred would fail its premises.
