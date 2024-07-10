package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Currency;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;
import org.springframework.beans.PropertyAccessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/deser/std/FromStringDeserializer.class */
public abstract class FromStringDeserializer<T> extends StdScalarDeserializer<T> {
    public abstract T _deserialize(String str, DeserializationContext deserializationContext) throws IOException;

    public static Class<?>[] types() {
        return new Class[]{File.class, URL.class, URI.class, Class.class, JavaType.class, Currency.class, Pattern.class, Locale.class, Charset.class, TimeZone.class, InetAddress.class, InetSocketAddress.class, StringBuilder.class};
    }

    public FromStringDeserializer(Class<?> vc) {
        super(vc);
    }

    public static Std findDeserializer(Class<?> rawType) {
        int kind;
        if (rawType == File.class) {
            kind = 1;
        } else if (rawType == URL.class) {
            kind = 2;
        } else if (rawType == URI.class) {
            kind = 3;
        } else if (rawType == Class.class) {
            kind = 4;
        } else if (rawType == JavaType.class) {
            kind = 5;
        } else if (rawType == Currency.class) {
            kind = 6;
        } else if (rawType == Pattern.class) {
            kind = 7;
        } else if (rawType == Locale.class) {
            kind = 8;
        } else if (rawType == Charset.class) {
            kind = 9;
        } else if (rawType == TimeZone.class) {
            kind = 10;
        } else if (rawType == InetAddress.class) {
            kind = 11;
        } else if (rawType == InetSocketAddress.class) {
            kind = 12;
        } else if (rawType == StringBuilder.class) {
            kind = 13;
        } else {
            return null;
        }
        return new Std(rawType, kind);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String text = p.getValueAsString();
        if (text != null) {
            if (text.length() != 0) {
                String text2 = text.trim();
                if (text2.length() != 0) {
                    try {
                        return _deserialize(text2, ctxt);
                    } catch (IllegalArgumentException | MalformedURLException e) {
                        String msg = "not a valid textual representation";
                        String m2 = e.getMessage();
                        if (m2 != null) {
                            msg = msg + ", problem: " + m2;
                        }
                        JsonMappingException e2 = ctxt.weirdStringException(text2, this._valueClass, msg);
                        e2.initCause(e);
                        throw e2;
                    }
                }
            }
            return _deserializeFromEmptyString();
        }
        JsonToken t = p.getCurrentToken();
        if (t == JsonToken.START_ARRAY) {
            return _deserializeFromArray(p, ctxt);
        }
        if (t == JsonToken.VALUE_EMBEDDED_OBJECT) {
            T t2 = (T) p.getEmbeddedObject();
            if (t2 == null) {
                return null;
            }
            if (this._valueClass.isAssignableFrom(t2.getClass())) {
                return t2;
            }
            return _deserializeEmbedded(t2, ctxt);
        }
        return (T) ctxt.handleUnexpectedToken(this._valueClass, p);
    }

    public T _deserializeEmbedded(Object ob, DeserializationContext ctxt) throws IOException {
        ctxt.reportInputMismatch(this, "Don't know how to convert embedded Object of type %s into %s", ob.getClass().getName(), this._valueClass.getName());
        return null;
    }

    protected T _deserializeFromEmptyString() throws IOException {
        return null;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/deser/std/FromStringDeserializer$Std.class */
    public static class Std extends FromStringDeserializer<Object> {
        private static final long serialVersionUID = 1;
        public static final int STD_FILE = 1;
        public static final int STD_URL = 2;
        public static final int STD_URI = 3;
        public static final int STD_CLASS = 4;
        public static final int STD_JAVA_TYPE = 5;
        public static final int STD_CURRENCY = 6;
        public static final int STD_PATTERN = 7;
        public static final int STD_LOCALE = 8;
        public static final int STD_CHARSET = 9;
        public static final int STD_TIME_ZONE = 10;
        public static final int STD_INET_ADDRESS = 11;
        public static final int STD_INET_SOCKET_ADDRESS = 12;
        public static final int STD_STRING_BUILDER = 13;
        protected final int _kind;

        protected Std(Class<?> valueType, int kind) {
            super(valueType);
            this._kind = kind;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.fasterxml.jackson.databind.deser.std.FromStringDeserializer
        public Object _deserialize(String value, DeserializationContext ctxt) throws IOException {
            switch (this._kind) {
                case 1:
                    return new File(value);
                case 2:
                    return new URL(value);
                case 3:
                    return URI.create(value);
                case 4:
                    try {
                        return ctxt.findClass(value);
                    } catch (Exception e) {
                        return ctxt.handleInstantiationProblem(this._valueClass, value, ClassUtil.getRootCause(e));
                    }
                case 5:
                    return ctxt.getTypeFactory().constructFromCanonical(value);
                case 6:
                    return Currency.getInstance(value);
                case 7:
                    return Pattern.compile(value);
                case 8:
                    int ix = _firstHyphenOrUnderscore(value);
                    if (ix < 0) {
                        return new Locale(value);
                    }
                    String first = value.substring(0, ix);
                    String value2 = value.substring(ix + 1);
                    int ix2 = _firstHyphenOrUnderscore(value2);
                    if (ix2 < 0) {
                        return new Locale(first, value2);
                    }
                    String second = value2.substring(0, ix2);
                    return new Locale(first, second, value2.substring(ix2 + 1));
                case 9:
                    return Charset.forName(value);
                case 10:
                    return TimeZone.getTimeZone(value);
                case 11:
                    return InetAddress.getByName(value);
                case 12:
                    if (value.startsWith(PropertyAccessor.PROPERTY_KEY_PREFIX)) {
                        int i = value.lastIndexOf(93);
                        if (i == -1) {
                            throw new InvalidFormatException(ctxt.getParser(), "Bracketed IPv6 address must contain closing bracket", value, InetSocketAddress.class);
                        }
                        int j = value.indexOf(58, i);
                        int port = j > -1 ? Integer.parseInt(value.substring(j + 1)) : 0;
                        return new InetSocketAddress(value.substring(0, i + 1), port);
                    }
                    int ix3 = value.indexOf(58);
                    if (ix3 >= 0 && value.indexOf(58, ix3 + 1) < 0) {
                        int port2 = Integer.parseInt(value.substring(ix3 + 1));
                        return new InetSocketAddress(value.substring(0, ix3), port2);
                    }
                    return new InetSocketAddress(value, 0);
                case 13:
                    return new StringBuilder(value);
                default:
                    VersionUtil.throwInternal();
                    return null;
            }
        }

        @Override // com.fasterxml.jackson.databind.deser.std.FromStringDeserializer
        protected Object _deserializeFromEmptyString() throws IOException {
            if (this._kind == 3) {
                return URI.create("");
            }
            if (this._kind == 8) {
                return Locale.ROOT;
            }
            if (this._kind == 13) {
                return new StringBuilder();
            }
            return super._deserializeFromEmptyString();
        }

        protected int _firstHyphenOrUnderscore(String str) {
            int end = str.length();
            for (int i = 0; i < end; i++) {
                char c = str.charAt(i);
                if (c == '_' || c == '-') {
                    return i;
                }
            }
            return -1;
        }
    }
}