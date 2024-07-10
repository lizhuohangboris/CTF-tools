package org.thymeleaf.expression;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/expression/Ids.class */
public class Ids {
    private final ITemplateContext context;

    public String seq(Object id) {
        Validate.notNull(id, "ID cannot be null");
        String str = id.toString();
        return str + this.context.getIdentifierSequences().getAndIncrementIDSeq(str);
    }

    public String next(Object id) {
        Validate.notNull(id, "ID cannot be null");
        String str = id.toString();
        return str + this.context.getIdentifierSequences().getNextIDSeq(str);
    }

    public String prev(Object id) {
        Validate.notNull(id, "ID cannot be null");
        String str = id.toString();
        return str + this.context.getIdentifierSequences().getPreviousIDSeq(str);
    }

    public Ids(ITemplateContext context) {
        Validate.notNull(context, "Context cannot be null");
        this.context = context;
    }
}