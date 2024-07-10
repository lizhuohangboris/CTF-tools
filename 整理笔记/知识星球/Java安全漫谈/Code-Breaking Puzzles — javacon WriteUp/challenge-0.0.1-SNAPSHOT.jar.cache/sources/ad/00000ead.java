package org.attoparser.trace;

import java.util.Arrays;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/trace/MarkupTraceEvent.class */
public abstract class MarkupTraceEvent {
    private final EventType eventType;
    final String[] contents;
    final int[] lines;
    final int[] cols;

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/trace/MarkupTraceEvent$EventType.class */
    public enum EventType {
        DOCUMENT_START("DS"),
        DOCUMENT_END("DE"),
        STANDALONE_ELEMENT_START("SES"),
        STANDALONE_ELEMENT_END("SEE"),
        NON_MINIMIZED_STANDALONE_ELEMENT_START("NSES"),
        NON_MINIMIZED_STANDALONE_ELEMENT_END("NSEE"),
        OPEN_ELEMENT_START("OES"),
        OPEN_ELEMENT_END("OEE"),
        AUTO_OPEN_ELEMENT_START("AOES"),
        AUTO_OPEN_ELEMENT_END("AOEE"),
        CLOSE_ELEMENT_START("CES"),
        CLOSE_ELEMENT_END("CEE"),
        AUTO_CLOSE_ELEMENT_START("ACES"),
        AUTO_CLOSE_ELEMENT_END("ACEE"),
        UNMATCHED_CLOSE_ELEMENT_START("UCES"),
        UNMATCHED_CLOSE_ELEMENT_END("UCEE"),
        ATTRIBUTE("A"),
        INNER_WHITE_SPACE("IWS"),
        TEXT("T"),
        COMMENT("C"),
        CDATA_SECTION("CD"),
        XML_DECLARATION("XD"),
        DOC_TYPE("DT"),
        PROCESSING_INSTRUCTION("P");
        
        private String stringRepresentation;

        EventType(String stringRepresentation) {
            this.stringRepresentation = stringRepresentation;
        }

        @Override // java.lang.Enum
        public String toString() {
            return this.stringRepresentation;
        }
    }

    private MarkupTraceEvent(EventType eventType, int[] lines, int[] cols, String... contents) {
        if (eventType == null) {
            throw new IllegalArgumentException("Event type cannot be null");
        }
        this.eventType = eventType;
        this.contents = contents;
        this.lines = lines;
        this.cols = cols;
    }

    public EventType getEventType() {
        return this.eventType;
    }

    public String toString() {
        String[] strArr;
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(this.eventType);
        if (this.contents != null) {
            if ((this.lines != null) & (this.lines.length == this.contents.length)) {
                for (int i = 0; i < this.contents.length; i++) {
                    strBuilder.append('(');
                    if (this.contents[i] != null) {
                        strBuilder.append(this.contents[i]);
                    }
                    strBuilder.append(')');
                    strBuilder.append('{');
                    strBuilder.append(String.valueOf(this.lines[i]));
                    strBuilder.append(',');
                    strBuilder.append(String.valueOf(this.cols[i]));
                    strBuilder.append('}');
                }
                return strBuilder.toString();
            }
        }
        if (this.contents != null) {
            for (String contentItem : this.contents) {
                strBuilder.append('(');
                if (contentItem != null) {
                    strBuilder.append(contentItem);
                }
                strBuilder.append(')');
            }
        }
        strBuilder.append('{');
        strBuilder.append(String.valueOf(this.lines[0]));
        strBuilder.append(',');
        strBuilder.append(String.valueOf(this.cols[0]));
        strBuilder.append('}');
        return strBuilder.toString();
    }

    public boolean matchesTypeAndContent(MarkupTraceEvent event) {
        if (this == event) {
            return true;
        }
        if (event == null) {
            return false;
        }
        if (this.eventType == null) {
            if (event.eventType != null) {
                return false;
            }
        } else if (!this.eventType.equals(event.eventType)) {
            return false;
        }
        if (this.contents == null) {
            if (event.contents != null) {
                return false;
            }
            return true;
        } else if (!Arrays.equals(this.contents, event.contents)) {
            return false;
        } else {
            return true;
        }
    }

    public int hashCode() {
        int result = this.eventType.hashCode();
        return (31 * ((31 * ((31 * result) + Arrays.hashCode(this.contents))) + Arrays.hashCode(this.lines))) + Arrays.hashCode(this.cols);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MarkupTraceEvent that = (MarkupTraceEvent) o;
        return Arrays.equals(this.cols, that.cols) && Arrays.equals(this.contents, that.contents) && Arrays.equals(this.lines, that.lines) && this.eventType == that.eventType;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/trace/MarkupTraceEvent$DocumentStartTraceEvent.class */
    public static final class DocumentStartTraceEvent extends MarkupTraceEvent {
        public DocumentStartTraceEvent(long startTimeNanos, int line, int col) {
            super(EventType.DOCUMENT_START, new int[]{line}, new int[]{col}, new String[]{String.valueOf(startTimeNanos)});
        }

        public long getStartTimeNanos() {
            return Long.parseLong(this.contents[0]);
        }

        public int getLine() {
            return this.lines[0];
        }

        public int getCol() {
            return this.cols[0];
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/trace/MarkupTraceEvent$DocumentEndTraceEvent.class */
    public static final class DocumentEndTraceEvent extends MarkupTraceEvent {
        public DocumentEndTraceEvent(long endTimeNanos, long totalTimeNanos, int line, int col) {
            super(EventType.DOCUMENT_END, new int[]{line}, new int[]{col}, new String[]{String.valueOf(endTimeNanos), String.valueOf(totalTimeNanos)});
        }

        public long getStartTimeNanos() {
            return Long.parseLong(this.contents[0]);
        }

        public long getTotalTimeNanos() {
            return Long.parseLong(this.contents[1]);
        }

        public int getLine() {
            return this.lines[0];
        }

        public int getCol() {
            return this.cols[0];
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/trace/MarkupTraceEvent$AbstractContentTraceEvent.class */
    static abstract class AbstractContentTraceEvent extends MarkupTraceEvent {
        protected AbstractContentTraceEvent(EventType type, String content, int line, int col) {
            super(type, new int[]{line}, new int[]{col}, new String[]{content});
            if (content == null) {
                throw new IllegalArgumentException("Contentn cannot be null");
            }
        }

        public String getContent() {
            return this.contents[0];
        }

        public int getLine() {
            return this.lines[0];
        }

        public int getCol() {
            return this.cols[0];
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/trace/MarkupTraceEvent$TextTraceEvent.class */
    public static final class TextTraceEvent extends AbstractContentTraceEvent {
        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractContentTraceEvent
        public /* bridge */ /* synthetic */ int getCol() {
            return super.getCol();
        }

        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractContentTraceEvent
        public /* bridge */ /* synthetic */ int getLine() {
            return super.getLine();
        }

        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractContentTraceEvent
        public /* bridge */ /* synthetic */ String getContent() {
            return super.getContent();
        }

        public TextTraceEvent(String content, int line, int col) {
            super(EventType.TEXT, content, line, col);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/trace/MarkupTraceEvent$CommentTraceEvent.class */
    public static final class CommentTraceEvent extends AbstractContentTraceEvent {
        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractContentTraceEvent
        public /* bridge */ /* synthetic */ int getCol() {
            return super.getCol();
        }

        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractContentTraceEvent
        public /* bridge */ /* synthetic */ int getLine() {
            return super.getLine();
        }

        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractContentTraceEvent
        public /* bridge */ /* synthetic */ String getContent() {
            return super.getContent();
        }

        public CommentTraceEvent(String content, int line, int col) {
            super(EventType.COMMENT, content, line, col);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/trace/MarkupTraceEvent$CDATASectionTraceEvent.class */
    public static final class CDATASectionTraceEvent extends AbstractContentTraceEvent {
        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractContentTraceEvent
        public /* bridge */ /* synthetic */ int getCol() {
            return super.getCol();
        }

        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractContentTraceEvent
        public /* bridge */ /* synthetic */ int getLine() {
            return super.getLine();
        }

        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractContentTraceEvent
        public /* bridge */ /* synthetic */ String getContent() {
            return super.getContent();
        }

        public CDATASectionTraceEvent(String content, int line, int col) {
            super(EventType.CDATA_SECTION, content, line, col);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/trace/MarkupTraceEvent$InnerWhiteSpaceTraceEvent.class */
    public static final class InnerWhiteSpaceTraceEvent extends AbstractContentTraceEvent {
        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractContentTraceEvent
        public /* bridge */ /* synthetic */ int getCol() {
            return super.getCol();
        }

        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractContentTraceEvent
        public /* bridge */ /* synthetic */ int getLine() {
            return super.getLine();
        }

        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractContentTraceEvent
        public /* bridge */ /* synthetic */ String getContent() {
            return super.getContent();
        }

        public InnerWhiteSpaceTraceEvent(String content, int line, int col) {
            super(EventType.INNER_WHITE_SPACE, content, line, col);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/trace/MarkupTraceEvent$AbstractElementTraceEvent.class */
    static abstract class AbstractElementTraceEvent extends MarkupTraceEvent {
        protected AbstractElementTraceEvent(EventType type, String elementName, int line, int col) {
            super(type, new int[]{line}, new int[]{col}, new String[]{elementName});
            if (elementName == null || elementName.trim().equals("")) {
                throw new IllegalArgumentException("Element name cannot be null or empty");
            }
        }

        public String getElementName() {
            return this.contents[0];
        }

        public int getLine() {
            return this.lines[0];
        }

        public int getCol() {
            return this.cols[0];
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/trace/MarkupTraceEvent$StandaloneElementStartTraceEvent.class */
    public static final class StandaloneElementStartTraceEvent extends AbstractElementTraceEvent {
        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractElementTraceEvent
        public /* bridge */ /* synthetic */ int getCol() {
            return super.getCol();
        }

        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractElementTraceEvent
        public /* bridge */ /* synthetic */ int getLine() {
            return super.getLine();
        }

        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractElementTraceEvent
        public /* bridge */ /* synthetic */ String getElementName() {
            return super.getElementName();
        }

        public StandaloneElementStartTraceEvent(String elementName, int line, int col) {
            super(EventType.STANDALONE_ELEMENT_START, elementName, line, col);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/trace/MarkupTraceEvent$StandaloneElementEndTraceEvent.class */
    public static final class StandaloneElementEndTraceEvent extends AbstractElementTraceEvent {
        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractElementTraceEvent
        public /* bridge */ /* synthetic */ int getCol() {
            return super.getCol();
        }

        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractElementTraceEvent
        public /* bridge */ /* synthetic */ int getLine() {
            return super.getLine();
        }

        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractElementTraceEvent
        public /* bridge */ /* synthetic */ String getElementName() {
            return super.getElementName();
        }

        public StandaloneElementEndTraceEvent(String elementName, int line, int col) {
            super(EventType.STANDALONE_ELEMENT_END, elementName, line, col);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/trace/MarkupTraceEvent$NonMinimizedStandaloneElementStartTraceEvent.class */
    public static final class NonMinimizedStandaloneElementStartTraceEvent extends AbstractElementTraceEvent {
        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractElementTraceEvent
        public /* bridge */ /* synthetic */ int getCol() {
            return super.getCol();
        }

        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractElementTraceEvent
        public /* bridge */ /* synthetic */ int getLine() {
            return super.getLine();
        }

        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractElementTraceEvent
        public /* bridge */ /* synthetic */ String getElementName() {
            return super.getElementName();
        }

        public NonMinimizedStandaloneElementStartTraceEvent(String elementName, int line, int col) {
            super(EventType.NON_MINIMIZED_STANDALONE_ELEMENT_START, elementName, line, col);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/trace/MarkupTraceEvent$NonMinimizedStandaloneElementEndTraceEvent.class */
    public static final class NonMinimizedStandaloneElementEndTraceEvent extends AbstractElementTraceEvent {
        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractElementTraceEvent
        public /* bridge */ /* synthetic */ int getCol() {
            return super.getCol();
        }

        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractElementTraceEvent
        public /* bridge */ /* synthetic */ int getLine() {
            return super.getLine();
        }

        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractElementTraceEvent
        public /* bridge */ /* synthetic */ String getElementName() {
            return super.getElementName();
        }

        public NonMinimizedStandaloneElementEndTraceEvent(String elementName, int line, int col) {
            super(EventType.NON_MINIMIZED_STANDALONE_ELEMENT_END, elementName, line, col);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/trace/MarkupTraceEvent$OpenElementStartTraceEvent.class */
    public static final class OpenElementStartTraceEvent extends AbstractElementTraceEvent {
        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractElementTraceEvent
        public /* bridge */ /* synthetic */ int getCol() {
            return super.getCol();
        }

        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractElementTraceEvent
        public /* bridge */ /* synthetic */ int getLine() {
            return super.getLine();
        }

        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractElementTraceEvent
        public /* bridge */ /* synthetic */ String getElementName() {
            return super.getElementName();
        }

        public OpenElementStartTraceEvent(String elementName, int line, int col) {
            super(EventType.OPEN_ELEMENT_START, elementName, line, col);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/trace/MarkupTraceEvent$OpenElementEndTraceEvent.class */
    public static final class OpenElementEndTraceEvent extends AbstractElementTraceEvent {
        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractElementTraceEvent
        public /* bridge */ /* synthetic */ int getCol() {
            return super.getCol();
        }

        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractElementTraceEvent
        public /* bridge */ /* synthetic */ int getLine() {
            return super.getLine();
        }

        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractElementTraceEvent
        public /* bridge */ /* synthetic */ String getElementName() {
            return super.getElementName();
        }

        public OpenElementEndTraceEvent(String elementName, int line, int col) {
            super(EventType.OPEN_ELEMENT_END, elementName, line, col);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/trace/MarkupTraceEvent$AutoOpenElementStartTraceEvent.class */
    public static final class AutoOpenElementStartTraceEvent extends AbstractElementTraceEvent {
        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractElementTraceEvent
        public /* bridge */ /* synthetic */ int getCol() {
            return super.getCol();
        }

        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractElementTraceEvent
        public /* bridge */ /* synthetic */ int getLine() {
            return super.getLine();
        }

        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractElementTraceEvent
        public /* bridge */ /* synthetic */ String getElementName() {
            return super.getElementName();
        }

        public AutoOpenElementStartTraceEvent(String elementName, int line, int col) {
            super(EventType.AUTO_OPEN_ELEMENT_START, elementName, line, col);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/trace/MarkupTraceEvent$AutoOpenElementEndTraceEvent.class */
    public static final class AutoOpenElementEndTraceEvent extends AbstractElementTraceEvent {
        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractElementTraceEvent
        public /* bridge */ /* synthetic */ int getCol() {
            return super.getCol();
        }

        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractElementTraceEvent
        public /* bridge */ /* synthetic */ int getLine() {
            return super.getLine();
        }

        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractElementTraceEvent
        public /* bridge */ /* synthetic */ String getElementName() {
            return super.getElementName();
        }

        public AutoOpenElementEndTraceEvent(String elementName, int line, int col) {
            super(EventType.AUTO_OPEN_ELEMENT_END, elementName, line, col);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/trace/MarkupTraceEvent$CloseElementStartTraceEvent.class */
    public static final class CloseElementStartTraceEvent extends AbstractElementTraceEvent {
        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractElementTraceEvent
        public /* bridge */ /* synthetic */ int getCol() {
            return super.getCol();
        }

        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractElementTraceEvent
        public /* bridge */ /* synthetic */ int getLine() {
            return super.getLine();
        }

        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractElementTraceEvent
        public /* bridge */ /* synthetic */ String getElementName() {
            return super.getElementName();
        }

        public CloseElementStartTraceEvent(String elementName, int line, int col) {
            super(EventType.CLOSE_ELEMENT_START, elementName, line, col);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/trace/MarkupTraceEvent$CloseElementEndTraceEvent.class */
    public static final class CloseElementEndTraceEvent extends AbstractElementTraceEvent {
        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractElementTraceEvent
        public /* bridge */ /* synthetic */ int getCol() {
            return super.getCol();
        }

        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractElementTraceEvent
        public /* bridge */ /* synthetic */ int getLine() {
            return super.getLine();
        }

        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractElementTraceEvent
        public /* bridge */ /* synthetic */ String getElementName() {
            return super.getElementName();
        }

        public CloseElementEndTraceEvent(String elementName, int line, int col) {
            super(EventType.CLOSE_ELEMENT_END, elementName, line, col);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/trace/MarkupTraceEvent$AutoCloseElementStartTraceEvent.class */
    public static final class AutoCloseElementStartTraceEvent extends AbstractElementTraceEvent {
        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractElementTraceEvent
        public /* bridge */ /* synthetic */ int getCol() {
            return super.getCol();
        }

        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractElementTraceEvent
        public /* bridge */ /* synthetic */ int getLine() {
            return super.getLine();
        }

        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractElementTraceEvent
        public /* bridge */ /* synthetic */ String getElementName() {
            return super.getElementName();
        }

        public AutoCloseElementStartTraceEvent(String elementName, int line, int col) {
            super(EventType.AUTO_CLOSE_ELEMENT_START, elementName, line, col);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/trace/MarkupTraceEvent$AutoCloseElementEndTraceEvent.class */
    public static final class AutoCloseElementEndTraceEvent extends AbstractElementTraceEvent {
        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractElementTraceEvent
        public /* bridge */ /* synthetic */ int getCol() {
            return super.getCol();
        }

        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractElementTraceEvent
        public /* bridge */ /* synthetic */ int getLine() {
            return super.getLine();
        }

        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractElementTraceEvent
        public /* bridge */ /* synthetic */ String getElementName() {
            return super.getElementName();
        }

        public AutoCloseElementEndTraceEvent(String elementName, int line, int col) {
            super(EventType.AUTO_CLOSE_ELEMENT_END, elementName, line, col);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/trace/MarkupTraceEvent$UnmatchedCloseElementStartTraceEvent.class */
    public static final class UnmatchedCloseElementStartTraceEvent extends AbstractElementTraceEvent {
        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractElementTraceEvent
        public /* bridge */ /* synthetic */ int getCol() {
            return super.getCol();
        }

        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractElementTraceEvent
        public /* bridge */ /* synthetic */ int getLine() {
            return super.getLine();
        }

        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractElementTraceEvent
        public /* bridge */ /* synthetic */ String getElementName() {
            return super.getElementName();
        }

        public UnmatchedCloseElementStartTraceEvent(String elementName, int line, int col) {
            super(EventType.UNMATCHED_CLOSE_ELEMENT_START, elementName, line, col);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/trace/MarkupTraceEvent$UnmatchedCloseElementEndTraceEvent.class */
    public static final class UnmatchedCloseElementEndTraceEvent extends AbstractElementTraceEvent {
        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractElementTraceEvent
        public /* bridge */ /* synthetic */ int getCol() {
            return super.getCol();
        }

        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractElementTraceEvent
        public /* bridge */ /* synthetic */ int getLine() {
            return super.getLine();
        }

        @Override // org.attoparser.trace.MarkupTraceEvent.AbstractElementTraceEvent
        public /* bridge */ /* synthetic */ String getElementName() {
            return super.getElementName();
        }

        public UnmatchedCloseElementEndTraceEvent(String elementName, int line, int col) {
            super(EventType.UNMATCHED_CLOSE_ELEMENT_END, elementName, line, col);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/trace/MarkupTraceEvent$AttributeTraceEvent.class */
    public static final class AttributeTraceEvent extends MarkupTraceEvent {
        public AttributeTraceEvent(String name, int nameLine, int nameCol, String operator, int operatorLine, int operatorCol, String outerValue, int valueLine, int valueCol) {
            super(EventType.ATTRIBUTE, new int[]{nameLine, operatorLine, valueLine}, new int[]{nameCol, operatorCol, valueCol}, new String[]{name, operator, outerValue});
            if (name == null || name.trim().equals("")) {
                throw new IllegalArgumentException("Attribute name cannot be null or empty");
            }
        }

        public String getName() {
            return this.contents[0];
        }

        public String getOperator() {
            return this.contents[1];
        }

        public String getOuterValue() {
            return this.contents[2];
        }

        public int getNameLine() {
            return this.lines[0];
        }

        public int getNameCol() {
            return this.cols[0];
        }

        public int getOperatorLine() {
            return this.lines[1];
        }

        public int getOperatorCol() {
            return this.cols[1];
        }

        public int getOuterValueLine() {
            return this.lines[2];
        }

        public int getOuterValueCol() {
            return this.cols[2];
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/trace/MarkupTraceEvent$XmlDeclarationTraceEvent.class */
    public static final class XmlDeclarationTraceEvent extends MarkupTraceEvent {
        public XmlDeclarationTraceEvent(String keyword, int keywordLine, int keywordCol, String version, int versionLine, int versionCol, String encoding, int encodingLine, int encodingCol, String standalone, int standaloneLine, int standaloneCol) {
            super(EventType.XML_DECLARATION, new int[]{keywordLine, versionLine, encodingLine, standaloneLine}, new int[]{keywordCol, versionCol, encodingCol, standaloneCol}, new String[]{keyword, version, encoding, standalone});
            if (keyword == null || keyword.trim().equals("")) {
                throw new IllegalArgumentException("Keyword cannot be null or empty");
            }
        }

        public String getKeyword() {
            return this.contents[0];
        }

        public String getVersion() {
            return this.contents[1];
        }

        public String getEncoding() {
            return this.contents[2];
        }

        public String getStandalone() {
            return this.contents[3];
        }

        public int getKeywordLine() {
            return this.lines[0];
        }

        public int getVersionLine() {
            return this.lines[1];
        }

        public int getEncodingLine() {
            return this.lines[2];
        }

        public int getStandaloneLine() {
            return this.lines[3];
        }

        public int getKeywordCol() {
            return this.lines[0];
        }

        public int getVersionCol() {
            return this.lines[1];
        }

        public int getEncodingCol() {
            return this.lines[2];
        }

        public int getStandaloneCol() {
            return this.lines[3];
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/trace/MarkupTraceEvent$DocTypeTraceEvent.class */
    public static final class DocTypeTraceEvent extends MarkupTraceEvent {
        public DocTypeTraceEvent(String keyword, int keywordLine, int keywordCol, String elementName, int elementNameLine, int elementNameCol, String type, int typeLine, int typeCol, String publicId, int publicIdLine, int publicIdCol, String systemId, int systemIdLine, int systemIdCol, String internalSubset, int internalSubsetLine, int internalSubsetCol) {
            super(EventType.DOC_TYPE, new int[]{keywordLine, elementNameLine, typeLine, publicIdLine, systemIdLine, internalSubsetLine}, new int[]{keywordCol, elementNameCol, typeCol, publicIdCol, systemIdCol, internalSubsetCol}, new String[]{keyword, elementName, type, publicId, systemId, internalSubset});
            if (keyword == null || keyword.trim().equals("")) {
                throw new IllegalArgumentException("Keyword cannot be null or empty");
            }
        }

        public String getKeyword() {
            return this.contents[0];
        }

        public String getElementName() {
            return this.contents[1];
        }

        public String getType() {
            return this.contents[2];
        }

        public String getPublicId() {
            return this.contents[3];
        }

        public String getSystemId() {
            return this.contents[4];
        }

        public String getInternalSubset() {
            return this.contents[5];
        }

        public int getKeywordLine() {
            return this.lines[0];
        }

        public int getElementNameLine() {
            return this.lines[1];
        }

        public int getTypeLine() {
            return this.lines[2];
        }

        public int getPublicIdLine() {
            return this.lines[3];
        }

        public int getSystemIdLine() {
            return this.lines[4];
        }

        public int getInternalSubsetLine() {
            return this.lines[5];
        }

        public int getKeywordCol() {
            return this.lines[0];
        }

        public int getElementNameCol() {
            return this.lines[1];
        }

        public int getTypeCol() {
            return this.lines[2];
        }

        public int getPublicIdCol() {
            return this.lines[3];
        }

        public int getSystemIdCol() {
            return this.lines[4];
        }

        public int getInternalSubsetCol() {
            return this.lines[5];
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/trace/MarkupTraceEvent$ProcessingInstructionTraceEvent.class */
    public static final class ProcessingInstructionTraceEvent extends MarkupTraceEvent {
        public ProcessingInstructionTraceEvent(String target, int targetLine, int targetCol, String content, int contentLine, int contentCol) {
            super(EventType.PROCESSING_INSTRUCTION, new int[]{targetLine, contentLine}, new int[]{targetCol, contentCol}, new String[]{target, content});
            if (target == null || target.trim().equals("")) {
                throw new IllegalArgumentException("Target cannot be null or empty");
            }
        }

        public String getTarget() {
            return this.contents[0];
        }

        public String getContent() {
            return this.contents[1];
        }

        public int getTargetLine() {
            return this.lines[0];
        }

        public int getContentLine() {
            return this.lines[1];
        }

        public int getTargetCol() {
            return this.lines[0];
        }

        public int getContentCol() {
            return this.lines[1];
        }
    }
}