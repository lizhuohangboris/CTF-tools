package org.hibernate.validator.cfg.defs;

import org.hibernate.validator.cfg.ConstraintDef;
import org.hibernate.validator.constraints.time.DurationMin;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/cfg/defs/DurationMinDef.class */
public class DurationMinDef extends ConstraintDef<DurationMinDef, DurationMin> {
    public DurationMinDef() {
        super(DurationMin.class);
    }

    public DurationMinDef days(long days) {
        addParameter("days", Long.valueOf(days));
        return this;
    }

    public DurationMinDef hours(long hours) {
        addParameter("hours", Long.valueOf(hours));
        return this;
    }

    public DurationMinDef minutes(long minutes) {
        addParameter("minutes", Long.valueOf(minutes));
        return this;
    }

    public DurationMinDef seconds(long seconds) {
        addParameter("seconds", Long.valueOf(seconds));
        return this;
    }

    public DurationMinDef millis(long millis) {
        addParameter("millis", Long.valueOf(millis));
        return this;
    }

    public DurationMinDef nanos(long nanos) {
        addParameter("nanos", Long.valueOf(nanos));
        return this;
    }

    public DurationMinDef inclusive(boolean inclusive) {
        addParameter("inclusive", Boolean.valueOf(inclusive));
        return this;
    }
}