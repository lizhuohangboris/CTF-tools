package org.hibernate.validator.internal.xml.mapping;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.XMLEvent;
import org.hibernate.validator.internal.xml.AbstractStaxBuilder;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/xml/mapping/ValidStaxBuilder.class */
class ValidStaxBuilder extends AbstractStaxBuilder {
    private static final String VALID_QNAME_LOCAL_PART = "valid";
    private Boolean cascading;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.xml.AbstractStaxBuilder
    public String getAcceptableQName() {
        return VALID_QNAME_LOCAL_PART;
    }

    @Override // org.hibernate.validator.internal.xml.AbstractStaxBuilder
    protected void add(XMLEventReader xmlEventReader, XMLEvent xmlEvent) {
        this.cascading = true;
    }

    public boolean build() {
        return this.cascading != null;
    }
}