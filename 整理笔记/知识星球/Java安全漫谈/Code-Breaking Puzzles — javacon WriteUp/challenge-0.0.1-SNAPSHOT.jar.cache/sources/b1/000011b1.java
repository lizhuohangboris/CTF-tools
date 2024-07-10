package org.hibernate.validator.internal.xml.config;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Map;
import javax.validation.BootstrapConfiguration;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.internal.util.privilegedactions.GetClassLoader;
import org.hibernate.validator.internal.util.privilegedactions.SetContextClassLoader;
import org.hibernate.validator.internal.xml.CloseIgnoringInputStream;
import org.hibernate.validator.internal.xml.XmlParserHelper;
import org.thymeleaf.engine.XMLDeclaration;
import org.xml.sax.SAXException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/xml/config/ValidationXmlParser.class */
public class ValidationXmlParser {
    private static final String VALIDATION_XML_FILE = "META-INF/validation.xml";
    private final ClassLoader externalClassLoader;
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private static final Map<String, String> SCHEMAS_BY_VERSION = Collections.unmodifiableMap(getSchemasByVersion());

    private static Map<String, String> getSchemasByVersion() {
        Map<String, String> schemasByVersion = CollectionHelper.newHashMap(3);
        schemasByVersion.put(XMLDeclaration.DEFAULT_VERSION, "META-INF/validation-configuration-1.0.xsd");
        schemasByVersion.put("1.1", "META-INF/validation-configuration-1.1.xsd");
        schemasByVersion.put("2.0", "META-INF/validation-configuration-2.0.xsd");
        return schemasByVersion;
    }

    public ValidationXmlParser(ClassLoader externalClassLoader) {
        this.externalClassLoader = externalClassLoader;
    }

    public final BootstrapConfiguration parseValidationXml() {
        InputStream in = getValidationXmlInputStream();
        if (in == null) {
            return BootstrapConfigurationImpl.getDefaultBootstrapConfiguration();
        }
        ClassLoader previousTccl = (ClassLoader) run(GetClassLoader.fromContext());
        try {
            try {
                run(SetContextClassLoader.action(ValidationXmlParser.class.getClassLoader()));
                XmlParserHelper xmlParserHelper = new XmlParserHelper();
                in.mark(Integer.MAX_VALUE);
                XMLEventReader xmlEventReader = xmlParserHelper.createXmlEventReader(VALIDATION_XML_FILE, new CloseIgnoringInputStream(in));
                String schemaVersion = xmlParserHelper.getSchemaVersion(VALIDATION_XML_FILE, xmlEventReader);
                xmlEventReader.close();
                in.reset();
                Schema schema = getSchema(xmlParserHelper, schemaVersion);
                Validator validator = schema.newValidator();
                validator.validate(new StreamSource(new CloseIgnoringInputStream(in)));
                in.reset();
                XMLEventReader xmlEventReader2 = xmlParserHelper.createXmlEventReader(VALIDATION_XML_FILE, new CloseIgnoringInputStream(in));
                ValidationConfigStaxBuilder validationConfigStaxBuilder = new ValidationConfigStaxBuilder(xmlEventReader2);
                xmlEventReader2.close();
                in.reset();
                BootstrapConfiguration build = validationConfigStaxBuilder.build();
                run(SetContextClassLoader.action(previousTccl));
                closeStream(in);
                return build;
            } catch (XMLStreamException | IOException | SAXException e) {
                throw LOG.getUnableToParseValidationXmlFileException(VALIDATION_XML_FILE, e);
            }
        } catch (Throwable th) {
            run(SetContextClassLoader.action(previousTccl));
            closeStream(in);
            throw th;
        }
    }

    private InputStream getValidationXmlInputStream() {
        LOG.debugf("Trying to load %s for XML based Validator configuration.", VALIDATION_XML_FILE);
        InputStream inputStream = ResourceLoaderHelper.getResettableInputStreamForPath(VALIDATION_XML_FILE, this.externalClassLoader);
        if (inputStream != null) {
            return inputStream;
        }
        LOG.debugf("No %s found. Using annotation based configuration only.", VALIDATION_XML_FILE);
        return null;
    }

    private Schema getSchema(XmlParserHelper xmlParserHelper, String schemaVersion) {
        String schemaResource = SCHEMAS_BY_VERSION.get(schemaVersion);
        if (schemaResource == null) {
            throw LOG.getUnsupportedSchemaVersionException(VALIDATION_XML_FILE, schemaVersion);
        }
        Schema schema = xmlParserHelper.getSchema(schemaResource);
        if (schema == null) {
            throw LOG.unableToGetXmlSchema(schemaResource);
        }
        return schema;
    }

    private void closeStream(InputStream inputStream) {
        try {
            inputStream.close();
        } catch (IOException e) {
            LOG.unableToCloseXMLFileInputStream(VALIDATION_XML_FILE);
        }
    }

    private static <T> T run(PrivilegedAction<T> action) {
        return System.getSecurityManager() != null ? (T) AccessController.doPrivileged(action) : action.run();
    }
}