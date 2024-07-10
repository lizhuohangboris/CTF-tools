package org.apache.catalina.mapper;

import javax.servlet.http.MappingMatch;
import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.Wrapper;
import org.apache.tomcat.util.buf.MessageBytes;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/mapper/MappingData.class */
public class MappingData {
    public Host host = null;
    public Context context = null;
    public int contextSlashCount = 0;
    public Context[] contexts = null;
    public Wrapper wrapper = null;
    public boolean jspWildCard = false;
    public final MessageBytes contextPath = MessageBytes.newInstance();
    public final MessageBytes requestPath = MessageBytes.newInstance();
    public final MessageBytes wrapperPath = MessageBytes.newInstance();
    public final MessageBytes pathInfo = MessageBytes.newInstance();
    public final MessageBytes redirectPath = MessageBytes.newInstance();
    public MappingMatch matchType = null;

    public void recycle() {
        this.host = null;
        this.context = null;
        this.contextSlashCount = 0;
        this.contexts = null;
        this.wrapper = null;
        this.jspWildCard = false;
        this.contextPath.recycle();
        this.requestPath.recycle();
        this.wrapperPath.recycle();
        this.pathInfo.recycle();
        this.redirectPath.recycle();
        this.matchType = null;
    }
}