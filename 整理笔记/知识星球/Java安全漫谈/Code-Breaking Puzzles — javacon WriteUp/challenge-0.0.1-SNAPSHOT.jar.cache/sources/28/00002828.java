package org.thymeleaf.engine;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/TextAttributeName.class */
public final class TextAttributeName extends AttributeName {
    final String completeNamespacedAttributeName;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static TextAttributeName forName(String prefix, String attributeName) {
        String completeNamespacedAttributeName;
        String[] completeAttributeNames;
        boolean hasPrefix = prefix != null && prefix.length() > 0;
        if (hasPrefix) {
            completeNamespacedAttributeName = prefix + ":" + attributeName;
            completeAttributeNames = new String[]{completeNamespacedAttributeName};
        } else {
            completeNamespacedAttributeName = attributeName;
            completeAttributeNames = new String[]{attributeName};
        }
        return new TextAttributeName(prefix, attributeName, completeNamespacedAttributeName, completeAttributeNames);
    }

    private TextAttributeName(String prefix, String attributeName, String completeNamespacedAttributeName, String[] completeAttributeNames) {
        super(prefix, attributeName, completeAttributeNames);
        this.completeNamespacedAttributeName = completeNamespacedAttributeName;
    }

    public String getCompleteNamespacedAttributeName() {
        return this.completeNamespacedAttributeName;
    }
}