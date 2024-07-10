package org.springframework.boot.autoconfigure.session;

import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.context.properties.bind.BindException;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/session/AbstractSessionCondition.class */
abstract class AbstractSessionCondition extends SpringBootCondition {
    private final WebApplicationType webApplicationType;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractSessionCondition(WebApplicationType webApplicationType) {
        this.webApplicationType = webApplicationType;
    }

    @Override // org.springframework.boot.autoconfigure.condition.SpringBootCondition
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        ConditionMessage.Builder message = ConditionMessage.forCondition("Session Condition", new Object[0]);
        Environment environment = context.getEnvironment();
        StoreType required = SessionStoreMappings.getType(this.webApplicationType, ((AnnotationMetadata) metadata).getClassName());
        if (!environment.containsProperty("spring.session.store-type")) {
            return ConditionOutcome.match(message.didNotFind(BeanDefinitionParserDelegate.PROPERTY_ELEMENT, "properties").items(ConditionMessage.Style.QUOTE, "spring.session.store-type"));
        }
        try {
            Binder binder = Binder.get(environment);
            return (ConditionOutcome) binder.bind("spring.session.store-type", StoreType.class).map(t -> {
                return new ConditionOutcome(t == required, message.found("spring.session.store-type property").items(t));
            }).orElse(ConditionOutcome.noMatch(message.didNotFind("spring.session.store-type property").atAll()));
        } catch (BindException e) {
            return ConditionOutcome.noMatch(message.found("invalid spring.session.store-type property").atAll());
        }
    }
}