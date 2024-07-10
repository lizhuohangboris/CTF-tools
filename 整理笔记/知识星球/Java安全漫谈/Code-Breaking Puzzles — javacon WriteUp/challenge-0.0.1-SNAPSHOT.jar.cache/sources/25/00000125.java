package ch.qos.logback.core.joran.spi;

import ch.qos.logback.core.joran.event.BodyEvent;
import ch.qos.logback.core.joran.event.EndEvent;
import ch.qos.logback.core.joran.event.SaxEvent;
import ch.qos.logback.core.joran.event.StartEvent;
import java.util.ArrayList;
import java.util.List;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/joran/spi/EventPlayer.class */
public class EventPlayer {
    final Interpreter interpreter;
    List<SaxEvent> eventList;
    int currentIndex;

    public EventPlayer(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    public List<SaxEvent> getCopyOfPlayerEventList() {
        return new ArrayList(this.eventList);
    }

    public void play(List<SaxEvent> aSaxEventList) {
        this.eventList = aSaxEventList;
        this.currentIndex = 0;
        while (this.currentIndex < this.eventList.size()) {
            SaxEvent se = this.eventList.get(this.currentIndex);
            if (se instanceof StartEvent) {
                this.interpreter.startElement((StartEvent) se);
                this.interpreter.getInterpretationContext().fireInPlay(se);
            }
            if (se instanceof BodyEvent) {
                this.interpreter.getInterpretationContext().fireInPlay(se);
                this.interpreter.characters((BodyEvent) se);
            }
            if (se instanceof EndEvent) {
                this.interpreter.getInterpretationContext().fireInPlay(se);
                this.interpreter.endElement((EndEvent) se);
            }
            this.currentIndex++;
        }
    }

    public void addEventsDynamically(List<SaxEvent> eventList, int offset) {
        this.eventList.addAll(this.currentIndex + offset, eventList);
    }
}