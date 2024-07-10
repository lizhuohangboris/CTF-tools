package org.springframework.util.xml;

import javax.xml.transform.Transformer;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/util/xml/TransformerUtils.class */
public abstract class TransformerUtils {
    public static final int DEFAULT_INDENT_AMOUNT = 2;

    public static void enableIndenting(Transformer transformer) {
        enableIndenting(transformer, 2);
    }

    public static void enableIndenting(Transformer transformer, int indentAmount) {
        Assert.notNull(transformer, "Transformer must not be null");
        if (indentAmount < 0) {
            throw new IllegalArgumentException("Invalid indent amount (must not be less than zero): " + indentAmount);
        }
        transformer.setOutputProperty("indent", CustomBooleanEditor.VALUE_YES);
        try {
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", String.valueOf(indentAmount));
        } catch (IllegalArgumentException e) {
        }
    }

    public static void disableIndenting(Transformer transformer) {
        Assert.notNull(transformer, "Transformer must not be null");
        transformer.setOutputProperty("indent", "no");
    }
}