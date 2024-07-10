package org.yaml.snakeyaml;

import java.util.Map;
import java.util.TimeZone;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.serializer.AnchorGenerator;
import org.yaml.snakeyaml.serializer.NumberAnchorGenerator;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/DumperOptions.class */
public class DumperOptions {
    private ScalarStyle defaultStyle = ScalarStyle.PLAIN;
    private FlowStyle defaultFlowStyle = FlowStyle.AUTO;
    private boolean canonical = false;
    private boolean allowUnicode = true;
    private boolean allowReadOnlyProperties = false;
    private int indent = 2;
    private int indicatorIndent = 0;
    private int bestWidth = 80;
    private boolean splitLines = true;
    private LineBreak lineBreak = LineBreak.UNIX;
    private boolean explicitStart = false;
    private boolean explicitEnd = false;
    private TimeZone timeZone = null;
    private Version version = null;
    private Map<String, String> tags = null;
    private Boolean prettyFlow = false;
    private AnchorGenerator anchorGenerator = new NumberAnchorGenerator(0);

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/DumperOptions$ScalarStyle.class */
    public enum ScalarStyle {
        DOUBLE_QUOTED('\"'),
        SINGLE_QUOTED('\''),
        LITERAL('|'),
        FOLDED('>'),
        PLAIN(null);
        
        private Character styleChar;

        ScalarStyle(Character style) {
            this.styleChar = style;
        }

        public Character getChar() {
            return this.styleChar;
        }

        @Override // java.lang.Enum
        public String toString() {
            return "Scalar style: '" + this.styleChar + "'";
        }

        public static ScalarStyle createStyle(Character style) {
            if (style == null) {
                return PLAIN;
            }
            switch (style.charValue()) {
                case '\"':
                    return DOUBLE_QUOTED;
                case '\'':
                    return SINGLE_QUOTED;
                case '>':
                    return FOLDED;
                case '|':
                    return LITERAL;
                default:
                    throw new YAMLException("Unknown scalar style character: " + style);
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/DumperOptions$FlowStyle.class */
    public enum FlowStyle {
        FLOW(Boolean.TRUE),
        BLOCK(Boolean.FALSE),
        AUTO(null);
        
        private Boolean styleBoolean;

        FlowStyle(Boolean flowStyle) {
            this.styleBoolean = flowStyle;
        }

        @Deprecated
        public static FlowStyle fromBoolean(Boolean flowStyle) {
            return flowStyle == null ? AUTO : flowStyle.booleanValue() ? FLOW : BLOCK;
        }

        public Boolean getStyleBoolean() {
            return this.styleBoolean;
        }

        @Override // java.lang.Enum
        public String toString() {
            return "Flow style: '" + this.styleBoolean + "'";
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/DumperOptions$LineBreak.class */
    public enum LineBreak {
        WIN("\r\n"),
        MAC("\r"),
        UNIX("\n");
        
        private String lineBreak;

        LineBreak(String lineBreak) {
            this.lineBreak = lineBreak;
        }

        public String getString() {
            return this.lineBreak;
        }

        @Override // java.lang.Enum
        public String toString() {
            return "Line break: " + name();
        }

        public static LineBreak getPlatformLineBreak() {
            LineBreak[] values;
            String platformLineBreak = System.getProperty("line.separator");
            for (LineBreak lb : values()) {
                if (lb.lineBreak.equals(platformLineBreak)) {
                    return lb;
                }
            }
            return UNIX;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/DumperOptions$Version.class */
    public enum Version {
        V1_0(new Integer[]{1, 0}),
        V1_1(new Integer[]{1, 1});
        
        private Integer[] version;

        Version(Integer[] version) {
            this.version = version;
        }

        public int major() {
            return this.version[0].intValue();
        }

        public int minor() {
            return this.version[1].intValue();
        }

        public String getRepresentation() {
            return this.version[0] + "." + this.version[1];
        }

        @Override // java.lang.Enum
        public String toString() {
            return "Version: " + getRepresentation();
        }
    }

    public boolean isAllowUnicode() {
        return this.allowUnicode;
    }

    public void setAllowUnicode(boolean allowUnicode) {
        this.allowUnicode = allowUnicode;
    }

    public ScalarStyle getDefaultScalarStyle() {
        return this.defaultStyle;
    }

    public void setDefaultScalarStyle(ScalarStyle defaultStyle) {
        if (defaultStyle == null) {
            throw new NullPointerException("Use ScalarStyle enum.");
        }
        this.defaultStyle = defaultStyle;
    }

    public void setIndent(int indent) {
        if (indent < 1) {
            throw new YAMLException("Indent must be at least 1");
        }
        if (indent > 10) {
            throw new YAMLException("Indent must be at most 10");
        }
        this.indent = indent;
    }

    public int getIndent() {
        return this.indent;
    }

    public void setIndicatorIndent(int indicatorIndent) {
        if (indicatorIndent < 0) {
            throw new YAMLException("Indicator indent must be non-negative.");
        }
        if (indicatorIndent > 9) {
            throw new YAMLException("Indicator indent must be at most Emitter.MAX_INDENT-1: 9");
        }
        this.indicatorIndent = indicatorIndent;
    }

    public int getIndicatorIndent() {
        return this.indicatorIndent;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public Version getVersion() {
        return this.version;
    }

    public void setCanonical(boolean canonical) {
        this.canonical = canonical;
    }

    public boolean isCanonical() {
        return this.canonical;
    }

    public void setPrettyFlow(boolean prettyFlow) {
        this.prettyFlow = Boolean.valueOf(prettyFlow);
    }

    public boolean isPrettyFlow() {
        return this.prettyFlow.booleanValue();
    }

    public void setWidth(int bestWidth) {
        this.bestWidth = bestWidth;
    }

    public int getWidth() {
        return this.bestWidth;
    }

    public void setSplitLines(boolean splitLines) {
        this.splitLines = splitLines;
    }

    public boolean getSplitLines() {
        return this.splitLines;
    }

    public LineBreak getLineBreak() {
        return this.lineBreak;
    }

    public void setDefaultFlowStyle(FlowStyle defaultFlowStyle) {
        if (defaultFlowStyle == null) {
            throw new NullPointerException("Use FlowStyle enum.");
        }
        this.defaultFlowStyle = defaultFlowStyle;
    }

    public FlowStyle getDefaultFlowStyle() {
        return this.defaultFlowStyle;
    }

    public void setLineBreak(LineBreak lineBreak) {
        if (lineBreak == null) {
            throw new NullPointerException("Specify line break.");
        }
        this.lineBreak = lineBreak;
    }

    public boolean isExplicitStart() {
        return this.explicitStart;
    }

    public void setExplicitStart(boolean explicitStart) {
        this.explicitStart = explicitStart;
    }

    public boolean isExplicitEnd() {
        return this.explicitEnd;
    }

    public void setExplicitEnd(boolean explicitEnd) {
        this.explicitEnd = explicitEnd;
    }

    public Map<String, String> getTags() {
        return this.tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    public boolean isAllowReadOnlyProperties() {
        return this.allowReadOnlyProperties;
    }

    public void setAllowReadOnlyProperties(boolean allowReadOnlyProperties) {
        this.allowReadOnlyProperties = allowReadOnlyProperties;
    }

    public TimeZone getTimeZone() {
        return this.timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public AnchorGenerator getAnchorGenerator() {
        return this.anchorGenerator;
    }

    public void setAnchorGenerator(AnchorGenerator anchorGenerator) {
        this.anchorGenerator = anchorGenerator;
    }
}