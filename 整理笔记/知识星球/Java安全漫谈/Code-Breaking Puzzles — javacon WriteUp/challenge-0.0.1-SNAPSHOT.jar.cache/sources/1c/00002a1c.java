package org.thymeleaf.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;
import java.util.StringTokenizer;
import org.unbescape.html.HtmlEscape;
import org.unbescape.java.JavaEscape;
import org.unbescape.javascript.JavaScriptEscape;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/StringUtils.class */
public final class StringUtils {
    private static final String ALPHA_NUMERIC = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final Random RANDOM = new Random();

    public static String toString(Object target) {
        if (target == null) {
            return null;
        }
        return target.toString();
    }

    public static String abbreviate(Object target, int maxSize) {
        Validate.isTrue(maxSize >= 3, "Maximum size must be greater or equal to 3");
        if (target == null) {
            return null;
        }
        String str = target.toString();
        if (str.length() <= maxSize) {
            return str;
        }
        StringBuilder strBuilder = new StringBuilder(maxSize + 2);
        strBuilder.append((CharSequence) str, 0, maxSize - 3);
        strBuilder.append("...");
        return strBuilder.toString();
    }

    public static Boolean equals(Object first, Object second) {
        if (first == null && second == null) {
            return Boolean.TRUE;
        }
        if (first == null || second == null) {
            return Boolean.FALSE;
        }
        return Boolean.valueOf(first.toString().equals(second.toString()));
    }

    public static Boolean equalsIgnoreCase(Object first, Object second) {
        if (first == null && second == null) {
            return Boolean.TRUE;
        }
        if (first == null || second == null) {
            return Boolean.FALSE;
        }
        return Boolean.valueOf(first.toString().equalsIgnoreCase(second.toString()));
    }

    public static Boolean contains(Object target, String fragment) {
        Validate.notNull(target, "Cannot apply contains on null");
        Validate.notNull(fragment, "Fragment cannot be null");
        return Boolean.valueOf(target.toString().contains(fragment));
    }

    public static Boolean containsIgnoreCase(Object target, String fragment, Locale locale) {
        Validate.notNull(target, "Cannot apply containsIgnoreCase on null");
        Validate.notNull(fragment, "Fragment cannot be null");
        Validate.notNull(locale, "Locale cannot be null");
        return Boolean.valueOf(target.toString().toUpperCase(locale).contains(fragment.toUpperCase(locale)));
    }

    public static Boolean startsWith(Object target, String prefix) {
        Validate.notNull(target, "Cannot apply startsWith on null");
        Validate.notNull(prefix, "Prefix cannot be null");
        return Boolean.valueOf(target.toString().startsWith(prefix));
    }

    public static Boolean endsWith(Object target, String suffix) {
        Validate.notNull(target, "Cannot apply endsWith on null");
        Validate.notNull(suffix, "Suffix cannot be null");
        return Boolean.valueOf(target.toString().endsWith(suffix));
    }

    public static String substring(Object target, int beginIndex, int endIndex) {
        if (target == null) {
            return null;
        }
        Validate.isTrue(beginIndex >= 0, "Begin index must be >= 0");
        return new String(target.toString().substring(beginIndex, endIndex));
    }

    public static String substring(Object target, int beginIndex) {
        if (target == null) {
            return null;
        }
        String str = target.toString();
        int len = str.length();
        Validate.isTrue(beginIndex >= 0 && beginIndex < len, "beginIndex must be >= 0 and < " + len);
        return str.substring(beginIndex);
    }

    public static String substringAfter(Object target, String substr) {
        String str;
        int index;
        Validate.notNull(substr, "Parameter substring cannot be null");
        if (target == null || (index = (str = target.toString()).indexOf(substr)) < 0) {
            return null;
        }
        return str.substring(index + substr.length());
    }

    public static String substringBefore(Object target, String substr) {
        String str;
        int index;
        Validate.notNull(substr, "Parameter substring cannot be null");
        if (target == null || (index = (str = target.toString()).indexOf(substr)) < 0) {
            return null;
        }
        return new String(str.substring(0, index));
    }

    public static String prepend(Object target, String prefix) {
        Validate.notNull(prefix, "Prefix cannot be null");
        if (target == null) {
            return null;
        }
        return prefix + target;
    }

    public static String append(Object target, String suffix) {
        Validate.notNull(suffix, "Suffix cannot be null");
        if (target == null) {
            return null;
        }
        return target + suffix;
    }

    public static String repeat(Object target, int times) {
        if (target == null) {
            return null;
        }
        String str = target.toString();
        StringBuilder strBuilder = new StringBuilder((str.length() * times) + 10);
        for (int i = 0; i < times; i++) {
            strBuilder.append(str);
        }
        return strBuilder.toString();
    }

    public static String concat(Object... values) {
        return concatReplaceNulls("", values);
    }

    public static String concatReplaceNulls(String nullValue, Object... values) {
        if (values == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Object value : values) {
            if (value == null) {
                sb.append(nullValue);
            } else {
                sb.append(value.toString());
            }
        }
        return sb.toString();
    }

    public static Integer indexOf(Object target, String fragment) {
        Validate.notNull(target, "Cannot apply indexOf on null");
        Validate.notNull(fragment, "Fragment cannot be null");
        return Integer.valueOf(target.toString().indexOf(fragment));
    }

    public static boolean isEmpty(String target) {
        return target == null || target.length() == 0;
    }

    public static boolean isEmptyOrWhitespace(String target) {
        int targetLen;
        if (target == null || (targetLen = target.length()) == 0) {
            return true;
        }
        char c0 = target.charAt(0);
        if (c0 < 'a' || c0 > 'z') {
            if (c0 >= 'A' && c0 <= 'Z') {
                return false;
            }
            for (int i = 0; i < targetLen; i++) {
                char c = target.charAt(i);
                if (c != ' ' && !Character.isWhitespace(c)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static String join(Object[] target, String separator) {
        Validate.notNull(separator, "Separator cannot be null");
        if (target == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        if (target.length > 0) {
            sb.append(target[0]);
            for (int i = 1; i < target.length; i++) {
                sb.append(separator);
                sb.append(target[i]);
            }
        }
        return sb.toString();
    }

    public static String join(Iterable<?> target, String separator) {
        Validate.notNull(separator, "Separator cannot be null");
        if (target == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        Iterator<?> it = target.iterator();
        if (it.hasNext()) {
            sb.append(it.next());
            while (it.hasNext()) {
                sb.append(separator);
                sb.append(it.next());
            }
        }
        return sb.toString();
    }

    public static String join(Iterable<?> target, char separator) {
        if (target == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        Iterator<?> it = target.iterator();
        if (it.hasNext()) {
            sb.append(it.next());
            while (it.hasNext()) {
                sb.append(separator);
                sb.append(it.next());
            }
        }
        return sb.toString();
    }

    public static String[] split(Object target, String separator) {
        Validate.notNull(separator, "Separator cannot be null");
        if (target == null) {
            return null;
        }
        StringTokenizer strTok = new StringTokenizer(target.toString(), separator);
        int size = strTok.countTokens();
        String[] array = new String[size];
        for (int i = 0; i < size; i++) {
            array[i] = strTok.nextToken();
        }
        return array;
    }

    public static Integer length(Object target) {
        Validate.notNull(target, "Cannot apply length on null");
        return Integer.valueOf(target.toString().length());
    }

    public static String replace(Object target, String before, String after) {
        Validate.notNull(before, "Parameter \"before\" cannot be null");
        Validate.notNull(after, "Parameter \"after\" cannot be null");
        if (target == null) {
            return null;
        }
        String targetStr = target.toString();
        int targetStrLen = targetStr.length();
        int beforeLen = before.length();
        if (targetStrLen == 0 || beforeLen == 0) {
            return targetStr;
        }
        int index = targetStr.indexOf(before);
        if (index < 0) {
            return targetStr;
        }
        StringBuilder stringBuilder = new StringBuilder(targetStrLen + 10);
        int lastPos = 0;
        while (index >= 0) {
            stringBuilder.append((CharSequence) targetStr, lastPos, index);
            stringBuilder.append(after);
            lastPos = index + beforeLen;
            index = targetStr.indexOf(before, lastPos);
        }
        stringBuilder.append((CharSequence) targetStr, lastPos, targetStrLen);
        return stringBuilder.toString();
    }

    public static String toUpperCase(Object target, Locale locale) {
        Validate.notNull(locale, "Locale cannot be null");
        if (target == null) {
            return null;
        }
        return target.toString().toUpperCase(locale);
    }

    public static String toLowerCase(Object target, Locale locale) {
        Validate.notNull(locale, "Locale cannot be null");
        if (target == null) {
            return null;
        }
        return target.toString().toLowerCase(locale);
    }

    public static String trim(Object target) {
        if (target == null) {
            return null;
        }
        return target.toString().trim();
    }

    public static String capitalize(Object target) {
        if (target == null) {
            return null;
        }
        StringBuilder result = new StringBuilder(target.toString());
        if (result.length() > 0) {
            result.setCharAt(0, Character.toTitleCase(result.charAt(0)));
        }
        return result.toString();
    }

    public static String unCapitalize(Object target) {
        if (target == null) {
            return null;
        }
        StringBuilder result = new StringBuilder(target.toString());
        if (result.length() > 0) {
            result.setCharAt(0, Character.toLowerCase(result.charAt(0)));
        }
        return result.toString();
    }

    private static int findNextWord(char[] buffer, int idx, char[] delimiterChars) {
        boolean z;
        int len = buffer.length;
        if (idx < 0 || idx >= len) {
            return -1;
        }
        boolean foundDelimiters = idx == 0;
        for (int i = idx; i < len; i++) {
            char ch2 = buffer[i];
            if (delimiterChars == null) {
                z = Character.isWhitespace(ch2);
            } else {
                z = Arrays.binarySearch(delimiterChars, ch2) >= 0;
            }
            boolean isDelimiter = z;
            if (isDelimiter) {
                foundDelimiters = true;
            } else if (foundDelimiters) {
                return i;
            }
        }
        return -1;
    }

    public static String capitalizeWords(Object target) {
        return capitalizeWords(target, null);
    }

    public static String capitalizeWords(Object target, Object delimiters) {
        if (target == null) {
            return null;
        }
        char[] buffer = target.toString().toCharArray();
        char[] delimiterChars = delimiters == null ? null : delimiters.toString().toCharArray();
        if (delimiterChars != null) {
            Arrays.sort(delimiterChars);
        }
        int findNextWord = findNextWord(buffer, 0, delimiterChars);
        while (true) {
            int idx = findNextWord;
            if (idx != -1) {
                buffer[idx] = Character.toTitleCase(buffer[idx]);
                findNextWord = findNextWord(buffer, idx + 1, delimiterChars);
            } else {
                return new String(buffer);
            }
        }
    }

    public static String escapeXml(Object target) {
        if (target == null) {
            return null;
        }
        return HtmlEscape.escapeHtml4Xml(target.toString());
    }

    public static String escapeJavaScript(Object target) {
        if (target == null) {
            return null;
        }
        return JavaScriptEscape.escapeJavaScript(target.toString());
    }

    public static String escapeJava(Object target) {
        if (target == null) {
            return null;
        }
        return JavaEscape.escapeJava(target.toString());
    }

    public static String unescapeJavaScript(Object target) {
        if (target == null) {
            return null;
        }
        return JavaScriptEscape.unescapeJavaScript(target.toString());
    }

    public static String unescapeJava(Object target) {
        if (target == null) {
            return null;
        }
        return JavaEscape.unescapeJava(target.toString());
    }

    public static String randomAlphanumeric(int count) {
        StringBuilder strBuilder = new StringBuilder(count);
        int anLen = ALPHA_NUMERIC.length();
        synchronized (RANDOM) {
            for (int i = 0; i < count; i++) {
                strBuilder.append(ALPHA_NUMERIC.charAt(RANDOM.nextInt(anLen)));
            }
        }
        return strBuilder.toString();
    }

    private StringUtils() {
    }
}