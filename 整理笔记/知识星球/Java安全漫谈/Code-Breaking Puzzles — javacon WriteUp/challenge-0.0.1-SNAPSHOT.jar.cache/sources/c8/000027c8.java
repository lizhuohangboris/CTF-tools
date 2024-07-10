package org.thymeleaf.dialect;

import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/dialect/AbstractDialect.class */
public class AbstractDialect implements IDialect {
    private final String name;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractDialect(String name) {
        Validate.notNull(name, "Dialect name cannot be null");
        this.name = name;
    }

    @Override // org.thymeleaf.dialect.IDialect
    public final String getName() {
        return this.name;
    }
}