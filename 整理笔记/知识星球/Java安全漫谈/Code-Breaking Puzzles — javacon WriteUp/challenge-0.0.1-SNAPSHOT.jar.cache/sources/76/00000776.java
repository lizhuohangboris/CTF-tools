package org.apache.catalina;

import java.beans.PropertyChangeListener;
import java.io.IOException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/Store.class */
public interface Store {
    Manager getManager();

    void setManager(Manager manager);

    int getSize() throws IOException;

    void addPropertyChangeListener(PropertyChangeListener propertyChangeListener);

    String[] keys() throws IOException;

    Session load(String str) throws ClassNotFoundException, IOException;

    void remove(String str) throws IOException;

    void clear() throws IOException;

    void removePropertyChangeListener(PropertyChangeListener propertyChangeListener);

    void save(Session session) throws IOException;
}