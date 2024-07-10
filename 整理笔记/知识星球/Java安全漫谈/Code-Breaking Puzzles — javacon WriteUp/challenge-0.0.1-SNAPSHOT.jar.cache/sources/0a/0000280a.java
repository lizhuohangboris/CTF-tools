package org.thymeleaf.engine;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.ICDATASection;
import org.thymeleaf.model.ICloseElementTag;
import org.thymeleaf.model.IComment;
import org.thymeleaf.model.IDocType;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelVisitor;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.model.IProcessingInstruction;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.model.ITemplateEnd;
import org.thymeleaf.model.ITemplateEvent;
import org.thymeleaf.model.ITemplateStart;
import org.thymeleaf.model.IText;
import org.thymeleaf.model.IXMLDeclaration;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.FastStringWriter;
import org.thymeleaf.util.Validate;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/Model.class */
public final class Model implements IModel {
    private static final int INITIAL_EVENT_QUEUE_SIZE = 50;
    private IEngineConfiguration configuration;
    private TemplateMode templateMode;
    IEngineTemplateEvent[] queue;
    int queueSize;

    public Model(IEngineConfiguration configuration, TemplateMode templateMode) {
        Validate.notNull(configuration, "Engine Configuration cannot be null");
        Validate.notNull(templateMode, "Template Mode cannot be null");
        this.templateMode = templateMode;
        this.configuration = configuration;
        this.queue = new IEngineTemplateEvent[50];
        Arrays.fill(this.queue, (Object) null);
        this.queueSize = 0;
    }

    public Model(IModel model) {
        Validate.notNull(model, "Model cannot be null");
        this.configuration = model.getConfiguration();
        this.templateMode = model.getTemplateMode();
        if (model instanceof Model) {
            Model mmodel = (Model) model;
            this.queue = (IEngineTemplateEvent[]) mmodel.queue.clone();
            this.queueSize = mmodel.queueSize;
        } else if (model instanceof TemplateModel) {
            TemplateModel templateModel = (TemplateModel) model;
            this.queue = new IEngineTemplateEvent[templateModel.queue.length + 25];
            System.arraycopy(templateModel.queue, 1, this.queue, 0, templateModel.queue.length - 2);
            this.queueSize = templateModel.queue.length - 2;
        } else {
            this.queue = new IEngineTemplateEvent[50];
            Arrays.fill(this.queue, (Object) null);
            this.queueSize = 0;
            insertModel(0, model);
        }
    }

    @Override // org.thymeleaf.model.IModel
    public final IEngineConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override // org.thymeleaf.model.IModel
    public final TemplateMode getTemplateMode() {
        return this.templateMode;
    }

    @Override // org.thymeleaf.model.IModel
    public int size() {
        return this.queueSize;
    }

    @Override // org.thymeleaf.model.IModel
    public ITemplateEvent get(int pos) {
        return this.queue[pos];
    }

    @Override // org.thymeleaf.model.IModel
    public void add(ITemplateEvent event) {
        insert(this.queueSize, event);
    }

    @Override // org.thymeleaf.model.IModel
    public void insert(int pos, ITemplateEvent event) {
        if (event == null) {
            return;
        }
        IEngineTemplateEvent engineEvent = asEngineEvent(event);
        if (engineEvent == TemplateStart.TEMPLATE_START_INSTANCE || engineEvent == TemplateEnd.TEMPLATE_END_INSTANCE) {
            throw new TemplateProcessingException("Cannot insert event of type TemplateStart/TemplateEnd. These events can only be added to models internally during template parsing.");
        }
        if (this.queue.length == this.queueSize) {
            this.queue = (IEngineTemplateEvent[]) Arrays.copyOf(this.queue, this.queue.length + 25);
        }
        if (pos != this.queueSize) {
            System.arraycopy(this.queue, pos, this.queue, pos + 1, this.queueSize - pos);
        }
        this.queue[pos] = engineEvent;
        this.queueSize++;
    }

    @Override // org.thymeleaf.model.IModel
    public void replace(int pos, ITemplateEvent event) {
        if (event == null) {
            return;
        }
        IEngineTemplateEvent engineEvent = asEngineEvent(event);
        if (engineEvent == TemplateStart.TEMPLATE_START_INSTANCE || engineEvent == TemplateEnd.TEMPLATE_END_INSTANCE) {
            throw new TemplateProcessingException("Cannot insert event of type TemplateStart/TemplateEnd. These events can only be added to models internally during template parsing.");
        }
        this.queue[pos] = engineEvent;
    }

    @Override // org.thymeleaf.model.IModel
    public void addModel(IModel model) {
        insertModel(this.queueSize, model);
    }

    @Override // org.thymeleaf.model.IModel
    public void insertModel(int pos, IModel model) {
        if (model == null || model.size() == 0) {
            return;
        }
        if (this.configuration != model.getConfiguration()) {
            throw new TemplateProcessingException("Cannot add model of class " + model.getClass().getName() + " to the current template, as it was created using a different Template Engine Configuration.");
        }
        if (this.templateMode != model.getTemplateMode()) {
            throw new TemplateProcessingException("Cannot add model of class " + model.getClass().getName() + " to the current template, as it was created using a different Template Mode: " + model.getTemplateMode() + " instead of the current " + this.templateMode);
        }
        if (this.queue.length <= this.queueSize + model.size()) {
            this.queue = (IEngineTemplateEvent[]) Arrays.copyOf(this.queue, Math.max(this.queueSize + model.size(), this.queue.length + 25));
        }
        if (model instanceof TemplateModel) {
            doInsertTemplateModel(pos, (TemplateModel) model);
        } else if (model instanceof Model) {
            doInsertModel(pos, (Model) model);
        } else {
            doInsertOtherModel(pos, model);
        }
    }

    private void doInsertModel(int pos, Model model) {
        System.arraycopy(this.queue, pos, this.queue, pos + model.queueSize, this.queueSize - pos);
        System.arraycopy(model.queue, 0, this.queue, pos, model.queueSize);
        this.queueSize += model.queueSize;
    }

    private void doInsertTemplateModel(int pos, TemplateModel model) {
        int insertionSize = model.queue.length - 2;
        System.arraycopy(this.queue, pos, this.queue, pos + insertionSize, this.queueSize - pos);
        System.arraycopy(model.queue, 1, this.queue, pos, insertionSize);
        this.queueSize += insertionSize;
    }

    private void doInsertOtherModel(int pos, IModel model) {
        int modelSize = model.size();
        for (int i = 0; i < modelSize; i++) {
            insert(pos + i, model.get(i));
        }
    }

    @Override // org.thymeleaf.model.IModel
    public void remove(int pos) {
        System.arraycopy(this.queue, pos + 1, this.queue, pos, this.queueSize - (pos + 1));
        this.queueSize--;
    }

    @Override // org.thymeleaf.model.IModel
    public void reset() {
        this.queueSize = 0;
    }

    public void process(ITemplateHandler handler) {
        for (int i = 0; i < this.queueSize; i++) {
            this.queue[i].beHandled(handler);
        }
    }

    public int process(ITemplateHandler handler, int offset, TemplateFlowController controller) {
        if (controller == null) {
            process(handler);
            return this.queueSize;
        } else if (this.queueSize == 0 || offset >= this.queueSize) {
            return 0;
        } else {
            int i = offset;
            while (i < this.queueSize && !controller.stopProcessing) {
                int i2 = i;
                i++;
                this.queue[i2].beHandled(handler);
            }
            return i - offset;
        }
    }

    @Override // org.thymeleaf.model.IModel
    public IModel cloneModel() {
        return new Model(this);
    }

    void resetAsCloneOf(Model model) {
        this.configuration = model.configuration;
        this.templateMode = model.templateMode;
        if (this.queue.length < model.queueSize) {
            this.queue = new IEngineTemplateEvent[model.queueSize];
        }
        System.arraycopy(model.queue, 0, this.queue, 0, model.queueSize);
        this.queueSize = model.queueSize;
    }

    @Override // org.thymeleaf.model.IModel
    public final void write(Writer writer) throws IOException {
        for (int i = 0; i < this.queueSize; i++) {
            this.queue[i].write(writer);
        }
    }

    @Override // org.thymeleaf.model.IModel
    public void accept(IModelVisitor visitor) {
        for (int i = 0; i < this.queueSize; i++) {
            this.queue[i].accept(visitor);
        }
    }

    public boolean sameAs(Model model) {
        if (model == null || model.queueSize != this.queueSize) {
            return false;
        }
        for (int i = 0; i < this.queueSize; i++) {
            if (this.queue[i] != model.queue[i]) {
                return false;
            }
        }
        return true;
    }

    public final String toString() {
        try {
            Writer writer = new FastStringWriter();
            write(writer);
            return writer.toString();
        } catch (IOException e) {
            throw new TemplateProcessingException("Error while creating String representation of model");
        }
    }

    static IEngineTemplateEvent asEngineEvent(ITemplateEvent event) {
        if (event instanceof IEngineTemplateEvent) {
            return (IEngineTemplateEvent) event;
        }
        if (event instanceof IText) {
            return Text.asEngineText((IText) event);
        }
        if (event instanceof IOpenElementTag) {
            return OpenElementTag.asEngineOpenElementTag((IOpenElementTag) event);
        }
        if (event instanceof ICloseElementTag) {
            return CloseElementTag.asEngineCloseElementTag((ICloseElementTag) event);
        }
        if (event instanceof IStandaloneElementTag) {
            return StandaloneElementTag.asEngineStandaloneElementTag((IStandaloneElementTag) event);
        }
        if (event instanceof IDocType) {
            return DocType.asEngineDocType((IDocType) event);
        }
        if (event instanceof IComment) {
            return Comment.asEngineComment((IComment) event);
        }
        if (event instanceof ICDATASection) {
            return CDATASection.asEngineCDATASection((ICDATASection) event);
        }
        if (event instanceof IXMLDeclaration) {
            return XMLDeclaration.asEngineXMLDeclaration((IXMLDeclaration) event);
        }
        if (event instanceof IProcessingInstruction) {
            return ProcessingInstruction.asEngineProcessingInstruction((IProcessingInstruction) event);
        }
        if (event instanceof ITemplateStart) {
            return TemplateStart.asEngineTemplateStart((ITemplateStart) event);
        }
        if (event instanceof ITemplateEnd) {
            return TemplateEnd.asEngineTemplateEnd((ITemplateEnd) event);
        }
        throw new TemplateProcessingException("Cannot handle in event of type: " + event.getClass().getName());
    }
}