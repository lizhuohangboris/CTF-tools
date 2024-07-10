package org.unbescape.xml;

import java.util.Arrays;
import org.springframework.asm.Opcodes;
import org.unbescape.xml.XmlEscapeSymbols;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/unbescape-1.1.6.RELEASE.jar:org/unbescape/xml/Xml10EscapeSymbolsInitializer.class */
final class Xml10EscapeSymbolsInitializer {
    public static XmlEscapeSymbols initializeXml10(boolean attributes) {
        XmlEscapeSymbols.References xml10References = new XmlEscapeSymbols.References();
        xml10References.addReference(34, "&quot;");
        xml10References.addReference(38, "&amp;");
        xml10References.addReference(39, "&apos;");
        xml10References.addReference(60, "&lt;");
        xml10References.addReference(62, "&gt;");
        byte[] escapeLevels = new byte[Opcodes.IF_ICMPLT];
        Arrays.fill(escapeLevels, (byte) 3);
        char c = 128;
        while (true) {
            char c2 = c;
            if (c2 >= 161) {
                break;
            }
            escapeLevels[c2] = 2;
            c = (char) (c2 + 1);
        }
        char c3 = 'A';
        while (true) {
            char c4 = c3;
            if (c4 > 'Z') {
                break;
            }
            escapeLevels[c4] = 4;
            c3 = (char) (c4 + 1);
        }
        char c5 = 'a';
        while (true) {
            char c6 = c5;
            if (c6 > 'z') {
                break;
            }
            escapeLevels[c6] = 4;
            c5 = (char) (c6 + 1);
        }
        char c7 = '0';
        while (true) {
            char c8 = c7;
            if (c8 > '9') {
                break;
            }
            escapeLevels[c8] = 4;
            c7 = (char) (c8 + 1);
        }
        escapeLevels[39] = 1;
        escapeLevels[34] = 1;
        escapeLevels[60] = 1;
        escapeLevels[62] = 1;
        escapeLevels[38] = 1;
        if (attributes) {
            escapeLevels[9] = 1;
            escapeLevels[10] = 1;
            escapeLevels[13] = 1;
        }
        char c9 = 127;
        while (true) {
            char c10 = c9;
            if (c10 > 132) {
                break;
            }
            escapeLevels[c10] = 1;
            c9 = (char) (c10 + 1);
        }
        char c11 = 134;
        while (true) {
            char c12 = c11;
            if (c12 <= 159) {
                escapeLevels[c12] = 1;
                c11 = (char) (c12 + 1);
            } else {
                return new XmlEscapeSymbols(xml10References, escapeLevels, new Xml10CodepointValidator());
            }
        }
    }

    private Xml10EscapeSymbolsInitializer() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/unbescape-1.1.6.RELEASE.jar:org/unbescape/xml/Xml10EscapeSymbolsInitializer$Xml10CodepointValidator.class */
    public static final class Xml10CodepointValidator implements XmlCodepointValidator {
        Xml10CodepointValidator() {
        }

        @Override // org.unbescape.xml.XmlCodepointValidator
        public boolean isValid(int codepoint) {
            if (codepoint < 32) {
                return codepoint == 9 || codepoint == 10 || codepoint == 13;
            } else if (codepoint <= 55295) {
                return true;
            } else {
                if (codepoint < 57344) {
                    return false;
                }
                if (codepoint > 65533 && codepoint < 65536) {
                    return false;
                }
                return true;
            }
        }
    }
}