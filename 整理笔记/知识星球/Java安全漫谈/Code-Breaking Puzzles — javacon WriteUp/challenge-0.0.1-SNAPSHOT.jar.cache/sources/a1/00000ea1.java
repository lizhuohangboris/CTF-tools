package org.attoparser.select;

import org.attoparser.select.MarkupSelectorFilter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/select/MarkupSelectorOrItem.class */
final class MarkupSelectorOrItem implements IMarkupSelectorItem {
    final IMarkupSelectorItem left;
    final IMarkupSelectorItem right;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MarkupSelectorOrItem(IMarkupSelectorItem left, IMarkupSelectorItem right) {
        if ((right.anyLevel() && !left.anyLevel()) || (!right.anyLevel() && left.anyLevel())) {
            throw new IllegalArgumentException("Left and right items must have the same value for ''anyLevel': " + left.toString() + " && " + right.toString());
        }
        this.left = left;
        this.right = right;
    }

    @Override // org.attoparser.select.IMarkupSelectorItem
    public boolean anyLevel() {
        return this.left.anyLevel();
    }

    @Override // org.attoparser.select.IMarkupSelectorItem
    public boolean matchesText(int markupBlockIndex, MarkupSelectorFilter.MarkupBlockMatchingCounter markupBlockMatchingCounter) {
        return this.left.matchesText(markupBlockIndex, markupBlockMatchingCounter) || this.right.matchesText(markupBlockIndex, markupBlockMatchingCounter);
    }

    @Override // org.attoparser.select.IMarkupSelectorItem
    public boolean matchesComment(int markupBlockIndex, MarkupSelectorFilter.MarkupBlockMatchingCounter markupBlockMatchingCounter) {
        return this.left.matchesComment(markupBlockIndex, markupBlockMatchingCounter) || this.right.matchesComment(markupBlockIndex, markupBlockMatchingCounter);
    }

    @Override // org.attoparser.select.IMarkupSelectorItem
    public boolean matchesCDATASection(int markupBlockIndex, MarkupSelectorFilter.MarkupBlockMatchingCounter markupBlockMatchingCounter) {
        return this.left.matchesCDATASection(markupBlockIndex, markupBlockMatchingCounter) || this.right.matchesCDATASection(markupBlockIndex, markupBlockMatchingCounter);
    }

    @Override // org.attoparser.select.IMarkupSelectorItem
    public boolean matchesDocTypeClause(int markupBlockIndex, MarkupSelectorFilter.MarkupBlockMatchingCounter markupBlockMatchingCounter) {
        return this.left.matchesDocTypeClause(markupBlockIndex, markupBlockMatchingCounter) || this.right.matchesDocTypeClause(markupBlockIndex, markupBlockMatchingCounter);
    }

    @Override // org.attoparser.select.IMarkupSelectorItem
    public boolean matchesXmlDeclaration(int markupBlockIndex, MarkupSelectorFilter.MarkupBlockMatchingCounter markupBlockMatchingCounter) {
        return this.left.matchesXmlDeclaration(markupBlockIndex, markupBlockMatchingCounter) || this.right.matchesXmlDeclaration(markupBlockIndex, markupBlockMatchingCounter);
    }

    @Override // org.attoparser.select.IMarkupSelectorItem
    public boolean matchesProcessingInstruction(int markupBlockIndex, MarkupSelectorFilter.MarkupBlockMatchingCounter markupBlockMatchingCounter) {
        return this.left.matchesProcessingInstruction(markupBlockIndex, markupBlockMatchingCounter) || this.right.matchesProcessingInstruction(markupBlockIndex, markupBlockMatchingCounter);
    }

    @Override // org.attoparser.select.IMarkupSelectorItem
    public boolean matchesElement(int markupBlockIndex, SelectorElementBuffer elementBuffer, MarkupSelectorFilter.MarkupBlockMatchingCounter markupBlockMatchingCounter) {
        return this.left.matchesElement(markupBlockIndex, elementBuffer, markupBlockMatchingCounter) || this.right.matchesElement(markupBlockIndex, elementBuffer, markupBlockMatchingCounter);
    }

    public String toString() {
        return "(" + this.left.toString() + " || " + this.right + ")";
    }
}