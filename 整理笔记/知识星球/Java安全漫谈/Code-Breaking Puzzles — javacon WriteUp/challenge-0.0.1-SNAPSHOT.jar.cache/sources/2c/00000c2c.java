package org.apache.tomcat.util.buf;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;
import org.springframework.util.ResourceUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/buf/UriUtil.class */
public final class UriUtil {
    private static final char[] HEX = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static final Pattern PATTERN_EXCLAMATION_MARK = Pattern.compile(ResourceUtils.JAR_URL_SEPARATOR);
    private static final Pattern PATTERN_CARET = Pattern.compile("\\^/");
    private static final Pattern PATTERN_ASTERISK = Pattern.compile("\\*/");
    private static final Pattern PATTERN_CUSTOM;
    private static final String REPLACE_CUSTOM;
    private static final String WAR_SEPARATOR;

    static {
        String custom = System.getProperty("org.apache.tomcat.util.buf.UriUtil.WAR_SEPARATOR");
        if (custom == null) {
            WAR_SEPARATOR = ResourceUtils.WAR_URL_SEPARATOR;
            PATTERN_CUSTOM = null;
            REPLACE_CUSTOM = null;
            return;
        }
        WAR_SEPARATOR = custom + "/";
        PATTERN_CUSTOM = Pattern.compile(Pattern.quote(WAR_SEPARATOR));
        StringBuffer sb = new StringBuffer(custom.length() * 3);
        byte[] ba = custom.getBytes();
        for (byte toEncode : ba) {
            sb.append('%');
            int low = toEncode & 15;
            int high = (toEncode & 240) >> 4;
            sb.append(HEX[high]);
            sb.append(HEX[low]);
        }
        REPLACE_CUSTOM = sb.toString();
    }

    private UriUtil() {
    }

    private static boolean isSchemeChar(char c) {
        return Character.isLetterOrDigit(c) || c == '+' || c == '-' || c == '.';
    }

    public static boolean hasScheme(CharSequence uri) {
        int len = uri.length();
        int i = 0;
        while (i < len) {
            char c = uri.charAt(i);
            if (c == ':') {
                return i > 0;
            } else if (isSchemeChar(c)) {
                i++;
            } else {
                return false;
            }
        }
        return false;
    }

    public static URL buildJarUrl(File jarFile) throws MalformedURLException {
        return buildJarUrl(jarFile, (String) null);
    }

    public static URL buildJarUrl(File jarFile, String entryPath) throws MalformedURLException {
        return buildJarUrl(jarFile.toURI().toString(), entryPath);
    }

    public static URL buildJarUrl(String fileUrlString) throws MalformedURLException {
        return buildJarUrl(fileUrlString, (String) null);
    }

    public static URL buildJarUrl(String fileUrlString, String entryPath) throws MalformedURLException {
        String safeString = makeSafeForJarUrl(fileUrlString);
        StringBuilder sb = new StringBuilder();
        sb.append(safeString);
        sb.append(ResourceUtils.JAR_URL_SEPARATOR);
        if (entryPath != null) {
            sb.append(makeSafeForJarUrl(entryPath));
        }
        return new URL(ResourceUtils.URL_PROTOCOL_JAR, null, -1, sb.toString());
    }

    public static URL buildJarSafeUrl(File file) throws MalformedURLException {
        String safe = makeSafeForJarUrl(file.toURI().toString());
        return new URL(safe);
    }

    private static String makeSafeForJarUrl(String input) {
        String tmp = PATTERN_EXCLAMATION_MARK.matcher(input).replaceAll("%21/");
        String tmp2 = PATTERN_ASTERISK.matcher(PATTERN_CARET.matcher(tmp).replaceAll("%5e/")).replaceAll("%2a/");
        if (PATTERN_CUSTOM != null) {
            tmp2 = PATTERN_CUSTOM.matcher(tmp2).replaceAll(REPLACE_CUSTOM);
        }
        return tmp2;
    }

    public static URL warToJar(URL warUrl) throws MalformedURLException {
        String file = warUrl.getFile();
        if (file.contains(ResourceUtils.WAR_URL_SEPARATOR)) {
            file = file.replaceFirst("\\*/", ResourceUtils.JAR_URL_SEPARATOR);
        } else if (file.contains("^/")) {
            file = file.replaceFirst("\\^/", ResourceUtils.JAR_URL_SEPARATOR);
        } else if (PATTERN_CUSTOM != null) {
            file = file.replaceFirst(PATTERN_CUSTOM.pattern(), ResourceUtils.JAR_URL_SEPARATOR);
        }
        return new URL(ResourceUtils.URL_PROTOCOL_JAR, warUrl.getHost(), warUrl.getPort(), file);
    }

    public static String getWarSeparator() {
        return WAR_SEPARATOR;
    }
}