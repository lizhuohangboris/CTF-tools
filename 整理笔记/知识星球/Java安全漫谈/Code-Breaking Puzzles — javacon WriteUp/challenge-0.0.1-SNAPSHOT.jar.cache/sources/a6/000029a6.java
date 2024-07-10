package org.thymeleaf.standard.util;

import org.thymeleaf.engine.AttributeDefinition;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.ElementTagStructureHandler;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/util/StandardProcessorUtils.class */
public final class StandardProcessorUtils {
    public static void replaceAttribute(IElementTagStructureHandler structureHandler, AttributeName oldAttributeName, AttributeDefinition attributeDefinition, String attributeName, String attributeValue) {
        if (structureHandler instanceof ElementTagStructureHandler) {
            ((ElementTagStructureHandler) structureHandler).replaceAttribute(oldAttributeName, attributeDefinition, attributeName, attributeValue, null);
        } else {
            structureHandler.replaceAttribute(oldAttributeName, attributeName, attributeValue);
        }
    }

    public static void setAttribute(IElementTagStructureHandler structureHandler, AttributeDefinition attributeDefinition, String attributeName, String attributeValue) {
        if (structureHandler instanceof ElementTagStructureHandler) {
            ((ElementTagStructureHandler) structureHandler).setAttribute(attributeDefinition, attributeName, attributeValue, null);
        } else {
            structureHandler.setAttribute(attributeName, attributeValue);
        }
    }

    private StandardProcessorUtils() {
    }
}