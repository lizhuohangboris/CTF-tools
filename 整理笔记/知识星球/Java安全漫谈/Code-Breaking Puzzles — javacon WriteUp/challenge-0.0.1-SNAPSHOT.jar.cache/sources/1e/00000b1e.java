package org.apache.logging.log4j.message;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import org.apache.logging.log4j.message.MapMessage;
import org.apache.logging.log4j.util.BiConsumer;
import org.apache.logging.log4j.util.EnglishEnums;
import org.apache.logging.log4j.util.IndexedReadOnlyStringMap;
import org.apache.logging.log4j.util.IndexedStringMap;
import org.apache.logging.log4j.util.MultiFormatStringBuilderFormattable;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.apache.logging.log4j.util.SortedArrayStringMap;
import org.apache.logging.log4j.util.StringBuilders;
import org.apache.logging.log4j.util.TriConsumer;

@AsynchronouslyFormattable
@PerformanceSensitive({"allocation"})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/message/MapMessage.class */
public class MapMessage<M extends MapMessage<M, V>, V> implements MultiFormatStringBuilderFormattable {
    private static final long serialVersionUID = -5031471831131487120L;
    private final IndexedStringMap data;

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/message/MapMessage$MapFormat.class */
    public enum MapFormat {
        XML,
        JSON,
        JAVA;

        public static MapFormat lookupIgnoreCase(String format) {
            if (XML.name().equalsIgnoreCase(format)) {
                return XML;
            }
            if (JSON.name().equalsIgnoreCase(format)) {
                return JSON;
            }
            if (JAVA.name().equalsIgnoreCase(format)) {
                return JAVA;
            }
            return null;
        }

        public static String[] names() {
            return new String[]{XML.name(), JSON.name(), JAVA.name()};
        }
    }

    public MapMessage() {
        this.data = new SortedArrayStringMap();
    }

    public MapMessage(int initialCapacity) {
        this.data = new SortedArrayStringMap(initialCapacity);
    }

    public MapMessage(Map<String, V> map) {
        this.data = new SortedArrayStringMap((Map<String, ?>) map);
    }

    @Override // org.apache.logging.log4j.message.MultiformatMessage
    public String[] getFormats() {
        return MapFormat.names();
    }

    @Override // org.apache.logging.log4j.message.Message
    public Object[] getParameters() {
        Object[] result = new Object[this.data.size()];
        for (int i = 0; i < this.data.size(); i++) {
            result[i] = this.data.getValueAt(i);
        }
        return result;
    }

    @Override // org.apache.logging.log4j.message.Message
    public String getFormat() {
        return "";
    }

    /* JADX WARN: Multi-variable type inference failed */
    public Map<String, V> getData() {
        TreeMap treeMap = new TreeMap();
        for (int i = 0; i < this.data.size(); i++) {
            treeMap.put(this.data.getKeyAt(i), this.data.getValueAt(i));
        }
        return Collections.unmodifiableMap(treeMap);
    }

    public IndexedReadOnlyStringMap getIndexedReadOnlyStringMap() {
        return this.data;
    }

    public void clear() {
        this.data.clear();
    }

    public boolean containsKey(String key) {
        return this.data.containsKey(key);
    }

    public void put(String key, String value) {
        if (value == null) {
            throw new IllegalArgumentException("No value provided for key " + key);
        }
        validate(key, value);
        this.data.putValue(key, value);
    }

    public void putAll(Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            this.data.putValue(entry.getKey(), entry.getValue());
        }
    }

    public String get(String key) {
        Object result = this.data.getValue(key);
        return ParameterFormatter.deepToString(result);
    }

    public String remove(String key) {
        String result = get(key);
        this.data.remove(key);
        return result;
    }

    public String asString() {
        return format(null, new StringBuilder()).toString();
    }

    public String asString(String format) {
        try {
            return format((MapFormat) EnglishEnums.valueOf(MapFormat.class, format), new StringBuilder()).toString();
        } catch (IllegalArgumentException e) {
            return asString();
        }
    }

    public <CV> void forEach(BiConsumer<String, ? super CV> action) {
        this.data.forEach(action);
    }

    public <CV, S> void forEach(TriConsumer<String, ? super CV, S> action, S state) {
        this.data.forEach(action, state);
    }

    private StringBuilder format(MapFormat format, StringBuilder sb) {
        if (format == null) {
            appendMap(sb);
        } else {
            switch (format) {
                case XML:
                    asXml(sb);
                    break;
                case JSON:
                    asJson(sb);
                    break;
                case JAVA:
                    asJava(sb);
                    break;
                default:
                    appendMap(sb);
                    break;
            }
        }
        return sb;
    }

    public void asXml(StringBuilder sb) {
        sb.append("<Map>\n");
        for (int i = 0; i < this.data.size(); i++) {
            sb.append("  <Entry key=\"").append(this.data.getKeyAt(i)).append("\">");
            int size = sb.length();
            ParameterFormatter.recursiveDeepToString(this.data.getValueAt(i), sb, null);
            StringBuilders.escapeXml(sb, size);
            sb.append("</Entry>\n");
        }
        sb.append("</Map>");
    }

    @Override // org.apache.logging.log4j.message.Message
    public String getFormattedMessage() {
        return asString();
    }

    @Override // org.apache.logging.log4j.message.MultiformatMessage
    public String getFormattedMessage(String[] formats) {
        return format(getFormat(formats), new StringBuilder()).toString();
    }

    private MapFormat getFormat(String[] formats) {
        if (formats == null || formats.length == 0) {
            return null;
        }
        for (String str : formats) {
            MapFormat mapFormat = MapFormat.lookupIgnoreCase(str);
            if (mapFormat != null) {
                return mapFormat;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void appendMap(StringBuilder sb) {
        for (int i = 0; i < this.data.size(); i++) {
            if (i > 0) {
                sb.append(' ');
            }
            sb.append(this.data.getKeyAt(i)).append('=').append('\"');
            ParameterFormatter.recursiveDeepToString(this.data.getValueAt(i), sb, null);
            sb.append('\"');
        }
    }

    protected void asJson(StringBuilder sb) {
        sb.append('{');
        for (int i = 0; i < this.data.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append('\"');
            int start = sb.length();
            sb.append(this.data.getKeyAt(i));
            StringBuilders.escapeJson(sb, start);
            sb.append('\"').append(':').append('\"');
            int start2 = sb.length();
            ParameterFormatter.recursiveDeepToString(this.data.getValueAt(i), sb, null);
            StringBuilders.escapeJson(sb, start2);
            sb.append('\"');
        }
        sb.append('}');
    }

    protected void asJava(StringBuilder sb) {
        sb.append('{');
        for (int i = 0; i < this.data.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(this.data.getKeyAt(i)).append('=').append('\"');
            ParameterFormatter.recursiveDeepToString(this.data.getValueAt(i), sb, null);
            sb.append('\"');
        }
        sb.append('}');
    }

    public M newInstance(Map<String, V> map) {
        return (M) new MapMessage(map);
    }

    public String toString() {
        return asString();
    }

    @Override // org.apache.logging.log4j.util.StringBuilderFormattable
    public void formatTo(StringBuilder buffer) {
        format(null, buffer);
    }

    @Override // org.apache.logging.log4j.util.MultiFormatStringBuilderFormattable
    public void formatTo(String[] formats, StringBuilder buffer) {
        format(getFormat(formats), buffer);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MapMessage<?, ?> that = (MapMessage) o;
        return this.data.equals(that.data);
    }

    public int hashCode() {
        return this.data.hashCode();
    }

    @Override // org.apache.logging.log4j.message.Message
    public Throwable getThrowable() {
        return null;
    }

    protected void validate(String key, boolean value) {
    }

    protected void validate(String key, byte value) {
    }

    protected void validate(String key, char value) {
    }

    protected void validate(String key, double value) {
    }

    protected void validate(String key, float value) {
    }

    protected void validate(String key, int value) {
    }

    protected void validate(String key, long value) {
    }

    protected void validate(String key, Object value) {
    }

    protected void validate(String key, short value) {
    }

    protected void validate(String key, String value) {
    }

    public M with(String key, boolean value) {
        validate(key, value);
        this.data.putValue(key, Boolean.valueOf(value));
        return this;
    }

    public M with(String key, byte value) {
        validate(key, value);
        this.data.putValue(key, Byte.valueOf(value));
        return this;
    }

    public M with(String key, char value) {
        validate(key, value);
        this.data.putValue(key, Character.valueOf(value));
        return this;
    }

    public M with(String key, double value) {
        validate(key, value);
        this.data.putValue(key, Double.valueOf(value));
        return this;
    }

    public M with(String key, float value) {
        validate(key, value);
        this.data.putValue(key, Float.valueOf(value));
        return this;
    }

    public M with(String key, int value) {
        validate(key, value);
        this.data.putValue(key, Integer.valueOf(value));
        return this;
    }

    public M with(String key, long value) {
        validate(key, value);
        this.data.putValue(key, Long.valueOf(value));
        return this;
    }

    public M with(String key, Object value) {
        validate(key, value);
        this.data.putValue(key, value);
        return this;
    }

    public M with(String key, short value) {
        validate(key, value);
        this.data.putValue(key, Short.valueOf(value));
        return this;
    }

    public M with(String key, String value) {
        put(key, value);
        return this;
    }
}