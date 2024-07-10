package org.springframework.aop.aspectj;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.PointcutPrimitive;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.validation.DataBinder;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/aspectj/AspectJAdviceParameterNameDiscoverer.class */
public class AspectJAdviceParameterNameDiscoverer implements ParameterNameDiscoverer {
    private static final String THIS_JOIN_POINT = "thisJoinPoint";
    private static final String THIS_JOIN_POINT_STATIC_PART = "thisJoinPointStaticPart";
    private static final int STEP_JOIN_POINT_BINDING = 1;
    private static final int STEP_THROWING_BINDING = 2;
    private static final int STEP_ANNOTATION_BINDING = 3;
    private static final int STEP_RETURNING_BINDING = 4;
    private static final int STEP_PRIMITIVE_ARGS_BINDING = 5;
    private static final int STEP_THIS_TARGET_ARGS_BINDING = 6;
    private static final int STEP_REFERENCE_PCUT_BINDING = 7;
    private static final int STEP_FINISHED = 8;
    private static final Set<String> singleValuedAnnotationPcds = new HashSet();
    private static final Set<String> nonReferencePointcutTokens = new HashSet();
    @Nullable
    private String pointcutExpression;
    private boolean raiseExceptions;
    @Nullable
    private String returningName;
    @Nullable
    private String throwingName;
    private Class<?>[] argumentTypes = new Class[0];
    private String[] parameterNameBindings = new String[0];
    private int numberOfRemainingUnboundArguments;

    static {
        singleValuedAnnotationPcds.add("@this");
        singleValuedAnnotationPcds.add("@target");
        singleValuedAnnotationPcds.add("@within");
        singleValuedAnnotationPcds.add("@withincode");
        singleValuedAnnotationPcds.add("@annotation");
        Set<PointcutPrimitive> pointcutPrimitives = PointcutParser.getAllSupportedPointcutPrimitives();
        for (PointcutPrimitive primitive : pointcutPrimitives) {
            nonReferencePointcutTokens.add(primitive.getName());
        }
        nonReferencePointcutTokens.add("&&");
        nonReferencePointcutTokens.add("!");
        nonReferencePointcutTokens.add("||");
        nonReferencePointcutTokens.add("and");
        nonReferencePointcutTokens.add("or");
        nonReferencePointcutTokens.add("not");
    }

    public AspectJAdviceParameterNameDiscoverer(@Nullable String pointcutExpression) {
        this.pointcutExpression = pointcutExpression;
    }

    public void setRaiseExceptions(boolean raiseExceptions) {
        this.raiseExceptions = raiseExceptions;
    }

    public void setReturningName(@Nullable String returningName) {
        this.returningName = returningName;
    }

    public void setThrowingName(@Nullable String throwingName) {
        this.throwingName = throwingName;
    }

    @Override // org.springframework.core.ParameterNameDiscoverer
    @Nullable
    public String[] getParameterNames(Method method) {
        this.argumentTypes = method.getParameterTypes();
        this.numberOfRemainingUnboundArguments = this.argumentTypes.length;
        this.parameterNameBindings = new String[this.numberOfRemainingUnboundArguments];
        int minimumNumberUnboundArgs = 0;
        if (this.returningName != null) {
            minimumNumberUnboundArgs = 0 + 1;
        }
        if (this.throwingName != null) {
            minimumNumberUnboundArgs++;
        }
        if (this.numberOfRemainingUnboundArguments < minimumNumberUnboundArgs) {
            throw new IllegalStateException("Not enough arguments in method to satisfy binding of returning and throwing variables");
        }
        int algorithmicStep = 1;
        while (this.numberOfRemainingUnboundArguments > 0 && algorithmicStep < 8) {
            try {
                int i = algorithmicStep;
                algorithmicStep++;
                switch (i) {
                    case 1:
                        if (!maybeBindThisJoinPoint()) {
                            maybeBindThisJoinPointStaticPart();
                            break;
                        } else {
                            break;
                        }
                    case 2:
                        maybeBindThrowingVariable();
                        break;
                    case 3:
                        maybeBindAnnotationsFromPointcutExpression();
                        break;
                    case 4:
                        maybeBindReturningVariable();
                        break;
                    case 5:
                        maybeBindPrimitiveArgsFromPointcutExpression();
                        break;
                    case 6:
                        maybeBindThisOrTargetOrArgsFromPointcutExpression();
                        break;
                    case 7:
                        maybeBindReferencePointcutParameter();
                        break;
                    default:
                        throw new IllegalStateException("Unknown algorithmic step: " + (algorithmicStep - 1));
                }
            } catch (IllegalArgumentException ex) {
                if (this.raiseExceptions) {
                    throw ex;
                }
                return null;
            } catch (AmbiguousBindingException ambigEx) {
                if (this.raiseExceptions) {
                    throw ambigEx;
                }
                return null;
            }
        }
        if (this.numberOfRemainingUnboundArguments == 0) {
            return this.parameterNameBindings;
        }
        if (this.raiseExceptions) {
            throw new IllegalStateException("Failed to bind all argument names: " + this.numberOfRemainingUnboundArguments + " argument(s) could not be bound");
        }
        return null;
    }

    @Override // org.springframework.core.ParameterNameDiscoverer
    @Nullable
    public String[] getParameterNames(Constructor<?> ctor) {
        if (this.raiseExceptions) {
            throw new UnsupportedOperationException("An advice method can never be a constructor");
        }
        return null;
    }

    private void bindParameterName(int index, String name) {
        this.parameterNameBindings[index] = name;
        this.numberOfRemainingUnboundArguments--;
    }

    private boolean maybeBindThisJoinPoint() {
        if (this.argumentTypes[0] == JoinPoint.class || this.argumentTypes[0] == ProceedingJoinPoint.class) {
            bindParameterName(0, THIS_JOIN_POINT);
            return true;
        }
        return false;
    }

    private void maybeBindThisJoinPointStaticPart() {
        if (this.argumentTypes[0] == JoinPoint.StaticPart.class) {
            bindParameterName(0, THIS_JOIN_POINT_STATIC_PART);
        }
    }

    private void maybeBindThrowingVariable() {
        if (this.throwingName == null) {
            return;
        }
        int throwableIndex = -1;
        for (int i = 0; i < this.argumentTypes.length; i++) {
            if (isUnbound(i) && isSubtypeOf(Throwable.class, i)) {
                if (throwableIndex == -1) {
                    throwableIndex = i;
                } else {
                    throw new AmbiguousBindingException("Binding of throwing parameter '" + this.throwingName + "' is ambiguous: could be bound to argument " + throwableIndex + " or argument " + i);
                }
            }
        }
        if (throwableIndex == -1) {
            throw new IllegalStateException("Binding of throwing parameter '" + this.throwingName + "' could not be completed as no available arguments are a subtype of Throwable");
        }
        bindParameterName(throwableIndex, this.throwingName);
    }

    private void maybeBindReturningVariable() {
        if (this.numberOfRemainingUnboundArguments == 0) {
            throw new IllegalStateException("Algorithm assumes that there must be at least one unbound parameter on entry to this method");
        }
        if (this.returningName != null) {
            if (this.numberOfRemainingUnboundArguments > 1) {
                throw new AmbiguousBindingException("Binding of returning parameter '" + this.returningName + "' is ambiguous, there are " + this.numberOfRemainingUnboundArguments + " candidates.");
            }
            for (int i = 0; i < this.parameterNameBindings.length; i++) {
                if (this.parameterNameBindings[i] == null) {
                    bindParameterName(i, this.returningName);
                    return;
                }
            }
        }
    }

    private void maybeBindAnnotationsFromPointcutExpression() {
        List<String> varNames = new ArrayList<>();
        String[] tokens = StringUtils.tokenizeToStringArray(this.pointcutExpression, " ");
        int i = 0;
        while (i < tokens.length) {
            String toMatch = tokens[i];
            int firstParenIndex = toMatch.indexOf(40);
            if (firstParenIndex != -1) {
                toMatch = toMatch.substring(0, firstParenIndex);
            }
            if (singleValuedAnnotationPcds.contains(toMatch)) {
                PointcutBody body = getPointcutBody(tokens, i);
                i += body.numTokensConsumed;
                String varName = maybeExtractVariableName(body.text);
                if (varName != null) {
                    varNames.add(varName);
                }
            } else if (tokens[i].startsWith("@args(") || tokens[i].equals("@args")) {
                PointcutBody body2 = getPointcutBody(tokens, i);
                i += body2.numTokensConsumed;
                maybeExtractVariableNamesFromArgs(body2.text, varNames);
            }
            i++;
        }
        bindAnnotationsFromVarNames(varNames);
    }

    private void bindAnnotationsFromVarNames(List<String> varNames) {
        if (!varNames.isEmpty()) {
            int numAnnotationSlots = countNumberOfUnboundAnnotationArguments();
            if (numAnnotationSlots > 1) {
                throw new AmbiguousBindingException("Found " + varNames.size() + " potential annotation variable(s), and " + numAnnotationSlots + " potential argument slots");
            }
            if (numAnnotationSlots == 1) {
                if (varNames.size() == 1) {
                    findAndBind(Annotation.class, varNames.get(0));
                    return;
                }
                throw new IllegalArgumentException("Found " + varNames.size() + " candidate annotation binding variables but only one potential argument binding slot");
            }
        }
    }

    @Nullable
    private String maybeExtractVariableName(@Nullable String candidateToken) {
        if (StringUtils.hasLength(candidateToken) && Character.isJavaIdentifierStart(candidateToken.charAt(0)) && Character.isLowerCase(candidateToken.charAt(0))) {
            char[] tokenChars = candidateToken.toCharArray();
            for (char tokenChar : tokenChars) {
                if (!Character.isJavaIdentifierPart(tokenChar)) {
                    return null;
                }
            }
            return candidateToken;
        }
        return null;
    }

    private void maybeExtractVariableNamesFromArgs(@Nullable String argsSpec, List<String> varNames) {
        if (argsSpec == null) {
            return;
        }
        String[] tokens = StringUtils.tokenizeToStringArray(argsSpec, ",");
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = StringUtils.trimWhitespace(tokens[i]);
            String varName = maybeExtractVariableName(tokens[i]);
            if (varName != null) {
                varNames.add(varName);
            }
        }
    }

    private void maybeBindThisOrTargetOrArgsFromPointcutExpression() {
        if (this.numberOfRemainingUnboundArguments > 1) {
            throw new AmbiguousBindingException("Still " + this.numberOfRemainingUnboundArguments + " unbound args at this(),target(),args() binding stage, with no way to determine between them");
        }
        List<String> varNames = new ArrayList<>();
        String[] tokens = StringUtils.tokenizeToStringArray(this.pointcutExpression, " ");
        int i = 0;
        while (i < tokens.length) {
            if (tokens[i].equals("this") || tokens[i].startsWith("this(") || tokens[i].equals(DataBinder.DEFAULT_OBJECT_NAME) || tokens[i].startsWith("target(")) {
                PointcutBody body = getPointcutBody(tokens, i);
                i += body.numTokensConsumed;
                String varName = maybeExtractVariableName(body.text);
                if (varName != null) {
                    varNames.add(varName);
                }
            } else if (tokens[i].equals("args") || tokens[i].startsWith("args(")) {
                PointcutBody body2 = getPointcutBody(tokens, i);
                i += body2.numTokensConsumed;
                List<String> candidateVarNames = new ArrayList<>();
                maybeExtractVariableNamesFromArgs(body2.text, candidateVarNames);
                for (String varName2 : candidateVarNames) {
                    if (!alreadyBound(varName2)) {
                        varNames.add(varName2);
                    }
                }
            }
            i++;
        }
        if (varNames.size() > 1) {
            throw new AmbiguousBindingException("Found " + varNames.size() + " candidate this(), target() or args() variables but only one unbound argument slot");
        }
        if (varNames.size() == 1) {
            for (int j = 0; j < this.parameterNameBindings.length; j++) {
                if (isUnbound(j)) {
                    bindParameterName(j, varNames.get(0));
                    return;
                }
            }
        }
    }

    private void maybeBindReferencePointcutParameter() {
        String varName;
        if (this.numberOfRemainingUnboundArguments > 1) {
            throw new AmbiguousBindingException("Still " + this.numberOfRemainingUnboundArguments + " unbound args at reference pointcut binding stage, with no way to determine between them");
        }
        List<String> varNames = new ArrayList<>();
        String[] tokens = StringUtils.tokenizeToStringArray(this.pointcutExpression, " ");
        int i = 0;
        while (i < tokens.length) {
            String toMatch = tokens[i];
            if (toMatch.startsWith("!")) {
                toMatch = toMatch.substring(1);
            }
            int firstParenIndex = toMatch.indexOf(40);
            if (firstParenIndex != -1) {
                toMatch = toMatch.substring(0, firstParenIndex);
            } else {
                if (tokens.length >= i + 2) {
                    String nextToken = tokens[i + 1];
                    if (nextToken.charAt(0) != '(') {
                    }
                }
                i++;
            }
            PointcutBody body = getPointcutBody(tokens, i);
            i += body.numTokensConsumed;
            if (!nonReferencePointcutTokens.contains(toMatch) && (varName = maybeExtractVariableName(body.text)) != null) {
                varNames.add(varName);
            }
            i++;
        }
        if (varNames.size() > 1) {
            throw new AmbiguousBindingException("Found " + varNames.size() + " candidate reference pointcut variables but only one unbound argument slot");
        }
        if (varNames.size() == 1) {
            for (int j = 0; j < this.parameterNameBindings.length; j++) {
                if (isUnbound(j)) {
                    bindParameterName(j, varNames.get(0));
                    return;
                }
            }
        }
    }

    private PointcutBody getPointcutBody(String[] tokens, int startIndex) {
        String currentToken = tokens[startIndex];
        int bodyStart = currentToken.indexOf(40);
        if (currentToken.charAt(currentToken.length() - 1) == ')') {
            return new PointcutBody(0, currentToken.substring(bodyStart + 1, currentToken.length() - 1));
        }
        StringBuilder sb = new StringBuilder();
        if (bodyStart >= 0 && bodyStart != currentToken.length() - 1) {
            sb.append(currentToken.substring(bodyStart + 1));
            sb.append(" ");
        }
        int numTokensConsumed = 0 + 1;
        int currentIndex = startIndex + numTokensConsumed;
        while (currentIndex < tokens.length) {
            if (tokens[currentIndex].equals("(")) {
                currentIndex++;
            } else if (tokens[currentIndex].endsWith(")")) {
                sb.append(tokens[currentIndex].substring(0, tokens[currentIndex].length() - 1));
                return new PointcutBody(numTokensConsumed, sb.toString().trim());
            } else {
                String toAppend = tokens[currentIndex];
                if (toAppend.startsWith("(")) {
                    toAppend = toAppend.substring(1);
                }
                sb.append(toAppend);
                sb.append(" ");
                currentIndex++;
                numTokensConsumed++;
            }
        }
        return new PointcutBody(numTokensConsumed, null);
    }

    private void maybeBindPrimitiveArgsFromPointcutExpression() {
        int numUnboundPrimitives = countNumberOfUnboundPrimitiveArguments();
        if (numUnboundPrimitives > 1) {
            throw new AmbiguousBindingException("Found '" + numUnboundPrimitives + "' unbound primitive arguments with no way to distinguish between them.");
        }
        if (numUnboundPrimitives == 1) {
            List<String> varNames = new ArrayList<>();
            String[] tokens = StringUtils.tokenizeToStringArray(this.pointcutExpression, " ");
            int i = 0;
            while (i < tokens.length) {
                if (tokens[i].equals("args") || tokens[i].startsWith("args(")) {
                    PointcutBody body = getPointcutBody(tokens, i);
                    i += body.numTokensConsumed;
                    maybeExtractVariableNamesFromArgs(body.text, varNames);
                }
                i++;
            }
            if (varNames.size() > 1) {
                throw new AmbiguousBindingException("Found " + varNames.size() + " candidate variable names but only one candidate binding slot when matching primitive args");
            }
            if (varNames.size() == 1) {
                for (int i2 = 0; i2 < this.argumentTypes.length; i2++) {
                    if (isUnbound(i2) && this.argumentTypes[i2].isPrimitive()) {
                        bindParameterName(i2, varNames.get(0));
                        return;
                    }
                }
            }
        }
    }

    private boolean isUnbound(int i) {
        return this.parameterNameBindings[i] == null;
    }

    private boolean alreadyBound(String varName) {
        for (int i = 0; i < this.parameterNameBindings.length; i++) {
            if (!isUnbound(i) && varName.equals(this.parameterNameBindings[i])) {
                return true;
            }
        }
        return false;
    }

    private boolean isSubtypeOf(Class<?> supertype, int argumentNumber) {
        return supertype.isAssignableFrom(this.argumentTypes[argumentNumber]);
    }

    private int countNumberOfUnboundAnnotationArguments() {
        int count = 0;
        for (int i = 0; i < this.argumentTypes.length; i++) {
            if (isUnbound(i) && isSubtypeOf(Annotation.class, i)) {
                count++;
            }
        }
        return count;
    }

    private int countNumberOfUnboundPrimitiveArguments() {
        int count = 0;
        for (int i = 0; i < this.argumentTypes.length; i++) {
            if (isUnbound(i) && this.argumentTypes[i].isPrimitive()) {
                count++;
            }
        }
        return count;
    }

    private void findAndBind(Class<?> argumentType, String varName) {
        for (int i = 0; i < this.argumentTypes.length; i++) {
            if (isUnbound(i) && isSubtypeOf(argumentType, i)) {
                bindParameterName(i, varName);
                return;
            }
        }
        throw new IllegalStateException("Expected to find an unbound argument of type '" + argumentType.getName() + "'");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/aspectj/AspectJAdviceParameterNameDiscoverer$PointcutBody.class */
    public static class PointcutBody {
        private int numTokensConsumed;
        @Nullable
        private String text;

        public PointcutBody(int tokens, @Nullable String text) {
            this.numTokensConsumed = tokens;
            this.text = text;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/aspectj/AspectJAdviceParameterNameDiscoverer$AmbiguousBindingException.class */
    public static class AmbiguousBindingException extends RuntimeException {
        public AmbiguousBindingException(String msg) {
            super(msg);
        }
    }
}