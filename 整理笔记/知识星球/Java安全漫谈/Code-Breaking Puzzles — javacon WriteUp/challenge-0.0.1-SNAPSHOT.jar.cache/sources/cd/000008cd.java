package org.apache.catalina.session;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletContext;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Loader;
import org.apache.catalina.Session;
import org.apache.catalina.security.SecurityUtil;
import org.apache.catalina.util.CustomObjectInputStream;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/session/StandardManager.class */
public class StandardManager extends ManagerBase {
    protected static final String name = "StandardManager";
    private final Log log = LogFactory.getLog(StandardManager.class);
    protected String pathname = "SESSIONS.ser";

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/session/StandardManager$PrivilegedDoLoad.class */
    public class PrivilegedDoLoad implements PrivilegedExceptionAction<Void> {
        PrivilegedDoLoad() {
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedExceptionAction
        public Void run() throws Exception {
            StandardManager.this.doLoad();
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/session/StandardManager$PrivilegedDoUnload.class */
    public class PrivilegedDoUnload implements PrivilegedExceptionAction<Void> {
        PrivilegedDoUnload() {
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedExceptionAction
        public Void run() throws Exception {
            StandardManager.this.doUnload();
            return null;
        }
    }

    @Override // org.apache.catalina.session.ManagerBase
    public String getName() {
        return name;
    }

    public String getPathname() {
        return this.pathname;
    }

    public void setPathname(String pathname) {
        String oldPathname = this.pathname;
        this.pathname = pathname;
        this.support.firePropertyChange("pathname", oldPathname, this.pathname);
    }

    @Override // org.apache.catalina.Manager
    public void load() throws ClassNotFoundException, IOException {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            try {
                AccessController.doPrivileged(new PrivilegedDoLoad());
                return;
            } catch (PrivilegedActionException ex) {
                Exception exception = ex.getException();
                if (exception instanceof ClassNotFoundException) {
                    throw ((ClassNotFoundException) exception);
                }
                if (exception instanceof IOException) {
                    throw ((IOException) exception);
                }
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Unreported exception in load() ", exception);
                    return;
                }
                return;
            }
        }
        doLoad();
    }

    protected void doLoad() throws ClassNotFoundException, IOException {
        if (this.log.isDebugEnabled()) {
            this.log.debug("Start: Loading persisted sessions");
        }
        this.sessions.clear();
        File file = file();
        if (file == null) {
            return;
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug(sm.getString("standardManager.loading", this.pathname));
        }
        try {
            FileInputStream fis = new FileInputStream(file.getAbsolutePath());
            BufferedInputStream bis = new BufferedInputStream(fis);
            Throwable th = null;
            try {
                Context c = getContext();
                Loader loader = c.getLoader();
                Log logger = c.getLogger();
                ClassLoader classLoader = loader != null ? loader.getClassLoader() : null;
                if (classLoader == null) {
                    classLoader = getClass().getClassLoader();
                }
                synchronized (this.sessions) {
                    try {
                        ObjectInputStream ois = new CustomObjectInputStream(bis, classLoader, logger, getSessionAttributeValueClassNamePattern(), getWarnOnSessionAttributeFilterFailure());
                        Throwable th2 = null;
                        try {
                            Integer count = (Integer) ois.readObject();
                            int n = count.intValue();
                            if (this.log.isDebugEnabled()) {
                                this.log.debug("Loading " + n + " persisted sessions");
                            }
                            for (int i = 0; i < n; i++) {
                                StandardSession session = getNewSession();
                                session.readObjectData(ois);
                                session.setManager(this);
                                this.sessions.put(session.getIdInternal(), session);
                                session.activate();
                                if (!session.isValidInternal()) {
                                    session.setValid(true);
                                    session.expire();
                                }
                                this.sessionCounter++;
                            }
                            if (ois != null) {
                                if (0 != 0) {
                                    try {
                                        ois.close();
                                    } catch (Throwable th3) {
                                        th2.addSuppressed(th3);
                                    }
                                } else {
                                    ois.close();
                                }
                            }
                            if (file.exists() && !file.delete()) {
                                this.log.warn(sm.getString("standardManager.deletePersistedFileFail", file));
                            }
                        } catch (Throwable th4) {
                            try {
                                throw th4;
                            } catch (Throwable th5) {
                                if (ois != null) {
                                    if (th4 != null) {
                                        try {
                                            ois.close();
                                        } catch (Throwable th6) {
                                            th4.addSuppressed(th6);
                                        }
                                    } else {
                                        ois.close();
                                    }
                                }
                                throw th5;
                            }
                        }
                    } catch (Throwable th7) {
                        if (file.exists() && !file.delete()) {
                            this.log.warn(sm.getString("standardManager.deletePersistedFileFail", file));
                        }
                        throw th7;
                    }
                }
                if (bis != null) {
                    if (0 != 0) {
                        try {
                            bis.close();
                        } catch (Throwable th8) {
                            th.addSuppressed(th8);
                        }
                    } else {
                        bis.close();
                    }
                }
                if (fis != null) {
                    if (0 != 0) {
                        fis.close();
                    } else {
                        fis.close();
                    }
                }
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Finish: Loading persisted sessions");
                }
            } catch (Throwable th9) {
                try {
                    throw th9;
                } catch (Throwable th10) {
                    if (bis != null) {
                        if (th9 != null) {
                            try {
                                bis.close();
                            } catch (Throwable th11) {
                                th9.addSuppressed(th11);
                            }
                        } else {
                            bis.close();
                        }
                    }
                    throw th10;
                }
            }
        } catch (FileNotFoundException e) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("No persisted data file found");
            }
        }
    }

    @Override // org.apache.catalina.Manager
    public void unload() throws IOException {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            try {
                AccessController.doPrivileged(new PrivilegedDoUnload());
                return;
            } catch (PrivilegedActionException ex) {
                Exception exception = ex.getException();
                if (exception instanceof IOException) {
                    throw ((IOException) exception);
                }
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Unreported exception in unLoad()", exception);
                    return;
                }
                return;
            }
        }
        doUnload();
    }

    protected void doUnload() throws IOException {
        if (this.log.isDebugEnabled()) {
            this.log.debug(sm.getString("standardManager.unloading.debug"));
        }
        if (this.sessions.isEmpty()) {
            this.log.debug(sm.getString("standardManager.unloading.nosessions"));
            return;
        }
        File file = file();
        if (file == null) {
            return;
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug(sm.getString("standardManager.unloading", this.pathname));
        }
        List<StandardSession> list = new ArrayList<>();
        FileOutputStream fos = new FileOutputStream(file.getAbsolutePath());
        Throwable th = null;
        try {
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            Throwable th2 = null;
            try {
                synchronized (this.sessions) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug("Unloading " + this.sessions.size() + " sessions");
                    }
                    oos.writeObject(Integer.valueOf(this.sessions.size()));
                    for (Session s : this.sessions.values()) {
                        StandardSession session = (StandardSession) s;
                        list.add(session);
                        session.passivate();
                        session.writeObjectData(oos);
                    }
                }
                if (oos != null) {
                    if (0 != 0) {
                        try {
                            oos.close();
                        } catch (Throwable th3) {
                            th2.addSuppressed(th3);
                        }
                    } else {
                        oos.close();
                    }
                }
                if (bos != null) {
                    if (0 != 0) {
                        bos.close();
                    } else {
                        bos.close();
                    }
                }
                if (fos != null) {
                    if (0 != 0) {
                        try {
                            fos.close();
                        } catch (Throwable th4) {
                            th.addSuppressed(th4);
                        }
                    } else {
                        fos.close();
                    }
                }
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Expiring " + list.size() + " persisted sessions");
                }
                Iterator<StandardSession> it = list.iterator();
                while (it.hasNext()) {
                    StandardSession session2 = it.next();
                    try {
                        session2.expire(false);
                    } catch (Throwable t) {
                        try {
                            ExceptionUtils.handleThrowable(t);
                            session2.recycle();
                        } finally {
                            session2.recycle();
                        }
                    }
                }
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Unloading complete");
                }
            } catch (Throwable th5) {
                try {
                    throw th5;
                } catch (Throwable th6) {
                    if (oos != null) {
                        if (th5 != null) {
                            try {
                                oos.close();
                            } catch (Throwable th7) {
                                th5.addSuppressed(th7);
                            }
                        } else {
                            oos.close();
                        }
                    }
                    throw th6;
                }
            }
        } catch (Throwable th8) {
            try {
                throw th8;
            } catch (Throwable th9) {
                if (fos != null) {
                    if (th8 != null) {
                        try {
                            fos.close();
                        } catch (Throwable th10) {
                            th8.addSuppressed(th10);
                        }
                    } else {
                        fos.close();
                    }
                }
                throw th9;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.session.ManagerBase, org.apache.catalina.util.LifecycleBase
    public synchronized void startInternal() throws LifecycleException {
        super.startInternal();
        try {
            load();
        } catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
            this.log.error(sm.getString("standardManager.managerLoad"), t);
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
        try {
            unload();
        } catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
            this.log.error(sm.getString("standardManager.managerUnload"), t);
        }
        Session[] sessions = findSessions();
        for (int i = 0; i < sessions.length; i++) {
            Session session = sessions[i];
            try {
                if (session.isValid()) {
                    session.expire();
                }
            } catch (Throwable t2) {
                try {
                    ExceptionUtils.handleThrowable(t2);
                    session.recycle();
                } finally {
                    session.recycle();
                }
            }
        }
        super.stopInternal();
    }

    protected File file() {
        if (this.pathname == null || this.pathname.length() == 0) {
            return null;
        }
        File file = new File(this.pathname);
        if (!file.isAbsolute()) {
            Context context = getContext();
            ServletContext servletContext = context.getServletContext();
            File tempdir = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
            if (tempdir != null) {
                file = new File(tempdir, this.pathname);
            }
        }
        return file;
    }
}