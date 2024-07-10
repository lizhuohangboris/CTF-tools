package org.hibernate.validator.internal.engine.messageinterpolation.el;

import javax.el.ArrayELResolver;
import javax.el.BeanELResolver;
import javax.el.CompositeELResolver;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.ListELResolver;
import javax.el.MapELResolver;
import javax.el.ResourceBundleELResolver;
import javax.el.StandardELContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/messageinterpolation/el/SimpleELContext.class */
public class SimpleELContext extends StandardELContext {
    private static final ELResolver DEFAULT_RESOLVER = new CompositeELResolver() { // from class: org.hibernate.validator.internal.engine.messageinterpolation.el.SimpleELContext.1
        {
            add(new RootResolver());
            add(new ArrayELResolver(true));
            add(new ListELResolver(true));
            add(new MapELResolver(true));
            add(new ResourceBundleELResolver());
            add(new BeanELResolver(true));
        }
    };

    public SimpleELContext(ExpressionFactory expressionFactory) {
        super(expressionFactory);
        putContext(ExpressionFactory.class, expressionFactory);
    }

    @Override // javax.el.StandardELContext
    public void addELResolver(ELResolver cELResolver) {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " does not support addELResolver.");
    }

    @Override // javax.el.StandardELContext, javax.el.ELContext
    public ELResolver getELResolver() {
        return DEFAULT_RESOLVER;
    }
}