package org.springframework.web.servlet.mvc.support;

import java.util.Collection;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.ui.Model;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/support/RedirectAttributes.class */
public interface RedirectAttributes extends Model {
    @Override // org.springframework.ui.Model
    RedirectAttributes addAttribute(String str, @Nullable Object obj);

    @Override // org.springframework.ui.Model
    RedirectAttributes addAttribute(Object obj);

    @Override // org.springframework.ui.Model
    RedirectAttributes addAllAttributes(Collection<?> collection);

    @Override // org.springframework.ui.Model
    RedirectAttributes mergeAttributes(Map<String, ?> map);

    RedirectAttributes addFlashAttribute(String str, @Nullable Object obj);

    RedirectAttributes addFlashAttribute(Object obj);

    Map<String, ?> getFlashAttributes();

    @Override // org.springframework.ui.Model
    /* bridge */ /* synthetic */ default Model mergeAttributes(Map map) {
        return mergeAttributes((Map<String, ?>) map);
    }

    @Override // org.springframework.ui.Model
    /* bridge */ /* synthetic */ default Model addAllAttributes(Collection collection) {
        return addAllAttributes((Collection<?>) collection);
    }
}