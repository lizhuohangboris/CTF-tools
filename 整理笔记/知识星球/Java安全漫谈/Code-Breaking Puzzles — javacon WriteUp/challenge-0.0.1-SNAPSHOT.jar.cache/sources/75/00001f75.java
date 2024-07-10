package org.springframework.expression.spel.standard;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.TypedValue;
import org.springframework.expression.common.ExpressionUtils;
import org.springframework.expression.spel.CompiledExpression;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.spel.SpelNode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.ast.SpelNodeImpl;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/standard/SpelExpression.class */
public class SpelExpression implements Expression {
    private static final int INTERPRETED_COUNT_THRESHOLD = 100;
    private static final int FAILED_ATTEMPTS_THRESHOLD = 100;
    private final String expression;
    private final SpelNodeImpl ast;
    private final SpelParserConfiguration configuration;
    @Nullable
    private EvaluationContext evaluationContext;
    @Nullable
    private CompiledExpression compiledAst;
    private volatile int interpretedCount = 0;
    private volatile int failedAttempts = 0;

    public SpelExpression(String expression, SpelNodeImpl ast, SpelParserConfiguration configuration) {
        this.expression = expression;
        this.ast = ast;
        this.configuration = configuration;
    }

    public void setEvaluationContext(EvaluationContext evaluationContext) {
        this.evaluationContext = evaluationContext;
    }

    public EvaluationContext getEvaluationContext() {
        if (this.evaluationContext == null) {
            this.evaluationContext = new StandardEvaluationContext();
        }
        return this.evaluationContext;
    }

    @Override // org.springframework.expression.Expression
    public String getExpressionString() {
        return this.expression;
    }

    @Override // org.springframework.expression.Expression
    @Nullable
    public Object getValue() throws EvaluationException {
        if (this.compiledAst != null) {
            try {
                EvaluationContext context = getEvaluationContext();
                return this.compiledAst.getValue(context.getRootObject().getValue(), context);
            } catch (Throwable ex) {
                if (this.configuration.getCompilerMode() == SpelCompilerMode.MIXED) {
                    this.interpretedCount = 0;
                    this.compiledAst = null;
                } else {
                    throw new SpelEvaluationException(ex, SpelMessage.EXCEPTION_RUNNING_COMPILED_EXPRESSION, new Object[0]);
                }
            }
        }
        ExpressionState expressionState = new ExpressionState(getEvaluationContext(), this.configuration);
        Object result = this.ast.getValue(expressionState);
        checkCompile(expressionState);
        return result;
    }

    @Override // org.springframework.expression.Expression
    @Nullable
    public <T> T getValue(@Nullable Class<T> expectedResultType) throws EvaluationException {
        if (this.compiledAst != null) {
            try {
                EvaluationContext context = getEvaluationContext();
                T t = (T) this.compiledAst.getValue(context.getRootObject().getValue(), context);
                if (expectedResultType == null) {
                    return t;
                }
                return (T) ExpressionUtils.convertTypedValue(getEvaluationContext(), new TypedValue(t), expectedResultType);
            } catch (Throwable ex) {
                if (this.configuration.getCompilerMode() == SpelCompilerMode.MIXED) {
                    this.interpretedCount = 0;
                    this.compiledAst = null;
                } else {
                    throw new SpelEvaluationException(ex, SpelMessage.EXCEPTION_RUNNING_COMPILED_EXPRESSION, new Object[0]);
                }
            }
        }
        ExpressionState expressionState = new ExpressionState(getEvaluationContext(), this.configuration);
        TypedValue typedResultValue = this.ast.getTypedValue(expressionState);
        checkCompile(expressionState);
        return (T) ExpressionUtils.convertTypedValue(expressionState.getEvaluationContext(), typedResultValue, expectedResultType);
    }

    @Override // org.springframework.expression.Expression
    @Nullable
    public Object getValue(Object rootObject) throws EvaluationException {
        if (this.compiledAst != null) {
            try {
                return this.compiledAst.getValue(rootObject, getEvaluationContext());
            } catch (Throwable ex) {
                if (this.configuration.getCompilerMode() == SpelCompilerMode.MIXED) {
                    this.interpretedCount = 0;
                    this.compiledAst = null;
                } else {
                    throw new SpelEvaluationException(ex, SpelMessage.EXCEPTION_RUNNING_COMPILED_EXPRESSION, new Object[0]);
                }
            }
        }
        ExpressionState expressionState = new ExpressionState(getEvaluationContext(), toTypedValue(rootObject), this.configuration);
        Object result = this.ast.getValue(expressionState);
        checkCompile(expressionState);
        return result;
    }

    @Override // org.springframework.expression.Expression
    @Nullable
    public <T> T getValue(Object rootObject, @Nullable Class<T> expectedResultType) throws EvaluationException {
        if (this.compiledAst != null) {
            try {
                T t = (T) this.compiledAst.getValue(rootObject, getEvaluationContext());
                if (expectedResultType == null) {
                    return t;
                }
                return (T) ExpressionUtils.convertTypedValue(getEvaluationContext(), new TypedValue(t), expectedResultType);
            } catch (Throwable ex) {
                if (this.configuration.getCompilerMode() == SpelCompilerMode.MIXED) {
                    this.interpretedCount = 0;
                    this.compiledAst = null;
                } else {
                    throw new SpelEvaluationException(ex, SpelMessage.EXCEPTION_RUNNING_COMPILED_EXPRESSION, new Object[0]);
                }
            }
        }
        ExpressionState expressionState = new ExpressionState(getEvaluationContext(), toTypedValue(rootObject), this.configuration);
        TypedValue typedResultValue = this.ast.getTypedValue(expressionState);
        checkCompile(expressionState);
        return (T) ExpressionUtils.convertTypedValue(expressionState.getEvaluationContext(), typedResultValue, expectedResultType);
    }

    @Override // org.springframework.expression.Expression
    @Nullable
    public Object getValue(EvaluationContext context) throws EvaluationException {
        Assert.notNull(context, "EvaluationContext is required");
        if (this.compiledAst != null) {
            try {
                return this.compiledAst.getValue(context.getRootObject().getValue(), context);
            } catch (Throwable ex) {
                if (this.configuration.getCompilerMode() == SpelCompilerMode.MIXED) {
                    this.interpretedCount = 0;
                    this.compiledAst = null;
                } else {
                    throw new SpelEvaluationException(ex, SpelMessage.EXCEPTION_RUNNING_COMPILED_EXPRESSION, new Object[0]);
                }
            }
        }
        ExpressionState expressionState = new ExpressionState(context, this.configuration);
        Object result = this.ast.getValue(expressionState);
        checkCompile(expressionState);
        return result;
    }

    @Override // org.springframework.expression.Expression
    @Nullable
    public <T> T getValue(EvaluationContext context, @Nullable Class<T> expectedResultType) throws EvaluationException {
        Assert.notNull(context, "EvaluationContext is required");
        if (this.compiledAst != null) {
            try {
                T t = (T) this.compiledAst.getValue(context.getRootObject().getValue(), context);
                if (expectedResultType != null) {
                    return (T) ExpressionUtils.convertTypedValue(context, new TypedValue(t), expectedResultType);
                }
                return t;
            } catch (Throwable ex) {
                if (this.configuration.getCompilerMode() == SpelCompilerMode.MIXED) {
                    this.interpretedCount = 0;
                    this.compiledAst = null;
                } else {
                    throw new SpelEvaluationException(ex, SpelMessage.EXCEPTION_RUNNING_COMPILED_EXPRESSION, new Object[0]);
                }
            }
        }
        ExpressionState expressionState = new ExpressionState(context, this.configuration);
        TypedValue typedResultValue = this.ast.getTypedValue(expressionState);
        checkCompile(expressionState);
        return (T) ExpressionUtils.convertTypedValue(context, typedResultValue, expectedResultType);
    }

    @Override // org.springframework.expression.Expression
    @Nullable
    public Object getValue(EvaluationContext context, Object rootObject) throws EvaluationException {
        Assert.notNull(context, "EvaluationContext is required");
        if (this.compiledAst != null) {
            try {
                return this.compiledAst.getValue(rootObject, context);
            } catch (Throwable ex) {
                if (this.configuration.getCompilerMode() == SpelCompilerMode.MIXED) {
                    this.interpretedCount = 0;
                    this.compiledAst = null;
                } else {
                    throw new SpelEvaluationException(ex, SpelMessage.EXCEPTION_RUNNING_COMPILED_EXPRESSION, new Object[0]);
                }
            }
        }
        ExpressionState expressionState = new ExpressionState(context, toTypedValue(rootObject), this.configuration);
        Object result = this.ast.getValue(expressionState);
        checkCompile(expressionState);
        return result;
    }

    @Override // org.springframework.expression.Expression
    @Nullable
    public <T> T getValue(EvaluationContext context, Object rootObject, @Nullable Class<T> expectedResultType) throws EvaluationException {
        Assert.notNull(context, "EvaluationContext is required");
        if (this.compiledAst != null) {
            try {
                T t = (T) this.compiledAst.getValue(rootObject, context);
                if (expectedResultType != null) {
                    return (T) ExpressionUtils.convertTypedValue(context, new TypedValue(t), expectedResultType);
                }
                return t;
            } catch (Throwable ex) {
                if (this.configuration.getCompilerMode() == SpelCompilerMode.MIXED) {
                    this.interpretedCount = 0;
                    this.compiledAst = null;
                } else {
                    throw new SpelEvaluationException(ex, SpelMessage.EXCEPTION_RUNNING_COMPILED_EXPRESSION, new Object[0]);
                }
            }
        }
        ExpressionState expressionState = new ExpressionState(context, toTypedValue(rootObject), this.configuration);
        TypedValue typedResultValue = this.ast.getTypedValue(expressionState);
        checkCompile(expressionState);
        return (T) ExpressionUtils.convertTypedValue(context, typedResultValue, expectedResultType);
    }

    @Override // org.springframework.expression.Expression
    @Nullable
    public Class<?> getValueType() throws EvaluationException {
        return getValueType(getEvaluationContext());
    }

    @Override // org.springframework.expression.Expression
    @Nullable
    public Class<?> getValueType(Object rootObject) throws EvaluationException {
        return getValueType(getEvaluationContext(), rootObject);
    }

    @Override // org.springframework.expression.Expression
    @Nullable
    public Class<?> getValueType(EvaluationContext context) throws EvaluationException {
        Assert.notNull(context, "EvaluationContext is required");
        ExpressionState expressionState = new ExpressionState(context, this.configuration);
        TypeDescriptor typeDescriptor = this.ast.getValueInternal(expressionState).getTypeDescriptor();
        if (typeDescriptor != null) {
            return typeDescriptor.getType();
        }
        return null;
    }

    @Override // org.springframework.expression.Expression
    @Nullable
    public Class<?> getValueType(EvaluationContext context, Object rootObject) throws EvaluationException {
        ExpressionState expressionState = new ExpressionState(context, toTypedValue(rootObject), this.configuration);
        TypeDescriptor typeDescriptor = this.ast.getValueInternal(expressionState).getTypeDescriptor();
        if (typeDescriptor != null) {
            return typeDescriptor.getType();
        }
        return null;
    }

    @Override // org.springframework.expression.Expression
    @Nullable
    public TypeDescriptor getValueTypeDescriptor() throws EvaluationException {
        return getValueTypeDescriptor(getEvaluationContext());
    }

    @Override // org.springframework.expression.Expression
    @Nullable
    public TypeDescriptor getValueTypeDescriptor(Object rootObject) throws EvaluationException {
        ExpressionState expressionState = new ExpressionState(getEvaluationContext(), toTypedValue(rootObject), this.configuration);
        return this.ast.getValueInternal(expressionState).getTypeDescriptor();
    }

    @Override // org.springframework.expression.Expression
    @Nullable
    public TypeDescriptor getValueTypeDescriptor(EvaluationContext context) throws EvaluationException {
        Assert.notNull(context, "EvaluationContext is required");
        ExpressionState expressionState = new ExpressionState(context, this.configuration);
        return this.ast.getValueInternal(expressionState).getTypeDescriptor();
    }

    @Override // org.springframework.expression.Expression
    @Nullable
    public TypeDescriptor getValueTypeDescriptor(EvaluationContext context, Object rootObject) throws EvaluationException {
        Assert.notNull(context, "EvaluationContext is required");
        ExpressionState expressionState = new ExpressionState(context, toTypedValue(rootObject), this.configuration);
        return this.ast.getValueInternal(expressionState).getTypeDescriptor();
    }

    @Override // org.springframework.expression.Expression
    public boolean isWritable(Object rootObject) throws EvaluationException {
        return this.ast.isWritable(new ExpressionState(getEvaluationContext(), toTypedValue(rootObject), this.configuration));
    }

    @Override // org.springframework.expression.Expression
    public boolean isWritable(EvaluationContext context) throws EvaluationException {
        Assert.notNull(context, "EvaluationContext is required");
        return this.ast.isWritable(new ExpressionState(context, this.configuration));
    }

    @Override // org.springframework.expression.Expression
    public boolean isWritable(EvaluationContext context, Object rootObject) throws EvaluationException {
        Assert.notNull(context, "EvaluationContext is required");
        return this.ast.isWritable(new ExpressionState(context, toTypedValue(rootObject), this.configuration));
    }

    @Override // org.springframework.expression.Expression
    public void setValue(Object rootObject, @Nullable Object value) throws EvaluationException {
        this.ast.setValue(new ExpressionState(getEvaluationContext(), toTypedValue(rootObject), this.configuration), value);
    }

    @Override // org.springframework.expression.Expression
    public void setValue(EvaluationContext context, @Nullable Object value) throws EvaluationException {
        Assert.notNull(context, "EvaluationContext is required");
        this.ast.setValue(new ExpressionState(context, this.configuration), value);
    }

    @Override // org.springframework.expression.Expression
    public void setValue(EvaluationContext context, Object rootObject, @Nullable Object value) throws EvaluationException {
        Assert.notNull(context, "EvaluationContext is required");
        this.ast.setValue(new ExpressionState(context, toTypedValue(rootObject), this.configuration), value);
    }

    private void checkCompile(ExpressionState expressionState) {
        this.interpretedCount++;
        SpelCompilerMode compilerMode = expressionState.getConfiguration().getCompilerMode();
        if (compilerMode != SpelCompilerMode.OFF) {
            if (compilerMode == SpelCompilerMode.IMMEDIATE) {
                if (this.interpretedCount > 1) {
                    compileExpression();
                }
            } else if (this.interpretedCount > 100) {
                compileExpression();
            }
        }
    }

    public boolean compileExpression() {
        if (this.failedAttempts > 100) {
            return false;
        }
        if (this.compiledAst == null) {
            synchronized (this.expression) {
                if (this.compiledAst != null) {
                    return true;
                }
                SpelCompiler compiler = SpelCompiler.getCompiler(this.configuration.getCompilerClassLoader());
                this.compiledAst = compiler.compile(this.ast);
                if (this.compiledAst == null) {
                    this.failedAttempts++;
                }
            }
        }
        return this.compiledAst != null;
    }

    public void revertToInterpreted() {
        this.compiledAst = null;
        this.interpretedCount = 0;
        this.failedAttempts = 0;
    }

    public SpelNode getAST() {
        return this.ast;
    }

    public String toStringAST() {
        return this.ast.toStringAST();
    }

    private TypedValue toTypedValue(@Nullable Object object) {
        return object != null ? new TypedValue(object) : TypedValue.NULL;
    }
}