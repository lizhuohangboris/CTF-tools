package org.springframework.format.datetime.joda;

import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/format/datetime/joda/DateTimeFormatterFactoryBean.class */
public class DateTimeFormatterFactoryBean extends DateTimeFormatterFactory implements FactoryBean<DateTimeFormatter>, InitializingBean {
    @Nullable
    private DateTimeFormatter dateTimeFormatter;

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        this.dateTimeFormatter = createDateTimeFormatter();
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.beans.factory.FactoryBean
    @Nullable
    public DateTimeFormatter getObject() {
        return this.dateTimeFormatter;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public Class<?> getObjectType() {
        return DateTimeFormatter.class;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public boolean isSingleton() {
        return true;
    }
}