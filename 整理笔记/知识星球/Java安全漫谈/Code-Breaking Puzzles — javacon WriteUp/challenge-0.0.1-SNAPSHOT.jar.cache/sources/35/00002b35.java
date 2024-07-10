package org.yaml.snakeyaml.util;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/util/PlatformFeatureDetector.class */
public class PlatformFeatureDetector {
    private Boolean isRunningOnAndroid = null;

    public boolean isRunningOnAndroid() {
        if (this.isRunningOnAndroid == null) {
            String name = System.getProperty("java.runtime.name");
            this.isRunningOnAndroid = Boolean.valueOf(name != null && name.startsWith("Android Runtime"));
        }
        return this.isRunningOnAndroid.booleanValue();
    }
}