package org.springframework.util;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/util/StringUtils.class */
public abstract class StringUtils {
    private static final String FOLDER_SEPARATOR = "/";
    private static final String WINDOWS_FOLDER_SEPARATOR = "\\";
    private static final String TOP_PATH = "..";
    private static final String CURRENT_PATH = ".";
    private static final char EXTENSION_SEPARATOR = '.';

    public static boolean isEmpty(@Nullable Object str) {
        return str == null || "".equals(str);
    }

    public static boolean hasLength(@Nullable CharSequence str) {
        return str != null && str.length() > 0;
    }

    public static boolean hasLength(@Nullable String str) {
        return (str == null || str.isEmpty()) ? false : true;
    }

    public static boolean hasText(@Nullable CharSequence str) {
        return str != null && str.length() > 0 && containsText(str);
    }

    public static boolean hasText(@Nullable String str) {
        return (str == null || str.isEmpty() || !containsText(str)) ? false : true;
    }

    private static boolean containsText(CharSequence str) {
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsWhitespace(@Nullable CharSequence str) {
        if (!hasLength(str)) {
            return false;
        }
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsWhitespace(@Nullable String str) {
        return containsWhitespace((CharSequence) str);
    }

    public static String trimWhitespace(String str) {
        if (!hasLength(str)) {
            return str;
        }
        int beginIndex = 0;
        int endIndex = str.length() - 1;
        while (beginIndex <= endIndex && Character.isWhitespace(str.charAt(beginIndex))) {
            beginIndex++;
        }
        while (endIndex > beginIndex && Character.isWhitespace(str.charAt(endIndex))) {
            endIndex--;
        }
        return str.substring(beginIndex, endIndex + 1);
    }

    public static String trimAllWhitespace(String str) {
        if (!hasLength(str)) {
            return str;
        }
        int len = str.length();
        StringBuilder sb = new StringBuilder(str.length());
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if (!Character.isWhitespace(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String trimLeadingWhitespace(String str) {
        if (!hasLength(str)) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str);
        while (sb.length() > 0 && Character.isWhitespace(sb.charAt(0))) {
            sb.deleteCharAt(0);
        }
        return sb.toString();
    }

    public static String trimTrailingWhitespace(String str) {
        if (!hasLength(str)) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str);
        while (sb.length() > 0 && Character.isWhitespace(sb.charAt(sb.length() - 1))) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public static String trimLeadingCharacter(String str, char leadingCharacter) {
        if (!hasLength(str)) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str);
        while (sb.length() > 0 && sb.charAt(0) == leadingCharacter) {
            sb.deleteCharAt(0);
        }
        return sb.toString();
    }

    public static String trimTrailingCharacter(String str, char trailingCharacter) {
        if (!hasLength(str)) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str);
        while (sb.length() > 0 && sb.charAt(sb.length() - 1) == trailingCharacter) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public static boolean startsWithIgnoreCase(@Nullable String str, @Nullable String prefix) {
        return str != null && prefix != null && str.length() >= prefix.length() && str.regionMatches(true, 0, prefix, 0, prefix.length());
    }

    public static boolean endsWithIgnoreCase(@Nullable String str, @Nullable String suffix) {
        return str != null && suffix != null && str.length() >= suffix.length() && str.regionMatches(true, str.length() - suffix.length(), suffix, 0, suffix.length());
    }

    public static boolean substringMatch(CharSequence str, int index, CharSequence substring) {
        if (index + substring.length() > str.length()) {
            return false;
        }
        for (int i = 0; i < substring.length(); i++) {
            if (str.charAt(index + i) != substring.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    public static int countOccurrencesOf(String str, String sub) {
        if (!hasLength(str) || !hasLength(sub)) {
            return 0;
        }
        int count = 0;
        int i = 0;
        while (true) {
            int pos = i;
            int idx = str.indexOf(sub, pos);
            if (idx != -1) {
                count++;
                i = idx + sub.length();
            } else {
                return count;
            }
        }
    }

    public static String replace(String inString, String oldPattern, @Nullable String newPattern) {
        if (!hasLength(inString) || !hasLength(oldPattern) || newPattern == null) {
            return inString;
        }
        int index = inString.indexOf(oldPattern);
        if (index == -1) {
            return inString;
        }
        int capacity = inString.length();
        if (newPattern.length() > oldPattern.length()) {
            capacity += 16;
        }
        StringBuilder sb = new StringBuilder(capacity);
        int pos = 0;
        int patLen = oldPattern.length();
        while (index >= 0) {
            sb.append(inString.substring(pos, index));
            sb.append(newPattern);
            pos = index + patLen;
            index = inString.indexOf(oldPattern, pos);
        }
        sb.append(inString.substring(pos));
        return sb.toString();
    }

    public static String delete(String inString, String pattern) {
        return replace(inString, pattern, "");
    }

    public static String deleteAny(String inString, @Nullable String charsToDelete) {
        if (!hasLength(inString) || !hasLength(charsToDelete)) {
            return inString;
        }
        StringBuilder sb = new StringBuilder(inString.length());
        for (int i = 0; i < inString.length(); i++) {
            char c = inString.charAt(i);
            if (charsToDelete.indexOf(c) == -1) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    @Nullable
    public static String quote(@Nullable String str) {
        if (str != null) {
            return "'" + str + "'";
        }
        return null;
    }

    @Nullable
    public static Object quoteIfString(@Nullable Object obj) {
        return obj instanceof String ? quote((String) obj) : obj;
    }

    public static String unqualify(String qualifiedName) {
        return unqualify(qualifiedName, '.');
    }

    public static String unqualify(String qualifiedName, char separator) {
        return qualifiedName.substring(qualifiedName.lastIndexOf(separator) + 1);
    }

    public static String capitalize(String str) {
        return changeFirstCharacterCase(str, true);
    }

    public static String uncapitalize(String str) {
        return changeFirstCharacterCase(str, false);
    }

    private static String changeFirstCharacterCase(String str, boolean capitalize) {
        char updatedChar;
        if (!hasLength(str)) {
            return str;
        }
        char baseChar = str.charAt(0);
        if (capitalize) {
            updatedChar = Character.toUpperCase(baseChar);
        } else {
            updatedChar = Character.toLowerCase(baseChar);
        }
        if (baseChar == updatedChar) {
            return str;
        }
        char[] chars = str.toCharArray();
        chars[0] = updatedChar;
        return new String(chars, 0, chars.length);
    }

    @Nullable
    public static String getFilename(@Nullable String path) {
        if (path == null) {
            return null;
        }
        int separatorIndex = path.lastIndexOf("/");
        return separatorIndex != -1 ? path.substring(separatorIndex + 1) : path;
    }

    @Nullable
    public static String getFilenameExtension(@Nullable String path) {
        int extIndex;
        if (path == null || (extIndex = path.lastIndexOf(46)) == -1) {
            return null;
        }
        int folderIndex = path.lastIndexOf("/");
        if (folderIndex > extIndex) {
            return null;
        }
        return path.substring(extIndex + 1);
    }

    public static String stripFilenameExtension(String path) {
        int extIndex = path.lastIndexOf(46);
        if (extIndex == -1) {
            return path;
        }
        int folderIndex = path.lastIndexOf("/");
        if (folderIndex > extIndex) {
            return path;
        }
        return path.substring(0, extIndex);
    }

    public static String applyRelativePath(String path, String relativePath) {
        int separatorIndex = path.lastIndexOf("/");
        if (separatorIndex != -1) {
            String newPath = path.substring(0, separatorIndex);
            if (!relativePath.startsWith("/")) {
                newPath = newPath + "/";
            }
            return newPath + relativePath;
        }
        return relativePath;
    }

    public static String cleanPath(String path) {
        if (!hasLength(path)) {
            return path;
        }
        String pathToUse = replace(path, WINDOWS_FOLDER_SEPARATOR, "/");
        int prefixIndex = pathToUse.indexOf(58);
        String prefix = "";
        if (prefixIndex != -1) {
            prefix = pathToUse.substring(0, prefixIndex + 1);
            if (prefix.contains("/")) {
                prefix = "";
            } else {
                pathToUse = pathToUse.substring(prefixIndex + 1);
            }
        }
        if (pathToUse.startsWith("/")) {
            prefix = prefix + "/";
            pathToUse = pathToUse.substring(1);
        }
        String[] pathArray = delimitedListToStringArray(pathToUse, "/");
        LinkedList<String> pathElements = new LinkedList<>();
        int tops = 0;
        for (int i = pathArray.length - 1; i >= 0; i--) {
            String element = pathArray[i];
            if (!".".equals(element)) {
                if ("..".equals(element)) {
                    tops++;
                } else if (tops > 0) {
                    tops--;
                } else {
                    pathElements.add(0, element);
                }
            }
        }
        for (int i2 = 0; i2 < tops; i2++) {
            pathElements.add(0, "..");
        }
        if (pathElements.size() == 1 && "".equals(pathElements.getLast()) && !prefix.endsWith("/")) {
            pathElements.add(0, ".");
        }
        return prefix + collectionToDelimitedString(pathElements, "/");
    }

    public static boolean pathEquals(String path1, String path2) {
        return cleanPath(path1).equals(cleanPath(path2));
    }

    public static String uriDecode(String source, Charset charset) {
        int length = source.length();
        if (length == 0) {
            return source;
        }
        Assert.notNull(charset, "Charset must not be null");
        ByteArrayOutputStream bos = new ByteArrayOutputStream(length);
        boolean changed = false;
        int i = 0;
        while (i < length) {
            int ch2 = source.charAt(i);
            if (ch2 == 37) {
                if (i + 2 < length) {
                    char hex1 = source.charAt(i + 1);
                    char hex2 = source.charAt(i + 2);
                    int u = Character.digit(hex1, 16);
                    int l = Character.digit(hex2, 16);
                    if (u == -1 || l == -1) {
                        throw new IllegalArgumentException("Invalid encoded sequence \"" + source.substring(i) + "\"");
                    }
                    bos.write((char) ((u << 4) + l));
                    i += 2;
                    changed = true;
                } else {
                    throw new IllegalArgumentException("Invalid encoded sequence \"" + source.substring(i) + "\"");
                }
            } else {
                bos.write(ch2);
            }
            i++;
        }
        return changed ? new String(bos.toByteArray(), charset) : source;
    }

    @Nullable
    public static Locale parseLocale(String localeValue) {
        String[] tokens = tokenizeLocaleSource(localeValue);
        if (tokens.length == 1) {
            validateLocalePart(localeValue);
            Locale resolved = Locale.forLanguageTag(localeValue);
            if (resolved.getLanguage().length() > 0) {
                return resolved;
            }
            return null;
        }
        return parseLocaleTokens(localeValue, tokens);
    }

    @Nullable
    public static Locale parseLocaleString(String localeString) {
        return parseLocaleTokens(localeString, tokenizeLocaleSource(localeString));
    }

    private static String[] tokenizeLocaleSource(String localeSource) {
        return tokenizeToStringArray(localeSource, "_ ", false, false);
    }

    @Nullable
    private static Locale parseLocaleTokens(String localeString, String[] tokens) {
        String language = tokens.length > 0 ? tokens[0] : "";
        String country = tokens.length > 1 ? tokens[1] : "";
        validateLocalePart(language);
        validateLocalePart(country);
        String variant = "";
        if (tokens.length > 2) {
            int endIndexOfCountryCode = localeString.indexOf(country, language.length()) + country.length();
            variant = trimLeadingWhitespace(localeString.substring(endIndexOfCountryCode));
            if (variant.startsWith("_")) {
                variant = trimLeadingCharacter(variant, '_');
            }
        }
        if (variant.isEmpty() && country.startsWith("#")) {
            variant = country;
            country = "";
        }
        if (language.length() > 0) {
            return new Locale(language, country, variant);
        }
        return null;
    }

    private static void validateLocalePart(String localePart) {
        for (int i = 0; i < localePart.length(); i++) {
            char ch2 = localePart.charAt(i);
            if (ch2 != ' ' && ch2 != '_' && ch2 != '-' && ch2 != '#' && !Character.isLetterOrDigit(ch2)) {
                throw new IllegalArgumentException("Locale part \"" + localePart + "\" contains invalid characters");
            }
        }
    }

    @Deprecated
    public static String toLanguageTag(Locale locale) {
        return locale.getLanguage() + (hasText(locale.getCountry()) ? "-" + locale.getCountry() : "");
    }

    public static TimeZone parseTimeZoneString(String timeZoneString) {
        TimeZone timeZone = TimeZone.getTimeZone(timeZoneString);
        if ("GMT".equals(timeZone.getID()) && !timeZoneString.startsWith("GMT")) {
            throw new IllegalArgumentException("Invalid time zone specification '" + timeZoneString + "'");
        }
        return timeZone;
    }

    public static String[] addStringToArray(@Nullable String[] array, String str) {
        if (ObjectUtils.isEmpty((Object[]) array)) {
            return new String[]{str};
        }
        String[] newArr = new String[array.length + 1];
        System.arraycopy(array, 0, newArr, 0, array.length);
        newArr[array.length] = str;
        return newArr;
    }

    @Nullable
    public static String[] concatenateStringArrays(@Nullable String[] array1, @Nullable String[] array2) {
        if (ObjectUtils.isEmpty((Object[]) array1)) {
            return array2;
        }
        if (ObjectUtils.isEmpty((Object[]) array2)) {
            return array1;
        }
        String[] newArr = new String[array1.length + array2.length];
        System.arraycopy(array1, 0, newArr, 0, array1.length);
        System.arraycopy(array2, 0, newArr, array1.length, array2.length);
        return newArr;
    }

    @Nullable
    @Deprecated
    public static String[] mergeStringArrays(@Nullable String[] array1, @Nullable String[] array2) {
        if (ObjectUtils.isEmpty((Object[]) array1)) {
            return array2;
        }
        if (ObjectUtils.isEmpty((Object[]) array2)) {
            return array1;
        }
        List<String> result = new ArrayList<>();
        result.addAll(Arrays.asList(array1));
        for (String str : array2) {
            if (!result.contains(str)) {
                result.add(str);
            }
        }
        return toStringArray(result);
    }

    public static String[] sortStringArray(String[] array) {
        if (ObjectUtils.isEmpty((Object[]) array)) {
            return new String[0];
        }
        Arrays.sort(array);
        return array;
    }

    public static String[] toStringArray(Collection<String> collection) {
        return (String[]) collection.toArray(new String[0]);
    }

    public static String[] toStringArray(Enumeration<String> enumeration) {
        return toStringArray(Collections.list(enumeration));
    }

    public static String[] trimArrayElements(String[] array) {
        if (ObjectUtils.isEmpty((Object[]) array)) {
            return array;
        }
        String[] result = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            String element = array[i];
            result[i] = element != null ? element.trim() : null;
        }
        return result;
    }

    public static String[] removeDuplicateStrings(String[] array) {
        if (ObjectUtils.isEmpty((Object[]) array)) {
            return array;
        }
        Set<String> set = new LinkedHashSet<>(Arrays.asList(array));
        return toStringArray(set);
    }

    @Nullable
    public static String[] split(@Nullable String toSplit, @Nullable String delimiter) {
        int offset;
        if (!hasLength(toSplit) || !hasLength(delimiter) || (offset = toSplit.indexOf(delimiter)) < 0) {
            return null;
        }
        String beforeDelimiter = toSplit.substring(0, offset);
        String afterDelimiter = toSplit.substring(offset + delimiter.length());
        return new String[]{beforeDelimiter, afterDelimiter};
    }

    @Nullable
    public static Properties splitArrayElementsIntoProperties(String[] array, String delimiter) {
        return splitArrayElementsIntoProperties(array, delimiter, null);
    }

    @Nullable
    public static Properties splitArrayElementsIntoProperties(String[] array, String delimiter, @Nullable String charsToDelete) {
        if (ObjectUtils.isEmpty((Object[]) array)) {
            return null;
        }
        Properties result = new Properties();
        for (String element : array) {
            if (charsToDelete != null) {
                element = deleteAny(element, charsToDelete);
            }
            String[] splittedElement = split(element, delimiter);
            if (splittedElement != null) {
                result.setProperty(splittedElement[0].trim(), splittedElement[1].trim());
            }
        }
        return result;
    }

    public static String[] tokenizeToStringArray(@Nullable String str, String delimiters) {
        return tokenizeToStringArray(str, delimiters, true, true);
    }

    public static String[] tokenizeToStringArray(@Nullable String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {
        if (str == null) {
            return new String[0];
        }
        StringTokenizer st = new StringTokenizer(str, delimiters);
        List<String> tokens = new ArrayList<>();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (trimTokens) {
                token = token.trim();
            }
            if (!ignoreEmptyTokens || token.length() > 0) {
                tokens.add(token);
            }
        }
        return toStringArray(tokens);
    }

    public static String[] delimitedListToStringArray(@Nullable String str, @Nullable String delimiter) {
        return delimitedListToStringArray(str, delimiter, null);
    }

    public static String[] delimitedListToStringArray(@Nullable String str, @Nullable String delimiter, @Nullable String charsToDelete) {
        int pos;
        if (str == null) {
            return new String[0];
        }
        if (delimiter == null) {
            return new String[]{str};
        }
        List<String> result = new ArrayList<>();
        if (delimiter.isEmpty()) {
            for (int i = 0; i < str.length(); i++) {
                result.add(deleteAny(str.substring(i, i + 1), charsToDelete));
            }
        } else {
            int i2 = 0;
            while (true) {
                pos = i2;
                int delPos = str.indexOf(delimiter, pos);
                if (delPos == -1) {
                    break;
                }
                result.add(deleteAny(str.substring(pos, delPos), charsToDelete));
                i2 = delPos + delimiter.length();
            }
            if (str.length() > 0 && pos <= str.length()) {
                result.add(deleteAny(str.substring(pos), charsToDelete));
            }
        }
        return toStringArray(result);
    }

    public static String[] commaDelimitedListToStringArray(@Nullable String str) {
        return delimitedListToStringArray(str, ",");
    }

    public static Set<String> commaDelimitedListToSet(@Nullable String str) {
        String[] tokens = commaDelimitedListToStringArray(str);
        return new LinkedHashSet(Arrays.asList(tokens));
    }

    public static String collectionToDelimitedString(@Nullable Collection<?> coll, String delim, String prefix, String suffix) {
        if (CollectionUtils.isEmpty(coll)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Iterator<?> it = coll.iterator();
        while (it.hasNext()) {
            sb.append(prefix).append(it.next()).append(suffix);
            if (it.hasNext()) {
                sb.append(delim);
            }
        }
        return sb.toString();
    }

    public static String collectionToDelimitedString(@Nullable Collection<?> coll, String delim) {
        return collectionToDelimitedString(coll, delim, "", "");
    }

    public static String collectionToCommaDelimitedString(@Nullable Collection<?> coll) {
        return collectionToDelimitedString(coll, ",");
    }

    public static String arrayToDelimitedString(@Nullable Object[] arr, String delim) {
        if (ObjectUtils.isEmpty(arr)) {
            return "";
        }
        if (arr.length == 1) {
            return ObjectUtils.nullSafeToString(arr[0]);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) {
                sb.append(delim);
            }
            sb.append(arr[i]);
        }
        return sb.toString();
    }

    public static String arrayToCommaDelimitedString(@Nullable Object[] arr) {
        return arrayToDelimitedString(arr, ",");
    }
}