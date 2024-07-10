package org.hibernate.validator.cfg.defs;

import org.hibernate.validator.cfg.ConstraintDef;
import org.hibernate.validator.constraints.CodePointLength;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/cfg/defs/CodePointLengthDef.class */
public class CodePointLengthDef extends ConstraintDef<CodePointLengthDef, CodePointLength> {
    public CodePointLengthDef() {
        super(CodePointLength.class);
    }

    public CodePointLengthDef min(int min) {
        addParameter("min", Integer.valueOf(min));
        return this;
    }

    public CodePointLengthDef max(int max) {
        addParameter("max", Integer.valueOf(max));
        return this;
    }

    public CodePointLengthDef normalizationStrategy(CodePointLength.NormalizationStrategy strategy) {
        addParameter("normalizationStrategy", strategy);
        return this;
    }
}