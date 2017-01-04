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
        while (this.currentChar != null && Character.isSpaceChar(this.currentChar)) {
            this.advance();
        }
    }

    public String nonterm() {
        String result = "";
        // TODO: Is space character a uppercase symbol?
        while (this.currentChar != null && Character.isUpperCase(this.currentChar)) {
            result += this.currentChar;
            this.advance();
        }
        return result;
    }

    public String term() {
        String result = "";
        // TODO: Is space character a lowercase symbol?
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

}
