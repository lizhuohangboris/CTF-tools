package org.yaml.snakeyaml.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.thymeleaf.standard.processor.StandardBlockTagProcessor;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.events.AliasEvent;
import org.yaml.snakeyaml.events.DocumentEndEvent;
import org.yaml.snakeyaml.events.DocumentStartEvent;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.ImplicitTuple;
import org.yaml.snakeyaml.events.MappingEndEvent;
import org.yaml.snakeyaml.events.MappingStartEvent;
import org.yaml.snakeyaml.events.ScalarEvent;
import org.yaml.snakeyaml.events.SequenceEndEvent;
import org.yaml.snakeyaml.events.SequenceStartEvent;
import org.yaml.snakeyaml.events.StreamEndEvent;
import org.yaml.snakeyaml.events.StreamStartEvent;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.reader.StreamReader;
import org.yaml.snakeyaml.scanner.Scanner;
import org.yaml.snakeyaml.scanner.ScannerImpl;
import org.yaml.snakeyaml.tokens.AliasToken;
import org.yaml.snakeyaml.tokens.AnchorToken;
import org.yaml.snakeyaml.tokens.BlockEntryToken;
import org.yaml.snakeyaml.tokens.DirectiveToken;
import org.yaml.snakeyaml.tokens.ScalarToken;
import org.yaml.snakeyaml.tokens.StreamEndToken;
import org.yaml.snakeyaml.tokens.StreamStartToken;
import org.yaml.snakeyaml.tokens.TagToken;
import org.yaml.snakeyaml.tokens.TagTuple;
import org.yaml.snakeyaml.tokens.Token;
import org.yaml.snakeyaml.util.ArrayStack;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/parser/ParserImpl.class */
public class ParserImpl implements Parser {
    private static final Map<String, String> DEFAULT_TAGS = new HashMap();
    protected final Scanner scanner;
    private Event currentEvent;
    private final ArrayStack<Production> states;
    private final ArrayStack<Mark> marks;
    private Production state;
    private VersionTagsTuple directives;

    static {
        DEFAULT_TAGS.put("!", "!");
        DEFAULT_TAGS.put("!!", Tag.PREFIX);
    }

    public ParserImpl(StreamReader reader) {
        this(new ScannerImpl(reader));
    }

    public ParserImpl(Scanner scanner) {
        this.scanner = scanner;
        this.currentEvent = null;
        this.directives = new VersionTagsTuple(null, new HashMap(DEFAULT_TAGS));
        this.states = new ArrayStack<>(100);
        this.marks = new ArrayStack<>(10);
        this.state = new ParseStreamStart();
    }

    @Override // org.yaml.snakeyaml.parser.Parser
    public boolean checkEvent(Event.ID choice) {
        peekEvent();
        return this.currentEvent != null && this.currentEvent.is(choice);
    }

    @Override // org.yaml.snakeyaml.parser.Parser
    public Event peekEvent() {
        if (this.currentEvent == null && this.state != null) {
            this.currentEvent = this.state.produce();
        }
        return this.currentEvent;
    }

    @Override // org.yaml.snakeyaml.parser.Parser
    public Event getEvent() {
        peekEvent();
        Event value = this.currentEvent;
        this.currentEvent = null;
        return value;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/parser/ParserImpl$ParseStreamStart.class */
    private class ParseStreamStart implements Production {
        private ParseStreamStart() {
        }

        @Override // org.yaml.snakeyaml.parser.Production
        public Event produce() {
            StreamStartToken token = (StreamStartToken) ParserImpl.this.scanner.getToken();
            Event event = new StreamStartEvent(token.getStartMark(), token.getEndMark());
            ParserImpl.this.state = new ParseImplicitDocumentStart();
            return event;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/parser/ParserImpl$ParseImplicitDocumentStart.class */
    private class ParseImplicitDocumentStart implements Production {
        private ParseImplicitDocumentStart() {
        }

        @Override // org.yaml.snakeyaml.parser.Production
        public Event produce() {
            if (!ParserImpl.this.scanner.checkToken(Token.ID.Directive, Token.ID.DocumentStart, Token.ID.StreamEnd)) {
                ParserImpl.this.directives = new VersionTagsTuple(null, ParserImpl.DEFAULT_TAGS);
                Token token = ParserImpl.this.scanner.peekToken();
                Mark startMark = token.getStartMark();
                Event event = new DocumentStartEvent(startMark, startMark, false, null, null);
                ParserImpl.this.states.push(new ParseDocumentEnd());
                ParserImpl.this.state = new ParseBlockNode();
                return event;
            }
            Production p = new ParseDocumentStart();
            return p.produce();
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/parser/ParserImpl$ParseDocumentStart.class */
    private class ParseDocumentStart implements Production {
        private ParseDocumentStart() {
        }

        @Override // org.yaml.snakeyaml.parser.Production
        public Event produce() {
            Event event;
            while (ParserImpl.this.scanner.checkToken(Token.ID.DocumentEnd)) {
                ParserImpl.this.scanner.getToken();
            }
            if (!ParserImpl.this.scanner.checkToken(Token.ID.StreamEnd)) {
                Mark startMark = ParserImpl.this.scanner.peekToken().getStartMark();
                VersionTagsTuple tuple = ParserImpl.this.processDirectives();
                if (!ParserImpl.this.scanner.checkToken(Token.ID.DocumentStart)) {
                    throw new ParserException(null, null, "expected '<document start>', but found '" + ParserImpl.this.scanner.peekToken().getTokenId() + "'", ParserImpl.this.scanner.peekToken().getStartMark());
                }
                Mark endMark = ParserImpl.this.scanner.getToken().getEndMark();
                event = new DocumentStartEvent(startMark, endMark, true, tuple.getVersion(), tuple.getTags());
                ParserImpl.this.states.push(new ParseDocumentEnd());
                ParserImpl.this.state = new ParseDocumentContent();
            } else {
                StreamEndToken token = (StreamEndToken) ParserImpl.this.scanner.getToken();
                event = new StreamEndEvent(token.getStartMark(), token.getEndMark());
                if (ParserImpl.this.states.isEmpty()) {
                    if (ParserImpl.this.marks.isEmpty()) {
                        ParserImpl.this.state = null;
                    } else {
                        throw new YAMLException("Unexpected end of stream. Marks left: " + ParserImpl.this.marks);
                    }
                } else {
                    throw new YAMLException("Unexpected end of stream. States left: " + ParserImpl.this.states);
                }
            }
            return event;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/parser/ParserImpl$ParseDocumentEnd.class */
    private class ParseDocumentEnd implements Production {
        private ParseDocumentEnd() {
        }

        @Override // org.yaml.snakeyaml.parser.Production
        public Event produce() {
            Token token = ParserImpl.this.scanner.peekToken();
            Mark startMark = token.getStartMark();
            Mark endMark = startMark;
            boolean explicit = false;
            if (ParserImpl.this.scanner.checkToken(Token.ID.DocumentEnd)) {
                Token token2 = ParserImpl.this.scanner.getToken();
                endMark = token2.getEndMark();
                explicit = true;
            }
            Event event = new DocumentEndEvent(startMark, endMark, explicit);
            ParserImpl.this.state = new ParseDocumentStart();
            return event;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/parser/ParserImpl$ParseDocumentContent.class */
    private class ParseDocumentContent implements Production {
        private ParseDocumentContent() {
        }

        @Override // org.yaml.snakeyaml.parser.Production
        public Event produce() {
            if (ParserImpl.this.scanner.checkToken(Token.ID.Directive, Token.ID.DocumentStart, Token.ID.DocumentEnd, Token.ID.StreamEnd)) {
                Event event = ParserImpl.this.processEmptyScalar(ParserImpl.this.scanner.peekToken().getStartMark());
                ParserImpl.this.state = (Production) ParserImpl.this.states.pop();
                return event;
            }
            Production p = new ParseBlockNode();
            return p.produce();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public VersionTagsTuple processDirectives() {
        DumperOptions.Version yamlVersion = null;
        HashMap<String, String> tagHandles = new HashMap<>();
        while (this.scanner.checkToken(Token.ID.Directive)) {
            DirectiveToken token = (DirectiveToken) this.scanner.getToken();
            if (token.getName().equals("YAML")) {
                if (yamlVersion != null) {
                    throw new ParserException(null, null, "found duplicate YAML directive", token.getStartMark());
                }
                List<Integer> value = token.getValue();
                Integer major = value.get(0);
                if (major.intValue() != 1) {
                    throw new ParserException(null, null, "found incompatible YAML document (version 1.* is required)", token.getStartMark());
                }
                Integer minor = value.get(1);
                switch (minor.intValue()) {
                    case 0:
                        yamlVersion = DumperOptions.Version.V1_0;
                        continue;
                    default:
                        yamlVersion = DumperOptions.Version.V1_1;
                        continue;
                }
            } else if (token.getName().equals("TAG")) {
                List<String> value2 = token.getValue();
                String handle = value2.get(0);
                String prefix = value2.get(1);
                if (tagHandles.containsKey(handle)) {
                    throw new ParserException(null, null, "duplicate tag handle " + handle, token.getStartMark());
                }
                tagHandles.put(handle, prefix);
            } else {
                continue;
            }
        }
        if (yamlVersion != null || !tagHandles.isEmpty()) {
            for (String key : DEFAULT_TAGS.keySet()) {
                if (!tagHandles.containsKey(key)) {
                    tagHandles.put(key, DEFAULT_TAGS.get(key));
                }
            }
            this.directives = new VersionTagsTuple(yamlVersion, tagHandles);
        }
        return this.directives;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/parser/ParserImpl$ParseBlockNode.class */
    public class ParseBlockNode implements Production {
        private ParseBlockNode() {
        }

        @Override // org.yaml.snakeyaml.parser.Production
        public Event produce() {
            return ParserImpl.this.parseNode(true, false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Event parseFlowNode() {
        return parseNode(false, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Event parseBlockNodeOrIndentlessSequence() {
        return parseNode(true, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Event parseNode(boolean block, boolean indentlessSequence) {
        Event event;
        String node;
        ImplicitTuple implicitValues;
        Mark startMark = null;
        Mark endMark = null;
        Mark tagMark = null;
        if (this.scanner.checkToken(Token.ID.Alias)) {
            AliasToken token = (AliasToken) this.scanner.getToken();
            event = new AliasEvent(token.getValue(), token.getStartMark(), token.getEndMark());
            this.state = this.states.pop();
        } else {
            String anchor = null;
            TagTuple tagTokenTag = null;
            if (this.scanner.checkToken(Token.ID.Anchor)) {
                AnchorToken token2 = (AnchorToken) this.scanner.getToken();
                startMark = token2.getStartMark();
                endMark = token2.getEndMark();
                anchor = token2.getValue();
                if (this.scanner.checkToken(Token.ID.Tag)) {
                    TagToken tagToken = (TagToken) this.scanner.getToken();
                    tagMark = tagToken.getStartMark();
                    endMark = tagToken.getEndMark();
                    tagTokenTag = tagToken.getValue();
                }
            } else if (this.scanner.checkToken(Token.ID.Tag)) {
                TagToken tagToken2 = (TagToken) this.scanner.getToken();
                startMark = tagToken2.getStartMark();
                tagMark = startMark;
                endMark = tagToken2.getEndMark();
                tagTokenTag = tagToken2.getValue();
                if (this.scanner.checkToken(Token.ID.Anchor)) {
                    AnchorToken token3 = (AnchorToken) this.scanner.getToken();
                    endMark = token3.getEndMark();
                    anchor = token3.getValue();
                }
            }
            String tag = null;
            if (tagTokenTag != null) {
                String handle = tagTokenTag.getHandle();
                String suffix = tagTokenTag.getSuffix();
                if (handle != null) {
                    if (!this.directives.getTags().containsKey(handle)) {
                        throw new ParserException("while parsing a node", startMark, "found undefined tag handle " + handle, tagMark);
                    }
                    tag = this.directives.getTags().get(handle) + suffix;
                } else {
                    tag = suffix;
                }
            }
            if (startMark == null) {
                startMark = this.scanner.peekToken().getStartMark();
                endMark = startMark;
            }
            boolean implicit = tag == null || tag.equals("!");
            if (indentlessSequence && this.scanner.checkToken(Token.ID.BlockEntry)) {
                Mark endMark2 = this.scanner.peekToken().getEndMark();
                event = new SequenceStartEvent(anchor, tag, implicit, startMark, endMark2, DumperOptions.FlowStyle.BLOCK);
                this.state = new ParseIndentlessSequenceEntry();
            } else if (this.scanner.checkToken(Token.ID.Scalar)) {
                ScalarToken token4 = (ScalarToken) this.scanner.getToken();
                Mark endMark3 = token4.getEndMark();
                if ((token4.getPlain() && tag == null) || "!".equals(tag)) {
                    implicitValues = new ImplicitTuple(true, false);
                } else if (tag == null) {
                    implicitValues = new ImplicitTuple(false, true);
                } else {
                    implicitValues = new ImplicitTuple(false, false);
                }
                event = new ScalarEvent(anchor, tag, implicitValues, token4.getValue(), startMark, endMark3, token4.getStyle());
                this.state = this.states.pop();
            } else if (this.scanner.checkToken(Token.ID.FlowSequenceStart)) {
                Mark endMark4 = this.scanner.peekToken().getEndMark();
                event = new SequenceStartEvent(anchor, tag, implicit, startMark, endMark4, DumperOptions.FlowStyle.FLOW);
                this.state = new ParseFlowSequenceFirstEntry();
            } else if (this.scanner.checkToken(Token.ID.FlowMappingStart)) {
                Mark endMark5 = this.scanner.peekToken().getEndMark();
                event = new MappingStartEvent(anchor, tag, implicit, startMark, endMark5, DumperOptions.FlowStyle.FLOW);
                this.state = new ParseFlowMappingFirstKey();
            } else if (block && this.scanner.checkToken(Token.ID.BlockSequenceStart)) {
                Mark endMark6 = this.scanner.peekToken().getStartMark();
                event = new SequenceStartEvent(anchor, tag, implicit, startMark, endMark6, DumperOptions.FlowStyle.BLOCK);
                this.state = new ParseBlockSequenceFirstEntry();
            } else if (block && this.scanner.checkToken(Token.ID.BlockMappingStart)) {
                Mark endMark7 = this.scanner.peekToken().getStartMark();
                event = new MappingStartEvent(anchor, tag, implicit, startMark, endMark7, DumperOptions.FlowStyle.BLOCK);
                this.state = new ParseBlockMappingFirstKey();
            } else if (anchor != null || tag != null) {
                event = new ScalarEvent(anchor, tag, new ImplicitTuple(implicit, false), "", startMark, endMark, DumperOptions.ScalarStyle.PLAIN);
                this.state = this.states.pop();
            } else {
                if (block) {
                    node = StandardBlockTagProcessor.ELEMENT_NAME;
                } else {
                    node = "flow";
                }
                Token token5 = this.scanner.peekToken();
                throw new ParserException("while parsing a " + node + " node", startMark, "expected the node content, but found '" + token5.getTokenId() + "'", token5.getStartMark());
            }
        }
        return event;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/parser/ParserImpl$ParseBlockSequenceFirstEntry.class */
    public class ParseBlockSequenceFirstEntry implements Production {
        private ParseBlockSequenceFirstEntry() {
        }

        @Override // org.yaml.snakeyaml.parser.Production
        public Event produce() {
            Token token = ParserImpl.this.scanner.getToken();
            ParserImpl.this.marks.push(token.getStartMark());
            return new ParseBlockSequenceEntry().produce();
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/parser/ParserImpl$ParseBlockSequenceEntry.class */
    private class ParseBlockSequenceEntry implements Production {
        private ParseBlockSequenceEntry() {
        }

        @Override // org.yaml.snakeyaml.parser.Production
        public Event produce() {
            if (ParserImpl.this.scanner.checkToken(Token.ID.BlockEntry)) {
                BlockEntryToken token = (BlockEntryToken) ParserImpl.this.scanner.getToken();
                if (!ParserImpl.this.scanner.checkToken(Token.ID.BlockEntry, Token.ID.BlockEnd)) {
                    ParserImpl.this.states.push(new ParseBlockSequenceEntry());
                    return new ParseBlockNode().produce();
                }
                ParserImpl.this.state = new ParseBlockSequenceEntry();
                return ParserImpl.this.processEmptyScalar(token.getEndMark());
            } else if (!ParserImpl.this.scanner.checkToken(Token.ID.BlockEnd)) {
                Token token2 = ParserImpl.this.scanner.peekToken();
                throw new ParserException("while parsing a block collection", (Mark) ParserImpl.this.marks.pop(), "expected <block end>, but found '" + token2.getTokenId() + "'", token2.getStartMark());
            } else {
                Token token3 = ParserImpl.this.scanner.getToken();
                Event event = new SequenceEndEvent(token3.getStartMark(), token3.getEndMark());
                ParserImpl.this.state = (Production) ParserImpl.this.states.pop();
                ParserImpl.this.marks.pop();
                return event;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/parser/ParserImpl$ParseIndentlessSequenceEntry.class */
    public class ParseIndentlessSequenceEntry implements Production {
        private ParseIndentlessSequenceEntry() {
        }

        @Override // org.yaml.snakeyaml.parser.Production
        public Event produce() {
            if (ParserImpl.this.scanner.checkToken(Token.ID.BlockEntry)) {
                Token token = ParserImpl.this.scanner.getToken();
                if (!ParserImpl.this.scanner.checkToken(Token.ID.BlockEntry, Token.ID.Key, Token.ID.Value, Token.ID.BlockEnd)) {
                    ParserImpl.this.states.push(new ParseIndentlessSequenceEntry());
                    return new ParseBlockNode().produce();
                }
                ParserImpl.this.state = new ParseIndentlessSequenceEntry();
                return ParserImpl.this.processEmptyScalar(token.getEndMark());
            }
            Token token2 = ParserImpl.this.scanner.peekToken();
            Event event = new SequenceEndEvent(token2.getStartMark(), token2.getEndMark());
            ParserImpl.this.state = (Production) ParserImpl.this.states.pop();
            return event;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/parser/ParserImpl$ParseBlockMappingFirstKey.class */
    public class ParseBlockMappingFirstKey implements Production {
        private ParseBlockMappingFirstKey() {
        }

        @Override // org.yaml.snakeyaml.parser.Production
        public Event produce() {
            Token token = ParserImpl.this.scanner.getToken();
            ParserImpl.this.marks.push(token.getStartMark());
            return new ParseBlockMappingKey().produce();
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/parser/ParserImpl$ParseBlockMappingKey.class */
    private class ParseBlockMappingKey implements Production {
        private ParseBlockMappingKey() {
        }

        @Override // org.yaml.snakeyaml.parser.Production
        public Event produce() {
            if (ParserImpl.this.scanner.checkToken(Token.ID.Key)) {
                Token token = ParserImpl.this.scanner.getToken();
                if (!ParserImpl.this.scanner.checkToken(Token.ID.Key, Token.ID.Value, Token.ID.BlockEnd)) {
                    ParserImpl.this.states.push(new ParseBlockMappingValue());
                    return ParserImpl.this.parseBlockNodeOrIndentlessSequence();
                }
                ParserImpl.this.state = new ParseBlockMappingValue();
                return ParserImpl.this.processEmptyScalar(token.getEndMark());
            } else if (!ParserImpl.this.scanner.checkToken(Token.ID.BlockEnd)) {
                Token token2 = ParserImpl.this.scanner.peekToken();
                throw new ParserException("while parsing a block mapping", (Mark) ParserImpl.this.marks.pop(), "expected <block end>, but found '" + token2.getTokenId() + "'", token2.getStartMark());
            } else {
                Token token3 = ParserImpl.this.scanner.getToken();
                Event event = new MappingEndEvent(token3.getStartMark(), token3.getEndMark());
                ParserImpl.this.state = (Production) ParserImpl.this.states.pop();
                ParserImpl.this.marks.pop();
                return event;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/parser/ParserImpl$ParseBlockMappingValue.class */
    public class ParseBlockMappingValue implements Production {
        private ParseBlockMappingValue() {
        }

        @Override // org.yaml.snakeyaml.parser.Production
        public Event produce() {
            if (!ParserImpl.this.scanner.checkToken(Token.ID.Value)) {
                ParserImpl.this.state = new ParseBlockMappingKey();
                Token token = ParserImpl.this.scanner.peekToken();
                return ParserImpl.this.processEmptyScalar(token.getStartMark());
            }
            Token token2 = ParserImpl.this.scanner.getToken();
            if (!ParserImpl.this.scanner.checkToken(Token.ID.Key, Token.ID.Value, Token.ID.BlockEnd)) {
                ParserImpl.this.states.push(new ParseBlockMappingKey());
                return ParserImpl.this.parseBlockNodeOrIndentlessSequence();
            }
            ParserImpl.this.state = new ParseBlockMappingKey();
            return ParserImpl.this.processEmptyScalar(token2.getEndMark());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/parser/ParserImpl$ParseFlowSequenceFirstEntry.class */
    public class ParseFlowSequenceFirstEntry implements Production {
        private ParseFlowSequenceFirstEntry() {
        }

        @Override // org.yaml.snakeyaml.parser.Production
        public Event produce() {
            Token token = ParserImpl.this.scanner.getToken();
            ParserImpl.this.marks.push(token.getStartMark());
            return new ParseFlowSequenceEntry(true).produce();
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/parser/ParserImpl$ParseFlowSequenceEntry.class */
    private class ParseFlowSequenceEntry implements Production {
        private boolean first;

        public ParseFlowSequenceEntry(boolean first) {
            this.first = false;
            this.first = first;
        }

        @Override // org.yaml.snakeyaml.parser.Production
        public Event produce() {
            if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowSequenceEnd)) {
                if (!this.first) {
                    if (ParserImpl.this.scanner.checkToken(Token.ID.FlowEntry)) {
                        ParserImpl.this.scanner.getToken();
                    } else {
                        Token token = ParserImpl.this.scanner.peekToken();
                        throw new ParserException("while parsing a flow sequence", (Mark) ParserImpl.this.marks.pop(), "expected ',' or ']', but got " + token.getTokenId(), token.getStartMark());
                    }
                }
                if (ParserImpl.this.scanner.checkToken(Token.ID.Key)) {
                    Token token2 = ParserImpl.this.scanner.peekToken();
                    Event event = new MappingStartEvent((String) null, (String) null, true, token2.getStartMark(), token2.getEndMark(), DumperOptions.FlowStyle.FLOW);
                    ParserImpl.this.state = new ParseFlowSequenceEntryMappingKey();
                    return event;
                } else if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowSequenceEnd)) {
                    ParserImpl.this.states.push(new ParseFlowSequenceEntry(false));
                    return ParserImpl.this.parseFlowNode();
                }
            }
            Token token3 = ParserImpl.this.scanner.getToken();
            Event event2 = new SequenceEndEvent(token3.getStartMark(), token3.getEndMark());
            ParserImpl.this.state = (Production) ParserImpl.this.states.pop();
            ParserImpl.this.marks.pop();
            return event2;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/parser/ParserImpl$ParseFlowSequenceEntryMappingKey.class */
    public class ParseFlowSequenceEntryMappingKey implements Production {
        private ParseFlowSequenceEntryMappingKey() {
        }

        @Override // org.yaml.snakeyaml.parser.Production
        public Event produce() {
            Token token = ParserImpl.this.scanner.getToken();
            if (!ParserImpl.this.scanner.checkToken(Token.ID.Value, Token.ID.FlowEntry, Token.ID.FlowSequenceEnd)) {
                ParserImpl.this.states.push(new ParseFlowSequenceEntryMappingValue());
                return ParserImpl.this.parseFlowNode();
            }
            ParserImpl.this.state = new ParseFlowSequenceEntryMappingValue();
            return ParserImpl.this.processEmptyScalar(token.getEndMark());
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/parser/ParserImpl$ParseFlowSequenceEntryMappingValue.class */
    private class ParseFlowSequenceEntryMappingValue implements Production {
        private ParseFlowSequenceEntryMappingValue() {
        }

        @Override // org.yaml.snakeyaml.parser.Production
        public Event produce() {
            if (!ParserImpl.this.scanner.checkToken(Token.ID.Value)) {
                ParserImpl.this.state = new ParseFlowSequenceEntryMappingEnd();
                Token token = ParserImpl.this.scanner.peekToken();
                return ParserImpl.this.processEmptyScalar(token.getStartMark());
            }
            Token token2 = ParserImpl.this.scanner.getToken();
            if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowEntry, Token.ID.FlowSequenceEnd)) {
                ParserImpl.this.states.push(new ParseFlowSequenceEntryMappingEnd());
                return ParserImpl.this.parseFlowNode();
            }
            ParserImpl.this.state = new ParseFlowSequenceEntryMappingEnd();
            return ParserImpl.this.processEmptyScalar(token2.getEndMark());
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/parser/ParserImpl$ParseFlowSequenceEntryMappingEnd.class */
    private class ParseFlowSequenceEntryMappingEnd implements Production {
        private ParseFlowSequenceEntryMappingEnd() {
        }

        @Override // org.yaml.snakeyaml.parser.Production
        public Event produce() {
            ParserImpl.this.state = new ParseFlowSequenceEntry(false);
            Token token = ParserImpl.this.scanner.peekToken();
            return new MappingEndEvent(token.getStartMark(), token.getEndMark());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/parser/ParserImpl$ParseFlowMappingFirstKey.class */
    public class ParseFlowMappingFirstKey implements Production {
        private ParseFlowMappingFirstKey() {
        }

        @Override // org.yaml.snakeyaml.parser.Production
        public Event produce() {
            Token token = ParserImpl.this.scanner.getToken();
            ParserImpl.this.marks.push(token.getStartMark());
            return new ParseFlowMappingKey(true).produce();
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/parser/ParserImpl$ParseFlowMappingKey.class */
    private class ParseFlowMappingKey implements Production {
        private boolean first;

        public ParseFlowMappingKey(boolean first) {
            this.first = false;
            this.first = first;
        }

        @Override // org.yaml.snakeyaml.parser.Production
        public Event produce() {
            if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowMappingEnd)) {
                if (!this.first) {
                    if (ParserImpl.this.scanner.checkToken(Token.ID.FlowEntry)) {
                        ParserImpl.this.scanner.getToken();
                    } else {
                        Token token = ParserImpl.this.scanner.peekToken();
                        throw new ParserException("while parsing a flow mapping", (Mark) ParserImpl.this.marks.pop(), "expected ',' or '}', but got " + token.getTokenId(), token.getStartMark());
                    }
                }
                if (ParserImpl.this.scanner.checkToken(Token.ID.Key)) {
                    Token token2 = ParserImpl.this.scanner.getToken();
                    if (!ParserImpl.this.scanner.checkToken(Token.ID.Value, Token.ID.FlowEntry, Token.ID.FlowMappingEnd)) {
                        ParserImpl.this.states.push(new ParseFlowMappingValue());
                        return ParserImpl.this.parseFlowNode();
                    }
                    ParserImpl.this.state = new ParseFlowMappingValue();
                    return ParserImpl.this.processEmptyScalar(token2.getEndMark());
                } else if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowMappingEnd)) {
                    ParserImpl.this.states.push(new ParseFlowMappingEmptyValue());
                    return ParserImpl.this.parseFlowNode();
                }
            }
            Token token3 = ParserImpl.this.scanner.getToken();
            Event event = new MappingEndEvent(token3.getStartMark(), token3.getEndMark());
            ParserImpl.this.state = (Production) ParserImpl.this.states.pop();
            ParserImpl.this.marks.pop();
            return event;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/parser/ParserImpl$ParseFlowMappingValue.class */
    public class ParseFlowMappingValue implements Production {
        private ParseFlowMappingValue() {
        }

        @Override // org.yaml.snakeyaml.parser.Production
        public Event produce() {
            if (!ParserImpl.this.scanner.checkToken(Token.ID.Value)) {
                ParserImpl.this.state = new ParseFlowMappingKey(false);
                Token token = ParserImpl.this.scanner.peekToken();
                return ParserImpl.this.processEmptyScalar(token.getStartMark());
            }
            Token token2 = ParserImpl.this.scanner.getToken();
            if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowEntry, Token.ID.FlowMappingEnd)) {
                ParserImpl.this.states.push(new ParseFlowMappingKey(false));
                return ParserImpl.this.parseFlowNode();
            }
            ParserImpl.this.state = new ParseFlowMappingKey(false);
            return ParserImpl.this.processEmptyScalar(token2.getEndMark());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/parser/ParserImpl$ParseFlowMappingEmptyValue.class */
    public class ParseFlowMappingEmptyValue implements Production {
        private ParseFlowMappingEmptyValue() {
        }

        @Override // org.yaml.snakeyaml.parser.Production
        public Event produce() {
            ParserImpl.this.state = new ParseFlowMappingKey(false);
            return ParserImpl.this.processEmptyScalar(ParserImpl.this.scanner.peekToken().getStartMark());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Event processEmptyScalar(Mark mark) {
        return new ScalarEvent((String) null, (String) null, new ImplicitTuple(true, false), "", mark, mark, DumperOptions.ScalarStyle.PLAIN);
    }
}