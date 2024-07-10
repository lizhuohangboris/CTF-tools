package org.apache.tomcat.util.buf;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Locale;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/buf/MessageBytes.class */
public final class MessageBytes implements Cloneable, Serializable {
    private static final long serialVersionUID = 1;
    private int type;
    public static final int T_NULL = 0;
    public static final int T_STR = 1;
    public static final int T_BYTES = 2;
    public static final int T_CHARS = 3;
    private int hashCode;
    private boolean hasHashCode;
    private final ByteChunk byteC;
    private final CharChunk charC;
    private String strValue;
    private boolean hasStrValue;
    private long longValue;
    private boolean hasLongValue;
    private static final MessageBytesFactory factory = new MessageBytesFactory();

    private MessageBytes() {
        this.type = 0;
        this.hashCode = 0;
        this.hasHashCode = false;
        this.byteC = new ByteChunk();
        this.charC = new CharChunk();
        this.hasStrValue = false;
        this.hasLongValue = false;
    }

    public static MessageBytes newInstance() {
        return factory.newInstance();
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public boolean isNull() {
        return this.byteC.isNull() && this.charC.isNull() && !this.hasStrValue;
    }

    public void recycle() {
        this.type = 0;
        this.byteC.recycle();
        this.charC.recycle();
        this.strValue = null;
        this.hasStrValue = false;
        this.hasHashCode = false;
        this.hasLongValue = false;
    }

    public void setBytes(byte[] b, int off, int len) {
        this.byteC.setBytes(b, off, len);
        this.type = 2;
        this.hasStrValue = false;
        this.hasHashCode = false;
        this.hasLongValue = false;
    }

    public void setChars(char[] c, int off, int len) {
        this.charC.setChars(c, off, len);
        this.type = 3;
        this.hasStrValue = false;
        this.hasHashCode = false;
        this.hasLongValue = false;
    }

    public void setString(String s) {
        this.strValue = s;
        this.hasHashCode = false;
        this.hasLongValue = false;
        if (s == null) {
            this.hasStrValue = false;
            this.type = 0;
            return;
        }
        this.hasStrValue = true;
        this.type = 1;
    }

    public String toString() {
        if (this.hasStrValue) {
            return this.strValue;
        }
        switch (this.type) {
            case 2:
                this.strValue = this.byteC.toString();
                this.hasStrValue = true;
                return this.strValue;
            case 3:
                this.strValue = this.charC.toString();
                this.hasStrValue = true;
                return this.strValue;
            default:
                return null;
        }
    }

    public int getType() {
        return this.type;
    }

    public ByteChunk getByteChunk() {
        return this.byteC;
    }

    public CharChunk getCharChunk() {
        return this.charC;
    }

    public String getString() {
        return this.strValue;
    }

    public Charset getCharset() {
        return this.byteC.getCharset();
    }

    public void setCharset(Charset charset) {
        this.byteC.setCharset(charset);
    }

    public void toBytes() {
        if (isNull()) {
            return;
        }
        if (!this.byteC.isNull()) {
            this.type = 2;
            return;
        }
        toString();
        this.type = 2;
        Charset charset = this.byteC.getCharset();
        ByteBuffer result = charset.encode(this.strValue);
        this.byteC.setBytes(result.array(), result.arrayOffset(), result.limit());
    }

    public void toChars() {
        if (isNull()) {
            return;
        }
        if (!this.charC.isNull()) {
            this.type = 3;
            return;
        }
        toString();
        this.type = 3;
        char[] cc = this.strValue.toCharArray();
        this.charC.setChars(cc, 0, cc.length);
    }

    public int getLength() {
        if (this.type == 2) {
            return this.byteC.getLength();
        }
        if (this.type == 3) {
            return this.charC.getLength();
        }
        if (this.type == 1) {
            return this.strValue.length();
        }
        toString();
        if (this.strValue == null) {
            return 0;
        }
        return this.strValue.length();
    }

    public boolean equals(String s) {
        switch (this.type) {
            case 1:
                if (this.strValue == null) {
                    return s == null;
                }
                return this.strValue.equals(s);
            case 2:
                return this.byteC.equals(s);
            case 3:
                return this.charC.equals(s);
            default:
                return false;
        }
    }

    public boolean equalsIgnoreCase(String s) {
        switch (this.type) {
            case 1:
                if (this.strValue == null) {
                    return s == null;
                }
                return this.strValue.equalsIgnoreCase(s);
            case 2:
                return this.byteC.equalsIgnoreCase(s);
            case 3:
                return this.charC.equalsIgnoreCase(s);
            default:
                return false;
        }
    }

    public boolean equals(Object obj) {
        if (obj instanceof MessageBytes) {
            return equals((MessageBytes) obj);
        }
        return false;
    }

    public boolean equals(MessageBytes mb) {
        switch (this.type) {
            case 1:
                return mb.equals(this.strValue);
            default:
                if (mb.type != 3 && mb.type != 2) {
                    return equals(mb.toString());
                }
                if (mb.type == 3 && this.type == 3) {
                    return this.charC.equals(mb.charC);
                }
                if (mb.type == 2 && this.type == 2) {
                    return this.byteC.equals(mb.byteC);
                }
                if (mb.type == 3 && this.type == 2) {
                    return this.byteC.equals(mb.charC);
                }
                if (mb.type == 2 && this.type == 3) {
                    return mb.byteC.equals(this.charC);
                }
                return true;
        }
    }

    public boolean startsWithIgnoreCase(String s, int pos) {
        switch (this.type) {
            case 1:
                if (this.strValue == null || this.strValue.length() < pos + s.length()) {
                    return false;
                }
                for (int i = 0; i < s.length(); i++) {
                    if (Ascii.toLower(s.charAt(i)) != Ascii.toLower(this.strValue.charAt(pos + i))) {
                        return false;
                    }
                }
                return true;
            case 2:
                return this.byteC.startsWithIgnoreCase(s, pos);
            case 3:
                return this.charC.startsWithIgnoreCase(s, pos);
            default:
                return false;
        }
    }

    public int hashCode() {
        if (this.hasHashCode) {
            return this.hashCode;
        }
        int code = hash();
        this.hashCode = code;
        this.hasHashCode = true;
        return code;
    }

    private int hash() {
        int code = 0;
        switch (this.type) {
            case 1:
                for (int i = 0; i < this.strValue.length(); i++) {
                    code = (code * 37) + this.strValue.charAt(i);
                }
                return code;
            case 2:
                return this.byteC.hash();
            case 3:
                return this.charC.hash();
            default:
                return 0;
        }
    }

    public int indexOf(String s, int starting) {
        toString();
        return this.strValue.indexOf(s, starting);
    }

    public int indexOf(String s) {
        return indexOf(s, 0);
    }

    public int indexOfIgnoreCase(String s, int starting) {
        toString();
        String upper = this.strValue.toUpperCase(Locale.ENGLISH);
        String sU = s.toUpperCase(Locale.ENGLISH);
        return upper.indexOf(sU, starting);
    }

    public void duplicate(MessageBytes src) throws IOException {
        switch (src.getType()) {
            case 1:
                this.type = 1;
                String sc = src.getString();
                setString(sc);
                break;
            case 2:
                this.type = 2;
                ByteChunk bc = src.getByteChunk();
                this.byteC.allocate(2 * bc.getLength(), -1);
                this.byteC.append(bc);
                break;
            case 3:
                this.type = 3;
                CharChunk cc = src.getCharChunk();
                this.charC.allocate(2 * cc.getLength(), -1);
                this.charC.append(cc);
                break;
        }
        setCharset(src.getCharset());
    }

    public void setLong(long l) {
        this.byteC.allocate(32, 64);
        long current = l;
        byte[] buf = this.byteC.getBuffer();
        int start = 0;
        int end = 0;
        if (l == 0) {
            end = 0 + 1;
            buf[0] = 48;
        }
        if (l < 0) {
            current = -l;
            int i = end;
            end++;
            buf[i] = 45;
        }
        while (current > 0) {
            int digit = (int) (current % 10);
            current /= 10;
            int i2 = end;
            end++;
            buf[i2] = HexUtils.getHex(digit);
        }
        this.byteC.setOffset(0);
        this.byteC.setEnd(end);
        if (l < 0) {
            start = 0 + 1;
        }
        for (int end2 = end - 1; end2 > start; end2--) {
            byte temp = buf[start];
            buf[start] = buf[end2];
            buf[end2] = temp;
            start++;
        }
        this.longValue = l;
        this.hasStrValue = false;
        this.hasHashCode = false;
        this.hasLongValue = true;
        this.type = 2;
    }

    public long getLong() {
        if (this.hasLongValue) {
            return this.longValue;
        }
        switch (this.type) {
            case 2:
                this.longValue = this.byteC.getLong();
                break;
            default:
                this.longValue = Long.parseLong(toString());
                break;
        }
        this.hasLongValue = true;
        return this.longValue;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/buf/MessageBytes$MessageBytesFactory.class */
    public static class MessageBytesFactory {
        protected MessageBytesFactory() {
        }

        public MessageBytes newInstance() {
            return new MessageBytes();
        }
    }
}