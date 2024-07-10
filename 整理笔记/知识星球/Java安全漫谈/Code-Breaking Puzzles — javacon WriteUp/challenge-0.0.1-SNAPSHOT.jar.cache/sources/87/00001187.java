package org.hibernate.validator.internal.util.logging.formatter;

import java.time.Duration;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/logging/formatter/DurationFormatter.class */
public class DurationFormatter {
    private final String stringRepresentation;

    public DurationFormatter(Duration duration) {
        if (Duration.ZERO.equals(duration)) {
            this.stringRepresentation = CustomBooleanEditor.VALUE_0;
            return;
        }
        long seconds = duration.getSeconds();
        long days = seconds / 86400;
        long hours = (seconds / 3600) % 24;
        long minutes = (seconds / 60) % 60;
        int millis = duration.getNano() / 1000000;
        int nanos = duration.getNano() % 1000000;
        StringBuilder formattedDuration = new StringBuilder();
        appendTimeUnit(formattedDuration, days, "days", "day");
        appendTimeUnit(formattedDuration, hours, "hours", "hour");
        appendTimeUnit(formattedDuration, minutes, "minutes", "minute");
        appendTimeUnit(formattedDuration, seconds % 60, "seconds", "second");
        appendTimeUnit(formattedDuration, millis, "milliseconds", "millisecond");
        appendTimeUnit(formattedDuration, nanos, "nanoseconds", "nanosecond");
        this.stringRepresentation = formattedDuration.toString();
    }

    private void appendTimeUnit(StringBuilder sb, long number, String pluralLabel, String singularLabel) {
        if (number == 0) {
            return;
        }
        if (sb.length() > 0) {
            sb.append(" ");
        }
        sb.append(number).append(" ").append(number == 1 ? singularLabel : pluralLabel);
    }

    public String toString() {
        return this.stringRepresentation;
    }
}