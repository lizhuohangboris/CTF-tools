package org.thymeleaf.engine;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/HTMLElementName.class */
public final class HTMLElementName extends ElementName {
    final String completeNamespacedElementName;
    final String completeHTML5ElementName;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static HTMLElementName forName(String prefix, String elementName) {
        String namePrefix;
        String completeNamespacedElementName;
        String completeHTML5ElementName;
        String[] completeAttributeNames;
        boolean hasPrefix = prefix != null && prefix.length() > 0;
        String nameElementName = (elementName == null || elementName.length() == 0) ? "" : elementName.toLowerCase();
        if (hasPrefix) {
            namePrefix = prefix.toLowerCase();
            completeNamespacedElementName = namePrefix + ":" + nameElementName;
            completeHTML5ElementName = namePrefix + "-" + nameElementName;
            completeAttributeNames = new String[]{completeNamespacedElementName, completeHTML5ElementName};
        } else {
            namePrefix = null;
            completeNamespacedElementName = nameElementName;
            completeHTML5ElementName = nameElementName;
            completeAttributeNames = new String[]{nameElementName};
        }
        return new HTMLElementName(namePrefix, nameElementName, completeNamespacedElementName, completeHTML5ElementName, completeAttributeNames);
    }

    private HTMLElementName(String prefix, String elementName, String completeNamespacedElementName, String completeHTML5ElementName, String[] completeElementNames) {
        super(prefix, elementName, completeElementNames);
        this.completeNamespacedElementName = completeNamespacedElementName;
        this.completeHTML5ElementName = completeHTML5ElementName;
    }

    public String getCompleteNamespacedElementName() {
        return this.completeNamespacedElementName;
    }

    public String getCompleteHTML5ElementName() {
        return this.completeHTML5ElementName;
    }
}