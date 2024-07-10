package ch.qos.logback.core.db;

import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.joran.util.PropertySetter;
import ch.qos.logback.core.joran.util.beans.BeanDescriptionCache;
import ch.qos.logback.core.util.OptionHelper;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import org.xml.sax.Attributes;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/db/BindDataSourceToJNDIAction.class */
public class BindDataSourceToJNDIAction extends Action {
    static final String DATA_SOURCE_CLASS = "dataSourceClass";
    static final String URL = "url";
    static final String USER = "user";
    static final String PASSWORD = "password";
    private final BeanDescriptionCache beanDescriptionCache;

    public BindDataSourceToJNDIAction(BeanDescriptionCache beanDescriptionCache) {
        this.beanDescriptionCache = beanDescriptionCache;
    }

    @Override // ch.qos.logback.core.joran.action.Action
    public void begin(InterpretationContext ec, String localName, Attributes attributes) {
        String dsClassName = ec.getProperty(DATA_SOURCE_CLASS);
        if (OptionHelper.isEmpty(dsClassName)) {
            addWarn("dsClassName is a required parameter");
            ec.addError("dsClassName is a required parameter");
            return;
        }
        String urlStr = ec.getProperty("url");
        String userStr = ec.getProperty("user");
        String passwordStr = ec.getProperty("password");
        try {
            DataSource ds = (DataSource) OptionHelper.instantiateByClassName(dsClassName, DataSource.class, this.context);
            PropertySetter setter = new PropertySetter(this.beanDescriptionCache, ds);
            setter.setContext(this.context);
            if (!OptionHelper.isEmpty(urlStr)) {
                setter.setProperty("url", urlStr);
            }
            if (!OptionHelper.isEmpty(userStr)) {
                setter.setProperty("user", userStr);
            }
            if (!OptionHelper.isEmpty(passwordStr)) {
                setter.setProperty("password", passwordStr);
            }
            new InitialContext().rebind("dataSource", ds);
        } catch (Exception oops) {
            addError("Could not bind  datasource. Reported error follows.", oops);
            ec.addError("Could not not bind  datasource of type [" + dsClassName + "].");
        }
    }

    @Override // ch.qos.logback.core.joran.action.Action
    public void end(InterpretationContext ec, String name) {
    }
}