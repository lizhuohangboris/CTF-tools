package org.apache.catalina.session;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Manager;
import org.apache.catalina.Store;
import org.apache.catalina.util.CustomObjectInputStream;
import org.apache.catalina.util.LifecycleBase;
import org.apache.catalina.util.ToStringUtil;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/session/StoreBase.class */
public abstract class StoreBase extends LifecycleBase implements Store {
    protected static final String storeName = "StoreBase";
    protected final PropertyChangeSupport support = new PropertyChangeSupport(this);
    protected static final StringManager sm = StringManager.getManager(StoreBase.class);
    protected Manager manager;

    public String getStoreName() {
        return storeName;
    }

    @Override // org.apache.catalina.Store
    public void setManager(Manager manager) {
        Manager oldManager = this.manager;
        this.manager = manager;
        this.support.firePropertyChange("manager", oldManager, this.manager);
    }

    @Override // org.apache.catalina.Store
    public Manager getManager() {
        return this.manager;
    }

    @Override // org.apache.catalina.Store
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.support.addPropertyChangeListener(listener);
    }

    @Override // org.apache.catalina.Store
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.support.removePropertyChangeListener(listener);
    }

    public String[] expiredKeys() throws IOException {
        return keys();
    }

    public void processExpires() {
        if (!getState().isAvailable()) {
            return;
        }
        try {
            String[] keys = expiredKeys();
            if (this.manager.getContext().getLogger().isDebugEnabled()) {
                this.manager.getContext().getLogger().debug(getStoreName() + ": processExpires check number of " + keys.length + " sessions");
            }
            long timeNow = System.currentTimeMillis();
            for (int i = 0; i < keys.length; i++) {
                try {
                    StandardSession session = (StandardSession) load(keys[i]);
                    if (session != null) {
                        int timeIdle = (int) ((timeNow - session.getThisAccessedTime()) / 1000);
                        if (timeIdle >= session.getMaxInactiveInterval()) {
                            if (this.manager.getContext().getLogger().isDebugEnabled()) {
                                this.manager.getContext().getLogger().debug(getStoreName() + ": processExpires expire store session " + keys[i]);
                            }
                            boolean isLoaded = false;
                            if (this.manager instanceof PersistentManagerBase) {
                                isLoaded = ((PersistentManagerBase) this.manager).isLoaded(keys[i]);
                            } else {
                                try {
                                    if (this.manager.findSession(keys[i]) != null) {
                                        isLoaded = true;
                                    }
                                } catch (IOException e) {
                                }
                            }
                            if (isLoaded) {
                                session.recycle();
                            } else {
                                session.expire();
                            }
                            remove(keys[i]);
                        }
                    }
                } catch (Exception e2) {
                    this.manager.getContext().getLogger().error("Session: " + keys[i] + "; ", e2);
                    try {
                        remove(keys[i]);
                    } catch (IOException e22) {
                        this.manager.getContext().getLogger().error("Error removing key", e22);
                    }
                }
            }
        } catch (IOException e3) {
            this.manager.getContext().getLogger().error("Error getting keys", e3);
        }
    }

    public ObjectInputStream getObjectInputStream(InputStream is) throws IOException {
        CustomObjectInputStream ois;
        BufferedInputStream bis = new BufferedInputStream(is);
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (this.manager instanceof ManagerBase) {
            ManagerBase managerBase = (ManagerBase) this.manager;
            ois = new CustomObjectInputStream(bis, classLoader, this.manager.getContext().getLogger(), managerBase.getSessionAttributeValueClassNamePattern(), managerBase.getWarnOnSessionAttributeFilterFailure());
        } else {
            ois = new CustomObjectInputStream(bis, classLoader);
        }
        return ois;
    }

    @Override // org.apache.catalina.util.LifecycleBase
    protected void initInternal() {
    }

    @Override // org.apache.catalina.util.LifecycleBase
    public synchronized void startInternal() throws LifecycleException {
        setState(LifecycleState.STARTING);
    }

    @Override // org.apache.catalina.util.LifecycleBase
    public synchronized void stopInternal() throws LifecycleException {
        setState(LifecycleState.STOPPING);
    }

    @Override // org.apache.catalina.util.LifecycleBase
    protected void destroyInternal() {
    }

    public String toString() {
        return ToStringUtil.toString(this, this.manager);
    }
}