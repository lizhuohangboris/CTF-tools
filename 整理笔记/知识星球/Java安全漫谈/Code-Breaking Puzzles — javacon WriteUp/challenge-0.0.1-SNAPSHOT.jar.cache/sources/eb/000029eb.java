package org.thymeleaf.util;

import java.io.IOException;
import java.io.Writer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/AbstractLazyCharSequence.class */
public abstract class AbstractLazyCharSequence implements IWritableCharSequence {
    private String resolvedText = null;

    protected abstract String resolveText();

    protected abstract void writeUnresolved(Writer writer) throws IOException;

    private String getText() {
        if (this.resolvedText == null) {
            this.resolvedText = resolveText();
        }
        return this.resolvedText;
    }

    @Override // java.lang.CharSequence
    public final int length() {
        return getText().length();
    }

    @Override // java.lang.CharSequence
    public final char charAt(int index) {
        return getText().charAt(index);
    }

    @Override // java.lang.CharSequence
    public final CharSequence subSequence(int beginIndex, int endIndex) {
        return getText().subSequence(beginIndex, endIndex);
    }

    @Override // org.thymeleaf.util.IWritableCharSequence
    public final void write(Writer writer) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Writer cannot be null");
        }
        if (this.resolvedText != null) {
            writer.write(this.resolvedText);
        } else {
            writeUnresolved(writer);
        }
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AbstractLazyCharSequence that = (AbstractLazyCharSequence) o;
        return getText().equals(that.getText());
    }

    public final int hashCode() {
        return getText().hashCode();
    }

    @Override // java.lang.CharSequence
    public final String toString() {
        return getText();
    }
}