package org.springframework.expression;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/Expression.class */
public interface Expression {
    String getExpressionString();

    @Nullable
    Object getValue() throws EvaluationException;

    @Nullable
    <T> T getValue(@Nullable Class<T> cls) throws EvaluationException;

    @Nullable
    Object getValue(Object obj) throws EvaluationException;

    @Nullable
    <T> T getValue(Object obj, @Nullable Class<T> cls) throws EvaluationException;

    @Nullable
    Object getValue(EvaluationContext evaluationContext) throws EvaluationException;

    @Nullable
    Object getValue(EvaluationContext evaluationContext, Object obj) throws EvaluationException;

    @Nullable
    <T> T getValue(EvaluationContext evaluationContext, @Nullable Class<T> cls) throws EvaluationException;

    @Nullable
    <T> T getValue(EvaluationContext evaluationContext, Object obj, @Nullable Class<T> cls) throws EvaluationException;

    @Nullable
    Class<?> getValueType() throws EvaluationException;

    @Nullable
    Class<?> getValueType(Object obj) throws EvaluationException;

    @Nullable
    Class<?> getValueType(EvaluationContext evaluationContext) throws EvaluationException;

    @Nullable
    Class<?> getValueType(EvaluationContext evaluationContext, Object obj) throws EvaluationException;

    @Nullable
    TypeDescriptor getValueTypeDescriptor() throws EvaluationException;

    @Nullable
    TypeDescriptor getValueTypeDescriptor(Object obj) throws EvaluationException;

    @Nullable
    TypeDescriptor getValueTypeDescriptor(EvaluationContext evaluationContext) throws EvaluationException;

    @Nullable
    TypeDescriptor getValueTypeDescriptor(EvaluationContext evaluationContext, Object obj) throws EvaluationException;

    boolean isWritable(Object obj) throws EvaluationException;

    boolean isWritable(EvaluationContext evaluationContext) throws EvaluationException;

    boolean isWritable(EvaluationContext evaluationContext, Object obj) throws EvaluationException;

    void setValue(Object obj, @Nullable Object obj2) throws EvaluationException;

    void setValue(EvaluationContext evaluationContext, @Nullable Object obj) throws EvaluationException;

    void setValue(EvaluationContext evaluationContext, Object obj, @Nullable Object obj2) throws EvaluationException;
}