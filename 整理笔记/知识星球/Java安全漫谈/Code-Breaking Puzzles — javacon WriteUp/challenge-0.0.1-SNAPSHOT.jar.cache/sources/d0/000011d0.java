package org.hibernate.validator.internal.xml.mapping;

import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.validation.groups.Default;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.internal.xml.AbstractStaxBuilder;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/xml/mapping/GroupConversionStaxBuilder.class */
class GroupConversionStaxBuilder extends AbstractStaxBuilder {
    private static final String GROUP_CONVERSION_TYPE_QNAME_LOCAL_PART = "convert-group";
    private final ClassLoadingHelper classLoadingHelper;
    private final DefaultPackageStaxBuilder defaultPackageStaxBuilder;
    private final Map<String, List<String>> groupConversionRules = new HashMap();
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private static final QName FROM_QNAME = new QName("from");
    private static final QName TO_QNAME = new QName("to");
    private static final String DEFAULT_GROUP_NAME = Default.class.getName();

    /* JADX INFO: Access modifiers changed from: package-private */
    public GroupConversionStaxBuilder(ClassLoadingHelper classLoadingHelper, DefaultPackageStaxBuilder defaultPackageStaxBuilder) {
        this.classLoadingHelper = classLoadingHelper;
        this.defaultPackageStaxBuilder = defaultPackageStaxBuilder;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.xml.AbstractStaxBuilder
    public String getAcceptableQName() {
        return GROUP_CONVERSION_TYPE_QNAME_LOCAL_PART;
    }

    @Override // org.hibernate.validator.internal.xml.AbstractStaxBuilder
    protected void add(XMLEventReader xmlEventReader, XMLEvent xmlEvent) {
        StartElement startElement = xmlEvent.asStartElement();
        String from = readAttribute(startElement, FROM_QNAME).orElse(DEFAULT_GROUP_NAME);
        String to = readAttribute(startElement, TO_QNAME).get();
        this.groupConversionRules.merge(from, Collections.singletonList(to), v1, v2 -> {
            return (List) Stream.concat(v1.stream(), v2.stream()).collect(Collectors.toList());
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Map<Class<?>, Class<?>> build() {
        String defaultPackage = this.defaultPackageStaxBuilder.build().orElse("");
        Map<Class<?>, List<Class<?>>> resultingMapping = (Map) this.groupConversionRules.entrySet().stream().collect(Collectors.groupingBy(entry -> {
            return this.classLoadingHelper.loadClass((String) entry.getKey(), defaultPackage);
        }, Collectors.collectingAndThen(Collectors.toList(), entries -> {
            return (List) entries.stream().flatMap(entry2 -> {
                return ((List) entry2.getValue()).stream();
            }).map(className -> {
                return this.classLoadingHelper.loadClass(className, defaultPackage);
            }).collect(Collectors.toList());
        })));
        for (Map.Entry<Class<?>, List<Class<?>>> entry2 : resultingMapping.entrySet()) {
            if (entry2.getValue().size() > 1) {
                throw LOG.getMultipleGroupConversionsForSameSourceException(entry2.getKey(), entry2.getValue());
            }
        }
        return (Map) resultingMapping.entrySet().stream().collect(Collectors.toMap((v0) -> {
            return v0.getKey();
        }, entry3 -> {
            return (Class) ((List) entry3.getValue()).get(0);
        }));
    }
}