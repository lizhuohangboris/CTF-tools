package ch.qos.logback.core.rolling.helper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/rolling/helper/TokenConverter.class */
public class TokenConverter {
    static final int IDENTITY = 0;
    static final int INTEGER = 1;
    static final int DATE = 1;
    int type;
    TokenConverter next;

    protected TokenConverter(int t) {
        this.type = t;
    }

    public TokenConverter getNext() {
        return this.next;
    }

    public void setNext(TokenConverter next) {
        this.next = next;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int i) {
        this.type = i;
    }
}