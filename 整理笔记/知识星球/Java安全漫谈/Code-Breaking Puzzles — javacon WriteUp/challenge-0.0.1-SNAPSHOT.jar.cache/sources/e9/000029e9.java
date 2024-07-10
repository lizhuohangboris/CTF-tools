package org.thymeleaf.templateresource;

import org.thymeleaf.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateresource/TemplateResourceUtils.class */
final class TemplateResourceUtils {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static String cleanPath(String path) {
        if (path == null) {
            return null;
        }
        String unixPath = StringUtils.replace(path, "\\", "/");
        if (unixPath.length() == 0 || (unixPath.indexOf("/.") < 0 && unixPath.indexOf("//") < 0)) {
            return unixPath;
        }
        boolean rootBased = unixPath.charAt(0) == '/';
        String unixPath2 = rootBased ? unixPath : '/' + unixPath;
        StringBuilder strBuilder = new StringBuilder(unixPath2.length());
        int index = unixPath2.lastIndexOf(47);
        int pos = unixPath2.length() - 1;
        int topCount = 0;
        while (index >= 0) {
            int tokenLen = pos - index;
            if (tokenLen > 0 && (tokenLen != 1 || unixPath2.charAt(index + 1) != '.')) {
                if (tokenLen == 2 && unixPath2.charAt(index + 1) == '.' && unixPath2.charAt(index + 2) == '.') {
                    topCount++;
                } else if (topCount > 0) {
                    topCount--;
                } else {
                    strBuilder.insert(0, unixPath2, index, index + tokenLen + 1);
                }
            }
            pos = index - 1;
            index = pos >= 0 ? unixPath2.lastIndexOf(47, pos) : -1;
        }
        for (int i = 0; i < topCount; i++) {
            strBuilder.insert(0, "/..");
        }
        if (!rootBased) {
            strBuilder.deleteCharAt(0);
        }
        return strBuilder.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String computeRelativeLocation(String location, String relativeLocation) {
        int separatorPos = location.lastIndexOf(47);
        if (separatorPos != -1) {
            StringBuilder relativeBuilder = new StringBuilder(location.length() + relativeLocation.length());
            relativeBuilder.append((CharSequence) location, 0, separatorPos);
            if (relativeLocation.charAt(0) != '/') {
                relativeBuilder.append('/');
            }
            relativeBuilder.append(relativeLocation);
            return relativeBuilder.toString();
        }
        return relativeLocation;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String computeBaseName(String path) {
        if (path == null || path.length() == 0) {
            return null;
        }
        String basePath = path.charAt(path.length() - 1) == '/' ? path.substring(0, path.length() - 1) : path;
        int slashPos = basePath.lastIndexOf(47);
        if (slashPos != -1) {
            int dotPos = basePath.lastIndexOf(46);
            if (dotPos != -1 && dotPos > slashPos + 1) {
                return basePath.substring(slashPos + 1, dotPos);
            }
            return basePath.substring(slashPos + 1);
        }
        int dotPos2 = basePath.lastIndexOf(46);
        if (dotPos2 != -1) {
            return basePath.substring(0, dotPos2);
        }
        if (basePath.length() > 0) {
            return basePath;
        }
        return null;
    }

    private TemplateResourceUtils() {
    }
}