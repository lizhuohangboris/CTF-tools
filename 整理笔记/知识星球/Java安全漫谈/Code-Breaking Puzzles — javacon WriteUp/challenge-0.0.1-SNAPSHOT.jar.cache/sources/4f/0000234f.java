package org.springframework.util;

import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/util/MimeTypeUtils.class */
public abstract class MimeTypeUtils {
    public static final String ALL_VALUE = "*/*";
    public static final String APPLICATION_JSON_VALUE = "application/json";
    public static final String APPLICATION_OCTET_STREAM_VALUE = "application/octet-stream";
    public static final String APPLICATION_XML_VALUE = "application/xml";
    public static final String IMAGE_GIF_VALUE = "image/gif";
    public static final String IMAGE_JPEG_VALUE = "image/jpeg";
    public static final String IMAGE_PNG_VALUE = "image/png";
    public static final String TEXT_HTML_VALUE = "text/html";
    public static final String TEXT_PLAIN_VALUE = "text/plain";
    public static final String TEXT_XML_VALUE = "text/xml";
    @Nullable
    private static volatile Random random;
    private static final byte[] BOUNDARY_CHARS = {45, 95, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90};
    public static final Comparator<MimeType> SPECIFICITY_COMPARATOR = new MimeType.SpecificityComparator();
    public static final MimeType ALL = MimeType.valueOf("*/*");
    public static final MimeType APPLICATION_JSON = MimeType.valueOf("application/json");
    public static final MimeType APPLICATION_OCTET_STREAM = MimeType.valueOf("application/octet-stream");
    public static final MimeType APPLICATION_XML = MimeType.valueOf("application/xml");
    public static final MimeType IMAGE_GIF = MimeType.valueOf("image/gif");
    public static final MimeType IMAGE_JPEG = MimeType.valueOf("image/jpeg");
    public static final MimeType IMAGE_PNG = MimeType.valueOf("image/png");
    public static final MimeType TEXT_HTML = MimeType.valueOf("text/html");
    public static final MimeType TEXT_PLAIN = MimeType.valueOf("text/plain");
    public static final MimeType TEXT_XML = MimeType.valueOf("text/xml");

    public static MimeType parseMimeType(String mimeType) {
        if (!StringUtils.hasLength(mimeType)) {
            throw new InvalidMimeTypeException(mimeType, "'mimeType' must not be empty");
        }
        int index = mimeType.indexOf(59);
        String fullType = (index >= 0 ? mimeType.substring(0, index) : mimeType).trim();
        if (fullType.isEmpty()) {
            throw new InvalidMimeTypeException(mimeType, "'mimeType' must not be empty");
        }
        if ("*".equals(fullType)) {
            fullType = "*/*";
        }
        int subIndex = fullType.indexOf(47);
        if (subIndex == -1) {
            throw new InvalidMimeTypeException(mimeType, "does not contain '/'");
        }
        if (subIndex == fullType.length() - 1) {
            throw new InvalidMimeTypeException(mimeType, "does not contain subtype after '/'");
        }
        String type = fullType.substring(0, subIndex);
        String subtype = fullType.substring(subIndex + 1, fullType.length());
        if ("*".equals(type) && !"*".equals(subtype)) {
            throw new InvalidMimeTypeException(mimeType, "wildcard type is legal only in '*/*' (all mime types)");
        }
        Map<String, String> parameters = null;
        do {
            int nextIndex = index + 1;
            boolean quoted = false;
            while (nextIndex < mimeType.length()) {
                char ch2 = mimeType.charAt(nextIndex);
                if (ch2 == ';') {
                    if (!quoted) {
                        break;
                    }
                } else if (ch2 == '\"') {
                    quoted = !quoted;
                }
                nextIndex++;
            }
            String parameter = mimeType.substring(index + 1, nextIndex).trim();
            if (parameter.length() > 0) {
                if (parameters == null) {
                    parameters = new LinkedHashMap<>(4);
                }
                int eqIndex = parameter.indexOf(61);
                if (eqIndex >= 0) {
                    String attribute = parameter.substring(0, eqIndex).trim();
                    String value = parameter.substring(eqIndex + 1, parameter.length()).trim();
                    parameters.put(attribute, value);
                }
            }
            index = nextIndex;
        } while (index < mimeType.length());
        try {
            return new MimeType(type, subtype, parameters);
        } catch (UnsupportedCharsetException ex) {
            throw new InvalidMimeTypeException(mimeType, "unsupported charset '" + ex.getCharsetName() + "'");
        } catch (IllegalArgumentException ex2) {
            throw new InvalidMimeTypeException(mimeType, ex2.getMessage());
        }
    }

    public static List<MimeType> parseMimeTypes(String mimeTypes) {
        if (!StringUtils.hasLength(mimeTypes)) {
            return Collections.emptyList();
        }
        String[] tokens = StringUtils.tokenizeToStringArray(mimeTypes, ",");
        List<MimeType> result = new ArrayList<>(tokens.length);
        for (String token : tokens) {
            result.add(parseMimeType(token));
        }
        return result;
    }

    public static String toString(Collection<? extends MimeType> mimeTypes) {
        StringBuilder builder = new StringBuilder();
        Iterator<? extends MimeType> iterator = mimeTypes.iterator();
        while (iterator.hasNext()) {
            MimeType mimeType = iterator.next();
            mimeType.appendTo(builder);
            if (iterator.hasNext()) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }

    public static void sortBySpecificity(List<MimeType> mimeTypes) {
        Assert.notNull(mimeTypes, "'mimeTypes' must not be null");
        if (mimeTypes.size() > 1) {
            mimeTypes.sort(SPECIFICITY_COMPARATOR);
        }
    }

    private static Random initRandom() {
        Random randomToUse = random;
        if (randomToUse == null) {
            synchronized (MimeTypeUtils.class) {
                randomToUse = random;
                if (randomToUse == null) {
                    randomToUse = new SecureRandom();
                    random = randomToUse;
                }
            }
        }
        return randomToUse;
    }

    public static byte[] generateMultipartBoundary() {
        Random randomToUse = initRandom();
        byte[] boundary = new byte[randomToUse.nextInt(11) + 30];
        for (int i = 0; i < boundary.length; i++) {
            boundary[i] = BOUNDARY_CHARS[randomToUse.nextInt(BOUNDARY_CHARS.length)];
        }
        return boundary;
    }

    public static String generateMultipartBoundaryString() {
        return new String(generateMultipartBoundary(), StandardCharsets.US_ASCII);
    }
}