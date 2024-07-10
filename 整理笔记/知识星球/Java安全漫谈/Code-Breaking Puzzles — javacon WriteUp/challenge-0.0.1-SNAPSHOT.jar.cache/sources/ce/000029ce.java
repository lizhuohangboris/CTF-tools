package org.thymeleaf.templateparser.text;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateparser/text/ParsingLocatorUtil.class */
final class ParsingLocatorUtil {
    public static void countChar(int[] locator, char c) {
        if (c == '\n') {
            locator[0] = locator[0] + 1;
            locator[1] = 1;
            return;
        }
        locator[1] = locator[1] + 1;
    }

    private ParsingLocatorUtil() {
    }
}