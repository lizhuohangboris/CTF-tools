package org.attoparser.dom;

import java.io.Reader;
import org.attoparser.MarkupParser;
import org.attoparser.ParseException;
import org.attoparser.config.ParseConfiguration;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/dom/DOMMarkupParser.class */
public final class DOMMarkupParser implements IDOMMarkupParser {
    private final MarkupParser markupParser;

    public DOMMarkupParser(ParseConfiguration configuration) {
        if (configuration == null) {
            throw new IllegalArgumentException("Configuration cannot be null");
        }
        this.markupParser = new MarkupParser(configuration);
    }

    @Override // org.attoparser.dom.IDOMMarkupParser
    public Document parse(String document) throws ParseException {
        return parse((String) null, document);
    }

    @Override // org.attoparser.dom.IDOMMarkupParser
    public Document parse(char[] document) throws ParseException {
        return parse((String) null, document);
    }

    @Override // org.attoparser.dom.IDOMMarkupParser
    public Document parse(char[] document, int offset, int len) throws ParseException {
        return parse(null, document, offset, len);
    }

    @Override // org.attoparser.dom.IDOMMarkupParser
    public Document parse(Reader reader) throws ParseException {
        return parse((String) null, reader);
    }

    @Override // org.attoparser.dom.IDOMMarkupParser
    public Document parse(String documentName, String document) throws ParseException {
        DOMBuilderMarkupHandler domHandler = new DOMBuilderMarkupHandler(documentName);
        this.markupParser.parse(document, domHandler);
        return domHandler.getDocument();
    }

    @Override // org.attoparser.dom.IDOMMarkupParser
    public Document parse(String documentName, char[] document) throws ParseException {
        DOMBuilderMarkupHandler domHandler = new DOMBuilderMarkupHandler(documentName);
        this.markupParser.parse(document, domHandler);
        return domHandler.getDocument();
    }

    @Override // org.attoparser.dom.IDOMMarkupParser
    public Document parse(String documentName, char[] document, int offset, int len) throws ParseException {
        DOMBuilderMarkupHandler domHandler = new DOMBuilderMarkupHandler(documentName);
        this.markupParser.parse(document, offset, len, domHandler);
        return domHandler.getDocument();
    }

    @Override // org.attoparser.dom.IDOMMarkupParser
    public Document parse(String documentName, Reader reader) throws ParseException {
        DOMBuilderMarkupHandler domHandler = new DOMBuilderMarkupHandler(documentName);
        this.markupParser.parse(reader, domHandler);
        return domHandler.getDocument();
    }
}