package org.hibernate.validator;

import java.security.BasicPermission;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/HibernateValidatorPermission.class */
public class HibernateValidatorPermission extends BasicPermission {
    public static final HibernateValidatorPermission ACCESS_PRIVATE_MEMBERS = new HibernateValidatorPermission("accessPrivateMembers");

    public HibernateValidatorPermission(String name) {
        super(name);
    }

    public HibernateValidatorPermission(String name, String actions) {
        super(name, actions);
    }
}