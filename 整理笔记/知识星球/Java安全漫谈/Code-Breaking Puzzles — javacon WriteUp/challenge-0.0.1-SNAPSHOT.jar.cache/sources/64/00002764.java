package org.springframework.web.util;

import javax.servlet.jsp.tagext.Tag;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/util/TagUtils.class */
public abstract class TagUtils {
    public static final String SCOPE_PAGE = "page";
    public static final String SCOPE_REQUEST = "request";
    public static final String SCOPE_SESSION = "session";
    public static final String SCOPE_APPLICATION = "application";

    public static int getScope(String scope) {
        Assert.notNull(scope, "Scope to search for cannot be null");
        if (scope.equals("request")) {
            return 2;
        }
        if (scope.equals("session")) {
            return 3;
        }
        if (scope.equals("application")) {
            return 4;
        }
        return 1;
    }

    public static boolean hasAncestorOfType(Tag tag, Class<?> ancestorTagClass) {
        Assert.notNull(tag, "Tag cannot be null");
        Assert.notNull(ancestorTagClass, "Ancestor tag class cannot be null");
        if (!Tag.class.isAssignableFrom(ancestorTagClass)) {
            throw new IllegalArgumentException("Class '" + ancestorTagClass.getName() + "' is not a valid Tag type");
        }
        Tag parent = tag.getParent();
        while (true) {
            Tag ancestor = parent;
            if (ancestor != null) {
                if (ancestorTagClass.isAssignableFrom(ancestor.getClass())) {
                    return true;
                }
                parent = ancestor.getParent();
            } else {
                return false;
            }
        }
    }

    public static void assertHasAncestorOfType(Tag tag, Class<?> ancestorTagClass, String tagName, String ancestorTagName) {
        Assert.hasText(tagName, "'tagName' must not be empty");
        Assert.hasText(ancestorTagName, "'ancestorTagName' must not be empty");
        if (!hasAncestorOfType(tag, ancestorTagClass)) {
            throw new IllegalStateException("The '" + tagName + "' tag can only be used inside a valid '" + ancestorTagName + "' tag.");
        }
    }
}