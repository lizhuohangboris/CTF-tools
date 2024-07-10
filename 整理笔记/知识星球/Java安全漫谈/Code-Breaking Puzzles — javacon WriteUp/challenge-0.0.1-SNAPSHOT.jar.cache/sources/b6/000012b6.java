package org.springframework.aop.framework;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;
import org.springframework.aop.DynamicIntroductionAdvice;
import org.springframework.aop.IntroductionAdvisor;
import org.springframework.aop.IntroductionInfo;
import org.springframework.aop.TargetSource;
import org.springframework.aop.support.DefaultIntroductionAdvisor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.target.EmptyTargetSource;
import org.springframework.aop.target.SingletonTargetSource;
import org.springframework.beans.PropertyAccessor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/framework/AdvisedSupport.class */
public class AdvisedSupport extends ProxyConfig implements Advised {
    private static final long serialVersionUID = 2651364800145442165L;
    public static final TargetSource EMPTY_TARGET_SOURCE = EmptyTargetSource.INSTANCE;
    TargetSource targetSource;
    private boolean preFiltered;
    AdvisorChainFactory advisorChainFactory;
    private transient Map<MethodCacheKey, List<Object>> methodCache;
    private List<Class<?>> interfaces;
    private List<Advisor> advisors;
    private Advisor[] advisorArray;

    public AdvisedSupport() {
        this.targetSource = EMPTY_TARGET_SOURCE;
        this.preFiltered = false;
        this.advisorChainFactory = new DefaultAdvisorChainFactory();
        this.interfaces = new ArrayList();
        this.advisors = new ArrayList();
        this.advisorArray = new Advisor[0];
        this.methodCache = new ConcurrentHashMap(32);
    }

    public AdvisedSupport(Class<?>... interfaces) {
        this();
        setInterfaces(interfaces);
    }

    public void setTarget(Object target) {
        setTargetSource(new SingletonTargetSource(target));
    }

    @Override // org.springframework.aop.framework.Advised
    public void setTargetSource(@Nullable TargetSource targetSource) {
        this.targetSource = targetSource != null ? targetSource : EMPTY_TARGET_SOURCE;
    }

    @Override // org.springframework.aop.framework.Advised
    public TargetSource getTargetSource() {
        return this.targetSource;
    }

    public void setTargetClass(@Nullable Class<?> targetClass) {
        this.targetSource = EmptyTargetSource.forClass(targetClass);
    }

    @Override // org.springframework.aop.TargetClassAware
    @Nullable
    public Class<?> getTargetClass() {
        return this.targetSource.getTargetClass();
    }

    @Override // org.springframework.aop.framework.Advised
    public void setPreFiltered(boolean preFiltered) {
        this.preFiltered = preFiltered;
    }

    @Override // org.springframework.aop.framework.Advised
    public boolean isPreFiltered() {
        return this.preFiltered;
    }

    public void setAdvisorChainFactory(AdvisorChainFactory advisorChainFactory) {
        Assert.notNull(advisorChainFactory, "AdvisorChainFactory must not be null");
        this.advisorChainFactory = advisorChainFactory;
    }

    public AdvisorChainFactory getAdvisorChainFactory() {
        return this.advisorChainFactory;
    }

    public void setInterfaces(Class<?>... interfaces) {
        Assert.notNull(interfaces, "Interfaces must not be null");
        this.interfaces.clear();
        for (Class<?> ifc : interfaces) {
            addInterface(ifc);
        }
    }

    public void addInterface(Class<?> intf) {
        Assert.notNull(intf, "Interface must not be null");
        if (!intf.isInterface()) {
            throw new IllegalArgumentException(PropertyAccessor.PROPERTY_KEY_PREFIX + intf.getName() + "] is not an interface");
        }
        if (!this.interfaces.contains(intf)) {
            this.interfaces.add(intf);
            adviceChanged();
        }
    }

    public boolean removeInterface(Class<?> intf) {
        return this.interfaces.remove(intf);
    }

    @Override // org.springframework.aop.framework.Advised
    public Class<?>[] getProxiedInterfaces() {
        return ClassUtils.toClassArray(this.interfaces);
    }

    @Override // org.springframework.aop.framework.Advised
    public boolean isInterfaceProxied(Class<?> intf) {
        for (Class<?> proxyIntf : this.interfaces) {
            if (intf.isAssignableFrom(proxyIntf)) {
                return true;
            }
        }
        return false;
    }

    @Override // org.springframework.aop.framework.Advised
    public final Advisor[] getAdvisors() {
        return this.advisorArray;
    }

    @Override // org.springframework.aop.framework.Advised
    public void addAdvisor(Advisor advisor) {
        int pos = this.advisors.size();
        addAdvisor(pos, advisor);
    }

    @Override // org.springframework.aop.framework.Advised
    public void addAdvisor(int pos, Advisor advisor) throws AopConfigException {
        if (advisor instanceof IntroductionAdvisor) {
            validateIntroductionAdvisor((IntroductionAdvisor) advisor);
        }
        addAdvisorInternal(pos, advisor);
    }

    @Override // org.springframework.aop.framework.Advised
    public boolean removeAdvisor(Advisor advisor) {
        int index = indexOf(advisor);
        if (index == -1) {
            return false;
        }
        removeAdvisor(index);
        return true;
    }

    @Override // org.springframework.aop.framework.Advised
    public void removeAdvisor(int index) throws AopConfigException {
        if (isFrozen()) {
            throw new AopConfigException("Cannot remove Advisor: Configuration is frozen.");
        }
        if (index < 0 || index > this.advisors.size() - 1) {
            throw new AopConfigException("Advisor index " + index + " is out of bounds: This configuration only has " + this.advisors.size() + " advisors.");
        }
        Advisor advisor = this.advisors.get(index);
        if (advisor instanceof IntroductionAdvisor) {
            IntroductionAdvisor ia = (IntroductionAdvisor) advisor;
            for (int j = 0; j < ia.getInterfaces().length; j++) {
                removeInterface(ia.getInterfaces()[j]);
            }
        }
        this.advisors.remove(index);
        updateAdvisorArray();
        adviceChanged();
    }

    @Override // org.springframework.aop.framework.Advised
    public int indexOf(Advisor advisor) {
        Assert.notNull(advisor, "Advisor must not be null");
        return this.advisors.indexOf(advisor);
    }

    @Override // org.springframework.aop.framework.Advised
    public boolean replaceAdvisor(Advisor a, Advisor b) throws AopConfigException {
        Assert.notNull(a, "Advisor a must not be null");
        Assert.notNull(b, "Advisor b must not be null");
        int index = indexOf(a);
        if (index == -1) {
            return false;
        }
        removeAdvisor(index);
        addAdvisor(index, b);
        return true;
    }

    public void addAdvisors(Advisor... advisors) {
        addAdvisors(Arrays.asList(advisors));
    }

    public void addAdvisors(Collection<Advisor> advisors) {
        if (isFrozen()) {
            throw new AopConfigException("Cannot add advisor: Configuration is frozen.");
        }
        if (!CollectionUtils.isEmpty(advisors)) {
            for (Advisor advisor : advisors) {
                if (advisor instanceof IntroductionAdvisor) {
                    validateIntroductionAdvisor((IntroductionAdvisor) advisor);
                }
                Assert.notNull(advisor, "Advisor must not be null");
                this.advisors.add(advisor);
            }
            updateAdvisorArray();
            adviceChanged();
        }
    }

    private void validateIntroductionAdvisor(IntroductionAdvisor advisor) {
        advisor.validateInterfaces();
        Class<?>[] ifcs = advisor.getInterfaces();
        for (Class<?> ifc : ifcs) {
            addInterface(ifc);
        }
    }

    private void addAdvisorInternal(int pos, Advisor advisor) throws AopConfigException {
        Assert.notNull(advisor, "Advisor must not be null");
        if (isFrozen()) {
            throw new AopConfigException("Cannot add advisor: Configuration is frozen.");
        }
        if (pos > this.advisors.size()) {
            throw new IllegalArgumentException("Illegal position " + pos + " in advisor list with size " + this.advisors.size());
        }
        this.advisors.add(pos, advisor);
        updateAdvisorArray();
        adviceChanged();
    }

    protected final void updateAdvisorArray() {
        this.advisorArray = (Advisor[]) this.advisors.toArray(new Advisor[0]);
    }

    protected final List<Advisor> getAdvisorsInternal() {
        return this.advisors;
    }

    @Override // org.springframework.aop.framework.Advised
    public void addAdvice(Advice advice) throws AopConfigException {
        int pos = this.advisors.size();
        addAdvice(pos, advice);
    }

    @Override // org.springframework.aop.framework.Advised
    public void addAdvice(int pos, Advice advice) throws AopConfigException {
        Assert.notNull(advice, "Advice must not be null");
        if (advice instanceof IntroductionInfo) {
            addAdvisor(pos, new DefaultIntroductionAdvisor(advice, (IntroductionInfo) advice));
        } else if (advice instanceof DynamicIntroductionAdvice) {
            throw new AopConfigException("DynamicIntroductionAdvice may only be added as part of IntroductionAdvisor");
        } else {
            addAdvisor(pos, new DefaultPointcutAdvisor(advice));
        }
    }

    @Override // org.springframework.aop.framework.Advised
    public boolean removeAdvice(Advice advice) throws AopConfigException {
        int index = indexOf(advice);
        if (index == -1) {
            return false;
        }
        removeAdvisor(index);
        return true;
    }

    @Override // org.springframework.aop.framework.Advised
    public int indexOf(Advice advice) {
        Assert.notNull(advice, "Advice must not be null");
        for (int i = 0; i < this.advisors.size(); i++) {
            Advisor advisor = this.advisors.get(i);
            if (advisor.getAdvice() == advice) {
                return i;
            }
        }
        return -1;
    }

    public boolean adviceIncluded(@Nullable Advice advice) {
        if (advice != null) {
            for (Advisor advisor : this.advisors) {
                if (advisor.getAdvice() == advice) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public int countAdvicesOfType(@Nullable Class<?> adviceClass) {
        int count = 0;
        if (adviceClass != null) {
            for (Advisor advisor : this.advisors) {
                if (adviceClass.isInstance(advisor.getAdvice())) {
                    count++;
                }
            }
        }
        return count;
    }

    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, @Nullable Class<?> targetClass) {
        MethodCacheKey cacheKey = new MethodCacheKey(method);
        List<Object> cached = this.methodCache.get(cacheKey);
        if (cached == null) {
            cached = this.advisorChainFactory.getInterceptorsAndDynamicInterceptionAdvice(this, method, targetClass);
            this.methodCache.put(cacheKey, cached);
        }
        return cached;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void adviceChanged() {
        this.methodCache.clear();
    }

    protected void copyConfigurationFrom(AdvisedSupport other) {
        copyConfigurationFrom(other, other.targetSource, new ArrayList(other.advisors));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void copyConfigurationFrom(AdvisedSupport other, TargetSource targetSource, List<Advisor> advisors) {
        copyFrom(other);
        this.targetSource = targetSource;
        this.advisorChainFactory = other.advisorChainFactory;
        this.interfaces = new ArrayList(other.interfaces);
        for (Advisor advisor : advisors) {
            if (advisor instanceof IntroductionAdvisor) {
                validateIntroductionAdvisor((IntroductionAdvisor) advisor);
            }
            Assert.notNull(advisor, "Advisor must not be null");
            this.advisors.add(advisor);
        }
        updateAdvisorArray();
        adviceChanged();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AdvisedSupport getConfigurationOnlyCopy() {
        AdvisedSupport copy = new AdvisedSupport();
        copy.copyFrom(this);
        copy.targetSource = EmptyTargetSource.forClass(getTargetClass(), getTargetSource().isStatic());
        copy.advisorChainFactory = this.advisorChainFactory;
        copy.interfaces = this.interfaces;
        copy.advisors = this.advisors;
        copy.updateAdvisorArray();
        return copy;
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        this.methodCache = new ConcurrentHashMap(32);
    }

    @Override // org.springframework.aop.framework.Advised
    public String toProxyConfigString() {
        return toString();
    }

    @Override // org.springframework.aop.framework.ProxyConfig
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getName());
        sb.append(": ").append(this.interfaces.size()).append(" interfaces ");
        sb.append(ClassUtils.classNamesToString(this.interfaces)).append("; ");
        sb.append(this.advisors.size()).append(" advisors ");
        sb.append(this.advisors).append("; ");
        sb.append("targetSource [").append(this.targetSource).append("]; ");
        sb.append(super.toString());
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/framework/AdvisedSupport$MethodCacheKey.class */
    public static final class MethodCacheKey implements Comparable<MethodCacheKey> {
        private final Method method;
        private final int hashCode;

        public MethodCacheKey(Method method) {
            this.method = method;
            this.hashCode = method.hashCode();
        }

        public boolean equals(Object other) {
            return this == other || ((other instanceof MethodCacheKey) && this.method == ((MethodCacheKey) other).method);
        }

        public int hashCode() {
            return this.hashCode;
        }

        public String toString() {
            return this.method.toString();
        }

        @Override // java.lang.Comparable
        public int compareTo(MethodCacheKey other) {
            int result = this.method.getName().compareTo(other.method.getName());
            if (result == 0) {
                result = this.method.toString().compareTo(other.method.toString());
            }
            return result;
        }
    }
}