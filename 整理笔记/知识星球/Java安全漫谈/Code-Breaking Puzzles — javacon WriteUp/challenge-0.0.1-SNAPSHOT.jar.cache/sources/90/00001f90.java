package org.springframework.expression.spel.support;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypeLocator;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/support/StandardTypeLocator.class */
public class StandardTypeLocator implements TypeLocator {
    @Nullable
    private final ClassLoader classLoader;
    private final List<String> knownPackagePrefixes;

    public StandardTypeLocator() {
        this(ClassUtils.getDefaultClassLoader());
    }

    public StandardTypeLocator(@Nullable ClassLoader classLoader) {
        this.knownPackagePrefixes = new LinkedList();
        this.classLoader = classLoader;
        registerImport("java.lang");
    }

    public void registerImport(String prefix) {
        this.knownPackagePrefixes.add(prefix);
    }

    public void removeImport(String prefix) {
        this.knownPackagePrefixes.remove(prefix);
    }

    public List<String> getImportPrefixes() {
        return Collections.unmodifiableList(this.knownPackagePrefixes);
    }

    @Override // org.springframework.expression.TypeLocator
    public Class<?> findType(String typeName) throws EvaluationException {
        try {
            return ClassUtils.forName(typeName, this.classLoader);
        } catch (ClassNotFoundException e) {
            for (String prefix : this.knownPackagePrefixes) {
                try {
                    String nameToLookup = prefix + '.' + typeName;
                    return ClassUtils.forName(nameToLookup, this.classLoader);
                } catch (ClassNotFoundException e2) {
                }
            }
            throw new SpelEvaluationException(SpelMessage.TYPE_NOT_FOUND, typeName);
        }
    }
}