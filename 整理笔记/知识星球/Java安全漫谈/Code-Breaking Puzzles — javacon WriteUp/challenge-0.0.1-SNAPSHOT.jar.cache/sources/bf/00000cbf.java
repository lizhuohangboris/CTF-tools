package org.apache.tomcat.util.http;

import java.io.Serializable;
import org.apache.tomcat.util.buf.MessageBytes;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/http/ServerCookie.class */
public class ServerCookie implements Serializable {
    private static final long serialVersionUID = 1;
    private final MessageBytes name = MessageBytes.newInstance();
    private final MessageBytes value = MessageBytes.newInstance();
    private final MessageBytes path = MessageBytes.newInstance();
    private final MessageBytes domain = MessageBytes.newInstance();
    private final MessageBytes comment = MessageBytes.newInstance();
    private int version = 0;

    public void recycle() {
        this.name.recycle();
        this.value.recycle();
        this.comment.recycle();
        this.path.recycle();
        this.domain.recycle();
        this.version = 0;
    }

    public MessageBytes getComment() {
        return this.comment;
    }

    public MessageBytes getDomain() {
        return this.domain;
    }

    public MessageBytes getPath() {
        return this.path;
    }

    public MessageBytes getName() {
        return this.name;
    }

    public MessageBytes getValue() {
        return this.value;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int v) {
        this.version = v;
    }

    public String toString() {
        return "Cookie " + getName() + "=" + getValue() + " ; " + getVersion() + " " + getPath() + " " + getDomain();
    }
}