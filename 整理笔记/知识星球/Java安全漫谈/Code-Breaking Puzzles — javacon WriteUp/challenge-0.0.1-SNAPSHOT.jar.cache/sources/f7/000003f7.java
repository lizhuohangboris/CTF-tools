package com.fasterxml.jackson.databind.ext;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.FromStringDeserializer;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/ext/DOMDeserializer.class */
public abstract class DOMDeserializer<T> extends FromStringDeserializer<T> {
    private static final long serialVersionUID = 1;
    private static final DocumentBuilderFactory DEFAULT_PARSER_FACTORY;

    @Override // com.fasterxml.jackson.databind.deser.std.FromStringDeserializer
    public abstract T _deserialize(String str, DeserializationContext deserializationContext);

    static {
        DocumentBuilderFactory parserFactory = DocumentBuilderFactory.newInstance();
        parserFactory.setNamespaceAware(true);
        parserFactory.setExpandEntityReferences(false);
        try {
            parserFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
        } catch (Error e) {
        } catch (ParserConfigurationException e2) {
        }
        DEFAULT_PARSER_FACTORY = parserFactory;
    }

    protected DOMDeserializer(Class<T> cls) {
        super(cls);
    }

    protected final Document parse(String value) throws IllegalArgumentException {
        try {
            return documentBuilder().parse(new InputSource(new StringReader(value)));
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse JSON String as XML: " + e.getMessage(), e);
        }
    }

    protected DocumentBuilder documentBuilder() throws ParserConfigurationException {
        return DEFAULT_PARSER_FACTORY.newDocumentBuilder();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/ext/DOMDeserializer$NodeDeserializer.class */
    public static class NodeDeserializer extends DOMDeserializer<Node> {
        private static final long serialVersionUID = 1;

        public NodeDeserializer() {
            super(Node.class);
        }

        @Override // com.fasterxml.jackson.databind.ext.DOMDeserializer, com.fasterxml.jackson.databind.deser.std.FromStringDeserializer
        public Node _deserialize(String value, DeserializationContext ctxt) throws IllegalArgumentException {
            return parse(value);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/ext/DOMDeserializer$DocumentDeserializer.class */
    public static class DocumentDeserializer extends DOMDeserializer<Document> {
        private static final long serialVersionUID = 1;

        public DocumentDeserializer() {
            super(Document.class);
        }

        @Override // com.fasterxml.jackson.databind.ext.DOMDeserializer, com.fasterxml.jackson.databind.deser.std.FromStringDeserializer
        public Document _deserialize(String value, DeserializationContext ctxt) throws IllegalArgumentException {
            return parse(value);
        }
    }
}