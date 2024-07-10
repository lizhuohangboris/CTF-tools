package org.thymeleaf.model;

import java.util.Map;
import org.thymeleaf.engine.AttributeName;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/model/IProcessableElementTag.class */
public interface IProcessableElementTag extends IElementTag {
    IAttribute[] getAllAttributes();

    Map<String, String> getAttributeMap();

    boolean hasAttribute(String str);

    boolean hasAttribute(String str, String str2);

    boolean hasAttribute(AttributeName attributeName);

    IAttribute getAttribute(String str);

    IAttribute getAttribute(String str, String str2);

    IAttribute getAttribute(AttributeName attributeName);

    String getAttributeValue(String str);

    String getAttributeValue(String str, String str2);

    String getAttributeValue(AttributeName attributeName);
}