package org.attoparser.select;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/select/ParseSelection.class */
public final class ParseSelection {
    private int levelCounter = 0;
    ParseSelectionLevel[] levels;

    /* JADX INFO: Access modifiers changed from: package-private */
    public int subscribeLevel() {
        ParseSelectionLevel[] newLevels = new ParseSelectionLevel[this.levelCounter + 1];
        if (this.levels != null) {
            System.arraycopy(this.levels, 0, newLevels, 0, this.levelCounter);
        }
        this.levels = newLevels;
        this.levels[this.levelCounter] = new ParseSelectionLevel();
        int i = this.levelCounter;
        this.levelCounter = i + 1;
        return i;
    }

    public int getSelectionLevels() {
        return this.levelCounter;
    }

    public String[] getSelectors(int level) {
        if (level >= this.levelCounter) {
            throw new IllegalArgumentException("Cannot return current selection: max level is " + this.levelCounter + " (specified: " + level + ")");
        }
        if (this.levels == null) {
            return null;
        }
        return this.levels[level].selectors;
    }

    public String[] getCurrentSelection(int level) {
        if (level >= this.levelCounter) {
            throw new IllegalArgumentException("Cannot return current selection: max level is " + this.levelCounter + " (specified: " + level + ")");
        }
        if (this.levels == null) {
            return null;
        }
        return this.levels[level].getCurrentSelection();
    }

    public boolean isMatchingAny(int level) {
        if (level >= this.levelCounter) {
            throw new IllegalArgumentException("Cannot return current selection: max level is " + this.levelCounter + " (specified: " + level + ")");
        }
        if (this.levels == null) {
            return false;
        }
        return this.levels[level].isSelectionActive();
    }

    public boolean isMatchingAny() {
        if (this.levels == null) {
            return false;
        }
        int i = 0;
        int n = this.levelCounter;
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                if (this.levels[i].isSelectionActive()) {
                    return true;
                }
                i++;
            } else {
                return false;
            }
        }
    }

    public String toString() {
        if (this.levels.length == 0) {
            return "";
        }
        StringBuilder strBuilder = new StringBuilder(40);
        strBuilder.append(this.levels[0]);
        if (this.levels.length > 1) {
            for (int i = 1; i < this.levels.length; i++) {
                strBuilder.append(" -> ");
                strBuilder.append(this.levels[i]);
            }
        }
        return strBuilder.toString();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/select/ParseSelection$ParseSelectionLevel.class */
    static final class ParseSelectionLevel {
        String[] selectors;
        boolean[] selection;

        private ParseSelectionLevel() {
        }

        String[] getCurrentSelection() {
            if (this.selection == null) {
                return null;
            }
            int size = 0;
            int i = 0;
            int n = this.selectors.length;
            while (true) {
                int i2 = n;
                n--;
                if (i2 == 0) {
                    break;
                }
                if (this.selection[i]) {
                    size++;
                }
                i++;
            }
            if (size == this.selectors.length) {
                return this.selectors;
            }
            String[] currentSelection = new String[size];
            int j = 0;
            int i3 = 0;
            int n2 = this.selectors.length;
            while (true) {
                int i4 = n2;
                n2--;
                if (i4 != 0) {
                    if (this.selection[i3]) {
                        int i5 = j;
                        j++;
                        currentSelection[i5] = this.selectors[i3];
                    }
                    i3++;
                } else {
                    return currentSelection;
                }
            }
        }

        public boolean isSelectionActive() {
            if (this.selection == null) {
                return false;
            }
            int i = 0;
            int n = this.selectors.length;
            while (true) {
                int i2 = n;
                n--;
                if (i2 != 0) {
                    if (this.selection[i]) {
                        return true;
                    }
                    i++;
                } else {
                    return false;
                }
            }
        }

        public String toString() {
            StringBuilder strBuilder = new StringBuilder(20);
            strBuilder.append('[');
            if (this.selection != null) {
                for (int i = 0; i < this.selectors.length; i++) {
                    if (this.selection[i]) {
                        if (strBuilder.length() > 1) {
                            strBuilder.append(',');
                        }
                        strBuilder.append(this.selectors[i]);
                    }
                }
            }
            strBuilder.append(']');
            return strBuilder.toString();
        }
    }
}