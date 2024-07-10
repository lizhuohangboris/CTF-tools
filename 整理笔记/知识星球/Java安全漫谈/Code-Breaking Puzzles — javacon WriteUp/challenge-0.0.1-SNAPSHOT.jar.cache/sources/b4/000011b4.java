package org.hibernate.validator.internal.xml.mapping;

import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import org.hibernate.validator.internal.xml.AbstractStaxBuilder;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/xml/mapping/AbstractMultiValuedElementStaxBuilder.class */
abstract class AbstractMultiValuedElementStaxBuilder extends AbstractStaxBuilder {
    private static final String VALUE_QNAME_LOCAL_PART = "value";
    private static final Class<?>[] EMPTY_CLASSES_ARRAY = new Class[0];
    private final ClassLoadingHelper classLoadingHelper;
    private final DefaultPackageStaxBuilder defaultPackageStaxBuilder;
    private final List<String> values = new ArrayList();

    public abstract void verifyClass(Class<?> cls);

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractMultiValuedElementStaxBuilder(ClassLoadingHelper classLoadingHelper, DefaultPackageStaxBuilder defaultPackageStaxBuilder) {
        this.classLoadingHelper = classLoadingHelper;
        this.defaultPackageStaxBuilder = defaultPackageStaxBuilder;
    }

    @Override // org.hibernate.validator.internal.xml.AbstractStaxBuilder
    protected void add(XMLEventReader xmlEventReader, XMLEvent xmlEvent) throws XMLStreamException {
        while (true) {
            if (!xmlEvent.isEndElement() || !xmlEvent.asEndElement().getName().getLocalPart().equals(getAcceptableQName())) {
                xmlEvent = xmlEventReader.nextEvent();
                if (xmlEvent.isStartElement() && xmlEvent.asStartElement().getName().getLocalPart().equals("value")) {
                    this.values.add(readSingleElement(xmlEventReader));
                }
            } else {
                return;
            }
        }
    }

    public Class<?>[] build() {
        String defaultPackage = this.defaultPackageStaxBuilder.build().orElse("");
        if (this.values.isEmpty()) {
            return EMPTY_CLASSES_ARRAY;
        }
        return (Class[]) this.values.stream().map(valueClass -> {
            return this.classLoadingHelper.loadClass(valueClass, defaultPackage);
        }).peek(this::verifyClass).toArray(x$0 -> {
            return new Class[x$0];
        });
    }
}