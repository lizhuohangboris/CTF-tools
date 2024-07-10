package org.apache.logging.log4j.message;

import java.util.Map;
import org.apache.logging.log4j.util.EnglishEnums;
import org.apache.logging.log4j.util.StringBuilders;

@AsynchronouslyFormattable
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/message/StructuredDataMessage.class */
public class StructuredDataMessage extends MapMessage<StructuredDataMessage, String> {
    private static final long serialVersionUID = 1703221292892071920L;
    private static final int MAX_LENGTH = 32;
    private static final int HASHVAL = 31;
    private StructuredDataId id;
    private String message;
    private String type;
    private final int maxLength;

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/message/StructuredDataMessage$Format.class */
    public enum Format {
        XML,
        FULL
    }

    public StructuredDataMessage(String id, String msg, String type) {
        this(id, msg, type, 32);
    }

    public StructuredDataMessage(String id, String msg, String type, int maxLength) {
        this.id = new StructuredDataId(id, (String[]) null, (String[]) null, maxLength);
        this.message = msg;
        this.type = type;
        this.maxLength = maxLength;
    }

    public StructuredDataMessage(String id, String msg, String type, Map<String, String> data) {
        this(id, msg, type, data, 32);
    }

    public StructuredDataMessage(String id, String msg, String type, Map<String, String> data, int maxLength) {
        super(data);
        this.id = new StructuredDataId(id, (String[]) null, (String[]) null, maxLength);
        this.message = msg;
        this.type = type;
        this.maxLength = maxLength;
    }

    public StructuredDataMessage(StructuredDataId id, String msg, String type) {
        this(id, msg, type, 32);
    }

    public StructuredDataMessage(StructuredDataId id, String msg, String type, int maxLength) {
        this.id = id;
        this.message = msg;
        this.type = type;
        this.maxLength = maxLength;
    }

    public StructuredDataMessage(StructuredDataId id, String msg, String type, Map<String, String> data) {
        this(id, msg, type, data, 32);
    }

    public StructuredDataMessage(StructuredDataId id, String msg, String type, Map<String, String> data, int maxLength) {
        super(data);
        this.id = id;
        this.message = msg;
        this.type = type;
        this.maxLength = maxLength;
    }

    private StructuredDataMessage(StructuredDataMessage msg, Map<String, String> map) {
        super(map);
        this.id = msg.id;
        this.message = msg.message;
        this.type = msg.type;
        this.maxLength = 32;
    }

    protected StructuredDataMessage() {
        this.maxLength = 32;
    }

    @Override // org.apache.logging.log4j.message.MapMessage, org.apache.logging.log4j.message.MultiformatMessage
    public String[] getFormats() {
        String[] formats = new String[Format.values().length];
        int i = 0;
        Format[] arr$ = Format.values();
        for (Format format : arr$) {
            int i2 = i;
            i++;
            formats[i2] = format.name();
        }
        return formats;
    }

    public StructuredDataId getId() {
        return this.id;
    }

    protected void setId(String id) {
        this.id = new StructuredDataId(id, null, null);
    }

    protected void setId(StructuredDataId id) {
        this.id = id;
    }

    public String getType() {
        return this.type;
    }

    protected void setType(String type) {
        if (type.length() > 32) {
            throw new IllegalArgumentException("structured data type exceeds maximum length of 32 characters: " + type);
        }
        this.type = type;
    }

    @Override // org.apache.logging.log4j.message.MapMessage, org.apache.logging.log4j.util.StringBuilderFormattable
    public void formatTo(StringBuilder buffer) {
        asString(Format.FULL, null, buffer);
    }

    @Override // org.apache.logging.log4j.message.MapMessage, org.apache.logging.log4j.util.MultiFormatStringBuilderFormattable
    public void formatTo(String[] formats, StringBuilder buffer) {
        asString(getFormat(formats), null, buffer);
    }

    @Override // org.apache.logging.log4j.message.MapMessage, org.apache.logging.log4j.message.Message
    public String getFormat() {
        return this.message;
    }

    protected void setMessageFormat(String msg) {
        this.message = msg;
    }

    @Override // org.apache.logging.log4j.message.MapMessage
    public String asString() {
        return asString(Format.FULL, null);
    }

    @Override // org.apache.logging.log4j.message.MapMessage
    public String asString(String format) {
        try {
            return asString((Format) EnglishEnums.valueOf(Format.class, format), null);
        } catch (IllegalArgumentException e) {
            return asString();
        }
    }

    public final String asString(Format format, StructuredDataId structuredDataId) {
        StringBuilder sb = new StringBuilder();
        asString(format, structuredDataId, sb);
        return sb.toString();
    }

    public final void asString(Format format, StructuredDataId structuredDataId, StringBuilder sb) {
        StructuredDataId sdId;
        String msg;
        boolean full = Format.FULL.equals(format);
        if (full) {
            String myType = getType();
            if (myType == null) {
                return;
            }
            sb.append(getType()).append(' ');
        }
        StructuredDataId sdId2 = getId();
        if (sdId2 != null) {
            sdId = sdId2.makeId(structuredDataId);
        } else {
            sdId = structuredDataId;
        }
        if (sdId == null || sdId.getName() == null) {
            return;
        }
        if (Format.XML.equals(format)) {
            asXml(sdId, sb);
            return;
        }
        sb.append('[');
        StringBuilders.appendValue(sb, sdId);
        sb.append(' ');
        appendMap(sb);
        sb.append(']');
        if (full && (msg = getFormat()) != null) {
            sb.append(' ').append(msg);
        }
    }

    private void asXml(StructuredDataId structuredDataId, StringBuilder sb) {
        sb.append("<StructuredData>\n");
        sb.append("<type>").append(this.type).append("</type>\n");
        sb.append("<id>").append(structuredDataId).append("</id>\n");
        super.asXml(sb);
        sb.append("\n</StructuredData>\n");
    }

    @Override // org.apache.logging.log4j.message.MapMessage, org.apache.logging.log4j.message.Message
    public String getFormattedMessage() {
        return asString(Format.FULL, null);
    }

    @Override // org.apache.logging.log4j.message.MapMessage, org.apache.logging.log4j.message.MultiformatMessage
    public String getFormattedMessage(String[] formats) {
        return asString(getFormat(formats), null);
    }

    private Format getFormat(String[] formats) {
        if (formats != null && formats.length > 0) {
            for (String format : formats) {
                if (Format.XML.name().equalsIgnoreCase(format)) {
                    return Format.XML;
                }
                if (Format.FULL.name().equalsIgnoreCase(format)) {
                    return Format.FULL;
                }
            }
            return null;
        }
        return Format.FULL;
    }

    @Override // org.apache.logging.log4j.message.MapMessage
    public String toString() {
        return asString(null, null);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.apache.logging.log4j.message.MapMessage
    public StructuredDataMessage newInstance(Map<String, String> map) {
        return new StructuredDataMessage(this, map);
    }

    @Override // org.apache.logging.log4j.message.MapMessage
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StructuredDataMessage that = (StructuredDataMessage) o;
        if (!super.equals(o)) {
            return false;
        }
        if (this.type != null) {
            if (!this.type.equals(that.type)) {
                return false;
            }
        } else if (that.type != null) {
            return false;
        }
        if (this.id != null) {
            if (!this.id.equals(that.id)) {
                return false;
            }
        } else if (that.id != null) {
            return false;
        }
        if (this.message != null) {
            if (!this.message.equals(that.message)) {
                return false;
            }
            return true;
        } else if (that.message != null) {
            return false;
        } else {
            return true;
        }
    }

    @Override // org.apache.logging.log4j.message.MapMessage
    public int hashCode() {
        int result = super.hashCode();
        return (31 * ((31 * ((31 * result) + (this.type != null ? this.type.hashCode() : 0))) + (this.id != null ? this.id.hashCode() : 0))) + (this.message != null ? this.message.hashCode() : 0);
    }

    @Override // org.apache.logging.log4j.message.MapMessage
    protected void validate(String key, boolean value) {
        validateKey(key);
    }

    @Override // org.apache.logging.log4j.message.MapMessage
    protected void validate(String key, byte value) {
        validateKey(key);
    }

    @Override // org.apache.logging.log4j.message.MapMessage
    protected void validate(String key, char value) {
        validateKey(key);
    }

    @Override // org.apache.logging.log4j.message.MapMessage
    protected void validate(String key, double value) {
        validateKey(key);
    }

    @Override // org.apache.logging.log4j.message.MapMessage
    protected void validate(String key, float value) {
        validateKey(key);
    }

    @Override // org.apache.logging.log4j.message.MapMessage
    protected void validate(String key, int value) {
        validateKey(key);
    }

    @Override // org.apache.logging.log4j.message.MapMessage
    protected void validate(String key, long value) {
        validateKey(key);
    }

    @Override // org.apache.logging.log4j.message.MapMessage
    protected void validate(String key, Object value) {
        validateKey(key);
    }

    @Override // org.apache.logging.log4j.message.MapMessage
    protected void validate(String key, short value) {
        validateKey(key);
    }

    @Override // org.apache.logging.log4j.message.MapMessage
    protected void validate(String key, String value) {
        validateKey(key);
    }

    protected void validateKey(String key) {
        if (this.maxLength > 0 && key.length() > this.maxLength) {
            throw new IllegalArgumentException("Structured data keys are limited to " + this.maxLength + " characters. key: " + key);
        }
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            if (c < '!' || c > '~' || c == '=' || c == ']' || c == '\"') {
                throw new IllegalArgumentException("Structured data keys must contain printable US ASCII charactersand may not contain a space, =, ], or \"");
            }
        }
    }
}