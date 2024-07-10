package com.fasterxml.jackson.core;

import ch.qos.logback.classic.spi.CallerData;
import com.fasterxml.jackson.core.io.CharTypes;
import org.thymeleaf.standard.expression.StandardExpressionObjectFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-core-2.9.7.jar:com/fasterxml/jackson/core/JsonStreamContext.class */
public abstract class JsonStreamContext {
    protected static final int TYPE_ROOT = 0;
    protected static final int TYPE_ARRAY = 1;
    protected static final int TYPE_OBJECT = 2;
    protected int _type;
    protected int _index;

    public abstract JsonStreamContext getParent();

    public abstract String getCurrentName();

    /* JADX INFO: Access modifiers changed from: protected */
    public JsonStreamContext() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public JsonStreamContext(JsonStreamContext base) {
        this._type = base._type;
        this._index = base._index;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public JsonStreamContext(int type, int index) {
        this._type = type;
        this._index = index;
    }

    public final boolean inArray() {
        return this._type == 1;
    }

    public final boolean inRoot() {
        return this._type == 0;
    }

    public final boolean inObject() {
        return this._type == 2;
    }

    @Deprecated
    public final String getTypeDesc() {
        switch (this._type) {
            case 0:
                return "ROOT";
            case 1:
                return "ARRAY";
            case 2:
                return "OBJECT";
            default:
                return CallerData.NA;
        }
    }

    public String typeDesc() {
        switch (this._type) {
            case 0:
                return StandardExpressionObjectFactory.ROOT_EXPRESSION_OBJECT_NAME;
            case 1:
                return "Array";
            case 2:
                return "Object";
            default:
                return CallerData.NA;
        }
    }

    public final int getEntryCount() {
        return this._index + 1;
    }

    public final int getCurrentIndex() {
        if (this._index < 0) {
            return 0;
        }
        return this._index;
    }

    public boolean hasCurrentIndex() {
        return this._index >= 0;
    }

    public boolean hasPathSegment() {
        if (this._type == 2) {
            return hasCurrentName();
        }
        if (this._type == 1) {
            return hasCurrentIndex();
        }
        return false;
    }

    public boolean hasCurrentName() {
        return getCurrentName() != null;
    }

    public Object getCurrentValue() {
        return null;
    }

    public void setCurrentValue(Object v) {
    }

    public JsonPointer pathAsPointer() {
        return JsonPointer.forPath(this, false);
    }

    public JsonPointer pathAsPointer(boolean includeRoot) {
        return JsonPointer.forPath(this, includeRoot);
    }

    public JsonLocation getStartLocation(Object srcRef) {
        return JsonLocation.NA;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(64);
        switch (this._type) {
            case 0:
                sb.append("/");
                break;
            case 1:
                sb.append('[');
                sb.append(getCurrentIndex());
                sb.append(']');
                break;
            case 2:
            default:
                sb.append('{');
                String currentName = getCurrentName();
                if (currentName != null) {
                    sb.append('\"');
                    CharTypes.appendQuoted(sb, currentName);
                    sb.append('\"');
                } else {
                    sb.append('?');
                }
                sb.append('}');
                break;
        }
        return sb.toString();
    }
}