package org.springframework.boot.autoconfigure.cloud;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.config.java.CloudScanConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@ConditionalOnClass({CloudScanConfiguration.class})
@Profile({"cloud"})
@ConditionalOnMissingBean({Cloud.class})
@AutoConfigureOrder(-2147483628)
@Import({CloudScanConfiguration.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/cloud/CloudServiceConnectorsAutoConfiguration.class */
public class CloudServiceConnectorsAutoConfiguration {
    public static final int ORDER = -2147483628;
}