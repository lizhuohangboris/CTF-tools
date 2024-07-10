package com.fasterxml.jackson.core;

import java.io.Serializable;
import java.nio.charset.Charset;
import org.unbescape.uri.UriEscape;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-core-2.9.7.jar:com/fasterxml/jackson/core/JsonLocation.class */
public class JsonLocation implements Serializable {
    private static final long serialVersionUID = 1;
    public static final int MAX_CONTENT_SNIPPET = 500;
    public static final JsonLocation NA = new JsonLocation(null, -1, -1, -1, -1);
    protected final long _totalBytes;
    protected final long _totalChars;
    protected final int _lineNr;
    protected final int _columnNr;
    final transient Object _sourceRef;

    public JsonLocation(Object srcRef, long totalChars, int lineNr, int colNr) {
        this(srcRef, -1L, totalChars, lineNr, colNr);
    }

    public JsonLocation(Object sourceRef, long totalBytes, long totalChars, int lineNr, int columnNr) {
        this._sourceRef = sourceRef;
        this._totalBytes = totalBytes;
        this._totalChars = totalChars;
        this._lineNr = lineNr;
        this._columnNr = columnNr;
    }

    public Object getSourceRef() {
        return this._sourceRef;
    }

    public int getLineNr() {
        return this._lineNr;
    }

    public int getColumnNr() {
        return this._columnNr;
    }

    public long getCharOffset() {
        return this._totalChars;
    }

    public long getByteOffset() {
        return this._totalBytes;
    }

    public String sourceDescription() {
        return _appendSourceDesc(new StringBuilder(100)).toString();
    }

    public int hashCode() {
        int hash = this._sourceRef == null ? 1 : this._sourceRef.hashCode();
        return (((hash ^ this._lineNr) + this._columnNr) ^ ((int) this._totalChars)) + ((int) this._totalBytes);
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other != null && (other instanceof JsonLocation)) {
            JsonLocation otherLoc = (JsonLocation) other;
            if (this._sourceRef == null) {
                if (otherLoc._sourceRef != null) {
                    return false;
                }
            } else if (!this._sourceRef.equals(otherLoc._sourceRef)) {
                return false;
            }
            return this._lineNr == otherLoc._lineNr && this._columnNr == otherLoc._columnNr && this._totalChars == otherLoc._totalChars && getByteOffset() == otherLoc.getByteOffset();
        }
        return false;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(80);
        sb.append("[Source: ");
        _appendSourceDesc(sb);
        sb.append("; line: ");
        sb.append(this._lineNr);
        sb.append(", column: ");
        sb.append(this._columnNr);
        sb.append(']');
        return sb.toString();
    }

    protected StringBuilder _appendSourceDesc(StringBuilder sb) {
        int len;
        Object srcRef = this._sourceRef;
        if (srcRef == null) {
            sb.append("UNKNOWN");
            return sb;
        }
        Class<?> srcType = srcRef instanceof Class ? (Class) srcRef : srcRef.getClass();
        String tn = srcType.getName();
        if (tn.startsWith("java.")) {
            tn = srcType.getSimpleName();
        } else if (srcRef instanceof byte[]) {
            tn = "byte[]";
        } else if (srcRef instanceof char[]) {
            tn = "char[]";
        }
        sb.append('(').append(tn).append(')');
        String charStr = " chars";
        if (srcRef instanceof CharSequence) {
            CharSequence cs = (CharSequence) srcRef;
            int len2 = cs.length();
            len = len2 - _append(sb, cs.subSequence(0, Math.min(len2, 500)).toString());
        } else if (srcRef instanceof char[]) {
            char[] ch2 = (char[]) srcRef;
            int len3 = ch2.length;
            len = len3 - _append(sb, new String(ch2, 0, Math.min(len3, 500)));
        } else if (srcRef instanceof byte[]) {
            byte[] b = (byte[]) srcRef;
            int maxLen = Math.min(b.length, 500);
            _append(sb, new String(b, 0, maxLen, Charset.forName(UriEscape.DEFAULT_ENCODING)));
            len = b.length - maxLen;
            charStr = " bytes";
        } else {
            len = 0;
        }
        if (len > 0) {
            sb.append("[truncated ").append(len).append(charStr).append(']');
        }
        return sb;
    }

    private int _append(StringBuilder sb, String content) {
        sb.append('\"').append(content).append('\"');
        return content.length();
    }
}