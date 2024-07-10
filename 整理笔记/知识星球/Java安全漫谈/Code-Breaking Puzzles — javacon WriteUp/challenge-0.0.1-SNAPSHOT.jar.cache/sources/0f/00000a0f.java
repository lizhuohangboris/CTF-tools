package org.apache.coyote;

import javax.servlet.http.HttpUpgradeHandler;
import org.apache.tomcat.ContextBind;
import org.apache.tomcat.InstanceManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/UpgradeToken.class */
public final class UpgradeToken {
    private final ContextBind contextBind;
    private final HttpUpgradeHandler httpUpgradeHandler;
    private final InstanceManager instanceManager;

    public UpgradeToken(HttpUpgradeHandler httpUpgradeHandler, ContextBind contextBind, InstanceManager instanceManager) {
        this.contextBind = contextBind;
        this.httpUpgradeHandler = httpUpgradeHandler;
        this.instanceManager = instanceManager;
    }

    public final ContextBind getContextBind() {
        return this.contextBind;
    }

    public final HttpUpgradeHandler getHttpUpgradeHandler() {
        return this.httpUpgradeHandler;
    }

    public final InstanceManager getInstanceManager() {
        return this.instanceManager;
    }
}