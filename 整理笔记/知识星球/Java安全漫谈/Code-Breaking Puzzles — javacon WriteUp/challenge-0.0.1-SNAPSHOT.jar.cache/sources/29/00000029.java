package ch.qos.logback.classic.html;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.pattern.MDCConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.helpers.Transform;
import ch.qos.logback.core.html.HTMLLayoutBase;
import ch.qos.logback.core.html.IThrowableRenderer;
import ch.qos.logback.core.pattern.Converter;
import java.util.Map;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/html/HTMLLayout.class */
public class HTMLLayout extends HTMLLayoutBase<ILoggingEvent> {
    static final String DEFAULT_CONVERSION_PATTERN = "%date%thread%level%logger%mdc%msg";
    IThrowableRenderer<ILoggingEvent> throwableRenderer;

    public HTMLLayout() {
        this.pattern = DEFAULT_CONVERSION_PATTERN;
        this.throwableRenderer = new DefaultThrowableRenderer();
        this.cssBuilder = new DefaultCssBuilder();
    }

    @Override // ch.qos.logback.core.html.HTMLLayoutBase, ch.qos.logback.core.LayoutBase, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        int errorCount = 0;
        if (this.throwableRenderer == null) {
            addError("ThrowableRender cannot be null.");
            errorCount = 0 + 1;
        }
        if (errorCount == 0) {
            super.start();
        }
    }

    @Override // ch.qos.logback.core.html.HTMLLayoutBase
    protected Map<String, String> getDefaultConverterMap() {
        return PatternLayout.defaultConverterMap;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // ch.qos.logback.core.Layout
    public String doLayout(ILoggingEvent event) {
        StringBuilder buf = new StringBuilder();
        startNewTableIfLimitReached(buf);
        boolean odd = true;
        long j = this.counter;
        this.counter = j + 1;
        if ((j & 1) == 0) {
            odd = false;
        }
        String level = event.getLevel().toString().toLowerCase();
        buf.append(CoreConstants.LINE_SEPARATOR);
        buf.append("<tr class=\"");
        buf.append(level);
        if (odd) {
            buf.append(" odd\">");
        } else {
            buf.append(" even\">");
        }
        buf.append(CoreConstants.LINE_SEPARATOR);
        Converter converter = this.head;
        while (true) {
            Converter converter2 = converter;
            if (converter2 == null) {
                break;
            }
            appendEventToBuffer(buf, converter2, event);
            converter = converter2.getNext();
        }
        buf.append("</tr>");
        buf.append(CoreConstants.LINE_SEPARATOR);
        if (event.getThrowableProxy() != null) {
            this.throwableRenderer.render(buf, event);
        }
        return buf.toString();
    }

    private void appendEventToBuffer(StringBuilder buf, Converter<ILoggingEvent> c, ILoggingEvent event) {
        buf.append("<td class=\"");
        buf.append(computeConverterName(c));
        buf.append("\">");
        buf.append(Transform.escapeTags(c.convert(event)));
        buf.append("</td>");
        buf.append(CoreConstants.LINE_SEPARATOR);
    }

    public IThrowableRenderer getThrowableRenderer() {
        return this.throwableRenderer;
    }

    public void setThrowableRenderer(IThrowableRenderer<ILoggingEvent> throwableRenderer) {
        this.throwableRenderer = throwableRenderer;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.html.HTMLLayoutBase
    public String computeConverterName(Converter c) {
        if (c instanceof MDCConverter) {
            MDCConverter mc = (MDCConverter) c;
            String key = mc.getFirstOption();
            if (key != null) {
                return key;
            }
            return "MDC";
        }
        return super.computeConverterName(c);
    }
}