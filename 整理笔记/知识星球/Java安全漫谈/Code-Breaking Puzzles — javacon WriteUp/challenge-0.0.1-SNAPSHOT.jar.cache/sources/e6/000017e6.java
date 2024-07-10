package org.springframework.boot.autoconfigure.session;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.session.mongodb")
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/session/MongoSessionProperties.class */
public class MongoSessionProperties {
    private String collectionName = "sessions";

    public String getCollectionName() {
        return this.collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }
}