package org.springframework.expression.spel.ast;

import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/ast/ValueRef.class */
public interface ValueRef {
    TypedValue getValue();

    void setValue(@Nullable Object obj);

    boolean isWritable();

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/ast/ValueRef$NullValueRef.class */
    public static class NullValueRef implements ValueRef {
        static final NullValueRef INSTANCE = new NullValueRef();

        @Override // org.springframework.expression.spel.ast.ValueRef
        public TypedValue getValue() {
            return TypedValue.NULL;
        }

        @Override // org.springframework.expression.spel.ast.ValueRef
        public void setValue(@Nullable Object newValue) {
            throw new SpelEvaluationException(0, SpelMessage.NOT_ASSIGNABLE, BeanDefinitionParserDelegate.NULL_ELEMENT);
        }

        @Override // org.springframework.expression.spel.ast.ValueRef
        public boolean isWritable() {
            return false;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/ast/ValueRef$TypedValueHolderValueRef.class */
    public static class TypedValueHolderValueRef implements ValueRef {
        private final TypedValue typedValue;
        private final SpelNodeImpl node;

        public TypedValueHolderValueRef(TypedValue typedValue, SpelNodeImpl node) {
            this.typedValue = typedValue;
            this.node = node;
        }

        @Override // org.springframework.expression.spel.ast.ValueRef
        public TypedValue getValue() {
            return this.typedValue;
        }

        @Override // org.springframework.expression.spel.ast.ValueRef
        public void setValue(@Nullable Object newValue) {
            throw new SpelEvaluationException(this.node.pos, SpelMessage.NOT_ASSIGNABLE, this.node.toStringAST());
        }

        @Override // org.springframework.expression.spel.ast.ValueRef
        public boolean isWritable() {
            return false;
        }
    }
}