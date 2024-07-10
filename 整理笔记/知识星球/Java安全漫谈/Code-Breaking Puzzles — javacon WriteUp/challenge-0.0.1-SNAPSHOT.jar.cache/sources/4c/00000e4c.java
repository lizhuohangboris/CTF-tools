package org.attoparser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.apache.tomcat.websocket.BasicAuthenticator;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader;
import org.springframework.validation.DefaultBindingErrorProcessor;
import org.springframework.web.servlet.tags.form.AbstractHtmlElementTag;
import org.springframework.web.servlet.tags.form.AbstractHtmlInputElementTag;
import org.springframework.web.servlet.tags.form.ErrorsTag;
import org.springframework.web.servlet.tags.form.FormTag;
import org.springframework.web.servlet.tags.form.InputTag;
import org.springframework.web.servlet.tags.form.TextareaTag;
import org.thymeleaf.spring5.processor.SpringInputGeneralFieldTagProcessor;
import org.thymeleaf.standard.processor.StandardXmlSpaceTagProcessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/HtmlNames.class */
final class HtmlNames {
    static final Set<String> ALL_STANDARD_ELEMENT_NAMES;
    static final Set<String> ALL_STANDARD_ATTRIBUTE_NAMES;

    static {
        List<String> allStandardElementNamesAux = new ArrayList<>(HtmlElements.ALL_STANDARD_ELEMENTS.size() + 3);
        for (HtmlElement element : HtmlElements.ALL_STANDARD_ELEMENTS) {
            allStandardElementNamesAux.add(new String(element.name));
        }
        Collections.sort(allStandardElementNamesAux);
        ALL_STANDARD_ELEMENT_NAMES = Collections.unmodifiableSet(new LinkedHashSet(allStandardElementNamesAux));
        List<String> allStandardAttributeNamesAux = new ArrayList<>(Arrays.asList("abbr", "accept", "accept-charset", AbstractHtmlInputElementTag.ACCESSKEY_ATTRIBUTE, "action", "align", "alt", "archive", InputTag.AUTOCOMPLETE_ATTRIBUTE, "autofocus", "autoplay", "axis", "border", "cellpadding", "cellspacing", "challenge", "char", "charoff", BasicAuthenticator.charsetparam, "checked", "cite", "class", "classid", "codebase", "codetype", TextareaTag.COLS_ATTRIBUTE, "colspan", FormTag.DEFAULT_COMMAND_NAME, "content", "contenteditable", "contextmenu", "controls", "coords", "data", SpringInputGeneralFieldTagProcessor.DATETIME_INPUT_TYPE_ATTR_VALUE, "declare", "default", "defer", AbstractHtmlElementTag.DIR_ATTRIBUTE, "disabled", "draggable", "dropzone", "enctype", "for", "form", "formaction", "formenctype", "formmethod", "formnovalidate", "formtarget", "frame", "headers", "height", SpringInputGeneralFieldTagProcessor.HIDDEN_INPUT_TYPE_ATTR_VALUE, "high", "href", "hreflang", "http-equiv", "icon", "id", "ismap", "keytype", "kind", "label", "lang", BeanDefinitionParserDelegate.LIST_ELEMENT, "longdesc", "loop", "low", "max", InputTag.MAXLENGTH_ATTRIBUTE, "media", "method", "min", "multiple", "muted", "name", "nohref", "novalidate", "onabort", "onafterprint", "onbeforeprint", "onbeforeunload", AbstractHtmlInputElementTag.ONBLUR_ATTRIBUTE, "oncanplay", "oncanplaythrough", AbstractHtmlInputElementTag.ONCHANGE_ATTRIBUTE, AbstractHtmlElementTag.ONCLICK_ATTRIBUTE, "oncontextmenu", "oncuechange", AbstractHtmlElementTag.ONDBLCLICK_ATTRIBUTE, "ondrag", "ondragend", "ondragenter", "ondragleave", "ondragover", "ondragstart", "ondrop", "ondurationchange", "onemptied", "onended", "onerror", AbstractHtmlInputElementTag.ONFOCUS_ATTRIBUTE, "onformchange", "onforminput", "onhaschange", "oninput", "oninvalid", AbstractHtmlElementTag.ONKEYDOWN_ATTRIBUTE, AbstractHtmlElementTag.ONKEYPRESS_ATTRIBUTE, AbstractHtmlElementTag.ONKEYUP_ATTRIBUTE, "onload", "onloadeddata", "onloadedmetadata", "onloadstart", "onmessage", AbstractHtmlElementTag.ONMOUSEDOWN_ATTRIBUTE, AbstractHtmlElementTag.ONMOUSEMOVE_ATTRIBUTE, AbstractHtmlElementTag.ONMOUSEOUT_ATTRIBUTE, AbstractHtmlElementTag.ONMOUSEOVER_ATTRIBUTE, AbstractHtmlElementTag.ONMOUSEUP_ATTRIBUTE, "onmousewheel", "onoffline", "ononline", "onpagehide", "onpageshow", "onpause", "onplay", "onplaying", "onpopstate", "onprogress", "onratechange", "onredo", "onreset", "onresize", "onscroll", "onseeked", "onseeking", "onselect", "onstalled", "onstorage", "onsubmit", "onsuspend", "ontimeupdate", "onundo", "onunload", "onvolumechange", "onwaiting", "open", "optimum", "pattern", "placeholder", "poster", "preload", DefaultBeanDefinitionDocumentReader.PROFILE_ATTRIBUTE, "radiogroup", AbstractHtmlInputElementTag.READONLY_ATTRIBUTE, "rel", DefaultBindingErrorProcessor.MISSING_FIELD_ERROR_CODE, "rev", TextareaTag.ROWS_ATTRIBUTE, "rowspan", "rules", "scheme", "scope", "selected", "shape", InputTag.SIZE_ATTRIBUTE, ErrorsTag.SPAN_TAG, "spellcheck", "src", "srclang", "standby", "style", "summary", AbstractHtmlElementTag.TABINDEX_ATTRIBUTE, "title", "translate", "type", "usemap", "valign", "value", "valuetype", "width", "xml:lang", StandardXmlSpaceTagProcessor.TARGET_ATTR_NAME, "xmlns"));
        Collections.sort(allStandardAttributeNamesAux);
        ALL_STANDARD_ATTRIBUTE_NAMES = Collections.unmodifiableSet(new LinkedHashSet(allStandardAttributeNamesAux));
    }

    private HtmlNames() {
    }
}