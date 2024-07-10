package org.apache.catalina.session;

import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.net.ssl.SSL;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Globals;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.SessionIdGenerator;
import org.apache.catalina.util.LifecycleMBeanBase;
import org.apache.catalina.util.SessionIdGeneratorBase;
import org.apache.catalina.util.StandardSessionIdGenerator;
import org.apache.catalina.util.ToStringUtil;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/session/ManagerBase.class */
public abstract class ManagerBase extends LifecycleMBeanBase implements Manager {
    private Context context;
    private static final String name = "ManagerBase";
    protected volatile int sessionMaxAliveTime;
    protected static final int TIMING_STATS_CACHE_SIZE = 100;
    protected static final StringManager sm = StringManager.getManager(ManagerBase.class);
    private Pattern sessionAttributeNamePattern;
    private Pattern sessionAttributeValueClassNamePattern;
    private boolean warnOnSessionAttributeFilterFailure;
    private boolean notifyBindingListenerOnUnchangedValue;
    private final Log log = LogFactory.getLog(ManagerBase.class);
    protected String secureRandomClass = null;
    protected String secureRandomAlgorithm = SSL.DEFAULT_SECURE_RANDOM_ALGORITHM;
    protected String secureRandomProvider = null;
    protected SessionIdGenerator sessionIdGenerator = null;
    protected Class<? extends SessionIdGenerator> sessionIdGeneratorClass = null;
    private final Object sessionMaxAliveTimeUpdateLock = new Object();
    protected final Deque<SessionTiming> sessionCreationTiming = new LinkedList();
    protected final Deque<SessionTiming> sessionExpirationTiming = new LinkedList();
    protected final AtomicLong expiredSessions = new AtomicLong(0);
    protected Map<String, Session> sessions = new ConcurrentHashMap();
    protected long sessionCounter = 0;
    protected volatile int maxActive = 0;
    private final Object maxActiveUpdateLock = new Object();
    protected int maxActiveSessions = -1;
    protected int rejectedSessions = 0;
    protected volatile int duplicates = 0;
    protected long processingTime = 0;
    private int count = 0;
    protected int processExpiresFrequency = 6;
    protected final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private boolean notifyAttributeListenerOnUnchangedValue = true;

    public ManagerBase() {
        if (Globals.IS_SECURITY_ENABLED) {
            setSessionAttributeValueClassNameFilter("java\\.lang\\.(?:Boolean|Integer|Long|Number|String)");
            setWarnOnSessionAttributeFilterFailure(true);
        }
    }

    @Override // org.apache.catalina.Manager
    public boolean getNotifyAttributeListenerOnUnchangedValue() {
        return this.notifyAttributeListenerOnUnchangedValue;
    }

    @Override // org.apache.catalina.Manager
    public void setNotifyAttributeListenerOnUnchangedValue(boolean notifyAttributeListenerOnUnchangedValue) {
        this.notifyAttributeListenerOnUnchangedValue = notifyAttributeListenerOnUnchangedValue;
    }

    @Override // org.apache.catalina.Manager
    public boolean getNotifyBindingListenerOnUnchangedValue() {
        return this.notifyBindingListenerOnUnchangedValue;
    }

    @Override // org.apache.catalina.Manager
    public void setNotifyBindingListenerOnUnchangedValue(boolean notifyBindingListenerOnUnchangedValue) {
        this.notifyBindingListenerOnUnchangedValue = notifyBindingListenerOnUnchangedValue;
    }

    public String getSessionAttributeNameFilter() {
        if (this.sessionAttributeNamePattern == null) {
            return null;
        }
        return this.sessionAttributeNamePattern.toString();
    }

    public void setSessionAttributeNameFilter(String sessionAttributeNameFilter) throws PatternSyntaxException {
        if (sessionAttributeNameFilter == null || sessionAttributeNameFilter.length() == 0) {
            this.sessionAttributeNamePattern = null;
        } else {
            this.sessionAttributeNamePattern = Pattern.compile(sessionAttributeNameFilter);
        }
    }

    protected Pattern getSessionAttributeNamePattern() {
        return this.sessionAttributeNamePattern;
    }

    public String getSessionAttributeValueClassNameFilter() {
        if (this.sessionAttributeValueClassNamePattern == null) {
            return null;
        }
        return this.sessionAttributeValueClassNamePattern.toString();
    }

    public Pattern getSessionAttributeValueClassNamePattern() {
        return this.sessionAttributeValueClassNamePattern;
    }

    public void setSessionAttributeValueClassNameFilter(String sessionAttributeValueClassNameFilter) throws PatternSyntaxException {
        if (sessionAttributeValueClassNameFilter == null || sessionAttributeValueClassNameFilter.length() == 0) {
            this.sessionAttributeValueClassNamePattern = null;
        } else {
            this.sessionAttributeValueClassNamePattern = Pattern.compile(sessionAttributeValueClassNameFilter);
        }
    }

    public boolean getWarnOnSessionAttributeFilterFailure() {
        return this.warnOnSessionAttributeFilterFailure;
    }

    public void setWarnOnSessionAttributeFilterFailure(boolean warnOnSessionAttributeFilterFailure) {
        this.warnOnSessionAttributeFilterFailure = warnOnSessionAttributeFilterFailure;
    }

    @Override // org.apache.catalina.Manager
    public Context getContext() {
        return this.context;
    }

    @Override // org.apache.catalina.Manager
    public void setContext(Context context) {
        if (this.context == context) {
            return;
        }
        if (!getState().equals(LifecycleState.NEW)) {
            throw new IllegalStateException(sm.getString("managerBase.setContextNotNew"));
        }
        Context oldContext = this.context;
        this.context = context;
        this.support.firePropertyChange(CoreConstants.CONTEXT_SCOPE_VALUE, oldContext, this.context);
    }

    public String getClassName() {
        return getClass().getName();
    }

    @Override // org.apache.catalina.Manager
    public SessionIdGenerator getSessionIdGenerator() {
        if (this.sessionIdGenerator != null) {
            return this.sessionIdGenerator;
        }
        if (this.sessionIdGeneratorClass != null) {
            try {
                this.sessionIdGenerator = this.sessionIdGeneratorClass.getConstructor(new Class[0]).newInstance(new Object[0]);
                return this.sessionIdGenerator;
            } catch (ReflectiveOperationException e) {
                return null;
            }
        }
        return null;
    }

    @Override // org.apache.catalina.Manager
    public void setSessionIdGenerator(SessionIdGenerator sessionIdGenerator) {
        this.sessionIdGenerator = sessionIdGenerator;
        this.sessionIdGeneratorClass = sessionIdGenerator.getClass();
    }

    public String getName() {
        return name;
    }

    public String getSecureRandomClass() {
        return this.secureRandomClass;
    }

    public void setSecureRandomClass(String secureRandomClass) {
        String oldSecureRandomClass = this.secureRandomClass;
        this.secureRandomClass = secureRandomClass;
        this.support.firePropertyChange("secureRandomClass", oldSecureRandomClass, this.secureRandomClass);
    }

    public String getSecureRandomAlgorithm() {
        return this.secureRandomAlgorithm;
    }

    public void setSecureRandomAlgorithm(String secureRandomAlgorithm) {
        this.secureRandomAlgorithm = secureRandomAlgorithm;
    }

    public String getSecureRandomProvider() {
        return this.secureRandomProvider;
    }

    public void setSecureRandomProvider(String secureRandomProvider) {
        this.secureRandomProvider = secureRandomProvider;
    }

    @Override // org.apache.catalina.Manager
    public int getRejectedSessions() {
        return this.rejectedSessions;
    }

    @Override // org.apache.catalina.Manager
    public long getExpiredSessions() {
        return this.expiredSessions.get();
    }

    @Override // org.apache.catalina.Manager
    public void setExpiredSessions(long expiredSessions) {
        this.expiredSessions.set(expiredSessions);
    }

    public long getProcessingTime() {
        return this.processingTime;
    }

    public void setProcessingTime(long processingTime) {
        this.processingTime = processingTime;
    }

    public int getProcessExpiresFrequency() {
        return this.processExpiresFrequency;
    }

    public void setProcessExpiresFrequency(int processExpiresFrequency) {
        if (processExpiresFrequency <= 0) {
            return;
        }
        int oldProcessExpiresFrequency = this.processExpiresFrequency;
        this.processExpiresFrequency = processExpiresFrequency;
        this.support.firePropertyChange("processExpiresFrequency", Integer.valueOf(oldProcessExpiresFrequency), Integer.valueOf(this.processExpiresFrequency));
    }

    @Override // org.apache.catalina.Manager
    public void backgroundProcess() {
        this.count = (this.count + 1) % this.processExpiresFrequency;
        if (this.count == 0) {
            processExpires();
        }
    }

    public void processExpires() {
        long timeNow = System.currentTimeMillis();
        Session[] sessions = findSessions();
        int expireHere = 0;
        if (this.log.isDebugEnabled()) {
            this.log.debug("Start expire sessions " + getName() + " at " + timeNow + " sessioncount " + sessions.length);
        }
        for (int i = 0; i < sessions.length; i++) {
            if (sessions[i] != null && !sessions[i].isValid()) {
                expireHere++;
            }
        }
        long timeEnd = System.currentTimeMillis();
        if (this.log.isDebugEnabled()) {
            this.log.debug("End expire sessions " + getName() + " processingTime " + (timeEnd - timeNow) + " expired sessions: " + expireHere);
        }
        this.processingTime += timeEnd - timeNow;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.util.LifecycleMBeanBase, org.apache.catalina.util.LifecycleBase
    public void initInternal() throws LifecycleException {
        super.initInternal();
        if (this.context == null) {
            throw new LifecycleException(sm.getString("managerBase.contextNull"));
        }
    }

    @Override // org.apache.catalina.util.LifecycleBase
    public void startInternal() throws LifecycleException {
        while (this.sessionCreationTiming.size() < 100) {
            this.sessionCreationTiming.add(null);
        }
        while (this.sessionExpirationTiming.size() < 100) {
            this.sessionExpirationTiming.add(null);
        }
        SessionIdGenerator sessionIdGenerator = getSessionIdGenerator();
        if (sessionIdGenerator == null) {
            sessionIdGenerator = new StandardSessionIdGenerator();
            setSessionIdGenerator(sessionIdGenerator);
        }
        sessionIdGenerator.setJvmRoute(getJvmRoute());
        if (sessionIdGenerator instanceof SessionIdGeneratorBase) {
            SessionIdGeneratorBase sig = (SessionIdGeneratorBase) sessionIdGenerator;
            sig.setSecureRandomAlgorithm(getSecureRandomAlgorithm());
            sig.setSecureRandomClass(getSecureRandomClass());
            sig.setSecureRandomProvider(getSecureRandomProvider());
        }
        if (sessionIdGenerator instanceof Lifecycle) {
            ((Lifecycle) sessionIdGenerator).start();
            return;
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug("Force random number initialization starting");
        }
        sessionIdGenerator.generateSessionId();
        if (this.log.isDebugEnabled()) {
            this.log.debug("Force random number initialization completed");
        }
    }

    @Override // org.apache.catalina.util.LifecycleBase
    public void stopInternal() throws LifecycleException {
        if (this.sessionIdGenerator instanceof Lifecycle) {
            ((Lifecycle) this.sessionIdGenerator).stop();
        }
    }

    @Override // org.apache.catalina.Manager
    public void add(Session session) {
        this.sessions.put(session.getIdInternal(), session);
        int size = getActiveSessions();
        if (size > this.maxActive) {
            synchronized (this.maxActiveUpdateLock) {
                if (size > this.maxActive) {
                    this.maxActive = size;
                }
            }
        }
    }

    @Override // org.apache.catalina.Manager
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.support.addPropertyChangeListener(listener);
    }

    @Override // org.apache.catalina.Manager
    public Session createSession(String sessionId) {
        if (this.maxActiveSessions >= 0 && getActiveSessions() >= this.maxActiveSessions) {
            this.rejectedSessions++;
            throw new TooManyActiveSessionsException(sm.getString("managerBase.createSession.ise"), this.maxActiveSessions);
        }
        Session session = createEmptySession();
        session.setNew(true);
        session.setValid(true);
        session.setCreationTime(System.currentTimeMillis());
        session.setMaxInactiveInterval(getContext().getSessionTimeout() * 60);
        String id = sessionId;
        if (id == null) {
            id = generateSessionId();
        }
        session.setId(id);
        this.sessionCounter++;
        SessionTiming timing = new SessionTiming(session.getCreationTime(), 0);
        synchronized (this.sessionCreationTiming) {
            this.sessionCreationTiming.add(timing);
            this.sessionCreationTiming.poll();
        }
        return session;
    }

    @Override // org.apache.catalina.Manager
    public Session createEmptySession() {
        return getNewSession();
    }

    @Override // org.apache.catalina.Manager
    public Session findSession(String id) throws IOException {
        if (id == null) {
            return null;
        }
        return this.sessions.get(id);
    }

    @Override // org.apache.catalina.Manager
    public Session[] findSessions() {
        return (Session[]) this.sessions.values().toArray(new Session[0]);
    }

    @Override // org.apache.catalina.Manager
    public void remove(Session session) {
        remove(session, false);
    }

    @Override // org.apache.catalina.Manager
    public void remove(Session session, boolean update) {
        if (update) {
            long timeNow = System.currentTimeMillis();
            int timeAlive = ((int) (timeNow - session.getCreationTimeInternal())) / 1000;
            updateSessionMaxAliveTime(timeAlive);
            this.expiredSessions.incrementAndGet();
            SessionTiming timing = new SessionTiming(timeNow, timeAlive);
            synchronized (this.sessionExpirationTiming) {
                this.sessionExpirationTiming.add(timing);
                this.sessionExpirationTiming.poll();
            }
        }
        if (session.getIdInternal() != null) {
            this.sessions.remove(session.getIdInternal());
        }
    }

    @Override // org.apache.catalina.Manager
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.support.removePropertyChangeListener(listener);
    }

    @Override // org.apache.catalina.Manager
    public void changeSessionId(Session session) {
        String newId = generateSessionId();
        changeSessionId(session, newId, true, true);
    }

    @Override // org.apache.catalina.Manager
    public void changeSessionId(Session session, String newId) {
        changeSessionId(session, newId, true, true);
    }

    protected void changeSessionId(Session session, String newId, boolean notifySessionListeners, boolean notifyContainerListeners) {
        String oldId = session.getIdInternal();
        session.setId(newId, false);
        session.tellChangedSessionId(newId, oldId, notifySessionListeners, notifyContainerListeners);
    }

    @Override // org.apache.catalina.Manager
    public boolean willAttributeDistribute(String name2, Object value) {
        Pattern sessionAttributeNamePattern = getSessionAttributeNamePattern();
        if (sessionAttributeNamePattern != null && !sessionAttributeNamePattern.matcher(name2).matches()) {
            if (getWarnOnSessionAttributeFilterFailure() || this.log.isDebugEnabled()) {
                String msg = sm.getString("managerBase.sessionAttributeNameFilter", name2, sessionAttributeNamePattern);
                if (getWarnOnSessionAttributeFilterFailure()) {
                    this.log.warn(msg);
                    return false;
                }
                this.log.debug(msg);
                return false;
            }
            return false;
        }
        Pattern sessionAttributeValueClassNamePattern = getSessionAttributeValueClassNamePattern();
        if (value != null && sessionAttributeValueClassNamePattern != null && !sessionAttributeValueClassNamePattern.matcher(value.getClass().getName()).matches()) {
            if (getWarnOnSessionAttributeFilterFailure() || this.log.isDebugEnabled()) {
                String msg2 = sm.getString("managerBase.sessionAttributeValueClassNameFilter", name2, value.getClass().getName(), sessionAttributeValueClassNamePattern);
                if (getWarnOnSessionAttributeFilterFailure()) {
                    this.log.warn(msg2);
                    return false;
                }
                this.log.debug(msg2);
                return false;
            }
            return false;
        }
        return true;
    }

    public StandardSession getNewSession() {
        return new StandardSession(this);
    }

    protected String generateSessionId() {
        String result = null;
        do {
            if (result != null) {
                this.duplicates++;
            }
            result = this.sessionIdGenerator.generateSessionId();
        } while (this.sessions.containsKey(result));
        return result;
    }

    public Engine getEngine() {
        Engine e = null;
        Container context = getContext();
        while (true) {
            Container c = context;
            if (e != null || c == null) {
                break;
            }
            if (c instanceof Engine) {
                e = (Engine) c;
            }
            context = c.getParent();
        }
        return e;
    }

    public String getJvmRoute() {
        Engine e = getEngine();
        if (e == null) {
            return null;
        }
        return e.getJvmRoute();
    }

    @Override // org.apache.catalina.Manager
    public void setSessionCounter(long sessionCounter) {
        this.sessionCounter = sessionCounter;
    }

    @Override // org.apache.catalina.Manager
    public long getSessionCounter() {
        return this.sessionCounter;
    }

    public int getDuplicates() {
        return this.duplicates;
    }

    public void setDuplicates(int duplicates) {
        this.duplicates = duplicates;
    }

    @Override // org.apache.catalina.Manager
    public int getActiveSessions() {
        return this.sessions.size();
    }

    @Override // org.apache.catalina.Manager
    public int getMaxActive() {
        return this.maxActive;
    }

    @Override // org.apache.catalina.Manager
    public void setMaxActive(int maxActive) {
        synchronized (this.maxActiveUpdateLock) {
            this.maxActive = maxActive;
        }
    }

    public int getMaxActiveSessions() {
        return this.maxActiveSessions;
    }

    public void setMaxActiveSessions(int max) {
        int oldMaxActiveSessions = this.maxActiveSessions;
        this.maxActiveSessions = max;
        this.support.firePropertyChange("maxActiveSessions", Integer.valueOf(oldMaxActiveSessions), Integer.valueOf(this.maxActiveSessions));
    }

    @Override // org.apache.catalina.Manager
    public int getSessionMaxAliveTime() {
        return this.sessionMaxAliveTime;
    }

    @Override // org.apache.catalina.Manager
    public void setSessionMaxAliveTime(int sessionMaxAliveTime) {
        synchronized (this.sessionMaxAliveTimeUpdateLock) {
            this.sessionMaxAliveTime = sessionMaxAliveTime;
        }
    }

    public void updateSessionMaxAliveTime(int sessionAliveTime) {
        if (sessionAliveTime > this.sessionMaxAliveTime) {
            synchronized (this.sessionMaxAliveTimeUpdateLock) {
                if (sessionAliveTime > this.sessionMaxAliveTime) {
                    this.sessionMaxAliveTime = sessionAliveTime;
                }
            }
        }
    }

    @Override // org.apache.catalina.Manager
    public int getSessionAverageAliveTime() {
        List<SessionTiming> copy = new ArrayList<>();
        synchronized (this.sessionExpirationTiming) {
            copy.addAll(this.sessionExpirationTiming);
        }
        int counter = 0;
        int result = 0;
        for (SessionTiming timing : copy) {
            if (timing != null) {
                int timeAlive = timing.getDuration();
                counter++;
                result = (result * ((counter - 1) / counter)) + (timeAlive / counter);
            }
        }
        return result;
    }

    @Override // org.apache.catalina.Manager
    public int getSessionCreateRate() {
        List<SessionTiming> copy = new ArrayList<>();
        synchronized (this.sessionCreationTiming) {
            copy.addAll(this.sessionCreationTiming);
        }
        return calculateRate(copy);
    }

    @Override // org.apache.catalina.Manager
    public int getSessionExpireRate() {
        List<SessionTiming> copy = new ArrayList<>();
        synchronized (this.sessionExpirationTiming) {
            copy.addAll(this.sessionExpirationTiming);
        }
        return calculateRate(copy);
    }

    private static int calculateRate(List<SessionTiming> sessionTiming) {
        long now = System.currentTimeMillis();
        long oldest = now;
        int counter = 0;
        int result = 0;
        for (SessionTiming timing : sessionTiming) {
            if (timing != null) {
                counter++;
                if (timing.getTimestamp() < oldest) {
                    oldest = timing.getTimestamp();
                }
            }
        }
        if (counter > 0) {
            if (oldest < now) {
                result = (org.apache.coyote.http11.Constants.DEFAULT_CONNECTION_TIMEOUT * counter) / ((int) (now - oldest));
            } else {
                result = Integer.MAX_VALUE;
            }
        }
        return result;
    }

    public String listSessionIds() {
        StringBuilder sb = new StringBuilder();
        for (String s : this.sessions.keySet()) {
            sb.append(s).append(" ");
        }
        return sb.toString();
    }

    public String getSessionAttribute(String sessionId, String key) {
        Session s = this.sessions.get(sessionId);
        if (s == null) {
            if (this.log.isInfoEnabled()) {
                this.log.info("Session not found " + sessionId);
                return null;
            }
            return null;
        }
        Object o = s.getSession().getAttribute(key);
        if (o == null) {
            return null;
        }
        return o.toString();
    }

    public HashMap<String, String> getSession(String sessionId) {
        Session s = this.sessions.get(sessionId);
        if (s == null) {
            if (this.log.isInfoEnabled()) {
                this.log.info("Session not found " + sessionId);
                return null;
            }
            return null;
        }
        Enumeration<String> ee = s.getSession().getAttributeNames();
        if (ee == null || !ee.hasMoreElements()) {
            return null;
        }
        HashMap<String, String> map = new HashMap<>();
        while (ee.hasMoreElements()) {
            String attrName = ee.nextElement();
            map.put(attrName, getSessionAttribute(sessionId, attrName));
        }
        return map;
    }

    public void expireSession(String sessionId) {
        Session s = this.sessions.get(sessionId);
        if (s == null) {
            if (this.log.isInfoEnabled()) {
                this.log.info("Session not found " + sessionId);
                return;
            }
            return;
        }
        s.expire();
    }

    public long getThisAccessedTimestamp(String sessionId) {
        Session s = this.sessions.get(sessionId);
        if (s == null) {
            return -1L;
        }
        return s.getThisAccessedTime();
    }

    public String getThisAccessedTime(String sessionId) {
        Session s = this.sessions.get(sessionId);
        if (s == null) {
            if (this.log.isInfoEnabled()) {
                this.log.info("Session not found " + sessionId);
                return "";
            }
            return "";
        }
        return new Date(s.getThisAccessedTime()).toString();
    }

    public long getLastAccessedTimestamp(String sessionId) {
        Session s = this.sessions.get(sessionId);
        if (s == null) {
            return -1L;
        }
        return s.getLastAccessedTime();
    }

    public String getLastAccessedTime(String sessionId) {
        Session s = this.sessions.get(sessionId);
        if (s == null) {
            if (this.log.isInfoEnabled()) {
                this.log.info("Session not found " + sessionId);
                return "";
            }
            return "";
        }
        return new Date(s.getLastAccessedTime()).toString();
    }

    public String getCreationTime(String sessionId) {
        Session s = this.sessions.get(sessionId);
        if (s == null) {
            if (this.log.isInfoEnabled()) {
                this.log.info("Session not found " + sessionId);
                return "";
            }
            return "";
        }
        return new Date(s.getCreationTime()).toString();
    }

    public long getCreationTimestamp(String sessionId) {
        Session s = this.sessions.get(sessionId);
        if (s == null) {
            return -1L;
        }
        return s.getCreationTime();
    }

    public String toString() {
        return ToStringUtil.toString(this, this.context);
    }

    @Override // org.apache.catalina.util.LifecycleMBeanBase
    public String getObjectNameKeyProperties() {
        StringBuilder name2 = new StringBuilder("type=Manager");
        name2.append(",host=");
        name2.append(this.context.getParent().getName());
        name2.append(",context=");
        String contextName = this.context.getName();
        if (!contextName.startsWith("/")) {
            name2.append('/');
        }
        name2.append(contextName);
        return name2.toString();
    }

    @Override // org.apache.catalina.util.LifecycleMBeanBase
    public String getDomainInternal() {
        return this.context.getDomain();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/session/ManagerBase$SessionTiming.class */
    public static final class SessionTiming {
        private final long timestamp;
        private final int duration;

        public SessionTiming(long timestamp, int duration) {
            this.timestamp = timestamp;
            this.duration = duration;
        }

        public long getTimestamp() {
            return this.timestamp;
        }

        public int getDuration() {
            return this.duration;
        }
    }
}