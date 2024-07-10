package org.yaml.snakeyaml.constructor;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/constructor/SafeConstructor.class */
public class SafeConstructor extends BaseConstructor {
    public static final ConstructUndefined undefinedConstructor = new ConstructUndefined();
    private static final Map<String, Boolean> BOOL_VALUES = new HashMap();
    private static final Pattern TIMESTAMP_REGEXP;
    private static final Pattern YMD_REGEXP;

    static {
        BOOL_VALUES.put(CustomBooleanEditor.VALUE_YES, Boolean.TRUE);
        BOOL_VALUES.put("no", Boolean.FALSE);
        BOOL_VALUES.put("true", Boolean.TRUE);
        BOOL_VALUES.put("false", Boolean.FALSE);
        BOOL_VALUES.put(CustomBooleanEditor.VALUE_ON, Boolean.TRUE);
        BOOL_VALUES.put(CustomBooleanEditor.VALUE_OFF, Boolean.FALSE);
        TIMESTAMP_REGEXP = Pattern.compile("^([0-9][0-9][0-9][0-9])-([0-9][0-9]?)-([0-9][0-9]?)(?:(?:[Tt]|[ \t]+)([0-9][0-9]?):([0-9][0-9]):([0-9][0-9])(?:\\.([0-9]*))?(?:[ \t]*(?:Z|([-+][0-9][0-9]?)(?::([0-9][0-9])?)?))?)?$");
        YMD_REGEXP = Pattern.compile("^([0-9][0-9][0-9][0-9])-([0-9][0-9]?)-([0-9][0-9]?)$");
    }

    public SafeConstructor() {
        this.yamlConstructors.put(Tag.NULL, new ConstructYamlNull());
        this.yamlConstructors.put(Tag.BOOL, new ConstructYamlBool());
        this.yamlConstructors.put(Tag.INT, new ConstructYamlInt());
        this.yamlConstructors.put(Tag.FLOAT, new ConstructYamlFloat());
        this.yamlConstructors.put(Tag.BINARY, new ConstructYamlBinary());
        this.yamlConstructors.put(Tag.TIMESTAMP, new ConstructYamlTimestamp());
        this.yamlConstructors.put(Tag.OMAP, new ConstructYamlOmap());
        this.yamlConstructors.put(Tag.PAIRS, new ConstructYamlPairs());
        this.yamlConstructors.put(Tag.SET, new ConstructYamlSet());
        this.yamlConstructors.put(Tag.STR, new ConstructYamlStr());
        this.yamlConstructors.put(Tag.SEQ, new ConstructYamlSeq());
        this.yamlConstructors.put(Tag.MAP, new ConstructYamlMap());
        this.yamlConstructors.put(null, undefinedConstructor);
        this.yamlClassConstructors.put(NodeId.scalar, undefinedConstructor);
        this.yamlClassConstructors.put(NodeId.sequence, undefinedConstructor);
        this.yamlClassConstructors.put(NodeId.mapping, undefinedConstructor);
    }

    public void flattenMapping(MappingNode node) {
        processDuplicateKeys(node);
        if (node.isMerged()) {
            node.setValue(mergeNode(node, true, new HashMap(), new ArrayList()));
        }
    }

    protected void processDuplicateKeys(MappingNode node) {
        List<NodeTuple> nodeValue = node.getValue();
        Map<Object, Integer> keys = new HashMap<>(nodeValue.size());
        TreeSet<Integer> toRemove = new TreeSet<>();
        int i = 0;
        for (NodeTuple tuple : nodeValue) {
            Node keyNode = tuple.getKeyNode();
            if (!keyNode.getTag().equals(Tag.MERGE)) {
                Object key = constructObject(keyNode);
                if (key != null) {
                    try {
                        key.hashCode();
                    } catch (Exception e) {
                        throw new ConstructorException("while constructing a mapping", node.getStartMark(), "found unacceptable key " + key, tuple.getKeyNode().getStartMark(), e);
                    }
                }
                Integer prevIndex = keys.put(key, Integer.valueOf(i));
                if (prevIndex == null) {
                    continue;
                } else if (!isAllowDuplicateKeys()) {
                    throw new DuplicateKeyException(node.getStartMark(), key, tuple.getKeyNode().getStartMark());
                } else {
                    toRemove.add(prevIndex);
                }
            }
            i++;
        }
        Iterator<Integer> indicies2remove = toRemove.descendingIterator();
        while (indicies2remove.hasNext()) {
            nodeValue.remove(indicies2remove.next().intValue());
        }
    }

    private List<NodeTuple> mergeNode(MappingNode node, boolean isPreffered, Map<Object, Integer> key2index, List<NodeTuple> values) {
        Iterator<NodeTuple> iter = node.getValue().iterator();
        while (iter.hasNext()) {
            NodeTuple nodeTuple = iter.next();
            Node keyNode = nodeTuple.getKeyNode();
            Node valueNode = nodeTuple.getValueNode();
            if (keyNode.getTag().equals(Tag.MERGE)) {
                iter.remove();
                switch (valueNode.getNodeId()) {
                    case mapping:
                        MappingNode mn = (MappingNode) valueNode;
                        mergeNode(mn, false, key2index, values);
                        continue;
                    case sequence:
                        SequenceNode sn = (SequenceNode) valueNode;
                        List<Node> vals = sn.getValue();
                        for (Node subnode : vals) {
                            if (!(subnode instanceof MappingNode)) {
                                throw new ConstructorException("while constructing a mapping", node.getStartMark(), "expected a mapping for merging, but found " + subnode.getNodeId(), subnode.getStartMark());
                            }
                            MappingNode mnode = (MappingNode) subnode;
                            mergeNode(mnode, false, key2index, values);
                        }
                        continue;
                    default:
                        throw new ConstructorException("while constructing a mapping", node.getStartMark(), "expected a mapping or list of mappings for merging, but found " + valueNode.getNodeId(), valueNode.getStartMark());
                }
            } else {
                Object key = constructObject(keyNode);
                if (!key2index.containsKey(key)) {
                    values.add(nodeTuple);
                    key2index.put(key, Integer.valueOf(values.size() - 1));
                } else if (isPreffered) {
                    values.set(key2index.get(key).intValue(), nodeTuple);
                }
            }
        }
        return values;
    }

    @Override // org.yaml.snakeyaml.constructor.BaseConstructor
    public void constructMapping2ndStep(MappingNode node, Map<Object, Object> mapping) {
        flattenMapping(node);
        super.constructMapping2ndStep(node, mapping);
    }

    @Override // org.yaml.snakeyaml.constructor.BaseConstructor
    public void constructSet2ndStep(MappingNode node, Set<Object> set) {
        flattenMapping(node);
        super.constructSet2ndStep(node, set);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/constructor/SafeConstructor$ConstructYamlNull.class */
    public class ConstructYamlNull extends AbstractConstruct {
        public ConstructYamlNull() {
            SafeConstructor.this = this$0;
        }

        @Override // org.yaml.snakeyaml.constructor.Construct
        public Object construct(Node node) {
            SafeConstructor.this.constructScalar((ScalarNode) node);
            return null;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/constructor/SafeConstructor$ConstructYamlBool.class */
    public class ConstructYamlBool extends AbstractConstruct {
        public ConstructYamlBool() {
            SafeConstructor.this = this$0;
        }

        @Override // org.yaml.snakeyaml.constructor.Construct
        public Object construct(Node node) {
            String val = SafeConstructor.this.constructScalar((ScalarNode) node);
            return SafeConstructor.BOOL_VALUES.get(val.toLowerCase());
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/constructor/SafeConstructor$ConstructYamlInt.class */
    public class ConstructYamlInt extends AbstractConstruct {
        public ConstructYamlInt() {
            SafeConstructor.this = this$0;
        }

        @Override // org.yaml.snakeyaml.constructor.Construct
        public Object construct(Node node) {
            String value;
            int base;
            String value2 = SafeConstructor.this.constructScalar((ScalarNode) node).toString().replaceAll("_", "");
            int sign = 1;
            char first = value2.charAt(0);
            if (first == '-') {
                sign = -1;
                value2 = value2.substring(1);
            } else if (first == '+') {
                value2 = value2.substring(1);
            }
            if (CustomBooleanEditor.VALUE_0.equals(value2)) {
                return 0;
            }
            if (value2.startsWith("0b")) {
                value = value2.substring(2);
                base = 2;
            } else if (value2.startsWith("0x")) {
                value = value2.substring(2);
                base = 16;
            } else if (value2.startsWith(CustomBooleanEditor.VALUE_0)) {
                value = value2.substring(1);
                base = 8;
            } else if (value2.indexOf(58) == -1) {
                return SafeConstructor.this.createNumber(sign, value2, 10);
            } else {
                String[] digits = value2.split(":");
                int bes = 1;
                int val = 0;
                int j = digits.length;
                for (int i = 0; i < j; i++) {
                    val = (int) (val + (Long.parseLong(digits[(j - i) - 1]) * bes));
                    bes *= 60;
                }
                return SafeConstructor.this.createNumber(sign, String.valueOf(val), 10);
            }
            return SafeConstructor.this.createNumber(sign, value, base);
        }
    }

    public Number createNumber(int sign, String number, int radix) {
        Number result;
        if (sign < 0) {
            number = "-" + number;
        }
        try {
            result = Integer.valueOf(number, radix);
        } catch (NumberFormatException e) {
            try {
                result = Long.valueOf(number, radix);
            } catch (NumberFormatException e2) {
                result = new BigInteger(number, radix);
            }
        }
        return result;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/constructor/SafeConstructor$ConstructYamlFloat.class */
    public class ConstructYamlFloat extends AbstractConstruct {
        public ConstructYamlFloat() {
            SafeConstructor.this = this$0;
        }

        @Override // org.yaml.snakeyaml.constructor.Construct
        public Object construct(Node node) {
            String value = SafeConstructor.this.constructScalar((ScalarNode) node).toString().replaceAll("_", "");
            int sign = 1;
            char first = value.charAt(0);
            if (first == '-') {
                sign = -1;
                value = value.substring(1);
            } else if (first == '+') {
                value = value.substring(1);
            }
            String valLower = value.toLowerCase();
            if (".inf".equals(valLower)) {
                return Double.valueOf(sign == -1 ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
            } else if (".nan".equals(valLower)) {
                return Double.valueOf(Double.NaN);
            } else {
                if (value.indexOf(58) != -1) {
                    String[] digits = value.split(":");
                    int bes = 1;
                    double val = 0.0d;
                    int j = digits.length;
                    for (int i = 0; i < j; i++) {
                        val += Double.parseDouble(digits[(j - i) - 1]) * bes;
                        bes *= 60;
                    }
                    return Double.valueOf(sign * val);
                }
                Double d = Double.valueOf(value);
                return Double.valueOf(d.doubleValue() * sign);
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/constructor/SafeConstructor$ConstructYamlBinary.class */
    public class ConstructYamlBinary extends AbstractConstruct {
        public ConstructYamlBinary() {
            SafeConstructor.this = this$0;
        }

        @Override // org.yaml.snakeyaml.constructor.Construct
        public Object construct(Node node) {
            String noWhiteSpaces = SafeConstructor.this.constructScalar((ScalarNode) node).toString().replaceAll("\\s", "");
            byte[] decoded = Base64Coder.decode(noWhiteSpaces.toCharArray());
            return decoded;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/constructor/SafeConstructor$ConstructYamlTimestamp.class */
    public static class ConstructYamlTimestamp extends AbstractConstruct {
        private Calendar calendar;

        public Calendar getCalendar() {
            return this.calendar;
        }

        @Override // org.yaml.snakeyaml.constructor.Construct
        public Object construct(Node node) {
            TimeZone timeZone;
            ScalarNode scalar = (ScalarNode) node;
            String nodeValue = scalar.getValue();
            Matcher match = SafeConstructor.YMD_REGEXP.matcher(nodeValue);
            if (!match.matches()) {
                Matcher match2 = SafeConstructor.TIMESTAMP_REGEXP.matcher(nodeValue);
                if (!match2.matches()) {
                    throw new YAMLException("Unexpected timestamp: " + nodeValue);
                }
                String year_s = match2.group(1);
                String month_s = match2.group(2);
                String day_s = match2.group(3);
                String hour_s = match2.group(4);
                String min_s = match2.group(5);
                String seconds = match2.group(6);
                String millis = match2.group(7);
                if (millis != null) {
                    seconds = seconds + "." + millis;
                }
                double fractions = Double.parseDouble(seconds);
                int sec_s = (int) Math.round(Math.floor(fractions));
                int usec = (int) Math.round((fractions - sec_s) * 1000.0d);
                String timezoneh_s = match2.group(8);
                String timezonem_s = match2.group(9);
                if (timezoneh_s != null) {
                    String time = timezonem_s != null ? ":" + timezonem_s : "00";
                    timeZone = TimeZone.getTimeZone("GMT" + timezoneh_s + time);
                } else {
                    timeZone = TimeZone.getTimeZone("UTC");
                }
                this.calendar = Calendar.getInstance(timeZone);
                this.calendar.set(1, Integer.parseInt(year_s));
                this.calendar.set(2, Integer.parseInt(month_s) - 1);
                this.calendar.set(5, Integer.parseInt(day_s));
                this.calendar.set(11, Integer.parseInt(hour_s));
                this.calendar.set(12, Integer.parseInt(min_s));
                this.calendar.set(13, sec_s);
                this.calendar.set(14, usec);
                return this.calendar.getTime();
            }
            String year_s2 = match.group(1);
            String month_s2 = match.group(2);
            String day_s2 = match.group(3);
            this.calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            this.calendar.clear();
            this.calendar.set(1, Integer.parseInt(year_s2));
            this.calendar.set(2, Integer.parseInt(month_s2) - 1);
            this.calendar.set(5, Integer.parseInt(day_s2));
            return this.calendar.getTime();
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/constructor/SafeConstructor$ConstructYamlOmap.class */
    public class ConstructYamlOmap extends AbstractConstruct {
        public ConstructYamlOmap() {
            SafeConstructor.this = this$0;
        }

        @Override // org.yaml.snakeyaml.constructor.Construct
        public Object construct(Node node) {
            Map<Object, Object> omap = new LinkedHashMap<>();
            if (!(node instanceof SequenceNode)) {
                throw new ConstructorException("while constructing an ordered map", node.getStartMark(), "expected a sequence, but found " + node.getNodeId(), node.getStartMark());
            }
            SequenceNode snode = (SequenceNode) node;
            for (Node subnode : snode.getValue()) {
                if (!(subnode instanceof MappingNode)) {
                    throw new ConstructorException("while constructing an ordered map", node.getStartMark(), "expected a mapping of length 1, but found " + subnode.getNodeId(), subnode.getStartMark());
                }
                MappingNode mnode = (MappingNode) subnode;
                if (mnode.getValue().size() != 1) {
                    throw new ConstructorException("while constructing an ordered map", node.getStartMark(), "expected a single mapping item, but found " + mnode.getValue().size() + " items", mnode.getStartMark());
                }
                Node keyNode = mnode.getValue().get(0).getKeyNode();
                Node valueNode = mnode.getValue().get(0).getValueNode();
                Object key = SafeConstructor.this.constructObject(keyNode);
                Object value = SafeConstructor.this.constructObject(valueNode);
                omap.put(key, value);
            }
            return omap;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/constructor/SafeConstructor$ConstructYamlPairs.class */
    public class ConstructYamlPairs extends AbstractConstruct {
        public ConstructYamlPairs() {
            SafeConstructor.this = this$0;
        }

        @Override // org.yaml.snakeyaml.constructor.Construct
        public Object construct(Node node) {
            if (!(node instanceof SequenceNode)) {
                throw new ConstructorException("while constructing pairs", node.getStartMark(), "expected a sequence, but found " + node.getNodeId(), node.getStartMark());
            }
            SequenceNode snode = (SequenceNode) node;
            List<Object[]> pairs = new ArrayList<>(snode.getValue().size());
            for (Node subnode : snode.getValue()) {
                if (!(subnode instanceof MappingNode)) {
                    throw new ConstructorException("while constructingpairs", node.getStartMark(), "expected a mapping of length 1, but found " + subnode.getNodeId(), subnode.getStartMark());
                }
                MappingNode mnode = (MappingNode) subnode;
                if (mnode.getValue().size() != 1) {
                    throw new ConstructorException("while constructing pairs", node.getStartMark(), "expected a single mapping item, but found " + mnode.getValue().size() + " items", mnode.getStartMark());
                }
                Node keyNode = mnode.getValue().get(0).getKeyNode();
                Node valueNode = mnode.getValue().get(0).getValueNode();
                Object key = SafeConstructor.this.constructObject(keyNode);
                Object value = SafeConstructor.this.constructObject(valueNode);
                pairs.add(new Object[]{key, value});
            }
            return pairs;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/constructor/SafeConstructor$ConstructYamlSet.class */
    public class ConstructYamlSet implements Construct {
        public ConstructYamlSet() {
            SafeConstructor.this = this$0;
        }

        @Override // org.yaml.snakeyaml.constructor.Construct
        public Object construct(Node node) {
            if (node.isTwoStepsConstruction()) {
                return SafeConstructor.this.constructedObjects.containsKey(node) ? SafeConstructor.this.constructedObjects.get(node) : SafeConstructor.this.createDefaultSet(((MappingNode) node).getValue().size());
            }
            return SafeConstructor.this.constructSet((MappingNode) node);
        }

        @Override // org.yaml.snakeyaml.constructor.Construct
        public void construct2ndStep(Node node, Object object) {
            if (node.isTwoStepsConstruction()) {
                SafeConstructor.this.constructSet2ndStep((MappingNode) node, (Set) object);
                return;
            }
            throw new YAMLException("Unexpected recursive set structure. Node: " + node);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/constructor/SafeConstructor$ConstructYamlStr.class */
    public class ConstructYamlStr extends AbstractConstruct {
        public ConstructYamlStr() {
            SafeConstructor.this = this$0;
        }

        @Override // org.yaml.snakeyaml.constructor.Construct
        public Object construct(Node node) {
            return SafeConstructor.this.constructScalar((ScalarNode) node);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/constructor/SafeConstructor$ConstructYamlSeq.class */
    public class ConstructYamlSeq implements Construct {
        public ConstructYamlSeq() {
            SafeConstructor.this = this$0;
        }

        @Override // org.yaml.snakeyaml.constructor.Construct
        public Object construct(Node node) {
            SequenceNode seqNode = (SequenceNode) node;
            if (node.isTwoStepsConstruction()) {
                return SafeConstructor.this.newList(seqNode);
            }
            return SafeConstructor.this.constructSequence(seqNode);
        }

        @Override // org.yaml.snakeyaml.constructor.Construct
        public void construct2ndStep(Node node, Object data) {
            if (node.isTwoStepsConstruction()) {
                SafeConstructor.this.constructSequenceStep2((SequenceNode) node, (List) data);
                return;
            }
            throw new YAMLException("Unexpected recursive sequence structure. Node: " + node);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/constructor/SafeConstructor$ConstructYamlMap.class */
    public class ConstructYamlMap implements Construct {
        public ConstructYamlMap() {
            SafeConstructor.this = this$0;
        }

        @Override // org.yaml.snakeyaml.constructor.Construct
        public Object construct(Node node) {
            MappingNode mnode = (MappingNode) node;
            if (node.isTwoStepsConstruction()) {
                return SafeConstructor.this.createDefaultMap(mnode.getValue().size());
            }
            return SafeConstructor.this.constructMapping(mnode);
        }

        @Override // org.yaml.snakeyaml.constructor.Construct
        public void construct2ndStep(Node node, Object object) {
            if (node.isTwoStepsConstruction()) {
                SafeConstructor.this.constructMapping2ndStep((MappingNode) node, (Map) object);
                return;
            }
            throw new YAMLException("Unexpected recursive mapping structure. Node: " + node);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/constructor/SafeConstructor$ConstructUndefined.class */
    public static final class ConstructUndefined extends AbstractConstruct {
        @Override // org.yaml.snakeyaml.constructor.Construct
        public Object construct(Node node) {
            throw new ConstructorException(null, null, "could not determine a constructor for the tag " + node.getTag(), node.getStartMark());
        }
    }
}