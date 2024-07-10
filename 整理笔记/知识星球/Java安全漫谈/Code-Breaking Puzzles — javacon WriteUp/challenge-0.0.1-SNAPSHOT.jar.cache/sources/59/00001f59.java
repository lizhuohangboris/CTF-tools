package org.springframework.expression.spel.ast;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.spel.support.BooleanTypedValue;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/ast/OperatorMatches.class */
public class OperatorMatches extends Operator {
    private static final int PATTERN_ACCESS_THRESHOLD = 1000000;
    private final ConcurrentMap<String, Pattern> patternCache;

    public OperatorMatches(int pos, SpelNodeImpl... operands) {
        super("matches", pos, operands);
        this.patternCache = new ConcurrentHashMap();
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public BooleanTypedValue getValueInternal(ExpressionState state) throws EvaluationException {
        SpelNodeImpl leftOp = getLeftOperand();
        SpelNodeImpl rightOp = getRightOperand();
        String left = (String) leftOp.getValue(state, String.class);
        Object right = getRightOperand().getValue(state);
        if (left == null) {
            throw new SpelEvaluationException(leftOp.getStartPosition(), SpelMessage.INVALID_FIRST_OPERAND_FOR_MATCHES_OPERATOR, null);
        }
        if (!(right instanceof String)) {
            throw new SpelEvaluationException(rightOp.getStartPosition(), SpelMessage.INVALID_SECOND_OPERAND_FOR_MATCHES_OPERATOR, right);
        }
        try {
            String rightString = (String) right;
            Pattern pattern = this.patternCache.get(rightString);
            if (pattern == null) {
                pattern = Pattern.compile(rightString);
                this.patternCache.putIfAbsent(rightString, pattern);
            }
            Matcher matcher = pattern.matcher(new MatcherInput(left, new AccessCount()));
            return BooleanTypedValue.forValue(matcher.matches());
        } catch (IllegalStateException ex) {
            throw new SpelEvaluationException(rightOp.getStartPosition(), ex, SpelMessage.FLAWED_PATTERN, right);
        } catch (PatternSyntaxException ex2) {
            throw new SpelEvaluationException(rightOp.getStartPosition(), ex2, SpelMessage.INVALID_PATTERN, right);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/ast/OperatorMatches$AccessCount.class */
    public static class AccessCount {
        private int count;

        private AccessCount() {
        }

        public void check() throws IllegalStateException {
            int i = this.count;
            this.count = i + 1;
            if (i > OperatorMatches.PATTERN_ACCESS_THRESHOLD) {
                throw new IllegalStateException("Pattern access threshold exceeded");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/ast/OperatorMatches$MatcherInput.class */
    public static class MatcherInput implements CharSequence {
        private final CharSequence value;
        private AccessCount access;

        public MatcherInput(CharSequence value, AccessCount access) {
            this.value = value;
            this.access = access;
        }

        @Override // java.lang.CharSequence
        public char charAt(int index) {
            this.access.check();
            return this.value.charAt(index);
        }

        @Override // java.lang.CharSequence
        public CharSequence subSequence(int start, int end) {
            return new MatcherInput(this.value.subSequence(start, end), this.access);
        }

        @Override // java.lang.CharSequence
        public int length() {
            return this.value.length();
        }

        @Override // java.lang.CharSequence
        public String toString() {
            return this.value.toString();
        }
    }
}