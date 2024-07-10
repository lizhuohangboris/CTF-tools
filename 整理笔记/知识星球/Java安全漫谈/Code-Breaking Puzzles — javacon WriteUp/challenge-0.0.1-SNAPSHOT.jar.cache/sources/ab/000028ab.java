package org.thymeleaf.spring5.context;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.ui.context.Theme;
import org.springframework.validation.Errors;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/context/IThymeleafRequestContext.class */
public interface IThymeleafRequestContext {
    MessageSource getMessageSource();

    Map<String, Object> getModel();

    Locale getLocale();

    TimeZone getTimeZone();

    void changeLocale(Locale locale);

    void changeLocale(Locale locale, TimeZone timeZone);

    void setDefaultHtmlEscape(boolean z);

    boolean isDefaultHtmlEscape();

    Boolean getDefaultHtmlEscape();

    String getContextPath();

    String getContextUrl(String str);

    String getContextUrl(String str, Map<String, ?> map);

    String getRequestPath();

    String getQueryString();

    String getMessage(String str, String str2);

    String getMessage(String str, Object[] objArr, String str2);

    String getMessage(String str, List<?> list, String str2);

    String getMessage(String str, Object[] objArr, String str2, boolean z);

    String getMessage(String str) throws NoSuchMessageException;

    String getMessage(String str, Object[] objArr) throws NoSuchMessageException;

    String getMessage(String str, List<?> list) throws NoSuchMessageException;

    String getMessage(String str, Object[] objArr, boolean z) throws NoSuchMessageException;

    String getMessage(MessageSourceResolvable messageSourceResolvable) throws NoSuchMessageException;

    String getMessage(MessageSourceResolvable messageSourceResolvable, boolean z) throws NoSuchMessageException;

    Optional<Errors> getErrors(String str);

    Optional<Errors> getErrors(String str, boolean z);

    Theme getTheme();

    IThymeleafRequestDataValueProcessor getRequestDataValueProcessor();

    IThymeleafBindStatus getBindStatus(String str) throws IllegalStateException;

    IThymeleafBindStatus getBindStatus(String str, boolean z) throws IllegalStateException;
}