package org.apache.catalina.util;

import java.util.Locale;
import org.springframework.web.context.support.XmlWebApplicationContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/util/ContextName.class */
public final class ContextName {
    public static final String ROOT_NAME = "ROOT";
    private static final String VERSION_MARKER = "##";
    private static final String FWD_SLASH_REPLACEMENT = "#";
    private final String baseName;
    private final String path;
    private final String version;
    private final String name;

    public ContextName(String name, boolean stripFileExtension) {
        String tmp2;
        String tmp1 = name;
        String tmp12 = (tmp1.startsWith("/") ? tmp1.substring(1) : tmp1).replaceAll("/", "#");
        tmp12 = (tmp12.startsWith(VERSION_MARKER) || "".equals(tmp12)) ? "ROOT" + tmp12 : tmp12;
        if (stripFileExtension && (tmp12.toLowerCase(Locale.ENGLISH).endsWith(".war") || tmp12.toLowerCase(Locale.ENGLISH).endsWith(XmlWebApplicationContext.DEFAULT_CONFIG_LOCATION_SUFFIX))) {
            tmp12 = tmp12.substring(0, tmp12.length() - 4);
        }
        this.baseName = tmp12;
        int versionIndex = this.baseName.indexOf(VERSION_MARKER);
        if (versionIndex > -1) {
            this.version = this.baseName.substring(versionIndex + 2);
            tmp2 = this.baseName.substring(0, versionIndex);
        } else {
            this.version = "";
            tmp2 = this.baseName;
        }
        if ("ROOT".equals(tmp2)) {
            this.path = "";
        } else {
            this.path = "/" + tmp2.replaceAll("#", "/");
        }
        if (versionIndex > -1) {
            this.name = this.path + VERSION_MARKER + this.version;
        } else {
            this.name = this.path;
        }
    }

    public ContextName(String path, String version) {
        if (path == null || "/".equals(path) || "/ROOT".equals(path)) {
            this.path = "";
        } else {
            this.path = path;
        }
        if (version == null) {
            this.version = "";
        } else {
            this.version = version;
        }
        if ("".equals(this.version)) {
            this.name = this.path;
        } else {
            this.name = this.path + VERSION_MARKER + this.version;
        }
        StringBuilder tmp = new StringBuilder();
        if ("".equals(this.path)) {
            tmp.append("ROOT");
        } else {
            tmp.append(this.path.substring(1).replaceAll("/", "#"));
        }
        if (this.version.length() > 0) {
            tmp.append(VERSION_MARKER);
            tmp.append(this.version);
        }
        this.baseName = tmp.toString();
    }

    public String getBaseName() {
        return this.baseName;
    }

    public String getPath() {
        return this.path;
    }

    public String getVersion() {
        return this.version;
    }

    public String getName() {
        return this.name;
    }

    public String getDisplayName() {
        StringBuilder tmp = new StringBuilder();
        if ("".equals(this.path)) {
            tmp.append('/');
        } else {
            tmp.append(this.path);
        }
        if (!"".equals(this.version)) {
            tmp.append(VERSION_MARKER);
            tmp.append(this.version);
        }
        return tmp.toString();
    }

    public String toString() {
        return getDisplayName();
    }

    public static ContextName extractFromPath(String path) {
        String path2;
        String replaceAll = path.replaceAll("\\\\", "/");
        while (true) {
            path2 = replaceAll;
            if (!path2.endsWith("/")) {
                break;
            }
            replaceAll = path2.substring(0, path2.length() - 1);
        }
        int lastSegment = path2.lastIndexOf(47);
        if (lastSegment > 0) {
            path2 = path2.substring(lastSegment + 1);
        }
        return new ContextName(path2, true);
    }
}