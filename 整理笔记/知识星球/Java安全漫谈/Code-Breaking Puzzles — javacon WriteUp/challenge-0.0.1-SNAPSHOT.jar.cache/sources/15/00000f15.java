package org.hibernate.validator.cfg.defs;

import org.hibernate.validator.cfg.ConstraintDef;
import org.hibernate.validator.constraints.ScriptAssert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/cfg/defs/ScriptAssertDef.class */
public class ScriptAssertDef extends ConstraintDef<ScriptAssertDef, ScriptAssert> {
    public ScriptAssertDef() {
        super(ScriptAssert.class);
    }

    public ScriptAssertDef lang(String lang) {
        addParameter("lang", lang);
        return this;
    }

    public ScriptAssertDef script(String script) {
        addParameter("script", script);
        return this;
    }

    public ScriptAssertDef alias(String alias) {
        addParameter("alias", alias);
        return this;
    }

    public ScriptAssertDef reportOn(String reportOn) {
        addParameter("reportOn", reportOn);
        return this;
    }
}