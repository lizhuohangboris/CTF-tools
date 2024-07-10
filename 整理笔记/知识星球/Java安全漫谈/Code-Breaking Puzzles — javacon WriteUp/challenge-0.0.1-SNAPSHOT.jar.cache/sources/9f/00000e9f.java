package org.attoparser.select;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.attoparser.select.MarkupSelectorItem;
import org.springframework.aop.framework.autoproxy.target.QuickTargetSourceCreator;
import org.springframework.asm.Opcodes;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/select/MarkupSelectorItems.class */
final class MarkupSelectorItems {
    private static final SelectorRepository NO_REFERENCE_RESOLVER_REPOSITORY = new SelectorRepository();
    private static final ConcurrentHashMap<IMarkupSelectorReferenceResolver, SelectorRepository> REPOSITORIES_BY_REFERENCE_RESOLVER = new ConcurrentHashMap<>(20);
    private static final String selectorPatternStr = "^(/{1,2})([^/\\s]*?)(\\[(?:.*)\\])?$";
    private static final Pattern selectorPattern = Pattern.compile(selectorPatternStr);
    private static final String modifiersPatternStr = "^(?:\\[(.*?)\\])(\\[(?:.*)\\])?$";
    private static final Pattern modifiersPattern = Pattern.compile(modifiersPatternStr);

    /* JADX INFO: Access modifiers changed from: package-private */
    public static List<IMarkupSelectorItem> forSelector(boolean html, String selector, IMarkupSelectorReferenceResolver referenceResolver) {
        ConcurrentHashMap<String, List<IMarkupSelectorItem>> map;
        if (isEmptyOrWhitespace(selector)) {
            throw new IllegalArgumentException("Selector cannot be null");
        }
        if (referenceResolver == null) {
            map = NO_REFERENCE_RESOLVER_REPOSITORY.getMap(html);
        } else {
            if (!REPOSITORIES_BY_REFERENCE_RESOLVER.containsKey(referenceResolver) && REPOSITORIES_BY_REFERENCE_RESOLVER.size() < 1000) {
                REPOSITORIES_BY_REFERENCE_RESOLVER.putIfAbsent(referenceResolver, new SelectorRepository());
            }
            map = REPOSITORIES_BY_REFERENCE_RESOLVER.get(referenceResolver).getMap(html);
        }
        List<IMarkupSelectorItem> items = map.get(selector);
        if (items != null) {
            return items;
        }
        List<IMarkupSelectorItem> items2 = Collections.unmodifiableList(parseSelector(html, selector, referenceResolver));
        if (map.size() < 1000) {
            map.putIfAbsent(selector, items2);
        }
        return items2;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/select/MarkupSelectorItems$SelectorRepository.class */
    static final class SelectorRepository {
        private static final int SELECTOR_ITEMS_MAX_SIZE = 1000;
        private final ConcurrentHashMap<String, List<IMarkupSelectorItem>> CASE_INSENSITIVE_SELECTOR_ITEMS = new ConcurrentHashMap<>(20);
        private final ConcurrentHashMap<String, List<IMarkupSelectorItem>> CASE_SENSITIVE_SELECTOR_ITEMS = new ConcurrentHashMap<>(20);

        SelectorRepository() {
        }

        ConcurrentHashMap<String, List<IMarkupSelectorItem>> getMap(boolean html) {
            return html ? this.CASE_INSENSITIVE_SELECTOR_ITEMS : this.CASE_SENSITIVE_SELECTOR_ITEMS;
        }
    }

    static List<IMarkupSelectorItem> parseSelector(boolean html, String selector, IMarkupSelectorReferenceResolver referenceResolver) {
        return parseSelector(html, selector, null, null, referenceResolver);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static List<IMarkupSelectorItem> parseSelector(boolean html, String selector, MarkupSelectorItem.IAttributeCondition initialAttributeCondition, MarkupSelectorItem.IndexCondition initialIndexCondition, IMarkupSelectorReferenceResolver referenceResolver) {
        List<IMarkupSelectorItem> result;
        boolean anyLevel;
        String lowerCase;
        String selectorSpecStr = selector.trim();
        if (!selectorSpecStr.startsWith("/")) {
            selectorSpecStr = "//" + selectorSpecStr;
        }
        int selectorSpecStrLen = selectorSpecStr.length();
        int firstNonSlash = 0;
        while (firstNonSlash < selectorSpecStrLen && selectorSpecStr.charAt(firstNonSlash) == '/') {
            firstNonSlash++;
        }
        if (firstNonSlash >= selectorSpecStrLen) {
            throw new IllegalArgumentException("Invalid syntax in selector \"" + selector + "\": '/' should be followed by further selector specification");
        }
        int selEnd = selectorSpecStr.substring(firstNonSlash).indexOf(47);
        if (selEnd != -1) {
            String tail = selectorSpecStr.substring(firstNonSlash).substring(selEnd);
            selectorSpecStr = selectorSpecStr.substring(0, firstNonSlash + selEnd);
            result = parseSelector(html, tail, referenceResolver);
        } else {
            result = new ArrayList<>(3);
        }
        Matcher matcher = selectorPattern.matcher(selectorSpecStr);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid syntax in selector \"" + selector + "\": selector does not match selector syntax: ((/|//)?selector)?([@attrib=\"value\" ((and|or) @attrib2=\"value\")?])?([index])?");
        }
        String rootGroup = matcher.group(1);
        String selectorNameGroup = matcher.group(2);
        String modifiersGroup = matcher.group(3);
        if (rootGroup == null) {
            throw new IllegalArgumentException("Invalid syntax in selector \"" + selector + "\": selector does not match selector syntax: ((/|//)?selector)?([@attrib=\"value\" ((and|or) @attrib2=\"value\")?])?([index])?");
        }
        if ("//".equals(rootGroup)) {
            anyLevel = true;
        } else if ("/".equals(rootGroup)) {
            anyLevel = false;
        } else {
            throw new IllegalArgumentException("Invalid syntax in selector \"" + selector + "\": selector does not match selector syntax: ((/|//)?selector)?([@attrib=\"value\" ((and|or) @attrib2=\"value\")?])?([index])?");
        }
        if (selectorNameGroup == null) {
            throw new IllegalArgumentException("Invalid syntax in selector \"" + selector + "\": selector does not match selector syntax: ((/|//)?selector)?([@attrib=\"value\" ((and|or) @attrib2=\"value\")?])?([index])?");
        }
        String path = selectorNameGroup;
        MarkupSelectorItem.IndexCondition index = initialIndexCondition;
        MarkupSelectorItem.IAttributeCondition attributeCondition = initialAttributeCondition;
        int idModifierPos = html ? path.indexOf("#") : -1;
        int classModifierPos = html ? path.indexOf(".") : -1;
        int referenceModifierPos = path.indexOf(QuickTargetSourceCreator.PREFIX_THREAD_LOCAL);
        MarkupSelectorItem.IAttributeCondition attributeCondition2 = attributeCondition;
        if (idModifierPos != -1) {
            if (classModifierPos != -1 || referenceModifierPos != -1) {
                throw new IllegalArgumentException("More than one modifier (id, class, reference) have been specified at selector expression \"" + selector + "\", which is forbidden.");
            }
            String selectorPathIdModifier = path.substring(idModifierPos + "#".length());
            path = path.substring(0, idModifierPos);
            if (isEmptyOrWhitespace(selectorPathIdModifier)) {
                throw new IllegalArgumentException("Empty id modifier in selector expression \"" + selector + "\", which is forbidden.");
            }
            MarkupSelectorItem.IAttributeCondition newAttributeCondition = new MarkupSelectorItem.AttributeCondition("id", MarkupSelectorItem.AttributeCondition.Operator.EQUALS, selectorPathIdModifier);
            if (attributeCondition == null) {
                attributeCondition2 = newAttributeCondition;
            } else {
                attributeCondition2 = new MarkupSelectorItem.AttributeConditionRelation(MarkupSelectorItem.AttributeConditionRelation.Type.AND, attributeCondition, newAttributeCondition);
            }
        }
        MarkupSelectorItem.IAttributeCondition attributeCondition3 = attributeCondition2;
        if (classModifierPos != -1) {
            if (idModifierPos != -1 || referenceModifierPos != -1) {
                throw new IllegalArgumentException("More than one modifier (id, class, reference) have been specified at selector expression \"" + selector + "\", which is forbidden.");
            }
            String selectorPathClassModifier = path.substring(classModifierPos + ".".length());
            path = path.substring(0, classModifierPos);
            if (isEmptyOrWhitespace(selectorPathClassModifier)) {
                throw new IllegalArgumentException("Empty id modifier in selector expression \"" + selector + "\", which is forbidden.");
            }
            MarkupSelectorItem.IAttributeCondition newAttributeCondition2 = new MarkupSelectorItem.AttributeCondition("class", MarkupSelectorItem.AttributeCondition.Operator.EQUALS, selectorPathClassModifier);
            if (attributeCondition2 == null) {
                attributeCondition3 = newAttributeCondition2;
            } else {
                attributeCondition3 = new MarkupSelectorItem.AttributeConditionRelation(MarkupSelectorItem.AttributeConditionRelation.Type.AND, attributeCondition2, newAttributeCondition2);
            }
        }
        String selectorPathReferenceModifier = null;
        if (referenceModifierPos != -1) {
            if (idModifierPos != -1 || classModifierPos != -1) {
                throw new IllegalArgumentException("More than one modifier (id, class, reference) have been specified at selector expression \"" + selector + "\", which is forbidden.");
            }
            selectorPathReferenceModifier = path.substring(referenceModifierPos + QuickTargetSourceCreator.PREFIX_THREAD_LOCAL.length());
            path = path.substring(0, referenceModifierPos);
            if (isEmptyOrWhitespace(selectorPathReferenceModifier)) {
                throw new IllegalArgumentException("Empty id modifier in selector expression \"" + selector + "\", which is forbidden.");
            }
        }
        boolean contentSelector = "content()".equals(path);
        boolean textSelector = "text()".equals(path);
        boolean commentSelector = "comment()".equals(path);
        boolean cdataSectionSelector = "cdata()".equals(path);
        boolean docTypeClauseSelector = "doctype()".equals(path);
        boolean xmlDeclarationSelector = "xmldecl()".equals(path);
        boolean processingInstructionSelector = "procinstr()".equals(path);
        boolean isNonElementSelector = contentSelector || textSelector || commentSelector || cdataSectionSelector || docTypeClauseSelector || xmlDeclarationSelector || processingInstructionSelector;
        String caseSensitiveSelectorPath = isNonElementSelector ? null : isEmptyOrWhitespace(path) ? null : path;
        if (caseSensitiveSelectorPath == null) {
            lowerCase = null;
        } else {
            lowerCase = html ? caseSensitiveSelectorPath.toLowerCase() : caseSensitiveSelectorPath;
        }
        String selectorPath = lowerCase;
        if (modifiersGroup != null) {
            String remainingModifiers = modifiersGroup;
            while (remainingModifiers != null) {
                Matcher modifiersMatcher = modifiersPattern.matcher(remainingModifiers);
                if (!modifiersMatcher.matches()) {
                    throw new IllegalArgumentException("Invalid syntax in selector \"" + selector + "\": selector does not match selector syntax: ((/|//)?selector)?([@attrib=\"value\" ((and|or) @attrib2=\"value\")?])?([index])?");
                }
                String currentModifier = modifiersMatcher.group(1);
                remainingModifiers = modifiersMatcher.group(2);
                MarkupSelectorItem.IndexCondition newIndex = parseIndex(currentModifier);
                if (newIndex != null) {
                    if (remainingModifiers != null) {
                        throw new IllegalArgumentException("Invalid syntax in selector \"" + selector + "\": selector does not match selector syntax: ((/|//)?selector)?([@attrib=\"value\" ((and|or) @attrib2=\"value\")?])?([index])?");
                    }
                    if (index != null) {
                        throw new IllegalArgumentException("Invalid syntax in selector \"" + selector + "\": cannot combine two different index modifiers (probably one was specified in the expression itself, and the other one comes from a reference resolver).");
                    }
                    index = newIndex;
                } else {
                    MarkupSelectorItem.IAttributeCondition newAttributeCondition3 = parseAttributeCondition(html, selector, currentModifier);
                    if (newAttributeCondition3 == null) {
                        throw new IllegalArgumentException("Invalid syntax in selector \"" + selector + "\": selector does not match selector syntax: (/|//)(selector)([@attrib=\"value\" ((and|or) @attrib2=\"value\")?])?([index])?");
                    }
                    if (attributeCondition3 == null) {
                        attributeCondition3 = newAttributeCondition3;
                    } else {
                        attributeCondition3 = new MarkupSelectorItem.AttributeConditionRelation(MarkupSelectorItem.AttributeConditionRelation.Type.AND, attributeCondition3, newAttributeCondition3);
                    }
                }
            }
        }
        IMarkupSelectorItem thisItem = new MarkupSelectorItem(html, anyLevel, contentSelector, textSelector, commentSelector, cdataSectionSelector, docTypeClauseSelector, xmlDeclarationSelector, processingInstructionSelector, selectorPath, index, attributeCondition3);
        if (referenceResolver != null && (selectorPathReferenceModifier != null || selectorPath != null)) {
            if (selectorPathReferenceModifier != null) {
                String resolvedSelector = referenceResolver.resolveSelectorFromReference(selectorPathReferenceModifier);
                if (resolvedSelector != null) {
                    if (resolvedSelector.startsWith("//")) {
                        if (!anyLevel) {
                            resolvedSelector = resolvedSelector.substring(1);
                        }
                    } else if (resolvedSelector.startsWith("/")) {
                        if (anyLevel) {
                            resolvedSelector = "/" + resolvedSelector;
                        }
                    } else if (!anyLevel) {
                        resolvedSelector = "/" + resolvedSelector;
                    }
                    List<IMarkupSelectorItem> parsedReference = parseSelector(html, resolvedSelector, null);
                    if (parsedReference != null && parsedReference.size() > 1) {
                        throw new IllegalArgumentException("Invalid selector resolved by reference resolver of class " + referenceResolver.getClass().getName() + "  for selector " + selectorPath + ": resolved selector has more than one level, which is forbidden.");
                    }
                    if (parsedReference != null && parsedReference.size() == 1) {
                        thisItem = new MarkupSelectorAndItem(thisItem, parsedReference.get(0));
                    }
                }
            } else {
                String resolvedSelector2 = referenceResolver.resolveSelectorFromReference(caseSensitiveSelectorPath);
                if (resolvedSelector2 != null) {
                    if (resolvedSelector2.startsWith("//")) {
                        if (!anyLevel) {
                            resolvedSelector2 = resolvedSelector2.substring(1);
                        }
                    } else if (resolvedSelector2.startsWith("/")) {
                        if (anyLevel) {
                            resolvedSelector2 = "/" + resolvedSelector2;
                        }
                    } else if (!anyLevel) {
                        resolvedSelector2 = "/" + resolvedSelector2;
                    }
                    List<IMarkupSelectorItem> parsedReference2 = parseSelector(html, resolvedSelector2, attributeCondition3, index, null);
                    if (parsedReference2 != null && parsedReference2.size() > 1) {
                        throw new IllegalArgumentException("Invalid selector resolved by reference resolver of class " + referenceResolver.getClass().getName() + "  for selector " + selectorPath + ": resolved selector has more than one level, which is forbidden.");
                    }
                    if (parsedReference2 != null && parsedReference2.size() == 1) {
                        thisItem = new MarkupSelectorOrItem(thisItem, parsedReference2.get(0));
                    }
                }
            }
        }
        result.add(0, thisItem);
        return result;
    }

    private static MarkupSelectorItem.IndexCondition parseIndex(String indexGroup) {
        if ("odd()".equals(indexGroup.toLowerCase())) {
            return MarkupSelectorItem.IndexCondition.INDEX_CONDITION_ODD;
        }
        if ("even()".equals(indexGroup.toLowerCase())) {
            return MarkupSelectorItem.IndexCondition.INDEX_CONDITION_EVEN;
        }
        if (indexGroup.charAt(0) == '>') {
            try {
                return new MarkupSelectorItem.IndexCondition(MarkupSelectorItem.IndexCondition.IndexConditionType.MORE_THAN, Integer.valueOf(indexGroup.substring(1).trim()).intValue());
            } catch (Exception e) {
                return null;
            }
        } else if (indexGroup.charAt(0) == '<') {
            try {
                return new MarkupSelectorItem.IndexCondition(MarkupSelectorItem.IndexCondition.IndexConditionType.LESS_THAN, Integer.valueOf(indexGroup.substring(1).trim()).intValue());
            } catch (Exception e2) {
                return null;
            }
        } else {
            try {
                return new MarkupSelectorItem.IndexCondition(MarkupSelectorItem.IndexCondition.IndexConditionType.VALUE, Integer.valueOf(indexGroup.trim()).intValue());
            } catch (Exception e3) {
                return null;
            }
        }
    }

    private static MarkupSelectorItem.IAttributeCondition parseAttributeCondition(boolean html, String selectorSpec, String attrGroup) {
        String text = attrGroup.trim();
        if (text.startsWith("(") && text.endsWith(")")) {
            text = text.substring(1, text.length() - 1);
        }
        int textLen = text.length();
        if (isEmptyOrWhitespace(text)) {
            throw new IllegalArgumentException("Invalid syntax in selector: \"" + selectorSpec + "\"");
        }
        boolean inDoubleLiteral = false;
        boolean inSimpleLiteral = false;
        int nestingLevel = 0;
        int i = 0;
        while (i < textLen) {
            char c = text.charAt(i);
            if (c == '\'' && !inDoubleLiteral) {
                inSimpleLiteral = !inSimpleLiteral;
                i++;
            } else if (c == '\"' && !inSimpleLiteral) {
                inDoubleLiteral = !inDoubleLiteral;
                i++;
            } else {
                if (!inSimpleLiteral && !inDoubleLiteral) {
                    if (c == '(') {
                        nestingLevel++;
                    } else if (c == ')') {
                        nestingLevel--;
                    } else if (nestingLevel == 0 && i + 4 < textLen && Character.isWhitespace(c) && ((text.charAt(i + 1) == 'a' || text.charAt(i + 1) == 'A') && ((text.charAt(i + 2) == 'n' || text.charAt(i + 2) == 'N') && ((text.charAt(i + 3) == 'd' || text.charAt(i + 3) == 'D') && Character.isWhitespace(text.charAt(i + 4)))))) {
                        MarkupSelectorItem.IAttributeCondition left = parseAttributeCondition(html, selectorSpec, text.substring(0, i));
                        MarkupSelectorItem.IAttributeCondition right = parseAttributeCondition(html, selectorSpec, text.substring(i + 5, textLen));
                        return new MarkupSelectorItem.AttributeConditionRelation(MarkupSelectorItem.AttributeConditionRelation.Type.AND, left, right);
                    } else if (nestingLevel == 0 && i + 3 < textLen && Character.isWhitespace(c) && ((text.charAt(i + 1) == 'o' || text.charAt(i + 1) == 'O') && ((text.charAt(i + 2) == 'r' || text.charAt(i + 2) == 'R') && Character.isWhitespace(text.charAt(i + 3))))) {
                        MarkupSelectorItem.IAttributeCondition left2 = parseAttributeCondition(html, selectorSpec, text.substring(0, i));
                        MarkupSelectorItem.IAttributeCondition right2 = parseAttributeCondition(html, selectorSpec, text.substring(i + 4, textLen));
                        return new MarkupSelectorItem.AttributeConditionRelation(MarkupSelectorItem.AttributeConditionRelation.Type.OR, left2, right2);
                    }
                }
                i++;
            }
        }
        return parseSimpleAttributeCondition(html, selectorSpec, text);
    }

    private static MarkupSelectorItem.AttributeCondition parseSimpleAttributeCondition(boolean html, String selectorSpec, String attributeSpec) {
        String[] fragments = tokenizeAttributeSpec(attributeSpec);
        String attrName = fragments[0];
        if (attrName.startsWith("@")) {
            attrName = attrName.substring(1);
        }
        String attrName2 = html ? attrName.toLowerCase() : attrName;
        MarkupSelectorItem.AttributeCondition.Operator operator = parseAttributeOperator(fragments[1]);
        String attrValue = fragments[2];
        if (attrValue != null) {
            if ((!attrValue.startsWith("\"") || !attrValue.endsWith("\"")) && (!attrValue.startsWith("'") || !attrValue.endsWith("'"))) {
                throw new IllegalArgumentException("Invalid syntax in selector: \"" + selectorSpec + "\"");
            }
            return new MarkupSelectorItem.AttributeCondition(attrName2, operator, attrValue.substring(1, attrValue.length() - 1));
        }
        return new MarkupSelectorItem.AttributeCondition(attrName2, operator, null);
    }

    private static String[] tokenizeAttributeSpec(String specification) {
        int equalsPos = specification.indexOf(61);
        if (equalsPos == -1) {
            return specification.charAt(0) == '!' ? new String[]{specification.substring(1).trim(), "!", null} : new String[]{specification.trim(), "", null};
        }
        char cprev = specification.charAt(equalsPos - 1);
        switch (cprev) {
            case '!':
                return new String[]{specification.substring(0, equalsPos - 1).trim(), "!=", specification.substring(equalsPos + 1).trim()};
            case '$':
                return new String[]{specification.substring(0, equalsPos - 1).trim(), "$=", specification.substring(equalsPos + 1).trim()};
            case '*':
                return new String[]{specification.substring(0, equalsPos - 1).trim(), "*=", specification.substring(equalsPos + 1).trim()};
            case Opcodes.DUP2_X2 /* 94 */:
                return new String[]{specification.substring(0, equalsPos - 1).trim(), "^=", specification.substring(equalsPos + 1).trim()};
            default:
                return new String[]{specification.substring(0, equalsPos).trim(), "=", specification.substring(equalsPos + 1).trim()};
        }
    }

    private static MarkupSelectorItem.AttributeCondition.Operator parseAttributeOperator(String operatorStr) {
        if (operatorStr == null) {
            return null;
        }
        if ("=".equals(operatorStr)) {
            return MarkupSelectorItem.AttributeCondition.Operator.EQUALS;
        }
        if ("!=".equals(operatorStr)) {
            return MarkupSelectorItem.AttributeCondition.Operator.NOT_EQUALS;
        }
        if ("^=".equals(operatorStr)) {
            return MarkupSelectorItem.AttributeCondition.Operator.STARTS_WITH;
        }
        if ("$=".equals(operatorStr)) {
            return MarkupSelectorItem.AttributeCondition.Operator.ENDS_WITH;
        }
        if ("*=".equals(operatorStr)) {
            return MarkupSelectorItem.AttributeCondition.Operator.CONTAINS;
        }
        if ("!".equals(operatorStr)) {
            return MarkupSelectorItem.AttributeCondition.Operator.NOT_EXISTS;
        }
        if ("".equals(operatorStr)) {
            return MarkupSelectorItem.AttributeCondition.Operator.EXISTS;
        }
        return null;
    }

    private static boolean isEmptyOrWhitespace(String target) {
        int targetLen;
        if (target == null || (targetLen = target.length()) == 0) {
            return true;
        }
        char c0 = target.charAt(0);
        if (c0 < 'a' || c0 > 'z') {
            if (c0 >= 'A' && c0 <= 'Z') {
                return false;
            }
            for (int i = 0; i < targetLen; i++) {
                char c = target.charAt(i);
                if (c != ' ' && !Character.isWhitespace(c)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private MarkupSelectorItems() {
    }
}