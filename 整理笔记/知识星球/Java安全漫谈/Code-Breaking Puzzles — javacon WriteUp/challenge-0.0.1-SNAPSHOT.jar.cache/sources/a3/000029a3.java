package org.thymeleaf.standard.util;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/util/StandardConditionalCommentUtils.class */
public final class StandardConditionalCommentUtils {
    public static ConditionalCommentParsingResult parseConditionalComment(CharSequence text) {
        int contentLen;
        int len = text.length();
        int i = 4;
        while (i < len && Character.isWhitespace(text.charAt(i))) {
            i++;
        }
        if (i < len) {
            int i2 = i;
            int i3 = i + 1;
            if (text.charAt(i2) != '[') {
                return null;
            }
            while (i3 < len && text.charAt(i3) != ']') {
                i3++;
            }
            if (i3 < len) {
                int startExpressionLen = i3 - i3;
                while (true) {
                    i3++;
                    if (i3 >= len || !Character.isWhitespace(text.charAt(i3))) {
                        break;
                    }
                }
                if (i3 < len) {
                    int i4 = i3 + 1;
                    if (text.charAt(i3) != '>') {
                        return null;
                    }
                    int i5 = (len - 3) - 1;
                    while (i5 > i4 && Character.isWhitespace(text.charAt(i5))) {
                        i5--;
                    }
                    if (i5 > i4) {
                        int i6 = i5;
                        int i7 = i5 - 1;
                        if (text.charAt(i6) != ']') {
                            return null;
                        }
                        int endExpressionLastPos = i7 + 1;
                        while (i7 > i4 && text.charAt(i7) != '[') {
                            i7--;
                        }
                        if (i7 <= i4) {
                            return null;
                        }
                        int endExpressionOffset = i7 + 1;
                        int endExpressionLen = endExpressionLastPos - endExpressionOffset;
                        while (true) {
                            i7--;
                            if (i7 < i4 || !Character.isWhitespace(text.charAt(i7))) {
                                break;
                            }
                        }
                        if (i7 <= i4) {
                            return null;
                        }
                        int i8 = i7 - 1;
                        if (text.charAt(i7) != '!' || i8 <= i4) {
                            return null;
                        }
                        int i9 = i8 - 1;
                        if (text.charAt(i8) == '<' && (contentLen = (i9 + 1) - i4) > 0 && startExpressionLen > 0 && endExpressionLen > 0) {
                            return new ConditionalCommentParsingResult(i3, startExpressionLen, i4, contentLen, endExpressionOffset, endExpressionLen);
                        }
                        return null;
                    }
                    return null;
                }
                return null;
            }
            return null;
        }
        return null;
    }

    private StandardConditionalCommentUtils() {
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/util/StandardConditionalCommentUtils$ConditionalCommentParsingResult.class */
    public static final class ConditionalCommentParsingResult {
        private final int startExpressionOffset;
        private final int startExpressionLen;
        private final int contentOffset;
        private final int contentLen;
        private final int endExpressionOffset;
        private final int endExpressionLen;

        public ConditionalCommentParsingResult(int startExpressionOffset, int startExpressionLen, int contentOffset, int contentLen, int endExpressionOffset, int endExpressionLen) {
            this.startExpressionOffset = startExpressionOffset;
            this.startExpressionLen = startExpressionLen;
            this.contentOffset = contentOffset;
            this.contentLen = contentLen;
            this.endExpressionOffset = endExpressionOffset;
            this.endExpressionLen = endExpressionLen;
        }

        public int getStartExpressionOffset() {
            return this.startExpressionOffset;
        }

        public int getStartExpressionLen() {
            return this.startExpressionLen;
        }

        public int getContentOffset() {
            return this.contentOffset;
        }

        public int getContentLen() {
            return this.contentLen;
        }

        public int getEndExpressionOffset() {
            return this.endExpressionOffset;
        }

        public int getEndExpressionLen() {
            return this.endExpressionLen;
        }
    }
}