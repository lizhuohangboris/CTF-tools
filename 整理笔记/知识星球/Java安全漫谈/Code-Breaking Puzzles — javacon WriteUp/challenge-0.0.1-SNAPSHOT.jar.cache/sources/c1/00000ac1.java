package org.apache.el.parser;

import ch.qos.logback.classic.spi.CallerData;
import ch.qos.logback.core.util.FileSize;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Deque;
import org.apache.coyote.http11.Constants;
import org.apache.tomcat.util.codec.binary.BaseNCodec;
import org.slf4j.Marker;
import org.springframework.aop.framework.autoproxy.target.QuickTargetSourceCreator;
import org.springframework.asm.Opcodes;
import org.springframework.asm.TypeReference;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.context.expression.StandardBeanExpressionResolver;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:org/apache/el/parser/ELParserTokenManager.class */
public class ELParserTokenManager implements ELParserConstants {
    Deque<Integer> deque;
    public PrintStream debugStream;
    static final long[] jjbitVec0 = {-2, -1, -1, -1};
    static final long[] jjbitVec2 = {0, 0, -1, -1};
    static final long[] jjbitVec3 = {2301339413881290750L, -16384, 4294967295L, 432345564227567616L};
    static final long[] jjbitVec4 = {0, 0, 0, -36028797027352577L};
    static final long[] jjbitVec5 = {0, -1, -1, -1};
    static final long[] jjbitVec6 = {-1, -1, 65535, 0};
    static final long[] jjbitVec7 = {-1, -1, 0, 0};
    static final long[] jjbitVec8 = {70368744177663L, 0, 0, 0};
    static final int[] jjnextStates = {0, 1, 3, 4, 2, 0, 1, 4, 2, 0, 1, 4, 5, 2, 0, 1, 2, 6, 16, 17, 18, 23, 24, 11, 12, 14, 6, 7, 9, 3, 4, 21, 22, 25, 26};
    public static final String[] jjstrLiteralImages = {"", null, "${", StandardBeanExpressionResolver.DEFAULT_EXPRESSION_PREFIX, null, null, null, null, "{", "}", null, null, null, null, "true", "false", BeanDefinitionParserDelegate.NULL_ELEMENT, ".", "(", ")", PropertyAccessor.PROPERTY_KEY_PREFIX, "]", ":", ";", ",", ">", "gt", "<", "lt", ">=", "ge", "<=", "le", "==", "eq", "!=", "ne", "!", "not", "&&", "and", "||", "or", "empty", "instanceof", "*", Marker.ANY_NON_NULL_MARKER, "-", CallerData.NA, "/", "div", QuickTargetSourceCreator.PREFIX_THREAD_LOCAL, "mod", "+=", "=", "->", null, null, null, null, null, null};
    public static final String[] lexStateNames = {"DEFAULT", "IN_EXPRESSION", "IN_SET_OR_MAP"};
    public static final int[] jjnewLexState = {-1, -1, 1, 1, -1, -1, -1, -1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    static final long[] jjtoToken = {2594073385365401359L};
    static final long[] jjtoSkip = {240};
    protected SimpleCharStream input_stream;
    private final int[] jjrounds;
    private final int[] jjstateSet;
    private final StringBuilder jjimage;
    private StringBuilder image;
    private int jjimageLen;
    private int lengthOfMatch;
    protected char curChar;
    int curLexState;
    int defaultLexState;
    int jjnewStateCnt;
    int jjround;
    int jjmatchedPos;
    int jjmatchedKind;

    public void setDebugStream(PrintStream ds) {
        this.debugStream = ds;
    }

    private final int jjStopStringLiteralDfa_0(int pos, long active0) {
        switch (pos) {
            case 0:
                if ((active0 & 12) != 0) {
                    this.jjmatchedKind = 1;
                    return 5;
                }
                return -1;
            default:
                return -1;
        }
    }

    private final int jjStartNfa_0(int pos, long active0) {
        return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0), pos + 1);
    }

    private int jjStopAtPos(int pos, int kind) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        return pos + 1;
    }

    private int jjMoveStringLiteralDfa0_0() {
        switch (this.curChar) {
            case '#':
                return jjMoveStringLiteralDfa1_0(8L);
            case '$':
                return jjMoveStringLiteralDfa1_0(4L);
            default:
                return jjMoveNfa_0(7, 0);
        }
    }

    private int jjMoveStringLiteralDfa1_0(long active0) {
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case '{':
                    if ((active0 & 4) != 0) {
                        return jjStopAtPos(1, 2);
                    }
                    if ((active0 & 8) != 0) {
                        return jjStopAtPos(1, 3);
                    }
                    break;
            }
            return jjStartNfa_0(0, active0);
        } catch (IOException e) {
            jjStopStringLiteralDfa_0(0, active0);
            return 1;
        }
    }

    private int jjMoveNfa_0(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 8;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            int i2 = this.jjround + 1;
            this.jjround = i2;
            if (i2 == Integer.MAX_VALUE) {
                ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1 << this.curChar;
                do {
                    i--;
                    switch (this.jjstateSet[i]) {
                        case 0:
                            if (((-103079215105L) & l) != 0) {
                                jjCheckNAddTwoStates(0, 1);
                                break;
                            }
                            break;
                        case 2:
                            if (((-103079215105L) & l) != 0) {
                                if (kind > 1) {
                                    kind = 1;
                                }
                                jjCheckNAddStates(0, 4);
                                break;
                            }
                            break;
                        case 3:
                            if (((-103079215105L) & l) != 0) {
                                jjCheckNAddTwoStates(3, 4);
                                break;
                            }
                            break;
                        case 4:
                            if ((103079215104L & l) != 0) {
                                jjCheckNAdd(5);
                                break;
                            }
                            break;
                        case 5:
                            if (((-103079215105L) & l) != 0) {
                                if (kind > 1) {
                                    kind = 1;
                                }
                                jjCheckNAddStates(5, 8);
                                break;
                            }
                            break;
                        case 6:
                            if ((103079215104L & l) != 0) {
                                if (kind > 1) {
                                    kind = 1;
                                }
                                jjCheckNAddStates(9, 13);
                                break;
                            }
                            break;
                        case 7:
                            if (((-103079215105L) & l) != 0) {
                                if (kind > 1) {
                                    kind = 1;
                                }
                                jjCheckNAddStates(0, 4);
                            } else if ((103079215104L & l) != 0) {
                                if (kind > 1) {
                                    kind = 1;
                                }
                                jjCheckNAdd(5);
                            }
                            if (((-103079215105L) & l) != 0) {
                                jjCheckNAddTwoStates(0, 1);
                                break;
                            }
                            break;
                    }
                } while (i != startsAt);
            } else if (this.curChar < 128) {
                long l2 = 1 << (this.curChar & '?');
                do {
                    i--;
                    switch (this.jjstateSet[i]) {
                        case 0:
                            if (((-268435457) & l2) != 0) {
                                jjCheckNAddTwoStates(0, 1);
                                break;
                            }
                            break;
                        case 1:
                            if (this.curChar == '\\') {
                                if (kind > 1) {
                                    kind = 1;
                                }
                                jjCheckNAddStates(14, 17);
                                break;
                            }
                            break;
                        case 2:
                            if (kind > 1) {
                                kind = 1;
                            }
                            jjCheckNAddStates(0, 4);
                            break;
                        case 3:
                            jjCheckNAddTwoStates(3, 4);
                            break;
                        case 5:
                            if (((-576460752571858945L) & l2) != 0) {
                                if (kind > 1) {
                                    kind = 1;
                                }
                                jjCheckNAddStates(5, 8);
                                break;
                            }
                            break;
                        case 7:
                            if (kind > 1) {
                                kind = 1;
                            }
                            jjCheckNAddStates(0, 4);
                            if (((-268435457) & l2) != 0) {
                                jjCheckNAddTwoStates(0, 1);
                                break;
                            } else if (this.curChar == '\\') {
                                if (kind > 1) {
                                    kind = 1;
                                }
                                jjCheckNAddStates(14, 17);
                                break;
                            }
                            break;
                    }
                } while (i != startsAt);
            } else {
                int hiByte = this.curChar >> '\b';
                int i1 = hiByte >> 6;
                long l1 = 1 << (hiByte & 63);
                int i22 = (this.curChar & 255) >> 6;
                long l22 = 1 << (this.curChar & '?');
                do {
                    i--;
                    switch (this.jjstateSet[i]) {
                        case 0:
                            if (jjCanMove_0(hiByte, i1, i22, l1, l22)) {
                                jjCheckNAddTwoStates(0, 1);
                                break;
                            }
                            break;
                        case 2:
                            if (jjCanMove_0(hiByte, i1, i22, l1, l22)) {
                                if (kind > 1) {
                                    kind = 1;
                                }
                                jjCheckNAddStates(0, 4);
                                break;
                            }
                            break;
                        case 3:
                            if (jjCanMove_0(hiByte, i1, i22, l1, l22)) {
                                jjCheckNAddTwoStates(3, 4);
                                break;
                            }
                            break;
                        case 5:
                            if (jjCanMove_0(hiByte, i1, i22, l1, l22)) {
                                if (kind > 1) {
                                    kind = 1;
                                }
                                jjCheckNAddStates(5, 8);
                                break;
                            }
                            break;
                        case 7:
                            if (jjCanMove_0(hiByte, i1, i22, l1, l22)) {
                                jjCheckNAddTwoStates(0, 1);
                            }
                            if (jjCanMove_0(hiByte, i1, i22, l1, l22)) {
                                if (kind > 1) {
                                    kind = 1;
                                }
                                jjCheckNAddStates(0, 4);
                                break;
                            }
                            break;
                    }
                } while (i != startsAt);
            }
            if (kind != Integer.MAX_VALUE) {
                this.jjmatchedKind = kind;
                this.jjmatchedPos = curPos;
                kind = Integer.MAX_VALUE;
            }
            curPos++;
            int i3 = this.jjnewStateCnt;
            i = i3;
            int i4 = startsAt;
            this.jjnewStateCnt = i4;
            int i5 = 8 - i4;
            startsAt = i5;
            if (i3 == i5) {
                return curPos;
            }
            try {
                this.curChar = this.input_stream.readChar();
            } catch (IOException e) {
                return curPos;
            }
        }
    }

    private final int jjStopStringLiteralDfa_2(int pos, long active0) {
        switch (pos) {
            case 0:
                if ((active0 & 131072) != 0) {
                    return 1;
                }
                if ((active0 & 5661751853039616L) != 0) {
                    this.jjmatchedKind = 56;
                    return 30;
                }
                return -1;
            case 1:
                if ((active0 & 4489650110464L) != 0) {
                    return 30;
                }
                if ((active0 & 5657262202929152L) != 0) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 1;
                    return 30;
                }
                return -1;
            case 2:
                if ((active0 & 5630873923747840L) != 0) {
                    return 30;
                }
                if ((active0 & 26388279181312L) != 0) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 2;
                    return 30;
                }
                return -1;
            case 3:
                if ((active0 & 81920) != 0) {
                    return 30;
                }
                if ((active0 & 26388279099392L) != 0) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 3;
                    return 30;
                }
                return -1;
            case 4:
                if ((active0 & 8796093054976L) != 0) {
                    return 30;
                }
                if ((active0 & 17592186044416L) != 0) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 4;
                    return 30;
                }
                return -1;
            case 5:
                if ((active0 & 17592186044416L) != 0) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 5;
                    return 30;
                }
                return -1;
            case 6:
                if ((active0 & 17592186044416L) != 0) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 6;
                    return 30;
                }
                return -1;
            case 7:
                if ((active0 & 17592186044416L) != 0) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 7;
                    return 30;
                }
                return -1;
            case 8:
                if ((active0 & 17592186044416L) != 0) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 8;
                    return 30;
                }
                return -1;
            default:
                return -1;
        }
    }

    private final int jjStartNfa_2(int pos, long active0) {
        return jjMoveNfa_2(jjStopStringLiteralDfa_2(pos, active0), pos + 1);
    }

    private int jjMoveStringLiteralDfa0_2() {
        switch (this.curChar) {
            case '!':
                this.jjmatchedKind = 37;
                return jjMoveStringLiteralDfa1_2(34359738368L);
            case '\"':
            case '#':
            case '$':
            case '\'':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case '@':
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case TypeReference.CONSTRUCTOR_REFERENCE /* 69 */:
            case 'F':
            case TypeReference.CAST /* 71 */:
            case 'H':
            case 'I':
            case 'J':
            case TypeReference.METHOD_REFERENCE_TYPE_ARGUMENT /* 75 */:
            case BaseNCodec.MIME_CHUNK_SIZE /* 76 */:
            case 'M':
            case 'N':
            case Opcodes.IASTORE /* 79 */:
            case 'P':
            case Opcodes.FASTORE /* 81 */:
            case Opcodes.DASTORE /* 82 */:
            case 'S':
            case Opcodes.BASTORE /* 84 */:
            case Opcodes.CASTORE /* 85 */:
            case Opcodes.SASTORE /* 86 */:
            case Opcodes.POP /* 87 */:
            case 'X':
            case 'Y':
            case 'Z':
            case '\\':
            case Opcodes.DUP2_X2 /* 94 */:
            case Opcodes.SWAP /* 95 */:
            case '`':
            case Opcodes.FADD /* 98 */:
            case 'c':
            case 'h':
            case Opcodes.FMUL /* 106 */:
            case Opcodes.DMUL /* 107 */:
            case 'p':
            case Opcodes.LREM /* 113 */:
            case Opcodes.FREM /* 114 */:
            case 's':
            case Opcodes.LNEG /* 117 */:
            case Opcodes.FNEG /* 118 */:
            case Opcodes.DNEG /* 119 */:
            case 'x':
            case Opcodes.LSHL /* 121 */:
            case 'z':
            default:
                return jjMoveNfa_2(0, 0);
            case '%':
                return jjStopAtPos(0, 51);
            case '&':
                return jjMoveStringLiteralDfa1_2(549755813888L);
            case '(':
                return jjStopAtPos(0, 18);
            case ')':
                return jjStopAtPos(0, 19);
            case '*':
                return jjStopAtPos(0, 45);
            case ELParserConstants.EMPTY /* 43 */:
                this.jjmatchedKind = 46;
                return jjMoveStringLiteralDfa1_2(9007199254740992L);
            case ',':
                return jjStopAtPos(0, 24);
            case '-':
                this.jjmatchedKind = 47;
                return jjMoveStringLiteralDfa1_2(36028797018963968L);
            case '.':
                return jjStartNfaWithStates_2(0, 17, 1);
            case '/':
                return jjStopAtPos(0, 49);
            case ':':
                return jjStopAtPos(0, 22);
            case ';':
                return jjStopAtPos(0, 23);
            case ELParserConstants.DIGIT /* 60 */:
                this.jjmatchedKind = 27;
                return jjMoveStringLiteralDfa1_2(2147483648L);
            case '=':
                this.jjmatchedKind = 54;
                return jjMoveStringLiteralDfa1_2(8589934592L);
            case '>':
                this.jjmatchedKind = 25;
                return jjMoveStringLiteralDfa1_2(536870912L);
            case Constants.QUESTION /* 63 */:
                return jjStopAtPos(0, 48);
            case '[':
                return jjStopAtPos(0, 20);
            case ']':
                return jjStopAtPos(0, 21);
            case 'a':
                return jjMoveStringLiteralDfa1_2(1099511627776L);
            case 'd':
                return jjMoveStringLiteralDfa1_2(1125899906842624L);
            case 'e':
                return jjMoveStringLiteralDfa1_2(8813272891392L);
            case Opcodes.FSUB /* 102 */:
                return jjMoveStringLiteralDfa1_2(32768L);
            case Opcodes.DSUB /* 103 */:
                return jjMoveStringLiteralDfa1_2(1140850688L);
            case Opcodes.LMUL /* 105 */:
                return jjMoveStringLiteralDfa1_2(17592186044416L);
            case 'l':
                return jjMoveStringLiteralDfa1_2(4563402752L);
            case Opcodes.LDIV /* 109 */:
                return jjMoveStringLiteralDfa1_2(4503599627370496L);
            case Opcodes.FDIV /* 110 */:
                return jjMoveStringLiteralDfa1_2(343597449216L);
            case Opcodes.DDIV /* 111 */:
                return jjMoveStringLiteralDfa1_2(4398046511104L);
            case 't':
                return jjMoveStringLiteralDfa1_2(16384L);
            case '{':
                return jjStopAtPos(0, 8);
            case '|':
                return jjMoveStringLiteralDfa1_2(2199023255552L);
            case '}':
                return jjStopAtPos(0, 9);
        }
    }

    private int jjMoveStringLiteralDfa1_2(long active0) {
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case '&':
                    if ((active0 & 549755813888L) != 0) {
                        return jjStopAtPos(1, 39);
                    }
                    break;
                case '=':
                    if ((active0 & 536870912) != 0) {
                        return jjStopAtPos(1, 29);
                    }
                    if ((active0 & 2147483648L) != 0) {
                        return jjStopAtPos(1, 31);
                    }
                    if ((active0 & 8589934592L) != 0) {
                        return jjStopAtPos(1, 33);
                    }
                    if ((active0 & 34359738368L) != 0) {
                        return jjStopAtPos(1, 35);
                    }
                    if ((active0 & 9007199254740992L) != 0) {
                        return jjStopAtPos(1, 53);
                    }
                    break;
                case '>':
                    if ((active0 & 36028797018963968L) != 0) {
                        return jjStopAtPos(1, 55);
                    }
                    break;
                case 'a':
                    return jjMoveStringLiteralDfa2_2(active0, 32768L);
                case 'e':
                    if ((active0 & FileSize.GB_COEFFICIENT) != 0) {
                        return jjStartNfaWithStates_2(1, 30, 30);
                    }
                    if ((active0 & 4294967296L) != 0) {
                        return jjStartNfaWithStates_2(1, 32, 30);
                    }
                    if ((active0 & 68719476736L) != 0) {
                        return jjStartNfaWithStates_2(1, 36, 30);
                    }
                    break;
                case Opcodes.LMUL /* 105 */:
                    return jjMoveStringLiteralDfa2_2(active0, 1125899906842624L);
                case Opcodes.LDIV /* 109 */:
                    return jjMoveStringLiteralDfa2_2(active0, 8796093022208L);
                case Opcodes.FDIV /* 110 */:
                    return jjMoveStringLiteralDfa2_2(active0, 18691697672192L);
                case Opcodes.DDIV /* 111 */:
                    return jjMoveStringLiteralDfa2_2(active0, 4503874505277440L);
                case Opcodes.LREM /* 113 */:
                    if ((active0 & 17179869184L) != 0) {
                        return jjStartNfaWithStates_2(1, 34, 30);
                    }
                    break;
                case Opcodes.FREM /* 114 */:
                    if ((active0 & 4398046511104L) != 0) {
                        return jjStartNfaWithStates_2(1, 42, 30);
                    }
                    return jjMoveStringLiteralDfa2_2(active0, 16384L);
                case 't':
                    if ((active0 & 67108864) != 0) {
                        return jjStartNfaWithStates_2(1, 26, 30);
                    }
                    if ((active0 & 268435456) != 0) {
                        return jjStartNfaWithStates_2(1, 28, 30);
                    }
                    break;
                case Opcodes.LNEG /* 117 */:
                    return jjMoveStringLiteralDfa2_2(active0, 65536L);
                case '|':
                    if ((active0 & 2199023255552L) != 0) {
                        return jjStopAtPos(1, 41);
                    }
                    break;
            }
            return jjStartNfa_2(0, active0);
        } catch (IOException e) {
            jjStopStringLiteralDfa_2(0, active0);
            return 1;
        }
    }

    private int jjMoveStringLiteralDfa2_2(long old0, long active0) {
        long active02 = active0 & old0;
        if (active02 == 0) {
            return jjStartNfa_2(0, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case 'd':
                    if ((active02 & 1099511627776L) != 0) {
                        return jjStartNfaWithStates_2(2, 40, 30);
                    }
                    if ((active02 & 4503599627370496L) != 0) {
                        return jjStartNfaWithStates_2(2, 52, 30);
                    }
                    break;
                case 'l':
                    return jjMoveStringLiteralDfa3_2(active02, 98304L);
                case 'p':
                    return jjMoveStringLiteralDfa3_2(active02, 8796093022208L);
                case 's':
                    return jjMoveStringLiteralDfa3_2(active02, 17592186044416L);
                case 't':
                    if ((active02 & 274877906944L) != 0) {
                        return jjStartNfaWithStates_2(2, 38, 30);
                    }
                    break;
                case Opcodes.LNEG /* 117 */:
                    return jjMoveStringLiteralDfa3_2(active02, 16384L);
                case Opcodes.FNEG /* 118 */:
                    if ((active02 & 1125899906842624L) != 0) {
                        return jjStartNfaWithStates_2(2, 50, 30);
                    }
                    break;
            }
            return jjStartNfa_2(1, active02);
        } catch (IOException e) {
            jjStopStringLiteralDfa_2(1, active02);
            return 2;
        }
    }

    private int jjMoveStringLiteralDfa3_2(long old0, long active0) {
        long active02 = active0 & old0;
        if (active02 == 0) {
            return jjStartNfa_2(1, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case 'e':
                    if ((active02 & 16384) != 0) {
                        return jjStartNfaWithStates_2(3, 14, 30);
                    }
                    break;
                case 'l':
                    if ((active02 & 65536) != 0) {
                        return jjStartNfaWithStates_2(3, 16, 30);
                    }
                    break;
                case 's':
                    return jjMoveStringLiteralDfa4_2(active02, 32768L);
                case 't':
                    return jjMoveStringLiteralDfa4_2(active02, 26388279066624L);
            }
            return jjStartNfa_2(2, active02);
        } catch (IOException e) {
            jjStopStringLiteralDfa_2(2, active02);
            return 3;
        }
    }

    private int jjMoveStringLiteralDfa4_2(long old0, long active0) {
        long active02 = active0 & old0;
        if (active02 == 0) {
            return jjStartNfa_2(2, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case 'a':
                    return jjMoveStringLiteralDfa5_2(active02, 17592186044416L);
                case 'e':
                    if ((active02 & 32768) != 0) {
                        return jjStartNfaWithStates_2(4, 15, 30);
                    }
                    break;
                case Opcodes.LSHL /* 121 */:
                    if ((active02 & 8796093022208L) != 0) {
                        return jjStartNfaWithStates_2(4, 43, 30);
                    }
                    break;
            }
            return jjStartNfa_2(3, active02);
        } catch (IOException e) {
            jjStopStringLiteralDfa_2(3, active02);
            return 4;
        }
    }

    private int jjMoveStringLiteralDfa5_2(long old0, long active0) {
        long active02 = active0 & old0;
        if (active02 == 0) {
            return jjStartNfa_2(3, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case Opcodes.FDIV /* 110 */:
                    return jjMoveStringLiteralDfa6_2(active02, 17592186044416L);
                default:
                    return jjStartNfa_2(4, active02);
            }
        } catch (IOException e) {
            jjStopStringLiteralDfa_2(4, active02);
            return 5;
        }
    }

    private int jjMoveStringLiteralDfa6_2(long old0, long active0) {
        long active02 = active0 & old0;
        if (active02 == 0) {
            return jjStartNfa_2(4, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case 'c':
                    return jjMoveStringLiteralDfa7_2(active02, 17592186044416L);
                default:
                    return jjStartNfa_2(5, active02);
            }
        } catch (IOException e) {
            jjStopStringLiteralDfa_2(5, active02);
            return 6;
        }
    }

    private int jjMoveStringLiteralDfa7_2(long old0, long active0) {
        long active02 = active0 & old0;
        if (active02 == 0) {
            return jjStartNfa_2(5, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case 'e':
                    return jjMoveStringLiteralDfa8_2(active02, 17592186044416L);
                default:
                    return jjStartNfa_2(6, active02);
            }
        } catch (IOException e) {
            jjStopStringLiteralDfa_2(6, active02);
            return 7;
        }
    }

    private int jjMoveStringLiteralDfa8_2(long old0, long active0) {
        long active02 = active0 & old0;
        if (active02 == 0) {
            return jjStartNfa_2(6, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case Opcodes.DDIV /* 111 */:
                    return jjMoveStringLiteralDfa9_2(active02, 17592186044416L);
                default:
                    return jjStartNfa_2(7, active02);
            }
        } catch (IOException e) {
            jjStopStringLiteralDfa_2(7, active02);
            return 8;
        }
    }

    private int jjMoveStringLiteralDfa9_2(long old0, long active0) {
        long active02 = active0 & old0;
        if (active02 == 0) {
            return jjStartNfa_2(7, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case Opcodes.FSUB /* 102 */:
                    if ((active02 & 17592186044416L) != 0) {
                        return jjStartNfaWithStates_2(9, 44, 30);
                    }
                    break;
            }
            return jjStartNfa_2(8, active02);
        } catch (IOException e) {
            jjStopStringLiteralDfa_2(8, active02);
            return 9;
        }
    }

    private int jjStartNfaWithStates_2(int pos, int kind, int state) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        try {
            this.curChar = this.input_stream.readChar();
            return jjMoveNfa_2(state, pos + 1);
        } catch (IOException e) {
            return pos + 1;
        }
    }

    private int jjMoveNfa_2(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 30;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            int i2 = this.jjround + 1;
            this.jjround = i2;
            if (i2 == Integer.MAX_VALUE) {
                ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1 << this.curChar;
                do {
                    i--;
                    switch (this.jjstateSet[i]) {
                        case 0:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 10) {
                                    kind = 10;
                                }
                                jjCheckNAddStates(18, 22);
                                break;
                            } else if ((103079215104L & l) != 0) {
                                if (kind > 56) {
                                    kind = 56;
                                }
                                jjCheckNAddTwoStates(28, 29);
                                break;
                            } else if (this.curChar == '\'') {
                                jjCheckNAddStates(23, 25);
                                break;
                            } else if (this.curChar == '\"') {
                                jjCheckNAddStates(26, 28);
                                break;
                            } else if (this.curChar == '.') {
                                jjCheckNAdd(1);
                                break;
                            }
                            break;
                        case 1:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 11) {
                                    kind = 11;
                                }
                                jjCheckNAddTwoStates(1, 2);
                                break;
                            }
                            break;
                        case 3:
                            if ((43980465111040L & l) != 0) {
                                jjCheckNAdd(4);
                                break;
                            }
                            break;
                        case 4:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 11) {
                                    kind = 11;
                                }
                                jjCheckNAdd(4);
                                break;
                            }
                            break;
                        case 5:
                            if (this.curChar == '\"') {
                                jjCheckNAddStates(26, 28);
                                break;
                            }
                            break;
                        case 6:
                            if (((-17179869185L) & l) != 0) {
                                jjCheckNAddStates(26, 28);
                                break;
                            }
                            break;
                        case 8:
                            if ((566935683072L & l) != 0) {
                                jjCheckNAddStates(26, 28);
                                break;
                            }
                            break;
                        case 9:
                            if (this.curChar == '\"' && kind > 13) {
                                kind = 13;
                                break;
                            }
                            break;
                        case 10:
                            if (this.curChar == '\'') {
                                jjCheckNAddStates(23, 25);
                                break;
                            }
                            break;
                        case 11:
                            if (((-549755813889L) & l) != 0) {
                                jjCheckNAddStates(23, 25);
                                break;
                            }
                            break;
                        case 13:
                            if ((566935683072L & l) != 0) {
                                jjCheckNAddStates(23, 25);
                                break;
                            }
                            break;
                        case 14:
                            if (this.curChar == '\'' && kind > 13) {
                                kind = 13;
                                break;
                            }
                            break;
                        case 15:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 10) {
                                    kind = 10;
                                }
                                jjCheckNAddStates(18, 22);
                                break;
                            }
                            break;
                        case 16:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 10) {
                                    kind = 10;
                                }
                                jjCheckNAdd(16);
                                break;
                            }
                            break;
                        case 17:
                            if ((287948901175001088L & l) != 0) {
                                jjCheckNAddTwoStates(17, 18);
                                break;
                            }
                            break;
                        case 18:
                            if (this.curChar == '.') {
                                if (kind > 11) {
                                    kind = 11;
                                }
                                jjCheckNAddTwoStates(19, 20);
                                break;
                            }
                            break;
                        case 19:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 11) {
                                    kind = 11;
                                }
                                jjCheckNAddTwoStates(19, 20);
                                break;
                            }
                            break;
                        case 21:
                            if ((43980465111040L & l) != 0) {
                                jjCheckNAdd(22);
                                break;
                            }
                            break;
                        case 22:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 11) {
                                    kind = 11;
                                }
                                jjCheckNAdd(22);
                                break;
                            }
                            break;
                        case 23:
                            if ((287948901175001088L & l) != 0) {
                                jjCheckNAddTwoStates(23, 24);
                                break;
                            }
                            break;
                        case 25:
                            if ((43980465111040L & l) != 0) {
                                jjCheckNAdd(26);
                                break;
                            }
                            break;
                        case 26:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 11) {
                                    kind = 11;
                                }
                                jjCheckNAdd(26);
                                break;
                            }
                            break;
                        case 27:
                            if ((103079215104L & l) != 0) {
                                if (kind > 56) {
                                    kind = 56;
                                }
                                jjCheckNAddTwoStates(28, 29);
                                break;
                            }
                            break;
                        case 28:
                            if ((287948969894477824L & l) != 0) {
                                if (kind > 56) {
                                    kind = 56;
                                }
                                jjCheckNAdd(28);
                                break;
                            }
                            break;
                        case 29:
                            if ((287948969894477824L & l) != 0) {
                                if (kind > 57) {
                                    kind = 57;
                                }
                                jjCheckNAdd(29);
                                break;
                            }
                            break;
                        case 30:
                            if ((287948969894477824L & l) != 0) {
                                if (kind > 57) {
                                    kind = 57;
                                }
                                jjCheckNAdd(29);
                            }
                            if ((287948969894477824L & l) != 0) {
                                if (kind > 56) {
                                    kind = 56;
                                }
                                jjCheckNAdd(28);
                                break;
                            }
                            break;
                    }
                } while (i != startsAt);
            } else if (this.curChar < 128) {
                long l2 = 1 << (this.curChar & '?');
                do {
                    i--;
                    switch (this.jjstateSet[i]) {
                        case 0:
                            if ((576460745995190270L & l2) != 0) {
                                if (kind > 56) {
                                    kind = 56;
                                }
                                jjCheckNAddTwoStates(28, 29);
                                break;
                            }
                            break;
                        case 2:
                            if ((137438953504L & l2) != 0) {
                                jjAddStates(29, 30);
                                break;
                            }
                            break;
                        case 6:
                            if (((-268435457) & l2) != 0) {
                                jjCheckNAddStates(26, 28);
                                break;
                            }
                            break;
                        case 7:
                            if (this.curChar == '\\') {
                                int[] iArr = this.jjstateSet;
                                int i3 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i3 + 1;
                                iArr[i3] = 8;
                                break;
                            }
                            break;
                        case 8:
                            if (this.curChar == '\\') {
                                jjCheckNAddStates(26, 28);
                                break;
                            }
                            break;
                        case 11:
                            if (((-268435457) & l2) != 0) {
                                jjCheckNAddStates(23, 25);
                                break;
                            }
                            break;
                        case 12:
                            if (this.curChar == '\\') {
                                int[] iArr2 = this.jjstateSet;
                                int i4 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i4 + 1;
                                iArr2[i4] = 13;
                                break;
                            }
                            break;
                        case 13:
                            if (this.curChar == '\\') {
                                jjCheckNAddStates(23, 25);
                                break;
                            }
                            break;
                        case 20:
                            if ((137438953504L & l2) != 0) {
                                jjAddStates(31, 32);
                                break;
                            }
                            break;
                        case 24:
                            if ((137438953504L & l2) != 0) {
                                jjAddStates(33, 34);
                                break;
                            }
                            break;
                        case 28:
                            if ((576460745995190270L & l2) != 0) {
                                if (kind > 56) {
                                    kind = 56;
                                }
                                jjCheckNAdd(28);
                                break;
                            }
                            break;
                        case 29:
                            if ((576460745995190270L & l2) != 0) {
                                if (kind > 57) {
                                    kind = 57;
                                }
                                jjCheckNAdd(29);
                                break;
                            }
                            break;
                        case 30:
                            if ((576460745995190270L & l2) != 0) {
                                if (kind > 57) {
                                    kind = 57;
                                }
                                jjCheckNAdd(29);
                            }
                            if ((576460745995190270L & l2) != 0) {
                                if (kind > 56) {
                                    kind = 56;
                                }
                                jjCheckNAdd(28);
                                break;
                            }
                            break;
                    }
                } while (i != startsAt);
            } else {
                int hiByte = this.curChar >> '\b';
                int i1 = hiByte >> 6;
                long l1 = 1 << (hiByte & 63);
                int i22 = (this.curChar & 255) >> 6;
                long l22 = 1 << (this.curChar & '?');
                do {
                    i--;
                    switch (this.jjstateSet[i]) {
                        case 0:
                            if (jjCanMove_1(hiByte, i1, i22, l1, l22)) {
                                if (kind > 56) {
                                    kind = 56;
                                }
                                jjCheckNAddTwoStates(28, 29);
                                break;
                            }
                            break;
                        case 6:
                            if (jjCanMove_0(hiByte, i1, i22, l1, l22)) {
                                jjAddStates(26, 28);
                                break;
                            }
                            break;
                        case 11:
                            if (jjCanMove_0(hiByte, i1, i22, l1, l22)) {
                                jjAddStates(23, 25);
                                break;
                            }
                            break;
                        case 28:
                            if (jjCanMove_1(hiByte, i1, i22, l1, l22)) {
                                if (kind > 56) {
                                    kind = 56;
                                }
                                jjCheckNAdd(28);
                                break;
                            }
                            break;
                        case 29:
                            if (jjCanMove_1(hiByte, i1, i22, l1, l22)) {
                                if (kind > 57) {
                                    kind = 57;
                                }
                                jjCheckNAdd(29);
                                break;
                            }
                            break;
                        case 30:
                            if (jjCanMove_1(hiByte, i1, i22, l1, l22)) {
                                if (kind > 56) {
                                    kind = 56;
                                }
                                jjCheckNAdd(28);
                            }
                            if (jjCanMove_1(hiByte, i1, i22, l1, l22)) {
                                if (kind > 57) {
                                    kind = 57;
                                }
                                jjCheckNAdd(29);
                                break;
                            }
                            break;
                    }
                } while (i != startsAt);
            }
            if (kind != Integer.MAX_VALUE) {
                this.jjmatchedKind = kind;
                this.jjmatchedPos = curPos;
                kind = Integer.MAX_VALUE;
            }
            curPos++;
            int i5 = this.jjnewStateCnt;
            i = i5;
            int i6 = startsAt;
            this.jjnewStateCnt = i6;
            int i7 = 30 - i6;
            startsAt = i7;
            if (i5 == i7) {
                return curPos;
            }
            try {
                this.curChar = this.input_stream.readChar();
            } catch (IOException e) {
                return curPos;
            }
        }
    }

    private final int jjStopStringLiteralDfa_1(int pos, long active0) {
        switch (pos) {
            case 0:
                if ((active0 & 131072) != 0) {
                    return 1;
                }
                if ((active0 & 5661751853039616L) != 0) {
                    this.jjmatchedKind = 56;
                    return 30;
                }
                return -1;
            case 1:
                if ((active0 & 4489650110464L) != 0) {
                    return 30;
                }
                if ((active0 & 5657262202929152L) != 0) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 1;
                    return 30;
                }
                return -1;
            case 2:
                if ((active0 & 5630873923747840L) != 0) {
                    return 30;
                }
                if ((active0 & 26388279181312L) != 0) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 2;
                    return 30;
                }
                return -1;
            case 3:
                if ((active0 & 81920) != 0) {
                    return 30;
                }
                if ((active0 & 26388279099392L) != 0) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 3;
                    return 30;
                }
                return -1;
            case 4:
                if ((active0 & 8796093054976L) != 0) {
                    return 30;
                }
                if ((active0 & 17592186044416L) != 0) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 4;
                    return 30;
                }
                return -1;
            case 5:
                if ((active0 & 17592186044416L) != 0) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 5;
                    return 30;
                }
                return -1;
            case 6:
                if ((active0 & 17592186044416L) != 0) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 6;
                    return 30;
                }
                return -1;
            case 7:
                if ((active0 & 17592186044416L) != 0) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 7;
                    return 30;
                }
                return -1;
            case 8:
                if ((active0 & 17592186044416L) != 0) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 8;
                    return 30;
                }
                return -1;
            default:
                return -1;
        }
    }

    private final int jjStartNfa_1(int pos, long active0) {
        return jjMoveNfa_1(jjStopStringLiteralDfa_1(pos, active0), pos + 1);
    }

    private int jjMoveStringLiteralDfa0_1() {
        switch (this.curChar) {
            case '!':
                this.jjmatchedKind = 37;
                return jjMoveStringLiteralDfa1_1(34359738368L);
            case '\"':
            case '#':
            case '$':
            case '\'':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case '@':
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case TypeReference.CONSTRUCTOR_REFERENCE /* 69 */:
            case 'F':
            case TypeReference.CAST /* 71 */:
            case 'H':
            case 'I':
            case 'J':
            case TypeReference.METHOD_REFERENCE_TYPE_ARGUMENT /* 75 */:
            case BaseNCodec.MIME_CHUNK_SIZE /* 76 */:
            case 'M':
            case 'N':
            case Opcodes.IASTORE /* 79 */:
            case 'P':
            case Opcodes.FASTORE /* 81 */:
            case Opcodes.DASTORE /* 82 */:
            case 'S':
            case Opcodes.BASTORE /* 84 */:
            case Opcodes.CASTORE /* 85 */:
            case Opcodes.SASTORE /* 86 */:
            case Opcodes.POP /* 87 */:
            case 'X':
            case 'Y':
            case 'Z':
            case '\\':
            case Opcodes.DUP2_X2 /* 94 */:
            case Opcodes.SWAP /* 95 */:
            case '`':
            case Opcodes.FADD /* 98 */:
            case 'c':
            case 'h':
            case Opcodes.FMUL /* 106 */:
            case Opcodes.DMUL /* 107 */:
            case 'p':
            case Opcodes.LREM /* 113 */:
            case Opcodes.FREM /* 114 */:
            case 's':
            case Opcodes.LNEG /* 117 */:
            case Opcodes.FNEG /* 118 */:
            case Opcodes.DNEG /* 119 */:
            case 'x':
            case Opcodes.LSHL /* 121 */:
            case 'z':
            default:
                return jjMoveNfa_1(0, 0);
            case '%':
                return jjStopAtPos(0, 51);
            case '&':
                return jjMoveStringLiteralDfa1_1(549755813888L);
            case '(':
                return jjStopAtPos(0, 18);
            case ')':
                return jjStopAtPos(0, 19);
            case '*':
                return jjStopAtPos(0, 45);
            case ELParserConstants.EMPTY /* 43 */:
                this.jjmatchedKind = 46;
                return jjMoveStringLiteralDfa1_1(9007199254740992L);
            case ',':
                return jjStopAtPos(0, 24);
            case '-':
                this.jjmatchedKind = 47;
                return jjMoveStringLiteralDfa1_1(36028797018963968L);
            case '.':
                return jjStartNfaWithStates_1(0, 17, 1);
            case '/':
                return jjStopAtPos(0, 49);
            case ':':
                return jjStopAtPos(0, 22);
            case ';':
                return jjStopAtPos(0, 23);
            case ELParserConstants.DIGIT /* 60 */:
                this.jjmatchedKind = 27;
                return jjMoveStringLiteralDfa1_1(2147483648L);
            case '=':
                this.jjmatchedKind = 54;
                return jjMoveStringLiteralDfa1_1(8589934592L);
            case '>':
                this.jjmatchedKind = 25;
                return jjMoveStringLiteralDfa1_1(536870912L);
            case Constants.QUESTION /* 63 */:
                return jjStopAtPos(0, 48);
            case '[':
                return jjStopAtPos(0, 20);
            case ']':
                return jjStopAtPos(0, 21);
            case 'a':
                return jjMoveStringLiteralDfa1_1(1099511627776L);
            case 'd':
                return jjMoveStringLiteralDfa1_1(1125899906842624L);
            case 'e':
                return jjMoveStringLiteralDfa1_1(8813272891392L);
            case Opcodes.FSUB /* 102 */:
                return jjMoveStringLiteralDfa1_1(32768L);
            case Opcodes.DSUB /* 103 */:
                return jjMoveStringLiteralDfa1_1(1140850688L);
            case Opcodes.LMUL /* 105 */:
                return jjMoveStringLiteralDfa1_1(17592186044416L);
            case 'l':
                return jjMoveStringLiteralDfa1_1(4563402752L);
            case Opcodes.LDIV /* 109 */:
                return jjMoveStringLiteralDfa1_1(4503599627370496L);
            case Opcodes.FDIV /* 110 */:
                return jjMoveStringLiteralDfa1_1(343597449216L);
            case Opcodes.DDIV /* 111 */:
                return jjMoveStringLiteralDfa1_1(4398046511104L);
            case 't':
                return jjMoveStringLiteralDfa1_1(16384L);
            case '{':
                return jjStopAtPos(0, 8);
            case '|':
                return jjMoveStringLiteralDfa1_1(2199023255552L);
            case '}':
                return jjStopAtPos(0, 9);
        }
    }

    private int jjMoveStringLiteralDfa1_1(long active0) {
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case '&':
                    if ((active0 & 549755813888L) != 0) {
                        return jjStopAtPos(1, 39);
                    }
                    break;
                case '=':
                    if ((active0 & 536870912) != 0) {
                        return jjStopAtPos(1, 29);
                    }
                    if ((active0 & 2147483648L) != 0) {
                        return jjStopAtPos(1, 31);
                    }
                    if ((active0 & 8589934592L) != 0) {
                        return jjStopAtPos(1, 33);
                    }
                    if ((active0 & 34359738368L) != 0) {
                        return jjStopAtPos(1, 35);
                    }
                    if ((active0 & 9007199254740992L) != 0) {
                        return jjStopAtPos(1, 53);
                    }
                    break;
                case '>':
                    if ((active0 & 36028797018963968L) != 0) {
                        return jjStopAtPos(1, 55);
                    }
                    break;
                case 'a':
                    return jjMoveStringLiteralDfa2_1(active0, 32768L);
                case 'e':
                    if ((active0 & FileSize.GB_COEFFICIENT) != 0) {
                        return jjStartNfaWithStates_1(1, 30, 30);
                    }
                    if ((active0 & 4294967296L) != 0) {
                        return jjStartNfaWithStates_1(1, 32, 30);
                    }
                    if ((active0 & 68719476736L) != 0) {
                        return jjStartNfaWithStates_1(1, 36, 30);
                    }
                    break;
                case Opcodes.LMUL /* 105 */:
                    return jjMoveStringLiteralDfa2_1(active0, 1125899906842624L);
                case Opcodes.LDIV /* 109 */:
                    return jjMoveStringLiteralDfa2_1(active0, 8796093022208L);
                case Opcodes.FDIV /* 110 */:
                    return jjMoveStringLiteralDfa2_1(active0, 18691697672192L);
                case Opcodes.DDIV /* 111 */:
                    return jjMoveStringLiteralDfa2_1(active0, 4503874505277440L);
                case Opcodes.LREM /* 113 */:
                    if ((active0 & 17179869184L) != 0) {
                        return jjStartNfaWithStates_1(1, 34, 30);
                    }
                    break;
                case Opcodes.FREM /* 114 */:
                    if ((active0 & 4398046511104L) != 0) {
                        return jjStartNfaWithStates_1(1, 42, 30);
                    }
                    return jjMoveStringLiteralDfa2_1(active0, 16384L);
                case 't':
                    if ((active0 & 67108864) != 0) {
                        return jjStartNfaWithStates_1(1, 26, 30);
                    }
                    if ((active0 & 268435456) != 0) {
                        return jjStartNfaWithStates_1(1, 28, 30);
                    }
                    break;
                case Opcodes.LNEG /* 117 */:
                    return jjMoveStringLiteralDfa2_1(active0, 65536L);
                case '|':
                    if ((active0 & 2199023255552L) != 0) {
                        return jjStopAtPos(1, 41);
                    }
                    break;
            }
            return jjStartNfa_1(0, active0);
        } catch (IOException e) {
            jjStopStringLiteralDfa_1(0, active0);
            return 1;
        }
    }

    private int jjMoveStringLiteralDfa2_1(long old0, long active0) {
        long active02 = active0 & old0;
        if (active02 == 0) {
            return jjStartNfa_1(0, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case 'd':
                    if ((active02 & 1099511627776L) != 0) {
                        return jjStartNfaWithStates_1(2, 40, 30);
                    }
                    if ((active02 & 4503599627370496L) != 0) {
                        return jjStartNfaWithStates_1(2, 52, 30);
                    }
                    break;
                case 'l':
                    return jjMoveStringLiteralDfa3_1(active02, 98304L);
                case 'p':
                    return jjMoveStringLiteralDfa3_1(active02, 8796093022208L);
                case 's':
                    return jjMoveStringLiteralDfa3_1(active02, 17592186044416L);
                case 't':
                    if ((active02 & 274877906944L) != 0) {
                        return jjStartNfaWithStates_1(2, 38, 30);
                    }
                    break;
                case Opcodes.LNEG /* 117 */:
                    return jjMoveStringLiteralDfa3_1(active02, 16384L);
                case Opcodes.FNEG /* 118 */:
                    if ((active02 & 1125899906842624L) != 0) {
                        return jjStartNfaWithStates_1(2, 50, 30);
                    }
                    break;
            }
            return jjStartNfa_1(1, active02);
        } catch (IOException e) {
            jjStopStringLiteralDfa_1(1, active02);
            return 2;
        }
    }

    private int jjMoveStringLiteralDfa3_1(long old0, long active0) {
        long active02 = active0 & old0;
        if (active02 == 0) {
            return jjStartNfa_1(1, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case 'e':
                    if ((active02 & 16384) != 0) {
                        return jjStartNfaWithStates_1(3, 14, 30);
                    }
                    break;
                case 'l':
                    if ((active02 & 65536) != 0) {
                        return jjStartNfaWithStates_1(3, 16, 30);
                    }
                    break;
                case 's':
                    return jjMoveStringLiteralDfa4_1(active02, 32768L);
                case 't':
                    return jjMoveStringLiteralDfa4_1(active02, 26388279066624L);
            }
            return jjStartNfa_1(2, active02);
        } catch (IOException e) {
            jjStopStringLiteralDfa_1(2, active02);
            return 3;
        }
    }

    private int jjMoveStringLiteralDfa4_1(long old0, long active0) {
        long active02 = active0 & old0;
        if (active02 == 0) {
            return jjStartNfa_1(2, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case 'a':
                    return jjMoveStringLiteralDfa5_1(active02, 17592186044416L);
                case 'e':
                    if ((active02 & 32768) != 0) {
                        return jjStartNfaWithStates_1(4, 15, 30);
                    }
                    break;
                case Opcodes.LSHL /* 121 */:
                    if ((active02 & 8796093022208L) != 0) {
                        return jjStartNfaWithStates_1(4, 43, 30);
                    }
                    break;
            }
            return jjStartNfa_1(3, active02);
        } catch (IOException e) {
            jjStopStringLiteralDfa_1(3, active02);
            return 4;
        }
    }

    private int jjMoveStringLiteralDfa5_1(long old0, long active0) {
        long active02 = active0 & old0;
        if (active02 == 0) {
            return jjStartNfa_1(3, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case Opcodes.FDIV /* 110 */:
                    return jjMoveStringLiteralDfa6_1(active02, 17592186044416L);
                default:
                    return jjStartNfa_1(4, active02);
            }
        } catch (IOException e) {
            jjStopStringLiteralDfa_1(4, active02);
            return 5;
        }
    }

    private int jjMoveStringLiteralDfa6_1(long old0, long active0) {
        long active02 = active0 & old0;
        if (active02 == 0) {
            return jjStartNfa_1(4, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case 'c':
                    return jjMoveStringLiteralDfa7_1(active02, 17592186044416L);
                default:
                    return jjStartNfa_1(5, active02);
            }
        } catch (IOException e) {
            jjStopStringLiteralDfa_1(5, active02);
            return 6;
        }
    }

    private int jjMoveStringLiteralDfa7_1(long old0, long active0) {
        long active02 = active0 & old0;
        if (active02 == 0) {
            return jjStartNfa_1(5, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case 'e':
                    return jjMoveStringLiteralDfa8_1(active02, 17592186044416L);
                default:
                    return jjStartNfa_1(6, active02);
            }
        } catch (IOException e) {
            jjStopStringLiteralDfa_1(6, active02);
            return 7;
        }
    }

    private int jjMoveStringLiteralDfa8_1(long old0, long active0) {
        long active02 = active0 & old0;
        if (active02 == 0) {
            return jjStartNfa_1(6, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case Opcodes.DDIV /* 111 */:
                    return jjMoveStringLiteralDfa9_1(active02, 17592186044416L);
                default:
                    return jjStartNfa_1(7, active02);
            }
        } catch (IOException e) {
            jjStopStringLiteralDfa_1(7, active02);
            return 8;
        }
    }

    private int jjMoveStringLiteralDfa9_1(long old0, long active0) {
        long active02 = active0 & old0;
        if (active02 == 0) {
            return jjStartNfa_1(7, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case Opcodes.FSUB /* 102 */:
                    if ((active02 & 17592186044416L) != 0) {
                        return jjStartNfaWithStates_1(9, 44, 30);
                    }
                    break;
            }
            return jjStartNfa_1(8, active02);
        } catch (IOException e) {
            jjStopStringLiteralDfa_1(8, active02);
            return 9;
        }
    }

    private int jjStartNfaWithStates_1(int pos, int kind, int state) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        try {
            this.curChar = this.input_stream.readChar();
            return jjMoveNfa_1(state, pos + 1);
        } catch (IOException e) {
            return pos + 1;
        }
    }

    private int jjMoveNfa_1(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 30;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            int i2 = this.jjround + 1;
            this.jjround = i2;
            if (i2 == Integer.MAX_VALUE) {
                ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1 << this.curChar;
                do {
                    i--;
                    switch (this.jjstateSet[i]) {
                        case 0:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 10) {
                                    kind = 10;
                                }
                                jjCheckNAddStates(18, 22);
                                break;
                            } else if ((103079215104L & l) != 0) {
                                if (kind > 56) {
                                    kind = 56;
                                }
                                jjCheckNAddTwoStates(28, 29);
                                break;
                            } else if (this.curChar == '\'') {
                                jjCheckNAddStates(23, 25);
                                break;
                            } else if (this.curChar == '\"') {
                                jjCheckNAddStates(26, 28);
                                break;
                            } else if (this.curChar == '.') {
                                jjCheckNAdd(1);
                                break;
                            }
                            break;
                        case 1:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 11) {
                                    kind = 11;
                                }
                                jjCheckNAddTwoStates(1, 2);
                                break;
                            }
                            break;
                        case 3:
                            if ((43980465111040L & l) != 0) {
                                jjCheckNAdd(4);
                                break;
                            }
                            break;
                        case 4:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 11) {
                                    kind = 11;
                                }
                                jjCheckNAdd(4);
                                break;
                            }
                            break;
                        case 5:
                            if (this.curChar == '\"') {
                                jjCheckNAddStates(26, 28);
                                break;
                            }
                            break;
                        case 6:
                            if (((-17179869185L) & l) != 0) {
                                jjCheckNAddStates(26, 28);
                                break;
                            }
                            break;
                        case 8:
                            if ((566935683072L & l) != 0) {
                                jjCheckNAddStates(26, 28);
                                break;
                            }
                            break;
                        case 9:
                            if (this.curChar == '\"' && kind > 13) {
                                kind = 13;
                                break;
                            }
                            break;
                        case 10:
                            if (this.curChar == '\'') {
                                jjCheckNAddStates(23, 25);
                                break;
                            }
                            break;
                        case 11:
                            if (((-549755813889L) & l) != 0) {
                                jjCheckNAddStates(23, 25);
                                break;
                            }
                            break;
                        case 13:
                            if ((566935683072L & l) != 0) {
                                jjCheckNAddStates(23, 25);
                                break;
                            }
                            break;
                        case 14:
                            if (this.curChar == '\'' && kind > 13) {
                                kind = 13;
                                break;
                            }
                            break;
                        case 15:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 10) {
                                    kind = 10;
                                }
                                jjCheckNAddStates(18, 22);
                                break;
                            }
                            break;
                        case 16:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 10) {
                                    kind = 10;
                                }
                                jjCheckNAdd(16);
                                break;
                            }
                            break;
                        case 17:
                            if ((287948901175001088L & l) != 0) {
                                jjCheckNAddTwoStates(17, 18);
                                break;
                            }
                            break;
                        case 18:
                            if (this.curChar == '.') {
                                if (kind > 11) {
                                    kind = 11;
                                }
                                jjCheckNAddTwoStates(19, 20);
                                break;
                            }
                            break;
                        case 19:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 11) {
                                    kind = 11;
                                }
                                jjCheckNAddTwoStates(19, 20);
                                break;
                            }
                            break;
                        case 21:
                            if ((43980465111040L & l) != 0) {
                                jjCheckNAdd(22);
                                break;
                            }
                            break;
                        case 22:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 11) {
                                    kind = 11;
                                }
                                jjCheckNAdd(22);
                                break;
                            }
                            break;
                        case 23:
                            if ((287948901175001088L & l) != 0) {
                                jjCheckNAddTwoStates(23, 24);
                                break;
                            }
                            break;
                        case 25:
                            if ((43980465111040L & l) != 0) {
                                jjCheckNAdd(26);
                                break;
                            }
                            break;
                        case 26:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 11) {
                                    kind = 11;
                                }
                                jjCheckNAdd(26);
                                break;
                            }
                            break;
                        case 27:
                            if ((103079215104L & l) != 0) {
                                if (kind > 56) {
                                    kind = 56;
                                }
                                jjCheckNAddTwoStates(28, 29);
                                break;
                            }
                            break;
                        case 28:
                            if ((287948969894477824L & l) != 0) {
                                if (kind > 56) {
                                    kind = 56;
                                }
                                jjCheckNAdd(28);
                                break;
                            }
                            break;
                        case 29:
                            if ((287948969894477824L & l) != 0) {
                                if (kind > 57) {
                                    kind = 57;
                                }
                                jjCheckNAdd(29);
                                break;
                            }
                            break;
                        case 30:
                            if ((287948969894477824L & l) != 0) {
                                if (kind > 57) {
                                    kind = 57;
                                }
                                jjCheckNAdd(29);
                            }
                            if ((287948969894477824L & l) != 0) {
                                if (kind > 56) {
                                    kind = 56;
                                }
                                jjCheckNAdd(28);
                                break;
                            }
                            break;
                    }
                } while (i != startsAt);
            } else if (this.curChar < 128) {
                long l2 = 1 << (this.curChar & '?');
                do {
                    i--;
                    switch (this.jjstateSet[i]) {
                        case 0:
                            if ((576460745995190270L & l2) != 0) {
                                if (kind > 56) {
                                    kind = 56;
                                }
                                jjCheckNAddTwoStates(28, 29);
                                break;
                            }
                            break;
                        case 2:
                            if ((137438953504L & l2) != 0) {
                                jjAddStates(29, 30);
                                break;
                            }
                            break;
                        case 6:
                            if (((-268435457) & l2) != 0) {
                                jjCheckNAddStates(26, 28);
                                break;
                            }
                            break;
                        case 7:
                            if (this.curChar == '\\') {
                                int[] iArr = this.jjstateSet;
                                int i3 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i3 + 1;
                                iArr[i3] = 8;
                                break;
                            }
                            break;
                        case 8:
                            if (this.curChar == '\\') {
                                jjCheckNAddStates(26, 28);
                                break;
                            }
                            break;
                        case 11:
                            if (((-268435457) & l2) != 0) {
                                jjCheckNAddStates(23, 25);
                                break;
                            }
                            break;
                        case 12:
                            if (this.curChar == '\\') {
                                int[] iArr2 = this.jjstateSet;
                                int i4 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i4 + 1;
                                iArr2[i4] = 13;
                                break;
                            }
                            break;
                        case 13:
                            if (this.curChar == '\\') {
                                jjCheckNAddStates(23, 25);
                                break;
                            }
                            break;
                        case 20:
                            if ((137438953504L & l2) != 0) {
                                jjAddStates(31, 32);
                                break;
                            }
                            break;
                        case 24:
                            if ((137438953504L & l2) != 0) {
                                jjAddStates(33, 34);
                                break;
                            }
                            break;
                        case 28:
                            if ((576460745995190270L & l2) != 0) {
                                if (kind > 56) {
                                    kind = 56;
                                }
                                jjCheckNAdd(28);
                                break;
                            }
                            break;
                        case 29:
                            if ((576460745995190270L & l2) != 0) {
                                if (kind > 57) {
                                    kind = 57;
                                }
                                jjCheckNAdd(29);
                                break;
                            }
                            break;
                        case 30:
                            if ((576460745995190270L & l2) != 0) {
                                if (kind > 57) {
                                    kind = 57;
                                }
                                jjCheckNAdd(29);
                            }
                            if ((576460745995190270L & l2) != 0) {
                                if (kind > 56) {
                                    kind = 56;
                                }
                                jjCheckNAdd(28);
                                break;
                            }
                            break;
                    }
                } while (i != startsAt);
            } else {
                int hiByte = this.curChar >> '\b';
                int i1 = hiByte >> 6;
                long l1 = 1 << (hiByte & 63);
                int i22 = (this.curChar & 255) >> 6;
                long l22 = 1 << (this.curChar & '?');
                do {
                    i--;
                    switch (this.jjstateSet[i]) {
                        case 0:
                            if (jjCanMove_1(hiByte, i1, i22, l1, l22)) {
                                if (kind > 56) {
                                    kind = 56;
                                }
                                jjCheckNAddTwoStates(28, 29);
                                break;
                            }
                            break;
                        case 6:
                            if (jjCanMove_0(hiByte, i1, i22, l1, l22)) {
                                jjAddStates(26, 28);
                                break;
                            }
                            break;
                        case 11:
                            if (jjCanMove_0(hiByte, i1, i22, l1, l22)) {
                                jjAddStates(23, 25);
                                break;
                            }
                            break;
                        case 28:
                            if (jjCanMove_1(hiByte, i1, i22, l1, l22)) {
                                if (kind > 56) {
                                    kind = 56;
                                }
                                jjCheckNAdd(28);
                                break;
                            }
                            break;
                        case 29:
                            if (jjCanMove_1(hiByte, i1, i22, l1, l22)) {
                                if (kind > 57) {
                                    kind = 57;
                                }
                                jjCheckNAdd(29);
                                break;
                            }
                            break;
                        case 30:
                            if (jjCanMove_1(hiByte, i1, i22, l1, l22)) {
                                if (kind > 56) {
                                    kind = 56;
                                }
                                jjCheckNAdd(28);
                            }
                            if (jjCanMove_1(hiByte, i1, i22, l1, l22)) {
                                if (kind > 57) {
                                    kind = 57;
                                }
                                jjCheckNAdd(29);
                                break;
                            }
                            break;
                    }
                } while (i != startsAt);
            }
            if (kind != Integer.MAX_VALUE) {
                this.jjmatchedKind = kind;
                this.jjmatchedPos = curPos;
                kind = Integer.MAX_VALUE;
            }
            curPos++;
            int i5 = this.jjnewStateCnt;
            i = i5;
            int i6 = startsAt;
            this.jjnewStateCnt = i6;
            int i7 = 30 - i6;
            startsAt = i7;
            if (i5 == i7) {
                return curPos;
            }
            try {
                this.curChar = this.input_stream.readChar();
            } catch (IOException e) {
                return curPos;
            }
        }
    }

    private static final boolean jjCanMove_0(int hiByte, int i1, int i2, long l1, long l2) {
        switch (hiByte) {
            case 0:
                return (jjbitVec2[i2] & l2) != 0;
            default:
                if ((jjbitVec0[i1] & l1) != 0) {
                    return true;
                }
                return false;
        }
    }

    private static final boolean jjCanMove_1(int hiByte, int i1, int i2, long l1, long l2) {
        switch (hiByte) {
            case 0:
                return (jjbitVec4[i2] & l2) != 0;
            case 48:
                return (jjbitVec5[i2] & l2) != 0;
            case 49:
                return (jjbitVec6[i2] & l2) != 0;
            case 51:
                return (jjbitVec7[i2] & l2) != 0;
            case 61:
                return (jjbitVec8[i2] & l2) != 0;
            default:
                if ((jjbitVec3[i1] & l1) != 0) {
                    return true;
                }
                return false;
        }
    }

    public ELParserTokenManager(SimpleCharStream stream) {
        this.deque = new ArrayDeque();
        this.debugStream = System.out;
        this.jjrounds = new int[30];
        this.jjstateSet = new int[60];
        this.jjimage = new StringBuilder();
        this.image = this.jjimage;
        this.curLexState = 0;
        this.defaultLexState = 0;
        this.input_stream = stream;
    }

    public ELParserTokenManager(SimpleCharStream stream, int lexState) {
        this(stream);
        SwitchTo(lexState);
    }

    public void ReInit(SimpleCharStream stream) {
        this.jjnewStateCnt = 0;
        this.jjmatchedPos = 0;
        this.curLexState = this.defaultLexState;
        this.input_stream = stream;
        ReInitRounds();
    }

    private void ReInitRounds() {
        this.jjround = -2147483647;
        int i = 30;
        while (true) {
            int i2 = i;
            i--;
            if (i2 > 0) {
                this.jjrounds[i] = Integer.MIN_VALUE;
            } else {
                return;
            }
        }
    }

    public void ReInit(SimpleCharStream stream, int lexState) {
        ReInit(stream);
        SwitchTo(lexState);
    }

    public void SwitchTo(int lexState) {
        if (lexState >= 3 || lexState < 0) {
            throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", 2);
        }
        this.curLexState = lexState;
    }

    protected Token jjFillToken() {
        String im = jjstrLiteralImages[this.jjmatchedKind];
        String curTokenImage = im == null ? this.input_stream.GetImage() : im;
        int beginLine = this.input_stream.getBeginLine();
        int beginColumn = this.input_stream.getBeginColumn();
        int endLine = this.input_stream.getEndLine();
        int endColumn = this.input_stream.getEndColumn();
        Token t = Token.newToken(this.jjmatchedKind, curTokenImage);
        t.beginLine = beginLine;
        t.endLine = endLine;
        t.beginColumn = beginColumn;
        t.endColumn = endColumn;
        return t;
    }

    public Token getNextToken() {
        int curPos = 0;
        while (true) {
            try {
                this.curChar = this.input_stream.BeginToken();
                this.image = this.jjimage;
                this.image.setLength(0);
                this.jjimageLen = 0;
                switch (this.curLexState) {
                    case 0:
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        curPos = jjMoveStringLiteralDfa0_0();
                        break;
                    case 1:
                        try {
                            this.input_stream.backup(0);
                            while (this.curChar <= ' ' && (4294977024L & (1 << this.curChar)) != 0) {
                                this.curChar = this.input_stream.BeginToken();
                            }
                            this.jjmatchedKind = Integer.MAX_VALUE;
                            this.jjmatchedPos = 0;
                            curPos = jjMoveStringLiteralDfa0_1();
                            if (this.jjmatchedPos == 0 && this.jjmatchedKind > 61) {
                                this.jjmatchedKind = 61;
                                break;
                            }
                        } catch (IOException e) {
                            break;
                        }
                        break;
                    case 2:
                        try {
                            this.input_stream.backup(0);
                            while (this.curChar <= ' ' && (4294977024L & (1 << this.curChar)) != 0) {
                                this.curChar = this.input_stream.BeginToken();
                            }
                            this.jjmatchedKind = Integer.MAX_VALUE;
                            this.jjmatchedPos = 0;
                            curPos = jjMoveStringLiteralDfa0_2();
                            if (this.jjmatchedPos == 0 && this.jjmatchedKind > 61) {
                                this.jjmatchedKind = 61;
                                break;
                            }
                        } catch (IOException e2) {
                            break;
                        }
                        break;
                }
                if (this.jjmatchedKind != Integer.MAX_VALUE) {
                    if (this.jjmatchedPos + 1 < curPos) {
                        this.input_stream.backup((curPos - this.jjmatchedPos) - 1);
                    }
                    if ((jjtoToken[this.jjmatchedKind >> 6] & (1 << (this.jjmatchedKind & 63))) != 0) {
                        Token matchedToken = jjFillToken();
                        TokenLexicalActions(matchedToken);
                        if (jjnewLexState[this.jjmatchedKind] != -1) {
                            this.curLexState = jjnewLexState[this.jjmatchedKind];
                        }
                        return matchedToken;
                    } else if (jjnewLexState[this.jjmatchedKind] != -1) {
                        this.curLexState = jjnewLexState[this.jjmatchedKind];
                    }
                } else {
                    int error_line = this.input_stream.getEndLine();
                    int error_column = this.input_stream.getEndColumn();
                    String error_after = null;
                    boolean EOFSeen = false;
                    try {
                        this.input_stream.readChar();
                        this.input_stream.backup(1);
                    } catch (IOException e3) {
                        EOFSeen = true;
                        error_after = curPos <= 1 ? "" : this.input_stream.GetImage();
                        if (this.curChar == '\n' || this.curChar == '\r') {
                            error_line++;
                            error_column = 0;
                        } else {
                            error_column++;
                        }
                    }
                    if (!EOFSeen) {
                        this.input_stream.backup(1);
                        error_after = curPos <= 1 ? "" : this.input_stream.GetImage();
                    }
                    throw new TokenMgrError(EOFSeen, this.curLexState, error_line, error_column, error_after, this.curChar, 0);
                }
            } catch (IOException e4) {
                this.jjmatchedKind = 0;
                return jjFillToken();
            }
        }
    }

    void TokenLexicalActions(Token matchedToken) {
        switch (this.jjmatchedKind) {
            case 2:
                this.image.append(jjstrLiteralImages[2]);
                this.lengthOfMatch = jjstrLiteralImages[2].length();
                this.deque.push(0);
                return;
            case 3:
                this.image.append(jjstrLiteralImages[3]);
                this.lengthOfMatch = jjstrLiteralImages[3].length();
                this.deque.push(0);
                return;
            case 4:
            case 5:
            case 6:
            case 7:
            default:
                return;
            case 8:
                this.image.append(jjstrLiteralImages[8]);
                this.lengthOfMatch = jjstrLiteralImages[8].length();
                this.deque.push(Integer.valueOf(this.curLexState));
                return;
            case 9:
                this.image.append(jjstrLiteralImages[9]);
                this.lengthOfMatch = jjstrLiteralImages[9].length();
                SwitchTo(this.deque.pop().intValue());
                return;
        }
    }

    private void jjCheckNAdd(int state) {
        if (this.jjrounds[state] != this.jjround) {
            int[] iArr = this.jjstateSet;
            int i = this.jjnewStateCnt;
            this.jjnewStateCnt = i + 1;
            iArr[i] = state;
            this.jjrounds[state] = this.jjround;
        }
    }

    private void jjAddStates(int start, int end) {
        int i;
        do {
            int[] iArr = this.jjstateSet;
            int i2 = this.jjnewStateCnt;
            this.jjnewStateCnt = i2 + 1;
            iArr[i2] = jjnextStates[start];
            i = start;
            start++;
        } while (i != end);
    }

    private void jjCheckNAddTwoStates(int state1, int state2) {
        jjCheckNAdd(state1);
        jjCheckNAdd(state2);
    }

    private void jjCheckNAddStates(int start, int end) {
        int i;
        do {
            jjCheckNAdd(jjnextStates[start]);
            i = start;
            start++;
        } while (i != end);
    }
}