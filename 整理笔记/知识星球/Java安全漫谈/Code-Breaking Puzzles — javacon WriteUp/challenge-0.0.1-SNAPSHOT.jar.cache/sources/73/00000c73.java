package org.apache.tomcat.util.descriptor.web;

import java.io.Serializable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/descriptor/web/InjectionTarget.class */
public class InjectionTarget implements Serializable {
    private static final long serialVersionUID = 1;
    private String targetClass;
    private String targetName;

    public InjectionTarget() {
    }

    public InjectionTarget(String targetClass, String targetName) {
        this.targetClass = targetClass;
        this.targetName = targetName;
    }

    public String getTargetClass() {
        return this.targetClass;
    }

    public void setTargetClass(String targetClass) {
        this.targetClass = targetClass;
    }

    public String getTargetName() {
        return this.targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }
}