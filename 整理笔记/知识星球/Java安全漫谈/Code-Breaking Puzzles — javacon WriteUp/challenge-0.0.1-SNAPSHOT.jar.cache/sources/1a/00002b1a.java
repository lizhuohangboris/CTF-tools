package org.yaml.snakeyaml.tokens;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.tokens.Token;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/tokens/BlockEndToken.class */
public final class BlockEndToken extends Token {
    public BlockEndToken(Mark startMark, Mark endMark) {
        super(startMark, endMark);
    }

    @Override // org.yaml.snakeyaml.tokens.Token
    public Token.ID getTokenId() {
        return Token.ID.BlockEnd;
    }
}