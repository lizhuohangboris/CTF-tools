package org.thymeleaf.processor.element;

import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.TemplateData;
import org.thymeleaf.inline.IInliner;
import org.thymeleaf.model.AttributeValueQuotes;
import org.thymeleaf.model.IModel;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/processor/element/IElementTagStructureHandler.class */
public interface IElementTagStructureHandler {
    void reset();

    void setLocalVariable(String str, Object obj);

    void removeLocalVariable(String str);

    void setAttribute(String str, String str2);

    void setAttribute(String str, String str2, AttributeValueQuotes attributeValueQuotes);

    void replaceAttribute(AttributeName attributeName, String str, String str2);

    void replaceAttribute(AttributeName attributeName, String str, String str2, AttributeValueQuotes attributeValueQuotes);

    void removeAttribute(String str);

    void removeAttribute(String str, String str2);

    void removeAttribute(AttributeName attributeName);

    void setSelectionTarget(Object obj);

    void setInliner(IInliner iInliner);

    void setTemplateData(TemplateData templateData);

    void setBody(CharSequence charSequence, boolean z);

    void setBody(IModel iModel, boolean z);

    void insertBefore(IModel iModel);

    void insertImmediatelyAfter(IModel iModel, boolean z);

    void replaceWith(CharSequence charSequence, boolean z);

    void replaceWith(IModel iModel, boolean z);

    void removeElement();

    void removeTags();

    void removeBody();

    void removeAllButFirstChild();

    void iterateElement(String str, String str2, Object obj);
}