package org.slf4j.helpers;

import java.io.ObjectStreamException;
import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/slf4j-api-1.7.25.jar:org/slf4j/helpers/NamedLoggerBase.class */
public abstract class NamedLoggerBase implements Logger, Serializable {
    private static final long serialVersionUID = 7535258609338176893L;
    protected String name;

    @Override // org.slf4j.Logger
    public String getName() {
        return this.name;
    }

    protected Object readResolve() throws ObjectStreamException {
        return LoggerFactory.getLogger(getName());
    }
}