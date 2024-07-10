package org.springframework.web.jsf;

import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/jsf/DecoratingNavigationHandler.class */
public abstract class DecoratingNavigationHandler extends NavigationHandler {
    @Nullable
    private NavigationHandler decoratedNavigationHandler;

    public abstract void handleNavigation(FacesContext facesContext, @Nullable String str, @Nullable String str2, @Nullable NavigationHandler navigationHandler);

    protected DecoratingNavigationHandler() {
    }

    protected DecoratingNavigationHandler(NavigationHandler originalNavigationHandler) {
        this.decoratedNavigationHandler = originalNavigationHandler;
    }

    @Nullable
    public final NavigationHandler getDecoratedNavigationHandler() {
        return this.decoratedNavigationHandler;
    }

    public final void handleNavigation(FacesContext facesContext, String fromAction, String outcome) {
        handleNavigation(facesContext, fromAction, outcome, this.decoratedNavigationHandler);
    }

    protected final void callNextHandlerInChain(FacesContext facesContext, @Nullable String fromAction, @Nullable String outcome, @Nullable NavigationHandler originalNavigationHandler) {
        NavigationHandler decoratedNavigationHandler = getDecoratedNavigationHandler();
        if (decoratedNavigationHandler instanceof DecoratingNavigationHandler) {
            DecoratingNavigationHandler decHandler = (DecoratingNavigationHandler) decoratedNavigationHandler;
            decHandler.handleNavigation(facesContext, fromAction, outcome, originalNavigationHandler);
        } else if (decoratedNavigationHandler != null) {
            decoratedNavigationHandler.handleNavigation(facesContext, fromAction, outcome);
        } else if (originalNavigationHandler != null) {
            originalNavigationHandler.handleNavigation(facesContext, fromAction, outcome);
        }
    }
}