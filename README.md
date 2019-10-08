# Interpreters generator

It generates lexer, LL(1)-parser and interpreter's stub on Java. It's 
not serious, but it works. I am not actively maintaining this project,
but if you submit a PR with any changes I will happily merge your
code. Also you can send you patches to valeriy@manenkov.com. In production use
ANTLR.

## Build

Build with Maven:

```bash
mvn clean package
```

`target` directory will contain file `interpreter-1.0-SNAPSHOT.jar`

## Usage

Program's input is:

 * File with grammars description
 * Directory to place generated code

```bash
java -jar interpreter-1.0-SNAPSHOT.jar myGrammars.txt output
```

After execute it you will `output` directory with parser's stub. You should 
fill methods in `Interpreter.java` to define behavior when interpreter will
visit different AST-nodes.

For demo you can run `MainTest` directly from IDE and get generated source
code in temp directory of your operation system. You also can see example 
directly in this repo in `example` directory.

## Language for describing grammars

I will show language on example by arithmetic expressions parser. Full source
code is:

```
expr   := term[a] *((PLUS[b] | MINUS[b]) term[c] #BinOp(a, b, c)[a]) $a;
term   := factor[d] *((MUL[e] | DIV[e]) factor[f] #BinOp(d, e, f)[d]) $d;
factor := (PLUS[a] | MINUS[a]) factor[b] #UnaryOp(a,b)[c] $c;
factor := INTEGER[d] #Num(d)[e] $e;
factor := LPAREN expr[f] RPAREN $f;

PLUS := '+';
MINUS := '-';
MUL := '*';
DIV := '/';
EQ := ':=';
LPAREN := '(';
RPAREN := ')';
INTEGER := ('0'|'1'|'2'|'3'|'4'|'5'|'6'|'7'|'8'|'9') *('0'|'1'|'2'|'3'|'4'|'5'|'6'|'7'|'8'|'9');

@BinOp(%n, %t, %n);
@UnaryOp(%t, %n);
@Num(%t);
```

This example contains sequence of defenitions:

 1. Grammars for parser
 2. Tokens for lexer
 3. AST-node defentions

You should keep this order. Other required rules is:

 * Non-terminals consist only of lowercase letters. This
   is `expr`, `term` and `factor` in the example above.
 * Terminals consist only of capital letters. This is `PLUS`, `MINUS`, `MUL`,
   `DIV` etc.
 * Expression `*( <expression> )` means repetition of the expression 
   `<expression>` zero or more times.
 * Values of non-terminals should be written in single quotes.
 * Expression `<expr1> | <expr2>` means OR.
 * You can use brackets to define operations priorities
 * AST-node defenitions starts from `@` symbol. Then should be written their
   name. It should be translated to Java-code as is. Use CamelCase style.
 * Arguments in brackets defines types of child nodes.  It can be terminals(`%t`)
   and non-terminals (`%n`). For example, defenition of binary operation is 
   `@BinOp(%n, %t, %n)`. It defines that `BinOp` node should contains two 
   non-terminals (operands) and one terminal (operation).
 * AST-node usage starts from `#` symbol. Then should be written their
   name that will be translated to Java code as is. It `#BinOp(a, b, c)` in example 
   above.
 * Local names for non-terminals and terminals should be written in square brackets.
   It `term[a]` and `PLUS[b]` in example above. This names will be used to create 
   `BinOp` node: `#BinOp(a, b, c)`.
 * You can return value from method using expressin like `$v` where `v` is name of variable
   (actually all non-terminals will be translated to methods of Java classes). You can
   see examples at the end of each non-terminal.

Thats all. Let's see to grammars for parser again:

```
expr   := term[a] *((PLUS[b] | MINUS[b]) term[c] #BinOp(a, b, c)[a]) $a;
term   := factor[d] *((MUL[e] | DIV[e]) factor[f] #BinOp(d, e, f)[d]) $d;
factor := (PLUS[a] | MINUS[a]) factor[b] #UnaryOp(a,b)[c] $c;
factor := INTEGER[d] #Num(d)[e] $e;
factor := LPAREN expr[f] RPAREN $f;
```

It can be replaced with more compact variant:

```
expr   := term *((PLUS | MINUS) term);
term   := factor *((MUL | DIV) factor);
factor := (PLUS | MINUS) factor | INTEGER | LPAREN expr RPAREN;
```

This is also correct and can be used to generate parser, but you personally 
should write return operators and results from subnodes to local variables.

Other parts o this parser explained in rules above. If you want to understand
how it works you should see the code in this repo. You can set breakpoint and
explore parsed AST tree in IDE.

## Generated source code

Output is a Java-project with files:

 * `AST.java` - abstract AST-node
 * `AST*.java` - types of your AST-nodes
 * `Lexer.java` - lexer
 * `Parser.java` - parser
 * `Token.java` - token's type. Required for lexer
 * `TokenType.java` - enumeration of token types 
 * `Interpreter.java` - stub of interpreter. You should define behavior for 
   each ast-node. It's how to interpret this node. Just define this behavior
   in methods of this class.

## Grammars of laguage for describing grammars

```
program := (rule)* (termdef)* (astdef)*

rule    := nonterm EQ expr SEMI
expr    := (expr2)* (LINE expr)*
expr2   := expr3 | STAR LPAREN expr3 (expr)* RPAREN
expr3   := atom | LPAREN expr RPAREN
atom    := nonterm | term | call | RET
nonterm := NONTERM (NAME | EMPTY)
call    := CALL (NAME | EMPTY)

termdef     := term EQ termexpr SEMI
termexpr    := (termexpr2)* (LINE termexpr)*
termexpr2   := termexpr3 | STAR LPAREN termexpr3 (termexpr)* RPAREN
termexpr3   := termatom | LPAREN termexpr RPAREN
termatom    := SQ <any symbols except single quote> SQ
term        := TERM (NAME | EMPTY)

astdef := ASTNAME LPAREN INTEGER RPAREN SEMI

SQ  := '\''
ASTNAME := '@' ('a'..'z' | 'A'..'Z')*
LPAREN  := '('
RPAREN  := ')'
SEMI    := ';'
NAME    := '[' ('a'..'z' | 'A'..'Z')* ']'
COMMA   := ','
EMPTY   := ''
WORD    := ('a'..'z' | 'A'..'Z')*
INTEGER := ('0'|'1'|'2'|'3'|'4'|'5'|'6'|'7'|'8'|'9') ('0'|'1'|'2'|'3'|'4'|'5'|'6'|'7'|'8'|'9')*
```

## License

MIT
