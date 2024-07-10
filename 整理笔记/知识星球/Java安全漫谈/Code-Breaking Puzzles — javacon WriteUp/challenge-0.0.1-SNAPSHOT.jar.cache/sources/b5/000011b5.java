package org.hibernate.validator.internal.xml.mapping;

import java.util.Optional;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import org.hibernate.validator.internal.xml.AbstractStaxBuilder;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/xml/mapping/AbstractOneLineStringStaxBuilder.class */
abstract class AbstractOneLineStringStaxBuilder extends AbstractStaxBuilder {
    private String value;

    @Override // org.hibernate.validator.internal.xml.AbstractStaxBuilder
    protected void add(XMLEventReader xmlEventReader, XMLEvent xmlEvent) throws XMLStreamException {
        this.value = readSingleElement(xmlEventReader);
    }

    public Optional<String> build() {
        return Optional.ofNullable(this.value);
    }
}