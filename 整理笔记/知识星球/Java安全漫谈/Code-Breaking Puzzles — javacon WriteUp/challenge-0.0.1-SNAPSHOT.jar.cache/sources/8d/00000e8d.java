package org.attoparser.output;

import java.io.Writer;
import org.attoparser.AbstractMarkupHandler;
import org.attoparser.ParseException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/output/TextOutputMarkupHandler.class */
public final class TextOutputMarkupHandler extends AbstractMarkupHandler {
    private final Writer writer;

    public TextOutputMarkupHandler(Writer writer) {
        if (writer == null) {
            throw new IllegalArgumentException("Writer cannot be null");
        }
        this.writer = writer;
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.ITextHandler
    public void handleText(char[] buffer, int offset, int len, int line, int col) throws ParseException {
        try {
            this.writer.write(buffer, offset, len);
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }
}