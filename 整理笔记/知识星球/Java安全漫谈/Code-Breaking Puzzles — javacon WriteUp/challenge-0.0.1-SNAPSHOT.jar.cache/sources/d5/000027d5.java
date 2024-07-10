package org.thymeleaf.engine;

import java.io.IOException;
import java.io.Writer;
import org.thymeleaf.util.IWritableCharSequence;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/AbstractTextualTemplateEvent.class */
abstract class AbstractTextualTemplateEvent extends AbstractTemplateEvent implements IEngineTemplateEvent {
    private final CharSequence contentCharSeq;
    private final String contentStr;
    private final int contentLength;
    private volatile String computedContentStr;
    private volatile int computedContentLength;
    private volatile Boolean computedContentIsWhitespace;
    private volatile Boolean computedContentIsInlineable;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractTextualTemplateEvent(CharSequence content) {
        this.computedContentStr = null;
        this.computedContentLength = -1;
        this.computedContentIsWhitespace = null;
        this.computedContentIsInlineable = null;
        this.contentCharSeq = content;
        if (content != null && (content instanceof String)) {
            this.contentStr = (String) content;
            this.contentLength = content.length();
            return;
        }
        this.contentStr = null;
        this.contentLength = -1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractTextualTemplateEvent(CharSequence content, String templateName, int line, int col) {
        super(templateName, line, col);
        this.computedContentStr = null;
        this.computedContentLength = -1;
        this.computedContentIsWhitespace = null;
        this.computedContentIsInlineable = null;
        this.contentCharSeq = content;
        if (content != null && (content instanceof String)) {
            this.contentStr = (String) content;
            this.contentLength = content.length();
            return;
        }
        this.contentStr = null;
        this.contentLength = -1;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final String getContentText() {
        if (this.contentStr != null || this.contentCharSeq == null) {
            return this.contentStr;
        }
        String t = this.computedContentStr;
        if (t == null) {
            String charSequence = this.contentCharSeq.toString();
            t = charSequence;
            this.computedContentStr = charSequence;
        }
        return t;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final int getContentLength() {
        if (this.contentLength >= 0 || this.contentCharSeq == null) {
            return this.contentLength;
        }
        int l = this.computedContentLength;
        if (l < 0) {
            int length = this.contentCharSeq.length();
            l = length;
            this.computedContentLength = length;
        }
        return l;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final char charAtContent(int index) {
        if (this.contentStr != null) {
            return this.contentStr.charAt(index);
        }
        if (this.computedContentStr != null) {
            return this.computedContentStr.charAt(index);
        }
        return this.contentCharSeq.charAt(index);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final CharSequence contentSubSequence(int start, int end) {
        if (this.contentStr != null) {
            return this.contentStr.subSequence(start, end);
        }
        if (this.computedContentStr != null) {
            return this.computedContentStr.subSequence(start, end);
        }
        return this.contentCharSeq.subSequence(start, end);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isWhitespace() {
        Boolean w = this.computedContentIsWhitespace;
        if (w == null) {
            Boolean computeWhitespace = computeWhitespace();
            w = computeWhitespace;
            this.computedContentIsWhitespace = computeWhitespace;
        }
        return w.booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isInlineable() {
        Boolean i = this.computedContentIsInlineable;
        if (i == null) {
            Boolean computeInlineable = computeInlineable();
            i = computeInlineable;
            this.computedContentIsInlineable = computeInlineable;
        }
        return i.booleanValue();
    }

    private Boolean computeWhitespace() {
        int n = getContentLength();
        if (n == 0) {
            return Boolean.FALSE;
        }
        while (true) {
            int i = n;
            n--;
            if (i != 0) {
                char c = charAtContent(n);
                if (c != ' ' && c != '\n' && !Character.isWhitespace(c)) {
                    return Boolean.FALSE;
                }
            } else {
                return Boolean.TRUE;
            }
        }
    }

    private Boolean computeInlineable() {
        int n = getContentLength();
        if (n == 0) {
            return Boolean.FALSE;
        }
        char c0 = 0;
        int inline = 0;
        while (true) {
            int i = n;
            n--;
            if (i != 0) {
                char c1 = charAtContent(n);
                if (n > 0 && c1 == ']' && c0 == ']') {
                    inline = 1;
                    n--;
                    c1 = charAtContent(n);
                } else if (n > 0 && c1 == ')' && c0 == ']') {
                    inline = 2;
                    n--;
                    c1 = charAtContent(n);
                } else if (inline == 1 && c1 == '[' && c0 == '[') {
                    return Boolean.TRUE;
                } else {
                    if (inline == 2 && c1 == '[' && c0 == '(') {
                        return Boolean.TRUE;
                    }
                }
                c0 = c1;
            } else {
                return Boolean.FALSE;
            }
        }
    }

    public final void writeContent(Writer writer) throws IOException {
        if (this.contentStr != null) {
            writer.write(this.contentStr);
        } else if (this.computedContentStr != null) {
            writer.write(this.computedContentStr);
        } else if (this.contentCharSeq instanceof IWritableCharSequence) {
            ((IWritableCharSequence) this.contentCharSeq).write(writer);
        } else {
            writer.write(getContentText());
        }
    }

    public String toString() {
        return getContentText();
    }
}