package org.hibernate.validator.internal.xml.mapping;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/xml/mapping/DefaultPackageStaxBuilder.class */
public class DefaultPackageStaxBuilder extends AbstractOneLineStringStaxBuilder {
    private static final String DEFAULT_PACKAGE_QNAME = "default-package";

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.xml.AbstractStaxBuilder
    public String getAcceptableQName() {
        return DEFAULT_PACKAGE_QNAME;
    }
}