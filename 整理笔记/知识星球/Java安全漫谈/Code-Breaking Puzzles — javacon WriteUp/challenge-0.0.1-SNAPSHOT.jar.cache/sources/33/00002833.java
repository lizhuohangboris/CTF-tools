package org.thymeleaf.engine;

import java.io.IOException;
import java.io.Writer;
import org.thymeleaf.model.IModelVisitor;
import org.thymeleaf.model.IXMLDeclaration;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/XMLDeclaration.class */
public final class XMLDeclaration extends AbstractTemplateEvent implements IXMLDeclaration, IEngineTemplateEvent {
    public static final String DEFAULT_KEYWORD = "xml";
    public static final String DEFAULT_VERSION = "1.0";
    public static final String ATTRIBUTE_NAME_VERSION = "version";
    public static final String ATTRIBUTE_NAME_ENCODING = "encoding";
    public static final String ATTRIBUTE_NAME_STANDALONE = "standalone";
    private final String keyword;
    private final String version;
    private final String encoding;
    private final String standalone;
    private final String xmlDeclaration;

    XMLDeclaration(String encoding) {
        this(DEFAULT_KEYWORD, DEFAULT_VERSION, encoding, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public XMLDeclaration(String keyword, String version, String encoding, String standalone) {
        this.keyword = keyword;
        this.version = version;
        this.encoding = encoding;
        this.standalone = standalone;
        this.xmlDeclaration = computeXmlDeclaration();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public XMLDeclaration(String xmlDeclaration, String keyword, String version, String encoding, String standalone, String templateName, int line, int col) {
        super(templateName, line, col);
        this.keyword = keyword;
        this.version = version;
        this.encoding = encoding;
        this.standalone = standalone;
        this.xmlDeclaration = xmlDeclaration != null ? xmlDeclaration : computeXmlDeclaration();
    }

    @Override // org.thymeleaf.model.IXMLDeclaration
    public String getKeyword() {
        return this.keyword;
    }

    @Override // org.thymeleaf.model.IXMLDeclaration
    public String getVersion() {
        return this.version;
    }

    @Override // org.thymeleaf.model.IXMLDeclaration
    public String getEncoding() {
        return this.encoding;
    }

    @Override // org.thymeleaf.model.IXMLDeclaration
    public String getStandalone() {
        return this.standalone;
    }

    @Override // org.thymeleaf.model.IXMLDeclaration
    public String getXmlDeclaration() {
        return this.xmlDeclaration;
    }

    private String computeXmlDeclaration() {
        StringBuilder strBuilder = new StringBuilder(40);
        strBuilder.append("<?");
        strBuilder.append(this.keyword);
        if (this.version != null) {
            strBuilder.append(' ');
            strBuilder.append(ATTRIBUTE_NAME_VERSION);
            strBuilder.append("=\"");
            strBuilder.append(this.version);
            strBuilder.append('\"');
        }
        if (this.encoding != null) {
            strBuilder.append(' ');
            strBuilder.append(ATTRIBUTE_NAME_ENCODING);
            strBuilder.append("=\"");
            strBuilder.append(this.encoding);
            strBuilder.append('\"');
        }
        if (this.standalone != null) {
            strBuilder.append(' ');
            strBuilder.append(ATTRIBUTE_NAME_STANDALONE);
            strBuilder.append("=\"");
            strBuilder.append(this.standalone);
            strBuilder.append('\"');
        }
        strBuilder.append("?>");
        return strBuilder.toString();
    }

    @Override // org.thymeleaf.model.ITemplateEvent
    public void accept(IModelVisitor visitor) {
        visitor.visit(this);
    }

    @Override // org.thymeleaf.model.ITemplateEvent
    public void write(Writer writer) throws IOException {
        writer.write(this.xmlDeclaration);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static XMLDeclaration asEngineXMLDeclaration(IXMLDeclaration xmlDeclaration) {
        if (xmlDeclaration instanceof XMLDeclaration) {
            return (XMLDeclaration) xmlDeclaration;
        }
        return new XMLDeclaration(null, xmlDeclaration.getKeyword(), xmlDeclaration.getVersion(), xmlDeclaration.getEncoding(), xmlDeclaration.getStandalone(), xmlDeclaration.getTemplateName(), xmlDeclaration.getLine(), xmlDeclaration.getCol());
    }

    @Override // org.thymeleaf.engine.IEngineTemplateEvent
    public void beHandled(ITemplateHandler handler) {
        handler.handleXMLDeclaration(this);
    }

    public String toString() {
        return getXmlDeclaration();
    }
}