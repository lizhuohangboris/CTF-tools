package com.fasterxml.jackson.core.io;

import java.math.BigDecimal;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-core-2.9.7.jar:com/fasterxml/jackson/core/io/NumberInput.class */
public final class NumberInput {
    public static final String NASTY_SMALL_DOUBLE = "2.2250738585072012e-308";
    static final long L_BILLION = 1000000000;
    static final String MIN_LONG_STR_NO_SIGN = String.valueOf(Long.MIN_VALUE).substring(1);
    static final String MAX_LONG_STR = String.valueOf(Long.MAX_VALUE);

    public static int parseInt(char[] ch2, int off, int len) {
        int num = ch2[off] - '0';
        if (len > 4) {
            int off2 = off + 1;
            int off3 = off2 + 1;
            int off4 = off3 + 1;
            off = off4 + 1;
            num = (((((((num * 10) + (ch2[off2] - '0')) * 10) + (ch2[off3] - '0')) * 10) + (ch2[off4] - '0')) * 10) + (ch2[off] - '0');
            len -= 4;
            if (len > 4) {
                int off5 = off + 1;
                int off6 = off5 + 1;
                int off7 = off6 + 1;
                return (((((((num * 10) + (ch2[off5] - '0')) * 10) + (ch2[off6] - '0')) * 10) + (ch2[off7] - '0')) * 10) + (ch2[off7 + 1] - '0');
            }
        }
        if (len > 1) {
            int off8 = off + 1;
            num = (num * 10) + (ch2[off8] - '0');
            if (len > 2) {
                int off9 = off8 + 1;
                num = (num * 10) + (ch2[off9] - '0');
                if (len > 3) {
                    num = (num * 10) + (ch2[off9 + 1] - '0');
                }
            }
        }
        return num;
    }

    public static int parseInt(String s) {
        char c = s.charAt(0);
        int len = s.length();
        boolean neg = c == '-';
        int offset = 1;
        if (neg) {
            if (len != 1 && len <= 10) {
                offset = 1 + 1;
                c = s.charAt(1);
            } else {
                return Integer.parseInt(s);
            }
        } else if (len > 9) {
            return Integer.parseInt(s);
        }
        if (c > '9' || c < '0') {
            return Integer.parseInt(s);
        }
        int num = c - '0';
        if (offset < len) {
            int i = offset;
            int offset2 = offset + 1;
            char c2 = s.charAt(i);
            if (c2 > '9' || c2 < '0') {
                return Integer.parseInt(s);
            }
            num = (num * 10) + (c2 - '0');
            if (offset2 < len) {
                int offset3 = offset2 + 1;
                char c3 = s.charAt(offset2);
                if (c3 > '9' || c3 < '0') {
                    return Integer.parseInt(s);
                }
                num = (num * 10) + (c3 - '0');
                if (offset3 < len) {
                    do {
                        int i2 = offset3;
                        offset3++;
                        char c4 = s.charAt(i2);
                        if (c4 > '9' || c4 < '0') {
                            return Integer.parseInt(s);
                        }
                        num = (num * 10) + (c4 - '0');
                    } while (offset3 < len);
                }
            }
        }
        return neg ? -num : num;
    }

    public static long parseLong(char[] ch2, int off, int len) {
        int len1 = len - 9;
        long val = parseInt(ch2, off, len1) * L_BILLION;
        return val + parseInt(ch2, off + len1, 9);
    }

    public static long parseLong(String s) {
        int length = s.length();
        if (length <= 9) {
            return parseInt(s);
        }
        return Long.parseLong(s);
    }

    public static boolean inLongRange(char[] ch2, int off, int len, boolean negative) {
        String cmpStr = negative ? MIN_LONG_STR_NO_SIGN : MAX_LONG_STR;
        int cmpLen = cmpStr.length();
        if (len < cmpLen) {
            return true;
        }
        if (len > cmpLen) {
            return false;
        }
        for (int i = 0; i < cmpLen; i++) {
            int diff = ch2[off + i] - cmpStr.charAt(i);
            if (diff != 0) {
                return diff < 0;
            }
        }
        return true;
    }

    public static boolean inLongRange(String s, boolean negative) {
        String cmp = negative ? MIN_LONG_STR_NO_SIGN : MAX_LONG_STR;
        int cmpLen = cmp.length();
        int alen = s.length();
        if (alen < cmpLen) {
            return true;
        }
        if (alen > cmpLen) {
            return false;
        }
        for (int i = 0; i < cmpLen; i++) {
            int diff = s.charAt(i) - cmp.charAt(i);
            if (diff != 0) {
                return diff < 0;
            }
        }
        return true;
    }

    public static int parseAsInt(String s, int def) {
        if (s == null) {
            return def;
        }
        String s2 = s.trim();
        int len = s2.length();
        if (len == 0) {
            return def;
        }
        int i = 0;
        if (0 < len) {
            char c = s2.charAt(0);
            if (c == '+') {
                s2 = s2.substring(1);
                len = s2.length();
            } else if (c == '-') {
                i = 0 + 1;
            }
        }
        while (i < len) {
            char c2 = s2.charAt(i);
            if (c2 <= '9' && c2 >= '0') {
                i++;
            } else {
                try {
                    return (int) parseDouble(s2);
                } catch (NumberFormatException e) {
                    return def;
                }
            }
        }
        try {
            return Integer.parseInt(s2);
        } catch (NumberFormatException e2) {
            return def;
        }
    }

    public static long parseAsLong(String s, long def) {
        if (s == null) {
            return def;
        }
        String s2 = s.trim();
        int len = s2.length();
        if (len == 0) {
            return def;
        }
        int i = 0;
        if (0 < len) {
            char c = s2.charAt(0);
            if (c == '+') {
                s2 = s2.substring(1);
                len = s2.length();
            } else if (c == '-') {
                i = 0 + 1;
            }
        }
        while (i < len) {
            char c2 = s2.charAt(i);
            if (c2 <= '9' && c2 >= '0') {
                i++;
            } else {
                try {
                    return (long) parseDouble(s2);
                } catch (NumberFormatException e) {
                    return def;
                }
            }
        }
        try {
            return Long.parseLong(s2);
        } catch (NumberFormatException e2) {
            return def;
        }
    }

    public static double parseAsDouble(String s, double def) {
        if (s == null) {
            return def;
        }
        String s2 = s.trim();
        int len = s2.length();
        if (len == 0) {
            return def;
        }
        try {
            return parseDouble(s2);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public static double parseDouble(String s) throws NumberFormatException {
        if (NASTY_SMALL_DOUBLE.equals(s)) {
            return Double.MIN_VALUE;
        }
        return Double.parseDouble(s);
    }

    public static BigDecimal parseBigDecimal(String s) throws NumberFormatException {
        try {
            return new BigDecimal(s);
        } catch (NumberFormatException e) {
            throw _badBD(s);
        }
    }

    public static BigDecimal parseBigDecimal(char[] b) throws NumberFormatException {
        return parseBigDecimal(b, 0, b.length);
    }

    public static BigDecimal parseBigDecimal(char[] b, int off, int len) throws NumberFormatException {
        try {
            return new BigDecimal(b, off, len);
        } catch (NumberFormatException e) {
            throw _badBD(new String(b, off, len));
        }
    }

    private static NumberFormatException _badBD(String s) {
        return new NumberFormatException("Value \"" + s + "\" can not be represented as BigDecimal");
    }
}