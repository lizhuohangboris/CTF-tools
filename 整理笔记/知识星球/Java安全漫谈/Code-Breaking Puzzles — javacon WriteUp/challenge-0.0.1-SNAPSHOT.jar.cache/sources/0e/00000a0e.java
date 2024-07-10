package org.apache.coyote;

import org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler;
import org.apache.tomcat.util.net.SocketWrapperBase;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/UpgradeProtocol.class */
public interface UpgradeProtocol {
    String getHttpUpgradeName(boolean z);

    byte[] getAlpnIdentifier();

    String getAlpnName();

    Processor getProcessor(SocketWrapperBase<?> socketWrapperBase, Adapter adapter);

    InternalHttpUpgradeHandler getInternalUpgradeHandler(SocketWrapperBase<?> socketWrapperBase, Adapter adapter, Request request);

    boolean accept(Request request);
}