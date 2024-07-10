package org.thymeleaf.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.thymeleaf.exceptions.TemplateProcessingException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/DataDrivenTemplateIterator.class */
public final class DataDrivenTemplateIterator implements Iterator<Object> {
    private static final char[] SSE_HEAD_EVENT_NAME = "head".toCharArray();
    private static final char[] SSE_MESSAGE_EVENT_NAME = ConstraintHelper.MESSAGE.toCharArray();
    private static final char[] SSE_TAIL_EVENT_NAME = "tail".toCharArray();
    private final List<Object> values = new ArrayList(10);
    private IThrottledTemplateWriterControl writerControl = null;
    private ISSEThrottledTemplateWriterControl sseControl = null;
    private char[] sseEventsPrefix = null;
    private char[] sseEventsComposedMessageEventName = null;
    private long sseEventsID = 0;
    private boolean inStep = false;
    private boolean feedingComplete = false;
    private boolean queried = false;

    public void setWriterControl(IThrottledTemplateWriterControl writerControl) {
        this.writerControl = writerControl;
        if (writerControl instanceof ISSEThrottledTemplateWriterControl) {
            this.sseControl = (ISSEThrottledTemplateWriterControl) this.writerControl;
        } else {
            this.sseControl = null;
        }
    }

    public void setSseEventsPrefix(String sseEventsPrefix) {
        this.sseEventsPrefix = (sseEventsPrefix == null || sseEventsPrefix.length() == 0) ? null : sseEventsPrefix.toCharArray();
        this.sseEventsComposedMessageEventName = composeToken(SSE_MESSAGE_EVENT_NAME);
    }

    public void setSseEventsFirstID(long sseEventsFirstID) {
        this.sseEventsID = sseEventsFirstID;
    }

    public void takeBackLastEventID() {
        if (this.sseEventsID > 0) {
            this.sseEventsID--;
        }
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        this.queried = true;
        return !this.values.isEmpty();
    }

    @Override // java.util.Iterator
    public Object next() {
        this.queried = true;
        if (this.values.isEmpty()) {
            throw new NoSuchElementException();
        }
        Object value = this.values.get(0);
        this.values.remove(0);
        return value;
    }

    public void startIteration() {
        this.inStep = true;
        if (this.sseControl != null) {
            char[] id = composeToken(Long.toString(this.sseEventsID).toCharArray());
            char[] event = this.sseEventsComposedMessageEventName;
            this.sseControl.startEvent(id, event);
            this.sseEventsID++;
        }
    }

    public void finishIteration() {
        finishStep();
    }

    public boolean hasBeenQueried() {
        return this.queried;
    }

    @Override // java.util.Iterator
    public void remove() {
        throw new UnsupportedOperationException("remove() is not supported in Throttled Iterator");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isPaused() {
        this.queried = true;
        return this.values.isEmpty() && !this.feedingComplete;
    }

    public boolean continueBufferExecution() {
        return !this.values.isEmpty();
    }

    public void feedBuffer(List<Object> newElements) {
        this.values.addAll(newElements);
    }

    public void startHead() {
        this.inStep = true;
        if (this.sseControl != null) {
            char[] id = composeToken(Long.toString(this.sseEventsID).toCharArray());
            char[] event = composeToken(SSE_HEAD_EVENT_NAME);
            this.sseControl.startEvent(id, event);
            this.sseEventsID++;
        }
    }

    public void feedingComplete() {
        this.feedingComplete = true;
    }

    public void startTail() {
        this.inStep = true;
        if (this.sseControl != null) {
            char[] id = composeToken(Long.toString(this.sseEventsID).toCharArray());
            char[] event = composeToken(SSE_TAIL_EVENT_NAME);
            this.sseControl.startEvent(id, event);
            this.sseEventsID++;
        }
    }

    public void finishStep() {
        if (!this.inStep) {
            return;
        }
        this.inStep = false;
        if (this.sseControl != null) {
            try {
                this.sseControl.endEvent();
            } catch (IOException e) {
                throw new TemplateProcessingException("Cannot signal end of SSE event", e);
            }
        }
    }

    public boolean isStepOutputFinished() {
        if (this.inStep) {
            return false;
        }
        if (this.writerControl != null) {
            try {
                return !this.writerControl.isOverflown();
            } catch (IOException e) {
                throw new TemplateProcessingException("Cannot signal end of SSE event", e);
            }
        }
        return true;
    }

    private char[] composeToken(char[] token) {
        if (this.sseEventsPrefix == null) {
            return token;
        }
        char[] result = new char[this.sseEventsPrefix.length + 1 + token.length];
        System.arraycopy(this.sseEventsPrefix, 0, result, 0, this.sseEventsPrefix.length);
        result[this.sseEventsPrefix.length] = '_';
        System.arraycopy(token, 0, result, this.sseEventsPrefix.length + 1, token.length);
        return result;
    }
}