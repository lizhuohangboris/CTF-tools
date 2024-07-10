package org.thymeleaf.engine;

import java.io.IOException;
import java.io.Writer;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelVisitor;
import org.thymeleaf.model.ITemplateEvent;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.FastStringWriter;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/TemplateModel.class */
public final class TemplateModel implements IModel {
    final IEngineConfiguration configuration;
    final TemplateData templateData;
    final IEngineTemplateEvent[] queue;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TemplateModel(IEngineConfiguration configuration, TemplateData templateData, IEngineTemplateEvent[] queue) {
        Validate.notNull(configuration, "Engine Configuration cannot be null");
        Validate.notNull(templateData, "Template Resolution cannot be null");
        Validate.notNull(queue, "Event queue cannot be null");
        Validate.isTrue(queue.length >= 2, "At least TemplateStart/TemplateEnd events must be added to a TemplateModel");
        Validate.isTrue(queue[0] == TemplateStart.TEMPLATE_START_INSTANCE, "First event in queue is not TemplateStart");
        Validate.isTrue(queue[queue.length - 1] == TemplateEnd.TEMPLATE_END_INSTANCE, "Last event in queue is not TemplateEnd");
        this.configuration = configuration;
        this.templateData = templateData;
        this.queue = queue;
    }

    public final TemplateData getTemplateData() {
        return this.templateData;
    }

    @Override // org.thymeleaf.model.IModel
    public final IEngineConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override // org.thymeleaf.model.IModel
    public final TemplateMode getTemplateMode() {
        return this.templateData.getTemplateMode();
    }

    @Override // org.thymeleaf.model.IModel
    public final int size() {
        return this.queue.length;
    }

    @Override // org.thymeleaf.model.IModel
    public final ITemplateEvent get(int pos) {
        return this.queue[pos];
    }

    @Override // org.thymeleaf.model.IModel
    public final void add(ITemplateEvent event) {
        immutableModelException();
    }

    @Override // org.thymeleaf.model.IModel
    public final void insert(int pos, ITemplateEvent event) {
        immutableModelException();
    }

    @Override // org.thymeleaf.model.IModel
    public final void replace(int pos, ITemplateEvent event) {
        immutableModelException();
    }

    @Override // org.thymeleaf.model.IModel
    public final void addModel(IModel model) {
        immutableModelException();
    }

    @Override // org.thymeleaf.model.IModel
    public final void insertModel(int pos, IModel model) {
        immutableModelException();
    }

    @Override // org.thymeleaf.model.IModel
    public final void remove(int pos) {
        immutableModelException();
    }

    @Override // org.thymeleaf.model.IModel
    public final void reset() {
        immutableModelException();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void process(ITemplateHandler handler) {
        for (int i = 0; i < this.queue.length; i++) {
            this.queue[i].beHandled(handler);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int process(ITemplateHandler handler, int offset, TemplateFlowController controller) {
        if (controller == null) {
            process(handler);
            return this.queue.length;
        } else if (this.queue.length == 0 || offset >= this.queue.length) {
            return 0;
        } else {
            int processed = 0;
            for (int i = offset; i < this.queue.length && !controller.stopProcessing; i++) {
                this.queue[i].beHandled(handler);
                processed++;
            }
            return processed;
        }
    }

    @Override // org.thymeleaf.model.IModel
    public final IModel cloneModel() {
        return new Model(this);
    }

    @Override // org.thymeleaf.model.IModel
    public final void write(Writer writer) throws IOException {
        for (int i = 0; i < this.queue.length; i++) {
            this.queue[i].write(writer);
        }
    }

    @Override // org.thymeleaf.model.IModel
    public void accept(IModelVisitor visitor) {
        for (int i = 0; i < this.queue.length; i++) {
            this.queue[i].accept(visitor);
        }
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

    private static void immutableModelException() {
        throw new UnsupportedOperationException("Modifications are not allowed on immutable model objects. This model object is an immutable implementation of the " + IModel.class.getName() + " interface, and no modifications are allowed in order to keep cache consistency and improve performance. To modify model events, convert first your immutable model object to a mutable one by means of the " + IModel.class.getName() + "#cloneModel() method");
    }
}