package org.springframework.boot.web.embedded.tomcat;

import java.lang.reflect.Method;
import org.apache.catalina.Context;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/embedded/tomcat/TomcatErrorPage.class */
public class TomcatErrorPage {
    private static final String ERROR_PAGE_CLASS = "org.apache.tomcat.util.descriptor.web.ErrorPage";
    private final String location;
    private final String exceptionType;
    private final int errorCode;
    private final Object nativePage = createNativePage();

    /* JADX INFO: Access modifiers changed from: package-private */
    public TomcatErrorPage(ErrorPage errorPage) {
        this.location = errorPage.getPath();
        this.exceptionType = errorPage.getExceptionName();
        this.errorCode = errorPage.getStatusCode();
    }

    private Object createNativePage() {
        try {
            if (ClassUtils.isPresent(ERROR_PAGE_CLASS, null)) {
                return BeanUtils.instantiateClass(ClassUtils.forName(ERROR_PAGE_CLASS, null));
            }
            return null;
        } catch (ClassNotFoundException | LinkageError e) {
            return null;
        }
    }

    public void addToContext(Context context) {
        Assert.state(this.nativePage != null, "No Tomcat 8 detected so no native error page exists");
        if (ClassUtils.isPresent(ERROR_PAGE_CLASS, null)) {
            org.apache.tomcat.util.descriptor.web.ErrorPage errorPage = (org.apache.tomcat.util.descriptor.web.ErrorPage) this.nativePage;
            errorPage.setLocation(this.location);
            errorPage.setErrorCode(this.errorCode);
            errorPage.setExceptionType(this.exceptionType);
            context.addErrorPage(errorPage);
            return;
        }
        callMethod(this.nativePage, "setLocation", this.location, String.class);
        callMethod(this.nativePage, "setErrorCode", Integer.valueOf(this.errorCode), Integer.TYPE);
        callMethod(this.nativePage, "setExceptionType", this.exceptionType, String.class);
        callMethod(context, "addErrorPage", this.nativePage, this.nativePage.getClass());
    }

    private void callMethod(Object target, String name, Object value, Class<?> type) {
        Method method = ReflectionUtils.findMethod(target.getClass(), name, type);
        ReflectionUtils.invokeMethod(method, target, value);
    }
}