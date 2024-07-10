package org.attoparser.dom;

import ch.qos.logback.core.rolling.helper.IntegerTokenConverter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.naming.EjbRef;
import org.apache.tomcat.websocket.BasicAuthenticator;
import org.attoparser.util.TextUtil;
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
import org.thymeleaf.spring5.processor.SpringOptionInSelectFieldTagProcessor;
import org.thymeleaf.standard.processor.StandardRemoveTagProcessor;
import org.thymeleaf.standard.processor.StandardXmlSpaceTagProcessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/dom/StructureTextsRepository.class */
public final class StructureTextsRepository {
    private static final String[] STANDARD_ATTRIBUTE_NAMES = {"abbr", "accept", "accept-charset", AbstractHtmlInputElementTag.ACCESSKEY_ATTRIBUTE, "action", "align", "alt", "archive", InputTag.AUTOCOMPLETE_ATTRIBUTE, "autofocus", "autoplay", "axis", "border", "cellpadding", "cellspacing", "challenge", "char", "charoff", BasicAuthenticator.charsetparam, "checked", "cite", "class", "classid", "codebase", "codetype", TextareaTag.COLS_ATTRIBUTE, "colspan", FormTag.DEFAULT_COMMAND_NAME, "content", "contenteditable", "contextmenu", "controls", "coords", "data", SpringInputGeneralFieldTagProcessor.DATETIME_INPUT_TYPE_ATTR_VALUE, "declare", "default", "defer", AbstractHtmlElementTag.DIR_ATTRIBUTE, "disabled", "draggable", "dropzone", "enctype", "for", "form", "formaction", "formenctype", "formmethod", "formnovalidate", "formtarget", "frame", "headers", "height", SpringInputGeneralFieldTagProcessor.HIDDEN_INPUT_TYPE_ATTR_VALUE, "high", "href", "hreflang", "http-equiv", "icon", "id", "ismap", "keytype", "kind", "label", "lang", BeanDefinitionParserDelegate.LIST_ELEMENT, "longdesc", "loop", "low", "max", InputTag.MAXLENGTH_ATTRIBUTE, "media", "method", "min", "multiple", "muted", "name", "nohref", "novalidate", "onabort", "onafterprint", "onbeforeprint", "onbeforeunload", AbstractHtmlInputElementTag.ONBLUR_ATTRIBUTE, "oncanplay", "oncanplaythrough", AbstractHtmlInputElementTag.ONCHANGE_ATTRIBUTE, AbstractHtmlElementTag.ONCLICK_ATTRIBUTE, "oncontextmenu", "oncuechange", AbstractHtmlElementTag.ONDBLCLICK_ATTRIBUTE, "ondrag", "ondragend", "ondragenter", "ondragleave", "ondragover", "ondragstart", "ondrop", "ondurationchange", "onemptied", "onended", "onerror", AbstractHtmlInputElementTag.ONFOCUS_ATTRIBUTE, "onformchange", "onforminput", "onhaschange", "oninput", "oninvalid", AbstractHtmlElementTag.ONKEYDOWN_ATTRIBUTE, AbstractHtmlElementTag.ONKEYPRESS_ATTRIBUTE, AbstractHtmlElementTag.ONKEYUP_ATTRIBUTE, "onload", "onloadeddata", "onloadedmetadata", "onloadstart", "onmessage", AbstractHtmlElementTag.ONMOUSEDOWN_ATTRIBUTE, AbstractHtmlElementTag.ONMOUSEMOVE_ATTRIBUTE, AbstractHtmlElementTag.ONMOUSEOUT_ATTRIBUTE, AbstractHtmlElementTag.ONMOUSEOVER_ATTRIBUTE, AbstractHtmlElementTag.ONMOUSEUP_ATTRIBUTE, "onmousewheel", "onoffline", "ononline", "onpagehide", "onpageshow", "onpause", "onplay", "onplaying", "onpopstate", "onprogress", "onratechange", "onredo", "onreset", "onresize", "onscroll", "onseeked", "onseeking", "onselect", "onstalled", "onstorage", "onsubmit", "onsuspend", "ontimeupdate", "onundo", "onunload", "onvolumechange", "onwaiting", "open", "optimum", "pattern", "placeholder", "poster", "preload", DefaultBeanDefinitionDocumentReader.PROFILE_ATTRIBUTE, "radiogroup", AbstractHtmlInputElementTag.READONLY_ATTRIBUTE, "rel", DefaultBindingErrorProcessor.MISSING_FIELD_ERROR_CODE, "rev", TextareaTag.ROWS_ATTRIBUTE, "rowspan", "rules", "scheme", "scope", "selected", "shape", InputTag.SIZE_ATTRIBUTE, ErrorsTag.SPAN_TAG, "spellcheck", "src", "srclang", "standby", "style", "summary", AbstractHtmlElementTag.TABINDEX_ATTRIBUTE, "title", "translate", "type", "usemap", "valign", "value", "valuetype", "width", "xml:lang", StandardXmlSpaceTagProcessor.TARGET_ATTR_NAME, "xmlns"};
    private static final String[] STANDARD_ELEMENT_NAMES = {"a", "abbr", "address", "area", "article", "aside", "audio", "b", "base", "bdi", "bdo", "blockquote", StandardRemoveTagProcessor.VALUE_BODY, "br", "button", "canvas", "caption", "cite", "code", "col", "colgroup", FormTag.DEFAULT_COMMAND_NAME, "datalist", "dd", "del", "details", "dfn", "dialog", "div", "dl", "dt", "em", "embed", "fieldset", "figcaption", "figure", "footer", "form", "g", "h1", "h2", "h3", "h4", "h5", "h6", "head", "header", "hgroup", "hr", org.thymeleaf.engine.DocType.DEFAULT_ELEMENT_NAME, IntegerTokenConverter.CONVERTER_KEY, "iframe", "img", "input", "ins", "kbd", "keygen", "label", "legend", "li", EjbRef.LINK, "main", BeanDefinitionParserDelegate.MAP_ELEMENT, "mark", "menu", "menuitem", BeanDefinitionParserDelegate.META_ELEMENT, "meter", "nav", "noscript", "object", "ol", "optgroup", SpringOptionInSelectFieldTagProcessor.OPTION_TAG_NAME, "output", "p", "param", "pre", "progress", "rb", "rp", "rt", "rtc", "ruby", "s", "samp", "script", "section", "select", "small", "source", ErrorsTag.SPAN_TAG, "strong", "style", "sub", "summary", "sup", "table", "tbody", "td", "textarea", "tfoot", "th", "thead", SpringInputGeneralFieldTagProcessor.TIME_INPUT_TYPE_ATTR_VALUE, "title", "tr", "track", "u", "ul", "var", "video", "wbr"};
    private static final String[] ALL_STANDARD_NAMES;

    static {
        String[] strArr;
        String[] strArr2;
        Set<String> allStandardNamesSet = new HashSet<>((STANDARD_ELEMENT_NAMES.length + STANDARD_ATTRIBUTE_NAMES.length + 1) * 2, 1.0f);
        allStandardNamesSet.addAll(Arrays.asList(STANDARD_ELEMENT_NAMES));
        allStandardNamesSet.addAll(Arrays.asList(STANDARD_ATTRIBUTE_NAMES));
        for (String str : STANDARD_ELEMENT_NAMES) {
            allStandardNamesSet.add(str.toUpperCase());
        }
        for (String str2 : STANDARD_ATTRIBUTE_NAMES) {
            allStandardNamesSet.add(str2.toUpperCase());
        }
        List<String> allStandardNamesList = new ArrayList<>(allStandardNamesSet);
        Collections.sort(allStandardNamesList);
        ALL_STANDARD_NAMES = (String[]) allStandardNamesList.toArray(new String[allStandardNamesList.size()]);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getStructureName(char[] buffer, int offset, int len) {
        int index = TextUtil.binarySearch(true, (CharSequence[]) ALL_STANDARD_NAMES, buffer, offset, len);
        if (index < 0) {
            return new String(buffer, offset, len);
        }
        return ALL_STANDARD_NAMES[index];
    }

    private StructureTextsRepository() {
    }
}