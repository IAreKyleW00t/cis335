Project 7
---------
#### Description
  A program that will interpret a basic mathematical expression using a top-down recursive descent method. The program will prompt a `$`, read input from the user, and then parse the input into a Node Tree and evaluate the expression in a prefix format.
  
  Each character in the input should be separated by at least one space.
  
  **Operands**: `+`, `-`, `*`, `/`, `(`, and `)`  
  **Grammar**:  
```
<expr> ::= <term> { + <term> | - <term> }
<term> ::= <factor> { * <factor> | / <factor> }
<factor> ::= intnum | ( <expr> )
```
  
  The program will terminate if an invalid expression is given (ie: 3 + + 3).
  
#### Example
  ```
  $ 3 + 2
    + 3 2 = 5
  $ 2 + 4 * 6
    + 2 * 4 6 = 26
  $ ( 6 + 2 ) * ( 10 - 7 )
    * + 6 2 - 10 7 = 24
  $ ( ( 6 + 2 ) / 4 ) * ( 10 - 7 ) + 5
    + * / + 6 2 4 - 10 7 5 = 11
  $ .
  ```
