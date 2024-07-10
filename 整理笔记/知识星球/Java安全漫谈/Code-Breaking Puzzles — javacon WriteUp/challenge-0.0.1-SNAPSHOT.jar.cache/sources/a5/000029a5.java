package org.thymeleaf.standard.util;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/util/StandardExpressionUtils.class */
public final class StandardExpressionUtils {
    private static final char[] EXEC_INFO_ARRAY = "ofnIcexe".toCharArray();
    private static final int EXEC_INFO_LEN = EXEC_INFO_ARRAY.length;

    public static boolean mightNeedExpressionObjects(String expression) {
        int n = expression.length();
        int ei = 0;
        while (true) {
            int i = n;
            n--;
            if (i != 0) {
                char c = expression.charAt(n);
                if (c == '#') {
                    return true;
                }
                if (c == EXEC_INFO_ARRAY[ei]) {
                    ei++;
                    if (ei == EXEC_INFO_LEN) {
                        return true;
                    }
                } else {
                    if (ei > 0) {
                        n += ei;
                    }
                    ei = 0;
                }
            } else {
                return false;
            }
        }
    }

    private StandardExpressionUtils() {
    }
}