package org.attoparser.select;

import java.util.Arrays;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/select/MarkupSelectorFilter.class */
final class MarkupSelectorFilter {
    private final MarkupSelectorFilter prev;
    private MarkupSelectorFilter next;
    private final IMarkupSelectorItem markupSelectorItem;
    private static final int MATCHED_MARKUP_LEVELS_LEN = 10;
    private boolean[] matchedMarkupLevels;
    private boolean matchesThisLevel;
    private final MarkupBlockMatchingCounter markupBlockMatchingCounter = new MarkupBlockMatchingCounter();
    int markupLevelCheckerIndex;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/select/MarkupSelectorFilter$MarkupBlockMatchingCounter.class */
    public static final class MarkupBlockMatchingCounter {
        static final int DEFAULT_COUNTER_SIZE = 4;
        int[] indexes = null;
        int[] counters = null;

        MarkupBlockMatchingCounter() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MarkupSelectorFilter(MarkupSelectorFilter prev, IMarkupSelectorItem markupSelectorItem) {
        this.prev = prev;
        if (this.prev != null) {
            this.prev.next = this;
        }
        this.matchedMarkupLevels = new boolean[10];
        Arrays.fill(this.matchedMarkupLevels, false);
        this.markupSelectorItem = markupSelectorItem;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean matchXmlDeclaration(boolean blockMatching, int markupLevel, int markupBlockIndex) {
        checkMarkupLevel(markupLevel);
        if (this.markupSelectorItem.anyLevel() || markupLevel == 0 || (this.prev != null && this.prev.matchedMarkupLevels[markupLevel - 1])) {
            this.matchesThisLevel = this.markupSelectorItem.matchesXmlDeclaration(markupBlockIndex, this.markupBlockMatchingCounter);
            if (!matchesPreviousOrCurrentLevel(markupLevel)) {
                return this.matchesThisLevel && this.next == null;
            } else if (this.next != null) {
                return this.next.matchXmlDeclaration(blockMatching, markupLevel, markupBlockIndex);
            } else {
                if (blockMatching) {
                    return true;
                }
                return this.matchesThisLevel;
            }
        } else if (matchesPreviousOrCurrentLevel(markupLevel)) {
            if (this.next != null) {
                return this.next.matchXmlDeclaration(blockMatching, markupLevel, markupBlockIndex);
            }
            return blockMatching;
        } else {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean matchDocTypeClause(boolean blockMatching, int markupLevel, int markupBlockIndex) {
        checkMarkupLevel(markupLevel);
        if (this.markupSelectorItem.anyLevel() || markupLevel == 0 || (this.prev != null && this.prev.matchedMarkupLevels[markupLevel - 1])) {
            this.matchesThisLevel = this.markupSelectorItem.matchesDocTypeClause(markupBlockIndex, this.markupBlockMatchingCounter);
            if (!matchesPreviousOrCurrentLevel(markupLevel)) {
                return this.matchesThisLevel && this.next == null;
            } else if (this.next != null) {
                return this.next.matchDocTypeClause(blockMatching, markupLevel, markupBlockIndex);
            } else {
                if (blockMatching) {
                    return true;
                }
                return this.matchesThisLevel;
            }
        } else if (matchesPreviousOrCurrentLevel(markupLevel)) {
            if (this.next != null) {
                return this.next.matchDocTypeClause(blockMatching, markupLevel, markupBlockIndex);
            }
            return blockMatching;
        } else {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean matchCDATASection(boolean blockMatching, int markupLevel, int markupBlockIndex) {
        checkMarkupLevel(markupLevel);
        if (this.markupSelectorItem.anyLevel() || markupLevel == 0 || (this.prev != null && this.prev.matchedMarkupLevels[markupLevel - 1])) {
            this.matchesThisLevel = this.markupSelectorItem.matchesCDATASection(markupBlockIndex, this.markupBlockMatchingCounter);
            if (!matchesPreviousOrCurrentLevel(markupLevel)) {
                return this.matchesThisLevel && this.next == null;
            } else if (this.next != null) {
                return this.next.matchCDATASection(blockMatching, markupLevel, markupBlockIndex);
            } else {
                if (blockMatching) {
                    return true;
                }
                return this.matchesThisLevel;
            }
        } else if (matchesPreviousOrCurrentLevel(markupLevel)) {
            if (this.next != null) {
                return this.next.matchCDATASection(blockMatching, markupLevel, markupBlockIndex);
            }
            return blockMatching;
        } else {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean matchText(boolean blockMatching, int markupLevel, int markupBlockIndex) {
        checkMarkupLevel(markupLevel);
        if (this.markupSelectorItem.anyLevel() || markupLevel == 0 || (this.prev != null && this.prev.matchedMarkupLevels[markupLevel - 1])) {
            this.matchesThisLevel = this.markupSelectorItem.matchesText(markupBlockIndex, this.markupBlockMatchingCounter);
            if (!matchesPreviousOrCurrentLevel(markupLevel)) {
                return this.matchesThisLevel && this.next == null;
            } else if (this.next != null) {
                return this.next.matchText(blockMatching, markupLevel, markupBlockIndex);
            } else {
                if (blockMatching) {
                    return true;
                }
                return this.matchesThisLevel;
            }
        } else if (matchesPreviousOrCurrentLevel(markupLevel)) {
            if (this.next != null) {
                return this.next.matchText(blockMatching, markupLevel, markupBlockIndex);
            }
            return blockMatching;
        } else {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean matchComment(boolean blockMatching, int markupLevel, int markupBlockIndex) {
        checkMarkupLevel(markupLevel);
        if (this.markupSelectorItem.anyLevel() || markupLevel == 0 || (this.prev != null && this.prev.matchedMarkupLevels[markupLevel - 1])) {
            this.matchesThisLevel = this.markupSelectorItem.matchesComment(markupBlockIndex, this.markupBlockMatchingCounter);
            if (!matchesPreviousOrCurrentLevel(markupLevel)) {
                return this.matchesThisLevel && this.next == null;
            } else if (this.next != null) {
                return this.next.matchComment(blockMatching, markupLevel, markupBlockIndex);
            } else {
                if (blockMatching) {
                    return true;
                }
                return this.matchesThisLevel;
            }
        } else if (matchesPreviousOrCurrentLevel(markupLevel)) {
            if (this.next != null) {
                return this.next.matchComment(blockMatching, markupLevel, markupBlockIndex);
            }
            return blockMatching;
        } else {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean matchStandaloneElement(boolean blockMatching, int markupLevel, int markupBlockIndex, SelectorElementBuffer elementBuffer) {
        checkMarkupLevel(markupLevel);
        if (this.markupSelectorItem.anyLevel() || markupLevel == 0 || (this.prev != null && this.prev.matchedMarkupLevels[markupLevel - 1])) {
            this.matchesThisLevel = this.markupSelectorItem.matchesElement(markupBlockIndex, elementBuffer, this.markupBlockMatchingCounter);
            if (!matchesPreviousOrCurrentLevel(markupLevel)) {
                return this.matchesThisLevel && this.next == null;
            } else if (this.next != null) {
                return this.next.matchStandaloneElement(blockMatching, markupLevel, markupBlockIndex, elementBuffer);
            } else {
                if (blockMatching) {
                    return true;
                }
                return this.matchesThisLevel;
            }
        } else if (matchesPreviousOrCurrentLevel(markupLevel)) {
            if (this.next != null) {
                return this.next.matchStandaloneElement(blockMatching, markupLevel, markupBlockIndex, elementBuffer);
            }
            return blockMatching;
        } else {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean matchOpenElement(boolean blockMatching, int markupLevel, int markupBlockIndex, SelectorElementBuffer elementBuffer) {
        checkMarkupLevel(markupLevel);
        if (this.markupSelectorItem.anyLevel() || markupLevel == 0 || (this.prev != null && this.prev.matchedMarkupLevels[markupLevel - 1])) {
            this.matchesThisLevel = this.markupSelectorItem.matchesElement(markupBlockIndex, elementBuffer, this.markupBlockMatchingCounter);
            if (matchesPreviousOrCurrentLevel(markupLevel)) {
                this.matchedMarkupLevels[markupLevel] = this.matchesThisLevel;
                if (this.next != null) {
                    return this.next.matchOpenElement(blockMatching, markupLevel, markupBlockIndex, elementBuffer);
                }
                if (blockMatching) {
                    return true;
                }
                return this.matchesThisLevel;
            } else if (this.matchesThisLevel) {
                this.matchedMarkupLevels[markupLevel] = true;
                return this.next == null;
            } else {
                return false;
            }
        } else if (matchesPreviousOrCurrentLevel(markupLevel)) {
            if (this.next != null) {
                return this.next.matchOpenElement(blockMatching, markupLevel, markupBlockIndex, elementBuffer);
            }
            return blockMatching;
        } else {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean matchProcessingInstruction(boolean blockMatching, int markupLevel, int markupBlockIndex) {
        checkMarkupLevel(markupLevel);
        if (this.markupSelectorItem.anyLevel() || markupLevel == 0 || (this.prev != null && this.prev.matchedMarkupLevels[markupLevel - 1])) {
            this.matchesThisLevel = this.markupSelectorItem.matchesProcessingInstruction(markupBlockIndex, this.markupBlockMatchingCounter);
            if (!matchesPreviousOrCurrentLevel(markupLevel)) {
                return this.matchesThisLevel && this.next == null;
            } else if (this.next != null) {
                return this.next.matchProcessingInstruction(blockMatching, markupLevel, markupBlockIndex);
            } else {
                if (blockMatching) {
                    return true;
                }
                return this.matchesThisLevel;
            }
        } else if (matchesPreviousOrCurrentLevel(markupLevel)) {
            if (this.next != null) {
                return this.next.matchProcessingInstruction(blockMatching, markupLevel, markupBlockIndex);
            }
            return blockMatching;
        } else {
            return false;
        }
    }

    private void checkMarkupLevel(int markupLevel) {
        if (markupLevel >= this.matchedMarkupLevels.length) {
            int newLen = Math.max(markupLevel + 1, this.matchedMarkupLevels.length + 10);
            boolean[] newMatchedMarkupLevels = new boolean[newLen];
            Arrays.fill(newMatchedMarkupLevels, false);
            System.arraycopy(this.matchedMarkupLevels, 0, newMatchedMarkupLevels, 0, this.matchedMarkupLevels.length);
            this.matchedMarkupLevels = newMatchedMarkupLevels;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeMatchesForLevel(int markupLevel) {
        if (this.matchedMarkupLevels.length > markupLevel) {
            this.matchedMarkupLevels[markupLevel] = false;
        }
        if (this.next == null) {
            return;
        }
        this.next.removeMatchesForLevel(markupLevel);
    }

    private boolean matchesPreviousOrCurrentLevel(int markupLevel) {
        this.markupLevelCheckerIndex = markupLevel;
        while (this.markupLevelCheckerIndex >= 0 && !this.matchedMarkupLevels[this.markupLevelCheckerIndex]) {
            this.markupLevelCheckerIndex--;
        }
        return this.markupLevelCheckerIndex >= 0;
    }
}