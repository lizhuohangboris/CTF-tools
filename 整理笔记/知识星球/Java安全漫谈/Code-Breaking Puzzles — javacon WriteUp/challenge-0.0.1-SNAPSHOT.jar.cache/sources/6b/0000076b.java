package org.apache.catalina;

import java.beans.PropertyChangeListener;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/Loader.class */
public interface Loader {
    void backgroundProcess();

    ClassLoader getClassLoader();

    Context getContext();

    void setContext(Context context);

    boolean getDelegate();

    void setDelegate(boolean z);

    boolean getReloadable();

    void setReloadable(boolean z);

    void addPropertyChangeListener(PropertyChangeListener propertyChangeListener);

    boolean modified();

    void removePropertyChangeListener(PropertyChangeListener propertyChangeListener);
}