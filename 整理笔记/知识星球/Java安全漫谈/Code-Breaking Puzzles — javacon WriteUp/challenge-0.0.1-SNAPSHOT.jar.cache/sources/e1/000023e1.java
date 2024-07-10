package org.springframework.validation.support;

import org.springframework.ui.ConcurrentModel;
import org.springframework.validation.BindingResult;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/validation/support/BindingAwareConcurrentModel.class */
public class BindingAwareConcurrentModel extends ConcurrentModel {
    @Override // org.springframework.ui.ConcurrentModel, java.util.concurrent.ConcurrentHashMap, java.util.AbstractMap, java.util.Map
    public Object put(String key, Object value) {
        removeBindingResultIfNecessary(key, value);
        return super.put(key, value);
    }

    private void removeBindingResultIfNecessary(String key, Object value) {
        if (!key.startsWith(BindingResult.MODEL_KEY_PREFIX)) {
            String resultKey = BindingResult.MODEL_KEY_PREFIX + key;
            BindingResult result = (BindingResult) get(resultKey);
            if (result != null && result.getTarget() != value) {
                remove(resultKey);
            }
        }
    }
}