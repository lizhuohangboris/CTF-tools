package org.springframework.boot.autoconfigure.orm.jpa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/orm/jpa/HibernateSettings.class */
public class HibernateSettings {
    private Supplier<String> ddlAuto;
    private Collection<HibernatePropertiesCustomizer> hibernatePropertiesCustomizers;

    public HibernateSettings ddlAuto(Supplier<String> ddlAuto) {
        this.ddlAuto = ddlAuto;
        return this;
    }

    public String getDdlAuto() {
        if (this.ddlAuto != null) {
            return this.ddlAuto.get();
        }
        return null;
    }

    public HibernateSettings hibernatePropertiesCustomizers(Collection<HibernatePropertiesCustomizer> hibernatePropertiesCustomizers) {
        this.hibernatePropertiesCustomizers = new ArrayList(hibernatePropertiesCustomizers);
        return this;
    }

    public Collection<HibernatePropertiesCustomizer> getHibernatePropertiesCustomizers() {
        return this.hibernatePropertiesCustomizers;
    }
}