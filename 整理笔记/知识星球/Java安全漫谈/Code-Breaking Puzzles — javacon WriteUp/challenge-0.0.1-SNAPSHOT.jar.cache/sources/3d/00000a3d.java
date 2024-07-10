package org.apache.coyote.http2;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/http2/AbstractStream.class */
public abstract class AbstractStream {
    private static final Log log = LogFactory.getLog(AbstractStream.class);
    private static final StringManager sm = StringManager.getManager(AbstractStream.class);
    private final Integer identifier;
    private volatile AbstractStream parentStream = null;
    private final Set<Stream> childStreams = Collections.newSetFromMap(new ConcurrentHashMap());
    private long windowSize = 65535;

    abstract String getConnectionId();

    public abstract int getWeight();

    public final Integer getIdentifier() {
        return this.identifier;
    }

    public AbstractStream(Integer identifier) {
        this.identifier = identifier;
    }

    public final void detachFromParent() {
        if (this.parentStream != null) {
            this.parentStream.getChildStreams().remove(this);
            this.parentStream = null;
        }
    }

    public final void addChild(Stream child) {
        child.setParentStream(this);
        this.childStreams.add(child);
    }

    public final boolean isDescendant(AbstractStream stream) {
        if (this.childStreams.contains(stream)) {
            return true;
        }
        for (AbstractStream child : this.childStreams) {
            if (child.isDescendant(stream)) {
                return true;
            }
        }
        return false;
    }

    public final AbstractStream getParentStream() {
        return this.parentStream;
    }

    final void setParentStream(AbstractStream parentStream) {
        this.parentStream = parentStream;
    }

    public final Set<Stream> getChildStreams() {
        return this.childStreams;
    }

    public final synchronized void setWindowSize(long windowSize) {
        this.windowSize = windowSize;
    }

    public final synchronized long getWindowSize() {
        return this.windowSize;
    }

    public synchronized void incrementWindowSize(int increment) throws Http2Exception {
        this.windowSize += increment;
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("abstractStream.windowSizeInc", getConnectionId(), getIdentifier(), Integer.toString(increment), Long.toString(this.windowSize)));
        }
        if (this.windowSize > 2147483647L) {
            String msg = sm.getString("abstractStream.windowSizeTooBig", getConnectionId(), this.identifier, Integer.toString(increment), Long.toString(this.windowSize));
            if (this.identifier.intValue() == 0) {
                throw new ConnectionException(msg, Http2Error.FLOW_CONTROL_ERROR);
            }
            throw new StreamException(msg, Http2Error.FLOW_CONTROL_ERROR, this.identifier.intValue());
        }
    }

    public final synchronized void decrementWindowSize(int decrement) {
        this.windowSize -= decrement;
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("abstractStream.windowSizeDec", getConnectionId(), getIdentifier(), Integer.toString(decrement), Long.toString(this.windowSize)));
        }
    }
}