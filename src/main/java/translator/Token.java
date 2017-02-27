package translator;

public final class Token {
    private final TokenType type;
    private final String value;

    public Token(final TokenType type, final String value) {
        this.type = type;
        this.value = value;
    }

    public TokenType type() {
        return type;
    }

    public String value() {
        return value;
    }
}
