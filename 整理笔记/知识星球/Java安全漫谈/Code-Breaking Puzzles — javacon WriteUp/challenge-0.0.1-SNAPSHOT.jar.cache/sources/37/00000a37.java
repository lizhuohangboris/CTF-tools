package org.apache.coyote.http11.upgrade;

import javax.servlet.http.HttpUpgradeHandler;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.SSLSupport;
import org.apache.tomcat.util.net.SocketEvent;
import org.apache.tomcat.util.net.SocketWrapperBase;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/http11/upgrade/InternalHttpUpgradeHandler.class */
public interface InternalHttpUpgradeHandler extends HttpUpgradeHandler {
    AbstractEndpoint.Handler.SocketState upgradeDispatch(SocketEvent socketEvent);

    void setSocketWrapper(SocketWrapperBase<?> socketWrapperBase);

    void setSslSupport(SSLSupport sSLSupport);

    void pause();
}