package org.thymeleaf.engine;

import java.util.Arrays;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.processor.element.IElementProcessor;
import org.thymeleaf.util.ProcessorComparators;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/ElementProcessorIterator.class */
final class ElementProcessorIterator {
    private int last = -1;
    private IElementProcessor[] processors = null;
    private boolean[] visited = null;
    private int size = 0;
    private IElementProcessor[] auxProcessors = null;
    private boolean[] auxVisited = null;
    private int auxSize = 0;
    private AbstractProcessableElementTag currentTag = null;
    private boolean lastToBeRepeated = false;
    private boolean lastWasRepeated = false;

    void reset() {
        this.size = 0;
        this.last = -1;
        this.currentTag = null;
        this.lastToBeRepeated = false;
        this.lastWasRepeated = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public IElementProcessor next(AbstractProcessableElementTag tag) {
        if (this.lastToBeRepeated) {
            IElementProcessor repeatedLast = computeRepeatedLast(tag);
            this.lastToBeRepeated = false;
            this.lastWasRepeated = true;
            return repeatedLast;
        }
        this.lastWasRepeated = false;
        if (this.currentTag != tag) {
            recompute(tag);
            this.currentTag = tag;
            this.last = -1;
        }
        if (this.processors == null) {
            return null;
        }
        int i = this.last + 1;
        int n = this.size - i;
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                if (!this.visited[i]) {
                    this.visited[i] = true;
                    this.last = i;
                    return this.processors[i];
                }
                i++;
            } else {
                this.last = this.size;
                return null;
            }
        }
    }

    private IElementProcessor computeRepeatedLast(AbstractProcessableElementTag tag) {
        if (this.currentTag != tag) {
            throw new TemplateProcessingException("Cannot return last processor to be repeated: changes were made and processor recompute is needed!");
        }
        if (this.processors == null) {
            throw new TemplateProcessingException("Cannot return last processor to be repeated: no processors in tag!");
        }
        return this.processors[this.last];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean lastWasRepeated() {
        return this.lastWasRepeated;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setLastToBeRepeated(AbstractProcessableElementTag tag) {
        if (this.currentTag != tag) {
            throw new TemplateProcessingException("Cannot set last processor to be repeated: processor recompute is needed!");
        }
        if (this.processors == null) {
            throw new TemplateProcessingException("Cannot set last processor to be repeated: no processors in tag!");
        }
        this.lastToBeRepeated = true;
    }

    private void recompute(AbstractProcessableElementTag tag) {
        IElementProcessor[] associatedProcessors = tag.getAssociatedProcessors();
        if (associatedProcessors.length == 0) {
            if (this.processors != null) {
                this.size = 0;
            }
        } else if (this.processors == null) {
            this.size = associatedProcessors.length;
            this.processors = new IElementProcessor[Math.max(this.size, 4)];
            this.visited = new boolean[Math.max(this.size, 4)];
            System.arraycopy(associatedProcessors, 0, this.processors, 0, this.size);
            Arrays.fill(this.visited, false);
        } else {
            this.auxSize = associatedProcessors.length;
            if (this.auxProcessors == null || this.auxSize > this.auxProcessors.length) {
                this.auxProcessors = new IElementProcessor[Math.max(this.auxSize, 4)];
                this.auxVisited = new boolean[Math.max(this.auxSize, 4)];
            }
            System.arraycopy(associatedProcessors, 0, this.auxProcessors, 0, this.auxSize);
            int i = 0;
            int j = 0;
            while (i < this.auxSize) {
                if (i >= this.size || j >= this.size) {
                    Arrays.fill(this.auxVisited, i, this.auxSize, false);
                    break;
                } else if (this.auxProcessors[i] == this.processors[j]) {
                    this.auxVisited[i] = this.visited[j];
                    i++;
                    j++;
                } else {
                    int comp = ProcessorComparators.PROCESSOR_COMPARATOR.compare(this.auxProcessors[i], this.processors[j]);
                    if (comp == 0) {
                        throw new IllegalStateException("Two different registered processors have returned zero as a result of their comparison, which is forbidden. Offending processors are " + this.auxProcessors[i].getClass().getName() + " and " + this.processors[j].getClass().getName());
                    }
                    if (comp < 0) {
                        this.auxVisited[i] = false;
                        i++;
                    } else {
                        j++;
                    }
                }
            }
            IElementProcessor[] swapProcessors = this.auxProcessors;
            boolean[] swapVisited = this.auxVisited;
            this.auxProcessors = this.processors;
            this.auxVisited = this.visited;
            this.processors = swapProcessors;
            this.visited = swapVisited;
            this.size = this.auxSize;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void resetAsCloneOf(ElementProcessorIterator original) {
        this.size = original.size;
        this.last = original.last;
        this.currentTag = original.currentTag;
        this.lastToBeRepeated = original.lastToBeRepeated;
        this.lastWasRepeated = original.lastWasRepeated;
        if (this.size > 0 && original.processors != null) {
            if (this.processors == null || this.processors.length < this.size) {
                this.processors = new IElementProcessor[this.size];
                this.visited = new boolean[this.size];
            }
            System.arraycopy(original.processors, 0, this.processors, 0, this.size);
            System.arraycopy(original.visited, 0, this.visited, 0, this.size);
        }
    }
}