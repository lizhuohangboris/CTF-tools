package org.thymeleaf.standard.inline;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.templatemode.TemplateMode;
import org.unbescape.xml.XmlEscape;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/inline/StandardXMLInliner.class */
public final class StandardXMLInliner extends AbstractStandardInliner {
    public StandardXMLInliner(IEngineConfiguration configuration) {
        super(configuration, TemplateMode.XML);
    }

    @Override // org.thymeleaf.standard.inline.AbstractStandardInliner
    protected String produceEscapedOutput(Object input) {
        if (input == null) {
            return "";
        }
        return XmlEscape.escapeXml10(input.toString());
    }
}