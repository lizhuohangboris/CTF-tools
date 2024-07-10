package org.thymeleaf.model;

import java.io.IOException;
import java.io.Writer;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/model/IModel.class */
public interface IModel {
    IEngineConfiguration getConfiguration();

    TemplateMode getTemplateMode();

    int size();

    ITemplateEvent get(int i);

    void add(ITemplateEvent iTemplateEvent);

    void insert(int i, ITemplateEvent iTemplateEvent);

    void replace(int i, ITemplateEvent iTemplateEvent);

    void addModel(IModel iModel);

    void insertModel(int i, IModel iModel);

    void remove(int i);

    void reset();

    IModel cloneModel();

    void accept(IModelVisitor iModelVisitor);

    void write(Writer writer) throws IOException;
}