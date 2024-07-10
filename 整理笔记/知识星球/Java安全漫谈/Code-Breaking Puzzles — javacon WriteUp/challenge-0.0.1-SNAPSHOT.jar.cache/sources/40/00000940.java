package org.apache.catalina.util;

import java.util.StringTokenizer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/util/Extension.class */
public final class Extension {
    private String extensionName = null;
    private String implementationURL = null;
    private String implementationVendor = null;
    private String implementationVendorId = null;
    private String implementationVersion = null;
    private String specificationVendor = null;
    private String specificationVersion = null;
    private boolean fulfilled = false;

    public String getExtensionName() {
        return this.extensionName;
    }

    public void setExtensionName(String extensionName) {
        this.extensionName = extensionName;
    }

    public String getImplementationURL() {
        return this.implementationURL;
    }

    public void setImplementationURL(String implementationURL) {
        this.implementationURL = implementationURL;
    }

    public String getImplementationVendor() {
        return this.implementationVendor;
    }

    public void setImplementationVendor(String implementationVendor) {
        this.implementationVendor = implementationVendor;
    }

    public String getImplementationVendorId() {
        return this.implementationVendorId;
    }

    public void setImplementationVendorId(String implementationVendorId) {
        this.implementationVendorId = implementationVendorId;
    }

    public String getImplementationVersion() {
        return this.implementationVersion;
    }

    public void setImplementationVersion(String implementationVersion) {
        this.implementationVersion = implementationVersion;
    }

    public String getSpecificationVendor() {
        return this.specificationVendor;
    }

    public void setSpecificationVendor(String specificationVendor) {
        this.specificationVendor = specificationVendor;
    }

    public String getSpecificationVersion() {
        return this.specificationVersion;
    }

    public void setSpecificationVersion(String specificationVersion) {
        this.specificationVersion = specificationVersion;
    }

    public void setFulfilled(boolean fulfilled) {
        this.fulfilled = fulfilled;
    }

    public boolean isFulfilled() {
        return this.fulfilled;
    }

    public boolean isCompatibleWith(Extension required) {
        if (this.extensionName == null || !this.extensionName.equals(required.getExtensionName())) {
            return false;
        }
        if (required.getSpecificationVersion() != null && !isNewer(this.specificationVersion, required.getSpecificationVersion())) {
            return false;
        }
        if (required.getImplementationVendorId() != null && (this.implementationVendorId == null || !this.implementationVendorId.equals(required.getImplementationVendorId()))) {
            return false;
        }
        if (required.getImplementationVersion() != null && !isNewer(this.implementationVersion, required.getImplementationVersion())) {
            return false;
        }
        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("Extension[");
        sb.append(this.extensionName);
        if (this.implementationURL != null) {
            sb.append(", implementationURL=");
            sb.append(this.implementationURL);
        }
        if (this.implementationVendor != null) {
            sb.append(", implementationVendor=");
            sb.append(this.implementationVendor);
        }
        if (this.implementationVendorId != null) {
            sb.append(", implementationVendorId=");
            sb.append(this.implementationVendorId);
        }
        if (this.implementationVersion != null) {
            sb.append(", implementationVersion=");
            sb.append(this.implementationVersion);
        }
        if (this.specificationVendor != null) {
            sb.append(", specificationVendor=");
            sb.append(this.specificationVendor);
        }
        if (this.specificationVersion != null) {
            sb.append(", specificationVersion=");
            sb.append(this.specificationVersion);
        }
        sb.append("]");
        return sb.toString();
    }

    private boolean isNewer(String first, String second) throws NumberFormatException {
        int fVersion;
        int sVersion;
        if (first == null || second == null) {
            return false;
        }
        if (first.equals(second)) {
            return true;
        }
        StringTokenizer fTok = new StringTokenizer(first, ".", true);
        StringTokenizer sTok = new StringTokenizer(second, ".", true);
        while (true) {
            if (fTok.hasMoreTokens() || sTok.hasMoreTokens()) {
                if (fTok.hasMoreTokens()) {
                    fVersion = Integer.parseInt(fTok.nextToken());
                } else {
                    fVersion = 0;
                }
                if (sTok.hasMoreTokens()) {
                    sVersion = Integer.parseInt(sTok.nextToken());
                } else {
                    sVersion = 0;
                }
                if (fVersion < sVersion) {
                    return false;
                }
                if (fVersion > sVersion) {
                    return true;
                }
                if (fTok.hasMoreTokens()) {
                    fTok.nextToken();
                }
                if (sTok.hasMoreTokens()) {
                    sTok.nextToken();
                }
            } else {
                return true;
            }
        }
    }
}