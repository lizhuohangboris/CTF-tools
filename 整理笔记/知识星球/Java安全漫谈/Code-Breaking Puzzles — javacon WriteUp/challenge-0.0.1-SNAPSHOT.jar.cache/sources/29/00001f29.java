package org.springframework.expression.spel;

import org.springframework.expression.ParseException;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/SpelParseException.class */
public class SpelParseException extends ParseException {
    private final SpelMessage message;
    private final Object[] inserts;

    public SpelParseException(@Nullable String expressionString, int position, SpelMessage message, Object... inserts) {
        super(expressionString, position, message.formatMessage(inserts));
        this.message = message;
        this.inserts = inserts;
    }

    public SpelParseException(int position, SpelMessage message, Object... inserts) {
        super(position, message.formatMessage(inserts));
        this.message = message;
        this.inserts = inserts;
    }

    public SpelParseException(int position, Throwable cause, SpelMessage message, Object... inserts) {
        super(position, message.formatMessage(inserts), cause);
        this.message = message;
        this.inserts = inserts;
    }

    public SpelMessage getMessageCode() {
        return this.message;
    }

    public Object[] getInserts() {
        return this.inserts;
    }
}