package org.thymeleaf.expression;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/expression/ExpressionObjects.class */
public class ExpressionObjects implements IExpressionObjects {
    private static final int EXPRESSION_OBJECT_MAP_DEFAULT_SIZE = 3;
    private final IExpressionContext context;
    private final IExpressionObjectFactory expressionObjectFactory;
    private final Set<String> expressionObjectNames;
    private Map<String, Object> objects;

    public ExpressionObjects(IExpressionContext context, IExpressionObjectFactory expressionObjectFactory) {
        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(expressionObjectFactory, "Expression Object Factory cannot be null");
        this.context = context;
        this.expressionObjectFactory = expressionObjectFactory;
        this.expressionObjectNames = this.expressionObjectFactory.getAllExpressionObjectNames();
    }

    @Override // org.thymeleaf.expression.IExpressionObjects
    public int size() {
        return this.expressionObjectNames.size();
    }

    @Override // org.thymeleaf.expression.IExpressionObjects
    public boolean containsObject(String name) {
        return this.expressionObjectNames.contains(name);
    }

    @Override // org.thymeleaf.expression.IExpressionObjects
    public Set<String> getObjectNames() {
        return this.expressionObjectNames;
    }

    @Override // org.thymeleaf.expression.IExpressionObjects
    public Object getObject(String name) {
        if (this.objects != null && this.objects.containsKey(name)) {
            return this.objects.get(name);
        }
        if (!this.expressionObjectNames.contains(name)) {
            return null;
        }
        Object object = this.expressionObjectFactory.buildObject(this.context, name);
        if (!this.expressionObjectFactory.isCacheable(name)) {
            return object;
        }
        if (this.objects == null) {
            this.objects = new HashMap(3);
        }
        this.objects.put(name, object);
        return object;
    }
}