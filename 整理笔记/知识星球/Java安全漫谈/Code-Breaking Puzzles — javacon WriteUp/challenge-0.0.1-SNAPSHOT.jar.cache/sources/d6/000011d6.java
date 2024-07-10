package org.hibernate.validator.messageinterpolation;

import java.lang.invoke.MethodHandles;
import java.util.Locale;
import javax.validation.MessageInterpolator;
import org.hibernate.validator.internal.engine.messageinterpolation.InterpolationTerm;
import org.hibernate.validator.internal.engine.messageinterpolation.ParameterTermResolver;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/messageinterpolation/ParameterMessageInterpolator.class */
public class ParameterMessageInterpolator extends AbstractMessageInterpolator {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());

    @Override // org.hibernate.validator.messageinterpolation.AbstractMessageInterpolator
    public String interpolate(MessageInterpolator.Context context, Locale locale, String term) {
        if (InterpolationTerm.isElExpression(term)) {
            LOG.warnElIsUnsupported(term);
            return term;
        }
        ParameterTermResolver parameterTermResolver = new ParameterTermResolver();
        return parameterTermResolver.interpolate(context, term);
    }
}