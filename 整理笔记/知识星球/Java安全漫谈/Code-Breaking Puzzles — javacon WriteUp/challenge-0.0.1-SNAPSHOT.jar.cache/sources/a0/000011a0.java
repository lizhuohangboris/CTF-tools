package org.hibernate.validator.internal.util.privilegedactions;

import java.net.URL;
import java.security.PrivilegedExceptionAction;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.SAXException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/privilegedactions/NewSchema.class */
public final class NewSchema implements PrivilegedExceptionAction<Schema> {
    private final SchemaFactory schemaFactory;
    private final URL url;

    public static NewSchema action(SchemaFactory schemaFactory, URL url) {
        return new NewSchema(schemaFactory, url);
    }

    public NewSchema(SchemaFactory schemaFactory, URL url) {
        this.schemaFactory = schemaFactory;
        this.url = url;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.security.PrivilegedExceptionAction
    public Schema run() throws SAXException {
        return this.schemaFactory.newSchema(this.url);
    }
}