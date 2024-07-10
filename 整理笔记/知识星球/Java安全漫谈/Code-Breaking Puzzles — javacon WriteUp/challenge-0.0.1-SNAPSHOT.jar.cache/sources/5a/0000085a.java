package org.apache.catalina.loader;

import ch.qos.logback.core.CoreConstants;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import javax.management.ObjectName;
import javax.servlet.ServletContext;
import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Loader;
import org.apache.catalina.util.LifecycleMBeanBase;
import org.apache.catalina.util.ToStringUtil;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.buf.UDecoder;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.res.StringManager;
import org.springframework.util.ResourceUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/loader/WebappLoader.class */
public class WebappLoader extends LifecycleMBeanBase implements Loader, PropertyChangeListener {
    private WebappClassLoaderBase classLoader;
    private Context context;
    private boolean delegate;
    private String loaderClass;
    private ClassLoader parentClassLoader;
    private boolean reloadable;
    protected final PropertyChangeSupport support;
    private String classpath;
    protected static final StringManager sm = StringManager.getManager(Constants.Package);
    private static final Log log = LogFactory.getLog(WebappLoader.class);

    public WebappLoader() {
        this(null);
    }

    public WebappLoader(ClassLoader parent) {
        this.classLoader = null;
        this.context = null;
        this.delegate = false;
        this.loaderClass = ParallelWebappClassLoader.class.getName();
        this.parentClassLoader = null;
        this.reloadable = false;
        this.support = new PropertyChangeSupport(this);
        this.classpath = null;
        this.parentClassLoader = parent;
    }

    @Override // org.apache.catalina.Loader
    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    @Override // org.apache.catalina.Loader
    public Context getContext() {
        return this.context;
    }

    @Override // org.apache.catalina.Loader
    public void setContext(Context context) {
        if (this.context == context) {
            return;
        }
        if (getState().isAvailable()) {
            throw new IllegalStateException(sm.getString("webappLoader.setContext.ise"));
        }
        if (this.context != null) {
            this.context.removePropertyChangeListener(this);
        }
        Context oldContext = this.context;
        this.context = context;
        this.support.firePropertyChange(CoreConstants.CONTEXT_SCOPE_VALUE, oldContext, this.context);
        if (this.context != null) {
            setReloadable(this.context.getReloadable());
            this.context.addPropertyChangeListener(this);
        }
    }

    @Override // org.apache.catalina.Loader
    public boolean getDelegate() {
        return this.delegate;
    }

    @Override // org.apache.catalina.Loader
    public void setDelegate(boolean delegate) {
        boolean oldDelegate = this.delegate;
        this.delegate = delegate;
        this.support.firePropertyChange("delegate", Boolean.valueOf(oldDelegate), Boolean.valueOf(this.delegate));
    }

    public String getLoaderClass() {
        return this.loaderClass;
    }

    public void setLoaderClass(String loaderClass) {
        this.loaderClass = loaderClass;
    }

    @Override // org.apache.catalina.Loader
    public boolean getReloadable() {
        return this.reloadable;
    }

    @Override // org.apache.catalina.Loader
    public void setReloadable(boolean reloadable) {
        boolean oldReloadable = this.reloadable;
        this.reloadable = reloadable;
        this.support.firePropertyChange("reloadable", Boolean.valueOf(oldReloadable), Boolean.valueOf(this.reloadable));
    }

    @Override // org.apache.catalina.Loader
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.support.addPropertyChangeListener(listener);
    }

    @Override // org.apache.catalina.Loader
    public void backgroundProcess() {
        if (this.reloadable && modified()) {
            try {
                Thread.currentThread().setContextClassLoader(WebappLoader.class.getClassLoader());
                if (this.context != null) {
                    this.context.reload();
                }
            } finally {
                if (this.context != null && this.context.getLoader() != null) {
                    Thread.currentThread().setContextClassLoader(this.context.getLoader().getClassLoader());
                }
            }
        }
    }

    public String[] getLoaderRepositories() {
        if (this.classLoader == null) {
            return new String[0];
        }
        URL[] urls = this.classLoader.getURLs();
        String[] result = new String[urls.length];
        for (int i = 0; i < urls.length; i++) {
            result[i] = urls[i].toExternalForm();
        }
        return result;
    }

    public String getLoaderRepositoriesString() {
        String[] repositories = getLoaderRepositories();
        StringBuilder sb = new StringBuilder();
        for (String str : repositories) {
            sb.append(str).append(":");
        }
        return sb.toString();
    }

    public String getClasspath() {
        return this.classpath;
    }

    @Override // org.apache.catalina.Loader
    public boolean modified() {
        if (this.classLoader != null) {
            return this.classLoader.modified();
        }
        return false;
    }

    @Override // org.apache.catalina.Loader
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.support.removePropertyChangeListener(listener);
    }

    public String toString() {
        return ToStringUtil.toString(this, this.context);
    }

    @Override // org.apache.catalina.util.LifecycleBase
    protected void startInternal() throws LifecycleException {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("webappLoader.starting"));
        }
        if (this.context.getResources() == null) {
            log.info("No resources for " + this.context);
            setState(LifecycleState.STARTING);
            return;
        }
        try {
            this.classLoader = createClassLoader();
            this.classLoader.setResources(this.context.getResources());
            this.classLoader.setDelegate(this.delegate);
            setClassPath();
            setPermissions();
            this.classLoader.start();
            String contextName = this.context.getName();
            if (!contextName.startsWith("/")) {
                contextName = "/" + contextName;
            }
            ObjectName cloname = new ObjectName(this.context.getDomain() + ":type=" + this.classLoader.getClass().getSimpleName() + ",host=" + this.context.getParent().getName() + ",context=" + contextName);
            Registry.getRegistry(null, null).registerComponent(this.classLoader, cloname, (String) null);
            setState(LifecycleState.STARTING);
        } catch (Throwable t) {
            Throwable t2 = ExceptionUtils.unwrapInvocationTargetException(t);
            ExceptionUtils.handleThrowable(t2);
            log.error("LifecycleException ", t2);
            throw new LifecycleException("start: ", t2);
        }
    }

    @Override // org.apache.catalina.util.LifecycleBase
    protected void stopInternal() throws LifecycleException {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("webappLoader.stopping"));
        }
        setState(LifecycleState.STOPPING);
        ServletContext servletContext = this.context.getServletContext();
        servletContext.removeAttribute(Globals.CLASS_PATH_ATTR);
        if (this.classLoader != null) {
            try {
                this.classLoader.stop();
                try {
                    String contextName = this.context.getName();
                    if (!contextName.startsWith("/")) {
                        contextName = "/" + contextName;
                    }
                    ObjectName cloname = new ObjectName(this.context.getDomain() + ":type=" + this.classLoader.getClass().getSimpleName() + ",host=" + this.context.getParent().getName() + ",context=" + contextName);
                    Registry.getRegistry(null, null).unregisterComponent(cloname);
                } catch (Exception e) {
                    log.error("LifecycleException ", e);
                }
            } finally {
                this.classLoader.destroy();
            }
        }
        this.classLoader = null;
    }

    @Override // java.beans.PropertyChangeListener
    public void propertyChange(PropertyChangeEvent event) {
        if ((event.getSource() instanceof Context) && event.getPropertyName().equals("reloadable")) {
            try {
                setReloadable(((Boolean) event.getNewValue()).booleanValue());
            } catch (NumberFormatException e) {
                log.error(sm.getString("webappLoader.reloadable", event.getNewValue().toString()));
            }
        }
    }

    private WebappClassLoaderBase createClassLoader() throws Exception {
        Class<?> clazz = Class.forName(this.loaderClass);
        if (this.parentClassLoader == null) {
            this.parentClassLoader = this.context.getParentClassLoader();
        }
        Class<?>[] argTypes = {ClassLoader.class};
        Object[] args = {this.parentClassLoader};
        Constructor<?> constr = clazz.getConstructor(argTypes);
        WebappClassLoaderBase classLoader = (WebappClassLoaderBase) constr.newInstance(args);
        return classLoader;
    }

    private void setPermissions() {
        if (!Globals.IS_SECURITY_ENABLED || this.context == null) {
            return;
        }
        ServletContext servletContext = this.context.getServletContext();
        File workDir = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
        if (workDir != null) {
            try {
                String workDirPath = workDir.getCanonicalPath();
                this.classLoader.addPermission(new FilePermission(workDirPath, "read,write"));
                this.classLoader.addPermission(new FilePermission(workDirPath + File.separator + "-", "read,write,delete"));
            } catch (IOException e) {
            }
        }
        for (URL url : this.context.getResources().getBaseUrls()) {
            this.classLoader.addPermission(url);
        }
    }

    private void setClassPath() {
        ServletContext servletContext;
        ClassLoader loader;
        if (this.context == null || (servletContext = this.context.getServletContext()) == null) {
            return;
        }
        StringBuilder classpath = new StringBuilder();
        ClassLoader loader2 = getClassLoader();
        if (this.delegate && loader2 != null) {
            loader2 = loader2.getParent();
        }
        while (loader2 != null && buildClassPath(classpath, loader2)) {
            loader2 = loader2.getParent();
        }
        if (this.delegate && (loader = getClassLoader()) != null) {
            buildClassPath(classpath, loader);
        }
        this.classpath = classpath.toString();
        servletContext.setAttribute(Globals.CLASS_PATH_ATTR, this.classpath);
    }

    private boolean buildClassPath(StringBuilder classpath, ClassLoader loader) {
        String repository;
        if (!(loader instanceof URLClassLoader)) {
            if (loader == ClassLoader.getSystemClassLoader()) {
                String cp = System.getProperty("java.class.path");
                if (cp != null && cp.length() > 0) {
                    if (classpath.length() > 0) {
                        classpath.append(File.pathSeparator);
                    }
                    classpath.append(cp);
                    return false;
                }
                return false;
            }
            log.info("Unknown loader " + loader + " " + loader.getClass());
            return false;
        }
        URL[] repositories = ((URLClassLoader) loader).getURLs();
        for (URL url : repositories) {
            String repository2 = url.toString();
            if (repository2.startsWith("file://")) {
                repository = UDecoder.URLDecode(repository2.substring(7));
            } else if (repository2.startsWith(ResourceUtils.FILE_URL_PREFIX)) {
                repository = UDecoder.URLDecode(repository2.substring(5));
            }
            if (repository != null) {
                if (classpath.length() > 0) {
                    classpath.append(File.pathSeparator);
                }
                classpath.append(repository);
            }
        }
        return true;
    }

    @Override // org.apache.catalina.util.LifecycleMBeanBase
    protected String getDomainInternal() {
        return this.context.getDomain();
    }

    @Override // org.apache.catalina.util.LifecycleMBeanBase
    protected String getObjectNameKeyProperties() {
        StringBuilder name = new StringBuilder("type=Loader");
        name.append(",host=");
        name.append(this.context.getParent().getName());
        name.append(",context=");
        String contextName = this.context.getName();
        if (!contextName.startsWith("/")) {
            name.append("/");
        }
        name.append(contextName);
        return name.toString();
    }
}