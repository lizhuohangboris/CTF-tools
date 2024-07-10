package org.apache.tomcat.util.buf;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/buf/B2CConverter.class */
public class B2CConverter {
    private static final StringManager sm = StringManager.getManager(B2CConverter.class);
    private static final Map<String, Charset> encodingToCharsetCache = new HashMap();
    protected static final int LEFTOVER_SIZE = 9;
    private final CharsetDecoder decoder;
    private ByteBuffer bb;
    private CharBuffer cb;
    private final ByteBuffer leftovers;

    static {
        for (Charset charset : Charset.availableCharsets().values()) {
            encodingToCharsetCache.put(charset.name().toLowerCase(Locale.ENGLISH), charset);
            for (String alias : charset.aliases()) {
                encodingToCharsetCache.put(alias.toLowerCase(Locale.ENGLISH), charset);
            }
        }
    }

    public static Charset getCharset(String enc) throws UnsupportedEncodingException {
        String lowerCaseEnc = enc.toLowerCase(Locale.ENGLISH);
        Charset charset = encodingToCharsetCache.get(lowerCaseEnc);
        if (charset == null) {
            throw new UnsupportedEncodingException(sm.getString("b2cConverter.unknownEncoding", lowerCaseEnc));
        }
        return charset;
    }

    public B2CConverter(Charset charset) {
        this(charset, false);
    }

    public B2CConverter(Charset charset, boolean replaceOnError) {
        CodingErrorAction action;
        this.bb = null;
        this.cb = null;
        byte[] left = new byte[9];
        this.leftovers = ByteBuffer.wrap(left);
        if (replaceOnError) {
            action = CodingErrorAction.REPLACE;
        } else {
            action = CodingErrorAction.REPORT;
        }
        if (charset.equals(StandardCharsets.UTF_8)) {
            this.decoder = new Utf8Decoder();
        } else {
            this.decoder = charset.newDecoder();
        }
        this.decoder.onMalformedInput(action);
        this.decoder.onUnmappableCharacter(action);
    }

    public void recycle() {
        this.decoder.reset();
        this.leftovers.position(0);
    }

    public void convert(ByteChunk bc, CharChunk cc, boolean endOfInput) throws IOException {
        CoderResult result;
        if (this.bb == null || this.bb.array() != bc.getBuffer()) {
            this.bb = ByteBuffer.wrap(bc.getBuffer(), bc.getStart(), bc.getLength());
        } else {
            this.bb.limit(bc.getEnd());
            this.bb.position(bc.getStart());
        }
        if (this.cb == null || this.cb.array() != cc.getBuffer()) {
            this.cb = CharBuffer.wrap(cc.getBuffer(), cc.getEnd(), cc.getBuffer().length - cc.getEnd());
        } else {
            this.cb.limit(cc.getBuffer().length);
            this.cb.position(cc.getEnd());
        }
        if (this.leftovers.position() > 0) {
            int pos = this.cb.position();
            do {
                this.leftovers.put(bc.subtractB());
                this.leftovers.flip();
                result = this.decoder.decode(this.leftovers, this.cb, endOfInput);
                this.leftovers.position(this.leftovers.limit());
                this.leftovers.limit(this.leftovers.array().length);
                if (!result.isUnderflow()) {
                    break;
                }
            } while (this.cb.position() == pos);
            if (result.isError() || result.isMalformed()) {
                result.throwException();
            }
            this.bb.position(bc.getStart());
            this.leftovers.position(0);
        }
        CoderResult result2 = this.decoder.decode(this.bb, this.cb, endOfInput);
        if (result2.isError() || result2.isMalformed()) {
            result2.throwException();
        } else if (result2.isOverflow()) {
            bc.setOffset(this.bb.position());
            cc.setEnd(this.cb.position());
        } else if (result2.isUnderflow()) {
            bc.setOffset(this.bb.position());
            cc.setEnd(this.cb.position());
            if (bc.getLength() > 0) {
                this.leftovers.limit(this.leftovers.array().length);
                this.leftovers.position(bc.getLength());
                bc.subtract(this.leftovers.array(), 0, bc.getLength());
            }
        }
    }

    public void convert(ByteBuffer bc, CharBuffer cc, ByteChunk.ByteInputChannel ic, boolean endOfInput) throws IOException {
        byte chr;
        CoderResult result;
        if (this.bb == null || this.bb.array() != bc.array()) {
            this.bb = ByteBuffer.wrap(bc.array(), bc.arrayOffset() + bc.position(), bc.remaining());
        } else {
            this.bb.limit(bc.limit());
            this.bb.position(bc.position());
        }
        if (this.cb == null || this.cb.array() != cc.array()) {
            this.cb = CharBuffer.wrap(cc.array(), cc.limit(), cc.capacity() - cc.limit());
        } else {
            this.cb.limit(cc.capacity());
            this.cb.position(cc.limit());
        }
        if (this.leftovers.position() > 0) {
            int pos = this.cb.position();
            do {
                if (bc.remaining() == 0) {
                    int n = ic.realReadBytes();
                    chr = n < 0 ? (byte) -1 : bc.get();
                } else {
                    chr = bc.get();
                }
                this.leftovers.put(chr);
                this.leftovers.flip();
                result = this.decoder.decode(this.leftovers, this.cb, endOfInput);
                this.leftovers.position(this.leftovers.limit());
                this.leftovers.limit(this.leftovers.array().length);
                if (!result.isUnderflow()) {
                    break;
                }
            } while (this.cb.position() == pos);
            if (result.isError() || result.isMalformed()) {
                result.throwException();
            }
            this.bb.position(bc.position());
            this.leftovers.position(0);
        }
        CoderResult result2 = this.decoder.decode(this.bb, this.cb, endOfInput);
        if (result2.isError() || result2.isMalformed()) {
            result2.throwException();
        } else if (result2.isOverflow()) {
            bc.position(this.bb.position());
            cc.limit(this.cb.position());
        } else if (result2.isUnderflow()) {
            bc.position(this.bb.position());
            cc.limit(this.cb.position());
            if (bc.remaining() > 0) {
                this.leftovers.limit(this.leftovers.array().length);
                this.leftovers.position(bc.remaining());
                bc.get(this.leftovers.array(), 0, bc.remaining());
            }
        }
    }

    public Charset getCharset() {
        return this.decoder.charset();
    }
}