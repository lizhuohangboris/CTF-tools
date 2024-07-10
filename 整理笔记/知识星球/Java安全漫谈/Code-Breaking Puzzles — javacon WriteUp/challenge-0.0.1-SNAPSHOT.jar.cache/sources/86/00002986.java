package org.thymeleaf.standard.processor;

import org.apache.catalina.Lifecycle;
import org.apache.tomcat.websocket.BasicAuthenticator;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.validation.DataBinder;
import org.springframework.web.servlet.tags.form.AbstractHtmlElementTag;
import org.springframework.web.servlet.tags.form.AbstractHtmlInputElementTag;
import org.springframework.web.servlet.tags.form.ErrorsTag;
import org.springframework.web.servlet.tags.form.InputTag;
import org.springframework.web.servlet.tags.form.TextareaTag;
import org.thymeleaf.spring5.processor.SpringInputGeneralFieldTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/StandardRemovableAttributeTagProcessor.class */
public final class StandardRemovableAttributeTagProcessor extends AbstractStandardAttributeModifierTagProcessor {
    public static final int PRECEDENCE = 1000;
    public static final String[] ATTR_NAMES = {"abbr", "accept", "accept-charset", AbstractHtmlInputElementTag.ACCESSKEY_ATTRIBUTE, "align", "alt", "archive", "audio", InputTag.AUTOCOMPLETE_ATTRIBUTE, "axis", "background", "bgcolor", "border", "cellpadding", "cellspacing", "challenge", BasicAuthenticator.charsetparam, "cite", "class", "classid", "codebase", "codetype", TextareaTag.COLS_ATTRIBUTE, "colspan", "compact", "content", "contenteditable", "contextmenu", "data", SpringInputGeneralFieldTagProcessor.DATETIME_INPUT_TYPE_ATTR_VALUE, AbstractHtmlElementTag.DIR_ATTRIBUTE, "draggable", "dropzone", "enctype", "for", "form", "formaction", "formenctype", "formmethod", "formtarget", "frame", "frameborder", "headers", "height", "high", "hreflang", "hspace", "http-equiv", "icon", "id", "keytype", "kind", "label", "lang", BeanDefinitionParserDelegate.LIST_ELEMENT, "longdesc", "low", "manifest", "marginheight", "marginwidth", "max", InputTag.MAXLENGTH_ATTRIBUTE, "media", "min", "optimum", "pattern", "placeholder", "poster", "preload", "radiogroup", "rel", "rev", TextareaTag.ROWS_ATTRIBUTE, "rowspan", "rules", "sandbox", "scheme", "scope", "scrolling", InputTag.SIZE_ATTRIBUTE, "sizes", ErrorsTag.SPAN_TAG, "spellcheck", "standby", "style", "srclang", Lifecycle.START_EVENT, "step", "summary", AbstractHtmlElementTag.TABINDEX_ATTRIBUTE, DataBinder.DEFAULT_OBJECT_NAME, "title", "usemap", "valuetype", "vspace", "width", "wrap"};

    public StandardRemovableAttributeTagProcessor(String dialectPrefix, String attrName) {
        super(TemplateMode.HTML, dialectPrefix, attrName, 1000, true, false);
    }
}