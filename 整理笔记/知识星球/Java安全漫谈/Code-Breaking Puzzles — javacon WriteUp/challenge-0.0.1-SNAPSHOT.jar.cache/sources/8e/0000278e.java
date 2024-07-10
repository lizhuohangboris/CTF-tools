package org.thymeleaf;

import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/DialectConfiguration.class */
public final class DialectConfiguration {
    private final boolean prefixSpecified;
    private final String prefix;
    private final IDialect dialect;

    public DialectConfiguration(IDialect dialect) {
        Validate.notNull(dialect, "Dialect cannot be null");
        this.prefixSpecified = false;
        this.prefix = null;
        this.dialect = dialect;
    }

    public DialectConfiguration(String prefix, IDialect dialect) {
        Validate.notNull(dialect, "Dialect cannot be null");
        this.prefixSpecified = true;
        this.prefix = prefix;
        this.dialect = dialect;
    }

    public IDialect getDialect() {
        return this.dialect;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public boolean isPrefixSpecified() {
        return this.prefixSpecified;
    }
}