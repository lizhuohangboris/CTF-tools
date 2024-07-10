package org.springframework.validation;

import java.io.Serializable;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/validation/MapBindingResult.class */
public class MapBindingResult extends AbstractBindingResult implements Serializable {
    private final Map<?, ?> target;

    public MapBindingResult(Map<?, ?> target, String objectName) {
        super(objectName);
        Assert.notNull(target, "Target Map must not be null");
        this.target = target;
    }

    public final Map<?, ?> getTargetMap() {
        return this.target;
    }

    @Override // org.springframework.validation.AbstractBindingResult, org.springframework.validation.BindingResult
    public final Object getTarget() {
        return this.target;
    }

    @Override // org.springframework.validation.AbstractBindingResult
    @Nullable
    protected Object getActualFieldValue(String field) {
        return this.target.get(field);
    }
}