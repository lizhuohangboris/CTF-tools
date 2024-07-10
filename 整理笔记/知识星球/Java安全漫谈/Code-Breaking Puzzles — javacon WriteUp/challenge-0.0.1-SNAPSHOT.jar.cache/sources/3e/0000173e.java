package org.springframework.boot.autoconfigure.jooq;

import java.sql.SQLException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jooq.ExecuteContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultExecuteListener;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.jdbc.support.SQLStateSQLExceptionTranslator;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jooq/JooqExceptionTranslator.class */
public class JooqExceptionTranslator extends DefaultExecuteListener {
    private static final Log logger = LogFactory.getLog(JooqExceptionTranslator.class);

    public void exception(ExecuteContext context) {
        SQLExceptionTranslator translator = getTranslator(context);
        SQLException sqlException = context.sqlException();
        while (true) {
            SQLException exception = sqlException;
            if (exception != null) {
                handle(context, translator, exception);
                sqlException = exception.getNextException();
            } else {
                return;
            }
        }
    }

    private SQLExceptionTranslator getTranslator(ExecuteContext context) {
        String dbName;
        SQLDialect dialect = context.configuration().dialect();
        if (dialect != null && dialect.thirdParty() != null && (dbName = dialect.thirdParty().springDbName()) != null) {
            return new SQLErrorCodeSQLExceptionTranslator(dbName);
        }
        return new SQLStateSQLExceptionTranslator();
    }

    private void handle(ExecuteContext context, SQLExceptionTranslator translator, SQLException exception) {
        DataAccessException translated = translate(context, translator, exception);
        if (exception.getNextException() == null) {
            context.exception(translated);
        } else {
            logger.error("Execution of SQL statement failed.", translated);
        }
    }

    private DataAccessException translate(ExecuteContext context, SQLExceptionTranslator translator, SQLException exception) {
        return translator.translate("jOOQ", context.sql(), exception);
    }
}