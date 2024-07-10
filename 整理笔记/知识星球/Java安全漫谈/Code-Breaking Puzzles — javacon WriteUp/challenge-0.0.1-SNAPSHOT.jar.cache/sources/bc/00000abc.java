package org.apache.el.parser;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.el.ELException;
import org.apache.tomcat.jni.SSL;
import org.springframework.asm.Opcodes;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:org/apache/el/parser/ELParser.class */
public class ELParser implements ELParserTreeConstants, ELParserConstants {
    protected JJTELParserState jjtree;
    public ELParserTokenManager token_source;
    SimpleCharStream jj_input_stream;
    public Token token;
    public Token jj_nt;
    private int jj_ntk;
    private Token jj_scanpos;
    private Token jj_lastpos;
    private int jj_la;
    private int jj_gen;
    private final int[] jj_la1;
    private static int[] jj_la1_0;
    private static int[] jj_la1_1;
    private final JJCalls[] jj_2_rtns;
    private boolean jj_rescan;
    private int jj_gc;
    private final LookaheadSuccess jj_ls;
    private List<int[]> jj_expentries;
    private int[] jj_expentry;
    private int jj_kind;
    private int[] jj_lasttokens;
    private int jj_endpos;

    public static Node parse(String ref) throws ELException {
        try {
            return new ELParser(new StringReader(ref)).CompositeExpression();
        } catch (ParseException pe) {
            throw new ELException(pe.getMessage());
        }
    }

    public final AstCompositeExpression CompositeExpression() throws ParseException {
        AstCompositeExpression jjtn000 = new AstCompositeExpression(0);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        while (true) {
            try {
                switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
                    case 1:
                    case 2:
                    case 3:
                        switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
                            case 1:
                                LiteralExpression();
                                break;
                            case 2:
                                DynamicExpression();
                                break;
                            case 3:
                                DeferredExpression();
                                break;
                            default:
                                this.jj_la1[1] = this.jj_gen;
                                jj_consume_token(-1);
                                throw new ParseException();
                        }
                    default:
                        this.jj_la1[0] = this.jj_gen;
                        jj_consume_token(0);
                        this.jjtree.closeNodeScope((Node) jjtn000, true);
                        jjtc000 = false;
                        return jjtn000;
                }
            } catch (Throwable jjte000) {
                try {
                    if (jjtc000) {
                        this.jjtree.clearNodeScope(jjtn000);
                    } else {
                        this.jjtree.popNode();
                    }
                    if (jjte000 instanceof RuntimeException) {
                        throw ((RuntimeException) jjte000);
                    }
                    if (jjte000 instanceof ParseException) {
                        throw ((ParseException) jjte000);
                    }
                    throw ((Error) jjte000);
                } finally {
                    if (jjtc000) {
                        this.jjtree.closeNodeScope((Node) jjtn000, true);
                    }
                }
            }
        }
    }

    public final void LiteralExpression() throws ParseException {
        AstLiteralExpression jjtn000 = new AstLiteralExpression(1);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            Token t = jj_consume_token(1);
            this.jjtree.closeNodeScope((Node) jjtn000, true);
            jjtc000 = false;
            jjtn000.setImage(t.image);
            if (0 != 0) {
                this.jjtree.closeNodeScope((Node) jjtn000, true);
            }
        } catch (Throwable th) {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node) jjtn000, true);
            }
            throw th;
        }
    }

    public final void DeferredExpression() throws ParseException {
        AstDeferredExpression jjtn000 = new AstDeferredExpression(2);
        this.jjtree.openNodeScope(jjtn000);
        try {
            jj_consume_token(3);
            Expression();
            jj_consume_token(9);
            if (1 != 0) {
                this.jjtree.closeNodeScope((Node) jjtn000, true);
            }
        } catch (Throwable jjte000) {
            try {
                if (1 != 0) {
                    this.jjtree.clearNodeScope(jjtn000);
                } else {
                    this.jjtree.popNode();
                }
                if (jjte000 instanceof RuntimeException) {
                    throw ((RuntimeException) jjte000);
                }
                if (jjte000 instanceof ParseException) {
                    throw ((ParseException) jjte000);
                }
                throw ((Error) jjte000);
            } catch (Throwable th) {
                if (1 != 0) {
                    this.jjtree.closeNodeScope((Node) jjtn000, true);
                }
                throw th;
            }
        }
    }

    public final void DynamicExpression() throws ParseException {
        AstDynamicExpression jjtn000 = new AstDynamicExpression(3);
        this.jjtree.openNodeScope(jjtn000);
        try {
            jj_consume_token(2);
            Expression();
            jj_consume_token(9);
            if (1 != 0) {
                this.jjtree.closeNodeScope((Node) jjtn000, true);
            }
        } catch (Throwable jjte000) {
            try {
                if (1 != 0) {
                    this.jjtree.clearNodeScope(jjtn000);
                } else {
                    this.jjtree.popNode();
                }
                if (jjte000 instanceof RuntimeException) {
                    throw ((RuntimeException) jjte000);
                }
                if (jjte000 instanceof ParseException) {
                    throw ((ParseException) jjte000);
                }
                throw ((Error) jjte000);
            } catch (Throwable th) {
                if (1 != 0) {
                    this.jjtree.closeNodeScope((Node) jjtn000, true);
                }
                throw th;
            }
        }
    }

    public final void Expression() throws ParseException {
        Semicolon();
    }

    public final void Semicolon() throws ParseException {
        Assignment();
        while (true) {
            switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
                case 23:
                    jj_consume_token(23);
                    AstSemicolon jjtn001 = new AstSemicolon(5);
                    this.jjtree.openNodeScope(jjtn001);
                    try {
                        Assignment();
                        if (1 != 0) {
                            this.jjtree.closeNodeScope(jjtn001, 2);
                        }
                    } catch (Throwable jjte001) {
                        try {
                            if (1 != 0) {
                                this.jjtree.clearNodeScope(jjtn001);
                            } else {
                                this.jjtree.popNode();
                            }
                            if (jjte001 instanceof RuntimeException) {
                                throw ((RuntimeException) jjte001);
                            }
                            if (jjte001 instanceof ParseException) {
                                throw ((ParseException) jjte001);
                            }
                            throw ((Error) jjte001);
                        } catch (Throwable th) {
                            if (1 != 0) {
                                this.jjtree.closeNodeScope(jjtn001, 2);
                            }
                            throw th;
                        }
                    }
                default:
                    this.jj_la1[2] = this.jj_gen;
                    return;
            }
        }
    }

    public final void Assignment() throws ParseException {
        if (jj_2_2(4)) {
            LambdaExpression();
            return;
        }
        switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
            case 8:
            case 10:
            case 11:
            case 13:
            case 14:
            case 15:
            case 16:
            case 18:
            case 20:
            case 37:
            case 38:
            case ELParserConstants.EMPTY /* 43 */:
            case 47:
            case 56:
                Choice();
                while (jj_2_1(2)) {
                    jj_consume_token(54);
                    AstAssign jjtn001 = new AstAssign(6);
                    this.jjtree.openNodeScope(jjtn001);
                    try {
                        Assignment();
                        if (1 != 0) {
                            this.jjtree.closeNodeScope(jjtn001, 2);
                        }
                    } catch (Throwable jjte001) {
                        try {
                            if (1 != 0) {
                                this.jjtree.clearNodeScope(jjtn001);
                            } else {
                                this.jjtree.popNode();
                            }
                            if (jjte001 instanceof RuntimeException) {
                                throw ((RuntimeException) jjte001);
                            }
                            if (jjte001 instanceof ParseException) {
                                throw ((ParseException) jjte001);
                            }
                            throw ((Error) jjte001);
                        } catch (Throwable th) {
                            if (1 != 0) {
                                this.jjtree.closeNodeScope(jjtn001, 2);
                            }
                            throw th;
                        }
                    }
                }
                return;
            case 9:
            case 12:
            case 17:
            case 19:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 39:
            case 40:
            case 41:
            case 42:
            case 44:
            case 45:
            case 46:
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            default:
                this.jj_la1[3] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
        }
    }

    public final void LambdaExpression() throws ParseException {
        AstLambdaExpression jjtn000 = new AstLambdaExpression(7);
        this.jjtree.openNodeScope(jjtn000);
        try {
            LambdaParameters();
            jj_consume_token(55);
            if (jj_2_3(3)) {
                LambdaExpression();
            } else {
                switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
                    case 8:
                    case 10:
                    case 11:
                    case 13:
                    case 14:
                    case 15:
                    case 16:
                    case 18:
                    case 20:
                    case 37:
                    case 38:
                    case ELParserConstants.EMPTY /* 43 */:
                    case 47:
                    case 56:
                        Choice();
                        break;
                    case 9:
                    case 12:
                    case 17:
                    case 19:
                    case 21:
                    case 22:
                    case 23:
                    case 24:
                    case 25:
                    case 26:
                    case 27:
                    case 28:
                    case 29:
                    case 30:
                    case 31:
                    case 32:
                    case 33:
                    case 34:
                    case 35:
                    case 36:
                    case 39:
                    case 40:
                    case 41:
                    case 42:
                    case 44:
                    case 45:
                    case 46:
                    case 48:
                    case 49:
                    case 50:
                    case 51:
                    case 52:
                    case 53:
                    case 54:
                    case 55:
                    default:
                        this.jj_la1[4] = this.jj_gen;
                        jj_consume_token(-1);
                        throw new ParseException();
                }
            }
        } catch (Throwable jjte000) {
            try {
                if (1 != 0) {
                    this.jjtree.clearNodeScope(jjtn000);
                } else {
                    this.jjtree.popNode();
                }
                if (jjte000 instanceof RuntimeException) {
                    throw ((RuntimeException) jjte000);
                }
                if (jjte000 instanceof ParseException) {
                    throw ((ParseException) jjte000);
                }
                throw ((Error) jjte000);
            } finally {
                if (1 != 0) {
                    this.jjtree.closeNodeScope((Node) jjtn000, true);
                }
            }
        }
    }

    public final void LambdaParameters() throws ParseException {
        AstLambdaParameters jjtn000 = new AstLambdaParameters(8);
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
                case 18:
                    jj_consume_token(18);
                    switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
                        case 56:
                            Identifier();
                            while (true) {
                                switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
                                    case 24:
                                        jj_consume_token(24);
                                        Identifier();
                                    default:
                                        this.jj_la1[5] = this.jj_gen;
                                        break;
                                }
                            }
                        default:
                            this.jj_la1[6] = this.jj_gen;
                            break;
                    }
                    jj_consume_token(19);
                    break;
                case 56:
                    Identifier();
                    break;
                default:
                    this.jj_la1[7] = this.jj_gen;
                    jj_consume_token(-1);
                    throw new ParseException();
            }
        } catch (Throwable jjte000) {
            try {
                if (1 != 0) {
                    this.jjtree.clearNodeScope(jjtn000);
                } else {
                    this.jjtree.popNode();
                }
                if (jjte000 instanceof RuntimeException) {
                    throw ((RuntimeException) jjte000);
                }
                if (jjte000 instanceof ParseException) {
                    throw ((ParseException) jjte000);
                }
                throw ((Error) jjte000);
            } finally {
                if (1 != 0) {
                    this.jjtree.closeNodeScope((Node) jjtn000, true);
                }
            }
        }
    }

    public final void LambdaExpressionOrInvocation() throws ParseException {
        AstLambdaExpression jjtn000 = new AstLambdaExpression(7);
        this.jjtree.openNodeScope(jjtn000);
        try {
            jj_consume_token(18);
            LambdaParameters();
            jj_consume_token(55);
            if (jj_2_4(3)) {
                LambdaExpression();
            } else {
                switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
                    case 8:
                    case 10:
                    case 11:
                    case 13:
                    case 14:
                    case 15:
                    case 16:
                    case 18:
                    case 20:
                    case 37:
                    case 38:
                    case ELParserConstants.EMPTY /* 43 */:
                    case 47:
                    case 56:
                        Choice();
                        break;
                    case 9:
                    case 12:
                    case 17:
                    case 19:
                    case 21:
                    case 22:
                    case 23:
                    case 24:
                    case 25:
                    case 26:
                    case 27:
                    case 28:
                    case 29:
                    case 30:
                    case 31:
                    case 32:
                    case 33:
                    case 34:
                    case 35:
                    case 36:
                    case 39:
                    case 40:
                    case 41:
                    case 42:
                    case 44:
                    case 45:
                    case 46:
                    case 48:
                    case 49:
                    case 50:
                    case 51:
                    case 52:
                    case 53:
                    case 54:
                    case 55:
                    default:
                        this.jj_la1[8] = this.jj_gen;
                        jj_consume_token(-1);
                        throw new ParseException();
                }
            }
            jj_consume_token(19);
            while (true) {
                switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
                    case 18:
                        MethodParameters();
                    default:
                        this.jj_la1[9] = this.jj_gen;
                        if (jjtc000) {
                            return;
                        }
                        return;
                }
            }
        } catch (Throwable jjte000) {
            try {
                if (1 != 0) {
                    this.jjtree.clearNodeScope(jjtn000);
                } else {
                    this.jjtree.popNode();
                }
                if (jjte000 instanceof RuntimeException) {
                    throw ((RuntimeException) jjte000);
                }
                if (jjte000 instanceof ParseException) {
                    throw ((ParseException) jjte000);
                }
                throw ((Error) jjte000);
            } finally {
                if (1 != 0) {
                    this.jjtree.closeNodeScope((Node) jjtn000, true);
                }
            }
        }
    }

    public final void Choice() throws ParseException {
        Or();
        while (jj_2_5(3)) {
            jj_consume_token(48);
            Choice();
            jj_consume_token(22);
            AstChoice jjtn001 = new AstChoice(9);
            this.jjtree.openNodeScope(jjtn001);
            try {
                Choice();
                if (1 != 0) {
                    this.jjtree.closeNodeScope(jjtn001, 3);
                }
            } catch (Throwable jjte001) {
                try {
                    if (1 != 0) {
                        this.jjtree.clearNodeScope(jjtn001);
                    } else {
                        this.jjtree.popNode();
                    }
                    if (jjte001 instanceof RuntimeException) {
                        throw ((RuntimeException) jjte001);
                    }
                    if (jjte001 instanceof ParseException) {
                        throw ((ParseException) jjte001);
                    }
                    throw ((Error) jjte001);
                } catch (Throwable th) {
                    if (1 != 0) {
                        this.jjtree.closeNodeScope(jjtn001, 3);
                    }
                    throw th;
                }
            }
        }
    }

    public final void Or() throws ParseException {
        And();
        while (true) {
            switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
                case 41:
                case 42:
                    switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
                        case 41:
                            jj_consume_token(41);
                            break;
                        case 42:
                            jj_consume_token(42);
                            break;
                        default:
                            this.jj_la1[11] = this.jj_gen;
                            jj_consume_token(-1);
                            throw new ParseException();
                    }
                    AstOr jjtn001 = new AstOr(10);
                    this.jjtree.openNodeScope(jjtn001);
                    try {
                        And();
                        if (1 != 0) {
                            this.jjtree.closeNodeScope(jjtn001, 2);
                        }
                    } catch (Throwable jjte001) {
                        try {
                            if (1 != 0) {
                                this.jjtree.clearNodeScope(jjtn001);
                            } else {
                                this.jjtree.popNode();
                            }
                            if (jjte001 instanceof RuntimeException) {
                                throw ((RuntimeException) jjte001);
                            }
                            if (jjte001 instanceof ParseException) {
                                throw ((ParseException) jjte001);
                            }
                            throw ((Error) jjte001);
                        } catch (Throwable th) {
                            if (1 != 0) {
                                this.jjtree.closeNodeScope(jjtn001, 2);
                            }
                            throw th;
                        }
                    }
                default:
                    this.jj_la1[10] = this.jj_gen;
                    return;
            }
        }
    }

    public final void And() throws ParseException {
        Equality();
        while (true) {
            switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
                case 39:
                case 40:
                    switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
                        case 39:
                            jj_consume_token(39);
                            break;
                        case 40:
                            jj_consume_token(40);
                            break;
                        default:
                            this.jj_la1[13] = this.jj_gen;
                            jj_consume_token(-1);
                            throw new ParseException();
                    }
                    AstAnd jjtn001 = new AstAnd(11);
                    this.jjtree.openNodeScope(jjtn001);
                    try {
                        Equality();
                        if (1 != 0) {
                            this.jjtree.closeNodeScope(jjtn001, 2);
                        }
                    } catch (Throwable jjte001) {
                        try {
                            if (1 != 0) {
                                this.jjtree.clearNodeScope(jjtn001);
                            } else {
                                this.jjtree.popNode();
                            }
                            if (jjte001 instanceof RuntimeException) {
                                throw ((RuntimeException) jjte001);
                            }
                            if (jjte001 instanceof ParseException) {
                                throw ((ParseException) jjte001);
                            }
                            throw ((Error) jjte001);
                        } catch (Throwable th) {
                            if (1 != 0) {
                                this.jjtree.closeNodeScope(jjtn001, 2);
                            }
                            throw th;
                        }
                    }
                default:
                    this.jj_la1[12] = this.jj_gen;
                    return;
            }
        }
    }

    public final void Equality() throws ParseException {
        Compare();
        while (true) {
            switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
                case 33:
                case 34:
                case 35:
                case 36:
                    switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
                        case 33:
                        case 34:
                            switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
                                case 33:
                                    jj_consume_token(33);
                                    break;
                                case 34:
                                    jj_consume_token(34);
                                    break;
                                default:
                                    this.jj_la1[15] = this.jj_gen;
                                    jj_consume_token(-1);
                                    throw new ParseException();
                            }
                            AstEqual jjtn001 = new AstEqual(12);
                            this.jjtree.openNodeScope(jjtn001);
                            try {
                                Compare();
                                if (1 == 0) {
                                    break;
                                } else {
                                    this.jjtree.closeNodeScope(jjtn001, 2);
                                    break;
                                }
                            } catch (Throwable jjte001) {
                                try {
                                    if (1 != 0) {
                                        this.jjtree.clearNodeScope(jjtn001);
                                    } else {
                                        this.jjtree.popNode();
                                    }
                                    if (jjte001 instanceof RuntimeException) {
                                        throw ((RuntimeException) jjte001);
                                    }
                                    if (jjte001 instanceof ParseException) {
                                        throw ((ParseException) jjte001);
                                    }
                                    throw ((Error) jjte001);
                                } catch (Throwable th) {
                                    if (1 != 0) {
                                        this.jjtree.closeNodeScope(jjtn001, 2);
                                    }
                                    throw th;
                                }
                            }
                        case 35:
                        case 36:
                            switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
                                case 35:
                                    jj_consume_token(35);
                                    break;
                                case 36:
                                    jj_consume_token(36);
                                    break;
                                default:
                                    this.jj_la1[16] = this.jj_gen;
                                    jj_consume_token(-1);
                                    throw new ParseException();
                            }
                            AstNotEqual jjtn002 = new AstNotEqual(13);
                            this.jjtree.openNodeScope(jjtn002);
                            try {
                                Compare();
                                if (1 == 0) {
                                    break;
                                } else {
                                    this.jjtree.closeNodeScope(jjtn002, 2);
                                    break;
                                }
                            } catch (Throwable jjte002) {
                                try {
                                    if (1 != 0) {
                                        this.jjtree.clearNodeScope(jjtn002);
                                    } else {
                                        this.jjtree.popNode();
                                    }
                                    if (jjte002 instanceof RuntimeException) {
                                        throw ((RuntimeException) jjte002);
                                    }
                                    if (jjte002 instanceof ParseException) {
                                        throw ((ParseException) jjte002);
                                    }
                                    throw ((Error) jjte002);
                                } catch (Throwable th2) {
                                    if (1 != 0) {
                                        this.jjtree.closeNodeScope(jjtn002, 2);
                                    }
                                    throw th2;
                                }
                            }
                        default:
                            this.jj_la1[17] = this.jj_gen;
                            jj_consume_token(-1);
                            throw new ParseException();
                    }
                default:
                    this.jj_la1[14] = this.jj_gen;
                    return;
            }
        }
    }

    public final void Compare() throws ParseException {
        Concatenation();
        while (true) {
            switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
                case 25:
                case 26:
                case 27:
                case 28:
                case 29:
                case 30:
                case 31:
                case 32:
                    switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
                        case 25:
                        case 26:
                            switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
                                case 25:
                                    jj_consume_token(25);
                                    break;
                                case 26:
                                    jj_consume_token(26);
                                    break;
                                default:
                                    this.jj_la1[20] = this.jj_gen;
                                    jj_consume_token(-1);
                                    throw new ParseException();
                            }
                            AstGreaterThan jjtn002 = new AstGreaterThan(15);
                            this.jjtree.openNodeScope(jjtn002);
                            try {
                                Concatenation();
                                if (1 == 0) {
                                    break;
                                } else {
                                    this.jjtree.closeNodeScope(jjtn002, 2);
                                    break;
                                }
                            } catch (Throwable jjte002) {
                                try {
                                    if (1 != 0) {
                                        this.jjtree.clearNodeScope(jjtn002);
                                    } else {
                                        this.jjtree.popNode();
                                    }
                                    if (jjte002 instanceof RuntimeException) {
                                        throw ((RuntimeException) jjte002);
                                    }
                                    if (jjte002 instanceof ParseException) {
                                        throw ((ParseException) jjte002);
                                    }
                                    throw ((Error) jjte002);
                                } catch (Throwable th) {
                                    if (1 != 0) {
                                        this.jjtree.closeNodeScope(jjtn002, 2);
                                    }
                                    throw th;
                                }
                            }
                        case 27:
                        case 28:
                            switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
                                case 27:
                                    jj_consume_token(27);
                                    break;
                                case 28:
                                    jj_consume_token(28);
                                    break;
                                default:
                                    this.jj_la1[19] = this.jj_gen;
                                    jj_consume_token(-1);
                                    throw new ParseException();
                            }
                            AstLessThan jjtn001 = new AstLessThan(14);
                            this.jjtree.openNodeScope(jjtn001);
                            try {
                                Concatenation();
                                if (1 == 0) {
                                    break;
                                } else {
                                    this.jjtree.closeNodeScope(jjtn001, 2);
                                    break;
                                }
                            } catch (Throwable jjte001) {
                                try {
                                    if (1 != 0) {
                                        this.jjtree.clearNodeScope(jjtn001);
                                    } else {
                                        this.jjtree.popNode();
                                    }
                                    if (jjte001 instanceof RuntimeException) {
                                        throw ((RuntimeException) jjte001);
                                    }
                                    if (jjte001 instanceof ParseException) {
                                        throw ((ParseException) jjte001);
                                    }
                                    throw ((Error) jjte001);
                                } catch (Throwable th2) {
                                    if (1 != 0) {
                                        this.jjtree.closeNodeScope(jjtn001, 2);
                                    }
                                    throw th2;
                                }
                            }
                        case 29:
                        case 30:
                            switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
                                case 29:
                                    jj_consume_token(29);
                                    break;
                                case 30:
                                    jj_consume_token(30);
                                    break;
                                default:
                                    this.jj_la1[22] = this.jj_gen;
                                    jj_consume_token(-1);
                                    throw new ParseException();
                            }
                            AstGreaterThanEqual jjtn004 = new AstGreaterThanEqual(17);
                            this.jjtree.openNodeScope(jjtn004);
                            try {
                                Concatenation();
                                if (1 == 0) {
                                    break;
                                } else {
                                    this.jjtree.closeNodeScope(jjtn004, 2);
                                    break;
                                }
                            } catch (Throwable jjte004) {
                                try {
                                    if (1 != 0) {
                                        this.jjtree.clearNodeScope(jjtn004);
                                    } else {
                                        this.jjtree.popNode();
                                    }
                                    if (jjte004 instanceof RuntimeException) {
                                        throw ((RuntimeException) jjte004);
                                    }
                                    if (jjte004 instanceof ParseException) {
                                        throw ((ParseException) jjte004);
                                    }
                                    throw ((Error) jjte004);
                                } catch (Throwable th3) {
                                    if (1 != 0) {
                                        this.jjtree.closeNodeScope(jjtn004, 2);
                                    }
                                    throw th3;
                                }
                            }
                        case 31:
                        case 32:
                            switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
                                case 31:
                                    jj_consume_token(31);
                                    break;
                                case 32:
                                    jj_consume_token(32);
                                    break;
                                default:
                                    this.jj_la1[21] = this.jj_gen;
                                    jj_consume_token(-1);
                                    throw new ParseException();
                            }
                            AstLessThanEqual jjtn003 = new AstLessThanEqual(16);
                            this.jjtree.openNodeScope(jjtn003);
                            try {
                                Concatenation();
                                if (1 == 0) {
                                    break;
                                } else {
                                    this.jjtree.closeNodeScope(jjtn003, 2);
                                    break;
                                }
                            } catch (Throwable jjte003) {
                                try {
                                    if (1 != 0) {
                                        this.jjtree.clearNodeScope(jjtn003);
                                    } else {
                                        this.jjtree.popNode();
                                    }
                                    if (jjte003 instanceof RuntimeException) {
                                        throw ((RuntimeException) jjte003);
                                    }
                                    if (jjte003 instanceof ParseException) {
                                        throw ((ParseException) jjte003);
                                    }
                                    throw ((Error) jjte003);
                                } catch (Throwable th4) {
                                    if (1 != 0) {
                                        this.jjtree.closeNodeScope(jjtn003, 2);
                                    }
                                    throw th4;
                                }
                            }
                        default:
                            this.jj_la1[23] = this.jj_gen;
                            jj_consume_token(-1);
                            throw new ParseException();
                    }
                default:
                    this.jj_la1[18] = this.jj_gen;
                    return;
            }
        }
    }

    public final void Concatenation() throws ParseException {
        Math();
        while (true) {
            switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
                case 53:
                    jj_consume_token(53);
                    AstConcatenation jjtn001 = new AstConcatenation(18);
                    this.jjtree.openNodeScope(jjtn001);
                    try {
                        Math();
                        if (1 != 0) {
                            this.jjtree.closeNodeScope(jjtn001, 2);
                        }
                    } catch (Throwable jjte001) {
                        try {
                            if (1 != 0) {
                                this.jjtree.clearNodeScope(jjtn001);
                            } else {
                                this.jjtree.popNode();
                            }
                            if (jjte001 instanceof RuntimeException) {
                                throw ((RuntimeException) jjte001);
                            }
                            if (jjte001 instanceof ParseException) {
                                throw ((ParseException) jjte001);
                            }
                            throw ((Error) jjte001);
                        } catch (Throwable th) {
                            if (1 != 0) {
                                this.jjtree.closeNodeScope(jjtn001, 2);
                            }
                            throw th;
                        }
                    }
                default:
                    this.jj_la1[24] = this.jj_gen;
                    return;
            }
        }
    }

    public final void Math() throws ParseException {
        Multiplication();
        while (true) {
            switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
                case 46:
                case 47:
                    switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
                        case 46:
                            jj_consume_token(46);
                            AstPlus jjtn001 = new AstPlus(19);
                            this.jjtree.openNodeScope(jjtn001);
                            try {
                                Multiplication();
                                if (1 == 0) {
                                    break;
                                } else {
                                    this.jjtree.closeNodeScope(jjtn001, 2);
                                    break;
                                }
                            } catch (Throwable jjte001) {
                                try {
                                    if (1 != 0) {
                                        this.jjtree.clearNodeScope(jjtn001);
                                    } else {
                                        this.jjtree.popNode();
                                    }
                                    if (jjte001 instanceof RuntimeException) {
                                        throw ((RuntimeException) jjte001);
                                    }
                                    if (jjte001 instanceof ParseException) {
                                        throw ((ParseException) jjte001);
                                    }
                                    throw ((Error) jjte001);
                                } catch (Throwable th) {
                                    if (1 != 0) {
                                        this.jjtree.closeNodeScope(jjtn001, 2);
                                    }
                                    throw th;
                                }
                            }
                        case 47:
                            jj_consume_token(47);
                            AstMinus jjtn002 = new AstMinus(20);
                            this.jjtree.openNodeScope(jjtn002);
                            try {
                                Multiplication();
                                if (1 == 0) {
                                    break;
                                } else {
                                    this.jjtree.closeNodeScope(jjtn002, 2);
                                    break;
                                }
                            } catch (Throwable jjte002) {
                                try {
                                    if (1 != 0) {
                                        this.jjtree.clearNodeScope(jjtn002);
                                    } else {
                                        this.jjtree.popNode();
                                    }
                                    if (jjte002 instanceof RuntimeException) {
                                        throw ((RuntimeException) jjte002);
                                    }
                                    if (jjte002 instanceof ParseException) {
                                        throw ((ParseException) jjte002);
                                    }
                                    throw ((Error) jjte002);
                                } catch (Throwable th2) {
                                    if (1 != 0) {
                                        this.jjtree.closeNodeScope(jjtn002, 2);
                                    }
                                    throw th2;
                                }
                            }
                        default:
                            this.jj_la1[26] = this.jj_gen;
                            jj_consume_token(-1);
                            throw new ParseException();
                    }
                default:
                    this.jj_la1[25] = this.jj_gen;
                    return;
            }
        }
    }

    public final void Multiplication() throws ParseException {
        Unary();
        while (true) {
            switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
                case 45:
                case 49:
                case 50:
                case 51:
                case 52:
                    switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
                        case 45:
                            jj_consume_token(45);
                            AstMult jjtn001 = new AstMult(21);
                            this.jjtree.openNodeScope(jjtn001);
                            try {
                                Unary();
                                if (1 == 0) {
                                    break;
                                } else {
                                    this.jjtree.closeNodeScope(jjtn001, 2);
                                    break;
                                }
                            } catch (Throwable jjte001) {
                                try {
                                    if (1 != 0) {
                                        this.jjtree.clearNodeScope(jjtn001);
                                    } else {
                                        this.jjtree.popNode();
                                    }
                                    if (jjte001 instanceof RuntimeException) {
                                        throw ((RuntimeException) jjte001);
                                    }
                                    if (jjte001 instanceof ParseException) {
                                        throw ((ParseException) jjte001);
                                    }
                                    throw ((Error) jjte001);
                                } catch (Throwable th) {
                                    if (1 != 0) {
                                        this.jjtree.closeNodeScope(jjtn001, 2);
                                    }
                                    throw th;
                                }
                            }
                        case 46:
                        case 47:
                        case 48:
                        default:
                            this.jj_la1[30] = this.jj_gen;
                            jj_consume_token(-1);
                            throw new ParseException();
                        case 49:
                        case 50:
                            switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
                                case 49:
                                    jj_consume_token(49);
                                    break;
                                case 50:
                                    jj_consume_token(50);
                                    break;
                                default:
                                    this.jj_la1[28] = this.jj_gen;
                                    jj_consume_token(-1);
                                    throw new ParseException();
                            }
                            AstDiv jjtn002 = new AstDiv(22);
                            this.jjtree.openNodeScope(jjtn002);
                            try {
                                Unary();
                                if (1 == 0) {
                                    break;
                                } else {
                                    this.jjtree.closeNodeScope(jjtn002, 2);
                                    break;
                                }
                            } catch (Throwable jjte002) {
                                try {
                                    if (1 != 0) {
                                        this.jjtree.clearNodeScope(jjtn002);
                                    } else {
                                        this.jjtree.popNode();
                                    }
                                    if (jjte002 instanceof RuntimeException) {
                                        throw ((RuntimeException) jjte002);
                                    }
                                    if (jjte002 instanceof ParseException) {
                                        throw ((ParseException) jjte002);
                                    }
                                    throw ((Error) jjte002);
                                } catch (Throwable th2) {
                                    if (1 != 0) {
                                        this.jjtree.closeNodeScope(jjtn002, 2);
                                    }
                                    throw th2;
                                }
                            }
                        case 51:
                        case 52:
                            switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
                                case 51:
                                    jj_consume_token(51);
                                    break;
                                case 52:
                                    jj_consume_token(52);
                                    break;
                                default:
                                    this.jj_la1[29] = this.jj_gen;
                                    jj_consume_token(-1);
                                    throw new ParseException();
                            }
                            AstMod jjtn003 = new AstMod(23);
                            this.jjtree.openNodeScope(jjtn003);
                            try {
                                Unary();
                                if (1 == 0) {
                                    break;
                                } else {
                                    this.jjtree.closeNodeScope(jjtn003, 2);
                                    break;
                                }
                            } catch (Throwable jjte003) {
                                try {
                                    if (1 != 0) {
                                        this.jjtree.clearNodeScope(jjtn003);
                                    } else {
                                        this.jjtree.popNode();
                                    }
                                    if (jjte003 instanceof RuntimeException) {
                                        throw ((RuntimeException) jjte003);
                                    }
                                    if (jjte003 instanceof ParseException) {
                                        throw ((ParseException) jjte003);
                                    }
                                    throw ((Error) jjte003);
                                } catch (Throwable th3) {
                                    if (1 != 0) {
                                        this.jjtree.closeNodeScope(jjtn003, 2);
                                    }
                                    throw th3;
                                }
                            }
                    }
                case 46:
                case 47:
                case 48:
                default:
                    this.jj_la1[27] = this.jj_gen;
                    return;
            }
        }
    }

    public final void Unary() throws ParseException {
        switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
            case 8:
            case 10:
            case 11:
            case 13:
            case 14:
            case 15:
            case 16:
            case 18:
            case 20:
            case 56:
                Value();
                return;
            case 9:
            case 12:
            case 17:
            case 19:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 39:
            case 40:
            case 41:
            case 42:
            case 44:
            case 45:
            case 46:
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            default:
                this.jj_la1[32] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
            case 37:
            case 38:
                switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
                    case 37:
                        jj_consume_token(37);
                        break;
                    case 38:
                        jj_consume_token(38);
                        break;
                    default:
                        this.jj_la1[31] = this.jj_gen;
                        jj_consume_token(-1);
                        throw new ParseException();
                }
                AstNot jjtn002 = new AstNot(25);
                this.jjtree.openNodeScope(jjtn002);
                try {
                    Unary();
                    if (1 != 0) {
                        this.jjtree.closeNodeScope((Node) jjtn002, true);
                        return;
                    }
                    return;
                } catch (Throwable jjte002) {
                    try {
                        if (1 != 0) {
                            this.jjtree.clearNodeScope(jjtn002);
                        } else {
                            this.jjtree.popNode();
                        }
                        if (jjte002 instanceof RuntimeException) {
                            throw ((RuntimeException) jjte002);
                        }
                        if (jjte002 instanceof ParseException) {
                            throw ((ParseException) jjte002);
                        }
                        throw ((Error) jjte002);
                    } catch (Throwable th) {
                        if (1 != 0) {
                            this.jjtree.closeNodeScope((Node) jjtn002, true);
                        }
                        throw th;
                    }
                }
            case ELParserConstants.EMPTY /* 43 */:
                jj_consume_token(43);
                AstEmpty jjtn003 = new AstEmpty(26);
                this.jjtree.openNodeScope(jjtn003);
                try {
                    Unary();
                    if (1 != 0) {
                        this.jjtree.closeNodeScope((Node) jjtn003, true);
                        return;
                    }
                    return;
                } catch (Throwable jjte003) {
                    try {
                        if (1 != 0) {
                            this.jjtree.clearNodeScope(jjtn003);
                        } else {
                            this.jjtree.popNode();
                        }
                        if (jjte003 instanceof RuntimeException) {
                            throw ((RuntimeException) jjte003);
                        }
                        if (jjte003 instanceof ParseException) {
                            throw ((ParseException) jjte003);
                        }
                        throw ((Error) jjte003);
                    } catch (Throwable th2) {
                        if (1 != 0) {
                            this.jjtree.closeNodeScope((Node) jjtn003, true);
                        }
                        throw th2;
                    }
                }
            case 47:
                jj_consume_token(47);
                AstNegative jjtn001 = new AstNegative(24);
                this.jjtree.openNodeScope(jjtn001);
                try {
                    Unary();
                    if (1 != 0) {
                        this.jjtree.closeNodeScope((Node) jjtn001, true);
                        return;
                    }
                    return;
                } catch (Throwable jjte001) {
                    try {
                        if (1 != 0) {
                            this.jjtree.clearNodeScope(jjtn001);
                        } else {
                            this.jjtree.popNode();
                        }
                        if (jjte001 instanceof RuntimeException) {
                            throw ((RuntimeException) jjte001);
                        }
                        if (jjte001 instanceof ParseException) {
                            throw ((ParseException) jjte001);
                        }
                        throw ((Error) jjte001);
                    } catch (Throwable th3) {
                        if (1 != 0) {
                            this.jjtree.closeNodeScope((Node) jjtn001, true);
                        }
                        throw th3;
                    }
                }
        }
    }

    public final void Value() throws ParseException {
        AstValue jjtn001 = new AstValue(27);
        this.jjtree.openNodeScope(jjtn001);
        try {
            ValuePrefix();
            while (true) {
                switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
                    case 17:
                    case 20:
                        ValueSuffix();
                    default:
                        this.jj_la1[33] = this.jj_gen;
                        if (1 != 0) {
                            this.jjtree.closeNodeScope(jjtn001, this.jjtree.nodeArity() > 1);
                            return;
                        }
                        return;
                }
            }
        } catch (Throwable jjte001) {
            try {
                if (1 != 0) {
                    this.jjtree.clearNodeScope(jjtn001);
                } else {
                    this.jjtree.popNode();
                }
                if (jjte001 instanceof RuntimeException) {
                    throw ((RuntimeException) jjte001);
                }
                if (jjte001 instanceof ParseException) {
                    throw ((ParseException) jjte001);
                }
                throw ((Error) jjte001);
            } catch (Throwable th) {
                if (1 != 0) {
                    this.jjtree.closeNodeScope(jjtn001, this.jjtree.nodeArity() > 1);
                }
                throw th;
            }
        }
    }

    public final void ValuePrefix() throws ParseException {
        switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
            case 8:
            case 18:
            case 20:
            case 56:
                NonLiteral();
                return;
            case 10:
            case 11:
            case 13:
            case 14:
            case 15:
            case 16:
                Literal();
                return;
            default:
                this.jj_la1[34] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
        }
    }

    public final void ValueSuffix() throws ParseException {
        switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
            case 17:
                DotSuffix();
                break;
            case 20:
                BracketSuffix();
                break;
            default:
                this.jj_la1[35] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
        }
        switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
            case 18:
                MethodParameters();
                return;
            default:
                this.jj_la1[36] = this.jj_gen;
                return;
        }
    }

    public final void DotSuffix() throws ParseException {
        AstDotSuffix jjtn000 = new AstDotSuffix(28);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            jj_consume_token(17);
            Token t = jj_consume_token(56);
            this.jjtree.closeNodeScope((Node) jjtn000, true);
            jjtc000 = false;
            jjtn000.setImage(t.image);
            if (0 != 0) {
                this.jjtree.closeNodeScope((Node) jjtn000, true);
            }
        } catch (Throwable th) {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node) jjtn000, true);
            }
            throw th;
        }
    }

    public final void BracketSuffix() throws ParseException {
        AstBracketSuffix jjtn000 = new AstBracketSuffix(29);
        this.jjtree.openNodeScope(jjtn000);
        try {
            jj_consume_token(20);
            Expression();
            jj_consume_token(21);
            if (1 != 0) {
                this.jjtree.closeNodeScope((Node) jjtn000, true);
            }
        } catch (Throwable jjte000) {
            try {
                if (1 != 0) {
                    this.jjtree.clearNodeScope(jjtn000);
                } else {
                    this.jjtree.popNode();
                }
                if (jjte000 instanceof RuntimeException) {
                    throw ((RuntimeException) jjte000);
                }
                if (jjte000 instanceof ParseException) {
                    throw ((ParseException) jjte000);
                }
                throw ((Error) jjte000);
            } catch (Throwable th) {
                if (1 != 0) {
                    this.jjtree.closeNodeScope((Node) jjtn000, true);
                }
                throw th;
            }
        }
    }

    public final void MethodParameters() throws ParseException {
        AstMethodParameters jjtn000 = new AstMethodParameters(30);
        this.jjtree.openNodeScope(jjtn000);
        try {
            jj_consume_token(18);
            switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
                case 8:
                case 10:
                case 11:
                case 13:
                case 14:
                case 15:
                case 16:
                case 18:
                case 20:
                case 37:
                case 38:
                case ELParserConstants.EMPTY /* 43 */:
                case 47:
                case 56:
                    Expression();
                    while (true) {
                        switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
                            case 24:
                                jj_consume_token(24);
                                Expression();
                            default:
                                this.jj_la1[37] = this.jj_gen;
                                break;
                        }
                    }
                case 9:
                case 12:
                case 17:
                case 19:
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                case 26:
                case 27:
                case 28:
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 39:
                case 40:
                case 41:
                case 42:
                case 44:
                case 45:
                case 46:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                default:
                    this.jj_la1[38] = this.jj_gen;
                    break;
            }
            jj_consume_token(19);
            if (1 != 0) {
                this.jjtree.closeNodeScope((Node) jjtn000, true);
            }
        } catch (Throwable jjte000) {
            try {
                if (1 != 0) {
                    this.jjtree.clearNodeScope(jjtn000);
                } else {
                    this.jjtree.popNode();
                }
                if (jjte000 instanceof RuntimeException) {
                    throw ((RuntimeException) jjte000);
                }
                if (jjte000 instanceof ParseException) {
                    throw ((ParseException) jjte000);
                }
                throw ((Error) jjte000);
            } catch (Throwable th) {
                if (1 != 0) {
                    this.jjtree.closeNodeScope((Node) jjtn000, true);
                }
                throw th;
            }
        }
    }

    public final void NonLiteral() throws ParseException {
        if (jj_2_6(5)) {
            LambdaExpressionOrInvocation();
            return;
        }
        switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
            case 18:
                jj_consume_token(18);
                Expression();
                jj_consume_token(19);
                return;
            default:
                this.jj_la1[39] = this.jj_gen;
                if (jj_2_7(Integer.MAX_VALUE)) {
                    Function();
                    return;
                }
                switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
                    case 56:
                        Identifier();
                        return;
                    default:
                        this.jj_la1[40] = this.jj_gen;
                        if (jj_2_8(3)) {
                            SetData();
                            return;
                        }
                        switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
                            case 8:
                                MapData();
                                return;
                            case 20:
                                ListData();
                                return;
                            default:
                                this.jj_la1[41] = this.jj_gen;
                                jj_consume_token(-1);
                                throw new ParseException();
                        }
                }
        }
    }

    public final void SetData() throws ParseException {
        AstSetData jjtn000 = new AstSetData(31);
        this.jjtree.openNodeScope(jjtn000);
        try {
            jj_consume_token(8);
            switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
                case 8:
                case 10:
                case 11:
                case 13:
                case 14:
                case 15:
                case 16:
                case 18:
                case 20:
                case 37:
                case 38:
                case ELParserConstants.EMPTY /* 43 */:
                case 47:
                case 56:
                    Expression();
                    while (true) {
                        switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
                            case 24:
                                jj_consume_token(24);
                                Expression();
                            default:
                                this.jj_la1[42] = this.jj_gen;
                                break;
                        }
                    }
                case 9:
                case 12:
                case 17:
                case 19:
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                case 26:
                case 27:
                case 28:
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 39:
                case 40:
                case 41:
                case 42:
                case 44:
                case 45:
                case 46:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                default:
                    this.jj_la1[43] = this.jj_gen;
                    break;
            }
            jj_consume_token(9);
            if (1 != 0) {
                this.jjtree.closeNodeScope((Node) jjtn000, true);
            }
        } catch (Throwable jjte000) {
            try {
                if (1 != 0) {
                    this.jjtree.clearNodeScope(jjtn000);
                } else {
                    this.jjtree.popNode();
                }
                if (jjte000 instanceof RuntimeException) {
                    throw ((RuntimeException) jjte000);
                }
                if (jjte000 instanceof ParseException) {
                    throw ((ParseException) jjte000);
                }
                throw ((Error) jjte000);
            } catch (Throwable th) {
                if (1 != 0) {
                    this.jjtree.closeNodeScope((Node) jjtn000, true);
                }
                throw th;
            }
        }
    }

    public final void ListData() throws ParseException {
        AstListData jjtn000 = new AstListData(32);
        this.jjtree.openNodeScope(jjtn000);
        try {
            jj_consume_token(20);
            switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
                case 8:
                case 10:
                case 11:
                case 13:
                case 14:
                case 15:
                case 16:
                case 18:
                case 20:
                case 37:
                case 38:
                case ELParserConstants.EMPTY /* 43 */:
                case 47:
                case 56:
                    Expression();
                    while (true) {
                        switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
                            case 24:
                                jj_consume_token(24);
                                Expression();
                            default:
                                this.jj_la1[44] = this.jj_gen;
                                break;
                        }
                    }
                case 9:
                case 12:
                case 17:
                case 19:
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                case 26:
                case 27:
                case 28:
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 39:
                case 40:
                case 41:
                case 42:
                case 44:
                case 45:
                case 46:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                default:
                    this.jj_la1[45] = this.jj_gen;
                    break;
            }
            jj_consume_token(21);
            if (1 != 0) {
                this.jjtree.closeNodeScope((Node) jjtn000, true);
            }
        } catch (Throwable jjte000) {
            try {
                if (1 != 0) {
                    this.jjtree.clearNodeScope(jjtn000);
                } else {
                    this.jjtree.popNode();
                }
                if (jjte000 instanceof RuntimeException) {
                    throw ((RuntimeException) jjte000);
                }
                if (jjte000 instanceof ParseException) {
                    throw ((ParseException) jjte000);
                }
                throw ((Error) jjte000);
            } catch (Throwable th) {
                if (1 != 0) {
                    this.jjtree.closeNodeScope((Node) jjtn000, true);
                }
                throw th;
            }
        }
    }

    public final void MapData() throws ParseException {
        AstMapData jjtn000 = new AstMapData(33);
        this.jjtree.openNodeScope(jjtn000);
        try {
            jj_consume_token(8);
            switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
                case 8:
                case 10:
                case 11:
                case 13:
                case 14:
                case 15:
                case 16:
                case 18:
                case 20:
                case 37:
                case 38:
                case ELParserConstants.EMPTY /* 43 */:
                case 47:
                case 56:
                    MapEntry();
                    while (true) {
                        switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
                            case 24:
                                jj_consume_token(24);
                                MapEntry();
                            default:
                                this.jj_la1[46] = this.jj_gen;
                                break;
                        }
                    }
                case 9:
                case 12:
                case 17:
                case 19:
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                case 26:
                case 27:
                case 28:
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 39:
                case 40:
                case 41:
                case 42:
                case 44:
                case 45:
                case 46:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                default:
                    this.jj_la1[47] = this.jj_gen;
                    break;
            }
            jj_consume_token(9);
            if (1 != 0) {
                this.jjtree.closeNodeScope((Node) jjtn000, true);
            }
        } catch (Throwable jjte000) {
            try {
                if (1 != 0) {
                    this.jjtree.clearNodeScope(jjtn000);
                } else {
                    this.jjtree.popNode();
                }
                if (jjte000 instanceof RuntimeException) {
                    throw ((RuntimeException) jjte000);
                }
                if (jjte000 instanceof ParseException) {
                    throw ((ParseException) jjte000);
                }
                throw ((Error) jjte000);
            } catch (Throwable th) {
                if (1 != 0) {
                    this.jjtree.closeNodeScope((Node) jjtn000, true);
                }
                throw th;
            }
        }
    }

    public final void MapEntry() throws ParseException {
        AstMapEntry jjtn000 = new AstMapEntry(34);
        this.jjtree.openNodeScope(jjtn000);
        try {
            Expression();
            jj_consume_token(22);
            Expression();
            if (1 != 0) {
                this.jjtree.closeNodeScope((Node) jjtn000, true);
            }
        } catch (Throwable jjte000) {
            try {
                if (1 != 0) {
                    this.jjtree.clearNodeScope(jjtn000);
                } else {
                    this.jjtree.popNode();
                }
                if (jjte000 instanceof RuntimeException) {
                    throw ((RuntimeException) jjte000);
                }
                if (jjte000 instanceof ParseException) {
                    throw ((ParseException) jjte000);
                }
                throw ((Error) jjte000);
            } catch (Throwable th) {
                if (1 != 0) {
                    this.jjtree.closeNodeScope((Node) jjtn000, true);
                }
                throw th;
            }
        }
    }

    public final void Identifier() throws ParseException {
        AstIdentifier jjtn000 = new AstIdentifier(35);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            Token t = jj_consume_token(56);
            this.jjtree.closeNodeScope((Node) jjtn000, true);
            jjtc000 = false;
            jjtn000.setImage(t.image);
            if (0 != 0) {
                this.jjtree.closeNodeScope((Node) jjtn000, true);
            }
        } catch (Throwable th) {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node) jjtn000, true);
            }
            throw th;
        }
    }

    public final void Function() throws ParseException {
        AstFunction jjtn000 = new AstFunction(36);
        this.jjtree.openNodeScope(jjtn000);
        Token t1 = null;
        try {
            Token t0 = jj_consume_token(56);
            switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
                case 22:
                    jj_consume_token(22);
                    t1 = jj_consume_token(56);
                    break;
                default:
                    this.jj_la1[48] = this.jj_gen;
                    break;
            }
            if (t1 != null) {
                jjtn000.setPrefix(t0.image);
                jjtn000.setLocalName(t1.image);
            } else {
                jjtn000.setLocalName(t0.image);
            }
            while (true) {
                MethodParameters();
                switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
                    case 18:
                    default:
                        this.jj_la1[49] = this.jj_gen;
                        if (jjtc000) {
                            return;
                        }
                        return;
                }
            }
        } catch (Throwable jjte000) {
            try {
                if (1 != 0) {
                    this.jjtree.clearNodeScope(jjtn000);
                } else {
                    this.jjtree.popNode();
                }
                if (jjte000 instanceof RuntimeException) {
                    throw ((RuntimeException) jjte000);
                }
                if (jjte000 instanceof ParseException) {
                    throw ((ParseException) jjte000);
                }
                throw ((Error) jjte000);
            } finally {
                if (1 != 0) {
                    this.jjtree.closeNodeScope((Node) jjtn000, true);
                }
            }
        }
    }

    public final void Literal() throws ParseException {
        switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
            case 10:
                Integer();
                return;
            case 11:
                FloatingPoint();
                return;
            case 12:
            default:
                this.jj_la1[50] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
            case 13:
                String();
                return;
            case 14:
            case 15:
                Boolean();
                return;
            case 16:
                Null();
                return;
        }
    }

    public final void Boolean() throws ParseException {
        switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
            case 14:
                AstTrue jjtn001 = new AstTrue(37);
                this.jjtree.openNodeScope(jjtn001);
                try {
                    jj_consume_token(14);
                    if (1 != 0) {
                        this.jjtree.closeNodeScope((Node) jjtn001, true);
                        return;
                    }
                    return;
                } catch (Throwable th) {
                    if (1 != 0) {
                        this.jjtree.closeNodeScope((Node) jjtn001, true);
                    }
                    throw th;
                }
            case 15:
                AstFalse jjtn002 = new AstFalse(38);
                this.jjtree.openNodeScope(jjtn002);
                try {
                    jj_consume_token(15);
                    if (1 != 0) {
                        this.jjtree.closeNodeScope((Node) jjtn002, true);
                        return;
                    }
                    return;
                } catch (Throwable th2) {
                    if (1 != 0) {
                        this.jjtree.closeNodeScope((Node) jjtn002, true);
                    }
                    throw th2;
                }
            default:
                this.jj_la1[51] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
        }
    }

    public final void FloatingPoint() throws ParseException {
        AstFloatingPoint jjtn000 = new AstFloatingPoint(39);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            Token t = jj_consume_token(11);
            this.jjtree.closeNodeScope((Node) jjtn000, true);
            jjtc000 = false;
            jjtn000.setImage(t.image);
            if (0 != 0) {
                this.jjtree.closeNodeScope((Node) jjtn000, true);
            }
        } catch (Throwable th) {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node) jjtn000, true);
            }
            throw th;
        }
    }

    public final void Integer() throws ParseException {
        AstInteger jjtn000 = new AstInteger(40);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            Token t = jj_consume_token(10);
            this.jjtree.closeNodeScope((Node) jjtn000, true);
            jjtc000 = false;
            jjtn000.setImage(t.image);
            if (0 != 0) {
                this.jjtree.closeNodeScope((Node) jjtn000, true);
            }
        } catch (Throwable th) {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node) jjtn000, true);
            }
            throw th;
        }
    }

    public final void String() throws ParseException {
        AstString jjtn000 = new AstString(41);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            Token t = jj_consume_token(13);
            this.jjtree.closeNodeScope((Node) jjtn000, true);
            jjtc000 = false;
            jjtn000.setImage(t.image);
            if (0 != 0) {
                this.jjtree.closeNodeScope((Node) jjtn000, true);
            }
        } catch (Throwable th) {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node) jjtn000, true);
            }
            throw th;
        }
    }

    public final void Null() throws ParseException {
        AstNull jjtn000 = new AstNull(42);
        this.jjtree.openNodeScope(jjtn000);
        try {
            jj_consume_token(16);
            if (1 != 0) {
                this.jjtree.closeNodeScope((Node) jjtn000, true);
            }
        } catch (Throwable th) {
            if (1 != 0) {
                this.jjtree.closeNodeScope((Node) jjtn000, true);
            }
            throw th;
        }
    }

    private boolean jj_2_1(int xla) {
        this.jj_la = xla;
        Token token = this.token;
        this.jj_scanpos = token;
        this.jj_lastpos = token;
        try {
            boolean z = !jj_3_1();
            jj_save(0, xla);
            return z;
        } catch (LookaheadSuccess e) {
            jj_save(0, xla);
            return true;
        } catch (Throwable th) {
            jj_save(0, xla);
            throw th;
        }
    }

    private boolean jj_2_2(int xla) {
        this.jj_la = xla;
        Token token = this.token;
        this.jj_scanpos = token;
        this.jj_lastpos = token;
        try {
            boolean z = !jj_3_2();
            jj_save(1, xla);
            return z;
        } catch (LookaheadSuccess e) {
            jj_save(1, xla);
            return true;
        } catch (Throwable th) {
            jj_save(1, xla);
            throw th;
        }
    }

    private boolean jj_2_3(int xla) {
        this.jj_la = xla;
        Token token = this.token;
        this.jj_scanpos = token;
        this.jj_lastpos = token;
        try {
            boolean z = !jj_3_3();
            jj_save(2, xla);
            return z;
        } catch (LookaheadSuccess e) {
            jj_save(2, xla);
            return true;
        } catch (Throwable th) {
            jj_save(2, xla);
            throw th;
        }
    }

    private boolean jj_2_4(int xla) {
        this.jj_la = xla;
        Token token = this.token;
        this.jj_scanpos = token;
        this.jj_lastpos = token;
        try {
            boolean z = !jj_3_4();
            jj_save(3, xla);
            return z;
        } catch (LookaheadSuccess e) {
            jj_save(3, xla);
            return true;
        } catch (Throwable th) {
            jj_save(3, xla);
            throw th;
        }
    }

    private boolean jj_2_5(int xla) {
        this.jj_la = xla;
        Token token = this.token;
        this.jj_scanpos = token;
        this.jj_lastpos = token;
        try {
            boolean z = !jj_3_5();
            jj_save(4, xla);
            return z;
        } catch (LookaheadSuccess e) {
            jj_save(4, xla);
            return true;
        } catch (Throwable th) {
            jj_save(4, xla);
            throw th;
        }
    }

    private boolean jj_2_6(int xla) {
        this.jj_la = xla;
        Token token = this.token;
        this.jj_scanpos = token;
        this.jj_lastpos = token;
        try {
            boolean z = !jj_3_6();
            jj_save(5, xla);
            return z;
        } catch (LookaheadSuccess e) {
            jj_save(5, xla);
            return true;
        } catch (Throwable th) {
            jj_save(5, xla);
            throw th;
        }
    }

    private boolean jj_2_7(int xla) {
        this.jj_la = xla;
        Token token = this.token;
        this.jj_scanpos = token;
        this.jj_lastpos = token;
        try {
            boolean z = !jj_3_7();
            jj_save(6, xla);
            return z;
        } catch (LookaheadSuccess e) {
            jj_save(6, xla);
            return true;
        } catch (Throwable th) {
            jj_save(6, xla);
            throw th;
        }
    }

    private boolean jj_2_8(int xla) {
        this.jj_la = xla;
        Token token = this.token;
        this.jj_scanpos = token;
        this.jj_lastpos = token;
        try {
            boolean z = !jj_3_8();
            jj_save(7, xla);
            return z;
        } catch (LookaheadSuccess e) {
            jj_save(7, xla);
            return true;
        } catch (Throwable th) {
            jj_save(7, xla);
            throw th;
        }
    }

    private boolean jj_3R_41() {
        Token xsp = this.jj_scanpos;
        if (jj_scan_token(39)) {
            this.jj_scanpos = xsp;
            return jj_scan_token(40);
        }
        return false;
    }

    private boolean jj_3R_30() {
        return jj_3R_22();
    }

    private boolean jj_3R_40() {
        Token xsp;
        if (jj_3R_44()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!jj_3R_45());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_107() {
        return jj_3R_36();
    }

    private boolean jj_3R_105() {
        return jj_3R_107();
    }

    private boolean jj_3R_43() {
        return jj_scan_token(24) || jj_3R_38();
    }

    private boolean jj_3R_34() {
        Token xsp;
        if (jj_3R_40()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!jj_3R_41());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_37() {
        return jj_scan_token(24);
    }

    private boolean jj_3R_35() {
        Token xsp = this.jj_scanpos;
        if (jj_scan_token(41)) {
            this.jj_scanpos = xsp;
            return jj_scan_token(42);
        }
        return false;
    }

    private boolean jj_3R_99() {
        if (jj_scan_token(8)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (jj_3R_105()) {
            this.jj_scanpos = xsp;
        }
        return jj_scan_token(9);
    }

    private boolean jj_3R_104() {
        return jj_3R_36();
    }

    private boolean jj_3R_29() {
        Token xsp;
        if (jj_3R_34()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!jj_3R_35());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3_5() {
        return jj_scan_token(48) || jj_3R_22() || jj_scan_token(22);
    }

    private boolean jj_3R_98() {
        if (jj_scan_token(20)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (jj_3R_104()) {
            this.jj_scanpos = xsp;
        }
        return jj_scan_token(21);
    }

    private boolean jj_3R_39() {
        Token xsp;
        if (jj_3R_38()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!jj_3R_43());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_31() {
        Token xsp;
        if (jj_3R_36()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!jj_3R_37());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_22() {
        Token xsp;
        if (jj_3R_29()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!jj_3_5());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3_3() {
        return jj_3R_21();
    }

    private boolean jj_3R_25() {
        if (jj_scan_token(8)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (jj_3R_31()) {
            this.jj_scanpos = xsp;
        }
        return jj_scan_token(9);
    }

    private boolean jj_3_4() {
        return jj_3R_21();
    }

    private boolean jj_3R_24() {
        return jj_scan_token(56) || jj_scan_token(22);
    }

    private boolean jj_3_7() {
        Token xsp = this.jj_scanpos;
        if (jj_3R_24()) {
            this.jj_scanpos = xsp;
        }
        return jj_scan_token(56) || jj_scan_token(18);
    }

    private boolean jj_3R_33() {
        if (jj_scan_token(18)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (jj_3R_39()) {
            this.jj_scanpos = xsp;
        }
        return jj_scan_token(19);
    }

    private boolean jj_3R_89() {
        return jj_3R_99();
    }

    private boolean jj_3R_88() {
        return jj_3R_98();
    }

    private boolean jj_3R_23() {
        if (jj_scan_token(18) || jj_3R_27() || jj_scan_token(55)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (jj_3_4()) {
            this.jj_scanpos = xsp;
            if (jj_3R_30()) {
                return true;
            }
        }
        return jj_scan_token(19);
    }

    private boolean jj_3_8() {
        return jj_3R_25();
    }

    private boolean jj_3R_87() {
        return jj_3R_38();
    }

    private boolean jj_3R_86() {
        return jj_3R_97();
    }

    private boolean jj_3R_85() {
        return jj_scan_token(18) || jj_3R_36();
    }

    private boolean jj_3_6() {
        return jj_3R_23();
    }

    private boolean jj_3R_77() {
        Token xsp = this.jj_scanpos;
        if (jj_3_6()) {
            this.jj_scanpos = xsp;
            if (jj_3R_85()) {
                this.jj_scanpos = xsp;
                if (jj_3R_86()) {
                    this.jj_scanpos = xsp;
                    if (jj_3R_87()) {
                        this.jj_scanpos = xsp;
                        if (jj_3_8()) {
                            this.jj_scanpos = xsp;
                            if (jj_3R_88()) {
                                this.jj_scanpos = xsp;
                                return jj_3R_89();
                            }
                            return false;
                        }
                        return false;
                    }
                    return false;
                }
                return false;
            }
            return false;
        }
        return false;
    }

    private boolean jj_3R_32() {
        return jj_3R_38();
    }

    private boolean jj_3R_27() {
        Token xsp = this.jj_scanpos;
        if (jj_3R_32()) {
            this.jj_scanpos = xsp;
            return jj_3R_33();
        }
        return false;
    }

    private boolean jj_3_1() {
        return jj_scan_token(54) || jj_3R_20();
    }

    private boolean jj_3R_106() {
        return jj_scan_token(18);
    }

    private boolean jj_3R_21() {
        if (jj_3R_27() || jj_scan_token(55)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (jj_3_3()) {
            this.jj_scanpos = xsp;
            return jj_3R_28();
        }
        return false;
    }

    private boolean jj_3R_46() {
        return jj_scan_token(23);
    }

    private boolean jj_3R_91() {
        return jj_scan_token(20);
    }

    private boolean jj_3R_26() {
        Token xsp;
        if (jj_3R_22()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!jj_3_1());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_79() {
        return jj_3R_91();
    }

    private boolean jj_3_2() {
        return jj_3R_21();
    }

    private boolean jj_3R_20() {
        Token xsp = this.jj_scanpos;
        if (jj_3_2()) {
            this.jj_scanpos = xsp;
            return jj_3R_26();
        }
        return false;
    }

    private boolean jj_3R_90() {
        return jj_scan_token(17);
    }

    private boolean jj_3R_42() {
        Token xsp;
        if (jj_3R_20()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!jj_3R_46());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_78() {
        return jj_3R_90();
    }

    private boolean jj_3R_75() {
        Token xsp = this.jj_scanpos;
        if (jj_3R_78()) {
            this.jj_scanpos = xsp;
            return jj_3R_79();
        }
        return false;
    }

    private boolean jj_3R_36() {
        return jj_3R_42();
    }

    private boolean jj_3R_72() {
        return jj_3R_75();
    }

    private boolean jj_3R_74() {
        return jj_3R_77();
    }

    private boolean jj_3R_71() {
        Token xsp = this.jj_scanpos;
        if (jj_3R_73()) {
            this.jj_scanpos = xsp;
            return jj_3R_74();
        }
        return false;
    }

    private boolean jj_3R_73() {
        return jj_3R_76();
    }

    private boolean jj_3R_70() {
        Token xsp;
        if (jj_3R_71()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!jj_3R_72());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_96() {
        return jj_scan_token(16);
    }

    private boolean jj_3R_66() {
        return jj_3R_70();
    }

    private boolean jj_3R_65() {
        return jj_scan_token(43) || jj_3R_59();
    }

    private boolean jj_3R_64() {
        Token xsp = this.jj_scanpos;
        if (jj_scan_token(37)) {
            this.jj_scanpos = xsp;
            if (jj_scan_token(38)) {
                return true;
            }
        }
        return jj_3R_59();
    }

    private boolean jj_3R_59() {
        Token xsp = this.jj_scanpos;
        if (jj_3R_63()) {
            this.jj_scanpos = xsp;
            if (jj_3R_64()) {
                this.jj_scanpos = xsp;
                if (jj_3R_65()) {
                    this.jj_scanpos = xsp;
                    return jj_3R_66();
                }
                return false;
            }
            return false;
        }
        return false;
    }

    private boolean jj_3R_63() {
        return jj_scan_token(47) || jj_3R_59();
    }

    private boolean jj_3R_95() {
        return jj_scan_token(13);
    }

    private boolean jj_3R_69() {
        Token xsp = this.jj_scanpos;
        if (jj_scan_token(51)) {
            this.jj_scanpos = xsp;
            return jj_scan_token(52);
        }
        return false;
    }

    private boolean jj_3R_94() {
        return jj_scan_token(10);
    }

    private boolean jj_3R_68() {
        Token xsp = this.jj_scanpos;
        if (jj_scan_token(49)) {
            this.jj_scanpos = xsp;
            return jj_scan_token(50);
        }
        return false;
    }

    private boolean jj_3R_60() {
        Token xsp = this.jj_scanpos;
        if (jj_3R_67()) {
            this.jj_scanpos = xsp;
            if (jj_3R_68()) {
                this.jj_scanpos = xsp;
                return jj_3R_69();
            }
            return false;
        }
        return false;
    }

    private boolean jj_3R_67() {
        return jj_scan_token(45);
    }

    private boolean jj_3R_57() {
        Token xsp;
        if (jj_3R_59()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!jj_3R_60());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_93() {
        return jj_scan_token(11);
    }

    private boolean jj_3R_62() {
        return jj_scan_token(47);
    }

    private boolean jj_3R_101() {
        return jj_scan_token(15);
    }

    private boolean jj_3R_58() {
        Token xsp = this.jj_scanpos;
        if (jj_3R_61()) {
            this.jj_scanpos = xsp;
            return jj_3R_62();
        }
        return false;
    }

    private boolean jj_3R_61() {
        return jj_scan_token(46);
    }

    private boolean jj_3R_100() {
        return jj_scan_token(14);
    }

    private boolean jj_3R_92() {
        Token xsp = this.jj_scanpos;
        if (jj_3R_100()) {
            this.jj_scanpos = xsp;
            return jj_3R_101();
        }
        return false;
    }

    private boolean jj_3R_51() {
        Token xsp;
        if (jj_3R_57()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!jj_3R_58());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_84() {
        return jj_3R_96();
    }

    private boolean jj_3R_83() {
        return jj_3R_95();
    }

    private boolean jj_3R_52() {
        return jj_scan_token(53);
    }

    private boolean jj_3R_82() {
        return jj_3R_94();
    }

    private boolean jj_3R_81() {
        return jj_3R_93();
    }

    private boolean jj_3R_102() {
        return jj_scan_token(22);
    }

    private boolean jj_3R_76() {
        Token xsp = this.jj_scanpos;
        if (jj_3R_80()) {
            this.jj_scanpos = xsp;
            if (jj_3R_81()) {
                this.jj_scanpos = xsp;
                if (jj_3R_82()) {
                    this.jj_scanpos = xsp;
                    if (jj_3R_83()) {
                        this.jj_scanpos = xsp;
                        return jj_3R_84();
                    }
                    return false;
                }
                return false;
            }
            return false;
        }
        return false;
    }

    private boolean jj_3R_80() {
        return jj_3R_92();
    }

    private boolean jj_3R_47() {
        Token xsp;
        if (jj_3R_51()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!jj_3R_52());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_103() {
        return jj_3R_106();
    }

    private boolean jj_3R_56() {
        Token xsp = this.jj_scanpos;
        if (jj_scan_token(29)) {
            this.jj_scanpos = xsp;
            return jj_scan_token(30);
        }
        return false;
    }

    private boolean jj_3R_55() {
        Token xsp = this.jj_scanpos;
        if (jj_scan_token(31)) {
            this.jj_scanpos = xsp;
            return jj_scan_token(32);
        }
        return false;
    }

    private boolean jj_3R_54() {
        Token xsp = this.jj_scanpos;
        if (jj_scan_token(25)) {
            this.jj_scanpos = xsp;
            return jj_scan_token(26);
        }
        return false;
    }

    private boolean jj_3R_48() {
        Token xsp = this.jj_scanpos;
        if (jj_3R_53()) {
            this.jj_scanpos = xsp;
            if (jj_3R_54()) {
                this.jj_scanpos = xsp;
                if (jj_3R_55()) {
                    this.jj_scanpos = xsp;
                    return jj_3R_56();
                }
                return false;
            }
            return false;
        }
        return false;
    }

    private boolean jj_3R_53() {
        Token xsp = this.jj_scanpos;
        if (jj_scan_token(27)) {
            this.jj_scanpos = xsp;
            return jj_scan_token(28);
        }
        return false;
    }

    private boolean jj_3R_97() {
        Token xsp;
        if (jj_scan_token(56)) {
            return true;
        }
        Token xsp2 = this.jj_scanpos;
        if (jj_3R_102()) {
            this.jj_scanpos = xsp2;
        }
        if (jj_3R_103()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!jj_3R_103());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_44() {
        Token xsp;
        if (jj_3R_47()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!jj_3R_48());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_50() {
        Token xsp = this.jj_scanpos;
        if (jj_scan_token(35)) {
            this.jj_scanpos = xsp;
            return jj_scan_token(36);
        }
        return false;
    }

    private boolean jj_3R_45() {
        Token xsp = this.jj_scanpos;
        if (jj_3R_49()) {
            this.jj_scanpos = xsp;
            return jj_3R_50();
        }
        return false;
    }

    private boolean jj_3R_49() {
        Token xsp = this.jj_scanpos;
        if (jj_scan_token(33)) {
            this.jj_scanpos = xsp;
            return jj_scan_token(34);
        }
        return false;
    }

    private boolean jj_3R_28() {
        return jj_3R_22();
    }

    private boolean jj_3R_38() {
        return jj_scan_token(56);
    }

    static {
        jj_la1_init_0();
        jj_la1_init_1();
    }

    private static void jj_la1_init_0() {
        jj_la1_0 = new int[]{14, 14, SSL.SSL_OP_TLS_ROLLBACK_BUG, 1436928, 1436928, 16777216, 0, 262144, 1436928, 262144, 0, 0, 0, 0, 0, 0, 0, 0, -33554432, 402653184, 100663296, Integer.MIN_VALUE, 1610612736, -33554432, 0, 0, 0, 0, 0, 0, 0, 0, 1436928, 1179648, 1436928, 1179648, 262144, 16777216, 1436928, 262144, 0, 1048832, 16777216, 1436928, 16777216, 1436928, 16777216, 1436928, 4194304, 262144, 125952, 49152};
    }

    private static void jj_la1_init_1() {
        jj_la1_1 = new int[]{0, 0, 0, 16812128, 16812128, 0, 16777216, 16777216, 16812128, 0, 1536, 1536, 384, 384, 30, 6, 24, 30, 1, 0, 0, 1, 0, 1, 2097152, 49152, 49152, 1974272, Opcodes.ASM6, 1572864, 1974272, 96, 16812128, 0, 16777216, 0, 0, 0, 16812128, 0, 16777216, 0, 0, 16812128, 0, 16812128, 0, 16812128, 0, 0, 0, 0};
    }

    public ELParser(InputStream stream) {
        this(stream, null);
    }

    public ELParser(InputStream stream, String encoding) {
        this.jjtree = new JJTELParserState();
        this.jj_la1 = new int[52];
        this.jj_2_rtns = new JJCalls[8];
        this.jj_rescan = false;
        this.jj_gc = 0;
        this.jj_ls = new LookaheadSuccess();
        this.jj_expentries = new ArrayList();
        this.jj_kind = -1;
        this.jj_lasttokens = new int[100];
        try {
            this.jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1);
            this.token_source = new ELParserTokenManager(this.jj_input_stream);
            this.token = new Token();
            this.jj_ntk = -1;
            this.jj_gen = 0;
            for (int i = 0; i < 52; i++) {
                this.jj_la1[i] = -1;
            }
            for (int i2 = 0; i2 < this.jj_2_rtns.length; i2++) {
                this.jj_2_rtns[i2] = new JJCalls();
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public void ReInit(InputStream stream) {
        ReInit(stream, null);
    }

    public void ReInit(InputStream stream, String encoding) {
        try {
            this.jj_input_stream.ReInit(stream, encoding, 1, 1);
            this.token_source.ReInit(this.jj_input_stream);
            this.token = new Token();
            this.jj_ntk = -1;
            this.jjtree.reset();
            this.jj_gen = 0;
            for (int i = 0; i < 52; i++) {
                this.jj_la1[i] = -1;
            }
            for (int i2 = 0; i2 < this.jj_2_rtns.length; i2++) {
                this.jj_2_rtns[i2] = new JJCalls();
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public ELParser(Reader stream) {
        this.jjtree = new JJTELParserState();
        this.jj_la1 = new int[52];
        this.jj_2_rtns = new JJCalls[8];
        this.jj_rescan = false;
        this.jj_gc = 0;
        this.jj_ls = new LookaheadSuccess();
        this.jj_expentries = new ArrayList();
        this.jj_kind = -1;
        this.jj_lasttokens = new int[100];
        this.jj_input_stream = new SimpleCharStream(stream, 1, 1);
        this.token_source = new ELParserTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 52; i++) {
            this.jj_la1[i] = -1;
        }
        for (int i2 = 0; i2 < this.jj_2_rtns.length; i2++) {
            this.jj_2_rtns[i2] = new JJCalls();
        }
    }

    public void ReInit(Reader stream) {
        this.jj_input_stream.ReInit(stream, 1, 1);
        this.token_source.ReInit(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jjtree.reset();
        this.jj_gen = 0;
        for (int i = 0; i < 52; i++) {
            this.jj_la1[i] = -1;
        }
        for (int i2 = 0; i2 < this.jj_2_rtns.length; i2++) {
            this.jj_2_rtns[i2] = new JJCalls();
        }
    }

    public ELParser(ELParserTokenManager tm) {
        this.jjtree = new JJTELParserState();
        this.jj_la1 = new int[52];
        this.jj_2_rtns = new JJCalls[8];
        this.jj_rescan = false;
        this.jj_gc = 0;
        this.jj_ls = new LookaheadSuccess();
        this.jj_expentries = new ArrayList();
        this.jj_kind = -1;
        this.jj_lasttokens = new int[100];
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 52; i++) {
            this.jj_la1[i] = -1;
        }
        for (int i2 = 0; i2 < this.jj_2_rtns.length; i2++) {
            this.jj_2_rtns[i2] = new JJCalls();
        }
    }

    public void ReInit(ELParserTokenManager tm) {
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jjtree.reset();
        this.jj_gen = 0;
        for (int i = 0; i < 52; i++) {
            this.jj_la1[i] = -1;
        }
        for (int i2 = 0; i2 < this.jj_2_rtns.length; i2++) {
            this.jj_2_rtns[i2] = new JJCalls();
        }
    }

    private Token jj_consume_token(int kind) throws ParseException {
        Token oldToken = this.token;
        if (oldToken.next != null) {
            this.token = this.token.next;
        } else {
            Token token = this.token;
            Token nextToken = this.token_source.getNextToken();
            token.next = nextToken;
            this.token = nextToken;
        }
        this.jj_ntk = -1;
        if (this.token.kind == kind) {
            this.jj_gen++;
            int i = this.jj_gc + 1;
            this.jj_gc = i;
            if (i > 100) {
                this.jj_gc = 0;
                for (int i2 = 0; i2 < this.jj_2_rtns.length; i2++) {
                    JJCalls jJCalls = this.jj_2_rtns[i2];
                    while (true) {
                        JJCalls c = jJCalls;
                        if (c != null) {
                            if (c.gen < this.jj_gen) {
                                c.first = null;
                            }
                            jJCalls = c.next;
                        }
                    }
                }
            }
            return this.token;
        }
        this.token = oldToken;
        this.jj_kind = kind;
        throw generateParseException();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:org/apache/el/parser/ELParser$LookaheadSuccess.class */
    public static final class LookaheadSuccess extends Error {
        private LookaheadSuccess() {
        }

        @Override // java.lang.Throwable
        public synchronized Throwable fillInStackTrace() {
            return this;
        }
    }

    private boolean jj_scan_token(int kind) {
        Token tok;
        if (this.jj_scanpos == this.jj_lastpos) {
            this.jj_la--;
            if (this.jj_scanpos.next == null) {
                Token token = this.jj_scanpos;
                Token nextToken = this.token_source.getNextToken();
                token.next = nextToken;
                this.jj_scanpos = nextToken;
                this.jj_lastpos = nextToken;
            } else {
                Token token2 = this.jj_scanpos.next;
                this.jj_scanpos = token2;
                this.jj_lastpos = token2;
            }
        } else {
            this.jj_scanpos = this.jj_scanpos.next;
        }
        if (this.jj_rescan) {
            int i = 0;
            Token token3 = this.token;
            while (true) {
                tok = token3;
                if (tok == null || tok == this.jj_scanpos) {
                    break;
                }
                i++;
                token3 = tok.next;
            }
            if (tok != null) {
                jj_add_error_token(kind, i);
            }
        }
        if (this.jj_scanpos.kind != kind) {
            return true;
        }
        if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
            throw this.jj_ls;
        }
        return false;
    }

    public final Token getNextToken() {
        if (this.token.next != null) {
            this.token = this.token.next;
        } else {
            Token token = this.token;
            Token nextToken = this.token_source.getNextToken();
            token.next = nextToken;
            this.token = nextToken;
        }
        this.jj_ntk = -1;
        this.jj_gen++;
        return this.token;
    }

    public final Token getToken(int index) {
        Token token;
        Token t = this.token;
        for (int i = 0; i < index; i++) {
            if (t.next != null) {
                token = t.next;
            } else {
                Token nextToken = this.token_source.getNextToken();
                token = nextToken;
                t.next = nextToken;
            }
            t = token;
        }
        return t;
    }

    private int jj_ntk() {
        Token token = this.token.next;
        this.jj_nt = token;
        if (token == null) {
            Token token2 = this.token;
            Token nextToken = this.token_source.getNextToken();
            token2.next = nextToken;
            int i = nextToken.kind;
            this.jj_ntk = i;
            return i;
        }
        int i2 = this.jj_nt.kind;
        this.jj_ntk = i2;
        return i2;
    }

    private void jj_add_error_token(int kind, int pos) {
        if (pos >= 100) {
            return;
        }
        if (pos == this.jj_endpos + 1) {
            int[] iArr = this.jj_lasttokens;
            int i = this.jj_endpos;
            this.jj_endpos = i + 1;
            iArr[i] = kind;
        } else if (this.jj_endpos != 0) {
            this.jj_expentry = new int[this.jj_endpos];
            for (int i2 = 0; i2 < this.jj_endpos; i2++) {
                this.jj_expentry[i2] = this.jj_lasttokens[i2];
            }
            Iterator<int[]> it = this.jj_expentries.iterator();
            loop1: while (true) {
                if (!it.hasNext()) {
                    break;
                }
                int[] oldentry = it.next();
                if (oldentry.length == this.jj_expentry.length) {
                    for (int i3 = 0; i3 < this.jj_expentry.length; i3++) {
                        if (oldentry[i3] != this.jj_expentry[i3]) {
                            break;
                        }
                    }
                    this.jj_expentries.add(this.jj_expentry);
                    break loop1;
                }
            }
            if (pos != 0) {
                int[] iArr2 = this.jj_lasttokens;
                this.jj_endpos = pos;
                iArr2[pos - 1] = kind;
            }
        }
    }

    /* JADX WARN: Type inference failed for: r0v16, types: [int[], int[][]] */
    public ParseException generateParseException() {
        this.jj_expentries.clear();
        boolean[] la1tokens = new boolean[62];
        if (this.jj_kind >= 0) {
            la1tokens[this.jj_kind] = true;
            this.jj_kind = -1;
        }
        for (int i = 0; i < 52; i++) {
            if (this.jj_la1[i] == this.jj_gen) {
                for (int j = 0; j < 32; j++) {
                    if ((jj_la1_0[i] & (1 << j)) != 0) {
                        la1tokens[j] = true;
                    }
                    if ((jj_la1_1[i] & (1 << j)) != 0) {
                        la1tokens[32 + j] = true;
                    }
                }
            }
        }
        for (int i2 = 0; i2 < 62; i2++) {
            if (la1tokens[i2]) {
                this.jj_expentry = new int[1];
                this.jj_expentry[0] = i2;
                this.jj_expentries.add(this.jj_expentry);
            }
        }
        this.jj_endpos = 0;
        jj_rescan_token();
        jj_add_error_token(0, 0);
        ?? r0 = new int[this.jj_expentries.size()];
        for (int i3 = 0; i3 < this.jj_expentries.size(); i3++) {
            r0[i3] = this.jj_expentries.get(i3);
        }
        return new ParseException(this.token, r0, tokenImage);
    }

    public final void enable_tracing() {
    }

    public final void disable_tracing() {
    }

    private void jj_rescan_token() {
        this.jj_rescan = true;
        for (int i = 0; i < 8; i++) {
            try {
                JJCalls p = this.jj_2_rtns[i];
                do {
                    if (p.gen > this.jj_gen) {
                        this.jj_la = p.arg;
                        Token token = p.first;
                        this.jj_scanpos = token;
                        this.jj_lastpos = token;
                        switch (i) {
                            case 0:
                                jj_3_1();
                                break;
                            case 1:
                                jj_3_2();
                                break;
                            case 2:
                                jj_3_3();
                                break;
                            case 3:
                                jj_3_4();
                                break;
                            case 4:
                                jj_3_5();
                                break;
                            case 5:
                                jj_3_6();
                                break;
                            case 6:
                                jj_3_7();
                                break;
                            case 7:
                                jj_3_8();
                                break;
                        }
                    }
                    p = p.next;
                } while (p != null);
            } catch (LookaheadSuccess e) {
            }
        }
        this.jj_rescan = false;
    }

    private void jj_save(int index, int xla) {
        JJCalls p;
        JJCalls jJCalls = this.jj_2_rtns[index];
        while (true) {
            p = jJCalls;
            if (p.gen <= this.jj_gen) {
                break;
            } else if (p.next == null) {
                JJCalls jJCalls2 = new JJCalls();
                p.next = jJCalls2;
                p = jJCalls2;
                break;
            } else {
                jJCalls = p.next;
            }
        }
        p.gen = (this.jj_gen + xla) - this.jj_la;
        p.first = this.token;
        p.arg = xla;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:org/apache/el/parser/ELParser$JJCalls.class */
    public static final class JJCalls {
        int gen;
        Token first;
        int arg;
        JJCalls next;

        JJCalls() {
        }
    }
}