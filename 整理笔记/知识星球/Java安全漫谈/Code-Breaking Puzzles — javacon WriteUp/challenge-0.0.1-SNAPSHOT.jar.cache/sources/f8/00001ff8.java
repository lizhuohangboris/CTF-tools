package org.springframework.format.number;

import java.util.Set;
import org.springframework.context.support.EmbeddedValueResolutionSupport;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Formatter;
import org.springframework.format.Parser;
import org.springframework.format.Printer;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/format/number/NumberFormatAnnotationFormatterFactory.class */
public class NumberFormatAnnotationFormatterFactory extends EmbeddedValueResolutionSupport implements AnnotationFormatterFactory<NumberFormat> {
    @Override // org.springframework.format.AnnotationFormatterFactory
    public /* bridge */ /* synthetic */ Parser getParser(NumberFormat numberFormat, Class cls) {
        return getParser2(numberFormat, (Class<?>) cls);
    }

    @Override // org.springframework.format.AnnotationFormatterFactory
    public /* bridge */ /* synthetic */ Printer getPrinter(NumberFormat numberFormat, Class cls) {
        return getPrinter2(numberFormat, (Class<?>) cls);
    }

    @Override // org.springframework.format.AnnotationFormatterFactory
    public Set<Class<?>> getFieldTypes() {
        return NumberUtils.STANDARD_NUMBER_TYPES;
    }

    /* renamed from: getPrinter  reason: avoid collision after fix types in other method */
    public Printer<Number> getPrinter2(NumberFormat annotation, Class<?> fieldType) {
        return configureFormatterFrom(annotation);
    }

    /* renamed from: getParser  reason: avoid collision after fix types in other method */
    public Parser<Number> getParser2(NumberFormat annotation, Class<?> fieldType) {
        return configureFormatterFrom(annotation);
    }

    private Formatter<Number> configureFormatterFrom(NumberFormat annotation) {
        String pattern = resolveEmbeddedValue(annotation.pattern());
        if (StringUtils.hasLength(pattern)) {
            return new NumberStyleFormatter(pattern);
        }
        NumberFormat.Style style = annotation.style();
        if (style == NumberFormat.Style.CURRENCY) {
            return new CurrencyStyleFormatter();
        }
        if (style == NumberFormat.Style.PERCENT) {
            return new PercentStyleFormatter();
        }
        return new NumberStyleFormatter();
    }
}