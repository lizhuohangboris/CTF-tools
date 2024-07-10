package org.springframework.boot.orm.jpa.hibernate;

import java.util.Locale;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/orm/jpa/hibernate/SpringPhysicalNamingStrategy.class */
public class SpringPhysicalNamingStrategy implements PhysicalNamingStrategy {
    public Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return apply(name, jdbcEnvironment);
    }

    public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return apply(name, jdbcEnvironment);
    }

    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return apply(name, jdbcEnvironment);
    }

    public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return apply(name, jdbcEnvironment);
    }

    public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return apply(name, jdbcEnvironment);
    }

    private Identifier apply(Identifier name, JdbcEnvironment jdbcEnvironment) {
        if (name == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder(name.getText().replace('.', '_'));
        int i = 1;
        while (i < builder.length() - 1) {
            if (isUnderscoreRequired(builder.charAt(i - 1), builder.charAt(i), builder.charAt(i + 1))) {
                int i2 = i;
                i++;
                builder.insert(i2, '_');
            }
            i++;
        }
        return getIdentifier(builder.toString(), name.isQuoted(), jdbcEnvironment);
    }

    protected Identifier getIdentifier(String name, boolean quoted, JdbcEnvironment jdbcEnvironment) {
        if (isCaseInsensitive(jdbcEnvironment)) {
            name = name.toLowerCase(Locale.ROOT);
        }
        return new Identifier(name, quoted);
    }

    protected boolean isCaseInsensitive(JdbcEnvironment jdbcEnvironment) {
        return true;
    }

    private boolean isUnderscoreRequired(char before, char current, char after) {
        return Character.isLowerCase(before) && Character.isUpperCase(current) && Character.isLowerCase(after);
    }
}