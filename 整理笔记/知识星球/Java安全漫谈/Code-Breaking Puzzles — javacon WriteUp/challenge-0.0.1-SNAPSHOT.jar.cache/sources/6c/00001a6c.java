package org.springframework.boot.web.embedded.jetty;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import javax.servlet.ServletContainerInitializer;
import org.apache.catalina.webresources.TomcatURLStreamHandlerFactory;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/embedded/jetty/JasperInitializer.class */
class JasperInitializer extends AbstractLifeCycle {
    private static final String[] INITIALIZER_CLASSES = {"org.eclipse.jetty.apache.jsp.JettyJasperInitializer", "org.apache.jasper.servlet.JasperInitializer"};
    private final WebAppContext context;
    private final ServletContainerInitializer initializer = newInitializer();

    /* JADX INFO: Access modifiers changed from: package-private */
    public JasperInitializer(WebAppContext context) {
        this.context = context;
    }

    private ServletContainerInitializer newInitializer() {
        String[] strArr;
        for (String className : INITIALIZER_CLASSES) {
            try {
                Class<?> initializerClass = ClassUtils.forName(className, null);
                return (ServletContainerInitializer) initializerClass.newInstance();
            } catch (Exception e) {
            }
        }
        return null;
    }

    protected void doStart() throws Exception {
        if (this.initializer == null) {
            return;
        }
        if (ClassUtils.isPresent("org.apache.catalina.webresources.TomcatURLStreamHandlerFactory", getClass().getClassLoader())) {
            TomcatURLStreamHandlerFactory.register();
        } else {
            try {
                URL.setURLStreamHandlerFactory(new WarUrlStreamHandlerFactory());
            } catch (Error e) {
            }
        }
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(this.context.getClassLoader());
            setExtendedListenerTypes(true);
            this.initializer.onStartup(null, this.context.getServletContext());
            setExtendedListenerTypes(false);
            Thread.currentThread().setContextClassLoader(classLoader);
        } catch (Throwable th) {
            Thread.currentThread().setContextClassLoader(classLoader);
            throw th;
        }
    }

    private void setExtendedListenerTypes(boolean extended) {
        try {
            this.context.getServletContext().setExtendedListenerTypes(extended);
        } catch (NoSuchMethodError e) {
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/embedded/jetty/JasperInitializer$WarUrlStreamHandlerFactory.class */
    private static class WarUrlStreamHandlerFactory implements URLStreamHandlerFactory {
        private WarUrlStreamHandlerFactory() {
        }

        @Override // java.net.URLStreamHandlerFactory
        public URLStreamHandler createURLStreamHandler(String protocol) {
            if (ResourceUtils.URL_PROTOCOL_WAR.equals(protocol)) {
                return new WarUrlStreamHandler();
            }
            return null;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/embedded/jetty/JasperInitializer$WarUrlStreamHandler.class */
    private static class WarUrlStreamHandler extends URLStreamHandler {
        private WarUrlStreamHandler() {
        }

        @Override // java.net.URLStreamHandler
        protected void parseURL(URL u, String spec, int start, int limit) {
            String path = ResourceUtils.JAR_URL_PREFIX + spec.substring(ResourceUtils.WAR_URL_PREFIX.length());
            int separator = path.indexOf(ResourceUtils.WAR_URL_SEPARATOR);
            if (separator >= 0) {
                path = path.substring(0, separator) + ResourceUtils.JAR_URL_SEPARATOR + path.substring(separator + 2);
            }
            setURL(u, u.getProtocol(), "", -1, null, null, path, null, null);
        }

        @Override // java.net.URLStreamHandler
        protected URLConnection openConnection(URL u) throws IOException {
            return new WarURLConnection(u);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/embedded/jetty/JasperInitializer$WarURLConnection.class */
    private static class WarURLConnection extends URLConnection {
        private final URLConnection connection;

        protected WarURLConnection(URL url) throws IOException {
            super(url);
            this.connection = new URL(url.getFile()).openConnection();
        }

        @Override // java.net.URLConnection
        public void connect() throws IOException {
            if (!this.connected) {
                this.connection.connect();
                this.connected = true;
            }
        }

        @Override // java.net.URLConnection
        public InputStream getInputStream() throws IOException {
            connect();
            return this.connection.getInputStream();
        }
    }
}