package org.attoparser.select;

import java.util.Arrays;
import org.attoparser.select.MarkupSelectorFilter;
import org.attoparser.util.TextUtil;
import org.springframework.beans.PropertyAccessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/select/MarkupSelectorItem.class */
final class MarkupSelectorItem implements IMarkupSelectorItem {
    static final String CONTENT_SELECTOR = "content()";
    static final String TEXT_SELECTOR = "text()";
    static final String COMMENT_SELECTOR = "comment()";
    static final String CDATA_SECTION_SELECTOR = "cdata()";
    static final String DOC_TYPE_CLAUSE_SELECTOR = "doctype()";
    static final String XML_DECLARATION_SELECTOR = "xmldecl()";
    static final String PROCESSING_INSTRUCTION_SELECTOR = "procinstr()";
    static final String ID_MODIFIER_SEPARATOR = "#";
    static final String CLASS_MODIFIER_SEPARATOR = ".";
    static final String REFERENCE_MODIFIER_SEPARATOR = "%";
    static final String ID_ATTRIBUTE_NAME = "id";
    static final String CLASS_ATTRIBUTE_NAME = "class";
    static final String ODD_SELECTOR = "odd()";
    static final String EVEN_SELECTOR = "even()";
    private final boolean html;
    private final boolean anyLevel;
    private final boolean contentSelector;
    private final boolean textSelector;
    private final boolean commentSelector;
    private final boolean cdataSectionSelector;
    private final boolean docTypeClauseSelector;
    private final boolean xmlDeclarationSelector;
    private final boolean processingInstructionSelector;
    private final String selectorPath;
    private final int selectorPathLen;
    private final IndexCondition index;
    private final IAttributeCondition attributeCondition;
    private final boolean requiresAttributesInElement;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/select/MarkupSelectorItem$IAttributeCondition.class */
    public interface IAttributeCondition {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MarkupSelectorItem(boolean html, boolean anyLevel, boolean contentSelector, boolean textSelector, boolean commentSelector, boolean cdataSectionSelector, boolean docTypeClauseSelector, boolean xmlDeclarationSelector, boolean processingInstructionSelector, String selectorPath, IndexCondition index, IAttributeCondition attributeCondition) {
        this.html = html;
        this.anyLevel = anyLevel;
        this.contentSelector = contentSelector;
        this.textSelector = textSelector;
        this.commentSelector = commentSelector;
        this.cdataSectionSelector = cdataSectionSelector;
        this.docTypeClauseSelector = docTypeClauseSelector;
        this.xmlDeclarationSelector = xmlDeclarationSelector;
        this.processingInstructionSelector = processingInstructionSelector;
        this.selectorPath = selectorPath;
        this.selectorPathLen = selectorPath != null ? selectorPath.length() : 0;
        this.index = index;
        this.attributeCondition = attributeCondition;
        this.requiresAttributesInElement = computeRequiresAttributesInElement(this.attributeCondition);
    }

    private static boolean computeRequiresAttributesInElement(IAttributeCondition attributeCondition) {
        if (attributeCondition == null) {
            return false;
        }
        if (attributeCondition instanceof AttributeConditionRelation) {
            AttributeConditionRelation relation = (AttributeConditionRelation) attributeCondition;
            return computeRequiresAttributesInElement(relation.left) || computeRequiresAttributesInElement(relation.right);
        }
        AttributeCondition attrCondition = (AttributeCondition) attributeCondition;
        return (attrCondition.operator.equals(AttributeCondition.Operator.NOT_EQUALS) || attrCondition.operator.equals(AttributeCondition.Operator.NOT_EXISTS)) ? false : true;
    }

    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        if (this.anyLevel) {
            strBuilder.append("//");
        } else {
            strBuilder.append("/");
        }
        if (this.selectorPath != null) {
            strBuilder.append(this.selectorPath);
        } else if (this.contentSelector) {
            strBuilder.append(CONTENT_SELECTOR);
        } else if (this.textSelector) {
            strBuilder.append(TEXT_SELECTOR);
        } else if (this.commentSelector) {
            strBuilder.append(COMMENT_SELECTOR);
        } else if (this.cdataSectionSelector) {
            strBuilder.append(CDATA_SECTION_SELECTOR);
        } else if (this.docTypeClauseSelector) {
            strBuilder.append(DOC_TYPE_CLAUSE_SELECTOR);
        } else if (this.xmlDeclarationSelector) {
            strBuilder.append(XML_DECLARATION_SELECTOR);
        } else if (this.processingInstructionSelector) {
            strBuilder.append(PROCESSING_INSTRUCTION_SELECTOR);
        } else {
            strBuilder.append("*");
        }
        if (this.attributeCondition != null) {
            strBuilder.append(PropertyAccessor.PROPERTY_KEY_PREFIX);
            strBuilder.append(toStringAttributeCondition(this.attributeCondition, false));
            strBuilder.append("]");
        }
        if (this.index != null) {
            strBuilder.append(PropertyAccessor.PROPERTY_KEY_PREFIX);
            switch (this.index.type) {
                case VALUE:
                    strBuilder.append(this.index.value);
                    break;
                case LESS_THAN:
                    strBuilder.append("<").append(this.index.value);
                    break;
                case MORE_THAN:
                    strBuilder.append(">").append(this.index.value);
                    break;
                case EVEN:
                    strBuilder.append(EVEN_SELECTOR);
                    break;
                case ODD:
                    strBuilder.append(ODD_SELECTOR);
                    break;
            }
            strBuilder.append("]");
        }
        return strBuilder.toString();
    }

    private static String toStringAttributeCondition(IAttributeCondition attributeCondition, boolean outputParenthesis) {
        if (attributeCondition instanceof AttributeConditionRelation) {
            AttributeConditionRelation relation = (AttributeConditionRelation) attributeCondition;
            if (outputParenthesis) {
                return "(" + toStringAttributeCondition(relation.left, true) + " " + relation.type + " " + toStringAttributeCondition(relation.right, true) + ")";
            }
            return toStringAttributeCondition(relation.left, true) + " " + relation.type + " " + toStringAttributeCondition(relation.right, true);
        }
        AttributeCondition attrCondition = (AttributeCondition) attributeCondition;
        return attrCondition.name + attrCondition.operator.text + (attrCondition.value != null ? "'" + attrCondition.value + "'" : "");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/select/MarkupSelectorItem$AttributeCondition.class */
    public static final class AttributeCondition implements IAttributeCondition {
        final String name;
        final Operator operator;
        final String value;

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/select/MarkupSelectorItem$AttributeCondition$Operator.class */
        public enum Operator {
            EQUALS("="),
            NOT_EQUALS("!="),
            STARTS_WITH("^="),
            ENDS_WITH("$="),
            EXISTS("*"),
            NOT_EXISTS("!"),
            CONTAINS("*=");
            
            private String text;

            Operator(String text) {
                this.text = text;
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public AttributeCondition(String name, Operator operator, String value) {
            this.name = name;
            this.operator = operator;
            this.value = value;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/select/MarkupSelectorItem$AttributeConditionRelation.class */
    public static final class AttributeConditionRelation implements IAttributeCondition {
        final Type type;
        final IAttributeCondition left;
        final IAttributeCondition right;

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/select/MarkupSelectorItem$AttributeConditionRelation$Type.class */
        public enum Type {
            AND,
            OR
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public AttributeConditionRelation(Type type, IAttributeCondition left, IAttributeCondition right) {
            this.type = type;
            this.left = left;
            this.right = right;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/select/MarkupSelectorItem$IndexCondition.class */
    public static final class IndexCondition {
        static IndexCondition INDEX_CONDITION_ODD = new IndexCondition(IndexConditionType.ODD, -1);
        static IndexCondition INDEX_CONDITION_EVEN = new IndexCondition(IndexConditionType.EVEN, -1);
        final IndexConditionType type;
        final int value;

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/select/MarkupSelectorItem$IndexCondition$IndexConditionType.class */
        public enum IndexConditionType {
            VALUE,
            LESS_THAN,
            MORE_THAN,
            EVEN,
            ODD
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public IndexCondition(IndexConditionType type, int value) {
            this.type = type;
            this.value = value;
        }
    }

    @Override // org.attoparser.select.IMarkupSelectorItem
    public boolean anyLevel() {
        return this.anyLevel;
    }

    @Override // org.attoparser.select.IMarkupSelectorItem
    public boolean matchesText(int markupBlockIndex, MarkupSelectorFilter.MarkupBlockMatchingCounter markupBlockMatchingCounter) {
        return (this.contentSelector || this.textSelector) && (this.index == null || matchesIndex(markupBlockIndex, markupBlockMatchingCounter, this.index));
    }

    @Override // org.attoparser.select.IMarkupSelectorItem
    public boolean matchesComment(int markupBlockIndex, MarkupSelectorFilter.MarkupBlockMatchingCounter markupBlockMatchingCounter) {
        return (this.contentSelector || this.commentSelector) && (this.index == null || matchesIndex(markupBlockIndex, markupBlockMatchingCounter, this.index));
    }

    @Override // org.attoparser.select.IMarkupSelectorItem
    public boolean matchesCDATASection(int markupBlockIndex, MarkupSelectorFilter.MarkupBlockMatchingCounter markupBlockMatchingCounter) {
        return (this.contentSelector || this.cdataSectionSelector) && (this.index == null || matchesIndex(markupBlockIndex, markupBlockMatchingCounter, this.index));
    }

    @Override // org.attoparser.select.IMarkupSelectorItem
    public boolean matchesDocTypeClause(int markupBlockIndex, MarkupSelectorFilter.MarkupBlockMatchingCounter markupBlockMatchingCounter) {
        return (this.contentSelector || this.docTypeClauseSelector) && (this.index == null || matchesIndex(markupBlockIndex, markupBlockMatchingCounter, this.index));
    }

    @Override // org.attoparser.select.IMarkupSelectorItem
    public boolean matchesXmlDeclaration(int markupBlockIndex, MarkupSelectorFilter.MarkupBlockMatchingCounter markupBlockMatchingCounter) {
        return (this.contentSelector || this.xmlDeclarationSelector) && (this.index == null || matchesIndex(markupBlockIndex, markupBlockMatchingCounter, this.index));
    }

    @Override // org.attoparser.select.IMarkupSelectorItem
    public boolean matchesProcessingInstruction(int markupBlockIndex, MarkupSelectorFilter.MarkupBlockMatchingCounter markupBlockMatchingCounter) {
        return (this.contentSelector || this.processingInstructionSelector) && (this.index == null || matchesIndex(markupBlockIndex, markupBlockMatchingCounter, this.index));
    }

    @Override // org.attoparser.select.IMarkupSelectorItem
    public boolean matchesElement(int markupBlockIndex, SelectorElementBuffer elementBuffer, MarkupSelectorFilter.MarkupBlockMatchingCounter markupBlockMatchingCounter) {
        if (this.textSelector || this.commentSelector || this.cdataSectionSelector || this.docTypeClauseSelector || this.xmlDeclarationSelector || this.processingInstructionSelector) {
            return false;
        }
        if (!this.contentSelector && this.requiresAttributesInElement && elementBuffer.attributeCount == 0) {
            return false;
        }
        if (!this.contentSelector && this.selectorPath != null) {
            if (!TextUtil.equals(!this.html, this.selectorPath, 0, this.selectorPathLen, elementBuffer.elementName, 0, elementBuffer.elementNameLen)) {
                return false;
            }
        }
        if (!this.contentSelector && this.attributeCondition != null && !matchesAttributeCondition(this.html, elementBuffer, this.attributeCondition)) {
            return false;
        }
        if (this.index != null && !matchesIndex(markupBlockIndex, markupBlockMatchingCounter, this.index)) {
            return false;
        }
        return true;
    }

    private static boolean matchesAttributeCondition(boolean html, SelectorElementBuffer elementBuffer, IAttributeCondition attributeCondition) {
        if (attributeCondition instanceof AttributeConditionRelation) {
            AttributeConditionRelation relation = (AttributeConditionRelation) attributeCondition;
            switch (relation.type) {
                case AND:
                    return matchesAttributeCondition(html, elementBuffer, relation.left) && matchesAttributeCondition(html, elementBuffer, relation.right);
                case OR:
                    return matchesAttributeCondition(html, elementBuffer, relation.left) || matchesAttributeCondition(html, elementBuffer, relation.right);
            }
        }
        AttributeCondition attrCondition = (AttributeCondition) attributeCondition;
        return matchesAttribute(html, elementBuffer, attrCondition.name, attrCondition.operator, attrCondition.value);
    }

    private static boolean matchesAttribute(boolean html, SelectorElementBuffer elementBuffer, String attrName, AttributeCondition.Operator attrOperator, String attrValue) {
        boolean found = false;
        for (int i = 0; i < elementBuffer.attributeCount; i++) {
            if (TextUtil.equals(!html, attrName, 0, attrName.length(), elementBuffer.attributeBuffers[i], 0, elementBuffer.attributeNameLens[i])) {
                found = true;
                if (html && "class".equals(attrName)) {
                    if (matchesClassAttributeValue(attrOperator, attrValue, elementBuffer.attributeBuffers[i], elementBuffer.attributeValueContentOffsets[i], elementBuffer.attributeValueContentLens[i])) {
                        return true;
                    }
                } else if (matchesAttributeValue(attrOperator, attrValue, elementBuffer.attributeBuffers[i], elementBuffer.attributeValueContentOffsets[i], elementBuffer.attributeValueContentLens[i])) {
                    return true;
                }
            }
        }
        if (found) {
            return false;
        }
        return AttributeCondition.Operator.NOT_EXISTS.equals(attrOperator);
    }

    private static boolean matchesAttributeValue(AttributeCondition.Operator attrOperator, String attrValue, char[] elementAttrValueBuffer, int elementAttrValueOffset, int elementAttrValueLen) {
        switch (attrOperator) {
            case EQUALS:
                return TextUtil.equals(true, (CharSequence) attrValue, 0, attrValue.length(), elementAttrValueBuffer, elementAttrValueOffset, elementAttrValueLen);
            case NOT_EQUALS:
                return !TextUtil.equals(true, (CharSequence) attrValue, 0, attrValue.length(), elementAttrValueBuffer, elementAttrValueOffset, elementAttrValueLen);
            case STARTS_WITH:
                return TextUtil.startsWith(true, elementAttrValueBuffer, elementAttrValueOffset, elementAttrValueLen, (CharSequence) attrValue, 0, attrValue.length());
            case ENDS_WITH:
                return TextUtil.endsWith(true, elementAttrValueBuffer, elementAttrValueOffset, elementAttrValueLen, (CharSequence) attrValue, 0, attrValue.length());
            case CONTAINS:
                return TextUtil.contains(true, elementAttrValueBuffer, elementAttrValueOffset, elementAttrValueLen, (CharSequence) attrValue, 0, attrValue.length());
            case EXISTS:
                return true;
            case NOT_EXISTS:
                return false;
            default:
                throw new IllegalArgumentException("Unknown operator: " + attrOperator);
        }
    }

    private static boolean matchesClassAttributeValue(AttributeCondition.Operator attrOperator, String attrValue, char[] elementAttrValueBuffer, int elementAttrValueOffset, int elementAttrValueLen) {
        if (elementAttrValueLen == 0) {
            return isEmptyOrWhitespace(attrValue);
        }
        int i = 0;
        while (i < elementAttrValueLen && Character.isWhitespace(elementAttrValueBuffer[elementAttrValueOffset + i])) {
            i++;
        }
        if (i == elementAttrValueLen) {
            return isEmptyOrWhitespace(attrValue);
        }
        while (i < elementAttrValueLen) {
            int lastOffset = elementAttrValueOffset + i;
            while (i < elementAttrValueLen && !Character.isWhitespace(elementAttrValueBuffer[elementAttrValueOffset + i])) {
                i++;
            }
            if (matchesAttributeValue(attrOperator, attrValue, elementAttrValueBuffer, lastOffset, (elementAttrValueOffset + i) - lastOffset)) {
                return true;
            }
            while (i < elementAttrValueLen && Character.isWhitespace(elementAttrValueBuffer[elementAttrValueOffset + i])) {
                i++;
            }
        }
        return false;
    }

    private static boolean matchesIndex(int markupBlockIndex, MarkupSelectorFilter.MarkupBlockMatchingCounter markupBlockMatchingCounter, IndexCondition indexCondition) {
        if (markupBlockMatchingCounter.counters == null) {
            markupBlockMatchingCounter.indexes = new int[4];
            markupBlockMatchingCounter.counters = new int[4];
            Arrays.fill(markupBlockMatchingCounter.indexes, -1);
            Arrays.fill(markupBlockMatchingCounter.counters, -1);
        }
        int i = 0;
        while (i < markupBlockMatchingCounter.indexes.length && markupBlockMatchingCounter.indexes[i] >= 0 && markupBlockMatchingCounter.indexes[i] != markupBlockIndex) {
            i++;
        }
        if (i == markupBlockMatchingCounter.indexes.length) {
            int[] newMarkupBlockMatchingIndexes = new int[markupBlockMatchingCounter.indexes.length + 4];
            int[] newMarkupBlockMatchingCounters = new int[markupBlockMatchingCounter.counters.length + 4];
            Arrays.fill(newMarkupBlockMatchingIndexes, -1);
            Arrays.fill(newMarkupBlockMatchingCounters, -1);
            System.arraycopy(markupBlockMatchingCounter.indexes, 0, newMarkupBlockMatchingIndexes, 0, markupBlockMatchingCounter.indexes.length);
            System.arraycopy(markupBlockMatchingCounter.counters, 0, newMarkupBlockMatchingCounters, 0, markupBlockMatchingCounter.counters.length);
            markupBlockMatchingCounter.indexes = newMarkupBlockMatchingIndexes;
            markupBlockMatchingCounter.counters = newMarkupBlockMatchingCounters;
        }
        if (markupBlockMatchingCounter.indexes[i] == -1) {
            markupBlockMatchingCounter.indexes[i] = markupBlockIndex;
            markupBlockMatchingCounter.counters[i] = 0;
        } else {
            int[] iArr = markupBlockMatchingCounter.counters;
            int i2 = i;
            iArr[i2] = iArr[i2] + 1;
        }
        switch (indexCondition.type) {
            case VALUE:
                if (indexCondition.value != markupBlockMatchingCounter.counters[i]) {
                    return false;
                }
                return true;
            case LESS_THAN:
                if (indexCondition.value <= markupBlockMatchingCounter.counters[i]) {
                    return false;
                }
                return true;
            case MORE_THAN:
                if (indexCondition.value >= markupBlockMatchingCounter.counters[i]) {
                    return false;
                }
                return true;
            case EVEN:
                if (markupBlockMatchingCounter.counters[i] % 2 != 0) {
                    return false;
                }
                return true;
            case ODD:
                if (markupBlockMatchingCounter.counters[i] % 2 == 0) {
                    return false;
                }
                return true;
            default:
                return true;
        }
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
}