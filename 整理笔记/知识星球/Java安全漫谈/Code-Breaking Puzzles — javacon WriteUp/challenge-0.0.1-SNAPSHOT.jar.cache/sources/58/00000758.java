package org.apache.catalina;

import java.beans.PropertyChangeListener;
import java.io.File;
import javax.management.ObjectName;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.juli.logging.Log;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/Container.class */
public interface Container extends Lifecycle {
    public static final String ADD_CHILD_EVENT = "addChild";
    public static final String ADD_VALVE_EVENT = "addValve";
    public static final String REMOVE_CHILD_EVENT = "removeChild";
    public static final String REMOVE_VALVE_EVENT = "removeValve";

    Log getLogger();

    String getLogName();

    ObjectName getObjectName();

    String getDomain();

    String getMBeanKeyProperties();

    Pipeline getPipeline();

    Cluster getCluster();

    void setCluster(Cluster cluster);

    int getBackgroundProcessorDelay();

    void setBackgroundProcessorDelay(int i);

    String getName();

    void setName(String str);

    Container getParent();

    void setParent(Container container);

    ClassLoader getParentClassLoader();

    void setParentClassLoader(ClassLoader classLoader);

    Realm getRealm();

    void setRealm(Realm realm);

    void backgroundProcess();

    void addChild(Container container);

    void addContainerListener(ContainerListener containerListener);

    void addPropertyChangeListener(PropertyChangeListener propertyChangeListener);

    Container findChild(String str);

    Container[] findChildren();

    ContainerListener[] findContainerListeners();

    void removeChild(Container container);

    void removeContainerListener(ContainerListener containerListener);

    void removePropertyChangeListener(PropertyChangeListener propertyChangeListener);

    void fireContainerEvent(String str, Object obj);

    void logAccess(Request request, Response response, long j, boolean z);

    AccessLog getAccessLog();

    int getStartStopThreads();

    void setStartStopThreads(int i);

    File getCatalinaBase();

    File getCatalinaHome();
}