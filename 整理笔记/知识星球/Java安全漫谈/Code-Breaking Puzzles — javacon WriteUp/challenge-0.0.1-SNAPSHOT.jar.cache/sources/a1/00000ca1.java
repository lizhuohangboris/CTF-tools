package org.apache.tomcat.util.digester;

import org.xml.sax.Attributes;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/digester/FactoryCreateRule.class */
public class FactoryCreateRule extends Rule {
    private boolean ignoreCreateExceptions;
    private ArrayStack<Boolean> exceptionIgnoredStack;
    protected ObjectCreationFactory creationFactory;

    public FactoryCreateRule(ObjectCreationFactory creationFactory, boolean ignoreCreateExceptions) {
        this.creationFactory = null;
        this.creationFactory = creationFactory;
        this.ignoreCreateExceptions = ignoreCreateExceptions;
    }

    @Override // org.apache.tomcat.util.digester.Rule
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        if (this.ignoreCreateExceptions) {
            if (this.exceptionIgnoredStack == null) {
                this.exceptionIgnoredStack = new ArrayStack<>();
            }
            try {
                Object instance = this.creationFactory.createObject(attributes);
                if (this.digester.log.isDebugEnabled()) {
                    this.digester.log.debug("[FactoryCreateRule]{" + this.digester.match + "} New " + instance.getClass().getName());
                }
                this.digester.push(instance);
                this.exceptionIgnoredStack.push(Boolean.FALSE);
                return;
            } catch (Exception e) {
                if (this.digester.log.isInfoEnabled()) {
                    this.digester.log.info("[FactoryCreateRule] Create exception ignored: " + (e.getMessage() == null ? e.getClass().getName() : e.getMessage()));
                    if (this.digester.log.isDebugEnabled()) {
                        this.digester.log.debug("[FactoryCreateRule] Ignored exception:", e);
                    }
                }
                this.exceptionIgnoredStack.push(Boolean.TRUE);
                return;
            }
        }
        Object instance2 = this.creationFactory.createObject(attributes);
        if (this.digester.log.isDebugEnabled()) {
            this.digester.log.debug("[FactoryCreateRule]{" + this.digester.match + "} New " + instance2.getClass().getName());
        }
        this.digester.push(instance2);
    }

    @Override // org.apache.tomcat.util.digester.Rule
    public void end(String namespace, String name) throws Exception {
        if (this.ignoreCreateExceptions && this.exceptionIgnoredStack != null && !this.exceptionIgnoredStack.empty() && this.exceptionIgnoredStack.pop().booleanValue()) {
            if (this.digester.log.isTraceEnabled()) {
                this.digester.log.trace("[FactoryCreateRule] No creation so no push so no pop");
                return;
            }
            return;
        }
        Object top = this.digester.pop();
        if (this.digester.log.isDebugEnabled()) {
            this.digester.log.debug("[FactoryCreateRule]{" + this.digester.match + "} Pop " + top.getClass().getName());
        }
    }

    @Override // org.apache.tomcat.util.digester.Rule
    public void finish() throws Exception {
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("FactoryCreateRule[");
        if (this.creationFactory != null) {
            sb.append("creationFactory=");
            sb.append(this.creationFactory);
        }
        sb.append("]");
        return sb.toString();
    }
}