package org.springframework.format.support;

import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.DateFormatterRegistrar;
import org.springframework.format.datetime.joda.JodaTimeFormatterRegistrar;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.format.number.NumberFormatAnnotationFormatterFactory;
import org.springframework.format.number.money.CurrencyUnitFormatter;
import org.springframework.format.number.money.Jsr354NumberFormatAnnotationFormatterFactory;
import org.springframework.format.number.money.MonetaryAmountFormatter;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringValueResolver;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/format/support/DefaultFormattingConversionService.class */
public class DefaultFormattingConversionService extends FormattingConversionService {
    private static final boolean jsr354Present;
    private static final boolean jodaTimePresent;

    static {
        ClassLoader classLoader = DefaultFormattingConversionService.class.getClassLoader();
        jsr354Present = ClassUtils.isPresent("javax.money.MonetaryAmount", classLoader);
        jodaTimePresent = ClassUtils.isPresent("org.joda.time.LocalDate", classLoader);
    }

    public DefaultFormattingConversionService() {
        this(null, true);
    }

    public DefaultFormattingConversionService(boolean registerDefaultFormatters) {
        this(null, registerDefaultFormatters);
    }

    public DefaultFormattingConversionService(@Nullable StringValueResolver embeddedValueResolver, boolean registerDefaultFormatters) {
        if (embeddedValueResolver != null) {
            setEmbeddedValueResolver(embeddedValueResolver);
        }
        DefaultConversionService.addDefaultConverters(this);
        if (registerDefaultFormatters) {
            addDefaultFormatters(this);
        }
    }

    public static void addDefaultFormatters(FormatterRegistry formatterRegistry) {
        formatterRegistry.addFormatterForFieldAnnotation(new NumberFormatAnnotationFormatterFactory());
        if (jsr354Present) {
            formatterRegistry.addFormatter(new CurrencyUnitFormatter());
            formatterRegistry.addFormatter(new MonetaryAmountFormatter());
            formatterRegistry.addFormatterForFieldAnnotation(new Jsr354NumberFormatAnnotationFormatterFactory());
        }
        new DateTimeFormatterRegistrar().registerFormatters(formatterRegistry);
        if (jodaTimePresent) {
            new JodaTimeFormatterRegistrar().registerFormatters(formatterRegistry);
        } else {
            new DateFormatterRegistrar().registerFormatters(formatterRegistry);
        }
    }
}