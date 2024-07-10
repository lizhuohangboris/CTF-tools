package org.springframework.boot.cloud;

import org.springframework.core.env.Environment;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/cloud/CloudPlatform.class */
public enum CloudPlatform {
    CLOUD_FOUNDRY { // from class: org.springframework.boot.cloud.CloudPlatform.1
        @Override // org.springframework.boot.cloud.CloudPlatform
        public boolean isActive(Environment environment) {
            return environment.containsProperty("VCAP_APPLICATION") || environment.containsProperty("VCAP_SERVICES");
        }
    },
    HEROKU { // from class: org.springframework.boot.cloud.CloudPlatform.2
        @Override // org.springframework.boot.cloud.CloudPlatform
        public boolean isActive(Environment environment) {
            return environment.containsProperty("DYNO");
        }
    },
    SAP { // from class: org.springframework.boot.cloud.CloudPlatform.3
        @Override // org.springframework.boot.cloud.CloudPlatform
        public boolean isActive(Environment environment) {
            return environment.containsProperty("HC_LANDSCAPE");
        }
    };

    public abstract boolean isActive(Environment environment);

    public boolean isUsingForwardHeaders() {
        return true;
    }

    public static CloudPlatform getActive(Environment environment) {
        CloudPlatform[] values;
        if (environment != null) {
            for (CloudPlatform cloudPlatform : values()) {
                if (cloudPlatform.isActive(environment)) {
                    return cloudPlatform;
                }
            }
            return null;
        }
        return null;
    }
}