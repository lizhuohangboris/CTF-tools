package org.apache.catalina.startup;

import org.apache.catalina.Container;
import org.apache.catalina.Host;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.digester.RuleSet;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/startup/HostRuleSet.class */
public class HostRuleSet implements RuleSet {
    protected final String prefix;

    public HostRuleSet() {
        this("");
    }

    public HostRuleSet(String prefix) {
        this.prefix = prefix;
    }

    @Override // org.apache.tomcat.util.digester.RuleSet
    public void addRuleInstances(Digester digester) {
        digester.addObjectCreate(this.prefix + "Host", "org.apache.catalina.core.StandardHost", "className");
        digester.addSetProperties(this.prefix + "Host");
        digester.addRule(this.prefix + "Host", new CopyParentClassLoaderRule());
        digester.addRule(this.prefix + "Host", new LifecycleListenerRule("org.apache.catalina.startup.HostConfig", "hostConfigClass"));
        digester.addSetNext(this.prefix + "Host", Container.ADD_CHILD_EVENT, "org.apache.catalina.Container");
        digester.addCallMethod(this.prefix + "Host/Alias", Host.ADD_ALIAS_EVENT, 0);
        digester.addObjectCreate(this.prefix + "Host/Cluster", null, "className");
        digester.addSetProperties(this.prefix + "Host/Cluster");
        digester.addSetNext(this.prefix + "Host/Cluster", "setCluster", "org.apache.catalina.Cluster");
        digester.addObjectCreate(this.prefix + "Host/Listener", null, "className");
        digester.addSetProperties(this.prefix + "Host/Listener");
        digester.addSetNext(this.prefix + "Host/Listener", "addLifecycleListener", "org.apache.catalina.LifecycleListener");
        digester.addRuleSet(new RealmRuleSet(this.prefix + "Host/"));
        digester.addObjectCreate(this.prefix + "Host/Valve", null, "className");
        digester.addSetProperties(this.prefix + "Host/Valve");
        digester.addSetNext(this.prefix + "Host/Valve", Container.ADD_VALVE_EVENT, "org.apache.catalina.Valve");
    }
}