package org.apache.coyote;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/ActionHook.class */
public interface ActionHook {
    void action(ActionCode actionCode, Object obj);
}