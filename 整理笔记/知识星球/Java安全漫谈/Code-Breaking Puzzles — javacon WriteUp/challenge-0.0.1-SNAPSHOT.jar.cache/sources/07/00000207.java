package ch.qos.logback.core.util;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.rolling.helper.FileNamePattern;
import ch.qos.logback.core.spi.ContextAwareBase;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/util/ContextUtil.class */
public class ContextUtil extends ContextAwareBase {
    public ContextUtil(Context context) {
        setContext(context);
    }

    public static String getLocalHostName() throws UnknownHostException, SocketException {
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            return localhost.getHostName();
        } catch (UnknownHostException e) {
            return getLocalAddressAsString();
        }
    }

    private static String getLocalAddressAsString() throws UnknownHostException, SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces != null && interfaces.hasMoreElements()) {
            Enumeration<InetAddress> addresses = interfaces.nextElement().getInetAddresses();
            while (addresses != null && addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                if (acceptableAddress(address)) {
                    return address.getHostAddress();
                }
            }
        }
        throw new UnknownHostException();
    }

    private static boolean acceptableAddress(InetAddress address) {
        return (address == null || address.isLoopbackAddress() || address.isAnyLocalAddress() || address.isLinkLocalAddress()) ? false : true;
    }

    public String safelyGetLocalHostName() {
        try {
            String localhostName = getLocalHostName();
            return localhostName;
        } catch (SecurityException e) {
            addError("Failed to get local hostname", e);
            return CoreConstants.UNKNOWN_LOCALHOST;
        } catch (SocketException e2) {
            addError("Failed to get local hostname", e2);
            return CoreConstants.UNKNOWN_LOCALHOST;
        } catch (UnknownHostException e3) {
            addError("Failed to get local hostname", e3);
            return CoreConstants.UNKNOWN_LOCALHOST;
        }
    }

    public void addProperties(Properties props) {
        if (props == null) {
            return;
        }
        for (String key : props.keySet()) {
            this.context.putProperty(key, props.getProperty(key));
        }
    }

    public static Map<String, String> getFilenameCollisionMap(Context context) {
        if (context == null) {
            return null;
        }
        Map<String, String> map = (Map) context.getObject(CoreConstants.FA_FILENAME_COLLISION_MAP);
        return map;
    }

    public static Map<String, FileNamePattern> getFilenamePatternCollisionMap(Context context) {
        if (context == null) {
            return null;
        }
        Map<String, FileNamePattern> map = (Map) context.getObject(CoreConstants.RFA_FILENAME_PATTERN_COLLISION_MAP);
        return map;
    }

    public void addGroovyPackages(List<String> frameworkPackages) {
        addFrameworkPackage(frameworkPackages, "org.codehaus.groovy.runtime");
    }

    public void addFrameworkPackage(List<String> frameworkPackages, String packageName) {
        if (!frameworkPackages.contains(packageName)) {
            frameworkPackages.add(packageName);
        }
    }
}