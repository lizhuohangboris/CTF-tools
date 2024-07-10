package org.springframework.boot.loader;

import java.lang.reflect.Method;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:org/springframework/boot/loader/MainMethodRunner.class */
public class MainMethodRunner {
    private final String mainClassName;
    private final String[] args;

    public MainMethodRunner(String mainClass, String[] args) {
        this.mainClassName = mainClass;
        this.args = args != null ? (String[]) args.clone() : null;
    }

    public void run() throws Exception {
        Class<?> mainClass = Thread.currentThread().getContextClassLoader().loadClass(this.mainClassName);
        Method mainMethod = mainClass.getDeclaredMethod("main", String[].class);
        mainMethod.invoke(null, this.args);
    }
}