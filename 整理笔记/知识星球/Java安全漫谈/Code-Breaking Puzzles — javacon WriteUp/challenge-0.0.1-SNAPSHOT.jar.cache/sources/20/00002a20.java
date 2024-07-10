package org.unbescape;

import java.util.Properties;
import org.thymeleaf.engine.XMLDeclaration;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/unbescape-1.1.6.RELEASE.jar:org/unbescape/Unbescape.class */
public final class Unbescape {
    public static final String VERSION;
    public static final String BUILD_TIMESTAMP;
    public static final int VERSION_MAJOR;
    public static final int VERSION_MINOR;
    public static final int VERSION_BUILD;
    public static final String VERSION_TYPE;

    static {
        String version = null;
        String buildTimestamp = null;
        try {
            Properties properties = new Properties();
            properties.load(ClassLoaderUtils.loadResourceAsStream("org/unbescape/unbescape.properties"));
            version = properties.getProperty(XMLDeclaration.ATTRIBUTE_NAME_VERSION);
            buildTimestamp = properties.getProperty("build.date");
        } catch (Exception e) {
        }
        VERSION = version;
        BUILD_TIMESTAMP = buildTimestamp;
        if (VERSION == null || VERSION.trim().length() == 0) {
            VERSION_MAJOR = 0;
            VERSION_MINOR = 0;
            VERSION_BUILD = 0;
            VERSION_TYPE = "UNKNOWN";
            return;
        }
        try {
            String versionRemainder = VERSION;
            int separatorIdx = versionRemainder.indexOf(46);
            VERSION_MAJOR = Integer.parseInt(versionRemainder.substring(0, separatorIdx));
            String versionRemainder2 = versionRemainder.substring(separatorIdx + 1);
            int separatorIdx2 = versionRemainder2.indexOf(46);
            VERSION_MINOR = Integer.parseInt(versionRemainder2.substring(0, separatorIdx2));
            String versionRemainder3 = versionRemainder2.substring(separatorIdx2 + 1);
            int separatorIdx3 = versionRemainder3.indexOf(46);
            if (separatorIdx3 < 0) {
                separatorIdx3 = versionRemainder3.indexOf(45);
            }
            VERSION_BUILD = Integer.parseInt(versionRemainder3.substring(0, separatorIdx3));
            VERSION_TYPE = versionRemainder3.substring(separatorIdx3 + 1);
        } catch (Exception e2) {
            throw new ExceptionInInitializerError("Exception during initialization of Unbescape versioning utilities. Identified Unbescape version is '" + VERSION + "', which does not follow the {major}.{minor}.{build}[.|-]{type} scheme");
        }
    }

    public static boolean isVersionStableRelease() {
        return "RELEASE".equals(VERSION_TYPE);
    }

    private Unbescape() {
    }
}