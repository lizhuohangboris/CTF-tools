package org.thymeleaf.templateparser.markup.decoupled;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateparser/markup/decoupled/DecoupledTemplateLogic.class */
public final class DecoupledTemplateLogic {
    private final Map<String, List<DecoupledInjectedAttribute>> injectedAttributes = new HashMap(20);

    public boolean hasInjectedAttributes() {
        return this.injectedAttributes.size() > 0;
    }

    public Set<String> getAllInjectedAttributeSelectors() {
        return this.injectedAttributes.keySet();
    }

    public List<DecoupledInjectedAttribute> getInjectedAttributesForSelector(String selector) {
        return this.injectedAttributes.get(selector);
    }

    public void addInjectedAttribute(String selector, DecoupledInjectedAttribute injectedAttribute) {
        Validate.notNull(selector, "Selector cannot be null");
        Validate.notNull(injectedAttribute, "Injected Attribute cannot be null");
        List<DecoupledInjectedAttribute> injectedAttributesForSelector = this.injectedAttributes.get(selector);
        if (injectedAttributesForSelector == null) {
            injectedAttributesForSelector = new ArrayList<>(2);
            this.injectedAttributes.put(selector, injectedAttributesForSelector);
        }
        injectedAttributesForSelector.add(injectedAttribute);
    }

    public String toString() {
        List<String> keys = new ArrayList<>(this.injectedAttributes.keySet());
        Collections.sort(keys);
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append('{');
        for (int i = 0; i < keys.size(); i++) {
            if (i > 0) {
                strBuilder.append(", ");
            }
            strBuilder.append(keys.get(i));
            strBuilder.append('=');
            strBuilder.append(this.injectedAttributes.get(keys.get(i)));
        }
        strBuilder.append('}');
        return strBuilder.toString();
    }
}