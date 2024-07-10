package ch.qos.logback.classic.spi;

import ch.qos.logback.core.CoreConstants;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/spi/ThrowableProxy.class */
public class ThrowableProxy implements IThrowableProxy {
    private Throwable throwable;
    private String className;
    private String message;
    StackTraceElementProxy[] stackTraceElementProxyArray;
    int commonFrames;
    private ThrowableProxy cause;
    private ThrowableProxy[] suppressed;
    private transient PackagingDataCalculator packagingDataCalculator;
    private boolean calculatedPackageData = false;
    private static final Method GET_SUPPRESSED_METHOD;
    private static final ThrowableProxy[] NO_SUPPRESSED;

    static {
        Method method = null;
        try {
            method = Throwable.class.getMethod("getSuppressed", new Class[0]);
        } catch (NoSuchMethodException e) {
        }
        GET_SUPPRESSED_METHOD = method;
        NO_SUPPRESSED = new ThrowableProxy[0];
    }

    public ThrowableProxy(Throwable throwable) {
        this.suppressed = NO_SUPPRESSED;
        this.throwable = throwable;
        this.className = throwable.getClass().getName();
        this.message = throwable.getMessage();
        this.stackTraceElementProxyArray = ThrowableProxyUtil.steArrayToStepArray(throwable.getStackTrace());
        Throwable nested = throwable.getCause();
        if (nested != null) {
            this.cause = new ThrowableProxy(nested);
            this.cause.commonFrames = ThrowableProxyUtil.findNumberOfCommonFrames(nested.getStackTrace(), this.stackTraceElementProxyArray);
        }
        if (GET_SUPPRESSED_METHOD != null) {
            try {
                Object obj = GET_SUPPRESSED_METHOD.invoke(throwable, new Object[0]);
                if (obj instanceof Throwable[]) {
                    Throwable[] throwableSuppressed = (Throwable[]) obj;
                    if (throwableSuppressed.length > 0) {
                        this.suppressed = new ThrowableProxy[throwableSuppressed.length];
                        for (int i = 0; i < throwableSuppressed.length; i++) {
                            this.suppressed[i] = new ThrowableProxy(throwableSuppressed[i]);
                            this.suppressed[i].commonFrames = ThrowableProxyUtil.findNumberOfCommonFrames(throwableSuppressed[i].getStackTrace(), this.stackTraceElementProxyArray);
                        }
                    }
                }
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e2) {
            }
        }
    }

    public Throwable getThrowable() {
        return this.throwable;
    }

    @Override // ch.qos.logback.classic.spi.IThrowableProxy
    public String getMessage() {
        return this.message;
    }

    @Override // ch.qos.logback.classic.spi.IThrowableProxy
    public String getClassName() {
        return this.className;
    }

    @Override // ch.qos.logback.classic.spi.IThrowableProxy
    public StackTraceElementProxy[] getStackTraceElementProxyArray() {
        return this.stackTraceElementProxyArray;
    }

    @Override // ch.qos.logback.classic.spi.IThrowableProxy
    public int getCommonFrames() {
        return this.commonFrames;
    }

    @Override // ch.qos.logback.classic.spi.IThrowableProxy
    public IThrowableProxy getCause() {
        return this.cause;
    }

    @Override // ch.qos.logback.classic.spi.IThrowableProxy
    public IThrowableProxy[] getSuppressed() {
        return this.suppressed;
    }

    public PackagingDataCalculator getPackagingDataCalculator() {
        if (this.throwable != null && this.packagingDataCalculator == null) {
            this.packagingDataCalculator = new PackagingDataCalculator();
        }
        return this.packagingDataCalculator;
    }

    public void calculatePackagingData() {
        PackagingDataCalculator pdc;
        if (!this.calculatedPackageData && (pdc = getPackagingDataCalculator()) != null) {
            this.calculatedPackageData = true;
            pdc.calculate(this);
        }
    }

    public void fullDump() {
        StringBuilder builder = new StringBuilder();
        StackTraceElementProxy[] arr$ = this.stackTraceElementProxyArray;
        for (StackTraceElementProxy step : arr$) {
            String string = step.toString();
            builder.append('\t').append(string);
            ThrowableProxyUtil.subjoinPackagingData(builder, step);
            builder.append(CoreConstants.LINE_SEPARATOR);
        }
        System.out.println(builder.toString());
    }
}