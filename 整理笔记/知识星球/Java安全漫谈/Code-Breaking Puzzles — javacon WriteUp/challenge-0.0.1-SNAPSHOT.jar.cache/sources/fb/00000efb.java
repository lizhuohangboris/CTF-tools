package org.hibernate.validator.cfg.defs;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import org.hibernate.validator.cfg.ConstraintDef;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/cfg/defs/EmailDef.class */
public class EmailDef extends ConstraintDef<EmailDef, Email> {
    public EmailDef() {
        super(Email.class);
    }

    public EmailDef regexp(String regexp) {
        addParameter("regexp", regexp);
        return this;
    }

    public EmailDef flags(Pattern.Flag... flags) {
        addParameter("flags", flags);
        return this;
    }
}