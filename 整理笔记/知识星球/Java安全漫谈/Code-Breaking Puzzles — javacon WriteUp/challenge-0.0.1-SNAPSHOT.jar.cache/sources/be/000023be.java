package org.springframework.validation;

import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/validation/BindingResultUtils.class */
public abstract class BindingResultUtils {
    @Nullable
    public static BindingResult getBindingResult(Map<?, ?> model, String name) {
        Assert.notNull(model, "Model map must not be null");
        Assert.notNull(name, "Name must not be null");
        Object attr = model.get(BindingResult.MODEL_KEY_PREFIX + name);
        if (attr != null && !(attr instanceof BindingResult)) {
            throw new IllegalStateException("BindingResult attribute is not of type BindingResult: " + attr);
        }
        return (BindingResult) attr;
    }

    public static BindingResult getRequiredBindingResult(Map<?, ?> model, String name) {
        BindingResult bindingResult = getBindingResult(model, name);
        if (bindingResult == null) {
            throw new IllegalStateException("No BindingResult attribute found for name '" + name + "'- have you exposed the correct model?");
        }
        return bindingResult;
    }
}