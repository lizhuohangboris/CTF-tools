package org.hibernate.validator.parameternameprovider;

import com.thoughtworks.paranamer.AdaptiveParanamer;
import com.thoughtworks.paranamer.CachingParanamer;
import com.thoughtworks.paranamer.Paranamer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import javax.validation.ParameterNameProvider;
import org.hibernate.validator.internal.engine.DefaultParameterNameProvider;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/parameternameprovider/ParanamerParameterNameProvider.class */
public class ParanamerParameterNameProvider implements ParameterNameProvider {
    private final ParameterNameProvider fallBackProvider;
    private final Paranamer paranamer;

    public ParanamerParameterNameProvider() {
        this(null);
    }

    public ParanamerParameterNameProvider(Paranamer paranamer) {
        this.paranamer = paranamer != null ? paranamer : new CachingParanamer(new AdaptiveParanamer());
        this.fallBackProvider = new DefaultParameterNameProvider();
    }

    @Override // javax.validation.ParameterNameProvider
    public List<String> getParameterNames(Constructor<?> constructor) {
        String[] parameterNames;
        synchronized (this.paranamer) {
            parameterNames = this.paranamer.lookupParameterNames(constructor, false);
        }
        if (parameterNames != null && parameterNames.length == constructor.getParameterTypes().length) {
            return Arrays.asList(parameterNames);
        }
        return this.fallBackProvider.getParameterNames(constructor);
    }

    @Override // javax.validation.ParameterNameProvider
    public List<String> getParameterNames(Method method) {
        String[] parameterNames;
        synchronized (this.paranamer) {
            parameterNames = this.paranamer.lookupParameterNames(method, false);
        }
        if (parameterNames != null && parameterNames.length == method.getParameterTypes().length) {
            return Arrays.asList(parameterNames);
        }
        return this.fallBackProvider.getParameterNames(method);
    }
}