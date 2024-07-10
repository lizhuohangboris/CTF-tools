package org.unbescape.xml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/unbescape-1.1.6.RELEASE.jar:org/unbescape/xml/XmlEscapeSymbols.class */
public final class XmlEscapeSymbols {
    static final XmlEscapeSymbols XML10_SYMBOLS = Xml10EscapeSymbolsInitializer.initializeXml10(false);
    static final XmlEscapeSymbols XML11_SYMBOLS = Xml11EscapeSymbolsInitializer.initializeXml11(false);
    static final XmlEscapeSymbols XML10_ATTRIBUTE_SYMBOLS = Xml10EscapeSymbolsInitializer.initializeXml10(true);
    static final XmlEscapeSymbols XML11_ATTRIBUTE_SYMBOLS = Xml11EscapeSymbolsInitializer.initializeXml11(true);
    static final char LEVELS_LEN = 161;
    final byte[] ESCAPE_LEVELS = new byte[161];
    final int[] SORTED_CODEPOINTS;
    final char[][] SORTED_CERS_BY_CODEPOINT;
    final char[][] SORTED_CERS;
    final int[] SORTED_CODEPOINTS_BY_CER;
    final XmlCodepointValidator CODEPOINT_VALIDATOR;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Type inference failed for: r1v11, types: [char[], char[][]] */
    /* JADX WARN: Type inference failed for: r1v9, types: [char[], char[][]] */
    public XmlEscapeSymbols(References references, byte[] escapeLevels, XmlCodepointValidator codepointValidator) {
        this.CODEPOINT_VALIDATOR = codepointValidator;
        System.arraycopy(escapeLevels, 0, this.ESCAPE_LEVELS, 0, 161);
        int structureLen = references.references.size();
        List<char[]> cers = new ArrayList<>(structureLen + 5);
        List<Integer> codepoints = new ArrayList<>(structureLen + 5);
        for (Reference reference : references.references) {
            cers.add(reference.cer);
            codepoints.add(Integer.valueOf(reference.codepoint));
        }
        this.SORTED_CODEPOINTS = new int[structureLen];
        this.SORTED_CERS_BY_CODEPOINT = new char[structureLen];
        this.SORTED_CERS = new char[structureLen];
        this.SORTED_CODEPOINTS_BY_CER = new int[structureLen];
        List<char[]> cersOrdered = new ArrayList<>(cers);
        Collections.sort(cersOrdered, new Comparator<char[]>() { // from class: org.unbescape.xml.XmlEscapeSymbols.1
            @Override // java.util.Comparator
            public int compare(char[] o1, char[] o2) {
                return new String(o1).compareTo(new String(o2));
            }
        });
        List<Integer> codepointsOrdered = new ArrayList<>(codepoints);
        Collections.sort(codepointsOrdered);
        short s = 0;
        while (true) {
            short i = s;
            if (i >= structureLen) {
                break;
            }
            int codepoint = codepointsOrdered.get(i).intValue();
            this.SORTED_CODEPOINTS[i] = codepoint;
            short s2 = 0;
            while (true) {
                short j = s2;
                if (j < structureLen) {
                    if (codepoint != codepoints.get(j).intValue()) {
                        s2 = (short) (j + 1);
                    } else {
                        this.SORTED_CERS_BY_CODEPOINT[i] = cers.get(j);
                        break;
                    }
                } else {
                    break;
                }
            }
            s = (short) (i + 1);
        }
        short s3 = 0;
        while (true) {
            short i2 = s3;
            if (i2 < structureLen) {
                char[] cer = cersOrdered.get(i2);
                this.SORTED_CERS[i2] = cer;
                short s4 = 0;
                while (true) {
                    short j2 = s4;
                    if (j2 < structureLen) {
                        if (!Arrays.equals(cer, cers.get(j2))) {
                            s4 = (short) (j2 + 1);
                        } else {
                            this.SORTED_CODEPOINTS_BY_CER[i2] = codepoints.get(j2).intValue();
                            break;
                        }
                    } else {
                        break;
                    }
                }
                s3 = (short) (i2 + 1);
            } else {
                return;
            }
        }
    }

    private static int compare(char[] cer, String text, int start, int end) {
        int textLen = end - start;
        int maxCommon = Math.min(cer.length, textLen);
        int i = 1;
        while (i < maxCommon) {
            char tc = text.charAt(start + i);
            if (cer[i] < tc) {
                return -1;
            }
            if (cer[i] <= tc) {
                i++;
            } else {
                return 1;
            }
        }
        if (cer.length > i) {
            return 1;
        }
        if (textLen > i) {
            return -1;
        }
        return 0;
    }

    private static int compare(char[] cer, char[] text, int start, int end) {
        int textLen = end - start;
        int maxCommon = Math.min(cer.length, textLen);
        int i = 1;
        while (i < maxCommon) {
            char tc = text[start + i];
            if (cer[i] < tc) {
                return -1;
            }
            if (cer[i] <= tc) {
                i++;
            } else {
                return 1;
            }
        }
        if (cer.length > i) {
            return 1;
        }
        if (textLen > i) {
            return -1;
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int binarySearch(char[][] values, String text, int start, int end) {
        int low = 0;
        int high = values.length - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            char[] midVal = values[mid];
            int cmp = compare(midVal, text, start, end);
            if (cmp == -1) {
                low = mid + 1;
            } else if (cmp == 1) {
                high = mid - 1;
            } else {
                return mid;
            }
        }
        return Integer.MIN_VALUE;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int binarySearch(char[][] values, char[] text, int start, int end) {
        int low = 0;
        int high = values.length - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            char[] midVal = values[mid];
            int cmp = compare(midVal, text, start, end);
            if (cmp == -1) {
                low = mid + 1;
            } else if (cmp == 1) {
                high = mid - 1;
            } else {
                return mid;
            }
        }
        return Integer.MIN_VALUE;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/unbescape-1.1.6.RELEASE.jar:org/unbescape/xml/XmlEscapeSymbols$References.class */
    public static final class References {
        private final List<Reference> references = new ArrayList(200);

        /* JADX INFO: Access modifiers changed from: package-private */
        public void addReference(int codepoint, String cer) {
            this.references.add(new Reference(cer, codepoint));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/unbescape-1.1.6.RELEASE.jar:org/unbescape/xml/XmlEscapeSymbols$Reference.class */
    public static final class Reference {
        private final char[] cer;
        private final int codepoint;

        private Reference(String cer, int codepoint) {
            this.cer = cer.toCharArray();
            this.codepoint = codepoint;
        }
    }
}