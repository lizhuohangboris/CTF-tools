package org.thymeleaf.standard.expression;

import java.io.Serializable;
import java.util.List;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/FragmentSignature.class */
public final class FragmentSignature implements Serializable {
    private static final long serialVersionUID = 6847640942405961705L;
    private static final char FRAGMENT_SIGNATURE_PARAMETERS_START = '(';
    private static final char FRAGMENT_SIGNATURE_PARAMETERS_END = ')';
    private final String fragmentName;
    private final List<String> parameterNames;

    public FragmentSignature(String fragmentName, List<String> parameterNames) {
        Validate.notEmpty(fragmentName, "Fragment name cannot be null or empty");
        this.fragmentName = fragmentName;
        this.parameterNames = parameterNames;
    }

    public String getFragmentName() {
        return this.fragmentName;
    }

    public boolean hasParameters() {
        return this.parameterNames != null && this.parameterNames.size() > 0;
    }

    public List<String> getParameterNames() {
        return this.parameterNames;
    }

    public String getStringRepresentation() {
        if (this.parameterNames == null || this.parameterNames.size() == 0) {
            return this.fragmentName;
        }
        return this.fragmentName + " (" + StringUtils.join((Iterable<?>) this.parameterNames, ',') + ')';
    }

    public String toString() {
        return getStringRepresentation();
    }
}