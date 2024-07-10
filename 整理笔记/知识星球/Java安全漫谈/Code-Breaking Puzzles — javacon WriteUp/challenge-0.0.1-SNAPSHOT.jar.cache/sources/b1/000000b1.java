package ch.qos.logback.core;

import ch.qos.logback.core.joran.spi.ConsoleTarget;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.WarnStatus;
import ch.qos.logback.core.util.EnvUtil;
import ch.qos.logback.core.util.OptionHelper;
import java.io.OutputStream;
import java.util.Arrays;
import org.springframework.beans.PropertyAccessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/ConsoleAppender.class */
public class ConsoleAppender<E> extends OutputStreamAppender<E> {
    protected ConsoleTarget target = ConsoleTarget.SystemOut;
    protected boolean withJansi = false;
    private static final String WindowsAnsiOutputStream_CLASS_NAME = "org.fusesource.jansi.WindowsAnsiOutputStream";

    public void setTarget(String value) {
        ConsoleTarget t = ConsoleTarget.findByName(value.trim());
        if (t == null) {
            targetWarn(value);
        } else {
            this.target = t;
        }
    }

    public String getTarget() {
        return this.target.getName();
    }

    private void targetWarn(String val) {
        Status status = new WarnStatus(PropertyAccessor.PROPERTY_KEY_PREFIX + val + "] should be one of " + Arrays.toString(ConsoleTarget.values()), this);
        status.add(new WarnStatus("Using previously set target, System.out by default.", this));
        addStatus(status);
    }

    @Override // ch.qos.logback.core.OutputStreamAppender, ch.qos.logback.core.UnsynchronizedAppenderBase, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        OutputStream targetStream = this.target.getStream();
        if (EnvUtil.isWindows() && this.withJansi) {
            targetStream = getTargetStreamForWindows(targetStream);
        }
        setOutputStream(targetStream);
        super.start();
    }

    private OutputStream getTargetStreamForWindows(OutputStream targetStream) {
        try {
            addInfo("Enabling JANSI WindowsAnsiOutputStream for the console.");
            Object windowsAnsiOutputStream = OptionHelper.instantiateByClassNameAndParameter(WindowsAnsiOutputStream_CLASS_NAME, Object.class, this.context, OutputStream.class, targetStream);
            return (OutputStream) windowsAnsiOutputStream;
        } catch (Exception e) {
            addWarn("Failed to create WindowsAnsiOutputStream. Falling back on the default stream.", e);
            return targetStream;
        }
    }

    public boolean isWithJansi() {
        return this.withJansi;
    }

    public void setWithJansi(boolean withJansi) {
        this.withJansi = withJansi;
    }
}