package org.springframework.boot.info;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import org.thymeleaf.engine.XMLDeclaration;
import org.thymeleaf.spring5.processor.SpringInputGeneralFieldTagProcessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/info/BuildProperties.class */
public class BuildProperties extends InfoProperties {
    public BuildProperties(Properties entries) {
        super(processEntries(entries));
    }

    public String getGroup() {
        return get("group");
    }

    public String getArtifact() {
        return get("artifact");
    }

    public String getName() {
        return get("name");
    }

    public String getVersion() {
        return get(XMLDeclaration.ATTRIBUTE_NAME_VERSION);
    }

    public Instant getTime() {
        return getInstant(SpringInputGeneralFieldTagProcessor.TIME_INPUT_TYPE_ATTR_VALUE);
    }

    private static Properties processEntries(Properties properties) {
        coerceDate(properties, SpringInputGeneralFieldTagProcessor.TIME_INPUT_TYPE_ATTR_VALUE);
        return properties;
    }

    private static void coerceDate(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value != null) {
            try {
                String updatedValue = String.valueOf(((Instant) DateTimeFormatter.ISO_INSTANT.parse(value, Instant::from)).toEpochMilli());
                properties.setProperty(key, updatedValue);
            } catch (DateTimeException e) {
            }
        }
    }
}