package org.apache.logging.log4j.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/util/FilteredObjectInputStream.class */
public class FilteredObjectInputStream extends ObjectInputStream {
    private static final List<String> REQUIRED_JAVA_CLASSES = Arrays.asList("java.math.BigDecimal", "java.math.BigInteger", "java.rmi.MarshalledObject", "[B");
    private static final List<String> REQUIRED_JAVA_PACKAGES = Arrays.asList("java.lang.", "java.time", "java.util.", "org.apache.logging.log4j.", "[Lorg.apache.logging.log4j.");
    private final Collection<String> allowedClasses;

    public FilteredObjectInputStream() throws IOException, SecurityException {
        this.allowedClasses = new HashSet();
    }

    public FilteredObjectInputStream(InputStream in) throws IOException {
        super(in);
        this.allowedClasses = new HashSet();
    }

    public FilteredObjectInputStream(Collection<String> allowedClasses) throws IOException, SecurityException {
        this.allowedClasses = allowedClasses;
    }

    public FilteredObjectInputStream(InputStream in, Collection<String> allowedClasses) throws IOException {
        super(in);
        this.allowedClasses = allowedClasses;
    }

    public Collection<String> getAllowedClasses() {
        return this.allowedClasses;
    }

    @Override // java.io.ObjectInputStream
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        String name = desc.getName();
        if (!isAllowedByDefault(name) && !this.allowedClasses.contains(name)) {
            throw new InvalidObjectException("Class is not allowed for deserialization: " + name);
        }
        return super.resolveClass(desc);
    }

    private static boolean isAllowedByDefault(String name) {
        return isRequiredPackage(name) || REQUIRED_JAVA_CLASSES.contains(name);
    }

    private static boolean isRequiredPackage(String name) {
        for (String packageName : REQUIRED_JAVA_PACKAGES) {
            if (name.startsWith(packageName)) {
                return true;
            }
        }
        return false;
    }
}