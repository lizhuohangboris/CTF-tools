package org.springframework.beans;

import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/PropertyAccessorUtils.class */
public abstract class PropertyAccessorUtils {
    public static String getPropertyName(String propertyPath) {
        int separatorIndex = propertyPath.endsWith("]") ? propertyPath.indexOf(91) : -1;
        return separatorIndex != -1 ? propertyPath.substring(0, separatorIndex) : propertyPath;
    }

    public static boolean isNestedOrIndexedProperty(@Nullable String propertyPath) {
        if (propertyPath == null) {
            return false;
        }
        for (int i = 0; i < propertyPath.length(); i++) {
            char ch2 = propertyPath.charAt(i);
            if (ch2 == '.' || ch2 == '[') {
                return true;
            }
        }
        return false;
    }

    public static int getFirstNestedPropertySeparatorIndex(String propertyPath) {
        return getNestedPropertySeparatorIndex(propertyPath, false);
    }

    public static int getLastNestedPropertySeparatorIndex(String propertyPath) {
        return getNestedPropertySeparatorIndex(propertyPath, true);
    }

    private static int getNestedPropertySeparatorIndex(String propertyPath, boolean last) {
        boolean inKey = false;
        int length = propertyPath.length();
        int i = last ? length - 1 : 0;
        while (true) {
            if (last) {
                if (i < 0) {
                    return -1;
                }
            } else if (i >= length) {
                return -1;
            }
            switch (propertyPath.charAt(i)) {
                case '.':
                    if (!inKey) {
                        return i;
                    }
                    break;
                case '[':
                case ']':
                    inKey = !inKey;
                    break;
            }
            if (last) {
                i--;
            } else {
                i++;
            }
        }
    }

    public static boolean matchesProperty(String registeredPath, String propertyPath) {
        if (!registeredPath.startsWith(propertyPath)) {
            return false;
        }
        if (registeredPath.length() == propertyPath.length()) {
            return true;
        }
        return registeredPath.charAt(propertyPath.length()) == '[' && registeredPath.indexOf(93, propertyPath.length() + 1) == registeredPath.length() - 1;
    }

    public static String canonicalPropertyName(@Nullable String propertyName) {
        if (propertyName == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(propertyName);
        int searchIndex = 0;
        while (searchIndex != -1) {
            int keyStart = sb.indexOf(PropertyAccessor.PROPERTY_KEY_PREFIX, searchIndex);
            searchIndex = -1;
            if (keyStart != -1) {
                int keyEnd = sb.indexOf("]", keyStart + PropertyAccessor.PROPERTY_KEY_PREFIX.length());
                if (keyEnd != -1) {
                    String key = sb.substring(keyStart + PropertyAccessor.PROPERTY_KEY_PREFIX.length(), keyEnd);
                    if ((key.startsWith("'") && key.endsWith("'")) || (key.startsWith("\"") && key.endsWith("\""))) {
                        sb.delete(keyStart + 1, keyStart + 2);
                        sb.delete(keyEnd - 2, keyEnd - 1);
                        keyEnd -= 2;
                    }
                    searchIndex = keyEnd + "]".length();
                }
            }
        }
        return sb.toString();
    }

    @Nullable
    public static String[] canonicalPropertyNames(@Nullable String[] propertyNames) {
        if (propertyNames == null) {
            return null;
        }
        String[] result = new String[propertyNames.length];
        for (int i = 0; i < propertyNames.length; i++) {
            result[i] = canonicalPropertyName(propertyNames[i]);
        }
        return result;
    }
}