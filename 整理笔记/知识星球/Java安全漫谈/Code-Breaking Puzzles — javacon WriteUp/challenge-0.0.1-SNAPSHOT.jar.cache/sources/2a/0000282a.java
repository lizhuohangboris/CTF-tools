package org.thymeleaf.engine;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/TextElementName.class */
public final class TextElementName extends ElementName {
    final String completeNamespacedElementName;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static TextElementName forName(String prefix, String elementName) {
        String completeNamespacedElementName;
        String[] completeElementNames;
        boolean hasPrefix = prefix != null && prefix.length() > 0;
        if (hasPrefix) {
            completeNamespacedElementName = prefix + ":" + elementName;
            completeElementNames = new String[]{completeNamespacedElementName};
        } else {
            completeNamespacedElementName = elementName;
            completeElementNames = new String[]{elementName};
        }
        return new TextElementName(prefix, elementName, completeNamespacedElementName, completeElementNames);
    }

    private TextElementName(String prefix, String elementName, String completeNamespacedElementName, String[] completeElementNames) {
        super(prefix, elementName, completeElementNames);
        this.completeNamespacedElementName = completeNamespacedElementName;
    }

    public String getCompleteNamespacedElementName() {
        return this.completeNamespacedElementName;
    }
}