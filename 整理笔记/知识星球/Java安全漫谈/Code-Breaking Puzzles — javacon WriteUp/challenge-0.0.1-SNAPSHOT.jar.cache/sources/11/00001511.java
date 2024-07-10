package org.springframework.boot.admin;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/admin/SpringApplicationAdminMXBean.class */
public interface SpringApplicationAdminMXBean {
    boolean isReady();

    boolean isEmbeddedWebApplication();

    String getProperty(String key);

    void shutdown();
}