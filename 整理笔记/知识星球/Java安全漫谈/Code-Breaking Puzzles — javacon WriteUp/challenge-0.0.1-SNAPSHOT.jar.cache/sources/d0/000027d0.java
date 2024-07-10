package org.thymeleaf.engine;

import java.io.IOException;
import java.io.Writer;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IElementTag;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.FastStringWriter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/AbstractElementTag.class */
abstract class AbstractElementTag extends AbstractTemplateEvent implements IElementTag {
    final TemplateMode templateMode;
    final ElementDefinition elementDefinition;
    final String elementCompleteName;
    final boolean synthetic;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractElementTag(TemplateMode templateMode, ElementDefinition elementDefinition, String elementCompleteName, boolean synthetic) {
        this.templateMode = templateMode;
        this.elementDefinition = elementDefinition;
        this.elementCompleteName = elementCompleteName;
        this.synthetic = synthetic;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractElementTag(TemplateMode templateMode, ElementDefinition elementDefinition, String elementCompleteName, boolean synthetic, String templateName, int line, int col) {
        super(templateName, line, col);
        this.templateMode = templateMode;
        this.elementDefinition = elementDefinition;
        this.elementCompleteName = elementCompleteName;
        this.synthetic = synthetic;
    }

    @Override // org.thymeleaf.model.IElementTag
    public final TemplateMode getTemplateMode() {
        return this.templateMode;
    }

    @Override // org.thymeleaf.model.IElementTag
    public final String getElementCompleteName() {
        return this.elementCompleteName;
    }

    @Override // org.thymeleaf.model.IElementTag
    public final ElementDefinition getElementDefinition() {
        return this.elementDefinition;
    }

    @Override // org.thymeleaf.model.IElementTag
    public final boolean isSynthetic() {
        return this.synthetic;
    }

    public final String toString() {
        Writer stringWriter = new FastStringWriter();
        try {
            write(stringWriter);
            return stringWriter.toString();
        } catch (IOException e) {
            throw new TemplateProcessingException("Exception while creating String representation of model entity", e);
        }
    }
}