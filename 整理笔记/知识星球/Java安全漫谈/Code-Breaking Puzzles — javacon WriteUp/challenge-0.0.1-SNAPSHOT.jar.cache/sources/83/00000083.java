package ch.qos.logback.classic.spi;

import java.io.Serializable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/spi/ClassPackagingData.class */
public class ClassPackagingData implements Serializable {
    private static final long serialVersionUID = -804643281218337001L;
    final String codeLocation;
    final String version;
    private final boolean exact;

    public ClassPackagingData(String codeLocation, String version) {
        this.codeLocation = codeLocation;
        this.version = version;
        this.exact = true;
    }

    public ClassPackagingData(String classLocation, String version, boolean exact) {
        this.codeLocation = classLocation;
        this.version = version;
        this.exact = exact;
    }

    public String getCodeLocation() {
        return this.codeLocation;
    }

    public String getVersion() {
        return this.version;
    }

    public boolean isExact() {
        return this.exact;
    }

    public int hashCode() {
        int result = (31 * 1) + (this.codeLocation == null ? 0 : this.codeLocation.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ClassPackagingData other = (ClassPackagingData) obj;
        if (this.codeLocation == null) {
            if (other.codeLocation != null) {
                return false;
            }
        } else if (!this.codeLocation.equals(other.codeLocation)) {
            return false;
        }
        if (this.exact != other.exact) {
            return false;
        }
        if (this.version == null) {
            if (other.version != null) {
                return false;
            }
            return true;
        } else if (!this.version.equals(other.version)) {
            return false;
        } else {
            return true;
        }
    }
}