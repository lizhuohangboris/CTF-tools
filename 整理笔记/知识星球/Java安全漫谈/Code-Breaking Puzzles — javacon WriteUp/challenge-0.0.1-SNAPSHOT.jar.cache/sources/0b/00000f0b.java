package org.hibernate.validator.cfg.defs;

import org.hibernate.validator.cfg.ConstraintDef;
import org.hibernate.validator.constraints.ParameterScriptAssert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/cfg/defs/ParameterScriptAssertDef.class */
public class ParameterScriptAssertDef extends ConstraintDef<ParameterScriptAssertDef, ParameterScriptAssert> {
    public ParameterScriptAssertDef() {
        super(ParameterScriptAssert.class);
    }

    public ParameterScriptAssertDef lang(String lang) {
        addParameter("lang", lang);
        return this;
    }

    public ParameterScriptAssertDef script(String script) {
        addParameter("script", script);
        return this;
    }
}