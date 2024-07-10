package org.thymeleaf.engine;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/XMLAttributeName.class */
public final class XMLAttributeName extends AttributeName {
    final String completeNamespacedAttributeName;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static XMLAttributeName forName(String prefix, String attributeName) {
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
        return new XMLAttributeName(prefix, attributeName, completeNamespacedAttributeName, completeAttributeNames);
    }

    private XMLAttributeName(String prefix, String attributeName, String completeNamespacedAttributeName, String[] completeAttributeNames) {
        super(prefix, attributeName, completeAttributeNames);
        this.completeNamespacedAttributeName = completeNamespacedAttributeName;
    }

    public String getCompleteNamespacedAttributeName() {
        return this.completeNamespacedAttributeName;
    }
}