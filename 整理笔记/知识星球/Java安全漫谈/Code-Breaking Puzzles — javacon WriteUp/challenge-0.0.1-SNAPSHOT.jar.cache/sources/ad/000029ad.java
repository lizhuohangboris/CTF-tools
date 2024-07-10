package org.thymeleaf.templateparser.markup;

import java.util.concurrent.ConcurrentHashMap;
import org.attoparser.select.IMarkupSelectorReferenceResolver;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateparser/markup/TemplateFragmentMarkupReferenceResolver.class */
final class TemplateFragmentMarkupReferenceResolver implements IMarkupSelectorReferenceResolver {
    private static final String HTML_FORMAT_WITHOUT_PREFIX = "/[ref='%1$s' or data-ref='%1$s' or fragment='%1$s' or data-fragment='%1$s' or fragment^='%1$s(' or data-fragment^='%1$s(' or fragment^='%1$s (' or data-fragment^='%1$s (']";
    private static final String HTML_FORMAT_WITH_PREFIX = "/[%1$s:ref='%%1$s' or data-%1$s-ref='%%1$s' or %1$s:fragment='%%1$s' or data-%1$s-fragment='%%1$s' or %1$s:fragment^='%%1$s(' or data-%1$s-fragment^='%%1$s(' or %1$s:fragment^='%%1$s (' or data-%1$s-fragment^='%%1$s (']";
    private static final String XML_FORMAT_WITHOUT_PREFIX = "/[ref='%1$s' or fragment='%1$s' or fragment^='%1$s(' or fragment^='%1$s (']";
    private static final String XML_FORMAT_WITH_PREFIX = "/[%1$s:ref='%%1$s' or %1$s:fragment='%%1$s' or %1$s:fragment^='%%1$s(' or %1$s:fragment^='%%1$s (']";
    private final ConcurrentHashMap<String, String> selectorsByReference = new ConcurrentHashMap<>(20);
    private final String resolverFormat;
    private static final TemplateFragmentMarkupReferenceResolver HTML_INSTANCE_NO_PREFIX = new TemplateFragmentMarkupReferenceResolver(true, null);
    private static final TemplateFragmentMarkupReferenceResolver XML_INSTANCE_NO_PREFIX = new TemplateFragmentMarkupReferenceResolver(false, null);
    private static final ConcurrentHashMap<String, TemplateFragmentMarkupReferenceResolver> HTML_INSTANCES_BY_PREFIX = new ConcurrentHashMap<>(3, 0.9f, 2);
    private static final ConcurrentHashMap<String, TemplateFragmentMarkupReferenceResolver> XML_INSTANCES_BY_PREFIX = new ConcurrentHashMap<>(3, 0.9f, 2);

    /* JADX INFO: Access modifiers changed from: package-private */
    public static TemplateFragmentMarkupReferenceResolver forPrefix(boolean html, String standardDialectPrefix) {
        return html ? forHTMLPrefix(standardDialectPrefix) : forXMLPrefix(standardDialectPrefix);
    }

    private static TemplateFragmentMarkupReferenceResolver forHTMLPrefix(String standardDialectPrefix) {
        if (standardDialectPrefix == null || standardDialectPrefix.length() == 0) {
            return HTML_INSTANCE_NO_PREFIX;
        }
        String prefix = standardDialectPrefix.toLowerCase();
        TemplateFragmentMarkupReferenceResolver resolver = HTML_INSTANCES_BY_PREFIX.get(prefix);
        if (resolver != null) {
            return resolver;
        }
        TemplateFragmentMarkupReferenceResolver newResolver = new TemplateFragmentMarkupReferenceResolver(true, prefix);
        HTML_INSTANCES_BY_PREFIX.putIfAbsent(prefix, newResolver);
        return HTML_INSTANCES_BY_PREFIX.get(prefix);
    }

    private static TemplateFragmentMarkupReferenceResolver forXMLPrefix(String standardDialectPrefix) {
        if (standardDialectPrefix == null || standardDialectPrefix.length() == 0) {
            return XML_INSTANCE_NO_PREFIX;
        }
        TemplateFragmentMarkupReferenceResolver resolver = XML_INSTANCES_BY_PREFIX.get(standardDialectPrefix);
        if (resolver != null) {
            return resolver;
        }
        TemplateFragmentMarkupReferenceResolver newResolver = new TemplateFragmentMarkupReferenceResolver(false, standardDialectPrefix);
        XML_INSTANCES_BY_PREFIX.putIfAbsent(standardDialectPrefix, newResolver);
        return XML_INSTANCES_BY_PREFIX.get(standardDialectPrefix);
    }

    private TemplateFragmentMarkupReferenceResolver(boolean html, String standardDialectPrefix) {
        if (standardDialectPrefix == null) {
            this.resolverFormat = html ? HTML_FORMAT_WITHOUT_PREFIX : XML_FORMAT_WITHOUT_PREFIX;
        } else {
            this.resolverFormat = html ? String.format(HTML_FORMAT_WITH_PREFIX, standardDialectPrefix) : String.format(XML_FORMAT_WITH_PREFIX, standardDialectPrefix);
        }
    }

    @Override // org.attoparser.select.IMarkupSelectorReferenceResolver
    public String resolveSelectorFromReference(String reference) {
        Validate.notNull(reference, "Reference cannot be null");
        String selector = this.selectorsByReference.get(reference);
        if (selector != null) {
            return selector;
        }
        String newSelector = String.format(this.resolverFormat, reference);
        this.selectorsByReference.put(reference, newSelector);
        return newSelector;
    }
}