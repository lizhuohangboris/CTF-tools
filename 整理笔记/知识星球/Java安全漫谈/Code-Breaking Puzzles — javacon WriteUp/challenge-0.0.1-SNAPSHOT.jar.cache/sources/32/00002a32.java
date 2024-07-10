package org.unbescape.html;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.asm.Opcodes;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/unbescape-1.1.6.RELEASE.jar:org/unbescape/html/HtmlEscapeSymbols.class */
public final class HtmlEscapeSymbols {
    static final int NCRS_BY_CODEPOINT_LEN = 12287;
    final Map<Integer, Short> NCRS_BY_CODEPOINT_OVERFLOW;
    static final char MAX_ASCII_CHAR = 127;
    final char[][] SORTED_NCRS;
    final int[] SORTED_CODEPOINTS;
    final int[][] DOUBLE_CODEPOINTS;
    static final short NO_NCR = 0;
    static final HtmlEscapeSymbols HTML4_SYMBOLS = Html4EscapeSymbolsInitializer.initializeHtml4();
    static final HtmlEscapeSymbols HTML5_SYMBOLS = Html5EscapeSymbolsInitializer.initializeHtml5();
    final short[] NCRS_BY_CODEPOINT = new short[NCRS_BY_CODEPOINT_LEN];
    final byte[] ESCAPE_LEVELS = new byte[Opcodes.LOR];

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Type inference failed for: r1v12, types: [char[], char[][]] */
    /* JADX WARN: Type inference failed for: r1v25, types: [int[], int[][]] */
    public HtmlEscapeSymbols(References references, byte[] escapeLevels) {
        System.arraycopy(escapeLevels, 0, this.ESCAPE_LEVELS, 0, Opcodes.LOR);
        List<char[]> ncrs = new ArrayList<>(references.references.size() + 5);
        List<Integer> codepoints = new ArrayList<>(references.references.size() + 5);
        List<int[]> doubleCodepoints = new ArrayList<>(100);
        Map<Integer, Short> ncrsByCodepointOverflow = new HashMap<>(20);
        for (Reference reference : references.references) {
            char[] referenceNcr = reference.ncr;
            int[] referenceCodepoints = reference.codepoints;
            ncrs.add(referenceNcr);
            if (referenceCodepoints.length == 1) {
                int referenceCodepoint = referenceCodepoints[0];
                codepoints.add(Integer.valueOf(referenceCodepoint));
            } else if (referenceCodepoints.length == 2) {
                doubleCodepoints.add(referenceCodepoints);
                codepoints.add(Integer.valueOf((-1) * doubleCodepoints.size()));
            } else {
                throw new RuntimeException("Unsupported codepoints #: " + referenceCodepoints.length + " for " + new String(referenceNcr));
            }
        }
        Arrays.fill(this.NCRS_BY_CODEPOINT, (short) 0);
        this.SORTED_NCRS = new char[ncrs.size()];
        this.SORTED_CODEPOINTS = new int[codepoints.size()];
        List<char[]> ncrsOrdered = new ArrayList<>(ncrs);
        Collections.sort(ncrsOrdered, new Comparator<char[]>() { // from class: org.unbescape.html.HtmlEscapeSymbols.1
            @Override // java.util.Comparator
            public int compare(char[] o1, char[] o2) {
                return HtmlEscapeSymbols.compare(o1, o2, 0, o2.length);
            }
        });
        short s = 0;
        while (true) {
            short i = s;
            if (i >= this.SORTED_NCRS.length) {
                break;
            }
            char[] ncr = ncrsOrdered.get(i);
            this.SORTED_NCRS[i] = ncr;
            short s2 = 0;
            while (true) {
                short j = s2;
                if (j >= this.SORTED_NCRS.length) {
                    break;
                } else if (!Arrays.equals(ncr, ncrs.get(j))) {
                    s2 = (short) (j + 1);
                } else {
                    int cp = codepoints.get(j).intValue();
                    this.SORTED_CODEPOINTS[i] = cp;
                    if (cp > 0) {
                        if (cp < NCRS_BY_CODEPOINT_LEN) {
                            if (this.NCRS_BY_CODEPOINT[cp] == 0) {
                                this.NCRS_BY_CODEPOINT[cp] = i;
                            } else {
                                int positionOfCurrent = positionInList(ncrs, this.SORTED_NCRS[this.NCRS_BY_CODEPOINT[cp]]);
                                int positionOfNew = positionInList(ncrs, ncr);
                                if (positionOfNew < positionOfCurrent) {
                                    this.NCRS_BY_CODEPOINT[cp] = i;
                                }
                            }
                        } else {
                            ncrsByCodepointOverflow.put(Integer.valueOf(cp), Short.valueOf(i));
                        }
                    }
                }
            }
            s = (short) (i + 1);
        }
        if (ncrsByCodepointOverflow.size() > 0) {
            this.NCRS_BY_CODEPOINT_OVERFLOW = ncrsByCodepointOverflow;
        } else {
            this.NCRS_BY_CODEPOINT_OVERFLOW = null;
        }
        if (doubleCodepoints.size() > 0) {
            this.DOUBLE_CODEPOINTS = new int[doubleCodepoints.size()];
            for (int i2 = 0; i2 < this.DOUBLE_CODEPOINTS.length; i2++) {
                this.DOUBLE_CODEPOINTS[i2] = doubleCodepoints.get(i2);
            }
            return;
        }
        this.DOUBLE_CODEPOINTS = null;
    }

    private static int positionInList(List<char[]> list, char[] element) {
        int i = 0;
        for (char[] e : list) {
            if (Arrays.equals(e, element)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    private static int compare(char[] ncr, String text, int start, int end) {
        int textLen = end - start;
        int maxCommon = Math.min(ncr.length, textLen);
        int i = 1;
        while (i < maxCommon) {
            char tc = text.charAt(start + i);
            if (ncr[i] < tc) {
                if (tc == ';') {
                    return 1;
                }
                return -1;
            } else if (ncr[i] <= tc) {
                i++;
            } else if (ncr[i] == ';') {
                return -1;
            } else {
                return 1;
            }
        }
        if (ncr.length > i) {
            if (ncr[i] == ';') {
                return -1;
            }
            return 1;
        } else if (textLen > i) {
            if (text.charAt(start + i) == ';') {
                return 1;
            }
            return -((textLen - i) + 10);
        } else {
            return 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int compare(char[] ncr, char[] text, int start, int end) {
        int textLen = end - start;
        int maxCommon = Math.min(ncr.length, textLen);
        int i = 1;
        while (i < maxCommon) {
            char tc = text[start + i];
            if (ncr[i] < tc) {
                if (tc == ';') {
                    return 1;
                }
                return -1;
            } else if (ncr[i] <= tc) {
                i++;
            } else if (ncr[i] == ';') {
                return -1;
            } else {
                return 1;
            }
        }
        if (ncr.length > i) {
            if (ncr[i] == ';') {
                return -1;
            }
            return 1;
        } else if (textLen > i) {
            if (text[start + i] == ';') {
                return 1;
            }
            return -((textLen - i) + 10);
        } else {
            return 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int binarySearch(char[][] values, String text, int start, int end) {
        int low = 0;
        int high = values.length - 1;
        int partialIndex = Integer.MIN_VALUE;
        int partialValue = Integer.MIN_VALUE;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            char[] midVal = values[mid];
            int cmp = compare(midVal, text, start, end);
            if (cmp == -1) {
                low = mid + 1;
            } else if (cmp == 1) {
                high = mid - 1;
            } else if (cmp < -10) {
                low = mid + 1;
                if (partialIndex == Integer.MIN_VALUE || partialValue < cmp) {
                    partialIndex = mid;
                    partialValue = cmp;
                }
            } else {
                return mid;
            }
        }
        if (partialIndex != Integer.MIN_VALUE) {
            return (-1) * (partialIndex + 10);
        }
        return Integer.MIN_VALUE;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int binarySearch(char[][] values, char[] text, int start, int end) {
        int low = 0;
        int high = values.length - 1;
        int partialIndex = Integer.MIN_VALUE;
        int partialValue = Integer.MIN_VALUE;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            char[] midVal = values[mid];
            int cmp = compare(midVal, text, start, end);
            if (cmp == -1) {
                low = mid + 1;
            } else if (cmp == 1) {
                high = mid - 1;
            } else if (cmp < -10) {
                low = mid + 1;
                if (partialIndex == Integer.MIN_VALUE || partialValue < cmp) {
                    partialIndex = mid;
                    partialValue = cmp;
                }
            } else {
                return mid;
            }
        }
        if (partialIndex != Integer.MIN_VALUE) {
            return (-1) * (partialIndex + 10);
        }
        return Integer.MIN_VALUE;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/unbescape-1.1.6.RELEASE.jar:org/unbescape/html/HtmlEscapeSymbols$References.class */
    public static final class References {
        private final List<Reference> references = new ArrayList(200);

        /* JADX INFO: Access modifiers changed from: package-private */
        public void addReference(int codepoint, String ncr) {
            this.references.add(new Reference(ncr, new int[]{codepoint}));
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void addReference(int codepoint0, int codepoint1, String ncr) {
            this.references.add(new Reference(ncr, new int[]{codepoint0, codepoint1}));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/unbescape-1.1.6.RELEASE.jar:org/unbescape/html/HtmlEscapeSymbols$Reference.class */
    public static final class Reference {
        private final char[] ncr;
        private final int[] codepoints;

        private Reference(String ncr, int[] codepoints) {
            this.ncr = ncr.toCharArray();
            this.codepoints = codepoints;
        }
    }
}