package org.apache.catalina.startup;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import org.apache.catalina.Context;
import org.apache.tomcat.util.scan.JarFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/startup/WebappServiceLoader.class */
public class WebappServiceLoader<T> {
    private static final String LIB = "/WEB-INF/lib/";
    private static final String SERVICES = "META-INF/services/";
    private final Context context;
    private final ServletContext servletContext;
    private final Pattern containerSciFilterPattern;

    public WebappServiceLoader(Context context) {
        this.context = context;
        this.servletContext = context.getServletContext();
        String containerSciFilter = context.getContainerSciFilter();
        if (containerSciFilter != null && containerSciFilter.length() > 0) {
            this.containerSciFilterPattern = Pattern.compile(containerSciFilter);
        } else {
            this.containerSciFilterPattern = null;
        }
    }

    public List<T> load(Class<T> serviceType) throws IOException {
        Enumeration<URL> resources;
        URL jarEntryURL;
        String configFile = SERVICES + serviceType.getName();
        LinkedHashSet<String> applicationServicesFound = new LinkedHashSet<>();
        LinkedHashSet<String> containerServicesFound = new LinkedHashSet<>();
        ClassLoader loader = this.servletContext.getClassLoader();
        List<String> orderedLibs = (List) this.servletContext.getAttribute(ServletContext.ORDERED_LIBS);
        if (orderedLibs != null) {
            for (String lib : orderedLibs) {
                URL jarUrl = this.servletContext.getResource("/WEB-INF/lib/" + lib);
                if (jarUrl != null) {
                    String base = jarUrl.toExternalForm();
                    if (base.endsWith("/")) {
                        jarEntryURL = new URL(base + configFile);
                    } else {
                        jarEntryURL = JarFactory.getJarEntryURL(jarUrl, configFile);
                    }
                    try {
                        URL url = jarEntryURL;
                        parseConfigFile(applicationServicesFound, url);
                    } catch (FileNotFoundException e) {
                    }
                }
            }
            loader = this.context.getParentClassLoader();
        }
        if (loader == null) {
            resources = ClassLoader.getSystemResources(configFile);
        } else {
            resources = loader.getResources(configFile);
        }
        while (resources.hasMoreElements()) {
            parseConfigFile(containerServicesFound, resources.nextElement());
        }
        if (this.containerSciFilterPattern != null) {
            Iterator<String> iter = containerServicesFound.iterator();
            while (iter.hasNext()) {
                if (this.containerSciFilterPattern.matcher(iter.next()).find()) {
                    iter.remove();
                }
            }
        }
        containerServicesFound.addAll(applicationServicesFound);
        if (containerServicesFound.isEmpty()) {
            return Collections.emptyList();
        }
        return loadServices(serviceType, containerServicesFound);
    }

    void parseConfigFile(LinkedHashSet<String> servicesFound, URL url) throws IOException {
        InputStream is = url.openStream();
        Throwable th = null;
        try {
            InputStreamReader in = new InputStreamReader(is, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(in);
            Throwable th2 = null;
            while (true) {
                try {
                    String readLine = reader.readLine();
                    String line = readLine;
                    if (readLine == null) {
                        break;
                    }
                    int i = line.indexOf(35);
                    if (i >= 0) {
                        line = line.substring(0, i);
                    }
                    String line2 = line.trim();
                    if (line2.length() != 0) {
                        servicesFound.add(line2);
                    }
                } catch (Throwable th3) {
                    try {
                        throw th3;
                    } catch (Throwable th4) {
                        if (reader != null) {
                            if (th3 != null) {
                                try {
                                    reader.close();
                                } catch (Throwable th5) {
                                    th3.addSuppressed(th5);
                                }
                            } else {
                                reader.close();
                            }
                        }
                        throw th4;
                    }
                }
            }
            if (reader != null) {
                if (0 != 0) {
                    try {
                        reader.close();
                    } catch (Throwable th6) {
                        th2.addSuppressed(th6);
                    }
                } else {
                    reader.close();
                }
            }
            if (in != null) {
                if (0 != 0) {
                    in.close();
                } else {
                    in.close();
                }
            }
            if (is != null) {
                if (0 == 0) {
                    is.close();
                    return;
                }
                try {
                    is.close();
                } catch (Throwable th7) {
                    th.addSuppressed(th7);
                }
            }
        } catch (Throwable th8) {
            try {
                throw th8;
            } catch (Throwable th9) {
                if (is != null) {
                    if (th8 != null) {
                        try {
                            is.close();
                        } catch (Throwable th10) {
                            th8.addSuppressed(th10);
                        }
                    } else {
                        is.close();
                    }
                }
                throw th9;
            }
        }
    }

    List<T> loadServices(Class<T> serviceType, LinkedHashSet<String> servicesFound) throws IOException {
        ClassLoader loader = this.servletContext.getClassLoader();
        List<T> services = new ArrayList<>(servicesFound.size());
        Iterator<String> it = servicesFound.iterator();
        while (it.hasNext()) {
            String serviceClass = it.next();
            try {
                Class<?> clazz = Class.forName(serviceClass, true, loader);
                services.add(serviceType.cast(clazz.getConstructor(new Class[0]).newInstance(new Object[0])));
            } catch (ClassCastException | ReflectiveOperationException e) {
                throw new IOException(e);
            }
        }
        return Collections.unmodifiableList(services);
    }
}