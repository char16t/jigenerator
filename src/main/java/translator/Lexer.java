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

    private String term() {
        String result = "";
        while (this.currentChar != null && Character.isUpperCase(this.currentChar)) {
            result += this.currentChar;
            this.advance();
        }
        return result;
    }

    private String nonterm() {
        String result = "";
        while (this.currentChar != null && Character.isLowerCase(this.currentChar)) {
            result += this.currentChar;
            this.advance();
        }
        return result;
    }

    private String eq() throws Exception {
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

    private String lparen() throws Exception {
        if (this.currentChar.equals('(')) {
            return "(";
        }
        error();
        return null;
    }

    private String rparen() throws Exception {
        if (this.currentChar.equals(')')) {
            return ")";
        }
        error();
        return null;
    }

    private String star() throws Exception {
        if (this.currentChar.equals('*')) {
            return "*";
        }
        error();
        return null;
    }

    private String quoted() throws Exception {
        String result = "";

        if (this.currentChar != '\'') {
            this.error();
        }
        this.advance();

        while (this.currentChar != '\'') {
            result += this.currentChar.toString();
            this.advance();
        }
        this.advance();

        return result;
    }

    private String astname() {
        String result = "";
        if (this.currentChar == '@') {
            this.advance();
        }
        while (this.currentChar != null && (Character.isLowerCase(this.currentChar) || Character.isUpperCase(this.currentChar))) {
            result += this.currentChar;
            this.advance();
        }
        return result;
    }

    private String astargtype() {
        String result = "";
        if (this.currentChar == '%') {
            this.advance();
        }
        if (this.currentChar == 't' || this.currentChar == 'n') {
            result += this.currentChar;
            this.advance();
        }
        return result;
    }

    private String ret() {
        String result = "";
        if (this.currentChar == '$') {
            this.advance();
        }
        while (this.currentChar != null && (Character.isLowerCase(this.currentChar) || Character.isUpperCase(this.currentChar))) {
            result += this.currentChar;
            this.advance();
        }
        return result;
    }

    // todo: need for review grammars
    private String call() {
        String result = "";
        if (this.currentChar == '#') {
            this.advance();
        }
        while (this.currentChar != null && (Character.isLowerCase(this.currentChar) || Character.isUpperCase(this.currentChar))) {
            result += this.currentChar;
            this.advance();
        }
        if (this.currentChar == '(') {
            result += '(';
            this.advance();
        }

        while (this.currentChar != null && this.currentChar != ')' && (Character.isLowerCase(this.currentChar) || Character.isUpperCase(this.currentChar) || this.currentChar == ' '|| this.currentChar == ',')) {
            result += this.currentChar;
            this.advance();
        }

        if (this.currentChar == ')') {
            result += ')';
            this.advance();
        }
        return "AST" + result;
    }

    private String name() {
        String result = "";
        if (this.currentChar == '[') {
            this.advance();
        }
        while (this.currentChar != null && (Character.isLowerCase(this.currentChar) || Character.isUpperCase(this.currentChar))) {
            result += this.currentChar;
            this.advance();
        }
        if (this.currentChar == ']') {
            this.advance();
        }
        return result;
    }

    private String integer() throws Exception {
        String result = "";
        if (this.currentChar.equals('0') || this.currentChar.equals('1') || this.currentChar.equals('2') || this.currentChar.equals('3') || this.currentChar.equals('4') || this.currentChar.equals('5') || this.currentChar.equals('6') || this.currentChar.equals('7') || this.currentChar.equals('8') || this.currentChar.equals('9')) {
            if (this.currentChar.equals('0')) {
                this.advance();
                result += "0";
            }
            if (this.currentChar.equals('1')) {
                this.advance();
                result += "1";
            }
            if (this.currentChar.equals('2')) {
                this.advance();
                result += "2";
            }
            if (this.currentChar.equals('3')) {
                this.advance();
                result += "3";
            }
            if (this.currentChar.equals('4')) {
                this.advance();
                result += "4";
            }
            if (this.currentChar.equals('5')) {
                this.advance();
                result += "5";
            }
            if (this.currentChar.equals('6')) {
                this.advance();
                result += "6";
            }
            if (this.currentChar.equals('7')) {
                this.advance();
                result += "7";
            }
            if (this.currentChar.equals('8')) {
                this.advance();
                result += "8";
            }
            if (this.currentChar.equals('9')) {
                this.advance();
                result += "9";
            }
        } else {
            this.error();
        }
        while (this.currentChar.equals('0') || this.currentChar.equals('1') || this.currentChar.equals('2') || this.currentChar.equals('3') || this.currentChar.equals('4') || this.currentChar.equals('5') || this.currentChar.equals('6') || this.currentChar.equals('7') || this.currentChar.equals('8') || this.currentChar.equals('9')) {
            if (this.currentChar.equals('0') || this.currentChar.equals('1') || this.currentChar.equals('2') || this.currentChar.equals('3') || this.currentChar.equals('4') || this.currentChar.equals('5') || this.currentChar.equals('6') || this.currentChar.equals('7') || this.currentChar.equals('8') || this.currentChar.equals('9')) {
                if (this.currentChar.equals('0')) {
                    this.advance();
                    result += "0";
                }
                if (this.currentChar.equals('1')) {
                    this.advance();
                    result += "1";
                }
                if (this.currentChar.equals('2')) {
                    this.advance();
                    result += "2";
                }
                if (this.currentChar.equals('3')) {
                    this.advance();
                    result += "3";
                }
                if (this.currentChar.equals('4')) {
                    this.advance();
                    result += "4";
                }
                if (this.currentChar.equals('5')) {
                    this.advance();
                    result += "5";
                }
                if (this.currentChar.equals('6')) {
                    this.advance();
                    result += "6";
                }
                if (this.currentChar.equals('7')) {
                    this.advance();
                    result += "7";
                }
                if (this.currentChar.equals('8')) {
                    this.advance();
                    result += "8";
                }
                if (this.currentChar.equals('9')) {
                    this.advance();
                    result += "9";
                }
            } else {
                this.error();
            }
        }

        return result;
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

            if (this.currentChar.equals('\'')) {
                return new Token(TokenType.QUOTED, this.quoted());
            }

            if (this.currentChar.equals('#')) {
                return new Token(TokenType.CALL, this.call());
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

            if (this.currentChar.equals('@')) {
                return new Token(TokenType.ASTNAME, this.astname());
            }

            if (this.currentChar.equals(',')) {
                this.advance();
                return new Token(TokenType.COMMA, ",");
            }

            if (this.currentChar.equals('$')) {
                return new Token(TokenType.RET, this.ret());
            }

            if (this.currentChar.equals('%')) {
                return new Token(TokenType.ASTARGTYPE, this.astargtype());
            }

            if (this.currentChar.equals('[')) {
                return new Token(TokenType.NAME, this.name());
            }

            if (this.currentChar.equals('0') || this.currentChar.equals('1') || this.currentChar.equals('2') || this.currentChar.equals('3') || this.currentChar.equals('4') || this.currentChar.equals('5') || this.currentChar.equals('6') || this.currentChar.equals('7') || this.currentChar.equals('8') || this.currentChar.equals('9')) {
                return new Token(TokenType.INTEGER, this.integer());
            }

            this.error();
        }

        return new Token(TokenType.EOF, null);
    }

}
