package org.springframework.boot.autoconfigure.jdbc;

import javax.sql.DataSource;
import org.springframework.context.ApplicationEvent;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jdbc/DataSourceSchemaCreatedEvent.class */
public class DataSourceSchemaCreatedEvent extends ApplicationEvent {
    public DataSourceSchemaCreatedEvent(DataSource source) {
        super(source);
    }
}