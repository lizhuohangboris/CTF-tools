package org.springframework.beans;

import java.io.PrintStream;
import java.io.PrintWriter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/PropertyBatchUpdateException.class */
public class PropertyBatchUpdateException extends BeansException {
    private final PropertyAccessException[] propertyAccessExceptions;

    public PropertyBatchUpdateException(PropertyAccessException[] propertyAccessExceptions) {
        super(null, null);
        Assert.notEmpty(propertyAccessExceptions, "At least 1 PropertyAccessException required");
        this.propertyAccessExceptions = propertyAccessExceptions;
    }

    public final int getExceptionCount() {
        return this.propertyAccessExceptions.length;
    }

    public final PropertyAccessException[] getPropertyAccessExceptions() {
        return this.propertyAccessExceptions;
    }

    @Nullable
    public PropertyAccessException getPropertyAccessException(String propertyName) {
        PropertyAccessException[] propertyAccessExceptionArr;
        for (PropertyAccessException pae : this.propertyAccessExceptions) {
            if (ObjectUtils.nullSafeEquals(propertyName, pae.getPropertyName())) {
                return pae;
            }
        }
        return null;
    }

    @Override // org.springframework.core.NestedRuntimeException, java.lang.Throwable
    public String getMessage() {
        StringBuilder sb = new StringBuilder("Failed properties: ");
        for (int i = 0; i < this.propertyAccessExceptions.length; i++) {
            sb.append(this.propertyAccessExceptions[i].getMessage());
            if (i < this.propertyAccessExceptions.length - 1) {
                sb.append("; ");
            }
        }
        return sb.toString();
    }

    @Override // java.lang.Throwable
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getName()).append("; nested PropertyAccessExceptions (");
        sb.append(getExceptionCount()).append(") are:");
        for (int i = 0; i < this.propertyAccessExceptions.length; i++) {
            sb.append('\n').append("PropertyAccessException ").append(i + 1).append(": ");
            sb.append(this.propertyAccessExceptions[i]);
        }
        return sb.toString();
    }

    @Override // java.lang.Throwable
    public void printStackTrace(PrintStream ps) {
        synchronized (ps) {
            ps.println(getClass().getName() + "; nested PropertyAccessException details (" + getExceptionCount() + ") are:");
            for (int i = 0; i < this.propertyAccessExceptions.length; i++) {
                ps.println("PropertyAccessException " + (i + 1) + ":");
                this.propertyAccessExceptions[i].printStackTrace(ps);
            }
        }
    }

    @Override // java.lang.Throwable
    public void printStackTrace(PrintWriter pw) {
        synchronized (pw) {
            pw.println(getClass().getName() + "; nested PropertyAccessException details (" + getExceptionCount() + ") are:");
            for (int i = 0; i < this.propertyAccessExceptions.length; i++) {
                pw.println("PropertyAccessException " + (i + 1) + ":");
                this.propertyAccessExceptions[i].printStackTrace(pw);
            }
        }
    }

    @Override // org.springframework.core.NestedRuntimeException
    public boolean contains(@Nullable Class<?> exType) {
        PropertyAccessException[] propertyAccessExceptionArr;
        if (exType == null) {
            return false;
        }
        if (exType.isInstance(this)) {
            return true;
        }
        for (PropertyAccessException pae : this.propertyAccessExceptions) {
            if (pae.contains(exType)) {
                return true;
            }
        }
        return false;
    }
}