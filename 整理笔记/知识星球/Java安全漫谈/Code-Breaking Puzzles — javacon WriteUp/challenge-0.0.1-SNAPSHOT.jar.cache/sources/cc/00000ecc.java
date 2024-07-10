package org.hibernate.validator;

import javax.validation.Configuration;
import javax.validation.ValidatorFactory;
import javax.validation.spi.BootstrapState;
import javax.validation.spi.ConfigurationState;
import javax.validation.spi.ValidationProvider;
import org.hibernate.validator.internal.engine.ConfigurationImpl;
import org.hibernate.validator.internal.engine.ValidatorFactoryImpl;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/HibernateValidator.class */
public class HibernateValidator implements ValidationProvider<HibernateValidatorConfiguration> {
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // javax.validation.spi.ValidationProvider
    public HibernateValidatorConfiguration createSpecializedConfiguration(BootstrapState state) {
        return (HibernateValidatorConfiguration) HibernateValidatorConfiguration.class.cast(new ConfigurationImpl(this));
    }

    @Override // javax.validation.spi.ValidationProvider
    public Configuration<?> createGenericConfiguration(BootstrapState state) {
        return new ConfigurationImpl(state);
    }

    @Override // javax.validation.spi.ValidationProvider
    public ValidatorFactory buildValidatorFactory(ConfigurationState configurationState) {
        return new ValidatorFactoryImpl(configurationState);
    }
}