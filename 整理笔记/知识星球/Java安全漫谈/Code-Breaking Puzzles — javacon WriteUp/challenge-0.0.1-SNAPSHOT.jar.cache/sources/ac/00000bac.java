package org.apache.naming;

import java.util.Iterator;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/naming/NamingContextEnumeration.class */
public class NamingContextEnumeration implements NamingEnumeration<NameClassPair> {
    protected final Iterator<NamingEntry> iterator;

    public NamingContextEnumeration(Iterator<NamingEntry> entries) {
        this.iterator = entries;
    }

    /* renamed from: next */
    public NameClassPair m727next() throws NamingException {
        return m728nextElement();
    }

    public boolean hasMore() throws NamingException {
        return this.iterator.hasNext();
    }

    public void close() throws NamingException {
    }

    public boolean hasMoreElements() {
        return this.iterator.hasNext();
    }

    /* renamed from: nextElement */
    public NameClassPair m728nextElement() {
        NamingEntry entry = this.iterator.next();
        return new NameClassPair(entry.name, entry.value.getClass().getName());
    }
}