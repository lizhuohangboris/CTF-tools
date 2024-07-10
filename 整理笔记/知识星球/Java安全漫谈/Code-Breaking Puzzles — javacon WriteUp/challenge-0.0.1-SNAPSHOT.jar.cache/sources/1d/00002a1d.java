package org.thymeleaf.util;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/TextUtils.class */
public final class TextUtils {
    public static boolean equals(boolean caseSensitive, CharSequence text1, CharSequence text2) {
        if (text1 == null) {
            throw new IllegalArgumentException("First text being compared cannot be null");
        }
        if (text2 == null) {
            throw new IllegalArgumentException("Second text being compared cannot be null");
        }
        if (text1 == text2) {
            return true;
        }
        if ((text1 instanceof String) && (text2 instanceof String)) {
            return caseSensitive ? text1.equals(text2) : ((String) text1).equalsIgnoreCase((String) text2);
        }
        return equals(caseSensitive, text1, 0, text1.length(), text2, 0, text2.length());
    }

    public static boolean equals(boolean caseSensitive, CharSequence text1, char[] text2) {
        return equals(caseSensitive, text1, 0, text1.length(), text2, 0, text2.length);
    }

    public static boolean equals(boolean caseSensitive, char[] text1, char[] text2) {
        return text1 == text2 || equals(caseSensitive, text1, 0, text1.length, text2, 0, text2.length);
    }

    public static boolean equals(boolean caseSensitive, char[] text1, int text1Offset, int text1Len, char[] text2, int text2Offset, int text2Len) {
        if (text1 == null) {
            throw new IllegalArgumentException("First text buffer being compared cannot be null");
        }
        if (text2 == null) {
            throw new IllegalArgumentException("Second text buffer being compared cannot be null");
        }
        if (text1Len != text2Len) {
            return false;
        }
        if (text1 == text2 && text1Offset == text2Offset && text1Len == text2Len) {
            return true;
        }
        int n = text1Len;
        int i = 0;
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                char c1 = text1[text1Offset + i];
                char c2 = text2[text2Offset + i];
                if (c1 != c2) {
                    if (caseSensitive) {
                        return false;
                    }
                    char c12 = Character.toUpperCase(c1);
                    char c22 = Character.toUpperCase(c2);
                    if (c12 != c22 && Character.toLowerCase(c12) != Character.toLowerCase(c22)) {
                        return false;
                    }
                }
                i++;
            } else {
                return true;
            }
        }
    }

    public static boolean equals(boolean caseSensitive, CharSequence text1, int text1Offset, int text1Len, char[] text2, int text2Offset, int text2Len) {
        if (text1 == null) {
            throw new IllegalArgumentException("First text being compared cannot be null");
        }
        if (text2 == null) {
            throw new IllegalArgumentException("Second text buffer being compared cannot be null");
        }
        if (text1Len != text2Len) {
            return false;
        }
        int n = text1Len;
        int i = 0;
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                char c1 = text1.charAt(text1Offset + i);
                char c2 = text2[text2Offset + i];
                if (c1 != c2) {
                    if (caseSensitive) {
                        return false;
                    }
                    char c12 = Character.toUpperCase(c1);
                    char c22 = Character.toUpperCase(c2);
                    if (c12 != c22 && Character.toLowerCase(c12) != Character.toLowerCase(c22)) {
                        return false;
                    }
                }
                i++;
            } else {
                return true;
            }
        }
    }

    public static boolean equals(boolean caseSensitive, CharSequence text1, int text1Offset, int text1Len, CharSequence text2, int text2Offset, int text2Len) {
        if (text1 == null) {
            throw new IllegalArgumentException("First text being compared cannot be null");
        }
        if (text2 == null) {
            throw new IllegalArgumentException("Second text being compared cannot be null");
        }
        if (text1Len != text2Len) {
            return false;
        }
        if (text1 == text2 && text1Offset == text2Offset && text1Len == text2Len) {
            return true;
        }
        int n = text1Len;
        int i = 0;
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                char c1 = text1.charAt(text1Offset + i);
                char c2 = text2.charAt(text2Offset + i);
                if (c1 != c2) {
                    if (caseSensitive) {
                        return false;
                    }
                    char c12 = Character.toUpperCase(c1);
                    char c22 = Character.toUpperCase(c2);
                    if (c12 != c22 && Character.toLowerCase(c12) != Character.toLowerCase(c22)) {
                        return false;
                    }
                }
                i++;
            } else {
                return true;
            }
        }
    }

    public static boolean startsWith(boolean caseSensitive, CharSequence text, CharSequence prefix) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        if (prefix == null) {
            throw new IllegalArgumentException("Prefix cannot be null");
        }
        if ((text instanceof String) && (prefix instanceof String)) {
            return caseSensitive ? ((String) text).startsWith((String) prefix) : startsWith(caseSensitive, text, 0, text.length(), prefix, 0, prefix.length());
        }
        return startsWith(caseSensitive, text, 0, text.length(), prefix, 0, prefix.length());
    }

    public static boolean startsWith(boolean caseSensitive, CharSequence text, char[] prefix) {
        return startsWith(caseSensitive, text, 0, text.length(), prefix, 0, prefix.length);
    }

    public static boolean startsWith(boolean caseSensitive, char[] text, char[] prefix) {
        return startsWith(caseSensitive, text, 0, text.length, prefix, 0, prefix.length);
    }

    public static boolean startsWith(boolean caseSensitive, char[] text, int textOffset, int textLen, char[] prefix, int prefixOffset, int prefixLen) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        if (prefix == null) {
            throw new IllegalArgumentException("Prefix cannot be null");
        }
        if (textLen < prefixLen) {
            return false;
        }
        int n = prefixLen;
        int i = 0;
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                char c1 = text[textOffset + i];
                char c2 = prefix[prefixOffset + i];
                if (c1 != c2) {
                    if (caseSensitive) {
                        return false;
                    }
                    char c12 = Character.toUpperCase(c1);
                    char c22 = Character.toUpperCase(c2);
                    if (c12 != c22 && Character.toLowerCase(c12) != Character.toLowerCase(c22)) {
                        return false;
                    }
                }
                i++;
            } else {
                return true;
            }
        }
    }

    public static boolean startsWith(boolean caseSensitive, CharSequence text, int textOffset, int textLen, char[] prefix, int prefixOffset, int prefixLen) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        if (prefix == null) {
            throw new IllegalArgumentException("Prefix cannot be null");
        }
        if (textLen < prefixLen) {
            return false;
        }
        int n = prefixLen;
        int i = 0;
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                char c1 = text.charAt(textOffset + i);
                char c2 = prefix[prefixOffset + i];
                if (c1 != c2) {
                    if (caseSensitive) {
                        return false;
                    }
                    char c12 = Character.toUpperCase(c1);
                    char c22 = Character.toUpperCase(c2);
                    if (c12 != c22 && Character.toLowerCase(c12) != Character.toLowerCase(c22)) {
                        return false;
                    }
                }
                i++;
            } else {
                return true;
            }
        }
    }

    public static boolean startsWith(boolean caseSensitive, char[] text, int textOffset, int textLen, CharSequence prefix, int prefixOffset, int prefixLen) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        if (prefix == null) {
            throw new IllegalArgumentException("Prefix cannot be null");
        }
        if (textLen < prefixLen) {
            return false;
        }
        int n = prefixLen;
        int i = 0;
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                char c1 = text[textOffset + i];
                char c2 = prefix.charAt(prefixOffset + i);
                if (c1 != c2) {
                    if (caseSensitive) {
                        return false;
                    }
                    char c12 = Character.toUpperCase(c1);
                    char c22 = Character.toUpperCase(c2);
                    if (c12 != c22 && Character.toLowerCase(c12) != Character.toLowerCase(c22)) {
                        return false;
                    }
                }
                i++;
            } else {
                return true;
            }
        }
    }

    public static boolean startsWith(boolean caseSensitive, CharSequence text, int textOffset, int textLen, CharSequence prefix, int prefixOffset, int prefixLen) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        if (prefix == null) {
            throw new IllegalArgumentException("Prefix cannot be null");
        }
        if (textLen < prefixLen) {
            return false;
        }
        int n = prefixLen;
        int i = 0;
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                char c1 = text.charAt(textOffset + i);
                char c2 = prefix.charAt(prefixOffset + i);
                if (c1 != c2) {
                    if (caseSensitive) {
                        return false;
                    }
                    char c12 = Character.toUpperCase(c1);
                    char c22 = Character.toUpperCase(c2);
                    if (c12 != c22 && Character.toLowerCase(c12) != Character.toLowerCase(c22)) {
                        return false;
                    }
                }
                i++;
            } else {
                return true;
            }
        }
    }

    public static boolean endsWith(boolean caseSensitive, CharSequence text, CharSequence suffix) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        if (suffix == null) {
            throw new IllegalArgumentException("Suffix cannot be null");
        }
        if ((text instanceof String) && (suffix instanceof String)) {
            return caseSensitive ? ((String) text).endsWith((String) suffix) : endsWith(caseSensitive, text, 0, text.length(), suffix, 0, suffix.length());
        }
        return endsWith(caseSensitive, text, 0, text.length(), suffix, 0, suffix.length());
    }

    public static boolean endsWith(boolean caseSensitive, CharSequence text, char[] suffix) {
        return endsWith(caseSensitive, text, 0, text.length(), suffix, 0, suffix.length);
    }

    public static boolean endsWith(boolean caseSensitive, char[] text, char[] suffix) {
        return endsWith(caseSensitive, text, 0, text.length, suffix, 0, suffix.length);
    }

    public static boolean endsWith(boolean caseSensitive, char[] text, int textOffset, int textLen, char[] suffix, int suffixOffset, int suffixLen) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        if (suffix == null) {
            throw new IllegalArgumentException("Suffix cannot be null");
        }
        if (textLen < suffixLen) {
            return false;
        }
        int textReverseOffset = (textOffset + textLen) - 1;
        int suffixReverseOffset = (suffixOffset + suffixLen) - 1;
        int n = suffixLen;
        int i = 0;
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                char c1 = text[textReverseOffset - i];
                char c2 = suffix[suffixReverseOffset - i];
                if (c1 != c2) {
                    if (caseSensitive) {
                        return false;
                    }
                    char c12 = Character.toUpperCase(c1);
                    char c22 = Character.toUpperCase(c2);
                    if (c12 != c22 && Character.toLowerCase(c12) != Character.toLowerCase(c22)) {
                        return false;
                    }
                }
                i++;
            } else {
                return true;
            }
        }
    }

    public static boolean endsWith(boolean caseSensitive, CharSequence text, int textOffset, int textLen, char[] suffix, int suffixOffset, int suffixLen) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        if (suffix == null) {
            throw new IllegalArgumentException("Suffix cannot be null");
        }
        if (textLen < suffixLen) {
            return false;
        }
        int textReverseOffset = (textOffset + textLen) - 1;
        int suffixReverseOffset = (suffixOffset + suffixLen) - 1;
        int n = suffixLen;
        int i = 0;
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                char c1 = text.charAt(textReverseOffset - i);
                char c2 = suffix[suffixReverseOffset - i];
                if (c1 != c2) {
                    if (caseSensitive) {
                        return false;
                    }
                    char c12 = Character.toUpperCase(c1);
                    char c22 = Character.toUpperCase(c2);
                    if (c12 != c22 && Character.toLowerCase(c12) != Character.toLowerCase(c22)) {
                        return false;
                    }
                }
                i++;
            } else {
                return true;
            }
        }
    }

    public static boolean endsWith(boolean caseSensitive, char[] text, int textOffset, int textLen, CharSequence suffix, int suffixOffset, int suffixLen) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        if (suffix == null) {
            throw new IllegalArgumentException("Suffix cannot be null");
        }
        if (textLen < suffixLen) {
            return false;
        }
        int textReverseOffset = (textOffset + textLen) - 1;
        int suffixReverseOffset = (suffixOffset + suffixLen) - 1;
        int n = suffixLen;
        int i = 0;
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                char c1 = text[textReverseOffset - i];
                char c2 = suffix.charAt(suffixReverseOffset - i);
                if (c1 != c2) {
                    if (caseSensitive) {
                        return false;
                    }
                    char c12 = Character.toUpperCase(c1);
                    char c22 = Character.toUpperCase(c2);
                    if (c12 != c22 && Character.toLowerCase(c12) != Character.toLowerCase(c22)) {
                        return false;
                    }
                }
                i++;
            } else {
                return true;
            }
        }
    }

    public static boolean endsWith(boolean caseSensitive, CharSequence text, int textOffset, int textLen, CharSequence suffix, int suffixOffset, int suffixLen) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        if (suffix == null) {
            throw new IllegalArgumentException("Suffix cannot be null");
        }
        if (textLen < suffixLen) {
            return false;
        }
        int textReverseOffset = (textOffset + textLen) - 1;
        int suffixReverseOffset = (suffixOffset + suffixLen) - 1;
        int n = suffixLen;
        int i = 0;
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                char c1 = text.charAt(textReverseOffset - i);
                char c2 = suffix.charAt(suffixReverseOffset - i);
                if (c1 != c2) {
                    if (caseSensitive) {
                        return false;
                    }
                    char c12 = Character.toUpperCase(c1);
                    char c22 = Character.toUpperCase(c2);
                    if (c12 != c22 && Character.toLowerCase(c12) != Character.toLowerCase(c22)) {
                        return false;
                    }
                }
                i++;
            } else {
                return true;
            }
        }
    }

    public static boolean contains(boolean caseSensitive, CharSequence text, CharSequence fragment) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        if (fragment == null) {
            throw new IllegalArgumentException("Fragment cannot be null");
        }
        if ((text instanceof String) && (fragment instanceof String)) {
            return caseSensitive ? ((String) text).contains(fragment) : contains(caseSensitive, text, 0, text.length(), fragment, 0, fragment.length());
        }
        return contains(caseSensitive, text, 0, text.length(), fragment, 0, fragment.length());
    }

    public static boolean contains(boolean caseSensitive, CharSequence text, char[] fragment) {
        return contains(caseSensitive, text, 0, text.length(), fragment, 0, fragment.length);
    }

    public static boolean contains(boolean caseSensitive, char[] text, char[] fragment) {
        return contains(caseSensitive, text, 0, text.length, fragment, 0, fragment.length);
    }

    public static boolean contains(boolean caseSensitive, char[] text, int textOffset, int textLen, char[] fragment, int fragmentOffset, int fragmentLen) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        if (fragment == null) {
            throw new IllegalArgumentException("Fragment cannot be null");
        }
        if (textLen < fragmentLen) {
            return false;
        }
        if (fragmentLen == 0) {
            return true;
        }
        int i = 0;
        int j = 0;
        while (i < textLen) {
            char c1 = text[textOffset + i];
            char c2 = fragment[fragmentOffset + j];
            if (c1 == c2) {
                j++;
                if (j == fragmentLen) {
                    return true;
                }
            } else {
                if (!caseSensitive) {
                    char c12 = Character.toUpperCase(c1);
                    char c22 = Character.toUpperCase(c2);
                    if (c12 == c22) {
                        j++;
                        if (j == fragmentLen) {
                            return true;
                        }
                    } else if (Character.toLowerCase(c12) == Character.toLowerCase(c22)) {
                        j++;
                        if (j == fragmentLen) {
                            return true;
                        }
                    }
                }
                if (j > 0) {
                    i -= j;
                }
                j = 0;
            }
            i++;
        }
        return false;
    }

    public static boolean contains(boolean caseSensitive, CharSequence text, int textOffset, int textLen, char[] fragment, int fragmentOffset, int fragmentLen) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        if (fragment == null) {
            throw new IllegalArgumentException("Fragment cannot be null");
        }
        if (textLen < fragmentLen) {
            return false;
        }
        if (fragmentLen == 0) {
            return true;
        }
        int i = 0;
        int j = 0;
        while (i < textLen) {
            char c1 = text.charAt(textOffset + i);
            char c2 = fragment[fragmentOffset + j];
            if (c1 == c2) {
                j++;
                if (j == fragmentLen) {
                    return true;
                }
            } else {
                if (!caseSensitive) {
                    char c12 = Character.toUpperCase(c1);
                    char c22 = Character.toUpperCase(c2);
                    if (c12 == c22) {
                        j++;
                        if (j == fragmentLen) {
                            return true;
                        }
                    } else if (Character.toLowerCase(c12) == Character.toLowerCase(c22)) {
                        j++;
                        if (j == fragmentLen) {
                            return true;
                        }
                    }
                }
                if (j > 0) {
                    i -= j;
                }
                j = 0;
            }
            i++;
        }
        return false;
    }

    public static boolean contains(boolean caseSensitive, char[] text, int textOffset, int textLen, CharSequence fragment, int fragmentOffset, int fragmentLen) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        if (fragment == null) {
            throw new IllegalArgumentException("Fragment cannot be null");
        }
        if (textLen < fragmentLen) {
            return false;
        }
        if (fragmentLen == 0) {
            return true;
        }
        int i = 0;
        int j = 0;
        while (i < textLen) {
            char c1 = text[textOffset + i];
            char c2 = fragment.charAt(fragmentOffset + j);
            if (c1 == c2) {
                j++;
                if (j == fragmentLen) {
                    return true;
                }
            } else {
                if (!caseSensitive) {
                    char c12 = Character.toUpperCase(c1);
                    char c22 = Character.toUpperCase(c2);
                    if (c12 == c22) {
                        j++;
                        if (j == fragmentLen) {
                            return true;
                        }
                    } else if (Character.toLowerCase(c12) == Character.toLowerCase(c22)) {
                        j++;
                        if (j == fragmentLen) {
                            return true;
                        }
                    }
                }
                if (j > 0) {
                    i -= j;
                }
                j = 0;
            }
            i++;
        }
        return false;
    }

    public static boolean contains(boolean caseSensitive, CharSequence text, int textOffset, int textLen, CharSequence fragment, int fragmentOffset, int fragmentLen) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        if (fragment == null) {
            throw new IllegalArgumentException("Fragment cannot be null");
        }
        if (textLen < fragmentLen) {
            return false;
        }
        if (fragmentLen == 0) {
            return true;
        }
        int i = 0;
        int j = 0;
        while (i < textLen) {
            char c1 = text.charAt(textOffset + i);
            char c2 = fragment.charAt(fragmentOffset + j);
            if (c1 == c2) {
                j++;
                if (j == fragmentLen) {
                    return true;
                }
            } else {
                if (!caseSensitive) {
                    char c12 = Character.toUpperCase(c1);
                    char c22 = Character.toUpperCase(c2);
                    if (c12 == c22) {
                        j++;
                        if (j == fragmentLen) {
                            return true;
                        }
                    } else if (Character.toLowerCase(c12) == Character.toLowerCase(c22)) {
                        j++;
                        if (j == fragmentLen) {
                            return true;
                        }
                    }
                }
                if (j > 0) {
                    i -= j;
                }
                j = 0;
            }
            i++;
        }
        return false;
    }

    public static int compareTo(boolean caseSensitive, CharSequence text1, CharSequence text2) {
        if (text1 == null) {
            throw new IllegalArgumentException("First text being compared cannot be null");
        }
        if (text2 == null) {
            throw new IllegalArgumentException("Second text being compared cannot be null");
        }
        if ((text1 instanceof String) && (text2 instanceof String)) {
            return caseSensitive ? ((String) text1).compareTo((String) text2) : ((String) text1).compareToIgnoreCase((String) text2);
        }
        return compareTo(caseSensitive, text1, 0, text1.length(), text2, 0, text2.length());
    }

    public static int compareTo(boolean caseSensitive, CharSequence text1, char[] text2) {
        return compareTo(caseSensitive, text1, 0, text1.length(), text2, 0, text2.length);
    }

    public static int compareTo(boolean caseSensitive, char[] text1, char[] text2) {
        return compareTo(caseSensitive, text1, 0, text1.length, text2, 0, text2.length);
    }

    public static int compareTo(boolean caseSensitive, char[] text1, int text1Offset, int text1Len, char[] text2, int text2Offset, int text2Len) {
        char c1;
        char c2;
        if (text1 == null) {
            throw new IllegalArgumentException("First text buffer being compared cannot be null");
        }
        if (text2 == null) {
            throw new IllegalArgumentException("Second text buffer being compared cannot be null");
        }
        if (text1 == text2 && text1Offset == text2Offset && text1Len == text2Len) {
            return 0;
        }
        int n = Math.min(text1Len, text2Len);
        int i = 0;
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                char c12 = text1[text1Offset + i];
                char c22 = text2[text2Offset + i];
                if (c12 != c22) {
                    if (caseSensitive) {
                        return c12 - c22;
                    }
                    char c13 = Character.toUpperCase(c12);
                    char c23 = Character.toUpperCase(c22);
                    if (c13 != c23 && (c1 = Character.toLowerCase(c13)) != (c2 = Character.toLowerCase(c23))) {
                        return c1 - c2;
                    }
                }
                i++;
            } else {
                return text1Len - text2Len;
            }
        }
    }

    public static int compareTo(boolean caseSensitive, CharSequence text1, int text1Offset, int text1Len, char[] text2, int text2Offset, int text2Len) {
        char c1;
        char c2;
        if (text1 == null) {
            throw new IllegalArgumentException("First text being compared cannot be null");
        }
        if (text2 == null) {
            throw new IllegalArgumentException("Second text buffer being compared cannot be null");
        }
        int n = Math.min(text1Len, text2Len);
        int i = 0;
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                char c12 = text1.charAt(text1Offset + i);
                char c22 = text2[text2Offset + i];
                if (c12 != c22) {
                    if (caseSensitive) {
                        return c12 - c22;
                    }
                    char c13 = Character.toUpperCase(c12);
                    char c23 = Character.toUpperCase(c22);
                    if (c13 != c23 && (c1 = Character.toLowerCase(c13)) != (c2 = Character.toLowerCase(c23))) {
                        return c1 - c2;
                    }
                }
                i++;
            } else {
                return text1Len - text2Len;
            }
        }
    }

    public static int compareTo(boolean caseSensitive, CharSequence text1, int text1Offset, int text1Len, CharSequence text2, int text2Offset, int text2Len) {
        char c1;
        char c2;
        if (text1 == null) {
            throw new IllegalArgumentException("First text being compared cannot be null");
        }
        if (text2 == null) {
            throw new IllegalArgumentException("Second text being compared cannot be null");
        }
        if (text1 == text2 && text1Offset == text2Offset && text1Len == text2Len) {
            return 0;
        }
        int n = Math.min(text1Len, text2Len);
        int i = 0;
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                char c12 = text1.charAt(text1Offset + i);
                char c22 = text2.charAt(text2Offset + i);
                if (c12 != c22) {
                    if (caseSensitive) {
                        return c12 - c22;
                    }
                    char c13 = Character.toUpperCase(c12);
                    char c23 = Character.toUpperCase(c22);
                    if (c13 != c23 && (c1 = Character.toLowerCase(c13)) != (c2 = Character.toLowerCase(c23))) {
                        return c1 - c2;
                    }
                }
                i++;
            } else {
                return text1Len - text2Len;
            }
        }
    }

    public static int binarySearch(boolean caseSensitive, char[][] values, char[] text, int textOffset, int textLen) {
        if (values == null) {
            throw new IllegalArgumentException("Values array cannot be null");
        }
        return binarySearch(caseSensitive, values, 0, values.length, text, textOffset, textLen);
    }

    public static int binarySearch(boolean caseSensitive, char[][] values, CharSequence text, int textOffset, int textLen) {
        if (values == null) {
            throw new IllegalArgumentException("Values array cannot be null");
        }
        return binarySearch(caseSensitive, values, 0, values.length, text, textOffset, textLen);
    }

    public static int binarySearch(boolean caseSensitive, CharSequence[] values, char[] text, int textOffset, int textLen) {
        if (values == null) {
            throw new IllegalArgumentException("Values array cannot be null");
        }
        return binarySearch(caseSensitive, values, 0, values.length, text, textOffset, textLen);
    }

    public static int binarySearch(boolean caseSensitive, CharSequence[] values, CharSequence text, int textOffset, int textLen) {
        if (values == null) {
            throw new IllegalArgumentException("Values array cannot be null");
        }
        return binarySearch(caseSensitive, values, 0, values.length, text, textOffset, textLen);
    }

    public static int binarySearch(boolean caseSensitive, char[][] values, int valuesOffset, int valuesLen, char[] text, int textOffset, int textLen) {
        if (values == null) {
            throw new IllegalArgumentException("Values array cannot be null");
        }
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        int low = valuesOffset;
        int high = (valuesOffset + valuesLen) - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            char[] midVal = values[mid];
            int cmp = compareTo(caseSensitive, midVal, 0, midVal.length, text, textOffset, textLen);
            if (cmp < 0) {
                low = mid + 1;
            } else if (cmp > 0) {
                high = mid - 1;
            } else {
                return mid;
            }
        }
        return -(low + 1);
    }

    public static int binarySearch(boolean caseSensitive, char[][] values, int valuesOffset, int valuesLen, CharSequence text, int textOffset, int textLen) {
        if (values == null) {
            throw new IllegalArgumentException("Values array cannot be null");
        }
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        int low = valuesOffset;
        int high = (valuesOffset + valuesLen) - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            char[] midVal = values[mid];
            int cmp = compareTo(caseSensitive, text, textOffset, textLen, midVal, 0, midVal.length);
            if (cmp > 0) {
                low = mid + 1;
            } else if (cmp < 0) {
                high = mid - 1;
            } else {
                return mid;
            }
        }
        return -(low + 1);
    }

    public static int binarySearch(boolean caseSensitive, CharSequence[] values, int valuesOffset, int valuesLen, char[] text, int textOffset, int textLen) {
        if (values == null) {
            throw new IllegalArgumentException("Values array cannot be null");
        }
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        int low = valuesOffset;
        int high = (valuesOffset + valuesLen) - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            CharSequence midVal = values[mid];
            int cmp = compareTo(caseSensitive, midVal, 0, midVal.length(), text, textOffset, textLen);
            if (cmp < 0) {
                low = mid + 1;
            } else if (cmp > 0) {
                high = mid - 1;
            } else {
                return mid;
            }
        }
        return -(low + 1);
    }

    public static int binarySearch(boolean caseSensitive, CharSequence[] values, int valuesOffset, int valuesLen, CharSequence text, int textOffset, int textLen) {
        if (values == null) {
            throw new IllegalArgumentException("Values array cannot be null");
        }
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        int low = valuesOffset;
        int high = (valuesOffset + valuesLen) - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            CharSequence midVal = values[mid];
            int cmp = compareTo(caseSensitive, text, textOffset, textLen, midVal, 0, midVal.length());
            if (cmp > 0) {
                low = mid + 1;
            } else if (cmp < 0) {
                high = mid - 1;
            } else {
                return mid;
            }
        }
        return -(low + 1);
    }

    public static int hashCode(char[] text, int textOffset, int textLen) {
        int h = 0;
        int off = textOffset;
        for (int i = 0; i < textLen; i++) {
            int i2 = off;
            off++;
            h = (31 * h) + text[i2];
        }
        return h;
    }

    public static int hashCode(CharSequence text) {
        return hashCodePart(0, text);
    }

    public static int hashCode(CharSequence text, int beginIndex, int endIndex) {
        return hashCodePart(0, text, beginIndex, endIndex);
    }

    public static int hashCode(CharSequence text0, CharSequence text1) {
        return hashCodePart(hashCodePart(0, text0), text1);
    }

    public static int hashCode(CharSequence text0, CharSequence text1, CharSequence text2) {
        return hashCodePart(hashCodePart(hashCodePart(0, text0), text1), text2);
    }

    public static int hashCode(CharSequence text0, CharSequence text1, CharSequence text2, CharSequence text3) {
        return hashCodePart(hashCodePart(hashCodePart(hashCodePart(0, text0), text1), text2), text3);
    }

    public static int hashCode(CharSequence text0, CharSequence text1, CharSequence text2, CharSequence text3, CharSequence text4) {
        return hashCodePart(hashCodePart(hashCodePart(hashCodePart(hashCodePart(0, text0), text1), text2), text3), text4);
    }

    private static int hashCodePart(int h, CharSequence text) {
        return hashCodePart(h, text, 0, text.length());
    }

    private static int hashCodePart(int h, CharSequence text, int beginIndex, int endIndex) {
        if (h == 0 && beginIndex == 0 && endIndex == text.length() && (text instanceof String)) {
            return text.hashCode();
        }
        int hh = h;
        for (int i = beginIndex; i < endIndex; i++) {
            hh = (31 * hh) + text.charAt(i);
        }
        return hh;
    }

    private TextUtils() {
    }
}