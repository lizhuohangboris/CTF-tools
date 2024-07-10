package org.thymeleaf.engine;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/HTMLAttributeName.class */
public final class HTMLAttributeName extends AttributeName {
    final String completeNamespacedAttributeName;
    final String completeHTML5AttributeName;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static HTMLAttributeName forName(String prefix, String attributeName) {
        String namePrefix;
        String completeNamespacedAttributeName;
        String completeHTML5AttributeName;
        String[] completeAttributeNames;
        boolean hasPrefix = prefix != null && prefix.length() > 0;
        String nameAttributeName = (attributeName == null || attributeName.length() == 0) ? null : attributeName.toLowerCase();
        if (hasPrefix) {
            namePrefix = prefix.toLowerCase();
            completeNamespacedAttributeName = namePrefix + ":" + nameAttributeName;
            completeHTML5AttributeName = "data-" + namePrefix + "-" + nameAttributeName;
            completeAttributeNames = new String[]{completeNamespacedAttributeName, completeHTML5AttributeName};
        } else {
            namePrefix = null;
            completeNamespacedAttributeName = nameAttributeName;
            completeHTML5AttributeName = nameAttributeName;
            completeAttributeNames = new String[]{nameAttributeName};
        }
        return new HTMLAttributeName(namePrefix, nameAttributeName, completeNamespacedAttributeName, completeHTML5AttributeName, completeAttributeNames);
    }

    private HTMLAttributeName(String prefix, String attributeName, String completeNamespacedAttributeName, String completeHTML5AttributeName, String[] completeAttributeNames) {
        super(prefix, attributeName, completeAttributeNames);
        this.completeNamespacedAttributeName = completeNamespacedAttributeName;
        this.completeHTML5AttributeName = completeHTML5AttributeName;
    }

    public String getCompleteNamespacedAttributeName() {
        return this.completeNamespacedAttributeName;
    }

    public String getCompleteHTML5AttributeName() {
        return this.completeHTML5AttributeName;
    }
}