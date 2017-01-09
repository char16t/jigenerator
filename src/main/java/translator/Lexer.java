package translator;

public class Lexer {
    String text;
    Integer pos;
    Character currentChar;

    public Lexer(String text) {
        this.text = text;
        this.pos = 0;
        this.currentChar = this.text.charAt(this.pos);
    }

    public void error() throws Exception {
        throw new Exception("Invalid character");
    }

    public void advance() {
        this.pos += 1;
        if (this.pos > this.text.length() - 1) {
            this.currentChar = null;
        } else {
            this.currentChar = this.text.charAt(this.pos);
        }
    }

    public void skipWhitespace() {
        while (this.currentChar != null && Character.isWhitespace(this.currentChar)) {
            this.advance();
        }
    }

    public String term() {
        String result = "";
        while (this.currentChar != null && Character.isUpperCase(this.currentChar)) {
            result += this.currentChar;
            this.advance();
        }
        return result;
    }

    public String nonterm() {
        String result = "";
        while (this.currentChar != null && Character.isLowerCase(this.currentChar)) {
            result += this.currentChar;
            this.advance();
        }
        return result;
    }

    public String eq() throws Exception {
        String result = "";
        if (this.currentChar.equals(':')) {
            this.advance();
            if (this.currentChar.equals('=')) {
                this.advance();
                return ":=";
            }
        }
        error();
        return result;
    }

    public String lparen() throws Exception {
        if (this.currentChar.equals('(')) {
            return "(";
        }
        error();
        return null;
    }

    public String rparen() throws Exception {
        if (this.currentChar.equals(')')) {
            return ")";
        }
        error();
        return null;
    }

    public String star() throws Exception {
        if (this.currentChar.equals('*')) {
            return "*";
        }
        error();
        return null;
    }

    public Token getNextToken() throws Exception {
        while (this.currentChar != null) {
            if (Character.isWhitespace(this.currentChar)) {
                this.skipWhitespace();
                continue;
            }

            if (Character.isUpperCase(this.currentChar)) {
                return new Token(TokenType.TERM, this.term());
            }

            if (Character.isLowerCase(this.currentChar)) {
                return new Token(TokenType.NONTERM, this.nonterm());
            }

            if (this.currentChar.equals(':')) {
                return new Token(TokenType.EQ, this.eq());
            }

            if (this.currentChar.equals('|')) {
                this.advance();
                return new Token(TokenType.LINE, "|");
            }

            if (this.currentChar.equals('*')) {
                this.advance();
                return new Token(TokenType.STAR, "*");
            }

            if (this.currentChar.equals('(')) {
                this.advance();
                return new Token(TokenType.LPAREN, "(");
            }

            if (this.currentChar.equals(')')) {
                this.advance();
                return new Token(TokenType.RPAREN, ")");
            }

            if (this.currentChar.equals(';')) {
                this.advance();
                return new Token(TokenType.SEMI, ";");
            }

            this.error();
        }

        return new Token(TokenType.EOF, null);
    }
}
