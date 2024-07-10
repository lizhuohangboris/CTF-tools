package org.thymeleaf.standard.inline;

import java.io.Writer;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.standard.serializer.IStandardJavaScriptSerializer;
import org.thymeleaf.standard.serializer.StandardSerializers;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.FastStringWriter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/inline/StandardJavaScriptInliner.class */
public final class StandardJavaScriptInliner extends AbstractStandardInliner {
    private final IStandardJavaScriptSerializer serializer;

    public StandardJavaScriptInliner(IEngineConfiguration configuration) {
        super(configuration, TemplateMode.JAVASCRIPT);
        this.serializer = StandardSerializers.getJavaScriptSerializer(configuration);
    }

    @Override // org.thymeleaf.standard.inline.AbstractStandardInliner
    protected String produceEscapedOutput(Object input) {
        Writer jsWriter = new FastStringWriter(input instanceof String ? ((String) input).length() * 2 : 20);
        this.serializer.serializeValue(input, jsWriter);
        return jsWriter.toString();
    }
}