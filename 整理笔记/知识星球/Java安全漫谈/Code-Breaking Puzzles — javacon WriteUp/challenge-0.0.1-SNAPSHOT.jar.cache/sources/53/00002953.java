package org.thymeleaf.standard.inline;

import java.io.Writer;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.standard.serializer.IStandardCSSSerializer;
import org.thymeleaf.standard.serializer.StandardSerializers;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.FastStringWriter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/inline/StandardCSSInliner.class */
public final class StandardCSSInliner extends AbstractStandardInliner {
    private final IStandardCSSSerializer serializer;

    public StandardCSSInliner(IEngineConfiguration configuration) {
        super(configuration, TemplateMode.CSS);
        this.serializer = StandardSerializers.getCSSSerializer(configuration);
    }

    @Override // org.thymeleaf.standard.inline.AbstractStandardInliner
    protected String produceEscapedOutput(Object input) {
        Writer cssWriter = new FastStringWriter(input instanceof String ? ((String) input).length() * 2 : 20);
        this.serializer.serializeValue(input, cssWriter);
        return cssWriter.toString();
    }
}