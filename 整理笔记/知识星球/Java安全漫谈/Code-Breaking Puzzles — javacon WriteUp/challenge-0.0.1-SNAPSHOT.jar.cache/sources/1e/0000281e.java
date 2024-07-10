package org.thymeleaf.engine;

import org.thymeleaf.templateparser.raw.IRawHandler;
import org.thymeleaf.templateparser.raw.RawParseException;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/TemplateHandlerAdapterRawHandler.class */
public final class TemplateHandlerAdapterRawHandler implements IRawHandler {
    private final String templateName;
    private final ITemplateHandler templateHandler;
    private final int lineOffset;
    private final int colOffset;

    public TemplateHandlerAdapterRawHandler(String templateName, ITemplateHandler templateHandler, int lineOffset, int colOffset) {
        Validate.notNull(templateHandler, "Template handler cannot be null");
        this.templateName = templateName;
        this.templateHandler = templateHandler;
        this.lineOffset = lineOffset > 0 ? lineOffset - 1 : lineOffset;
        this.colOffset = colOffset > 0 ? colOffset - 1 : colOffset;
    }

    @Override // org.thymeleaf.templateparser.raw.IRawHandler
    public void handleDocumentStart(long startTimeNanos, int line, int col) throws RawParseException {
        this.templateHandler.handleTemplateStart(TemplateStart.TEMPLATE_START_INSTANCE);
    }

    @Override // org.thymeleaf.templateparser.raw.IRawHandler
    public void handleDocumentEnd(long endTimeNanos, long totalTimeNanos, int line, int col) throws RawParseException {
        this.templateHandler.handleTemplateEnd(TemplateEnd.TEMPLATE_END_INSTANCE);
    }

    @Override // org.thymeleaf.templateparser.raw.IRawHandler
    public void handleText(char[] buffer, int offset, int len, int line, int col) throws RawParseException {
        this.templateHandler.handleText(new Text(new String(buffer, offset, len), this.templateName, this.lineOffset + line, (line == 1 ? this.colOffset : 0) + col));
    }
}