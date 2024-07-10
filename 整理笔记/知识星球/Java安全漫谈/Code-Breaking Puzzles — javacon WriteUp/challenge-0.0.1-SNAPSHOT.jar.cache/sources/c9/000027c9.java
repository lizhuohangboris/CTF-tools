package org.thymeleaf.dialect;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/dialect/AbstractProcessorDialect.class */
public abstract class AbstractProcessorDialect extends AbstractDialect implements IProcessorDialect {
    private final String prefix;
    private final int processorPrecedence;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractProcessorDialect(String name, String prefix, int processorPrecedence) {
        super(name);
        this.prefix = prefix;
        this.processorPrecedence = processorPrecedence;
    }

    @Override // org.thymeleaf.dialect.IProcessorDialect
    public final String getPrefix() {
        return this.prefix;
    }

    @Override // org.thymeleaf.dialect.IProcessorDialect
    public final int getDialectProcessorPrecedence() {
        return this.processorPrecedence;
    }
}