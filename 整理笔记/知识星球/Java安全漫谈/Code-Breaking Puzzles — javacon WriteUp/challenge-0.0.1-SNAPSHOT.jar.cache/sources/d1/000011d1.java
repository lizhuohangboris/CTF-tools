package org.hibernate.validator.internal.xml.mapping;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.core.AnnotationProcessingOptions;
import org.hibernate.validator.internal.metadata.core.AnnotationProcessingOptionsImpl;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.raw.ConstrainedElement;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.internal.util.privilegedactions.GetClassLoader;
import org.hibernate.validator.internal.util.privilegedactions.SetContextClassLoader;
import org.hibernate.validator.internal.xml.CloseIgnoringInputStream;
import org.hibernate.validator.internal.xml.XmlParserHelper;
import org.thymeleaf.engine.XMLDeclaration;
import org.xml.sax.SAXException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/xml/mapping/MappingXmlParser.class */
public class MappingXmlParser {
    private final ConstraintHelper constraintHelper;
    private final TypeResolutionHelper typeResolutionHelper;
    private final ValueExtractorManager valueExtractorManager;
    private final ClassLoadingHelper classLoadingHelper;
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private static final Map<String, String> SCHEMAS_BY_VERSION = Collections.unmodifiableMap(getSchemasByVersion());
    private final Set<Class<?>> processedClasses = CollectionHelper.newHashSet();
    private final AnnotationProcessingOptionsImpl annotationProcessingOptions = new AnnotationProcessingOptionsImpl();
    private final Map<Class<?>, List<Class<?>>> defaultSequences = CollectionHelper.newHashMap();
    private final Map<Class<?>, Set<ConstrainedElement>> constrainedElements = CollectionHelper.newHashMap();
    private final XmlParserHelper xmlParserHelper = new XmlParserHelper();

    private static Map<String, String> getSchemasByVersion() {
        Map<String, String> schemasByVersion = new HashMap<>();
        schemasByVersion.put(XMLDeclaration.DEFAULT_VERSION, "META-INF/validation-mapping-1.0.xsd");
        schemasByVersion.put("1.1", "META-INF/validation-mapping-1.1.xsd");
        schemasByVersion.put("2.0", "META-INF/validation-mapping-2.0.xsd");
        return schemasByVersion;
    }

    public MappingXmlParser(ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager, ClassLoader externalClassLoader) {
        this.constraintHelper = constraintHelper;
        this.typeResolutionHelper = typeResolutionHelper;
        this.valueExtractorManager = valueExtractorManager;
        this.classLoadingHelper = new ClassLoadingHelper(externalClassLoader, (ClassLoader) run(GetClassLoader.fromContext()));
    }

    public final void parse(Set<InputStream> mappingStreams) {
        ClassLoader previousTccl = (ClassLoader) run(GetClassLoader.fromContext());
        try {
            try {
                run(SetContextClassLoader.action(MappingXmlParser.class.getClassLoader()));
                Set<String> alreadyProcessedConstraintDefinitions = CollectionHelper.newHashSet();
                for (InputStream in : mappingStreams) {
                    in.mark(Integer.MAX_VALUE);
                    XMLEventReader xmlEventReader = this.xmlParserHelper.createXmlEventReader("constraint mapping file", new CloseIgnoringInputStream(in));
                    String schemaVersion = this.xmlParserHelper.getSchemaVersion("constraint mapping file", xmlEventReader);
                    xmlEventReader.close();
                    in.reset();
                    String schemaResourceName = getSchemaResourceName(schemaVersion);
                    Schema schema = this.xmlParserHelper.getSchema(schemaResourceName);
                    if (schema == null) {
                        throw LOG.unableToGetXmlSchema(schemaResourceName);
                    }
                    Validator validator = schema.newValidator();
                    validator.validate(new StreamSource(new CloseIgnoringInputStream(in)));
                    in.reset();
                    ConstraintMappingsStaxBuilder constraintMappingsStaxBuilder = new ConstraintMappingsStaxBuilder(this.classLoadingHelper, this.constraintHelper, this.typeResolutionHelper, this.valueExtractorManager, this.annotationProcessingOptions, this.defaultSequences);
                    XMLEventReader xmlEventReader2 = this.xmlParserHelper.createXmlEventReader("constraint mapping file", new CloseIgnoringInputStream(in));
                    while (xmlEventReader2.hasNext()) {
                        constraintMappingsStaxBuilder.process(xmlEventReader2, xmlEventReader2.nextEvent());
                    }
                    constraintMappingsStaxBuilder.build(this.processedClasses, this.constrainedElements, alreadyProcessedConstraintDefinitions);
                    xmlEventReader2.close();
                    in.reset();
                }
            } catch (IOException | XMLStreamException | SAXException e) {
                throw LOG.getErrorParsingMappingFileException(e);
            }
        } finally {
            run(SetContextClassLoader.action(previousTccl));
        }
    }

    public final Set<Class<?>> getXmlConfiguredClasses() {
        return this.processedClasses;
    }

    public final AnnotationProcessingOptions getAnnotationProcessingOptions() {
        return this.annotationProcessingOptions;
    }

    public final Set<ConstrainedElement> getConstrainedElementsForClass(Class<?> beanClass) {
        if (this.constrainedElements.containsKey(beanClass)) {
            return this.constrainedElements.get(beanClass);
        }
        return Collections.emptySet();
    }

    public final List<Class<?>> getDefaultSequenceForClass(Class<?> beanClass) {
        return this.defaultSequences.get(beanClass);
    }

    private String getSchemaResourceName(String schemaVersion) {
        String schemaResource = SCHEMAS_BY_VERSION.get(schemaVersion);
        if (schemaResource == null) {
            throw LOG.getUnsupportedSchemaVersionException("constraint mapping file", schemaVersion);
        }
        return schemaResource;
    }

    private static <T> T run(PrivilegedAction<T> action) {
        return System.getSecurityManager() != null ? (T) AccessController.doPrivileged(action) : action.run();
    }
}