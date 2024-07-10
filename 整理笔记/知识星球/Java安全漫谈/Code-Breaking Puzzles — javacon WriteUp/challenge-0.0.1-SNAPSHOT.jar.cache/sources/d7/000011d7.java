package org.hibernate.validator.messageinterpolation;

import java.lang.invoke.MethodHandles;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Locale;
import javax.el.ELManager;
import javax.el.ExpressionFactory;
import javax.validation.MessageInterpolator;
import org.hibernate.validator.internal.engine.messageinterpolation.InterpolationTerm;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.internal.util.privilegedactions.GetClassLoader;
import org.hibernate.validator.internal.util.privilegedactions.SetContextClassLoader;
import org.hibernate.validator.spi.resourceloading.ResourceBundleLocator;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/messageinterpolation/ResourceBundleMessageInterpolator.class */
public class ResourceBundleMessageInterpolator extends AbstractMessageInterpolator {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final ExpressionFactory expressionFactory;

    public ResourceBundleMessageInterpolator() {
        this.expressionFactory = buildExpressionFactory();
    }

    public ResourceBundleMessageInterpolator(ResourceBundleLocator userResourceBundleLocator) {
        super(userResourceBundleLocator);
        this.expressionFactory = buildExpressionFactory();
    }

    public ResourceBundleMessageInterpolator(ResourceBundleLocator userResourceBundleLocator, ResourceBundleLocator contributorResourceBundleLocator) {
        super(userResourceBundleLocator, contributorResourceBundleLocator);
        this.expressionFactory = buildExpressionFactory();
    }

    public ResourceBundleMessageInterpolator(ResourceBundleLocator userResourceBundleLocator, ResourceBundleLocator contributorResourceBundleLocator, boolean cachingEnabled) {
        super(userResourceBundleLocator, contributorResourceBundleLocator, cachingEnabled);
        this.expressionFactory = buildExpressionFactory();
    }

    public ResourceBundleMessageInterpolator(ResourceBundleLocator userResourceBundleLocator, boolean cachingEnabled) {
        super(userResourceBundleLocator, null, cachingEnabled);
        this.expressionFactory = buildExpressionFactory();
    }

    public ResourceBundleMessageInterpolator(ResourceBundleLocator userResourceBundleLocator, boolean cachingEnabled, ExpressionFactory expressionFactory) {
        super(userResourceBundleLocator, null, cachingEnabled);
        this.expressionFactory = expressionFactory;
    }

    @Override // org.hibernate.validator.messageinterpolation.AbstractMessageInterpolator
    public String interpolate(MessageInterpolator.Context context, Locale locale, String term) {
        InterpolationTerm expression = new InterpolationTerm(term, locale, this.expressionFactory);
        return expression.interpolate(context);
    }

    private static ExpressionFactory buildExpressionFactory() {
        if (canLoadExpressionFactory()) {
            ExpressionFactory expressionFactory = ELManager.getExpressionFactory();
            LOG.debug("Loaded expression factory via original TCCL");
            return expressionFactory;
        }
        ClassLoader originalContextClassLoader = (ClassLoader) run(GetClassLoader.fromContext());
        try {
            run(SetContextClassLoader.action(ResourceBundleMessageInterpolator.class.getClassLoader()));
            if (canLoadExpressionFactory()) {
                ExpressionFactory expressionFactory2 = ELManager.getExpressionFactory();
                LOG.debug("Loaded expression factory via HV classloader");
                return expressionFactory2;
            }
            run(SetContextClassLoader.action(ELManager.class.getClassLoader()));
            if (!canLoadExpressionFactory()) {
                run(SetContextClassLoader.action(originalContextClassLoader));
                throw LOG.getUnableToInitializeELExpressionFactoryException(null);
            }
            ExpressionFactory expressionFactory3 = ELManager.getExpressionFactory();
            LOG.debug("Loaded expression factory via EL classloader");
            run(SetContextClassLoader.action(originalContextClassLoader));
            return expressionFactory3;
        } finally {
        }
    }

    private static boolean canLoadExpressionFactory() {
        try {
            ExpressionFactory.newInstance();
            return true;
        } catch (Throwable th) {
            return false;
        }
    }

    private static <T> T run(PrivilegedAction<T> action) {
        return System.getSecurityManager() != null ? (T) AccessController.doPrivileged(action) : action.run();
    }
}