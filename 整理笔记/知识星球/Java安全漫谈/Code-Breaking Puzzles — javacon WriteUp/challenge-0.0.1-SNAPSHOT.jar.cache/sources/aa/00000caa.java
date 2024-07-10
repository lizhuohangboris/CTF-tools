package org.apache.tomcat.util.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/file/ConfigFileLoader.class */
public class ConfigFileLoader {
    private static final StringManager sm = StringManager.getManager(ConfigFileLoader.class.getPackage().getName());
    private static final File CATALINA_BASE_FILE;
    private static final URI CATALINA_BASE_URI;

    static {
        String catalinaBase = System.getProperty("catalina.base");
        if (catalinaBase != null) {
            CATALINA_BASE_FILE = new File(catalinaBase);
            CATALINA_BASE_URI = CATALINA_BASE_FILE.toURI();
            return;
        }
        CATALINA_BASE_FILE = null;
        CATALINA_BASE_URI = null;
    }

    private ConfigFileLoader() {
    }

    public static InputStream getInputStream(String location) throws IOException {
        File f = new File(location);
        if (!f.isAbsolute()) {
            f = new File(CATALINA_BASE_FILE, location);
        }
        if (f.isFile()) {
            return new FileInputStream(f);
        }
        URI uri = getURI(location);
        try {
            URL url = uri.toURL();
            return url.openConnection().getInputStream();
        } catch (IllegalArgumentException e) {
            throw new IOException(sm.getString("configFileLoader.cannotObtainURL", location), e);
        }
    }

    public static URI getURI(String location) {
        URI uri;
        if (CATALINA_BASE_URI != null) {
            uri = CATALINA_BASE_URI.resolve(location);
        } else {
            uri = URI.create(location);
        }
        return uri;
    }
}