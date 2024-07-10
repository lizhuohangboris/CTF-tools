package org.thymeleaf.standard.expression;

import java.util.ArrayList;
import org.thymeleaf.util.Validate;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/ExpressionParsingState.class */
public final class ExpressionParsingState extends ArrayList<ExpressionParsingNode> {
    private static final long serialVersionUID = 3972191269638891028L;

    public void addNode(String semiParsedString) {
        Validate.notNull(semiParsedString, "String cannot be null");
        add(new ExpressionParsingNode(semiParsedString));
    }

    public void addNode(Expression parsedExpression) {
        Validate.notNull(parsedExpression, "Expression cannot be null");
        add(new ExpressionParsingNode(parsedExpression));
    }

    public void insertNode(int pos, String semiParsedString) {
        Validate.notNull(semiParsedString, "String cannot be null");
        add(pos, new ExpressionParsingNode(semiParsedString));
    }

    public void insertNode(int pos, Expression parsedExpression) {
        Validate.notNull(parsedExpression, "Expression cannot be null");
        add(pos, new ExpressionParsingNode(parsedExpression));
    }

    public void setNode(int pos, String semiParsedString) {
        Validate.notNull(semiParsedString, "String cannot be null");
        set(pos, new ExpressionParsingNode(semiParsedString));
    }

    public void setNode(int pos, Expression parsedExpression) {
        Validate.notNull(parsedExpression, "Expression cannot be null");
        set(pos, new ExpressionParsingNode(parsedExpression));
    }

    public boolean hasStringRoot() {
        return hasStringAt(0);
    }

    public boolean hasExpressionRoot() {
        return hasExpressionAt(0);
    }

    public boolean hasStringAt(int pos) {
        return size() > pos && get(pos).isInput();
    }

    public boolean hasExpressionAt(int pos) {
        return size() > pos && get(pos).isExpression();
    }
}