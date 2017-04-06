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

    public String plus() throws Exception {
        String result = "";
        if (this.currentChar != null && this.currentChar.equals('+')) {
            this.advance();
            result += "+";
        }
        return result;
    }

    public String minus() throws Exception {
        String result = "";
        if (this.currentChar != null && this.currentChar.equals('-')) {
            this.advance();
            result += "-";
        }
        return result;
    }

    public String mul() throws Exception {
        String result = "";
        if (this.currentChar != null && this.currentChar.equals('*')) {
            this.advance();
            result += "*";
        }
        return result;
    }

    public String div() throws Exception {
        String result = "";
        if (this.currentChar != null && this.currentChar.equals('/')) {
            this.advance();
            result += "/";
        }
        return result;
    }

    public String eq() throws Exception {
        String result = "";
        if (this.currentChar != null && this.currentChar.equals('=')) {
            this.advance();
            if (this.currentChar != null && this.currentChar.equals(':')) {
                this.advance();
                result += ":=";
            }
        }
        return result;
    }

    public String lparen() throws Exception {
        String result = "";
        if (this.currentChar != null && this.currentChar.equals('(')) {
            this.advance();
            result += "(";
        }
        return result;
    }

    public String rparen() throws Exception {
        String result = "";
        if (this.currentChar != null && this.currentChar.equals(')')) {
            this.advance();
            result += ")";
        }
        return result;
    }

    public String integer() throws Exception {
        String result = "";
        if (this.currentChar.equals('0') || this.currentChar.equals('1') || this.currentChar.equals('2') || this.currentChar.equals('3') || this.currentChar.equals('4') || this.currentChar.equals('5') || this.currentChar.equals('6') || this.currentChar.equals('7') || this.currentChar.equals('8') || this.currentChar.equals('9')) {
            if (this.currentChar != null && this.currentChar.equals('0')) {
                this.advance();
                result += "0";
            }
            if (this.currentChar != null && this.currentChar.equals('1')) {
                this.advance();
                result += "1";
            }
            if (this.currentChar != null && this.currentChar.equals('2')) {
                this.advance();
                result += "2";
            }
            if (this.currentChar != null && this.currentChar.equals('3')) {
                this.advance();
                result += "3";
            }
            if (this.currentChar != null && this.currentChar.equals('4')) {
                this.advance();
                result += "4";
            }
            if (this.currentChar != null && this.currentChar.equals('5')) {
                this.advance();
                result += "5";
            }
            if (this.currentChar != null && this.currentChar.equals('6')) {
                this.advance();
                result += "6";
            }
            if (this.currentChar != null && this.currentChar.equals('7')) {
                this.advance();
                result += "7";
            }
            if (this.currentChar != null && this.currentChar.equals('8')) {
                this.advance();
                result += "8";
            }
            if (this.currentChar != null && this.currentChar.equals('9')) {
                this.advance();
                result += "9";
            }
        } else {
            this.error();
        }
        while (this.currentChar != null && (this.currentChar.equals('0') || this.currentChar.equals('1') || this.currentChar.equals('2') || this.currentChar.equals('3') || this.currentChar.equals('4') || this.currentChar.equals('5') || this.currentChar.equals('6') || this.currentChar.equals('7') || this.currentChar.equals('8') || this.currentChar.equals('9'))) {
            if (this.currentChar.equals('0') || this.currentChar.equals('1') || this.currentChar.equals('2') || this.currentChar.equals('3') || this.currentChar.equals('4') || this.currentChar.equals('5') || this.currentChar.equals('6') || this.currentChar.equals('7') || this.currentChar.equals('8') || this.currentChar.equals('9')) {
                if (this.currentChar != null && this.currentChar.equals('0')) {
                    this.advance();
                    result += "0";
                }
                if (this.currentChar != null && this.currentChar.equals('1')) {
                    this.advance();
                    result += "1";
                }
                if (this.currentChar != null && this.currentChar.equals('2')) {
                    this.advance();
                    result += "2";
                }
                if (this.currentChar != null && this.currentChar.equals('3')) {
                    this.advance();
                    result += "3";
                }
                if (this.currentChar != null && this.currentChar.equals('4')) {
                    this.advance();
                    result += "4";
                }
                if (this.currentChar != null && this.currentChar.equals('5')) {
                    this.advance();
                    result += "5";
                }
                if (this.currentChar != null && this.currentChar.equals('6')) {
                    this.advance();
                    result += "6";
                }
                if (this.currentChar != null && this.currentChar.equals('7')) {
                    this.advance();
                    result += "7";
                }
                if (this.currentChar != null && this.currentChar.equals('8')) {
                    this.advance();
                    result += "8";
                }
                if (this.currentChar != null && this.currentChar.equals('9')) {
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
            if (this.currentChar.equals('+')) {
                return new Token(TokenType.PLUS, this.plus());
            }

            if (this.currentChar.equals('-')) {
                return new Token(TokenType.MINUS, this.minus());
            }

            if (this.currentChar.equals('*')) {
                return new Token(TokenType.MUL, this.mul());
            }

            if (this.currentChar.equals('/')) {
                return new Token(TokenType.DIV, this.div());
            }

            if (this.currentChar.equals(':')) {
                return new Token(TokenType.EQ, this.eq());
            }

            if (this.currentChar.equals('(')) {
                return new Token(TokenType.LPAREN, this.lparen());
            }

            if (this.currentChar.equals(')')) {
                return new Token(TokenType.RPAREN, this.rparen());
            }

            if (this.currentChar.equals('0') || this.currentChar.equals('1') || this.currentChar.equals('2') || this.currentChar.equals('3') || this.currentChar.equals('4') || this.currentChar.equals('5') || this.currentChar.equals('6') || this.currentChar.equals('7') || this.currentChar.equals('8') || this.currentChar.equals('9')) {
                return new Token(TokenType.INTEGER, this.integer());
            }

            this.error();
        }
        return new Token(TokenType.EOF, null);
    }
}