package org.springframework.util.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;
import org.springframework.lang.Nullable;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/util/xml/StaxEventHandler.class */
class StaxEventHandler extends AbstractStaxHandler {
    private final XMLEventFactory eventFactory;
    private final XMLEventWriter eventWriter;

    public StaxEventHandler(XMLEventWriter eventWriter) {
        this.eventFactory = XMLEventFactory.newInstance();
        this.eventWriter = eventWriter;
    }

    public StaxEventHandler(XMLEventWriter eventWriter, XMLEventFactory factory) {
        this.eventFactory = factory;
        this.eventWriter = eventWriter;
    }

    @Override // org.xml.sax.ContentHandler
    public void setDocumentLocator(@Nullable Locator locator) {
        if (locator != null) {
            this.eventFactory.setLocation(new LocatorLocationAdapter(locator));
        }
    }

    @Override // org.springframework.util.xml.AbstractStaxHandler
    protected void startDocumentInternal() throws XMLStreamException {
        this.eventWriter.add(this.eventFactory.createStartDocument());
    }

    @Override // org.springframework.util.xml.AbstractStaxHandler
    protected void endDocumentInternal() throws XMLStreamException {
        this.eventWriter.add(this.eventFactory.createEndDocument());
    }

    @Override // org.springframework.util.xml.AbstractStaxHandler
    protected void startElementInternal(QName name, Attributes atts, Map<String, String> namespaceMapping) throws XMLStreamException {
        List<Attribute> attributes = getAttributes(atts);
        List<Namespace> namespaces = getNamespaces(namespaceMapping);
        this.eventWriter.add(this.eventFactory.createStartElement(name, attributes.iterator(), namespaces.iterator()));
    }

    private List<Namespace> getNamespaces(Map<String, String> namespaceMappings) {
        List<Namespace> result = new ArrayList<>(namespaceMappings.size());
        namespaceMappings.forEach(prefix, namespaceUri -> {
            result.add(this.eventFactory.createNamespace(prefix, namespaceUri));
        });
        return result;
    }

    private List<Attribute> getAttributes(Attributes attributes) {
        int attrLength = attributes.getLength();
        List<Attribute> result = new ArrayList<>(attrLength);
        for (int i = 0; i < attrLength; i++) {
            QName attrName = toQName(attributes.getURI(i), attributes.getQName(i));
            if (!isNamespaceDeclaration(attrName)) {
                result.add(this.eventFactory.createAttribute(attrName, attributes.getValue(i)));
            }
        }
        return result;
    }

    @Override // org.springframework.util.xml.AbstractStaxHandler
    protected void endElementInternal(QName name, Map<String, String> namespaceMapping) throws XMLStreamException {
        List<Namespace> namespaces = getNamespaces(namespaceMapping);
        this.eventWriter.add(this.eventFactory.createEndElement(name, namespaces.iterator()));
    }

    @Override // org.springframework.util.xml.AbstractStaxHandler
    protected void charactersInternal(String data) throws XMLStreamException {
        this.eventWriter.add(this.eventFactory.createCharacters(data));
    }

    @Override // org.springframework.util.xml.AbstractStaxHandler
    protected void cDataInternal(String data) throws XMLStreamException {
        this.eventWriter.add(this.eventFactory.createCData(data));
    }

    @Override // org.springframework.util.xml.AbstractStaxHandler
    protected void ignorableWhitespaceInternal(String data) throws XMLStreamException {
        this.eventWriter.add(this.eventFactory.createIgnorableSpace(data));
    }

    @Override // org.springframework.util.xml.AbstractStaxHandler
    protected void processingInstructionInternal(String target, String data) throws XMLStreamException {
        this.eventWriter.add(this.eventFactory.createProcessingInstruction(target, data));
    }

    @Override // org.springframework.util.xml.AbstractStaxHandler
    protected void dtdInternal(String dtd) throws XMLStreamException {
        this.eventWriter.add(this.eventFactory.createDTD(dtd));
    }

    @Override // org.springframework.util.xml.AbstractStaxHandler
    protected void commentInternal(String comment) throws XMLStreamException {
        this.eventWriter.add(this.eventFactory.createComment(comment));
    }

    @Override // org.springframework.util.xml.AbstractStaxHandler
    protected void skippedEntityInternal(String name) {
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/util/xml/StaxEventHandler$LocatorLocationAdapter.class */
    private static final class LocatorLocationAdapter implements Location {
        private final Locator locator;

        public LocatorLocationAdapter(Locator locator) {
            this.locator = locator;
        }

        public int getLineNumber() {
            return this.locator.getLineNumber();
        }

        public int getColumnNumber() {
            return this.locator.getColumnNumber();
        }

        public int getCharacterOffset() {
            return -1;
        }

        public String getPublicId() {
            return this.locator.getPublicId();
        }

        public String getSystemId() {
            return this.locator.getSystemId();
        }
    }
}