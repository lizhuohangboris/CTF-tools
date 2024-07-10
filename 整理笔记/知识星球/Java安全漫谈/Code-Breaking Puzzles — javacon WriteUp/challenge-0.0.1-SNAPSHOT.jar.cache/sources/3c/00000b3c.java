package org.apache.logging.log4j.message;

import java.io.Serializable;
import org.apache.logging.log4j.util.StringBuilderFormattable;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/message/StructuredDataId.class */
public class StructuredDataId implements Serializable, StringBuilderFormattable {
    public static final StructuredDataId TIME_QUALITY = new StructuredDataId("timeQuality", null, new String[]{"tzKnown", "isSynced", "syncAccuracy"});
    public static final StructuredDataId ORIGIN = new StructuredDataId("origin", null, new String[]{"ip", "enterpriseId", "software", "swVersion"});
    public static final StructuredDataId META = new StructuredDataId(BeanDefinitionParserDelegate.META_ELEMENT, null, new String[]{"sequenceId", "sysUpTime", "language"});
    public static final int RESERVED = -1;
    private static final long serialVersionUID = 9031746276396249990L;
    private static final int MAX_LENGTH = 32;
    private static final String AT_SIGN = "@";
    private final String name;
    private final int enterpriseNumber;
    private final String[] required;
    private final String[] optional;

    public StructuredDataId(String name) {
        this(name, (String[]) null, (String[]) null, 32);
    }

    public StructuredDataId(String name, int maxLength) {
        this(name, (String[]) null, (String[]) null, maxLength);
    }

    public StructuredDataId(String name, String[] required, String[] optional) {
        this(name, required, optional, 32);
    }

    public StructuredDataId(String name, String[] required, String[] optional, int maxLength) {
        int index = -1;
        if (name != null) {
            if (maxLength > 0 && name.length() > 32) {
                throw new IllegalArgumentException(String.format("Length of id %s exceeds maximum of %d characters", name, Integer.valueOf(maxLength)));
            }
            index = name.indexOf(AT_SIGN);
        }
        if (index > 0) {
            this.name = name.substring(0, index);
            this.enterpriseNumber = Integer.parseInt(name.substring(index + 1));
        } else {
            this.name = name;
            this.enterpriseNumber = -1;
        }
        this.required = required;
        this.optional = optional;
    }

    public StructuredDataId(String name, int enterpriseNumber, String[] required, String[] optional) {
        this(name, enterpriseNumber, required, optional, 32);
    }

    public StructuredDataId(String name, int enterpriseNumber, String[] required, String[] optional, int maxLength) {
        if (name == null) {
            throw new IllegalArgumentException("No structured id name was supplied");
        }
        if (name.contains(AT_SIGN)) {
            throw new IllegalArgumentException("Structured id name cannot contain an " + Strings.quote(AT_SIGN));
        }
        if (enterpriseNumber <= 0) {
            throw new IllegalArgumentException("No enterprise number was supplied");
        }
        this.name = name;
        this.enterpriseNumber = enterpriseNumber;
        String id = name + AT_SIGN + enterpriseNumber;
        if (maxLength > 0 && id.length() > maxLength) {
            throw new IllegalArgumentException("Length of id exceeds maximum of " + maxLength + " characters: " + id);
        }
        this.required = required;
        this.optional = optional;
    }

    public StructuredDataId makeId(StructuredDataId id) {
        if (id == null) {
            return this;
        }
        return makeId(id.getName(), id.getEnterpriseNumber());
    }

    public StructuredDataId makeId(String defaultId, int anEnterpriseNumber) {
        String id;
        String[] req;
        String[] opt;
        if (anEnterpriseNumber <= 0) {
            return this;
        }
        if (this.name != null) {
            id = this.name;
            req = this.required;
            opt = this.optional;
        } else {
            id = defaultId;
            req = null;
            opt = null;
        }
        return new StructuredDataId(id, anEnterpriseNumber, req, opt);
    }

    public String[] getRequired() {
        return this.required;
    }

    public String[] getOptional() {
        return this.optional;
    }

    public String getName() {
        return this.name;
    }

    public int getEnterpriseNumber() {
        return this.enterpriseNumber;
    }

    public boolean isReserved() {
        return this.enterpriseNumber <= 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(this.name.length() + 10);
        formatTo(sb);
        return sb.toString();
    }

    @Override // org.apache.logging.log4j.util.StringBuilderFormattable
    public void formatTo(StringBuilder buffer) {
        if (isReserved()) {
            buffer.append(this.name);
        } else {
            buffer.append(this.name).append(AT_SIGN).append(this.enterpriseNumber);
        }
    }
}