package org.springframework.boot.web.reactive.result.view;

import com.samskivert.mustache.Mustache;
import org.springframework.web.reactive.result.view.AbstractUrlBasedView;
import org.springframework.web.reactive.result.view.UrlBasedViewResolver;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/reactive/result/view/MustacheViewResolver.class */
public class MustacheViewResolver extends UrlBasedViewResolver {
    private final Mustache.Compiler compiler;
    private String charset;

    public MustacheViewResolver() {
        this.compiler = Mustache.compiler();
        setViewClass(requiredViewClass());
    }

    public MustacheViewResolver(Mustache.Compiler compiler) {
        this.compiler = compiler;
        setViewClass(requiredViewClass());
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    protected Class<?> requiredViewClass() {
        return MustacheView.class;
    }

    protected AbstractUrlBasedView createView(String viewName) {
        MustacheView view = (MustacheView) super.createView(viewName);
        view.setCompiler(this.compiler);
        view.setCharset(this.charset);
        return view;
    }
}