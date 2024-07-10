package org.springframework.web.servlet.view.tiles3;

import org.apache.tiles.request.render.Renderer;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/view/tiles3/TilesViewResolver.class */
public class TilesViewResolver extends UrlBasedViewResolver {
    @Nullable
    private Renderer renderer;
    @Nullable
    private Boolean alwaysInclude;

    public TilesViewResolver() {
        setViewClass(requiredViewClass());
    }

    @Override // org.springframework.web.servlet.view.UrlBasedViewResolver
    protected Class<?> requiredViewClass() {
        return TilesView.class;
    }

    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
    }

    public void setAlwaysInclude(Boolean alwaysInclude) {
        this.alwaysInclude = alwaysInclude;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.view.UrlBasedViewResolver
    public TilesView buildView(String viewName) throws Exception {
        TilesView view = (TilesView) super.buildView(viewName);
        if (this.renderer != null) {
            view.setRenderer(this.renderer);
        }
        if (this.alwaysInclude != null) {
            view.setAlwaysInclude(this.alwaysInclude.booleanValue());
        }
        return view;
    }
}