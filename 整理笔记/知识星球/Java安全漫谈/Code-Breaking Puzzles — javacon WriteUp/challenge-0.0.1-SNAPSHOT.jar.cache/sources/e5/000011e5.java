package org.hibernate.validator.spi.cfg;

import org.hibernate.validator.cfg.ConstraintMapping;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/spi/cfg/ConstraintMappingContributor.class */
public interface ConstraintMappingContributor {

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/spi/cfg/ConstraintMappingContributor$ConstraintMappingBuilder.class */
    public interface ConstraintMappingBuilder {
        ConstraintMapping addConstraintMapping();
    }

    void createConstraintMappings(ConstraintMappingBuilder constraintMappingBuilder);
}