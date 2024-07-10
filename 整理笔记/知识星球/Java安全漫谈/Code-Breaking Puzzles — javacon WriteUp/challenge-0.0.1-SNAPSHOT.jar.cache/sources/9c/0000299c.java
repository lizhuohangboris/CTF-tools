package org.thymeleaf.standard.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.ClassLoaderUtils;
import org.thymeleaf.util.DateUtils;
import org.unbescape.json.JsonEscape;
import org.unbescape.json.JsonEscapeLevel;
import org.unbescape.json.JsonEscapeType;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/serializer/StandardJavaScriptSerializer.class */
public final class StandardJavaScriptSerializer implements IStandardJavaScriptSerializer {
    private static final Logger logger = LoggerFactory.getLogger(StandardJavaScriptSerializer.class);
    private final IStandardJavaScriptSerializer delegate;

    private String computeJacksonPackageNameIfPresent() {
        try {
            String objectMapperPackageName = ObjectMapper.class.getPackage().getName();
            return objectMapperPackageName.substring(0, objectMapperPackageName.length() - ".databind".length());
        } catch (Throwable th) {
            return null;
        }
    }

    public StandardJavaScriptSerializer(boolean useJacksonIfAvailable) {
        IStandardJavaScriptSerializer newDelegate = null;
        String jacksonPrefix = useJacksonIfAvailable ? computeJacksonPackageNameIfPresent() : null;
        if (jacksonPrefix != null) {
            try {
                newDelegate = new JacksonStandardJavaScriptSerializer(jacksonPrefix);
            } catch (Exception e) {
                handleErrorLoggingOnJacksonInitialization(e);
            } catch (NoSuchMethodError e2) {
                handleErrorLoggingOnJacksonInitialization(e2);
            }
        }
        this.delegate = newDelegate == null ? new DefaultStandardJavaScriptSerializer() : newDelegate;
    }

    @Override // org.thymeleaf.standard.serializer.IStandardJavaScriptSerializer
    public void serializeValue(Object object, Writer writer) {
        this.delegate.serializeValue(object, writer);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/serializer/StandardJavaScriptSerializer$JacksonStandardJavaScriptSerializer.class */
    private static final class JacksonStandardJavaScriptSerializer implements IStandardJavaScriptSerializer {
        private final ObjectMapper mapper = new ObjectMapper();

        JacksonStandardJavaScriptSerializer(String jacksonPrefix) {
            this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            this.mapper.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
            this.mapper.enable(JsonGenerator.Feature.ESCAPE_NON_ASCII);
            this.mapper.getFactory().setCharacterEscapes(new JacksonThymeleafCharacterEscapes());
            this.mapper.setDateFormat(new JacksonThymeleafISO8601DateFormat());
            Class<?> javaTimeModuleClass = ClassLoaderUtils.findClass(jacksonPrefix + ".datatype.jsr310.JavaTimeModule");
            if (javaTimeModuleClass != null) {
                try {
                    this.mapper.registerModule((Module) javaTimeModuleClass.newInstance());
                    this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
                } catch (IllegalAccessException e) {
                    throw new ConfigurationException("Exception while trying to initialize JSR310 support for Jackson", e);
                } catch (InstantiationException e2) {
                    throw new ConfigurationException("Exception while trying to initialize JSR310 support for Jackson", e2);
                }
            }
        }

        @Override // org.thymeleaf.standard.serializer.IStandardJavaScriptSerializer
        public void serializeValue(Object object, Writer writer) {
            try {
                this.mapper.writeValue(writer, object);
            } catch (IOException e) {
                throw new TemplateProcessingException("An exception was raised while trying to serialize object to JavaScript using Jackson", e);
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/serializer/StandardJavaScriptSerializer$JacksonThymeleafISO8601DateFormat.class */
    private static final class JacksonThymeleafISO8601DateFormat extends DateFormat {
        private static final long serialVersionUID = 1354081220093875129L;
        private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZ");

        JacksonThymeleafISO8601DateFormat() {
            setCalendar(this.dateFormat.getCalendar());
            setNumberFormat(this.dateFormat.getNumberFormat());
        }

        @Override // java.text.DateFormat
        public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
            StringBuffer formatted = this.dateFormat.format(date, toAppendTo, fieldPosition);
            formatted.insert(26, ':');
            return formatted;
        }

        @Override // java.text.DateFormat
        public Date parse(String source, ParsePosition pos) {
            throw new UnsupportedOperationException("JacksonThymeleafISO8601DateFormat should never be asked for a 'parse' operation");
        }

        @Override // java.text.DateFormat, java.text.Format
        public Object clone() {
            JacksonThymeleafISO8601DateFormat other = (JacksonThymeleafISO8601DateFormat) super.clone();
            other.dateFormat = (SimpleDateFormat) this.dateFormat.clone();
            return other;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/serializer/StandardJavaScriptSerializer$JacksonThymeleafCharacterEscapes.class */
    private static final class JacksonThymeleafCharacterEscapes extends CharacterEscapes {
        private static final int[] CHARACTER_ESCAPES = CharacterEscapes.standardAsciiEscapesForJSON();
        private static final SerializableString SLASH_ESCAPE;
        private static final SerializableString AMPERSAND_ESCAPE;

        static {
            CHARACTER_ESCAPES[47] = -2;
            CHARACTER_ESCAPES[38] = -2;
            SLASH_ESCAPE = new SerializedString("\\/");
            AMPERSAND_ESCAPE = new SerializedString("\\u0026");
        }

        JacksonThymeleafCharacterEscapes() {
        }

        @Override // com.fasterxml.jackson.core.io.CharacterEscapes
        public int[] getEscapeCodesForAscii() {
            return CHARACTER_ESCAPES;
        }

        @Override // com.fasterxml.jackson.core.io.CharacterEscapes
        public SerializableString getEscapeSequence(int ch2) {
            if (ch2 == 47) {
                return SLASH_ESCAPE;
            }
            if (ch2 == 38) {
                return AMPERSAND_ESCAPE;
            }
            return null;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/serializer/StandardJavaScriptSerializer$DefaultStandardJavaScriptSerializer.class */
    private static final class DefaultStandardJavaScriptSerializer implements IStandardJavaScriptSerializer {
        private DefaultStandardJavaScriptSerializer() {
        }

        @Override // org.thymeleaf.standard.serializer.IStandardJavaScriptSerializer
        public void serializeValue(Object object, Writer writer) {
            try {
                writeValue(writer, object);
            } catch (IOException e) {
                throw new TemplateProcessingException("An exception was raised while trying to serialize object to JavaScript using the default serializer", e);
            }
        }

        private static void writeValue(Writer writer, Object object) throws IOException {
            if (object == null) {
                writeNull(writer);
            } else if (object instanceof CharSequence) {
                writeString(writer, object.toString());
            } else if (object instanceof Character) {
                writeString(writer, object.toString());
            } else if (object instanceof Number) {
                writeNumber(writer, (Number) object);
            } else if (object instanceof Boolean) {
                writeBoolean(writer, (Boolean) object);
            } else if (object instanceof Date) {
                writeDate(writer, (Date) object);
            } else if (object instanceof Calendar) {
                writeDate(writer, ((Calendar) object).getTime());
            } else if (object.getClass().isArray()) {
                writeArray(writer, object);
            } else if (object instanceof Collection) {
                writeCollection(writer, (Collection) object);
            } else if (object instanceof Map) {
                writeMap(writer, (Map) object);
            } else if (object instanceof Enum) {
                writeEnum(writer, object);
            } else {
                writeObject(writer, object);
            }
        }

        private static void writeNull(Writer writer) throws IOException {
            writer.write(BeanDefinitionParserDelegate.NULL_ELEMENT);
        }

        private static void writeString(Writer writer, String str) throws IOException {
            writer.write(34);
            writer.write(JsonEscape.escapeJson(str, JsonEscapeType.SINGLE_ESCAPE_CHARS_DEFAULT_TO_UHEXA, JsonEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET));
            writer.write(34);
        }

        private static void writeNumber(Writer writer, Number number) throws IOException {
            writer.write(number.toString());
        }

        private static void writeBoolean(Writer writer, Boolean bool) throws IOException {
            writer.write(bool.toString());
        }

        private static void writeDate(Writer writer, Date date) throws IOException {
            writer.write(34);
            writer.write(DateUtils.formatISO(date));
            writer.write(34);
        }

        private static void writeArray(Writer writer, Object arrayObj) throws IOException {
            writer.write(91);
            if (arrayObj instanceof Object[]) {
                Object[] array = (Object[]) arrayObj;
                boolean first = true;
                for (Object element : array) {
                    if (first) {
                        first = false;
                    } else {
                        writer.write(44);
                    }
                    writeValue(writer, element);
                }
            } else if (arrayObj instanceof boolean[]) {
                boolean[] array2 = (boolean[]) arrayObj;
                boolean first2 = true;
                for (boolean element2 : array2) {
                    if (first2) {
                        first2 = false;
                    } else {
                        writer.write(44);
                    }
                    writeValue(writer, Boolean.valueOf(element2));
                }
            } else if (arrayObj instanceof byte[]) {
                byte[] array3 = (byte[]) arrayObj;
                boolean first3 = true;
                for (byte element3 : array3) {
                    if (first3) {
                        first3 = false;
                    } else {
                        writer.write(44);
                    }
                    writeValue(writer, Byte.valueOf(element3));
                }
            } else if (arrayObj instanceof short[]) {
                short[] array4 = (short[]) arrayObj;
                boolean first4 = true;
                for (short element4 : array4) {
                    if (first4) {
                        first4 = false;
                    } else {
                        writer.write(44);
                    }
                    writeValue(writer, Short.valueOf(element4));
                }
            } else if (arrayObj instanceof int[]) {
                int[] array5 = (int[]) arrayObj;
                boolean first5 = true;
                for (int element5 : array5) {
                    if (first5) {
                        first5 = false;
                    } else {
                        writer.write(44);
                    }
                    writeValue(writer, Integer.valueOf(element5));
                }
            } else if (arrayObj instanceof long[]) {
                long[] array6 = (long[]) arrayObj;
                boolean first6 = true;
                for (long element6 : array6) {
                    if (first6) {
                        first6 = false;
                    } else {
                        writer.write(44);
                    }
                    writeValue(writer, Long.valueOf(element6));
                }
            } else if (arrayObj instanceof float[]) {
                float[] array7 = (float[]) arrayObj;
                boolean first7 = true;
                for (float element7 : array7) {
                    if (first7) {
                        first7 = false;
                    } else {
                        writer.write(44);
                    }
                    writeValue(writer, Float.valueOf(element7));
                }
            } else if (arrayObj instanceof double[]) {
                double[] array8 = (double[]) arrayObj;
                boolean first8 = true;
                for (double element8 : array8) {
                    if (first8) {
                        first8 = false;
                    } else {
                        writer.write(44);
                    }
                    writeValue(writer, Double.valueOf(element8));
                }
            } else {
                throw new IllegalArgumentException("Cannot write value \"" + arrayObj + "\" of class " + arrayObj.getClass().getName() + " as an array");
            }
            writer.write(93);
        }

        private static void writeCollection(Writer writer, Collection<?> collection) throws IOException {
            writer.write(91);
            boolean first = true;
            for (Object element : collection) {
                if (first) {
                    first = false;
                } else {
                    writer.write(44);
                }
                writeValue(writer, element);
            }
            writer.write(93);
        }

        private static void writeMap(Writer writer, Map<?, ?> map) throws IOException {
            writer.write(123);
            boolean first = true;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (first) {
                    first = false;
                } else {
                    writer.write(44);
                }
                writeKeyValue(writer, entry.getKey(), entry.getValue());
            }
            writer.write(125);
        }

        private static void writeKeyValue(Writer writer, Object key, Object value) throws IOException {
            writeValue(writer, key);
            writer.write(58);
            writeValue(writer, value);
        }

        private static void writeObject(Writer writer, Object object) throws IOException {
            try {
                PropertyDescriptor[] descriptors = Introspector.getBeanInfo(object.getClass()).getPropertyDescriptors();
                Map<String, Object> properties = new LinkedHashMap<>(descriptors.length + 1, 1.0f);
                for (PropertyDescriptor descriptor : descriptors) {
                    Method readMethod = descriptor.getReadMethod();
                    if (readMethod != null) {
                        String name = descriptor.getName();
                        if (!"class".equals(name.toLowerCase())) {
                            Object value = readMethod.invoke(object, new Object[0]);
                            properties.put(name, value);
                        }
                    }
                }
                writeMap(writer, properties);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Could not perform introspection on object of class " + object.getClass().getName(), e);
            } catch (InvocationTargetException e2) {
                throw new IllegalArgumentException("Could not perform introspection on object of class " + object.getClass().getName(), e2);
            } catch (IntrospectionException e3) {
                throw new IllegalArgumentException("Could not perform introspection on object of class " + object.getClass().getName(), e3);
            }
        }

        private static void writeEnum(Writer writer, Object object) throws IOException {
            Enum<?> enumObject = (Enum) object;
            writeString(writer, enumObject.toString());
        }
    }

    private void handleErrorLoggingOnJacksonInitialization(Throwable e) {
        if (logger.isDebugEnabled()) {
            logger.warn("[THYMELEAF] Could not initialize Jackson-based serializer even if the Jackson library was detected to be present at the classpath. Please make sure you are adding the jackson-databind module to your classpath, and that version is >= 2.5.0. THYMELEAF INITIALIZATION WILL CONTINUE, but Jackson will not be used for JavaScript serialization.", e);
        } else {
            logger.warn("[THYMELEAF] Could not initialize Jackson-based serializer even if the Jackson library was detected to be present at the classpath. Please make sure you are adding the jackson-databind module to your classpath, and that version is >= 2.5.0. THYMELEAF INITIALIZATION WILL CONTINUE, but Jackson will not be used for JavaScript serialization. Set the log to DEBUG to see a complete exception trace. Exception message is: " + e.getMessage());
        }
    }
}