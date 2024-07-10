package org.apache.catalina.session;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Session;
import org.apache.catalina.Store;
import org.apache.catalina.StoreManager;
import org.apache.catalina.security.SecurityUtil;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/session/PersistentManagerBase.class */
public abstract class PersistentManagerBase extends ManagerBase implements StoreManager {
    private static final String name = "PersistentManagerBase";
    private static final String PERSISTED_LAST_ACCESSED_TIME = "org.apache.catalina.session.PersistentManagerBase.persistedLastAccessedTime";
    private final Log log = LogFactory.getLog(PersistentManagerBase.class);
    protected Store store = null;
    protected boolean saveOnRestart = true;
    protected int maxIdleBackup = -1;
    protected int minIdleSwap = -1;
    protected int maxIdleSwap = -1;
    private final Map<String, Object> sessionSwapInLocks = new HashMap();
    private final ThreadLocal<Session> sessionToSwapIn = new ThreadLocal<>();

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/session/PersistentManagerBase$PrivilegedStoreClear.class */
    private class PrivilegedStoreClear implements PrivilegedExceptionAction<Void> {
        PrivilegedStoreClear() {
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedExceptionAction
        public Void run() throws Exception {
            PersistentManagerBase.this.store.clear();
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/session/PersistentManagerBase$PrivilegedStoreRemove.class */
    public class PrivilegedStoreRemove implements PrivilegedExceptionAction<Void> {
        private String id;

        PrivilegedStoreRemove(String id) {
            this.id = id;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedExceptionAction
        public Void run() throws Exception {
            PersistentManagerBase.this.store.remove(this.id);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/session/PersistentManagerBase$PrivilegedStoreLoad.class */
    public class PrivilegedStoreLoad implements PrivilegedExceptionAction<Session> {
        private String id;

        PrivilegedStoreLoad(String id) {
            this.id = id;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedExceptionAction
        public Session run() throws Exception {
            return PersistentManagerBase.this.store.load(this.id);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/session/PersistentManagerBase$PrivilegedStoreSave.class */
    public class PrivilegedStoreSave implements PrivilegedExceptionAction<Void> {
        private Session session;

        PrivilegedStoreSave(Session session) {
            this.session = session;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedExceptionAction
        public Void run() throws Exception {
            PersistentManagerBase.this.store.save(this.session);
            return null;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/session/PersistentManagerBase$PrivilegedStoreKeys.class */
    private class PrivilegedStoreKeys implements PrivilegedExceptionAction<String[]> {
        PrivilegedStoreKeys() {
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedExceptionAction
        public String[] run() throws Exception {
            return PersistentManagerBase.this.store.keys();
        }
    }

    public int getMaxIdleBackup() {
        return this.maxIdleBackup;
    }

    public void setMaxIdleBackup(int backup) {
        if (backup == this.maxIdleBackup) {
            return;
        }
        int oldBackup = this.maxIdleBackup;
        this.maxIdleBackup = backup;
        this.support.firePropertyChange("maxIdleBackup", Integer.valueOf(oldBackup), Integer.valueOf(this.maxIdleBackup));
    }

    public int getMaxIdleSwap() {
        return this.maxIdleSwap;
    }

    public void setMaxIdleSwap(int max) {
        if (max == this.maxIdleSwap) {
            return;
        }
        int oldMaxIdleSwap = this.maxIdleSwap;
        this.maxIdleSwap = max;
        this.support.firePropertyChange("maxIdleSwap", Integer.valueOf(oldMaxIdleSwap), Integer.valueOf(this.maxIdleSwap));
    }

    public int getMinIdleSwap() {
        return this.minIdleSwap;
    }

    public void setMinIdleSwap(int min) {
        if (this.minIdleSwap == min) {
            return;
        }
        int oldMinIdleSwap = this.minIdleSwap;
        this.minIdleSwap = min;
        this.support.firePropertyChange("minIdleSwap", Integer.valueOf(oldMinIdleSwap), Integer.valueOf(this.minIdleSwap));
    }

    public boolean isLoaded(String id) {
        try {
            if (super.findSession(id) != null) {
                return true;
            }
            return false;
        } catch (IOException e) {
            this.log.error("checking isLoaded for id, " + id + ", " + e.getMessage(), e);
            return false;
        }
    }

    @Override // org.apache.catalina.session.ManagerBase
    public String getName() {
        return name;
    }

    public void setStore(Store store) {
        this.store = store;
        store.setManager(this);
    }

    @Override // org.apache.catalina.StoreManager
    public Store getStore() {
        return this.store;
    }

    public boolean getSaveOnRestart() {
        return this.saveOnRestart;
    }

    public void setSaveOnRestart(boolean saveOnRestart) {
        if (saveOnRestart == this.saveOnRestart) {
            return;
        }
        boolean oldSaveOnRestart = this.saveOnRestart;
        this.saveOnRestart = saveOnRestart;
        this.support.firePropertyChange("saveOnRestart", Boolean.valueOf(oldSaveOnRestart), Boolean.valueOf(this.saveOnRestart));
    }

    public void clearStore() {
        if (this.store == null) {
            return;
        }
        try {
            if (SecurityUtil.isPackageProtectionEnabled()) {
                try {
                    AccessController.doPrivileged(new PrivilegedStoreClear());
                } catch (PrivilegedActionException ex) {
                    Exception exception = ex.getException();
                    this.log.error("Exception clearing the Store: " + exception, exception);
                }
            } else {
                this.store.clear();
            }
        } catch (IOException e) {
            this.log.error("Exception clearing the Store: " + e, e);
        }
    }

    @Override // org.apache.catalina.session.ManagerBase
    public void processExpires() {
        long timeNow = System.currentTimeMillis();
        Session[] sessions = findSessions();
        int expireHere = 0;
        if (this.log.isDebugEnabled()) {
            this.log.debug("Start expire sessions " + getName() + " at " + timeNow + " sessioncount " + sessions.length);
        }
        for (Session session : sessions) {
            if (!session.isValid()) {
                this.expiredSessions.incrementAndGet();
                expireHere++;
            }
        }
        processPersistenceChecks();
        if (getStore() instanceof StoreBase) {
            ((StoreBase) getStore()).processExpires();
        }
        long timeEnd = System.currentTimeMillis();
        if (this.log.isDebugEnabled()) {
            this.log.debug("End expire sessions " + getName() + " processingTime " + (timeEnd - timeNow) + " expired sessions: " + expireHere);
        }
        this.processingTime += timeEnd - timeNow;
    }

    public void processPersistenceChecks() {
        processMaxIdleSwaps();
        processMaxActiveSwaps();
        processMaxIdleBackups();
    }

    @Override // org.apache.catalina.session.ManagerBase, org.apache.catalina.Manager
    public Session findSession(String id) throws IOException {
        Session session = super.findSession(id);
        if (session != null) {
            synchronized (session) {
                session = super.findSession(session.getIdInternal());
                if (session != null) {
                    session.access();
                    session.endAccess();
                }
            }
        }
        if (session != null) {
            return session;
        }
        return swapIn(id);
    }

    @Override // org.apache.catalina.StoreManager
    public void removeSuper(Session session) {
        super.remove(session, false);
    }

    @Override // org.apache.catalina.Manager
    public void load() {
        String[] ids;
        this.sessions.clear();
        if (this.store == null) {
            return;
        }
        try {
            if (SecurityUtil.isPackageProtectionEnabled()) {
                try {
                    ids = (String[]) AccessController.doPrivileged(new PrivilegedStoreKeys());
                } catch (PrivilegedActionException ex) {
                    Exception exception = ex.getException();
                    this.log.error("Exception in the Store during load: " + exception, exception);
                    return;
                }
            } else {
                ids = this.store.keys();
            }
            int n = ids.length;
            if (n == 0) {
                return;
            }
            if (this.log.isDebugEnabled()) {
                this.log.debug(sm.getString("persistentManager.loading", String.valueOf(n)));
            }
            for (String str : ids) {
                try {
                    swapIn(str);
                } catch (IOException e) {
                    this.log.error("Failed load session from store, " + e.getMessage(), e);
                }
            }
        } catch (IOException e2) {
            this.log.error("Can't load sessions from store, " + e2.getMessage(), e2);
        }
    }

    @Override // org.apache.catalina.session.ManagerBase, org.apache.catalina.Manager
    public void remove(Session session, boolean update) {
        super.remove(session, update);
        if (this.store != null) {
            removeSession(session.getIdInternal());
        }
    }

    protected void removeSession(String id) {
        try {
            if (SecurityUtil.isPackageProtectionEnabled()) {
                try {
                    AccessController.doPrivileged(new PrivilegedStoreRemove(id));
                } catch (PrivilegedActionException ex) {
                    Exception exception = ex.getException();
                    this.log.error("Exception in the Store during removeSession: " + exception, exception);
                }
            } else {
                this.store.remove(id);
            }
        } catch (IOException e) {
            this.log.error("Exception removing session  " + e.getMessage(), e);
        }
    }

    @Override // org.apache.catalina.Manager
    public void unload() {
        Session[] sessions;
        int n;
        if (this.store == null || (n = (sessions = findSessions()).length) == 0) {
            return;
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug(sm.getString("persistentManager.unloading", String.valueOf(n)));
        }
        for (int i = 0; i < n; i++) {
            try {
                swapOut(sessions[i]);
            } catch (IOException e) {
            }
        }
    }

    @Override // org.apache.catalina.DistributedManager
    public int getActiveSessionsFull() {
        int result = getActiveSessions();
        try {
            result += getStore().getSize();
        } catch (IOException e) {
            this.log.warn(sm.getString("persistentManager.storeSizeException"));
        }
        return result;
    }

    @Override // org.apache.catalina.DistributedManager
    public Set<String> getSessionIdsFull() {
        Set<String> sessionIds = new HashSet<>();
        sessionIds.addAll(this.sessions.keySet());
        try {
            String[] storeKeys = getStore().keys();
            for (String storeKey : storeKeys) {
                sessionIds.add(storeKey);
            }
        } catch (IOException e) {
            this.log.warn(sm.getString("persistentManager.storeKeysException"));
        }
        return sessionIds;
    }

    protected Session swapIn(String id) throws IOException {
        Object swapInLock;
        Session session;
        if (this.store == null) {
            return null;
        }
        synchronized (this) {
            swapInLock = this.sessionSwapInLocks.get(id);
            if (swapInLock == null) {
                swapInLock = new Object();
                this.sessionSwapInLocks.put(id, swapInLock);
            }
        }
        synchronized (swapInLock) {
            session = this.sessions.get(id);
            if (session == null) {
                Session currentSwapInSession = this.sessionToSwapIn.get();
                if (currentSwapInSession == null || !id.equals(currentSwapInSession.getId())) {
                    session = loadSessionFromStore(id);
                    this.sessionToSwapIn.set(session);
                    if (session != null && !session.isValid()) {
                        this.log.error(sm.getString("persistentManager.swapInInvalid", id));
                        session.expire();
                        removeSession(id);
                        session = null;
                    }
                    if (session != null) {
                        reactivateLoadedSession(id, session);
                    }
                }
                this.sessionToSwapIn.remove();
            }
        }
        synchronized (this) {
            this.sessionSwapInLocks.remove(id);
        }
        return session;
    }

    private void reactivateLoadedSession(String id, Session session) {
        if (this.log.isDebugEnabled()) {
            this.log.debug(sm.getString("persistentManager.swapIn", id));
        }
        session.setManager(this);
        ((StandardSession) session).tellNew();
        add(session);
        ((StandardSession) session).activate();
        session.access();
        session.endAccess();
    }

    private Session loadSessionFromStore(String id) throws IOException {
        try {
            if (SecurityUtil.isPackageProtectionEnabled()) {
                return securedStoreLoad(id);
            }
            return this.store.load(id);
        } catch (ClassNotFoundException e) {
            String msg = sm.getString("persistentManager.deserializeError", id);
            this.log.error(msg, e);
            throw new IllegalStateException(msg, e);
        }
    }

    private Session securedStoreLoad(String id) throws IOException, ClassNotFoundException {
        try {
            return (Session) AccessController.doPrivileged(new PrivilegedStoreLoad(id));
        } catch (PrivilegedActionException ex) {
            Exception e = ex.getException();
            this.log.error(sm.getString("persistentManager.swapInException", id), e);
            if (e instanceof IOException) {
                throw ((IOException) e);
            }
            if (e instanceof ClassNotFoundException) {
                throw ((ClassNotFoundException) e);
            }
            return null;
        }
    }

    protected void swapOut(Session session) throws IOException {
        if (this.store == null || !session.isValid()) {
            return;
        }
        ((StandardSession) session).passivate();
        writeSession(session);
        super.remove(session, true);
        session.recycle();
    }

    protected void writeSession(Session session) throws IOException {
        if (this.store == null || !session.isValid()) {
            return;
        }
        try {
            if (SecurityUtil.isPackageProtectionEnabled()) {
                try {
                    AccessController.doPrivileged(new PrivilegedStoreSave(session));
                } catch (PrivilegedActionException ex) {
                    Exception exception = ex.getException();
                    if (exception instanceof IOException) {
                        throw ((IOException) exception);
                    }
                    this.log.error("Exception in the Store during writeSession: " + exception, exception);
                }
            } else {
                this.store.save(session);
            }
        } catch (IOException e) {
            this.log.error(sm.getString("persistentManager.serializeError", session.getIdInternal(), e));
            throw e;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.session.ManagerBase, org.apache.catalina.util.LifecycleBase
    public synchronized void startInternal() throws LifecycleException {
        super.startInternal();
        if (this.store == null) {
            this.log.error("No Store configured, persistence disabled");
        } else if (this.store instanceof Lifecycle) {
            ((Lifecycle) this.store).start();
        }
        setState(LifecycleState.STARTING);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.session.ManagerBase, org.apache.catalina.util.LifecycleBase
    public synchronized void stopInternal() throws LifecycleException {
        if (this.log.isDebugEnabled()) {
            this.log.debug("Stopping");
        }
        setState(LifecycleState.STOPPING);
        if (getStore() != null && this.saveOnRestart) {
            unload();
        } else {
            Session[] sessions = findSessions();
            for (Session session : sessions) {
                StandardSession session2 = (StandardSession) session;
                if (session2.isValid()) {
                    session2.expire();
                }
            }
        }
        if (getStore() instanceof Lifecycle) {
            ((Lifecycle) getStore()).stop();
        }
        super.stopInternal();
    }

    protected void processMaxIdleSwaps() {
        if (!getState().isAvailable() || this.maxIdleSwap < 0) {
            return;
        }
        Session[] sessions = findSessions();
        if (this.maxIdleSwap >= 0) {
            for (Session session : sessions) {
                StandardSession session2 = (StandardSession) session;
                synchronized (session2) {
                    if (session2.isValid()) {
                        int timeIdle = (int) (session2.getIdleTimeInternal() / 1000);
                        if (timeIdle >= this.maxIdleSwap && timeIdle >= this.minIdleSwap) {
                            if (session2.accessCount == null || session2.accessCount.get() <= 0) {
                                if (this.log.isDebugEnabled()) {
                                    this.log.debug(sm.getString("persistentManager.swapMaxIdle", session2.getIdInternal(), Integer.valueOf(timeIdle)));
                                }
                                try {
                                    swapOut(session2);
                                } catch (IOException e) {
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    protected void processMaxActiveSwaps() {
        if (!getState().isAvailable() || this.minIdleSwap < 0 || getMaxActiveSessions() < 0) {
            return;
        }
        Session[] sessions = findSessions();
        int limit = (int) (getMaxActiveSessions() * 0.9d);
        if (limit >= sessions.length) {
            return;
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug(sm.getString("persistentManager.tooManyActive", Integer.valueOf(sessions.length)));
        }
        int toswap = sessions.length - limit;
        for (int i = 0; i < sessions.length && toswap > 0; i++) {
            StandardSession session = (StandardSession) sessions[i];
            synchronized (session) {
                int timeIdle = (int) (session.getIdleTimeInternal() / 1000);
                if (timeIdle >= this.minIdleSwap) {
                    if (session.accessCount == null || session.accessCount.get() <= 0) {
                        if (this.log.isDebugEnabled()) {
                            this.log.debug(sm.getString("persistentManager.swapTooManyActive", session.getIdInternal(), Integer.valueOf(timeIdle)));
                        }
                        try {
                            swapOut(session);
                        } catch (IOException e) {
                        }
                        toswap--;
                    }
                }
            }
        }
    }

    protected void processMaxIdleBackups() {
        if (!getState().isAvailable() || this.maxIdleBackup < 0) {
            return;
        }
        Session[] sessions = findSessions();
        if (this.maxIdleBackup >= 0) {
            for (Session session : sessions) {
                StandardSession session2 = (StandardSession) session;
                synchronized (session2) {
                    if (session2.isValid()) {
                        long lastAccessedTime = session2.getLastAccessedTimeInternal();
                        Long persistedLastAccessedTime = (Long) session2.getNote(PERSISTED_LAST_ACCESSED_TIME);
                        if (persistedLastAccessedTime == null || lastAccessedTime != persistedLastAccessedTime.longValue()) {
                            int timeIdle = (int) (session2.getIdleTimeInternal() / 1000);
                            if (timeIdle >= this.maxIdleBackup) {
                                if (this.log.isDebugEnabled()) {
                                    this.log.debug(sm.getString("persistentManager.backupMaxIdle", session2.getIdInternal(), Integer.valueOf(timeIdle)));
                                }
                                try {
                                    writeSession(session2);
                                } catch (IOException e) {
                                }
                                session2.setNote(PERSISTED_LAST_ACCESSED_TIME, Long.valueOf(lastAccessedTime));
                            }
                        }
                    }
                }
            }
        }
    }
}