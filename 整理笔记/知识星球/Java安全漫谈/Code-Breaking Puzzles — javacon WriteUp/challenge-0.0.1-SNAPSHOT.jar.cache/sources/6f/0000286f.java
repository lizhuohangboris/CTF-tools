package org.thymeleaf.model;

import java.util.Map;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.TemplateData;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/model/IModelFactory.class */
public interface IModelFactory {
    IModel createModel();

    IModel createModel(ITemplateEvent iTemplateEvent);

    IModel parse(TemplateData templateData, String str);

    ICDATASection createCDATASection(CharSequence charSequence);

    IComment createComment(CharSequence charSequence);

    IDocType createHTML5DocType();

    IDocType createDocType(String str, String str2);

    IDocType createDocType(String str, String str2, String str3, String str4, String str5);

    IProcessingInstruction createProcessingInstruction(String str, String str2);

    IText createText(CharSequence charSequence);

    IXMLDeclaration createXMLDeclaration(String str, String str2, String str3);

    IStandaloneElementTag createStandaloneElementTag(String str);

    IStandaloneElementTag createStandaloneElementTag(String str, String str2, String str3);

    IStandaloneElementTag createStandaloneElementTag(String str, boolean z, boolean z2);

    IStandaloneElementTag createStandaloneElementTag(String str, String str2, String str3, boolean z, boolean z2);

    IStandaloneElementTag createStandaloneElementTag(String str, Map<String, String> map, AttributeValueQuotes attributeValueQuotes, boolean z, boolean z2);

    IOpenElementTag createOpenElementTag(String str);

    IOpenElementTag createOpenElementTag(String str, String str2, String str3);

    IOpenElementTag createOpenElementTag(String str, boolean z);

    IOpenElementTag createOpenElementTag(String str, String str2, String str3, boolean z);

    IOpenElementTag createOpenElementTag(String str, Map<String, String> map, AttributeValueQuotes attributeValueQuotes, boolean z);

    ICloseElementTag createCloseElementTag(String str);

    ICloseElementTag createCloseElementTag(String str, boolean z, boolean z2);

    <T extends IProcessableElementTag> T setAttribute(T t, String str, String str2);

    <T extends IProcessableElementTag> T setAttribute(T t, String str, String str2, AttributeValueQuotes attributeValueQuotes);

    <T extends IProcessableElementTag> T replaceAttribute(T t, AttributeName attributeName, String str, String str2);

    <T extends IProcessableElementTag> T replaceAttribute(T t, AttributeName attributeName, String str, String str2, AttributeValueQuotes attributeValueQuotes);

    <T extends IProcessableElementTag> T removeAttribute(T t, String str);

    <T extends IProcessableElementTag> T removeAttribute(T t, String str, String str2);

    <T extends IProcessableElementTag> T removeAttribute(T t, AttributeName attributeName);
}