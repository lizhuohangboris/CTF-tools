package org.hibernate.validator.internal.xml;

import java.util.Optional;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/xml/AbstractStaxBuilder.class */
public abstract class AbstractStaxBuilder {
    /* JADX INFO: Access modifiers changed from: protected */
    public abstract String getAcceptableQName();

    protected abstract void add(XMLEventReader xMLEventReader, XMLEvent xMLEvent) throws XMLStreamException;

    protected boolean accept(XMLEvent xmlEvent) {
        return xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equals(getAcceptableQName());
    }

    public boolean process(XMLEventReader xmlEventReader, XMLEvent xmlEvent) {
        if (accept(xmlEvent)) {
            try {
                add(xmlEventReader, xmlEvent);
                return true;
            } catch (XMLStreamException e) {
                throw new IllegalStateException((Throwable) e);
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String readSingleElement(XMLEventReader xmlEventReader) throws XMLStreamException {
        XMLEvent xmlEvent = xmlEventReader.nextEvent();
        StringBuilder stringBuilder = new StringBuilder(xmlEvent.asCharacters().getData());
        while (xmlEventReader.peek().isCharacters()) {
            XMLEvent xmlEvent2 = xmlEventReader.nextEvent();
            stringBuilder.append(xmlEvent2.asCharacters().getData());
        }
        return stringBuilder.toString().trim();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Optional<String> readAttribute(StartElement startElement, QName qName) {
        Attribute attribute = startElement.getAttributeByName(qName);
        return Optional.ofNullable(attribute).map((v0) -> {
            return v0.getValue();
        });
    }
}