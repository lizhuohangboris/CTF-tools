package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import java.io.IOException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/deser/std/StackTraceElementDeserializer.class */
public class StackTraceElementDeserializer extends StdScalarDeserializer<StackTraceElement> {
    private static final long serialVersionUID = 1;

    public StackTraceElementDeserializer() {
        super(StackTraceElement.class);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public StackTraceElement deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken t = p.getCurrentToken();
        if (t == JsonToken.START_OBJECT) {
            String className = "";
            String methodName = "";
            String fileName = "";
            String moduleName = null;
            String moduleVersion = null;
            String classLoaderName = null;
            int lineNumber = -1;
            while (true) {
                JsonToken t2 = p.nextValue();
                if (t2 != JsonToken.END_OBJECT) {
                    String propName = p.getCurrentName();
                    if ("className".equals(propName)) {
                        className = p.getText();
                    } else if ("classLoaderName".equals(propName)) {
                        classLoaderName = p.getText();
                    } else if ("fileName".equals(propName)) {
                        fileName = p.getText();
                    } else if ("lineNumber".equals(propName)) {
                        if (t2.isNumeric()) {
                            lineNumber = p.getIntValue();
                        } else {
                            lineNumber = _parseIntPrimitive(p, ctxt);
                        }
                    } else if ("methodName".equals(propName)) {
                        methodName = p.getText();
                    } else if (!"nativeMethod".equals(propName)) {
                        if ("moduleName".equals(propName)) {
                            moduleName = p.getText();
                        } else if ("moduleVersion".equals(propName)) {
                            moduleVersion = p.getText();
                        } else if (!"declaringClass".equals(propName) && !"format".equals(propName)) {
                            handleUnknownProperty(p, ctxt, this._valueClass, propName);
                        }
                    }
                    p.skipChildren();
                } else {
                    return constructValue(ctxt, className, methodName, fileName, lineNumber, moduleName, moduleVersion, classLoaderName);
                }
            }
        } else if (t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
            p.nextToken();
            StackTraceElement value = deserialize(p, ctxt);
            if (p.nextToken() != JsonToken.END_ARRAY) {
                handleMissingEndArrayForSingle(p, ctxt);
            }
            return value;
        } else {
            return (StackTraceElement) ctxt.handleUnexpectedToken(this._valueClass, p);
        }
    }

    @Deprecated
    protected StackTraceElement constructValue(DeserializationContext ctxt, String className, String methodName, String fileName, int lineNumber, String moduleName, String moduleVersion) {
        return constructValue(ctxt, className, methodName, fileName, lineNumber, moduleName, moduleVersion, null);
    }

    protected StackTraceElement constructValue(DeserializationContext ctxt, String className, String methodName, String fileName, int lineNumber, String moduleName, String moduleVersion, String classLoaderName) {
        return new StackTraceElement(className, methodName, fileName, lineNumber);
    }
}