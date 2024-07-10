package ch.qos.logback.classic.util;

import ch.qos.logback.classic.BasicConfigurator;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.gaffer.GafferUtil;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.Configurator;
import ch.qos.logback.core.LogbackException;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.status.ErrorStatus;
import ch.qos.logback.core.status.InfoStatus;
import ch.qos.logback.core.status.StatusManager;
import ch.qos.logback.core.status.WarnStatus;
import ch.qos.logback.core.util.Loader;
import ch.qos.logback.core.util.StatusListenerConfigHelper;
import java.io.IOException;
import java.net.URL;
import java.util.Set;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.thymeleaf.engine.XMLDeclaration;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/util/ContextInitializer.class */
public class ContextInitializer {
    public static final String GROOVY_AUTOCONFIG_FILE = "logback.groovy";
    public static final String AUTOCONFIG_FILE = "logback.xml";
    public static final String TEST_AUTOCONFIG_FILE = "logback-test.xml";
    public static final String CONFIG_FILE_PROPERTY = "logback.configurationFile";
    final LoggerContext loggerContext;

    public ContextInitializer(LoggerContext loggerContext) {
        this.loggerContext = loggerContext;
    }

    public void configureByResource(URL url) throws JoranException {
        if (url == null) {
            throw new IllegalArgumentException("URL argument cannot be null");
        }
        String urlString = url.toString();
        if (urlString.endsWith("groovy")) {
            if (EnvUtil.isGroovyAvailable()) {
                GafferUtil.runGafferConfiguratorOn(this.loggerContext, this, url);
                return;
            }
            StatusManager sm = this.loggerContext.getStatusManager();
            sm.add(new ErrorStatus("Groovy classes are not available on the class path. ABORTING INITIALIZATION.", this.loggerContext));
        } else if (urlString.endsWith(XMLDeclaration.DEFAULT_KEYWORD)) {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(this.loggerContext);
            configurator.doConfigure(url);
        } else {
            throw new LogbackException("Unexpected filename extension of file [" + url.toString() + "]. Should be either .groovy or .xml");
        }
    }

    void joranConfigureByResource(URL url) throws JoranException {
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(this.loggerContext);
        configurator.doConfigure(url);
    }

    /* JADX WARN: Removed duplicated region for block: B:75:0x0088  */
    /* JADX WARN: Removed duplicated region for block: B:88:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private java.net.URL findConfigFileURLFromSystemProperties(java.lang.ClassLoader r6, boolean r7) {
        /*
            r5 = this;
            java.lang.String r0 = "logback.configurationFile"
            java.lang.String r0 = ch.qos.logback.core.util.OptionHelper.getSystemProperty(r0)
            r8 = r0
            r0 = r8
            if (r0 == 0) goto La4
            r0 = 0
            r9 = r0
            java.net.URL r0 = new java.net.URL     // Catch: java.net.MalformedURLException -> L2a java.lang.Throwable -> L93
            r1 = r0
            r2 = r8
            r1.<init>(r2)     // Catch: java.net.MalformedURLException -> L2a java.lang.Throwable -> L93
            r9 = r0
            r0 = r9
            r10 = r0
            r0 = r7
            if (r0 == 0) goto L27
            r0 = r5
            r1 = r8
            r2 = r6
            r3 = r9
            r0.statusOnResourceSearch(r1, r2, r3)
        L27:
            r0 = r10
            return r0
        L2a:
            r10 = move-exception
            r0 = r8
            r1 = r6
            java.net.URL r0 = ch.qos.logback.core.util.Loader.getResource(r0, r1)     // Catch: java.lang.Throwable -> L93
            r9 = r0
            r0 = r9
            if (r0 == 0) goto L4b
            r0 = r9
            r11 = r0
            r0 = r7
            if (r0 == 0) goto L48
            r0 = r5
            r1 = r8
            r2 = r6
            r3 = r9
            r0.statusOnResourceSearch(r1, r2, r3)
        L48:
            r0 = r11
            return r0
        L4b:
            java.io.File r0 = new java.io.File     // Catch: java.lang.Throwable -> L93
            r1 = r0
            r2 = r8
            r1.<init>(r2)     // Catch: java.lang.Throwable -> L93
            r11 = r0
            r0 = r11
            boolean r0 = r0.exists()     // Catch: java.lang.Throwable -> L93
            if (r0 == 0) goto L84
            r0 = r11
            boolean r0 = r0.isFile()     // Catch: java.lang.Throwable -> L93
            if (r0 == 0) goto L84
            r0 = r11
            java.net.URI r0 = r0.toURI()     // Catch: java.net.MalformedURLException -> L82 java.lang.Throwable -> L93
            java.net.URL r0 = r0.toURL()     // Catch: java.net.MalformedURLException -> L82 java.lang.Throwable -> L93
            r9 = r0
            r0 = r9
            r12 = r0
            r0 = r7
            if (r0 == 0) goto L7f
            r0 = r5
            r1 = r8
            r2 = r6
            r3 = r9
            r0.statusOnResourceSearch(r1, r2, r3)
        L7f:
            r0 = r12
            return r0
        L82:
            r12 = move-exception
        L84:
            r0 = r7
            if (r0 == 0) goto La4
            r0 = r5
            r1 = r8
            r2 = r6
            r3 = r9
            r0.statusOnResourceSearch(r1, r2, r3)
            goto La4
        L93:
            r13 = move-exception
            r0 = r7
            if (r0 == 0) goto La1
            r0 = r5
            r1 = r8
            r2 = r6
            r3 = r9
            r0.statusOnResourceSearch(r1, r2, r3)
        La1:
            r0 = r13
            throw r0
        La4:
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: ch.qos.logback.classic.util.ContextInitializer.findConfigFileURLFromSystemProperties(java.lang.ClassLoader, boolean):java.net.URL");
    }

    public URL findURLOfDefaultConfigurationFile(boolean updateStatus) {
        ClassLoader myClassLoader = Loader.getClassLoaderOfObject(this);
        URL url = findConfigFileURLFromSystemProperties(myClassLoader, updateStatus);
        if (url != null) {
            return url;
        }
        URL url2 = getResource(TEST_AUTOCONFIG_FILE, myClassLoader, updateStatus);
        if (url2 != null) {
            return url2;
        }
        URL url3 = getResource(GROOVY_AUTOCONFIG_FILE, myClassLoader, updateStatus);
        if (url3 != null) {
            return url3;
        }
        return getResource(AUTOCONFIG_FILE, myClassLoader, updateStatus);
    }

    private URL getResource(String filename, ClassLoader myClassLoader, boolean updateStatus) {
        URL url = Loader.getResource(filename, myClassLoader);
        if (updateStatus) {
            statusOnResourceSearch(filename, myClassLoader, url);
        }
        return url;
    }

    public void autoConfig() throws JoranException {
        StatusListenerConfigHelper.installIfAsked(this.loggerContext);
        URL url = findURLOfDefaultConfigurationFile(true);
        if (url != null) {
            configureByResource(url);
            return;
        }
        Configurator c = (Configurator) EnvUtil.loadFromServiceLoader(Configurator.class);
        if (c != null) {
            try {
                c.setContext(this.loggerContext);
                c.configure(this.loggerContext);
                return;
            } catch (Exception e) {
                Object[] objArr = new Object[1];
                objArr[0] = c != null ? c.getClass().getCanonicalName() : BeanDefinitionParserDelegate.NULL_ELEMENT;
                throw new LogbackException(String.format("Failed to initialize Configurator: %s using ServiceLoader", objArr), e);
            }
        }
        BasicConfigurator basicConfigurator = new BasicConfigurator();
        basicConfigurator.setContext(this.loggerContext);
        basicConfigurator.configure(this.loggerContext);
    }

    private void statusOnResourceSearch(String resourceName, ClassLoader classLoader, URL url) {
        StatusManager sm = this.loggerContext.getStatusManager();
        if (url == null) {
            sm.add(new InfoStatus("Could NOT find resource [" + resourceName + "]", this.loggerContext));
            return;
        }
        sm.add(new InfoStatus("Found resource [" + resourceName + "] at [" + url.toString() + "]", this.loggerContext));
        multiplicityWarning(resourceName, classLoader);
    }

    private void multiplicityWarning(String resourceName, ClassLoader classLoader) {
        Set<URL> urlSet = null;
        StatusManager sm = this.loggerContext.getStatusManager();
        try {
            urlSet = Loader.getResources(resourceName, classLoader);
        } catch (IOException e) {
            sm.add(new ErrorStatus("Failed to get url list for resource [" + resourceName + "]", this.loggerContext, e));
        }
        if (urlSet != null && urlSet.size() > 1) {
            sm.add(new WarnStatus("Resource [" + resourceName + "] occurs multiple times on the classpath.", this.loggerContext));
            for (URL url : urlSet) {
                sm.add(new WarnStatus("Resource [" + resourceName + "] occurs at [" + url.toString() + "]", this.loggerContext));
            }
        }
    }
}