package org.thymeleaf.engine;

import java.io.IOException;
import java.io.Writer;
import org.thymeleaf.model.ICDATASection;
import org.thymeleaf.model.IModelVisitor;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/CDATASection.class */
public final class CDATASection extends AbstractTextualTemplateEvent implements ICDATASection {
    static final String CDATA_PREFIX = "<![CDATA[";
    static final String CDATA_SUFFIX = "]]>";
    final String prefix;
    final String suffix;
    private volatile String computedCDATASectionStr;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CDATASection(CharSequence content) {
        this(CDATA_PREFIX, content, CDATA_SUFFIX);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CDATASection(String prefix, CharSequence content, String suffix) {
        super(content);
        this.computedCDATASectionStr = null;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    CDATASection(CharSequence content, String templateName, int line, int col) {
        this(CDATA_PREFIX, content, CDATA_SUFFIX, templateName, line, col);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CDATASection(String prefix, CharSequence content, String suffix, String templateName, int line, int col) {
        super(content, templateName, line, col);
        this.computedCDATASectionStr = null;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    @Override // org.thymeleaf.model.ICDATASection
    public String getCDATASection() {
        String c = this.computedCDATASectionStr;
        if (c == null) {
            String str = this.prefix + getContentText() + this.suffix;
            c = str;
            this.computedCDATASectionStr = str;
        }
        return c;
    }

    @Override // org.thymeleaf.model.ICDATASection
    public String getContent() {
        return getContentText();
    }

    @Override // java.lang.CharSequence
    public int length() {
        return this.prefix.length() + getContentLength() + this.suffix.length();
    }

    @Override // java.lang.CharSequence
    public char charAt(int index) {
        if (index < this.prefix.length()) {
            return this.prefix.charAt(index);
        }
        int prefixedContentLen = this.prefix.length() + getContentLength();
        if (index >= prefixedContentLen) {
            return this.suffix.charAt(index - prefixedContentLen);
        }
        return charAtContent(index - this.prefix.length());
    }

    @Override // java.lang.CharSequence
    public CharSequence subSequence(int start, int end) {
        if (start >= this.prefix.length() && end < this.prefix.length() + getContentLength()) {
            return contentSubSequence(start - this.prefix.length(), end - this.prefix.length());
        }
        return getCDATASection().subSequence(start, end);
    }

    @Override // org.thymeleaf.model.ITemplateEvent
    public void accept(IModelVisitor visitor) {
        visitor.visit(this);
    }

    @Override // org.thymeleaf.model.ITemplateEvent
    public void write(Writer writer) throws IOException {
        writer.write(this.prefix);
        writeContent(writer);
        writer.write(this.suffix);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static CDATASection asEngineCDATASection(ICDATASection cdataSection) {
        if (cdataSection instanceof CDATASection) {
            return (CDATASection) cdataSection;
        }
        return new CDATASection(cdataSection.getContent(), cdataSection.getTemplateName(), cdataSection.getLine(), cdataSection.getCol());
    }

    @Override // org.thymeleaf.engine.IEngineTemplateEvent
    public void beHandled(ITemplateHandler handler) {
        handler.handleCDATASection(this);
    }

    @Override // org.thymeleaf.engine.AbstractTextualTemplateEvent, java.lang.CharSequence
    public String toString() {
        return getCDATASection();
    }
}