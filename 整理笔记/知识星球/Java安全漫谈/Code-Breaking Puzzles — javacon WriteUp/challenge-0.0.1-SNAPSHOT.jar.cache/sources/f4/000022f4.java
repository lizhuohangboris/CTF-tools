package org.springframework.scripting.support;

import org.springframework.aop.target.dynamic.BeanFactoryRefreshableTargetSource;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.scripting.ScriptFactory;
import org.springframework.scripting.ScriptSource;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/scripting/support/RefreshableScriptTargetSource.class */
public class RefreshableScriptTargetSource extends BeanFactoryRefreshableTargetSource {
    private final ScriptFactory scriptFactory;
    private final ScriptSource scriptSource;
    private final boolean isFactoryBean;

    public RefreshableScriptTargetSource(BeanFactory beanFactory, String beanName, ScriptFactory scriptFactory, ScriptSource scriptSource, boolean isFactoryBean) {
        super(beanFactory, beanName);
        Assert.notNull(scriptFactory, "ScriptFactory must not be null");
        Assert.notNull(scriptSource, "ScriptSource must not be null");
        this.scriptFactory = scriptFactory;
        this.scriptSource = scriptSource;
        this.isFactoryBean = isFactoryBean;
    }

    @Override // org.springframework.aop.target.dynamic.AbstractRefreshableTargetSource
    protected boolean requiresRefresh() {
        return this.scriptFactory.requiresScriptedObjectRefresh(this.scriptSource);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.aop.target.dynamic.BeanFactoryRefreshableTargetSource
    public Object obtainFreshBean(BeanFactory beanFactory, String beanName) {
        return super.obtainFreshBean(beanFactory, this.isFactoryBean ? BeanFactory.FACTORY_BEAN_PREFIX + beanName : beanName);
    }
}