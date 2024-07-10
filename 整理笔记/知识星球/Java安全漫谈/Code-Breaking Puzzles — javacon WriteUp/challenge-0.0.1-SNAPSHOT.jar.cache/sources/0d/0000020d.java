package ch.qos.logback.core.util;

import java.util.ArrayList;
import java.util.List;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/util/EnvUtil.class */
public class EnvUtil {
    private static boolean isJDK_N_OrHigher(int n) {
        List<String> versionList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            versionList.add("1." + (n + i));
        }
        String javaVersion = System.getProperty("java.version");
        if (javaVersion == null) {
            return false;
        }
        for (String v : versionList) {
            if (javaVersion.startsWith(v)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isJDK5() {
        return isJDK_N_OrHigher(5);
    }

    public static boolean isJDK6OrHigher() {
        return isJDK_N_OrHigher(6);
    }

    public static boolean isJDK7OrHigher() {
        return isJDK_N_OrHigher(7);
    }

    public static boolean isJaninoAvailable() {
        ClassLoader classLoader = EnvUtil.class.getClassLoader();
        try {
            Class<?> bindingClass = classLoader.loadClass("org.codehaus.janino.ScriptEvaluator");
            return bindingClass != null;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isWindows() {
        String os = System.getProperty("os.name");
        return os.startsWith("Windows");
    }
}