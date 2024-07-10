package org.springframework.expression.spel.standard;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.expression.ParseException;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateAwareExpressionParser;
import org.springframework.expression.spel.InternalParseException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.spel.SpelParseException;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.ast.Assign;
import org.springframework.expression.spel.ast.BeanReference;
import org.springframework.expression.spel.ast.BooleanLiteral;
import org.springframework.expression.spel.ast.CompoundExpression;
import org.springframework.expression.spel.ast.ConstructorReference;
import org.springframework.expression.spel.ast.Elvis;
import org.springframework.expression.spel.ast.FunctionReference;
import org.springframework.expression.spel.ast.Identifier;
import org.springframework.expression.spel.ast.Indexer;
import org.springframework.expression.spel.ast.InlineList;
import org.springframework.expression.spel.ast.InlineMap;
import org.springframework.expression.spel.ast.Literal;
import org.springframework.expression.spel.ast.MethodReference;
import org.springframework.expression.spel.ast.NullLiteral;
import org.springframework.expression.spel.ast.OpAnd;
import org.springframework.expression.spel.ast.OpDec;
import org.springframework.expression.spel.ast.OpDivide;
import org.springframework.expression.spel.ast.OpEQ;
import org.springframework.expression.spel.ast.OpGE;
import org.springframework.expression.spel.ast.OpGT;
import org.springframework.expression.spel.ast.OpInc;
import org.springframework.expression.spel.ast.OpLE;
import org.springframework.expression.spel.ast.OpLT;
import org.springframework.expression.spel.ast.OpMinus;
import org.springframework.expression.spel.ast.OpModulus;
import org.springframework.expression.spel.ast.OpMultiply;
import org.springframework.expression.spel.ast.OpNE;
import org.springframework.expression.spel.ast.OpOr;
import org.springframework.expression.spel.ast.OpPlus;
import org.springframework.expression.spel.ast.OperatorBetween;
import org.springframework.expression.spel.ast.OperatorInstanceof;
import org.springframework.expression.spel.ast.OperatorMatches;
import org.springframework.expression.spel.ast.OperatorNot;
import org.springframework.expression.spel.ast.OperatorPower;
import org.springframework.expression.spel.ast.Projection;
import org.springframework.expression.spel.ast.PropertyOrFieldReference;
import org.springframework.expression.spel.ast.QualifiedIdentifier;
import org.springframework.expression.spel.ast.Selection;
import org.springframework.expression.spel.ast.SpelNodeImpl;
import org.springframework.expression.spel.ast.StringLiteral;
import org.springframework.expression.spel.ast.Ternary;
import org.springframework.expression.spel.ast.TypeReference;
import org.springframework.expression.spel.ast.VariableReference;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/standard/InternalSpelExpressionParser.class */
public class InternalSpelExpressionParser extends TemplateAwareExpressionParser {
    private static final Pattern VALID_QUALIFIED_ID_PATTERN = Pattern.compile("[\\p{L}\\p{N}_$]+");
    private final SpelParserConfiguration configuration;
    private final Deque<SpelNodeImpl> constructedNodes = new ArrayDeque();
    private String expressionString = "";
    private List<Token> tokenStream = Collections.emptyList();
    private int tokenStreamLength;
    private int tokenStreamPointer;

    public InternalSpelExpressionParser(SpelParserConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override // org.springframework.expression.common.TemplateAwareExpressionParser
    public SpelExpression doParseExpression(String expressionString, @Nullable ParserContext context) throws ParseException {
        try {
            this.expressionString = expressionString;
            Tokenizer tokenizer = new Tokenizer(expressionString);
            this.tokenStream = tokenizer.process();
            this.tokenStreamLength = this.tokenStream.size();
            this.tokenStreamPointer = 0;
            this.constructedNodes.clear();
            SpelNodeImpl ast = eatExpression();
            Assert.state(ast != null, "No node");
            Token t = peekToken();
            if (t != null) {
                throw new SpelParseException(t.startPos, SpelMessage.MORE_INPUT, toString(nextToken()));
            }
            Assert.isTrue(this.constructedNodes.isEmpty(), "At least one node expected");
            return new SpelExpression(expressionString, ast, this.configuration);
        } catch (InternalParseException ex) {
            throw ex.getCause();
        }
    }

    @Nullable
    private SpelNodeImpl eatExpression() {
        SpelNodeImpl expr = eatLogicalOrExpression();
        Token t = peekToken();
        if (t != null) {
            if (t.kind == TokenKind.ASSIGN) {
                if (expr == null) {
                    expr = new NullLiteral(toPos(t.startPos - 1, t.endPos - 1));
                }
                nextToken();
                SpelNodeImpl assignedValue = eatLogicalOrExpression();
                return new Assign(toPos(t), expr, assignedValue);
            } else if (t.kind == TokenKind.ELVIS) {
                if (expr == null) {
                    expr = new NullLiteral(toPos(t.startPos - 1, t.endPos - 2));
                }
                nextToken();
                SpelNodeImpl valueIfNull = eatExpression();
                if (valueIfNull == null) {
                    valueIfNull = new NullLiteral(toPos(t.startPos + 1, t.endPos + 1));
                }
                return new Elvis(toPos(t), expr, valueIfNull);
            } else if (t.kind == TokenKind.QMARK) {
                if (expr == null) {
                    expr = new NullLiteral(toPos(t.startPos - 1, t.endPos - 1));
                }
                nextToken();
                SpelNodeImpl ifTrueExprValue = eatExpression();
                eatToken(TokenKind.COLON);
                SpelNodeImpl ifFalseExprValue = eatExpression();
                return new Ternary(toPos(t), expr, ifTrueExprValue, ifFalseExprValue);
            }
        }
        return expr;
    }

    @Nullable
    private SpelNodeImpl eatLogicalOrExpression() {
        SpelNodeImpl eatLogicalAndExpression = eatLogicalAndExpression();
        while (true) {
            SpelNodeImpl expr = eatLogicalAndExpression;
            if (peekIdentifierToken("or") || peekToken(TokenKind.SYMBOLIC_OR)) {
                Token t = takeToken();
                SpelNodeImpl rhExpr = eatLogicalAndExpression();
                checkOperands(t, expr, rhExpr);
                eatLogicalAndExpression = new OpOr(toPos(t), expr, rhExpr);
            } else {
                return expr;
            }
        }
    }

    @Nullable
    private SpelNodeImpl eatLogicalAndExpression() {
        SpelNodeImpl eatRelationalExpression = eatRelationalExpression();
        while (true) {
            SpelNodeImpl expr = eatRelationalExpression;
            if (peekIdentifierToken("and") || peekToken(TokenKind.SYMBOLIC_AND)) {
                Token t = takeToken();
                SpelNodeImpl rhExpr = eatRelationalExpression();
                checkOperands(t, expr, rhExpr);
                eatRelationalExpression = new OpAnd(toPos(t), expr, rhExpr);
            } else {
                return expr;
            }
        }
    }

    @Nullable
    private SpelNodeImpl eatRelationalExpression() {
        SpelNodeImpl expr = eatSumExpression();
        Token relationalOperatorToken = maybeEatRelationalOperator();
        if (relationalOperatorToken != null) {
            Token t = takeToken();
            SpelNodeImpl rhExpr = eatSumExpression();
            checkOperands(t, expr, rhExpr);
            TokenKind tk = relationalOperatorToken.kind;
            if (relationalOperatorToken.isNumericRelationalOperator()) {
                int pos = toPos(t);
                if (tk == TokenKind.GT) {
                    return new OpGT(pos, expr, rhExpr);
                }
                if (tk == TokenKind.LT) {
                    return new OpLT(pos, expr, rhExpr);
                }
                if (tk == TokenKind.LE) {
                    return new OpLE(pos, expr, rhExpr);
                }
                if (tk == TokenKind.GE) {
                    return new OpGE(pos, expr, rhExpr);
                }
                if (tk == TokenKind.EQ) {
                    return new OpEQ(pos, expr, rhExpr);
                }
                Assert.isTrue(tk == TokenKind.NE, "Not-equals token expected");
                return new OpNE(pos, expr, rhExpr);
            } else if (tk == TokenKind.INSTANCEOF) {
                return new OperatorInstanceof(toPos(t), expr, rhExpr);
            } else {
                if (tk == TokenKind.MATCHES) {
                    return new OperatorMatches(toPos(t), expr, rhExpr);
                }
                Assert.isTrue(tk == TokenKind.BETWEEN, "Between token expected");
                return new OperatorBetween(toPos(t), expr, rhExpr);
            }
        }
        return expr;
    }

    @Nullable
    private SpelNodeImpl eatSumExpression() {
        SpelNodeImpl expr = eatProductExpression();
        while (peekToken(TokenKind.PLUS, TokenKind.MINUS, TokenKind.INC)) {
            Token t = takeToken();
            SpelNodeImpl rhExpr = eatProductExpression();
            checkRightOperand(t, rhExpr);
            if (t.kind == TokenKind.PLUS) {
                expr = new OpPlus(toPos(t), expr, rhExpr);
            } else if (t.kind == TokenKind.MINUS) {
                expr = new OpMinus(toPos(t), expr, rhExpr);
            }
        }
        return expr;
    }

    @Nullable
    private SpelNodeImpl eatProductExpression() {
        SpelNodeImpl eatPowerIncDecExpression = eatPowerIncDecExpression();
        while (true) {
            SpelNodeImpl expr = eatPowerIncDecExpression;
            if (peekToken(TokenKind.STAR, TokenKind.DIV, TokenKind.MOD)) {
                Token t = takeToken();
                SpelNodeImpl rhExpr = eatPowerIncDecExpression();
                checkOperands(t, expr, rhExpr);
                if (t.kind == TokenKind.STAR) {
                    eatPowerIncDecExpression = new OpMultiply(toPos(t), expr, rhExpr);
                } else if (t.kind == TokenKind.DIV) {
                    eatPowerIncDecExpression = new OpDivide(toPos(t), expr, rhExpr);
                } else {
                    Assert.isTrue(t.kind == TokenKind.MOD, "Mod token expected");
                    eatPowerIncDecExpression = new OpModulus(toPos(t), expr, rhExpr);
                }
            } else {
                return expr;
            }
        }
    }

    @Nullable
    private SpelNodeImpl eatPowerIncDecExpression() {
        SpelNodeImpl expr = eatUnaryExpression();
        if (peekToken(TokenKind.POWER)) {
            Token t = takeToken();
            SpelNodeImpl rhExpr = eatUnaryExpression();
            checkRightOperand(t, rhExpr);
            return new OperatorPower(toPos(t), expr, rhExpr);
        } else if (expr != null && peekToken(TokenKind.INC, TokenKind.DEC)) {
            Token t2 = takeToken();
            if (t2.getKind() == TokenKind.INC) {
                return new OpInc(toPos(t2), true, expr);
            }
            return new OpDec(toPos(t2), true, expr);
        } else {
            return expr;
        }
    }

    @Nullable
    private SpelNodeImpl eatUnaryExpression() {
        if (peekToken(TokenKind.PLUS, TokenKind.MINUS, TokenKind.NOT)) {
            Token t = takeToken();
            SpelNodeImpl expr = eatUnaryExpression();
            Assert.state(expr != null, "No node");
            if (t.kind == TokenKind.NOT) {
                return new OperatorNot(toPos(t), expr);
            }
            if (t.kind == TokenKind.PLUS) {
                return new OpPlus(toPos(t), expr);
            }
            Assert.isTrue(t.kind == TokenKind.MINUS, "Minus token expected");
            return new OpMinus(toPos(t), expr);
        } else if (peekToken(TokenKind.INC, TokenKind.DEC)) {
            Token t2 = takeToken();
            SpelNodeImpl expr2 = eatUnaryExpression();
            if (t2.getKind() == TokenKind.INC) {
                return new OpInc(toPos(t2), false, expr2);
            }
            return new OpDec(toPos(t2), false, expr2);
        } else {
            return eatPrimaryExpression();
        }
    }

    @Nullable
    private SpelNodeImpl eatPrimaryExpression() {
        SpelNodeImpl start = eatStartNode();
        List<SpelNodeImpl> nodes = null;
        SpelNodeImpl eatNode = eatNode();
        while (true) {
            SpelNodeImpl node = eatNode;
            if (node == null) {
                break;
            }
            if (nodes == null) {
                nodes = new ArrayList<>(4);
                nodes.add(start);
            }
            nodes.add(node);
            eatNode = eatNode();
        }
        if (start == null || nodes == null) {
            return start;
        }
        return new CompoundExpression(toPos(start.getStartPosition(), nodes.get(nodes.size() - 1).getEndPosition()), (SpelNodeImpl[]) nodes.toArray(new SpelNodeImpl[0]));
    }

    @Nullable
    private SpelNodeImpl eatNode() {
        return peekToken(TokenKind.DOT, TokenKind.SAFE_NAVI) ? eatDottedNode() : eatNonDottedNode();
    }

    @Nullable
    private SpelNodeImpl eatNonDottedNode() {
        if (peekToken(TokenKind.LSQUARE) && maybeEatIndexer()) {
            return pop();
        }
        return null;
    }

    private SpelNodeImpl eatDottedNode() {
        Token t = takeToken();
        boolean nullSafeNavigation = t.kind == TokenKind.SAFE_NAVI;
        if (maybeEatMethodOrProperty(nullSafeNavigation) || maybeEatFunctionOrVar() || maybeEatProjection(nullSafeNavigation) || maybeEatSelection(nullSafeNavigation)) {
            return pop();
        }
        if (peekToken() == null) {
            throw internalException(t.startPos, SpelMessage.OOD, new Object[0]);
        }
        throw internalException(t.startPos, SpelMessage.UNEXPECTED_DATA_AFTER_DOT, toString(peekToken()));
    }

    private boolean maybeEatFunctionOrVar() {
        if (!peekToken(TokenKind.HASH)) {
            return false;
        }
        Token t = takeToken();
        Token functionOrVariableName = eatToken(TokenKind.IDENTIFIER);
        SpelNodeImpl[] args = maybeEatMethodArgs();
        if (args == null) {
            push(new VariableReference(functionOrVariableName.stringValue(), toPos(t.startPos, functionOrVariableName.endPos)));
            return true;
        }
        push(new FunctionReference(functionOrVariableName.stringValue(), toPos(t.startPos, functionOrVariableName.endPos), args));
        return true;
    }

    @Nullable
    private SpelNodeImpl[] maybeEatMethodArgs() {
        if (!peekToken(TokenKind.LPAREN)) {
            return null;
        }
        List<SpelNodeImpl> args = new ArrayList<>();
        consumeArguments(args);
        eatToken(TokenKind.RPAREN);
        return (SpelNodeImpl[]) args.toArray(new SpelNodeImpl[0]);
    }

    private void eatConstructorArgs(List<SpelNodeImpl> accumulatedArguments) {
        if (!peekToken(TokenKind.LPAREN)) {
            throw new InternalParseException(new SpelParseException(this.expressionString, positionOf(peekToken()), SpelMessage.MISSING_CONSTRUCTOR_ARGS, new Object[0]));
        }
        consumeArguments(accumulatedArguments);
        eatToken(TokenKind.RPAREN);
    }

    private void consumeArguments(List<SpelNodeImpl> accumulatedArguments) {
        Token next;
        Token t = peekToken();
        Assert.state(t != null, "Expected token");
        int pos = t.startPos;
        do {
            nextToken();
            Token t2 = peekToken();
            if (t2 == null) {
                throw internalException(pos, SpelMessage.RUN_OUT_OF_ARGUMENTS, new Object[0]);
            }
            if (t2.kind != TokenKind.RPAREN) {
                accumulatedArguments.add(eatExpression());
            }
            next = peekToken();
            if (next == null) {
                break;
            }
        } while (next.kind == TokenKind.COMMA);
        if (next == null) {
            throw internalException(pos, SpelMessage.RUN_OUT_OF_ARGUMENTS, new Object[0]);
        }
    }

    private int positionOf(@Nullable Token t) {
        if (t == null) {
            return this.expressionString.length();
        }
        return t.startPos;
    }

    @Nullable
    private SpelNodeImpl eatStartNode() {
        if (maybeEatLiteral()) {
            return pop();
        }
        if (maybeEatParenExpression()) {
            return pop();
        }
        if (maybeEatTypeReference() || maybeEatNullReference() || maybeEatConstructorReference() || maybeEatMethodOrProperty(false) || maybeEatFunctionOrVar()) {
            return pop();
        }
        if (maybeEatBeanReference()) {
            return pop();
        }
        if (maybeEatProjection(false) || maybeEatSelection(false) || maybeEatIndexer()) {
            return pop();
        }
        if (maybeEatInlineListOrMap()) {
            return pop();
        }
        return null;
    }

    private boolean maybeEatBeanReference() {
        Token beanNameToken;
        String beanName;
        BeanReference beanReference;
        if (peekToken(TokenKind.BEAN_REF) || peekToken(TokenKind.FACTORY_BEAN_REF)) {
            Token beanRefToken = takeToken();
            if (peekToken(TokenKind.IDENTIFIER)) {
                beanNameToken = eatToken(TokenKind.IDENTIFIER);
                beanName = beanNameToken.stringValue();
            } else if (peekToken(TokenKind.LITERAL_STRING)) {
                beanNameToken = eatToken(TokenKind.LITERAL_STRING);
                String beanName2 = beanNameToken.stringValue();
                beanName = beanName2.substring(1, beanName2.length() - 1);
            } else {
                throw internalException(beanRefToken.startPos, SpelMessage.INVALID_BEAN_REFERENCE, new Object[0]);
            }
            if (beanRefToken.getKind() == TokenKind.FACTORY_BEAN_REF) {
                String beanNameString = String.valueOf(TokenKind.FACTORY_BEAN_REF.tokenChars) + beanName;
                beanReference = new BeanReference(toPos(beanRefToken.startPos, beanNameToken.endPos), beanNameString);
            } else {
                beanReference = new BeanReference(toPos(beanNameToken), beanName);
            }
            this.constructedNodes.push(beanReference);
            return true;
        }
        return false;
    }

    private boolean maybeEatTypeReference() {
        if (peekToken(TokenKind.IDENTIFIER)) {
            Token typeName = peekToken();
            Assert.state(typeName != null, "Expected token");
            if (!"T".equals(typeName.stringValue())) {
                return false;
            }
            Token t = takeToken();
            if (peekToken(TokenKind.RSQUARE)) {
                push(new PropertyOrFieldReference(false, t.stringValue(), toPos(t)));
                return true;
            }
            eatToken(TokenKind.LPAREN);
            SpelNodeImpl node = eatPossiblyQualifiedId();
            int dims = 0;
            while (peekToken(TokenKind.LSQUARE, true)) {
                eatToken(TokenKind.RSQUARE);
                dims++;
            }
            eatToken(TokenKind.RPAREN);
            this.constructedNodes.push(new TypeReference(toPos(typeName), node, dims));
            return true;
        }
        return false;
    }

    private boolean maybeEatNullReference() {
        if (peekToken(TokenKind.IDENTIFIER)) {
            Token nullToken = peekToken();
            Assert.state(nullToken != null, "Expected token");
            if (!BeanDefinitionParserDelegate.NULL_ELEMENT.equalsIgnoreCase(nullToken.stringValue())) {
                return false;
            }
            nextToken();
            this.constructedNodes.push(new NullLiteral(toPos(nullToken)));
            return true;
        }
        return false;
    }

    private boolean maybeEatProjection(boolean nullSafeNavigation) {
        Token t = peekToken();
        if (!peekToken(TokenKind.PROJECT, true)) {
            return false;
        }
        Assert.state(t != null, "No token");
        SpelNodeImpl expr = eatExpression();
        Assert.state(expr != null, "No node");
        eatToken(TokenKind.RSQUARE);
        this.constructedNodes.push(new Projection(nullSafeNavigation, toPos(t), expr));
        return true;
    }

    private boolean maybeEatInlineListOrMap() {
        SpelNodeImpl expr;
        Token t = peekToken();
        if (!peekToken(TokenKind.LCURLY, true)) {
            return false;
        }
        Assert.state(t != null, "No token");
        Token closingCurly = peekToken();
        if (peekToken(TokenKind.RCURLY, true)) {
            Assert.state(closingCurly != null, "No token");
            expr = new InlineList(toPos(t.startPos, closingCurly.endPos), new SpelNodeImpl[0]);
        } else if (peekToken(TokenKind.COLON, true)) {
            expr = new InlineMap(toPos(t.startPos, eatToken(TokenKind.RCURLY).endPos), new SpelNodeImpl[0]);
        } else {
            SpelNodeImpl firstExpression = eatExpression();
            if (peekToken(TokenKind.RCURLY)) {
                List<SpelNodeImpl> elements = new ArrayList<>();
                elements.add(firstExpression);
                expr = new InlineList(toPos(t.startPos, eatToken(TokenKind.RCURLY).endPos), (SpelNodeImpl[]) elements.toArray(new SpelNodeImpl[0]));
            } else if (peekToken(TokenKind.COMMA, true)) {
                List<SpelNodeImpl> elements2 = new ArrayList<>();
                elements2.add(firstExpression);
                do {
                    elements2.add(eatExpression());
                } while (peekToken(TokenKind.COMMA, true));
                expr = new InlineList(toPos(t.startPos, eatToken(TokenKind.RCURLY).endPos), (SpelNodeImpl[]) elements2.toArray(new SpelNodeImpl[0]));
            } else if (peekToken(TokenKind.COLON, true)) {
                List<SpelNodeImpl> elements3 = new ArrayList<>();
                elements3.add(firstExpression);
                elements3.add(eatExpression());
                while (peekToken(TokenKind.COMMA, true)) {
                    elements3.add(eatExpression());
                    eatToken(TokenKind.COLON);
                    elements3.add(eatExpression());
                }
                expr = new InlineMap(toPos(t.startPos, eatToken(TokenKind.RCURLY).endPos), (SpelNodeImpl[]) elements3.toArray(new SpelNodeImpl[0]));
            } else {
                throw internalException(t.startPos, SpelMessage.OOD, new Object[0]);
            }
        }
        this.constructedNodes.push(expr);
        return true;
    }

    private boolean maybeEatIndexer() {
        Token t = peekToken();
        if (!peekToken(TokenKind.LSQUARE, true)) {
            return false;
        }
        Assert.state(t != null, "No token");
        SpelNodeImpl expr = eatExpression();
        Assert.state(expr != null, "No node");
        eatToken(TokenKind.RSQUARE);
        this.constructedNodes.push(new Indexer(toPos(t), expr));
        return true;
    }

    private boolean maybeEatSelection(boolean nullSafeNavigation) {
        Token t = peekToken();
        if (!peekSelectToken()) {
            return false;
        }
        Assert.state(t != null, "No token");
        nextToken();
        SpelNodeImpl expr = eatExpression();
        if (expr == null) {
            throw internalException(toPos(t), SpelMessage.MISSING_SELECTION_EXPRESSION, new Object[0]);
        }
        eatToken(TokenKind.RSQUARE);
        if (t.kind == TokenKind.SELECT_FIRST) {
            this.constructedNodes.push(new Selection(nullSafeNavigation, 1, toPos(t), expr));
            return true;
        } else if (t.kind == TokenKind.SELECT_LAST) {
            this.constructedNodes.push(new Selection(nullSafeNavigation, 2, toPos(t), expr));
            return true;
        } else {
            this.constructedNodes.push(new Selection(nullSafeNavigation, 0, toPos(t), expr));
            return true;
        }
    }

    private SpelNodeImpl eatPossiblyQualifiedId() {
        Token node;
        Deque<SpelNodeImpl> qualifiedIdPieces = new ArrayDeque<>();
        Token peekToken = peekToken();
        while (true) {
            node = peekToken;
            if (!isValidQualifiedId(node)) {
                break;
            }
            nextToken();
            if (node.kind != TokenKind.DOT) {
                qualifiedIdPieces.add(new Identifier(node.stringValue(), toPos(node)));
            }
            peekToken = peekToken();
        }
        if (qualifiedIdPieces.isEmpty()) {
            if (node == null) {
                throw internalException(this.expressionString.length(), SpelMessage.OOD, new Object[0]);
            }
            throw internalException(node.startPos, SpelMessage.NOT_EXPECTED_TOKEN, "qualified ID", node.getKind().toString().toLowerCase());
        }
        int pos = toPos(qualifiedIdPieces.getFirst().getStartPosition(), qualifiedIdPieces.getLast().getEndPosition());
        return new QualifiedIdentifier(pos, (SpelNodeImpl[]) qualifiedIdPieces.toArray(new SpelNodeImpl[0]));
    }

    private boolean isValidQualifiedId(@Nullable Token node) {
        if (node == null || node.kind == TokenKind.LITERAL_STRING) {
            return false;
        }
        if (node.kind == TokenKind.DOT || node.kind == TokenKind.IDENTIFIER) {
            return true;
        }
        String value = node.stringValue();
        return StringUtils.hasLength(value) && VALID_QUALIFIED_ID_PATTERN.matcher(value).matches();
    }

    private boolean maybeEatMethodOrProperty(boolean nullSafeNavigation) {
        if (peekToken(TokenKind.IDENTIFIER)) {
            Token methodOrPropertyName = takeToken();
            SpelNodeImpl[] args = maybeEatMethodArgs();
            if (args == null) {
                push(new PropertyOrFieldReference(nullSafeNavigation, methodOrPropertyName.stringValue(), toPos(methodOrPropertyName)));
                return true;
            }
            push(new MethodReference(nullSafeNavigation, methodOrPropertyName.stringValue(), toPos(methodOrPropertyName), args));
            return true;
        }
        return false;
    }

    private boolean maybeEatConstructorReference() {
        if (peekIdentifierToken("new")) {
            Token newToken = takeToken();
            if (peekToken(TokenKind.RSQUARE)) {
                push(new PropertyOrFieldReference(false, newToken.stringValue(), toPos(newToken)));
                return true;
            }
            SpelNodeImpl possiblyQualifiedConstructorName = eatPossiblyQualifiedId();
            List<SpelNodeImpl> nodes = new ArrayList<>();
            nodes.add(possiblyQualifiedConstructorName);
            if (peekToken(TokenKind.LSQUARE)) {
                List<SpelNodeImpl> dimensions = new ArrayList<>();
                while (peekToken(TokenKind.LSQUARE, true)) {
                    if (!peekToken(TokenKind.RSQUARE)) {
                        dimensions.add(eatExpression());
                    } else {
                        dimensions.add(null);
                    }
                    eatToken(TokenKind.RSQUARE);
                }
                if (maybeEatInlineListOrMap()) {
                    nodes.add(pop());
                }
                push(new ConstructorReference(toPos(newToken), (SpelNodeImpl[]) dimensions.toArray(new SpelNodeImpl[0]), (SpelNodeImpl[]) nodes.toArray(new SpelNodeImpl[0])));
                return true;
            }
            eatConstructorArgs(nodes);
            push(new ConstructorReference(toPos(newToken), (SpelNodeImpl[]) nodes.toArray(new SpelNodeImpl[0])));
            return true;
        }
        return false;
    }

    private void push(SpelNodeImpl newNode) {
        this.constructedNodes.push(newNode);
    }

    private SpelNodeImpl pop() {
        return this.constructedNodes.pop();
    }

    private boolean maybeEatLiteral() {
        Token t = peekToken();
        if (t == null) {
            return false;
        }
        if (t.kind == TokenKind.LITERAL_INT) {
            push(Literal.getIntLiteral(t.stringValue(), toPos(t), 10));
        } else if (t.kind == TokenKind.LITERAL_LONG) {
            push(Literal.getLongLiteral(t.stringValue(), toPos(t), 10));
        } else if (t.kind == TokenKind.LITERAL_HEXINT) {
            push(Literal.getIntLiteral(t.stringValue(), toPos(t), 16));
        } else if (t.kind == TokenKind.LITERAL_HEXLONG) {
            push(Literal.getLongLiteral(t.stringValue(), toPos(t), 16));
        } else if (t.kind == TokenKind.LITERAL_REAL) {
            push(Literal.getRealLiteral(t.stringValue(), toPos(t), false));
        } else if (t.kind == TokenKind.LITERAL_REAL_FLOAT) {
            push(Literal.getRealLiteral(t.stringValue(), toPos(t), true));
        } else if (peekIdentifierToken("true")) {
            push(new BooleanLiteral(t.stringValue(), toPos(t), true));
        } else if (peekIdentifierToken("false")) {
            push(new BooleanLiteral(t.stringValue(), toPos(t), false));
        } else if (t.kind == TokenKind.LITERAL_STRING) {
            push(new StringLiteral(t.stringValue(), toPos(t), t.stringValue()));
        } else {
            return false;
        }
        nextToken();
        return true;
    }

    private boolean maybeEatParenExpression() {
        if (peekToken(TokenKind.LPAREN)) {
            nextToken();
            SpelNodeImpl expr = eatExpression();
            Assert.state(expr != null, "No node");
            eatToken(TokenKind.RPAREN);
            push(expr);
            return true;
        }
        return false;
    }

    @Nullable
    private Token maybeEatRelationalOperator() {
        Token t = peekToken();
        if (t == null) {
            return null;
        }
        if (t.isNumericRelationalOperator()) {
            return t;
        }
        if (t.isIdentifier()) {
            String idString = t.stringValue();
            if (idString.equalsIgnoreCase("instanceof")) {
                return t.asInstanceOfToken();
            }
            if (idString.equalsIgnoreCase("matches")) {
                return t.asMatchesToken();
            }
            if (idString.equalsIgnoreCase("between")) {
                return t.asBetweenToken();
            }
            return null;
        }
        return null;
    }

    private Token eatToken(TokenKind expectedKind) {
        Token t = nextToken();
        if (t == null) {
            int pos = this.expressionString.length();
            throw internalException(pos, SpelMessage.OOD, new Object[0]);
        } else if (t.kind != expectedKind) {
            throw internalException(t.startPos, SpelMessage.NOT_EXPECTED_TOKEN, expectedKind.toString().toLowerCase(), t.getKind().toString().toLowerCase());
        } else {
            return t;
        }
    }

    private boolean peekToken(TokenKind desiredTokenKind) {
        return peekToken(desiredTokenKind, false);
    }

    private boolean peekToken(TokenKind desiredTokenKind, boolean consumeIfMatched) {
        Token t = peekToken();
        if (t == null) {
            return false;
        }
        if (t.kind == desiredTokenKind) {
            if (consumeIfMatched) {
                this.tokenStreamPointer++;
                return true;
            }
            return true;
        } else if (desiredTokenKind == TokenKind.IDENTIFIER && t.kind.ordinal() >= TokenKind.DIV.ordinal() && t.kind.ordinal() <= TokenKind.NOT.ordinal() && t.data != null) {
            return true;
        } else {
            return false;
        }
    }

    private boolean peekToken(TokenKind possible1, TokenKind possible2) {
        Token t = peekToken();
        if (t == null) {
            return false;
        }
        return t.kind == possible1 || t.kind == possible2;
    }

    private boolean peekToken(TokenKind possible1, TokenKind possible2, TokenKind possible3) {
        Token t = peekToken();
        if (t == null) {
            return false;
        }
        return t.kind == possible1 || t.kind == possible2 || t.kind == possible3;
    }

    private boolean peekIdentifierToken(String identifierString) {
        Token t = peekToken();
        return t != null && t.kind == TokenKind.IDENTIFIER && identifierString.equalsIgnoreCase(t.stringValue());
    }

    private boolean peekSelectToken() {
        Token t = peekToken();
        if (t == null) {
            return false;
        }
        return t.kind == TokenKind.SELECT || t.kind == TokenKind.SELECT_FIRST || t.kind == TokenKind.SELECT_LAST;
    }

    private Token takeToken() {
        if (this.tokenStreamPointer >= this.tokenStreamLength) {
            throw new IllegalStateException("No token");
        }
        List<Token> list = this.tokenStream;
        int i = this.tokenStreamPointer;
        this.tokenStreamPointer = i + 1;
        return list.get(i);
    }

    @Nullable
    private Token nextToken() {
        if (this.tokenStreamPointer >= this.tokenStreamLength) {
            return null;
        }
        List<Token> list = this.tokenStream;
        int i = this.tokenStreamPointer;
        this.tokenStreamPointer = i + 1;
        return list.get(i);
    }

    @Nullable
    private Token peekToken() {
        if (this.tokenStreamPointer >= this.tokenStreamLength) {
            return null;
        }
        return this.tokenStream.get(this.tokenStreamPointer);
    }

    public String toString(@Nullable Token t) {
        if (t == null) {
            return "";
        }
        if (t.getKind().hasPayload()) {
            return t.stringValue();
        }
        return t.kind.toString().toLowerCase();
    }

    private void checkOperands(Token token, @Nullable SpelNodeImpl left, @Nullable SpelNodeImpl right) {
        checkLeftOperand(token, left);
        checkRightOperand(token, right);
    }

    private void checkLeftOperand(Token token, @Nullable SpelNodeImpl operandExpression) {
        if (operandExpression == null) {
            throw internalException(token.startPos, SpelMessage.LEFT_OPERAND_PROBLEM, new Object[0]);
        }
    }

    private void checkRightOperand(Token token, @Nullable SpelNodeImpl operandExpression) {
        if (operandExpression == null) {
            throw internalException(token.startPos, SpelMessage.RIGHT_OPERAND_PROBLEM, new Object[0]);
        }
    }

    private InternalParseException internalException(int pos, SpelMessage message, Object... inserts) {
        return new InternalParseException(new SpelParseException(this.expressionString, pos, message, inserts));
    }

    private int toPos(Token t) {
        return (t.startPos << 16) + t.endPos;
    }

    private int toPos(int start, int end) {
        return (start << 16) + end;
    }
}