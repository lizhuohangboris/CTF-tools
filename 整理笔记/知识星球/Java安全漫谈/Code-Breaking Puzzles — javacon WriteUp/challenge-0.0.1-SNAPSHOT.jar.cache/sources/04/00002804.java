package org.thymeleaf.engine;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.IEngineContext;
import org.thymeleaf.engine.TemplateModelController;
import org.thymeleaf.model.ITemplateEvent;
import org.thymeleaf.model.IText;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/IteratedGatheringModelProcessable.class */
public final class IteratedGatheringModelProcessable extends AbstractGatheringModelProcessable {
    private static final String DEFAULT_STATUS_VAR_SUFFIX = "Stat";
    private final IEngineContext context;
    private final TemplateMode templateMode;
    private final String iterVariableName;
    private final String iterStatusVariableName;
    private final IterationStatusVar iterStatusVariable;
    private final Iterator<?> iterator;
    private final Text precedingWhitespace;
    private IterationModels iterationModels;
    private DataDrivenTemplateIterator dataDrivenIterator;
    private int iter;
    private int iterOffset;
    private Model iterModel;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/IteratedGatheringModelProcessable$IterationWhiteSpaceHandling.class */
    public enum IterationWhiteSpaceHandling {
        ZERO_ITER,
        SINGLE_ITER,
        MULTIPLE_ITER
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public IteratedGatheringModelProcessable(IEngineConfiguration configuration, ProcessorTemplateHandler processorTemplateHandler, IEngineContext context, TemplateModelController modelController, TemplateFlowController flowController, TemplateModelController.SkipBody gatheredSkipBody, boolean gatheredSkipCloseTag, ProcessorExecutionVars processorExecutionVars, String iterVariableName, String iterStatusVariableName, Object iteratedObject, Text precedingWhitespace) {
        super(configuration, processorTemplateHandler, context, modelController, flowController, gatheredSkipBody, gatheredSkipCloseTag, processorExecutionVars);
        this.context = context;
        this.templateMode = context.getTemplateMode();
        this.iterator = computeIteratedObjectIterator(iteratedObject);
        this.iterVariableName = iterVariableName;
        if (StringUtils.isEmptyOrWhitespace(iterStatusVariableName)) {
            this.iterStatusVariableName = iterVariableName + DEFAULT_STATUS_VAR_SUFFIX;
        } else {
            this.iterStatusVariableName = iterStatusVariableName;
        }
        this.iterStatusVariable = new IterationStatusVar();
        this.iterStatusVariable.index = 0;
        this.iterStatusVariable.size = computeIteratedObjectSize(iteratedObject);
        this.precedingWhitespace = precedingWhitespace;
        if (this.iterator != null && (this.iterator instanceof DataDrivenTemplateIterator)) {
            this.dataDrivenIterator = (DataDrivenTemplateIterator) this.iterator;
        } else {
            this.dataDrivenIterator = null;
        }
        this.iter = 0;
        this.iterOffset = 0;
        this.iterModel = null;
    }

    @Override // org.thymeleaf.engine.AbstractGatheringModelProcessable, org.thymeleaf.engine.IGatheringModelProcessable
    public ProcessorExecutionVars initializeProcessorExecutionVars() {
        return super.initializeProcessorExecutionVars().cloneVars();
    }

    @Override // org.thymeleaf.engine.IEngineProcessable
    public boolean process() {
        IterationWhiteSpaceHandling iterationWhiteSpaceHandling;
        TemplateFlowController flowController = getFlowController();
        if (flowController != null && flowController.stopProcessing) {
            return false;
        }
        if (this.iterModel == null && flowController != null && this.dataDrivenIterator != null && this.dataDrivenIterator.isPaused()) {
            flowController.stopProcessing = true;
            return false;
        }
        if (this.iterationModels == null) {
            if (this.dataDrivenIterator != null) {
                if (this.iterator.hasNext()) {
                    iterationWhiteSpaceHandling = IterationWhiteSpaceHandling.SINGLE_ITER;
                    this.iterStatusVariable.current = this.iterator.next();
                } else {
                    iterationWhiteSpaceHandling = IterationWhiteSpaceHandling.ZERO_ITER;
                }
            } else if (this.iterator.hasNext()) {
                this.iterStatusVariable.current = this.iterator.next();
                if (this.iterator.hasNext()) {
                    iterationWhiteSpaceHandling = IterationWhiteSpaceHandling.MULTIPLE_ITER;
                } else {
                    iterationWhiteSpaceHandling = IterationWhiteSpaceHandling.SINGLE_ITER;
                }
            } else {
                iterationWhiteSpaceHandling = IterationWhiteSpaceHandling.ZERO_ITER;
            }
            this.iterationModels = computeIterationModels(iterationWhiteSpaceHandling);
        }
        if (this.iter == 0) {
            if (!this.iterationModels.isEmpty()) {
                boolean iterationIsNew = false;
                if (this.iterModel == null) {
                    this.iterModel = this.iterationModels.modelFirst;
                    iterationIsNew = true;
                }
                if (!processIterationModel(flowController, iterationIsNew)) {
                    return false;
                }
                this.iter++;
                this.iterOffset = 0;
                this.iterModel = null;
                if (flowController != null && this.dataDrivenIterator != null && this.dataDrivenIterator.isPaused()) {
                    flowController.stopProcessing = true;
                    return false;
                }
            } else {
                resetGatheredSkipFlagsAfterNoIterations();
            }
        }
        while (true) {
            if (this.iterModel != null || this.iterator.hasNext()) {
                boolean iterationIsNew2 = false;
                if (this.iterModel == null && this.iterOffset == 0) {
                    this.iterStatusVariable.index++;
                    this.iterStatusVariable.current = this.iterator.next();
                    iterationIsNew2 = true;
                }
                if (this.iterModel == null) {
                    this.iterModel = this.iterator.hasNext() ? this.iterationModels.modelMiddle : this.iterationModels.modelLast;
                }
                if (!processIterationModel(flowController, iterationIsNew2)) {
                    return false;
                }
                this.iter++;
                this.iterOffset = 0;
                this.iterModel = null;
                if (flowController != null && this.dataDrivenIterator != null && this.dataDrivenIterator.isPaused()) {
                    flowController.stopProcessing = true;
                    return false;
                }
            } else {
                this.context.decreaseLevel();
                return true;
            }
        }
    }

    private boolean processIterationModel(TemplateFlowController flowController, boolean iterationIsNew) {
        if (iterationIsNew) {
            this.context.increaseLevel();
            this.context.setVariable(this.iterVariableName, this.iterStatusVariable.current);
            this.context.setVariable(this.iterStatusVariableName, this.iterStatusVariable);
            prepareProcessing();
            if (this.dataDrivenIterator != null) {
                this.dataDrivenIterator.startIteration();
            }
        }
        this.iterOffset += this.iterModel.process(getProcessorTemplateHandler(), this.iterOffset, flowController);
        if (flowController != null && (this.iterOffset < this.iterModel.queueSize || flowController.stopProcessing)) {
            return false;
        }
        this.context.decreaseLevel();
        if (this.dataDrivenIterator != null) {
            this.dataDrivenIterator.finishIteration();
            return true;
        }
        return true;
    }

    private static Integer computeIteratedObjectSize(Object iteratedObject) {
        if (iteratedObject == null) {
            return 0;
        }
        if (iteratedObject instanceof Collection) {
            return Integer.valueOf(((Collection) iteratedObject).size());
        }
        if (iteratedObject instanceof Map) {
            return Integer.valueOf(((Map) iteratedObject).size());
        }
        if (iteratedObject.getClass().isArray()) {
            return Integer.valueOf(Array.getLength(iteratedObject));
        }
        if ((iteratedObject instanceof Iterable) || (iteratedObject instanceof Iterator)) {
            return null;
        }
        return 1;
    }

    private static Iterator<?> computeIteratedObjectIterator(final Object iteratedObject) {
        if (iteratedObject == null) {
            return Collections.EMPTY_LIST.iterator();
        }
        if (iteratedObject instanceof Collection) {
            return ((Collection) iteratedObject).iterator();
        }
        if (iteratedObject instanceof Map) {
            return ((Map) iteratedObject).entrySet().iterator();
        }
        if (iteratedObject.getClass().isArray()) {
            return new Iterator<Object>() { // from class: org.thymeleaf.engine.IteratedGatheringModelProcessable.1
                protected final Object array;
                protected final int length;
                private int i = 0;

                {
                    this.array = iteratedObject;
                    this.length = Array.getLength(this.array);
                }

                @Override // java.util.Iterator
                public boolean hasNext() {
                    return this.i < this.length;
                }

                @Override // java.util.Iterator
                public Object next() {
                    Object obj = this.array;
                    int i = this.i;
                    this.i = i + 1;
                    return Array.get(obj, i);
                }

                @Override // java.util.Iterator
                public void remove() {
                    throw new UnsupportedOperationException("Cannot remove from an array iterator");
                }
            };
        }
        if (iteratedObject instanceof Iterable) {
            return ((Iterable) iteratedObject).iterator();
        }
        if (iteratedObject instanceof Iterator) {
            return (Iterator) iteratedObject;
        }
        if (iteratedObject instanceof Enumeration) {
            return new Iterator<Object>() { // from class: org.thymeleaf.engine.IteratedGatheringModelProcessable.2
                protected final Enumeration<?> enumeration;

                {
                    this.enumeration = (Enumeration) iteratedObject;
                }

                @Override // java.util.Iterator
                public boolean hasNext() {
                    return this.enumeration.hasMoreElements();
                }

                @Override // java.util.Iterator
                public Object next() {
                    return this.enumeration.nextElement();
                }

                @Override // java.util.Iterator
                public void remove() {
                    throw new UnsupportedOperationException("Cannot remove from an Enumeration iterator");
                }
            };
        }
        return Collections.singletonList(iteratedObject).iterator();
    }

    private IterationModels computeIterationModels(IterationWhiteSpaceHandling iterationWhiteSpaceHandling) {
        if (iterationWhiteSpaceHandling == IterationWhiteSpaceHandling.ZERO_ITER) {
            return IterationModels.EMPTY;
        }
        Model innerModel = getInnerModel();
        int gatheredModelSize = innerModel.size();
        if (iterationWhiteSpaceHandling == IterationWhiteSpaceHandling.SINGLE_ITER) {
            return new IterationModels(innerModel, innerModel, innerModel);
        }
        if (!this.templateMode.isText()) {
            if (this.precedingWhitespace != null) {
                Model modelWithWhiteSpace = new Model(innerModel);
                modelWithWhiteSpace.insert(0, this.precedingWhitespace);
                return new IterationModels(innerModel, modelWithWhiteSpace, modelWithWhiteSpace);
            }
            return new IterationModels(innerModel, innerModel, innerModel);
        } else if (innerModel.size() <= 2) {
            return new IterationModels(innerModel, innerModel, innerModel);
        } else {
            int firstBodyEventCutPoint = -1;
            int lastBodyEventCutPoint = -1;
            ITemplateEvent firstBodyEvent = innerModel.get(1);
            Text firstTextBodyEvent = null;
            if ((innerModel.get(0) instanceof OpenElementTag) && (firstBodyEvent instanceof IText)) {
                firstTextBodyEvent = Text.asEngineText((IText) firstBodyEvent);
                int firstTextEventLen = firstTextBodyEvent.length();
                int i = 0;
                while (true) {
                    if (i >= firstTextEventLen || -1 >= 0) {
                        break;
                    }
                    char c = firstTextBodyEvent.charAt(i);
                    if (c == '\n') {
                        firstBodyEventCutPoint = i + 1;
                        break;
                    } else if (!Character.isWhitespace(c)) {
                        break;
                    } else {
                        i++;
                    }
                }
            }
            ITemplateEvent lastBodyEvent = innerModel.get(gatheredModelSize - 2);
            Text lastTextBodyEvent = null;
            if (firstBodyEventCutPoint >= 0 && (innerModel.get(gatheredModelSize - 1) instanceof CloseElementTag) && (lastBodyEvent instanceof IText)) {
                lastTextBodyEvent = Text.asEngineText((IText) lastBodyEvent);
                int lastTextEventLen = lastTextBodyEvent.length();
                int i2 = lastTextEventLen - 1;
                while (true) {
                    if (i2 < 0 || -1 >= 0) {
                        break;
                    }
                    char c2 = lastTextBodyEvent.charAt(i2);
                    if (c2 == '\n') {
                        lastBodyEventCutPoint = i2 + 1;
                        break;
                    } else if (!Character.isWhitespace(c2)) {
                        break;
                    } else {
                        i2--;
                    }
                }
            }
            if (firstBodyEventCutPoint < 0 || lastBodyEventCutPoint < 0) {
                return new IterationModels(innerModel, innerModel, innerModel);
            }
            if (firstBodyEvent == lastBodyEvent) {
                Text textForFirst = new Text(firstTextBodyEvent.subSequence(0, lastBodyEventCutPoint));
                Text textForMiddle = new Text(firstTextBodyEvent.subSequence(firstBodyEventCutPoint, lastBodyEventCutPoint));
                Text textForLast = new Text(firstTextBodyEvent.subSequence(firstBodyEventCutPoint, firstTextBodyEvent.length()));
                Model modelFirst = new Model(innerModel);
                modelFirst.replace(1, textForFirst);
                Model modelMiddle = new Model(innerModel);
                modelMiddle.replace(1, textForMiddle);
                Model modelLast = new Model(innerModel);
                modelLast.replace(1, textForLast);
                return new IterationModels(modelFirst, modelMiddle, modelLast);
            }
            Model modelFirst2 = new Model(innerModel);
            Model modelMiddle2 = new Model(innerModel);
            Model modelLast2 = new Model(innerModel);
            if (firstBodyEventCutPoint > 0) {
                Text headTextForMiddleAndMax = new Text(firstTextBodyEvent.subSequence(firstBodyEventCutPoint, firstTextBodyEvent.length()));
                modelMiddle2.replace(1, headTextForMiddleAndMax);
                modelLast2.replace(1, headTextForMiddleAndMax);
            }
            if (lastBodyEventCutPoint < lastTextBodyEvent.length()) {
                Text tailTextForFirstAndMiddle = new Text(lastTextBodyEvent.subSequence(0, lastBodyEventCutPoint));
                modelFirst2.replace(gatheredModelSize - 2, tailTextForFirstAndMiddle);
                modelMiddle2.replace(gatheredModelSize - 2, tailTextForFirstAndMiddle);
            }
            return new IterationModels(modelFirst2, modelMiddle2, modelLast2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/IteratedGatheringModelProcessable$IterationModels.class */
    public static final class IterationModels {
        static IterationModels EMPTY = new IterationModels(null, null, null);
        final Model modelFirst;
        final Model modelMiddle;
        final Model modelLast;
        final boolean empty;

        IterationModels(Model modelFirst, Model modelMiddle, Model modelLast) {
            this.modelFirst = modelFirst;
            this.modelMiddle = modelMiddle;
            this.modelLast = modelLast;
            this.empty = this.modelFirst == null && this.modelMiddle == null && this.modelLast == null;
        }

        boolean isEmpty() {
            return this.empty;
        }
    }
}