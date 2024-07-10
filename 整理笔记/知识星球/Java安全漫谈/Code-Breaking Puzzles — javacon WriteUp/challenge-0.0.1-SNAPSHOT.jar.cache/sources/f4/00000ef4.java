package org.hibernate.validator.cfg.defs;

import org.hibernate.validator.cfg.ConstraintDef;
import org.hibernate.validator.constraints.Currency;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/cfg/defs/CurrencyDef.class */
public class CurrencyDef extends ConstraintDef<CurrencyDef, Currency> {
    public CurrencyDef() {
        super(Currency.class);
    }

    public CurrencyDef value(String... value) {
        addParameter("value", value);
        return this;
    }
}