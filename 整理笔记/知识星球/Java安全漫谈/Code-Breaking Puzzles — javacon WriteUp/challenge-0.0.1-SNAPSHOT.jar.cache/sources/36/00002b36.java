package org.yaml.snakeyaml.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import org.unbescape.uri.UriEscape;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.external.com.google.gdata.util.common.base.Escaper;
import org.yaml.snakeyaml.external.com.google.gdata.util.common.base.PercentEscaper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/util/UriEncoder.class */
public abstract class UriEncoder {
    private static final CharsetDecoder UTF8Decoder = Charset.forName(UriEscape.DEFAULT_ENCODING).newDecoder().onMalformedInput(CodingErrorAction.REPORT);
    private static final String SAFE_CHARS = "-_.!~*'()@:$&,;=[]/";
    private static final Escaper escaper = new PercentEscaper(SAFE_CHARS, false);

    public static String encode(String uri) {
        return escaper.escape(uri);
    }

    public static String decode(ByteBuffer buff) throws CharacterCodingException {
        CharBuffer chars = UTF8Decoder.decode(buff);
        return chars.toString();
    }

    public static String decode(String buff) {
        try {
            return URLDecoder.decode(buff, UriEscape.DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new YAMLException(e);
        }
    }
}