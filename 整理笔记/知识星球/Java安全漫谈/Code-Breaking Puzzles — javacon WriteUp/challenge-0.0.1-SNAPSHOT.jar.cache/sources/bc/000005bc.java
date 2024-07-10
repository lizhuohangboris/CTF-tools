package io.tricking.challenge.spel;

import java.util.Collections;
import java.util.List;
import org.springframework.expression.ConstructorResolver;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/classes/io/tricking/challenge/spel/SmallEvaluationContext.class */
public class SmallEvaluationContext extends StandardEvaluationContext {
    @Override // org.springframework.expression.spel.support.StandardEvaluationContext
    public void setConstructorResolvers(List<ConstructorResolver> constructorResolvers) {
    }

    @Override // org.springframework.expression.spel.support.StandardEvaluationContext, org.springframework.expression.EvaluationContext
    public List<ConstructorResolver> getConstructorResolvers() {
        return Collections.emptyList();
    }
}