package org.thymeleaf.engine;

import ch.qos.logback.core.rolling.helper.IntegerTokenConverter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.naming.EjbRef;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.web.servlet.tags.form.ErrorsTag;
import org.springframework.web.servlet.tags.form.FormTag;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.processor.element.IElementProcessor;
import org.thymeleaf.processor.element.MatchingAttributeName;
import org.thymeleaf.processor.element.MatchingElementName;
import org.thymeleaf.spring5.processor.SpringInputGeneralFieldTagProcessor;
import org.thymeleaf.spring5.processor.SpringOptionInSelectFieldTagProcessor;
import org.thymeleaf.standard.processor.StandardRemoveTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.TextUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/ElementDefinitions.class */
public final class ElementDefinitions {
    public static final Set<String> ALL_STANDARD_HTML_ELEMENT_NAMES;
    private static final HTMLElementDefinitionSpec HTML = new HTMLElementDefinitionSpec(ElementNames.forHTMLName(DocType.DEFAULT_ELEMENT_NAME), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec HEAD = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("head"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec TITLE = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("title"), HTMLElementType.ESCAPABLE_RAW_TEXT);
    private static final HTMLElementDefinitionSpec BASE = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("base"), HTMLElementType.VOID);
    private static final HTMLElementDefinitionSpec LINK = new HTMLElementDefinitionSpec(ElementNames.forHTMLName(EjbRef.LINK), HTMLElementType.VOID);
    private static final HTMLElementDefinitionSpec META = new HTMLElementDefinitionSpec(ElementNames.forHTMLName(BeanDefinitionParserDelegate.META_ELEMENT), HTMLElementType.VOID);
    private static final HTMLElementDefinitionSpec STYLE = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("style"), HTMLElementType.RAW_TEXT);
    private static final HTMLElementDefinitionSpec SCRIPT = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("script"), HTMLElementType.RAW_TEXT);
    private static final HTMLElementDefinitionSpec NOSCRIPT = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("noscript"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec BODY = new HTMLElementDefinitionSpec(ElementNames.forHTMLName(StandardRemoveTagProcessor.VALUE_BODY), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec ARTICLE = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("article"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec SECTION = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("section"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec NAV = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("nav"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec ASIDE = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("aside"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec H1 = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("h1"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec H2 = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("h2"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec H3 = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("h3"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec H4 = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("h4"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec H5 = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("h5"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec H6 = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("h6"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec HGROUP = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("hgroup"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec HEADER = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("header"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec FOOTER = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("footer"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec ADDRESS = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("address"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec MAIN = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("main"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec P = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("p"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec HR = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("hr"), HTMLElementType.VOID);
    private static final HTMLElementDefinitionSpec PRE = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("pre"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec BLOCKQUOTE = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("blockquote"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec OL = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("ol"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec UL = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("ul"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec LI = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("li"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec DL = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("dl"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec DT = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("dt"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec DD = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("dd"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec FIGURE = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("figure"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec FIGCAPTION = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("figcaption"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec DIV = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("div"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec A = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("a"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec EM = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("em"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec STRONG = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("strong"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec SMALL = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("small"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec S = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("s"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec CITE = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("cite"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec G = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("g"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec DFN = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("dfn"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec ABBR = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("abbr"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec TIME = new HTMLElementDefinitionSpec(ElementNames.forHTMLName(SpringInputGeneralFieldTagProcessor.TIME_INPUT_TYPE_ATTR_VALUE), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec CODE = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("code"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec VAR = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("var"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec SAMP = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("samp"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec KBD = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("kbd"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec SUB = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("sub"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec SUP = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("sup"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec I = new HTMLElementDefinitionSpec(ElementNames.forHTMLName(IntegerTokenConverter.CONVERTER_KEY), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec B = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("b"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec U = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("u"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec MARK = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("mark"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec RUBY = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("ruby"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec RB = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("rb"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec RT = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("rt"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec RTC = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("rtc"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec RP = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("rp"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec BDI = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("bdi"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec BDO = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("bdo"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec SPAN = new HTMLElementDefinitionSpec(ElementNames.forHTMLName(ErrorsTag.SPAN_TAG), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec BR = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("br"), HTMLElementType.VOID);
    private static final HTMLElementDefinitionSpec WBR = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("wbr"), HTMLElementType.VOID);
    private static final HTMLElementDefinitionSpec INS = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("ins"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec DEL = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("del"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec IMG = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("img"), HTMLElementType.VOID);
    private static final HTMLElementDefinitionSpec IFRAME = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("iframe"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec EMBED = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("embed"), HTMLElementType.VOID);
    private static final HTMLElementDefinitionSpec OBJECT = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("object"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec PARAM = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("param"), HTMLElementType.VOID);
    private static final HTMLElementDefinitionSpec VIDEO = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("video"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec AUDIO = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("audio"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec SOURCE = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("source"), HTMLElementType.VOID);
    private static final HTMLElementDefinitionSpec TRACK = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("track"), HTMLElementType.VOID);
    private static final HTMLElementDefinitionSpec CANVAS = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("canvas"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec MAP = new HTMLElementDefinitionSpec(ElementNames.forHTMLName(BeanDefinitionParserDelegate.MAP_ELEMENT), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec AREA = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("area"), HTMLElementType.VOID);
    private static final HTMLElementDefinitionSpec TABLE = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("table"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec CAPTION = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("caption"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec COLGROUP = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("colgroup"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec COL = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("col"), HTMLElementType.VOID);
    private static final HTMLElementDefinitionSpec TBODY = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("tbody"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec THEAD = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("thead"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec TFOOT = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("tfoot"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec TR = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("tr"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec TD = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("td"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec TH = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("th"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec FORM = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("form"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec FIELDSET = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("fieldset"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec LEGEND = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("legend"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec LABEL = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("label"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec INPUT = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("input"), HTMLElementType.VOID);
    private static final HTMLElementDefinitionSpec BUTTON = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("button"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec SELECT = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("select"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec DATALIST = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("datalist"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec OPTGROUP = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("optgroup"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec OPTION = new HTMLElementDefinitionSpec(ElementNames.forHTMLName(SpringOptionInSelectFieldTagProcessor.OPTION_TAG_NAME), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec TEXTAREA = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("textarea"), HTMLElementType.ESCAPABLE_RAW_TEXT);
    private static final HTMLElementDefinitionSpec KEYGEN = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("keygen"), HTMLElementType.VOID);
    private static final HTMLElementDefinitionSpec OUTPUT = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("output"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec PROGRESS = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("progress"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec METER = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("meter"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec DETAILS = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("details"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec SUMMARY = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("summary"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec COMMAND = new HTMLElementDefinitionSpec(ElementNames.forHTMLName(FormTag.DEFAULT_COMMAND_NAME), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec MENU = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("menu"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec MENUITEM = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("menuitem"), HTMLElementType.VOID);
    private static final HTMLElementDefinitionSpec DIALOG = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("dialog"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec TEMPLATE = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("template"), HTMLElementType.RAW_TEXT);
    private static final HTMLElementDefinitionSpec ELEMENT = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("element"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec DECORATOR = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("decorator"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec CONTENT = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("content"), HTMLElementType.NORMAL);
    private static final HTMLElementDefinitionSpec SHADOW = new HTMLElementDefinitionSpec(ElementNames.forHTMLName("shadow"), HTMLElementType.NORMAL);
    private final ElementDefinitionRepository htmlElementRepository;
    private final ElementDefinitionRepository xmlElementRepository;
    private final ElementDefinitionRepository textElementRepository;
    private final ElementDefinitionRepository javascriptElementRepository;
    private final ElementDefinitionRepository cssElementRepository;

    static {
        String[] strArr;
        List<String> htmlElementDefinitionNamesAux = new ArrayList<>(HTMLElementDefinitionSpec.ALL_SPECS.size() + 1);
        for (HTMLElementDefinitionSpec elementDefinitionSpec : HTMLElementDefinitionSpec.ALL_SPECS) {
            for (String completeElementName : elementDefinitionSpec.name.completeElementNames) {
                htmlElementDefinitionNamesAux.add(completeElementName);
            }
        }
        Collections.sort(htmlElementDefinitionNamesAux);
        ALL_STANDARD_HTML_ELEMENT_NAMES = Collections.unmodifiableSet(new LinkedHashSet(htmlElementDefinitionNamesAux));
    }

    public ElementDefinitions(Map<TemplateMode, Set<IElementProcessor>> elementProcessorsByTemplateMode) {
        List<HTMLElementDefinition> standardHTMLElementDefinitions = new ArrayList<>(HTMLElementDefinitionSpec.ALL_SPECS.size() + 1);
        for (HTMLElementDefinitionSpec definitionSpec : HTMLElementDefinitionSpec.ALL_SPECS) {
            standardHTMLElementDefinitions.add(buildHTMLElementDefinition(definitionSpec.name, definitionSpec.type, elementProcessorsByTemplateMode.get(TemplateMode.HTML)));
        }
        this.htmlElementRepository = new ElementDefinitionRepository(TemplateMode.HTML, elementProcessorsByTemplateMode);
        this.xmlElementRepository = new ElementDefinitionRepository(TemplateMode.XML, elementProcessorsByTemplateMode);
        this.textElementRepository = new ElementDefinitionRepository(TemplateMode.TEXT, elementProcessorsByTemplateMode);
        this.javascriptElementRepository = new ElementDefinitionRepository(TemplateMode.JAVASCRIPT, elementProcessorsByTemplateMode);
        this.cssElementRepository = new ElementDefinitionRepository(TemplateMode.CSS, elementProcessorsByTemplateMode);
        for (HTMLElementDefinition elementDefinition : standardHTMLElementDefinitions) {
            this.htmlElementRepository.storeStandardElement(elementDefinition);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static HTMLElementDefinition buildHTMLElementDefinition(HTMLElementName name, HTMLElementType type, Set<IElementProcessor> elementProcessors) {
        Set<IElementProcessor> associatedProcessors = new LinkedHashSet<>(2);
        if (elementProcessors != null) {
            for (IElementProcessor processor : elementProcessors) {
                TemplateMode templateMode = processor.getTemplateMode();
                if (templateMode == TemplateMode.HTML) {
                    MatchingElementName matchingElementName = processor.getMatchingElementName();
                    MatchingAttributeName matchingAttributeName = processor.getMatchingAttributeName();
                    if ((matchingElementName != null && matchingElementName.getTemplateMode() != TemplateMode.HTML) || (matchingAttributeName != null && matchingAttributeName.getTemplateMode() != TemplateMode.HTML)) {
                        throw new ConfigurationException("HTML processors must return HTML element names and HTML attribute names (processor: " + processor.getClass().getName() + ")");
                    }
                    if (matchingAttributeName == null || matchingAttributeName.isMatchingAllAttributes()) {
                        if (matchingElementName == null || matchingElementName.matches(name)) {
                            associatedProcessors.add(processor);
                        }
                    }
                }
            }
        }
        return new HTMLElementDefinition(name, type, associatedProcessors);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static XMLElementDefinition buildXMLElementDefinition(XMLElementName name, Set<IElementProcessor> elementProcessors) {
        Set<IElementProcessor> associatedProcessors = new LinkedHashSet<>(2);
        if (elementProcessors != null) {
            for (IElementProcessor processor : elementProcessors) {
                TemplateMode templateMode = processor.getTemplateMode();
                if (templateMode == TemplateMode.XML) {
                    MatchingElementName matchingElementName = processor.getMatchingElementName();
                    MatchingAttributeName matchingAttributeName = processor.getMatchingAttributeName();
                    if ((matchingElementName != null && matchingElementName.getTemplateMode() != TemplateMode.XML) || (matchingAttributeName != null && matchingAttributeName.getTemplateMode() != TemplateMode.XML)) {
                        throw new ConfigurationException("XML processors must return XML element names and XML attribute names (processor: " + processor.getClass().getName() + ")");
                    }
                    if (matchingAttributeName == null || matchingAttributeName.isMatchingAllAttributes()) {
                        if (matchingElementName == null || matchingElementName.matches(name)) {
                            associatedProcessors.add(processor);
                        }
                    }
                }
            }
        }
        return new XMLElementDefinition(name, associatedProcessors);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static TextElementDefinition buildTextElementDefinition(TemplateMode templateMode, TextElementName name, Set<IElementProcessor> elementProcessors) {
        Set<IElementProcessor> associatedProcessors = new LinkedHashSet<>(2);
        if (elementProcessors != null) {
            for (IElementProcessor processor : elementProcessors) {
                if (processor.getTemplateMode() == templateMode) {
                    MatchingElementName matchingElementName = processor.getMatchingElementName();
                    MatchingAttributeName matchingAttributeName = processor.getMatchingAttributeName();
                    if ((matchingElementName != null && matchingElementName.getTemplateMode() != templateMode) || (matchingAttributeName != null && matchingAttributeName.getTemplateMode() != templateMode)) {
                        throw new ConfigurationException(templateMode + " processors must return " + templateMode + "element names and " + templateMode + " attribute names (processor: " + processor.getClass().getName() + ")");
                    }
                    if (matchingAttributeName == null || matchingAttributeName.isMatchingAllAttributes()) {
                        if (matchingElementName == null || matchingElementName.matches(name)) {
                            associatedProcessors.add(processor);
                        }
                    }
                }
            }
        }
        return new TextElementDefinition(name, associatedProcessors);
    }

    public ElementDefinition forName(TemplateMode templateMode, String elementName) {
        if (templateMode == null) {
            throw new IllegalArgumentException("Template Mode cannot be null");
        }
        switch (templateMode) {
            case HTML:
                return forHTMLName(elementName);
            case XML:
                return forXMLName(elementName);
            case TEXT:
                return forTextName(elementName);
            case JAVASCRIPT:
                return forJavaScriptName(elementName);
            case CSS:
                return forCSSName(elementName);
            case RAW:
                throw new IllegalArgumentException("Element Definitions cannot be obtained for " + templateMode + " template mode ");
            default:
                throw new IllegalArgumentException("Unknown template mode " + templateMode);
        }
    }

    public ElementDefinition forName(TemplateMode templateMode, String prefix, String elementName) {
        if (templateMode == null) {
            throw new IllegalArgumentException("Template Mode cannot be null");
        }
        switch (templateMode) {
            case HTML:
                return forHTMLName(prefix, elementName);
            case XML:
                return forXMLName(prefix, elementName);
            case TEXT:
                return forTextName(prefix, elementName);
            case JAVASCRIPT:
                return forJavaScriptName(prefix, elementName);
            case CSS:
                return forCSSName(prefix, elementName);
            case RAW:
                throw new IllegalArgumentException("Element Definitions cannot be obtained for " + templateMode + " template mode ");
            default:
                throw new IllegalArgumentException("Unknown template mode " + templateMode);
        }
    }

    public ElementDefinition forName(TemplateMode templateMode, char[] elementName, int elementNameOffset, int elementNameLen) {
        if (templateMode == null) {
            throw new IllegalArgumentException("Template Mode cannot be null");
        }
        switch (templateMode) {
            case HTML:
                return forHTMLName(elementName, elementNameOffset, elementNameLen);
            case XML:
                return forXMLName(elementName, elementNameOffset, elementNameLen);
            case TEXT:
                return forTextName(elementName, elementNameOffset, elementNameLen);
            case JAVASCRIPT:
                return forJavaScriptName(elementName, elementNameOffset, elementNameLen);
            case CSS:
                return forCSSName(elementName, elementNameOffset, elementNameLen);
            case RAW:
                throw new IllegalArgumentException("Element Definitions cannot be obtained for " + templateMode + " template mode ");
            default:
                throw new IllegalArgumentException("Unknown template mode " + templateMode);
        }
    }

    public HTMLElementDefinition forHTMLName(String elementName) {
        if (elementName == null || elementName.length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (HTMLElementDefinition) this.htmlElementRepository.getElement(elementName);
    }

    public HTMLElementDefinition forHTMLName(String prefix, String elementName) {
        if (elementName == null || elementName.length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (HTMLElementDefinition) this.htmlElementRepository.getElement(prefix, elementName);
    }

    public HTMLElementDefinition forHTMLName(char[] elementName, int elementNameOffset, int elementNameLen) {
        if (elementName == null || elementNameLen == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (elementNameOffset < 0 || elementNameLen < 0) {
            throw new IllegalArgumentException("Both name offset and length must be equal to or greater than zero");
        }
        return (HTMLElementDefinition) this.htmlElementRepository.getElement(elementName, elementNameOffset, elementNameLen);
    }

    public XMLElementDefinition forXMLName(String elementName) {
        if (elementName == null || elementName.length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (XMLElementDefinition) this.xmlElementRepository.getElement(elementName);
    }

    public XMLElementDefinition forXMLName(String prefix, String elementName) {
        if (elementName == null || elementName.length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (XMLElementDefinition) this.xmlElementRepository.getElement(prefix, elementName);
    }

    public XMLElementDefinition forXMLName(char[] elementName, int elementNameOffset, int elementNameLen) {
        if (elementName == null || elementNameLen == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (elementNameOffset < 0 || elementNameLen < 0) {
            throw new IllegalArgumentException("Both name offset and length must be equal to or greater than zero");
        }
        return (XMLElementDefinition) this.xmlElementRepository.getElement(elementName, elementNameOffset, elementNameLen);
    }

    public TextElementDefinition forTextName(String elementName) {
        if (elementName == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        return (TextElementDefinition) this.textElementRepository.getElement(elementName);
    }

    public TextElementDefinition forTextName(String prefix, String elementName) {
        if (elementName == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        return (TextElementDefinition) this.textElementRepository.getElement(prefix, elementName);
    }

    public TextElementDefinition forTextName(char[] elementName, int elementNameOffset, int elementNameLen) {
        if (elementName == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        if (elementNameOffset < 0 || elementNameLen < 0) {
            throw new IllegalArgumentException("Both name offset and length must be equal to or greater than zero");
        }
        return (TextElementDefinition) this.textElementRepository.getElement(elementName, elementNameOffset, elementNameLen);
    }

    public TextElementDefinition forJavaScriptName(String elementName) {
        if (elementName == null) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (TextElementDefinition) this.javascriptElementRepository.getElement(elementName);
    }

    public TextElementDefinition forJavaScriptName(String prefix, String elementName) {
        if (elementName == null) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (TextElementDefinition) this.javascriptElementRepository.getElement(prefix, elementName);
    }

    public TextElementDefinition forJavaScriptName(char[] elementName, int elementNameOffset, int elementNameLen) {
        if (elementName == null) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (elementNameOffset < 0 || elementNameLen < 0) {
            throw new IllegalArgumentException("Both name offset and length must be equal to or greater than zero");
        }
        return (TextElementDefinition) this.javascriptElementRepository.getElement(elementName, elementNameOffset, elementNameLen);
    }

    public TextElementDefinition forCSSName(String elementName) {
        if (elementName == null) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (TextElementDefinition) this.cssElementRepository.getElement(elementName);
    }

    public TextElementDefinition forCSSName(String prefix, String elementName) {
        if (elementName == null) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (TextElementDefinition) this.cssElementRepository.getElement(prefix, elementName);
    }

    public TextElementDefinition forCSSName(char[] elementName, int elementNameOffset, int elementNameLen) {
        if (elementName == null) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (elementNameOffset < 0 || elementNameLen < 0) {
            throw new IllegalArgumentException("Both name offset and length must be equal to or greater than zero");
        }
        return (TextElementDefinition) this.cssElementRepository.getElement(elementName, elementNameOffset, elementNameLen);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/ElementDefinitions$ElementDefinitionRepository.class */
    public static final class ElementDefinitionRepository {
        private final TemplateMode templateMode;
        private final Map<TemplateMode, Set<IElementProcessor>> elementProcessorsByTemplateMode;
        private final List<String> standardRepositoryNames;
        private final List<ElementDefinition> standardRepository;
        private final List<String> repositoryNames;
        private final List<ElementDefinition> repository;
        private final ReadWriteLock lock = new ReentrantReadWriteLock(true);
        private final Lock readLock = this.lock.readLock();
        private final Lock writeLock = this.lock.writeLock();

        ElementDefinitionRepository(TemplateMode templateMode, Map<TemplateMode, Set<IElementProcessor>> elementProcessorsByTemplateMode) {
            this.templateMode = templateMode;
            this.elementProcessorsByTemplateMode = elementProcessorsByTemplateMode;
            this.standardRepositoryNames = templateMode == TemplateMode.HTML ? new ArrayList(150) : null;
            this.standardRepository = templateMode == TemplateMode.HTML ? new ArrayList(150) : null;
            this.repositoryNames = new ArrayList(150);
            this.repository = new ArrayList(150);
        }

        ElementDefinition getElement(char[] text, int offset, int len) {
            int index;
            if (this.standardRepository != null && (index = binarySearch(this.templateMode.isCaseSensitive(), this.standardRepositoryNames, text, offset, len)) >= 0) {
                return this.standardRepository.get(index);
            }
            this.readLock.lock();
            try {
                int index2 = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, text, offset, len);
                if (index2 >= 0) {
                    ElementDefinition elementDefinition = this.repository.get(index2);
                    this.readLock.unlock();
                    return elementDefinition;
                }
                this.readLock.unlock();
                this.writeLock.lock();
                try {
                    ElementDefinition storeElement = storeElement(text, offset, len);
                    this.writeLock.unlock();
                    return storeElement;
                } catch (Throwable th) {
                    this.writeLock.unlock();
                    throw th;
                }
            } catch (Throwable th2) {
                this.readLock.unlock();
                throw th2;
            }
        }

        ElementDefinition getElement(String completeElementName) {
            int index;
            if (this.standardRepository != null && (index = binarySearch(this.templateMode.isCaseSensitive(), this.standardRepositoryNames, completeElementName)) >= 0) {
                return this.standardRepository.get(index);
            }
            this.readLock.lock();
            try {
                int index2 = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, completeElementName);
                if (index2 >= 0) {
                    ElementDefinition elementDefinition = this.repository.get(index2);
                    this.readLock.unlock();
                    return elementDefinition;
                }
                this.readLock.unlock();
                this.writeLock.lock();
                try {
                    ElementDefinition storeElement = storeElement(completeElementName);
                    this.writeLock.unlock();
                    return storeElement;
                } catch (Throwable th) {
                    this.writeLock.unlock();
                    throw th;
                }
            } catch (Throwable th2) {
                this.readLock.unlock();
                throw th2;
            }
        }

        ElementDefinition getElement(String prefix, String elementName) {
            int index;
            if (this.standardRepository != null && (index = binarySearch(this.templateMode.isCaseSensitive(), this.standardRepositoryNames, prefix, elementName)) >= 0) {
                return this.standardRepository.get(index);
            }
            this.readLock.lock();
            try {
                int index2 = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, prefix, elementName);
                if (index2 >= 0) {
                    ElementDefinition elementDefinition = this.repository.get(index2);
                    this.readLock.unlock();
                    return elementDefinition;
                }
                this.readLock.unlock();
                this.writeLock.lock();
                try {
                    ElementDefinition storeElement = storeElement(prefix, elementName);
                    this.writeLock.unlock();
                    return storeElement;
                } catch (Throwable th) {
                    this.writeLock.unlock();
                    throw th;
                }
            } catch (Throwable th2) {
                this.readLock.unlock();
                throw th2;
            }
        }

        private ElementDefinition storeElement(char[] text, int offset, int len) {
            ElementDefinition elementDefinition;
            int index = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, text, offset, len);
            if (index >= 0) {
                return this.repository.get(index);
            }
            Set<IElementProcessor> elementProcessors = this.elementProcessorsByTemplateMode.get(this.templateMode);
            if (this.templateMode == TemplateMode.HTML) {
                elementDefinition = ElementDefinitions.buildHTMLElementDefinition(ElementNames.forHTMLName(text, offset, len), HTMLElementType.NORMAL, elementProcessors);
            } else {
                elementDefinition = this.templateMode == TemplateMode.XML ? ElementDefinitions.buildXMLElementDefinition(ElementNames.forXMLName(text, offset, len), elementProcessors) : ElementDefinitions.buildTextElementDefinition(this.templateMode, ElementNames.forTextName(text, offset, len), elementProcessors);
            }
            String[] completeElementNames = elementDefinition.elementName.completeElementNames;
            for (String completeElementName : completeElementNames) {
                int index2 = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, completeElementName);
                this.repositoryNames.add((index2 + 1) * (-1), completeElementName);
                this.repository.add((index2 + 1) * (-1), elementDefinition);
            }
            return elementDefinition;
        }

        private ElementDefinition storeElement(String text) {
            ElementDefinition elementDefinition;
            int index = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, text);
            if (index >= 0) {
                return this.repository.get(index);
            }
            Set<IElementProcessor> elementProcessors = this.elementProcessorsByTemplateMode.get(this.templateMode);
            if (this.templateMode == TemplateMode.HTML) {
                elementDefinition = ElementDefinitions.buildHTMLElementDefinition(ElementNames.forHTMLName(text), HTMLElementType.NORMAL, elementProcessors);
            } else {
                elementDefinition = this.templateMode == TemplateMode.XML ? ElementDefinitions.buildXMLElementDefinition(ElementNames.forXMLName(text), elementProcessors) : ElementDefinitions.buildTextElementDefinition(this.templateMode, ElementNames.forTextName(text), elementProcessors);
            }
            String[] completeElementNames = elementDefinition.elementName.completeElementNames;
            for (String completeElementName : completeElementNames) {
                int index2 = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, completeElementName);
                this.repositoryNames.add((index2 + 1) * (-1), completeElementName);
                this.repository.add((index2 + 1) * (-1), elementDefinition);
            }
            return elementDefinition;
        }

        private ElementDefinition storeElement(String prefix, String elementName) {
            ElementDefinition elementDefinition;
            int index = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, prefix, elementName);
            if (index >= 0) {
                return this.repository.get(index);
            }
            Set<IElementProcessor> elementProcessors = this.elementProcessorsByTemplateMode.get(this.templateMode);
            if (this.templateMode == TemplateMode.HTML) {
                elementDefinition = ElementDefinitions.buildHTMLElementDefinition(ElementNames.forHTMLName(prefix, elementName), HTMLElementType.NORMAL, elementProcessors);
            } else {
                elementDefinition = this.templateMode == TemplateMode.XML ? ElementDefinitions.buildXMLElementDefinition(ElementNames.forXMLName(prefix, elementName), elementProcessors) : ElementDefinitions.buildTextElementDefinition(this.templateMode, ElementNames.forTextName(prefix, elementName), elementProcessors);
            }
            String[] completeElementNames = elementDefinition.elementName.completeElementNames;
            for (String completeElementName : completeElementNames) {
                int index2 = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, completeElementName);
                this.repositoryNames.add((index2 + 1) * (-1), completeElementName);
                this.repository.add((index2 + 1) * (-1), elementDefinition);
            }
            return elementDefinition;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public ElementDefinition storeStandardElement(ElementDefinition elementDefinition) {
            String[] completeElementNames = elementDefinition.elementName.completeElementNames;
            for (String completeElementName : completeElementNames) {
                int index = binarySearch(this.templateMode.isCaseSensitive(), this.standardRepositoryNames, completeElementName);
                this.standardRepositoryNames.add((index + 1) * (-1), completeElementName);
                this.standardRepository.add((index + 1) * (-1), elementDefinition);
                int index2 = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, completeElementName);
                this.repositoryNames.add((index2 + 1) * (-1), completeElementName);
                this.repository.add((index2 + 1) * (-1), elementDefinition);
            }
            return elementDefinition;
        }

        private static int binarySearch(boolean caseSensitive, List<String> values, char[] text, int offset, int len) {
            int low = 0;
            int high = values.size() - 1;
            while (low <= high) {
                int mid = (low + high) >>> 1;
                String midVal = values.get(mid);
                int cmp = TextUtils.compareTo(caseSensitive, midVal, 0, midVal.length(), text, offset, len);
                if (cmp < 0) {
                    low = mid + 1;
                } else if (cmp > 0) {
                    high = mid - 1;
                } else {
                    return mid;
                }
            }
            return -(low + 1);
        }

        private static int binarySearch(boolean caseSensitive, List<String> values, String text) {
            int low = 0;
            int high = values.size() - 1;
            while (low <= high) {
                int mid = (low + high) >>> 1;
                String midVal = values.get(mid);
                int cmp = TextUtils.compareTo(caseSensitive, midVal, text);
                if (cmp < 0) {
                    low = mid + 1;
                } else if (cmp > 0) {
                    high = mid - 1;
                } else {
                    return mid;
                }
            }
            return -(low + 1);
        }

        private static int binarySearch(boolean caseSensitive, List<String> values, String prefix, String elementName) {
            if (prefix == null || prefix.trim().length() == 0) {
                return binarySearch(caseSensitive, values, elementName);
            }
            int prefixLen = prefix.length();
            int elementNameLen = elementName.length();
            int low = 0;
            int high = values.size() - 1;
            while (low <= high) {
                int mid = (low + high) >>> 1;
                String midVal = values.get(mid);
                int midValLen = midVal.length();
                if (TextUtils.startsWith(caseSensitive, midVal, prefix)) {
                    if (midValLen <= prefixLen) {
                        low = mid + 1;
                    } else {
                        int cmp = midVal.charAt(prefixLen) - ':';
                        if (cmp < 0) {
                            low = mid + 1;
                        } else if (cmp > 0) {
                            high = mid - 1;
                        } else {
                            int cmp2 = TextUtils.compareTo(caseSensitive, midVal, prefixLen + 1, midValLen - (prefixLen + 1), elementName, 0, elementNameLen);
                            if (cmp2 < 0) {
                                low = mid + 1;
                            } else if (cmp2 > 0) {
                                high = mid - 1;
                            } else {
                                return mid;
                            }
                        }
                    }
                } else {
                    int cmp3 = TextUtils.compareTo(caseSensitive, midVal, prefix);
                    if (cmp3 < 0) {
                        low = mid + 1;
                    } else if (cmp3 > 0) {
                        high = mid - 1;
                    } else {
                        throw new IllegalStateException("Bad comparison of midVal \"" + midVal + "\" and prefix \"" + prefix + "\"");
                    }
                }
            }
            return -(low + 1);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/ElementDefinitions$HTMLElementDefinitionSpec.class */
    private static final class HTMLElementDefinitionSpec {
        static final List<HTMLElementDefinitionSpec> ALL_SPECS = new ArrayList();
        HTMLElementName name;
        HTMLElementType type;

        HTMLElementDefinitionSpec(HTMLElementName name, HTMLElementType type) {
            this.name = name;
            this.type = type;
            ALL_SPECS.add(this);
        }
    }
}