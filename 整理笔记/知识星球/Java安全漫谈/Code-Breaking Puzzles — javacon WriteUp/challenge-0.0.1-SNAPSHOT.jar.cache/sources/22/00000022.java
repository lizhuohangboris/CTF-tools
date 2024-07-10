package ch.qos.logback.classic.gaffer;

import ch.qos.logback.classic.ClassicConstants;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.status.ErrorStatus;
import ch.qos.logback.core.status.StatusManager;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/gaffer/GafferUtil.class */
public class GafferUtil {
    private static String ERROR_MSG = "Failed to instantiate ch.qos.logback.classic.gaffer.GafferConfigurator";

    public static void runGafferConfiguratorOn(LoggerContext loggerContext, Object origin, File configFile) {
        GafferConfigurator gafferConfigurator = newGafferConfiguratorInstance(loggerContext, origin);
        if (gafferConfigurator != null) {
            gafferConfigurator.run(configFile);
        }
    }

    public static void runGafferConfiguratorOn(LoggerContext loggerContext, Object origin, URL configFile) {
        GafferConfigurator gafferConfigurator = newGafferConfiguratorInstance(loggerContext, origin);
        if (gafferConfigurator != null) {
            gafferConfigurator.run(configFile);
        }
    }

    private static GafferConfigurator newGafferConfiguratorInstance(LoggerContext loggerContext, Object origin) {
        try {
            Class gcClass = Class.forName(ClassicConstants.GAFFER_CONFIGURATOR_FQCN);
            Constructor c = gcClass.getConstructor(LoggerContext.class);
            return (GafferConfigurator) c.newInstance(loggerContext);
        } catch (ClassNotFoundException e) {
            addError(loggerContext, origin, ERROR_MSG, e);
            return null;
        } catch (IllegalAccessException e2) {
            addError(loggerContext, origin, ERROR_MSG, e2);
            return null;
        } catch (InstantiationException e3) {
            addError(loggerContext, origin, ERROR_MSG, e3);
            return null;
        } catch (NoSuchMethodException e4) {
            addError(loggerContext, origin, ERROR_MSG, e4);
            return null;
        } catch (InvocationTargetException e5) {
            addError(loggerContext, origin, ERROR_MSG, e5);
            return null;
        }
    }

    private static void addError(LoggerContext context, Object origin, String msg) {
        addError(context, origin, msg, null);
    }

    private static void addError(LoggerContext context, Object origin, String msg, Throwable t) {
        StatusManager sm = context.getStatusManager();
        if (sm == null) {
            return;
        }
        sm.add(new ErrorStatus(msg, origin, t));
    }
}