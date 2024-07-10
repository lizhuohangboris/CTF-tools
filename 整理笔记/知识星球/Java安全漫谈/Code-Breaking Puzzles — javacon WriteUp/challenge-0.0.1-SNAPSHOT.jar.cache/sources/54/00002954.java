package org.thymeleaf.standard.inline;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.templatemode.TemplateMode;
import org.unbescape.html.HtmlEscape;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/inline/StandardHTMLInliner.class */
public final class StandardHTMLInliner extends AbstractStandardInliner {
    public StandardHTMLInliner(IEngineConfiguration configuration) {
        super(configuration, TemplateMode.HTML);
    }

    @Override // org.thymeleaf.standard.inline.AbstractStandardInliner
    protected String produceEscapedOutput(Object input) {
        if (input == null) {
            return "";
        }
        return HtmlEscape.escapeHtml4Xml(input.toString());
    }
}