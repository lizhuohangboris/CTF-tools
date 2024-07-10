package org.thymeleaf.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
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
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.processor.element.IElementProcessor;
import org.thymeleaf.processor.element.MatchingAttributeName;
import org.thymeleaf.processor.element.MatchingElementName;
import org.thymeleaf.spring5.processor.SpringInputGeneralFieldTagProcessor;
import org.thymeleaf.standard.processor.StandardXmlSpaceTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.TextUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/AttributeDefinitions.class */
public final class AttributeDefinitions {
    public static final Set<String> ALL_STANDARD_HTML_ATTRIBUTE_NAMES;
    private static final Set<String> ALL_STANDARD_BOOLEAN_HTML_ATTRIBUTE_NAMES;
    private final AttributeDefinitionRepository htmlAttributeRepository;
    private final AttributeDefinitionRepository xmlAttributeRepository;
    private final AttributeDefinitionRepository textAttributeRepository;
    private final AttributeDefinitionRepository javascriptAttributeRepository;
    private final AttributeDefinitionRepository cssAttributeRepository;

    static {
        List<String> htmlAttributeNameListAux = new ArrayList<>(Arrays.asList("abbr", "accept", "accept-charset", AbstractHtmlInputElementTag.ACCESSKEY_ATTRIBUTE, "action", "align", "alt", "archive", "async", InputTag.AUTOCOMPLETE_ATTRIBUTE, "autofocus", "autoplay", "axis", "border", "cellpadding", "cellspacing", "challenge", "char", "charoff", BasicAuthenticator.charsetparam, "checked", "cite", "class", "classid", "codebase", "codetype", TextareaTag.COLS_ATTRIBUTE, "colspan", FormTag.DEFAULT_COMMAND_NAME, "content", "contenteditable", "contextmenu", "controls", "coords", "data", SpringInputGeneralFieldTagProcessor.DATETIME_INPUT_TYPE_ATTR_VALUE, "declare", "default", "defer", AbstractHtmlElementTag.DIR_ATTRIBUTE, "disabled", "draggable", "dropzone", "enctype", "for", "form", "formaction", "formenctype", "formmethod", "formnovalidate", "formtarget", "frame", "headers", "height", SpringInputGeneralFieldTagProcessor.HIDDEN_INPUT_TYPE_ATTR_VALUE, "high", "href", "hreflang", "http-equiv", "icon", "id", "ismap", "keytype", "kind", "label", "lang", BeanDefinitionParserDelegate.LIST_ELEMENT, "longdesc", "loop", "low", "max", InputTag.MAXLENGTH_ATTRIBUTE, "media", "method", "min", "multiple", "muted", "name", "nohref", "novalidate", "nowrap", "onabort", "onafterprint", "onbeforeprint", "onbeforeunload", AbstractHtmlInputElementTag.ONBLUR_ATTRIBUTE, "oncanplay", "oncanplaythrough", AbstractHtmlInputElementTag.ONCHANGE_ATTRIBUTE, AbstractHtmlElementTag.ONCLICK_ATTRIBUTE, "oncontextmenu", "oncuechange", AbstractHtmlElementTag.ONDBLCLICK_ATTRIBUTE, "ondrag", "ondragend", "ondragenter", "ondragleave", "ondragover", "ondragstart", "ondrop", "ondurationchange", "onemptied", "onended", "onerror", AbstractHtmlInputElementTag.ONFOCUS_ATTRIBUTE, "onformchange", "onforminput", "onhaschange", "oninput", "oninvalid", AbstractHtmlElementTag.ONKEYDOWN_ATTRIBUTE, AbstractHtmlElementTag.ONKEYPRESS_ATTRIBUTE, AbstractHtmlElementTag.ONKEYUP_ATTRIBUTE, "onload", "onloadeddata", "onloadedmetadata", "onloadstart", "onmessage", AbstractHtmlElementTag.ONMOUSEDOWN_ATTRIBUTE, AbstractHtmlElementTag.ONMOUSEMOVE_ATTRIBUTE, AbstractHtmlElementTag.ONMOUSEOUT_ATTRIBUTE, AbstractHtmlElementTag.ONMOUSEOVER_ATTRIBUTE, AbstractHtmlElementTag.ONMOUSEUP_ATTRIBUTE, "onmousewheel", "onoffline", "ononline", "onpagehide", "onpageshow", "onpause", "onplay", "onplaying", "onpopstate", "onprogress", "onratechange", "onredo", "onreset", "onresize", "onscroll", "onseeked", "onseeking", "onselect", "onstalled", "onstorage", "onsubmit", "onsuspend", "ontimeupdate", "onundo", "onunload", "onvolumechange", "onwaiting", "open", "optimum", "pattern", "placeholder", "poster", "preload", DefaultBeanDefinitionDocumentReader.PROFILE_ATTRIBUTE, "pubdate", "radiogroup", AbstractHtmlInputElementTag.READONLY_ATTRIBUTE, "rel", DefaultBindingErrorProcessor.MISSING_FIELD_ERROR_CODE, "rev", "reversed", TextareaTag.ROWS_ATTRIBUTE, "rowspan", "rules", "scheme", "scope", "scoped", "seamless", "selected", "shape", InputTag.SIZE_ATTRIBUTE, ErrorsTag.SPAN_TAG, "spellcheck", "src", "srclang", "standby", "style", "summary", AbstractHtmlElementTag.TABINDEX_ATTRIBUTE, "title", "translate", "type", "usemap", "valign", "value", "valuetype", "width", "xml:lang", StandardXmlSpaceTagProcessor.TARGET_ATTR_NAME, "xmlns"));
        Collections.sort(htmlAttributeNameListAux);
        ALL_STANDARD_HTML_ATTRIBUTE_NAMES = Collections.unmodifiableSet(new LinkedHashSet(htmlAttributeNameListAux));
        Set<String> htmlBooleanAttributeNameSetAux = new HashSet<>(Arrays.asList("async", "autofocus", "autoplay", "checked", "controls", "declare", "default", "defer", "disabled", "formnovalidate", SpringInputGeneralFieldTagProcessor.HIDDEN_INPUT_TYPE_ATTR_VALUE, "ismap", "loop", "multiple", "novalidate", "nowrap", "open", "pubdate", AbstractHtmlInputElementTag.READONLY_ATTRIBUTE, DefaultBindingErrorProcessor.MISSING_FIELD_ERROR_CODE, "reversed", "selected", "scoped", "seamless"));
        ALL_STANDARD_BOOLEAN_HTML_ATTRIBUTE_NAMES = Collections.unmodifiableSet(new LinkedHashSet(htmlBooleanAttributeNameSetAux));
    }

    public AttributeDefinitions(Map<TemplateMode, Set<IElementProcessor>> elementProcessorsByTemplateMode) {
        List<HTMLAttributeDefinition> standardHTMLAttributeDefinitions = new ArrayList<>(ALL_STANDARD_HTML_ATTRIBUTE_NAMES.size() + 1);
        for (String attributeNameStr : ALL_STANDARD_HTML_ATTRIBUTE_NAMES) {
            standardHTMLAttributeDefinitions.add(buildHTMLAttributeDefinition(AttributeNames.forHTMLName(attributeNameStr), elementProcessorsByTemplateMode.get(TemplateMode.HTML)));
        }
        this.htmlAttributeRepository = new AttributeDefinitionRepository(TemplateMode.HTML, elementProcessorsByTemplateMode);
        this.xmlAttributeRepository = new AttributeDefinitionRepository(TemplateMode.XML, elementProcessorsByTemplateMode);
        this.textAttributeRepository = new AttributeDefinitionRepository(TemplateMode.TEXT, elementProcessorsByTemplateMode);
        this.javascriptAttributeRepository = new AttributeDefinitionRepository(TemplateMode.JAVASCRIPT, elementProcessorsByTemplateMode);
        this.cssAttributeRepository = new AttributeDefinitionRepository(TemplateMode.CSS, elementProcessorsByTemplateMode);
        for (HTMLAttributeDefinition attributeDefinition : standardHTMLAttributeDefinitions) {
            this.htmlAttributeRepository.storeStandardAttribute(attributeDefinition);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static HTMLAttributeDefinition buildHTMLAttributeDefinition(HTMLAttributeName name, Set<IElementProcessor> elementProcessors) {
        String[] completeAttributeNames;
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
                    if (matchingAttributeName != null && !matchingAttributeName.isMatchingAllAttributes() && matchingAttributeName.matches(name)) {
                        associatedProcessors.add(processor);
                    }
                }
            }
        }
        boolean booleanAttribute = false;
        for (String completeAttributeName : name.getCompleteAttributeNames()) {
            if (ALL_STANDARD_BOOLEAN_HTML_ATTRIBUTE_NAMES.contains(completeAttributeName)) {
                booleanAttribute = true;
            }
        }
        return new HTMLAttributeDefinition(name, booleanAttribute, associatedProcessors);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static XMLAttributeDefinition buildXMLAttributeDefinition(XMLAttributeName name, Set<IElementProcessor> elementProcessors) {
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
                    if (matchingAttributeName != null && !matchingAttributeName.isMatchingAllAttributes() && matchingAttributeName.matches(name)) {
                        associatedProcessors.add(processor);
                    }
                }
            }
        }
        return new XMLAttributeDefinition(name, associatedProcessors);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static TextAttributeDefinition buildTextAttributeDefinition(TemplateMode templateMode, TextAttributeName name, Set<IElementProcessor> elementProcessors) {
        Set<IElementProcessor> associatedProcessors = new LinkedHashSet<>(2);
        if (elementProcessors != null) {
            for (IElementProcessor processor : elementProcessors) {
                if (processor.getTemplateMode() == templateMode) {
                    MatchingElementName matchingElementName = processor.getMatchingElementName();
                    MatchingAttributeName matchingAttributeName = processor.getMatchingAttributeName();
                    if ((matchingElementName != null && matchingElementName.getTemplateMode() != templateMode) || (matchingAttributeName != null && matchingAttributeName.getTemplateMode() != templateMode)) {
                        throw new ConfigurationException(templateMode + " processors must return " + templateMode + "element names and " + templateMode + " attribute names (processor: " + processor.getClass().getName() + ")");
                    }
                    if (matchingAttributeName != null && !matchingAttributeName.isMatchingAllAttributes() && matchingAttributeName.matches(name)) {
                        associatedProcessors.add(processor);
                    }
                }
            }
        }
        return new TextAttributeDefinition(name, associatedProcessors);
    }

    public AttributeDefinition forName(TemplateMode templateMode, String attributeName) {
        if (templateMode == null) {
            throw new IllegalArgumentException("Template Mode cannot be null");
        }
        switch (templateMode) {
            case HTML:
                return forHTMLName(attributeName);
            case XML:
                return forXMLName(attributeName);
            case TEXT:
                return forTextName(attributeName);
            case JAVASCRIPT:
                return forJavaScriptName(attributeName);
            case CSS:
                return forCSSName(attributeName);
            case RAW:
                throw new IllegalArgumentException("Attribute Definitions cannot be obtained for " + templateMode + " template mode ");
            default:
                throw new IllegalArgumentException("Unknown template mode " + templateMode);
        }
    }

    public AttributeDefinition forName(TemplateMode templateMode, String prefix, String attributeName) {
        if (templateMode == null) {
            throw new IllegalArgumentException("Template Mode cannot be null");
        }
        switch (templateMode) {
            case HTML:
                return forHTMLName(prefix, attributeName);
            case XML:
                return forXMLName(prefix, attributeName);
            case TEXT:
                return forTextName(prefix, attributeName);
            case JAVASCRIPT:
                return forJavaScriptName(prefix, attributeName);
            case CSS:
                return forCSSName(prefix, attributeName);
            case RAW:
                throw new IllegalArgumentException("Attribute Definitions cannot be obtained for " + templateMode + " template mode ");
            default:
                throw new IllegalArgumentException("Unknown template mode " + templateMode);
        }
    }

    public AttributeDefinition forName(TemplateMode templateMode, char[] attributeName, int attributeNameOffset, int attributeNameLen) {
        if (templateMode == null) {
            throw new IllegalArgumentException("Template Mode cannot be null");
        }
        switch (templateMode) {
            case HTML:
                return forHTMLName(attributeName, attributeNameOffset, attributeNameLen);
            case XML:
                return forXMLName(attributeName, attributeNameOffset, attributeNameLen);
            case TEXT:
                return forTextName(attributeName, attributeNameOffset, attributeNameLen);
            case JAVASCRIPT:
                return forJavaScriptName(attributeName, attributeNameOffset, attributeNameLen);
            case CSS:
                return forCSSName(attributeName, attributeNameOffset, attributeNameLen);
            case RAW:
                throw new IllegalArgumentException("Attribute Definitions cannot be obtained for " + templateMode + " template mode ");
            default:
                throw new IllegalArgumentException("Unknown template mode " + templateMode);
        }
    }

    public HTMLAttributeDefinition forHTMLName(String attributeName) {
        if (attributeName == null || attributeName.length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (HTMLAttributeDefinition) this.htmlAttributeRepository.getAttribute(attributeName);
    }

    public HTMLAttributeDefinition forHTMLName(String prefix, String attributeName) {
        if (attributeName == null || attributeName.length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (HTMLAttributeDefinition) this.htmlAttributeRepository.getAttribute(prefix, attributeName);
    }

    public HTMLAttributeDefinition forHTMLName(char[] attributeName, int attributeNameOffset, int attributeNameLen) {
        if (attributeName == null || attributeNameLen == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (attributeNameOffset < 0 || attributeNameLen < 0) {
            throw new IllegalArgumentException("Both name offset and length must be equal to or greater than zero");
        }
        return (HTMLAttributeDefinition) this.htmlAttributeRepository.getAttribute(attributeName, attributeNameOffset, attributeNameLen);
    }

    public XMLAttributeDefinition forXMLName(String attributeName) {
        if (attributeName == null || attributeName.length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (XMLAttributeDefinition) this.xmlAttributeRepository.getAttribute(attributeName);
    }

    public XMLAttributeDefinition forXMLName(String prefix, String attributeName) {
        if (attributeName == null || attributeName.length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (XMLAttributeDefinition) this.xmlAttributeRepository.getAttribute(prefix, attributeName);
    }

    public XMLAttributeDefinition forXMLName(char[] attributeName, int attributeNameOffset, int attributeNameLen) {
        if (attributeName == null || attributeNameLen == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (attributeNameOffset < 0 || attributeNameLen < 0) {
            throw new IllegalArgumentException("Both name offset and length must be equal to or greater than zero");
        }
        return (XMLAttributeDefinition) this.xmlAttributeRepository.getAttribute(attributeName, attributeNameOffset, attributeNameLen);
    }

    public TextAttributeDefinition forTextName(String attributeName) {
        if (attributeName == null || attributeName.length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (TextAttributeDefinition) this.textAttributeRepository.getAttribute(attributeName);
    }

    public TextAttributeDefinition forTextName(String prefix, String attributeName) {
        if (attributeName == null || attributeName.length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (TextAttributeDefinition) this.textAttributeRepository.getAttribute(prefix, attributeName);
    }

    public TextAttributeDefinition forTextName(char[] attributeName, int attributeNameOffset, int attributeNameLen) {
        if (attributeName == null || attributeNameLen == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (attributeNameOffset < 0 || attributeNameLen < 0) {
            throw new IllegalArgumentException("Both name offset and length must be equal to or greater than zero");
        }
        return (TextAttributeDefinition) this.textAttributeRepository.getAttribute(attributeName, attributeNameOffset, attributeNameLen);
    }

    public TextAttributeDefinition forJavaScriptName(String attributeName) {
        if (attributeName == null || attributeName.length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (TextAttributeDefinition) this.javascriptAttributeRepository.getAttribute(attributeName);
    }

    public TextAttributeDefinition forJavaScriptName(String prefix, String attributeName) {
        if (attributeName == null || attributeName.length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (TextAttributeDefinition) this.javascriptAttributeRepository.getAttribute(prefix, attributeName);
    }

    public TextAttributeDefinition forJavaScriptName(char[] attributeName, int attributeNameOffset, int attributeNameLen) {
        if (attributeName == null || attributeNameLen == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (attributeNameOffset < 0 || attributeNameLen < 0) {
            throw new IllegalArgumentException("Both name offset and length must be equal to or greater than zero");
        }
        return (TextAttributeDefinition) this.javascriptAttributeRepository.getAttribute(attributeName, attributeNameOffset, attributeNameLen);
    }

    public TextAttributeDefinition forCSSName(String attributeName) {
        if (attributeName == null || attributeName.length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (TextAttributeDefinition) this.cssAttributeRepository.getAttribute(attributeName);
    }

    public TextAttributeDefinition forCSSName(String prefix, String attributeName) {
        if (attributeName == null || attributeName.length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (TextAttributeDefinition) this.cssAttributeRepository.getAttribute(prefix, attributeName);
    }

    public TextAttributeDefinition forCSSName(char[] attributeName, int attributeNameOffset, int attributeNameLen) {
        if (attributeName == null || attributeNameLen == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (attributeNameOffset < 0 || attributeNameLen < 0) {
            throw new IllegalArgumentException("Both name offset and length must be equal to or greater than zero");
        }
        return (TextAttributeDefinition) this.cssAttributeRepository.getAttribute(attributeName, attributeNameOffset, attributeNameLen);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/AttributeDefinitions$AttributeDefinitionRepository.class */
    public static final class AttributeDefinitionRepository {
        private final TemplateMode templateMode;
        private final Map<TemplateMode, Set<IElementProcessor>> elementProcessorsByTemplateMode;
        private final List<String> standardRepositoryNames;
        private final List<AttributeDefinition> standardRepository;
        private final List<String> repositoryNames;
        private final List<AttributeDefinition> repository;
        private final ReadWriteLock lock = new ReentrantReadWriteLock(true);
        private final Lock readLock = this.lock.readLock();
        private final Lock writeLock = this.lock.writeLock();

        AttributeDefinitionRepository(TemplateMode templateMode, Map<TemplateMode, Set<IElementProcessor>> elementProcessorsByTemplateMode) {
            this.templateMode = templateMode;
            this.elementProcessorsByTemplateMode = elementProcessorsByTemplateMode;
            this.standardRepositoryNames = templateMode == TemplateMode.HTML ? new ArrayList(150) : null;
            this.standardRepository = templateMode == TemplateMode.HTML ? new ArrayList(150) : null;
            this.repositoryNames = new ArrayList(500);
            this.repository = new ArrayList(500);
        }

        AttributeDefinition getAttribute(char[] text, int offset, int len) {
            int index;
            if (this.standardRepository != null && (index = binarySearch(this.templateMode.isCaseSensitive(), this.standardRepositoryNames, text, offset, len)) >= 0) {
                return this.standardRepository.get(index);
            }
            this.readLock.lock();
            try {
                int index2 = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, text, offset, len);
                if (index2 >= 0) {
                    AttributeDefinition attributeDefinition = this.repository.get(index2);
                    this.readLock.unlock();
                    return attributeDefinition;
                }
                this.readLock.unlock();
                this.writeLock.lock();
                try {
                    AttributeDefinition storeAttribute = storeAttribute(text, offset, len);
                    this.writeLock.unlock();
                    return storeAttribute;
                } catch (Throwable th) {
                    this.writeLock.unlock();
                    throw th;
                }
            } catch (Throwable th2) {
                this.readLock.unlock();
                throw th2;
            }
        }

        AttributeDefinition getAttribute(String completeAttributeName) {
            int index;
            if (this.standardRepository != null && (index = binarySearch(this.templateMode.isCaseSensitive(), this.standardRepositoryNames, completeAttributeName)) >= 0) {
                return this.standardRepository.get(index);
            }
            this.readLock.lock();
            try {
                int index2 = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, completeAttributeName);
                if (index2 >= 0) {
                    AttributeDefinition attributeDefinition = this.repository.get(index2);
                    this.readLock.unlock();
                    return attributeDefinition;
                }
                this.readLock.unlock();
                this.writeLock.lock();
                try {
                    AttributeDefinition storeAttribute = storeAttribute(completeAttributeName);
                    this.writeLock.unlock();
                    return storeAttribute;
                } catch (Throwable th) {
                    this.writeLock.unlock();
                    throw th;
                }
            } catch (Throwable th2) {
                this.readLock.unlock();
                throw th2;
            }
        }

        AttributeDefinition getAttribute(String prefix, String attributeName) {
            int index;
            if (this.standardRepository != null && (index = binarySearch(this.templateMode.isCaseSensitive(), this.standardRepositoryNames, prefix, attributeName)) >= 0) {
                return this.standardRepository.get(index);
            }
            this.readLock.lock();
            try {
                int index2 = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, prefix, attributeName);
                if (index2 >= 0) {
                    AttributeDefinition attributeDefinition = this.repository.get(index2);
                    this.readLock.unlock();
                    return attributeDefinition;
                }
                this.readLock.unlock();
                this.writeLock.lock();
                try {
                    AttributeDefinition storeAttribute = storeAttribute(prefix, attributeName);
                    this.writeLock.unlock();
                    return storeAttribute;
                } catch (Throwable th) {
                    this.writeLock.unlock();
                    throw th;
                }
            } catch (Throwable th2) {
                this.readLock.unlock();
                throw th2;
            }
        }

        private AttributeDefinition storeAttribute(char[] text, int offset, int len) {
            AttributeDefinition attributeDefinition;
            int index = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, text, offset, len);
            if (index >= 0) {
                return this.repository.get(index);
            }
            Set<IElementProcessor> elementProcessors = this.elementProcessorsByTemplateMode.get(this.templateMode);
            if (this.templateMode == TemplateMode.HTML) {
                attributeDefinition = AttributeDefinitions.buildHTMLAttributeDefinition(AttributeNames.forHTMLName(text, offset, len), elementProcessors);
            } else {
                attributeDefinition = this.templateMode == TemplateMode.XML ? AttributeDefinitions.buildXMLAttributeDefinition(AttributeNames.forXMLName(text, offset, len), elementProcessors) : AttributeDefinitions.buildTextAttributeDefinition(this.templateMode, AttributeNames.forTextName(text, offset, len), elementProcessors);
            }
            String[] completeAttributeNames = attributeDefinition.attributeName.completeAttributeNames;
            for (String completeAttributeName : completeAttributeNames) {
                int index2 = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, completeAttributeName);
                this.repositoryNames.add((index2 + 1) * (-1), completeAttributeName);
                this.repository.add((index2 + 1) * (-1), attributeDefinition);
            }
            return attributeDefinition;
        }

        private AttributeDefinition storeAttribute(String attributeName) {
            AttributeDefinition attributeDefinition;
            int index = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, attributeName);
            if (index >= 0) {
                return this.repository.get(index);
            }
            Set<IElementProcessor> elementProcessors = this.elementProcessorsByTemplateMode.get(this.templateMode);
            if (this.templateMode == TemplateMode.HTML) {
                attributeDefinition = AttributeDefinitions.buildHTMLAttributeDefinition(AttributeNames.forHTMLName(attributeName), elementProcessors);
            } else {
                attributeDefinition = this.templateMode == TemplateMode.XML ? AttributeDefinitions.buildXMLAttributeDefinition(AttributeNames.forXMLName(attributeName), elementProcessors) : AttributeDefinitions.buildTextAttributeDefinition(this.templateMode, AttributeNames.forTextName(attributeName), elementProcessors);
            }
            String[] completeAttributeNames = attributeDefinition.attributeName.completeAttributeNames;
            for (String completeAttributeName : completeAttributeNames) {
                int index2 = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, completeAttributeName);
                this.repositoryNames.add((index2 + 1) * (-1), completeAttributeName);
                this.repository.add((index2 + 1) * (-1), attributeDefinition);
            }
            return attributeDefinition;
        }

        private AttributeDefinition storeAttribute(String prefix, String attributeName) {
            AttributeDefinition attributeDefinition;
            int index = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, prefix, attributeName);
            if (index >= 0) {
                return this.repository.get(index);
            }
            Set<IElementProcessor> elementProcessors = this.elementProcessorsByTemplateMode.get(this.templateMode);
            if (this.templateMode == TemplateMode.HTML) {
                attributeDefinition = AttributeDefinitions.buildHTMLAttributeDefinition(AttributeNames.forHTMLName(prefix, attributeName), elementProcessors);
            } else {
                attributeDefinition = this.templateMode == TemplateMode.XML ? AttributeDefinitions.buildXMLAttributeDefinition(AttributeNames.forXMLName(prefix, attributeName), elementProcessors) : AttributeDefinitions.buildTextAttributeDefinition(this.templateMode, AttributeNames.forTextName(prefix, attributeName), elementProcessors);
            }
            String[] completeAttributeNames = attributeDefinition.attributeName.completeAttributeNames;
            for (String completeAttributeName : completeAttributeNames) {
                int index2 = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, completeAttributeName);
                this.repositoryNames.add((index2 + 1) * (-1), completeAttributeName);
                this.repository.add((index2 + 1) * (-1), attributeDefinition);
            }
            return attributeDefinition;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public AttributeDefinition storeStandardAttribute(AttributeDefinition attributeDefinition) {
            String[] completeAttributeNames = attributeDefinition.attributeName.completeAttributeNames;
            for (String completeAttributeName : completeAttributeNames) {
                int index = binarySearch(this.templateMode.isCaseSensitive(), this.standardRepositoryNames, completeAttributeName);
                this.standardRepositoryNames.add((index + 1) * (-1), completeAttributeName);
                this.standardRepository.add((index + 1) * (-1), attributeDefinition);
                int index2 = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, completeAttributeName);
                this.repositoryNames.add((index2 + 1) * (-1), completeAttributeName);
                this.repository.add((index2 + 1) * (-1), attributeDefinition);
            }
            return attributeDefinition;
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

        private static int binarySearch(boolean caseSensitive, List<String> values, String prefix, String attributeName) {
            if (prefix == null || prefix.trim().length() == 0) {
                return binarySearch(caseSensitive, values, attributeName);
            }
            int prefixLen = prefix.length();
            int attributeNameLen = attributeName.length();
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
                            int cmp2 = TextUtils.compareTo(caseSensitive, midVal, prefixLen + 1, midValLen - (prefixLen + 1), attributeName, 0, attributeNameLen);
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
}