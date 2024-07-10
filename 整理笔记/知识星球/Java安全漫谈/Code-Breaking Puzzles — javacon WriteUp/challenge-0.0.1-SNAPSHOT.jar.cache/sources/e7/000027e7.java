package org.thymeleaf.engine;

import java.io.IOException;
import java.io.Writer;
import org.thymeleaf.model.IDocType;
import org.thymeleaf.model.IModelVisitor;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/DocType.class */
public final class DocType extends AbstractTemplateEvent implements IDocType, IEngineTemplateEvent {
    public static final String DEFAULT_KEYWORD = "DOCTYPE";
    public static final String DEFAULT_ELEMENT_NAME = "html";
    public static final String DEFAULT_TYPE_PUBLIC = "PUBLIC";
    public static final String DEFAULT_TYPE_SYSTEM = "SYSTEM";
    private final String keyword;
    private final String elementName;
    private final String type;
    private final String publicId;
    private final String systemId;
    private final String internalSubset;
    private final String docType;

    DocType() {
        this(null, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DocType(String publicId, String systemId) {
        this(DEFAULT_KEYWORD, DEFAULT_ELEMENT_NAME, publicId, systemId, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DocType(String keyword, String elementName, String publicId, String systemId, String internalSubset) {
        this.keyword = keyword;
        this.elementName = elementName;
        this.type = computeType(publicId, systemId);
        this.publicId = publicId;
        this.systemId = systemId;
        this.internalSubset = internalSubset;
        this.docType = computeDocType();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DocType(String docType, String keyword, String elementName, String publicId, String systemId, String internalSubset, String templateName, int line, int col) {
        super(templateName, line, col);
        this.keyword = keyword;
        this.elementName = elementName;
        this.type = computeType(publicId, systemId);
        this.publicId = publicId;
        this.systemId = systemId;
        this.internalSubset = internalSubset;
        this.docType = docType != null ? docType : computeDocType();
    }

    @Override // org.thymeleaf.model.IDocType
    public String getKeyword() {
        return this.keyword;
    }

    @Override // org.thymeleaf.model.IDocType
    public String getElementName() {
        return this.elementName;
    }

    @Override // org.thymeleaf.model.IDocType
    public String getType() {
        return this.type;
    }

    @Override // org.thymeleaf.model.IDocType
    public String getPublicId() {
        return this.publicId;
    }

    @Override // org.thymeleaf.model.IDocType
    public String getSystemId() {
        return this.systemId;
    }

    @Override // org.thymeleaf.model.IDocType
    public String getInternalSubset() {
        return this.internalSubset;
    }

    @Override // org.thymeleaf.model.IDocType
    public String getDocType() {
        return this.docType;
    }

    private String computeDocType() {
        StringBuilder strBuilder = new StringBuilder(120);
        strBuilder.append("<!");
        strBuilder.append(this.keyword);
        strBuilder.append(' ');
        strBuilder.append(this.elementName);
        if (this.type != null) {
            strBuilder.append(' ');
            strBuilder.append(this.type);
            if (this.publicId != null) {
                strBuilder.append(" \"");
                strBuilder.append(this.publicId);
                strBuilder.append('\"');
            }
            strBuilder.append(" \"");
            strBuilder.append(this.systemId);
            strBuilder.append('\"');
        }
        if (this.internalSubset != null) {
            strBuilder.append(" [");
            strBuilder.append(this.internalSubset);
            strBuilder.append(']');
        }
        strBuilder.append('>');
        return strBuilder.toString();
    }

    private static String computeType(String publicId, String systemId) {
        if (publicId != null && systemId == null) {
            throw new IllegalArgumentException("DOCTYPE clause cannot have a non-null PUBLIC ID and a null SYSTEM ID");
        }
        if (publicId == null && systemId == null) {
            return null;
        }
        if (publicId != null) {
            return DEFAULT_TYPE_PUBLIC;
        }
        return DEFAULT_TYPE_SYSTEM;
    }

    @Override // org.thymeleaf.model.ITemplateEvent
    public void accept(IModelVisitor visitor) {
        visitor.visit(this);
    }

    @Override // org.thymeleaf.model.ITemplateEvent
    public void write(Writer writer) throws IOException {
        writer.write(this.docType);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static DocType asEngineDocType(IDocType docType) {
        if (docType instanceof DocType) {
            return (DocType) docType;
        }
        return new DocType(null, docType.getKeyword(), docType.getElementName(), docType.getPublicId(), docType.getSystemId(), docType.getInternalSubset(), docType.getTemplateName(), docType.getLine(), docType.getCol());
    }

    @Override // org.thymeleaf.engine.IEngineTemplateEvent
    public void beHandled(ITemplateHandler handler) {
        handler.handleDocType(this);
    }

    public String toString() {
        return getDocType();
    }
}