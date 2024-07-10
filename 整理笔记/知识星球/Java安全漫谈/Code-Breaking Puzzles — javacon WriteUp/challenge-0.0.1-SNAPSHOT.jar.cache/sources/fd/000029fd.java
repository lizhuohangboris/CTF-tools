package org.thymeleaf.util;

import java.io.IOException;
import java.io.Writer;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.standard.serializer.IStandardCSSSerializer;
import org.thymeleaf.standard.serializer.IStandardJavaScriptSerializer;
import org.thymeleaf.standard.serializer.StandardSerializers;
import org.thymeleaf.templatemode.TemplateMode;
import org.unbescape.html.HtmlEscape;
import org.unbescape.xml.XmlEscape;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/LazyEscapingCharSequence.class */
public final class LazyEscapingCharSequence extends AbstractLazyCharSequence {
    private final IEngineConfiguration configuration;
    private final TemplateMode templateMode;
    private final Object input;

    public LazyEscapingCharSequence(IEngineConfiguration configuration, TemplateMode templateMode, Object input) {
        if (configuration == null) {
            throw new IllegalArgumentException("Engine Configuraion is null, which is forbidden");
        }
        if (templateMode == null) {
            throw new IllegalArgumentException("Template Mode is null, which is forbidden");
        }
        this.configuration = configuration;
        this.templateMode = templateMode;
        this.input = input;
    }

    @Override // org.thymeleaf.util.AbstractLazyCharSequence
    protected String resolveText() {
        Writer stringWriter = new FastStringWriter();
        produceEscapedOutput(stringWriter);
        return stringWriter.toString();
    }

    @Override // org.thymeleaf.util.AbstractLazyCharSequence
    protected void writeUnresolved(Writer writer) throws IOException {
        produceEscapedOutput(writer);
    }

    private void produceEscapedOutput(Writer writer) {
        try {
            switch (this.templateMode) {
                case TEXT:
                case HTML:
                    if (this.input != null) {
                        HtmlEscape.escapeHtml4Xml(this.input.toString(), writer);
                        return;
                    }
                    return;
                case XML:
                    if (this.input != null) {
                        XmlEscape.escapeXml10(this.input.toString(), writer);
                        return;
                    }
                    return;
                case JAVASCRIPT:
                    IStandardJavaScriptSerializer javaScriptSerializer = StandardSerializers.getJavaScriptSerializer(this.configuration);
                    javaScriptSerializer.serializeValue(this.input, writer);
                    return;
                case CSS:
                    IStandardCSSSerializer cssSerializer = StandardSerializers.getCSSSerializer(this.configuration);
                    cssSerializer.serializeValue(this.input, writer);
                    return;
                case RAW:
                    if (this.input != null) {
                        writer.write(this.input.toString());
                        return;
                    }
                    return;
                default:
                    throw new TemplateProcessingException("Unrecognized template mode " + this.templateMode + ". Cannot produce escaped output for this template mode.");
            }
        } catch (IOException e) {
            throw new TemplateProcessingException("An error happened while trying to produce escaped output", e);
        }
    }
}