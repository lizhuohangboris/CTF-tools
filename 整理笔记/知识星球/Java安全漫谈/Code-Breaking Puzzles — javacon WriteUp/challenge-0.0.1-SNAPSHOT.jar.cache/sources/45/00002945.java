package org.thymeleaf.standard.expression;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/StandardExpressionExecutionContext.class */
public final class StandardExpressionExecutionContext {
    public static final StandardExpressionExecutionContext RESTRICTED = new StandardExpressionExecutionContext(true, false, false);
    public static final StandardExpressionExecutionContext RESTRICTED_FORBID_UNSAFE_EXP_RESULTS = new StandardExpressionExecutionContext(true, true, false);
    public static final StandardExpressionExecutionContext NORMAL = new StandardExpressionExecutionContext(false, false, false);
    private static final StandardExpressionExecutionContext RESTRICTED_WITH_TYPE_CONVERSION = new StandardExpressionExecutionContext(true, false, true);
    private static final StandardExpressionExecutionContext RESTRICTED_FORBID_UNSAFE_EXP_RESULTS_WITH_TYPE_CONVERSION = new StandardExpressionExecutionContext(true, true, true);
    private static final StandardExpressionExecutionContext NORMAL_WITH_TYPE_CONVERSION = new StandardExpressionExecutionContext(false, false, true);
    private final boolean restrictVariableAccess;
    private final boolean forbidUnsafeExpressionResults;
    private final boolean performTypeConversion;

    private StandardExpressionExecutionContext(boolean restrictVariableAccess, boolean forbidUnsafeExpressionResults, boolean performTypeConversion) {
        this.restrictVariableAccess = restrictVariableAccess;
        this.forbidUnsafeExpressionResults = forbidUnsafeExpressionResults;
        this.performTypeConversion = performTypeConversion;
    }

    public boolean getRestrictVariableAccess() {
        return this.restrictVariableAccess;
    }

    public boolean getForbidUnsafeExpressionResults() {
        return this.forbidUnsafeExpressionResults;
    }

    public boolean getPerformTypeConversion() {
        return this.performTypeConversion;
    }

    public StandardExpressionExecutionContext withoutTypeConversion() {
        if (!getPerformTypeConversion()) {
            return this;
        }
        if (this == NORMAL_WITH_TYPE_CONVERSION) {
            return NORMAL;
        }
        if (this == RESTRICTED_WITH_TYPE_CONVERSION) {
            return RESTRICTED;
        }
        if (this == RESTRICTED_FORBID_UNSAFE_EXP_RESULTS_WITH_TYPE_CONVERSION) {
            return RESTRICTED_FORBID_UNSAFE_EXP_RESULTS;
        }
        return new StandardExpressionExecutionContext(getRestrictVariableAccess(), getForbidUnsafeExpressionResults(), false);
    }

    public StandardExpressionExecutionContext withTypeConversion() {
        if (getPerformTypeConversion()) {
            return this;
        }
        if (this == NORMAL) {
            return NORMAL_WITH_TYPE_CONVERSION;
        }
        if (this == RESTRICTED) {
            return RESTRICTED_WITH_TYPE_CONVERSION;
        }
        if (this == RESTRICTED_FORBID_UNSAFE_EXP_RESULTS) {
            return RESTRICTED_FORBID_UNSAFE_EXP_RESULTS_WITH_TYPE_CONVERSION;
        }
        return new StandardExpressionExecutionContext(getRestrictVariableAccess(), getForbidUnsafeExpressionResults(), true);
    }
}