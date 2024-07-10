package org.hibernate.validator.cfg.defs;

import org.hibernate.validator.cfg.ConstraintDef;
import org.hibernate.validator.constraints.time.DurationMax;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/cfg/defs/DurationMaxDef.class */
public class DurationMaxDef extends ConstraintDef<DurationMaxDef, DurationMax> {
    public DurationMaxDef() {
        super(DurationMax.class);
    }

    public DurationMaxDef days(long days) {
        addParameter("days", Long.valueOf(days));
        return this;
    }

    public DurationMaxDef hours(long hours) {
        addParameter("hours", Long.valueOf(hours));
        return this;
    }

    public DurationMaxDef minutes(long minutes) {
        addParameter("minutes", Long.valueOf(minutes));
        return this;
    }

    public DurationMaxDef seconds(long seconds) {
        addParameter("seconds", Long.valueOf(seconds));
        return this;
    }

    public DurationMaxDef millis(long millis) {
        addParameter("millis", Long.valueOf(millis));
        return this;
    }

    public DurationMaxDef nanos(long nanos) {
        addParameter("nanos", Long.valueOf(nanos));
        return this;
    }

    public DurationMaxDef inclusive(boolean inclusive) {
        addParameter("inclusive", Boolean.valueOf(inclusive));
        return this;
    }
}