package org.attoparser.select;

import org.attoparser.select.MarkupSelectorFilter;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/select/IMarkupSelectorItem.class */
public interface IMarkupSelectorItem {
    boolean anyLevel();

    boolean matchesText(int i, MarkupSelectorFilter.MarkupBlockMatchingCounter markupBlockMatchingCounter);

    boolean matchesComment(int i, MarkupSelectorFilter.MarkupBlockMatchingCounter markupBlockMatchingCounter);

    boolean matchesCDATASection(int i, MarkupSelectorFilter.MarkupBlockMatchingCounter markupBlockMatchingCounter);

    boolean matchesDocTypeClause(int i, MarkupSelectorFilter.MarkupBlockMatchingCounter markupBlockMatchingCounter);

    boolean matchesXmlDeclaration(int i, MarkupSelectorFilter.MarkupBlockMatchingCounter markupBlockMatchingCounter);

    boolean matchesProcessingInstruction(int i, MarkupSelectorFilter.MarkupBlockMatchingCounter markupBlockMatchingCounter);

    boolean matchesElement(int i, SelectorElementBuffer selectorElementBuffer, MarkupSelectorFilter.MarkupBlockMatchingCounter markupBlockMatchingCounter);
}