package ch.qos.logback.core.pattern;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.ContextAware;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/pattern/ConverterUtil.class */
public class ConverterUtil {
    public static <E> void startConverters(Converter<E> head) {
        Converter<E> converter = head;
        while (true) {
            Converter<E> c = converter;
            if (c != null) {
                if (c instanceof CompositeConverter) {
                    CompositeConverter<E> cc = (CompositeConverter) c;
                    Converter<E> childConverter = cc.childConverter;
                    startConverters(childConverter);
                    cc.start();
                } else if (c instanceof DynamicConverter) {
                    DynamicConverter<E> dc = (DynamicConverter) c;
                    dc.start();
                }
                converter = c.getNext();
            } else {
                return;
            }
        }
    }

    public static <E> Converter<E> findTail(Converter<E> head) {
        Converter<E> p;
        Converter<E> converter = head;
        while (true) {
            p = converter;
            if (p == null) {
                break;
            }
            Converter<E> next = p.getNext();
            if (next == null) {
                break;
            }
            converter = next;
        }
        return p;
    }

    public static <E> void setContextForConverters(Context context, Converter<E> head) {
        Converter<E> converter = head;
        while (true) {
            Converter<E> c = converter;
            if (c != null) {
                if (c instanceof ContextAware) {
                    ((ContextAware) c).setContext(context);
                }
                converter = c.getNext();
            } else {
                return;
            }
        }
    }
}