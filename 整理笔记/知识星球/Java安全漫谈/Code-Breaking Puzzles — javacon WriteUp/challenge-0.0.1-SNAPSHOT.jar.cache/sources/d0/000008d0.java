package org.apache.catalina.session;

import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.WriteAbortedException;
import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionContext;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionIdListener;
import javax.servlet.http.HttpSessionListener;
import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.SessionEvent;
import org.apache.catalina.SessionListener;
import org.apache.catalina.TomcatPrincipal;
import org.apache.catalina.security.SecurityUtil;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/session/StandardSession.class */
public class StandardSession implements HttpSession, Session, Serializable {
    private static final long serialVersionUID = 1;
    protected static final boolean STRICT_SERVLET_COMPLIANCE = Globals.STRICT_SERVLET_COMPLIANCE;
    protected static final boolean ACTIVITY_CHECK;
    protected static final boolean LAST_ACCESS_AT_START;
    protected static final String[] EMPTY_ARRAY;
    protected transient Manager manager;
    protected static final StringManager sm;
    @Deprecated
    protected static volatile HttpSessionContext sessionContext;
    protected transient AtomicInteger accessCount;
    protected ConcurrentMap<String, Object> attributes = new ConcurrentHashMap();
    protected transient String authType = null;
    protected long creationTime = 0;
    protected volatile transient boolean expiring = false;
    protected transient StandardSessionFacade facade = null;
    protected String id = null;
    protected volatile long lastAccessedTime = this.creationTime;
    protected transient ArrayList<SessionListener> listeners = new ArrayList<>();
    protected volatile int maxInactiveInterval = -1;
    protected volatile boolean isNew = false;
    protected volatile boolean isValid = false;
    protected transient Map<String, Object> notes = new Hashtable();
    protected transient Principal principal = null;
    protected final transient PropertyChangeSupport support = new PropertyChangeSupport(this);
    protected volatile long thisAccessedTime = this.creationTime;

    static {
        String activityCheck = System.getProperty("org.apache.catalina.session.StandardSession.ACTIVITY_CHECK");
        if (activityCheck == null) {
            ACTIVITY_CHECK = STRICT_SERVLET_COMPLIANCE;
        } else {
            ACTIVITY_CHECK = Boolean.parseBoolean(activityCheck);
        }
        String lastAccessAtStart = System.getProperty("org.apache.catalina.session.StandardSession.LAST_ACCESS_AT_START");
        if (lastAccessAtStart == null) {
            LAST_ACCESS_AT_START = STRICT_SERVLET_COMPLIANCE;
        } else {
            LAST_ACCESS_AT_START = Boolean.parseBoolean(lastAccessAtStart);
        }
        EMPTY_ARRAY = new String[0];
        sm = StringManager.getManager(StandardSession.class);
        sessionContext = null;
    }

    public StandardSession(Manager manager) {
        this.manager = null;
        this.accessCount = null;
        this.manager = manager;
        if (ACTIVITY_CHECK) {
            this.accessCount = new AtomicInteger();
        }
    }

    @Override // org.apache.catalina.Session
    public String getAuthType() {
        return this.authType;
    }

    @Override // org.apache.catalina.Session
    public void setAuthType(String authType) {
        String oldAuthType = this.authType;
        this.authType = authType;
        this.support.firePropertyChange("authType", oldAuthType, this.authType);
    }

    @Override // org.apache.catalina.Session
    public void setCreationTime(long time) {
        this.creationTime = time;
        this.lastAccessedTime = time;
        this.thisAccessedTime = time;
    }

    @Override // javax.servlet.http.HttpSession, org.apache.catalina.Session
    public String getId() {
        return this.id;
    }

    @Override // org.apache.catalina.Session
    public String getIdInternal() {
        return this.id;
    }

    @Override // org.apache.catalina.Session
    public void setId(String id) {
        setId(id, true);
    }

    @Override // org.apache.catalina.Session
    public void setId(String id, boolean notify) {
        if (this.id != null && this.manager != null) {
            this.manager.remove(this);
        }
        this.id = id;
        if (this.manager != null) {
            this.manager.add(this);
        }
        if (notify) {
            tellNew();
        }
    }

    public void tellNew() {
        fireSessionEvent(Session.SESSION_CREATED_EVENT, null);
        Context context = this.manager.getContext();
        Object[] listeners = context.getApplicationLifecycleListeners();
        if (listeners != null && listeners.length > 0) {
            HttpSessionEvent event = new HttpSessionEvent(getSession());
            for (int i = 0; i < listeners.length; i++) {
                if (listeners[i] instanceof HttpSessionListener) {
                    HttpSessionListener listener = (HttpSessionListener) listeners[i];
                    try {
                        context.fireContainerEvent("beforeSessionCreated", listener);
                        listener.sessionCreated(event);
                        context.fireContainerEvent("afterSessionCreated", listener);
                    } catch (Throwable t) {
                        ExceptionUtils.handleThrowable(t);
                        try {
                            context.fireContainerEvent("afterSessionCreated", listener);
                        } catch (Exception e) {
                        }
                        this.manager.getContext().getLogger().error(sm.getString("standardSession.sessionEvent"), t);
                    }
                }
            }
        }
    }

    @Override // org.apache.catalina.Session
    public void tellChangedSessionId(String newId, String oldId, boolean notifySessionListeners, boolean notifyContainerListeners) {
        Object[] listeners;
        Context context = this.manager.getContext();
        if (notifyContainerListeners) {
            context.fireContainerEvent(Context.CHANGE_SESSION_ID_EVENT, new String[]{oldId, newId});
        }
        if (notifySessionListeners && (listeners = context.getApplicationEventListeners()) != null && listeners.length > 0) {
            HttpSessionEvent event = new HttpSessionEvent(getSession());
            for (Object listener : listeners) {
                if (listener instanceof HttpSessionIdListener) {
                    HttpSessionIdListener idListener = (HttpSessionIdListener) listener;
                    try {
                        idListener.sessionIdChanged(event, oldId);
                    } catch (Throwable t) {
                        this.manager.getContext().getLogger().error(sm.getString("standardSession.sessionEvent"), t);
                    }
                }
            }
        }
    }

    @Override // org.apache.catalina.Session
    public long getThisAccessedTime() {
        if (!isValidInternal()) {
            throw new IllegalStateException(sm.getString("standardSession.getThisAccessedTime.ise"));
        }
        return this.thisAccessedTime;
    }

    @Override // org.apache.catalina.Session
    public long getThisAccessedTimeInternal() {
        return this.thisAccessedTime;
    }

    @Override // javax.servlet.http.HttpSession, org.apache.catalina.Session
    public long getLastAccessedTime() {
        if (!isValidInternal()) {
            throw new IllegalStateException(sm.getString("standardSession.getLastAccessedTime.ise"));
        }
        return this.lastAccessedTime;
    }

    @Override // org.apache.catalina.Session
    public long getLastAccessedTimeInternal() {
        return this.lastAccessedTime;
    }

    @Override // org.apache.catalina.Session
    public long getIdleTime() {
        if (!isValidInternal()) {
            throw new IllegalStateException(sm.getString("standardSession.getIdleTime.ise"));
        }
        return getIdleTimeInternal();
    }

    @Override // org.apache.catalina.Session
    public long getIdleTimeInternal() {
        long timeIdle;
        long timeNow = System.currentTimeMillis();
        if (LAST_ACCESS_AT_START) {
            timeIdle = timeNow - this.lastAccessedTime;
        } else {
            timeIdle = timeNow - this.thisAccessedTime;
        }
        return timeIdle;
    }

    @Override // org.apache.catalina.Session
    public Manager getManager() {
        return this.manager;
    }

    @Override // org.apache.catalina.Session
    public void setManager(Manager manager) {
        this.manager = manager;
    }

    @Override // javax.servlet.http.HttpSession, org.apache.catalina.Session
    public int getMaxInactiveInterval() {
        return this.maxInactiveInterval;
    }

    @Override // javax.servlet.http.HttpSession, org.apache.catalina.Session
    public void setMaxInactiveInterval(int interval) {
        this.maxInactiveInterval = interval;
    }

    @Override // org.apache.catalina.Session
    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    @Override // org.apache.catalina.Session
    public Principal getPrincipal() {
        return this.principal;
    }

    @Override // org.apache.catalina.Session
    public void setPrincipal(Principal principal) {
        Principal oldPrincipal = this.principal;
        this.principal = principal;
        this.support.firePropertyChange("principal", oldPrincipal, this.principal);
    }

    @Override // org.apache.catalina.Session
    public HttpSession getSession() {
        if (this.facade == null) {
            if (SecurityUtil.isPackageProtectionEnabled()) {
                this.facade = (StandardSessionFacade) AccessController.doPrivileged(new PrivilegedNewSessionFacade(this));
            } else {
                this.facade = new StandardSessionFacade(this);
            }
        }
        return this.facade;
    }

    @Override // org.apache.catalina.Session
    public boolean isValid() {
        if (!this.isValid) {
            return false;
        }
        if (this.expiring) {
            return true;
        }
        if (ACTIVITY_CHECK && this.accessCount.get() > 0) {
            return true;
        }
        if (this.maxInactiveInterval > 0) {
            int timeIdle = (int) (getIdleTimeInternal() / 1000);
            if (timeIdle >= this.maxInactiveInterval) {
                expire(true);
            }
        }
        return this.isValid;
    }

    @Override // org.apache.catalina.Session
    public void setValid(boolean isValid) {
        this.isValid = isValid;
    }

    @Override // org.apache.catalina.Session
    public void access() {
        this.thisAccessedTime = System.currentTimeMillis();
        if (ACTIVITY_CHECK) {
            this.accessCount.incrementAndGet();
        }
    }

    @Override // org.apache.catalina.Session
    public void endAccess() {
        this.isNew = false;
        if (LAST_ACCESS_AT_START) {
            this.lastAccessedTime = this.thisAccessedTime;
            this.thisAccessedTime = System.currentTimeMillis();
        } else {
            this.thisAccessedTime = System.currentTimeMillis();
            this.lastAccessedTime = this.thisAccessedTime;
        }
        if (ACTIVITY_CHECK) {
            this.accessCount.decrementAndGet();
        }
    }

    @Override // org.apache.catalina.Session
    public void addSessionListener(SessionListener listener) {
        this.listeners.add(listener);
    }

    @Override // org.apache.catalina.Session
    public void expire() {
        expire(true);
    }

    public void expire(boolean notify) {
        if (!this.isValid) {
            return;
        }
        synchronized (this) {
            if (this.expiring || !this.isValid) {
                return;
            }
            if (this.manager == null) {
                return;
            }
            this.expiring = true;
            Context context = this.manager.getContext();
            if (notify) {
                ClassLoader oldContextClassLoader = context.bind(Globals.IS_SECURITY_ENABLED, null);
                Object[] listeners = context.getApplicationLifecycleListeners();
                if (listeners != null && listeners.length > 0) {
                    HttpSessionEvent event = new HttpSessionEvent(getSession());
                    for (int i = 0; i < listeners.length; i++) {
                        int j = (listeners.length - 1) - i;
                        if (listeners[j] instanceof HttpSessionListener) {
                            HttpSessionListener listener = (HttpSessionListener) listeners[j];
                            try {
                                context.fireContainerEvent("beforeSessionDestroyed", listener);
                                listener.sessionDestroyed(event);
                                context.fireContainerEvent("afterSessionDestroyed", listener);
                            } catch (Throwable t) {
                                ExceptionUtils.handleThrowable(t);
                                try {
                                    context.fireContainerEvent("afterSessionDestroyed", listener);
                                } catch (Exception e) {
                                }
                                this.manager.getContext().getLogger().error(sm.getString("standardSession.sessionEvent"), t);
                            }
                        }
                    }
                }
                context.unbind(Globals.IS_SECURITY_ENABLED, oldContextClassLoader);
            }
            if (ACTIVITY_CHECK) {
                this.accessCount.set(0);
            }
            this.manager.remove(this, true);
            if (notify) {
                fireSessionEvent(Session.SESSION_DESTROYED_EVENT, null);
            }
            if (this.principal instanceof TomcatPrincipal) {
                TomcatPrincipal gp = (TomcatPrincipal) this.principal;
                try {
                    gp.logout();
                } catch (Exception e2) {
                    this.manager.getContext().getLogger().error(sm.getString("standardSession.logoutfail"), e2);
                }
            }
            setValid(false);
            this.expiring = false;
            String[] keys = keys();
            ClassLoader oldContextClassLoader2 = context.bind(Globals.IS_SECURITY_ENABLED, null);
            for (String str : keys) {
                removeAttributeInternal(str, notify);
            }
            context.unbind(Globals.IS_SECURITY_ENABLED, oldContextClassLoader2);
        }
    }

    public void passivate() {
        fireSessionEvent(Session.SESSION_PASSIVATED_EVENT, null);
        HttpSessionEvent event = null;
        String[] keys = keys();
        for (String str : keys) {
            Object attribute = this.attributes.get(str);
            if (attribute instanceof HttpSessionActivationListener) {
                if (event == null) {
                    event = new HttpSessionEvent(getSession());
                }
                try {
                    ((HttpSessionActivationListener) attribute).sessionWillPassivate(event);
                } catch (Throwable t) {
                    ExceptionUtils.handleThrowable(t);
                    this.manager.getContext().getLogger().error(sm.getString("standardSession.attributeEvent"), t);
                }
            }
        }
    }

    public void activate() {
        if (ACTIVITY_CHECK) {
            this.accessCount = new AtomicInteger();
        }
        fireSessionEvent(Session.SESSION_ACTIVATED_EVENT, null);
        HttpSessionEvent event = null;
        String[] keys = keys();
        for (String str : keys) {
            Object attribute = this.attributes.get(str);
            if (attribute instanceof HttpSessionActivationListener) {
                if (event == null) {
                    event = new HttpSessionEvent(getSession());
                }
                try {
                    ((HttpSessionActivationListener) attribute).sessionDidActivate(event);
                } catch (Throwable t) {
                    ExceptionUtils.handleThrowable(t);
                    this.manager.getContext().getLogger().error(sm.getString("standardSession.attributeEvent"), t);
                }
            }
        }
    }

    @Override // org.apache.catalina.Session
    public Object getNote(String name) {
        return this.notes.get(name);
    }

    @Override // org.apache.catalina.Session
    public Iterator<String> getNoteNames() {
        return this.notes.keySet().iterator();
    }

    @Override // org.apache.catalina.Session
    public void recycle() {
        this.attributes.clear();
        setAuthType(null);
        this.creationTime = 0L;
        this.expiring = false;
        this.id = null;
        this.lastAccessedTime = 0L;
        this.maxInactiveInterval = -1;
        this.notes.clear();
        setPrincipal(null);
        this.isNew = false;
        this.isValid = false;
        this.manager = null;
    }

    @Override // org.apache.catalina.Session
    public void removeNote(String name) {
        this.notes.remove(name);
    }

    @Override // org.apache.catalina.Session
    public void removeSessionListener(SessionListener listener) {
        this.listeners.remove(listener);
    }

    @Override // org.apache.catalina.Session
    public void setNote(String name, Object value) {
        this.notes.put(name, value);
    }

    public String toString() {
        return "StandardSession[" + this.id + "]";
    }

    public void readObjectData(ObjectInputStream stream) throws ClassNotFoundException, IOException {
        doReadObject(stream);
    }

    public void writeObjectData(ObjectOutputStream stream) throws IOException {
        doWriteObject(stream);
    }

    @Override // javax.servlet.http.HttpSession, org.apache.catalina.Session
    public long getCreationTime() {
        if (!isValidInternal()) {
            throw new IllegalStateException(sm.getString("standardSession.getCreationTime.ise"));
        }
        return this.creationTime;
    }

    @Override // org.apache.catalina.Session
    public long getCreationTimeInternal() {
        return this.creationTime;
    }

    @Override // javax.servlet.http.HttpSession
    public ServletContext getServletContext() {
        if (this.manager == null) {
            return null;
        }
        Context context = this.manager.getContext();
        return context.getServletContext();
    }

    @Override // javax.servlet.http.HttpSession
    @Deprecated
    public HttpSessionContext getSessionContext() {
        if (sessionContext == null) {
            sessionContext = new StandardSessionContext();
        }
        return sessionContext;
    }

    @Override // javax.servlet.http.HttpSession
    public Object getAttribute(String name) {
        if (!isValidInternal()) {
            throw new IllegalStateException(sm.getString("standardSession.getAttribute.ise"));
        }
        if (name == null) {
            return null;
        }
        return this.attributes.get(name);
    }

    @Override // javax.servlet.http.HttpSession
    public Enumeration<String> getAttributeNames() {
        if (!isValidInternal()) {
            throw new IllegalStateException(sm.getString("standardSession.getAttributeNames.ise"));
        }
        Set<String> names = new HashSet<>();
        names.addAll(this.attributes.keySet());
        return Collections.enumeration(names);
    }

    @Override // javax.servlet.http.HttpSession
    @Deprecated
    public Object getValue(String name) {
        return getAttribute(name);
    }

    @Override // javax.servlet.http.HttpSession
    @Deprecated
    public String[] getValueNames() {
        if (!isValidInternal()) {
            throw new IllegalStateException(sm.getString("standardSession.getValueNames.ise"));
        }
        return keys();
    }

    @Override // javax.servlet.http.HttpSession
    public void invalidate() {
        if (!isValidInternal()) {
            throw new IllegalStateException(sm.getString("standardSession.invalidate.ise"));
        }
        expire();
    }

    @Override // javax.servlet.http.HttpSession
    public boolean isNew() {
        if (!isValidInternal()) {
            throw new IllegalStateException(sm.getString("standardSession.isNew.ise"));
        }
        return this.isNew;
    }

    @Override // javax.servlet.http.HttpSession
    @Deprecated
    public void putValue(String name, Object value) {
        setAttribute(name, value);
    }

    @Override // javax.servlet.http.HttpSession
    public void removeAttribute(String name) {
        removeAttribute(name, true);
    }

    public void removeAttribute(String name, boolean notify) {
        if (!isValidInternal()) {
            throw new IllegalStateException(sm.getString("standardSession.removeAttribute.ise"));
        }
        removeAttributeInternal(name, notify);
    }

    @Override // javax.servlet.http.HttpSession
    @Deprecated
    public void removeValue(String name) {
        removeAttribute(name);
    }

    @Override // javax.servlet.http.HttpSession
    public void setAttribute(String name, Object value) {
        setAttribute(name, value, true);
    }

    /* JADX WARN: Code restructure failed: missing block: B:167:0x01a8, code lost:
        if (r9.manager.getNotifyAttributeListenerOnUnchangedValue() != false) goto L67;
     */
    /* JADX WARN: Code restructure failed: missing block: B:184:0x0233, code lost:
        if (r9.manager.getNotifyAttributeListenerOnUnchangedValue() != false) goto L84;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void setAttribute(java.lang.String r10, java.lang.Object r11, boolean r12) {
        /*
            Method dump skipped, instructions count: 632
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.catalina.session.StandardSession.setAttribute(java.lang.String, java.lang.Object, boolean):void");
    }

    public boolean isValidInternal() {
        return this.isValid;
    }

    @Override // org.apache.catalina.Session
    public boolean isAttributeDistributable(String name, Object value) {
        return value instanceof Serializable;
    }

    protected void doReadObject(ObjectInputStream stream) throws ClassNotFoundException, IOException {
        this.authType = null;
        this.creationTime = ((Long) stream.readObject()).longValue();
        this.lastAccessedTime = ((Long) stream.readObject()).longValue();
        this.maxInactiveInterval = ((Integer) stream.readObject()).intValue();
        this.isNew = ((Boolean) stream.readObject()).booleanValue();
        this.isValid = ((Boolean) stream.readObject()).booleanValue();
        this.thisAccessedTime = ((Long) stream.readObject()).longValue();
        this.principal = null;
        this.id = (String) stream.readObject();
        if (this.manager.getContext().getLogger().isDebugEnabled()) {
            this.manager.getContext().getLogger().debug("readObject() loading session " + this.id);
        }
        if (this.attributes == null) {
            this.attributes = new ConcurrentHashMap();
        }
        int n = ((Integer) stream.readObject()).intValue();
        boolean isValidSave = this.isValid;
        this.isValid = true;
        for (int i = 0; i < n; i++) {
            String name = (String) stream.readObject();
            try {
                Object value = stream.readObject();
                if (this.manager.getContext().getLogger().isDebugEnabled()) {
                    this.manager.getContext().getLogger().debug("  loading attribute '" + name + "' with value '" + value + "'");
                }
                if (!exclude(name, value)) {
                    this.attributes.put(name, value);
                }
            } catch (WriteAbortedException wae) {
                if (wae.getCause() instanceof NotSerializableException) {
                    String msg = sm.getString("standardSession.notDeserializable", name, this.id);
                    if (this.manager.getContext().getLogger().isDebugEnabled()) {
                        this.manager.getContext().getLogger().debug(msg, wae);
                    } else {
                        this.manager.getContext().getLogger().warn(msg);
                    }
                } else {
                    throw wae;
                }
            }
        }
        this.isValid = isValidSave;
        if (this.listeners == null) {
            this.listeners = new ArrayList<>();
        }
        if (this.notes == null) {
            this.notes = new Hashtable();
        }
    }

    protected void doWriteObject(ObjectOutputStream stream) throws IOException {
        stream.writeObject(Long.valueOf(this.creationTime));
        stream.writeObject(Long.valueOf(this.lastAccessedTime));
        stream.writeObject(Integer.valueOf(this.maxInactiveInterval));
        stream.writeObject(Boolean.valueOf(this.isNew));
        stream.writeObject(Boolean.valueOf(this.isValid));
        stream.writeObject(Long.valueOf(this.thisAccessedTime));
        stream.writeObject(this.id);
        if (this.manager.getContext().getLogger().isDebugEnabled()) {
            this.manager.getContext().getLogger().debug("writeObject() storing session " + this.id);
        }
        String[] keys = keys();
        List<String> saveNames = new ArrayList<>();
        List<Object> saveValues = new ArrayList<>();
        for (int i = 0; i < keys.length; i++) {
            Object value = this.attributes.get(keys[i]);
            if (value != null) {
                if (isAttributeDistributable(keys[i], value) && !exclude(keys[i], value)) {
                    saveNames.add(keys[i]);
                    saveValues.add(value);
                } else {
                    removeAttributeInternal(keys[i], true);
                }
            }
        }
        int n = saveNames.size();
        stream.writeObject(Integer.valueOf(n));
        for (int i2 = 0; i2 < n; i2++) {
            stream.writeObject(saveNames.get(i2));
            try {
                stream.writeObject(saveValues.get(i2));
                if (this.manager.getContext().getLogger().isDebugEnabled()) {
                    this.manager.getContext().getLogger().debug("  storing attribute '" + saveNames.get(i2) + "' with value '" + saveValues.get(i2) + "'");
                }
            } catch (NotSerializableException e) {
                this.manager.getContext().getLogger().warn(sm.getString("standardSession.notSerializable", saveNames.get(i2), this.id), e);
            }
        }
    }

    protected boolean exclude(String name, Object value) {
        if (Constants.excludedAttributeNames.contains(name)) {
            return true;
        }
        Manager manager = getManager();
        return (manager == null || manager.willAttributeDistribute(name, value)) ? false : true;
    }

    public void fireSessionEvent(String type, Object data) {
        SessionListener[] list;
        if (this.listeners.size() < 1) {
            return;
        }
        SessionEvent event = new SessionEvent(this, type, data);
        SessionListener[] list2 = new SessionListener[0];
        synchronized (this.listeners) {
            list = (SessionListener[]) this.listeners.toArray(list2);
        }
        for (SessionListener sessionListener : list) {
            sessionListener.sessionEvent(event);
        }
    }

    protected String[] keys() {
        return (String[]) this.attributes.keySet().toArray(EMPTY_ARRAY);
    }

    protected void removeAttributeInternal(String name, boolean notify) {
        if (name == null) {
            return;
        }
        Object value = this.attributes.remove(name);
        if (!notify || value == null) {
            return;
        }
        HttpSessionBindingEvent event = null;
        if (value instanceof HttpSessionBindingListener) {
            event = new HttpSessionBindingEvent(getSession(), name, value);
            ((HttpSessionBindingListener) value).valueUnbound(event);
        }
        Context context = this.manager.getContext();
        Object[] listeners = context.getApplicationEventListeners();
        if (listeners == null) {
            return;
        }
        for (int i = 0; i < listeners.length; i++) {
            if (listeners[i] instanceof HttpSessionAttributeListener) {
                HttpSessionAttributeListener listener = (HttpSessionAttributeListener) listeners[i];
                try {
                    context.fireContainerEvent("beforeSessionAttributeRemoved", listener);
                    if (event == null) {
                        event = new HttpSessionBindingEvent(getSession(), name, value);
                    }
                    listener.attributeRemoved(event);
                    context.fireContainerEvent("afterSessionAttributeRemoved", listener);
                } catch (Throwable t) {
                    ExceptionUtils.handleThrowable(t);
                    try {
                        context.fireContainerEvent("afterSessionAttributeRemoved", listener);
                    } catch (Exception e) {
                    }
                    this.manager.getContext().getLogger().error(sm.getString("standardSession.attributeEvent"), t);
                }
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/session/StandardSession$PrivilegedNewSessionFacade.class */
    public static class PrivilegedNewSessionFacade implements PrivilegedAction<StandardSessionFacade> {
        private final HttpSession session;

        public PrivilegedNewSessionFacade(HttpSession session) {
            this.session = session;
        }

        @Override // java.security.PrivilegedAction
        public StandardSessionFacade run() {
            return new StandardSessionFacade(this.session);
        }
    }
}