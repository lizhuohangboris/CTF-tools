package org.yaml.snakeyaml.tokens;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.tokens.Token;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/tokens/AnchorToken.class */
public final class AnchorToken extends Token {
    private final String value;

    public AnchorToken(String value, Mark startMark, Mark endMark) {
        super(startMark, endMark);
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    @Override // org.yaml.snakeyaml.tokens.Token
    public Token.ID getTokenId() {
        return Token.ID.Anchor;
    }
}