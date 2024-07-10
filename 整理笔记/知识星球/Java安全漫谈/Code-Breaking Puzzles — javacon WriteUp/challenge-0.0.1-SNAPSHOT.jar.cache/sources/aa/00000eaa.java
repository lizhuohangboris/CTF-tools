package org.attoparser.simple;

import java.io.Reader;
import org.attoparser.MarkupParser;
import org.attoparser.ParseException;
import org.attoparser.config.ParseConfiguration;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/simple/SimpleMarkupParser.class */
public final class SimpleMarkupParser implements ISimpleMarkupParser {
    private final MarkupParser markupParser;

    public SimpleMarkupParser(ParseConfiguration configuration) {
        if (configuration == null) {
            throw new IllegalArgumentException("Configuration cannot be null");
        }
        this.markupParser = new MarkupParser(configuration);
    }

    @Override // org.attoparser.simple.ISimpleMarkupParser
    public void parse(String document, ISimpleMarkupHandler handler) throws ParseException {
        if (handler == null) {
            throw new IllegalArgumentException("Handler cannot be null");
        }
        this.markupParser.parse(document, new SimplifierMarkupHandler(handler));
    }

    @Override // org.attoparser.simple.ISimpleMarkupParser
    public void parse(char[] document, ISimpleMarkupHandler handler) throws ParseException {
        if (handler == null) {
            throw new IllegalArgumentException("Handler cannot be null");
        }
        this.markupParser.parse(document, new SimplifierMarkupHandler(handler));
    }

    @Override // org.attoparser.simple.ISimpleMarkupParser
    public void parse(char[] document, int offset, int len, ISimpleMarkupHandler handler) throws ParseException {
        if (handler == null) {
            throw new IllegalArgumentException("Handler cannot be null");
        }
        this.markupParser.parse(document, offset, len, new SimplifierMarkupHandler(handler));
    }

    @Override // org.attoparser.simple.ISimpleMarkupParser
    public void parse(Reader reader, ISimpleMarkupHandler handler) throws ParseException {
        if (handler == null) {
            throw new IllegalArgumentException("Handler cannot be null");
        }
        this.markupParser.parse(reader, new SimplifierMarkupHandler(handler));
    }
}