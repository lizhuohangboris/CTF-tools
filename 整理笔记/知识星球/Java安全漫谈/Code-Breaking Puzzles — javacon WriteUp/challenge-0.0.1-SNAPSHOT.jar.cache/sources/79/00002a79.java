package org.yaml.snakeyaml.constructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/constructor/Constructor.class */
public class Constructor extends SafeConstructor {
    public Constructor() {
        this(Object.class);
    }

    public Constructor(Class<? extends Object> theRoot) {
        this(new TypeDescription(checkRoot(theRoot)));
    }

    private static Class<? extends Object> checkRoot(Class<? extends Object> theRoot) {
        if (theRoot == null) {
            throw new NullPointerException("Root class must be provided.");
        }
        return theRoot;
    }

    public Constructor(TypeDescription theRoot) {
        this(theRoot, null);
    }

    public Constructor(TypeDescription theRoot, Collection<TypeDescription> moreTDs) {
        if (theRoot == null) {
            throw new NullPointerException("Root type must be provided.");
        }
        this.yamlConstructors.put(null, new ConstructYamlObject());
        if (!Object.class.equals(theRoot.getType())) {
            this.rootTag = new Tag(theRoot.getType());
        }
        this.yamlClassConstructors.put(NodeId.scalar, new ConstructScalar());
        this.yamlClassConstructors.put(NodeId.mapping, new ConstructMapping());
        this.yamlClassConstructors.put(NodeId.sequence, new ConstructSequence());
        addTypeDescription(theRoot);
        if (moreTDs != null) {
            for (TypeDescription td : moreTDs) {
                addTypeDescription(td);
            }
        }
    }

    public Constructor(String theRoot) throws ClassNotFoundException {
        this((Class<? extends Object>) Class.forName(check(theRoot)));
    }

    private static final String check(String s) {
        if (s == null) {
            throw new NullPointerException("Root type must be provided.");
        }
        if (s.trim().length() == 0) {
            throw new YAMLException("Root type must be provided.");
        }
        return s;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/constructor/Constructor$ConstructMapping.class */
    protected class ConstructMapping implements Construct {
        /* JADX INFO: Access modifiers changed from: protected */
        public ConstructMapping() {
        }

        @Override // org.yaml.snakeyaml.constructor.Construct
        public Object construct(Node node) {
            MappingNode mnode = (MappingNode) node;
            if (Map.class.isAssignableFrom(node.getType())) {
                if (node.isTwoStepsConstruction()) {
                    return Constructor.this.newMap(mnode);
                }
                return Constructor.this.constructMapping(mnode);
            } else if (Collection.class.isAssignableFrom(node.getType())) {
                if (node.isTwoStepsConstruction()) {
                    return Constructor.this.newSet(mnode);
                }
                return Constructor.this.constructSet(mnode);
            } else {
                Object obj = Constructor.this.newInstance(mnode);
                if (node.isTwoStepsConstruction()) {
                    return obj;
                }
                return constructJavaBean2ndStep(mnode, obj);
            }
        }

        @Override // org.yaml.snakeyaml.constructor.Construct
        public void construct2ndStep(Node node, Object object) {
            if (Map.class.isAssignableFrom(node.getType())) {
                Constructor.this.constructMapping2ndStep((MappingNode) node, (Map) object);
            } else if (Set.class.isAssignableFrom(node.getType())) {
                Constructor.this.constructSet2ndStep((MappingNode) node, (Set) object);
            } else {
                constructJavaBean2ndStep((MappingNode) node, object);
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Code restructure failed: missing block: B:47:0x01cf, code lost:
            if (r19.getType() == java.lang.Float.class) goto L56;
         */
        /* JADX WARN: Multi-variable type inference failed */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public java.lang.Object constructJavaBean2ndStep(org.yaml.snakeyaml.nodes.MappingNode r9, java.lang.Object r10) {
            /*
                Method dump skipped, instructions count: 630
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: org.yaml.snakeyaml.constructor.Constructor.ConstructMapping.constructJavaBean2ndStep(org.yaml.snakeyaml.nodes.MappingNode, java.lang.Object):java.lang.Object");
        }

        private Object newInstance(TypeDescription memberDescription, String propertyName, Node node) {
            Object newInstance = memberDescription.newInstance(propertyName, node);
            if (newInstance != null) {
                Constructor.this.constructedObjects.put(node, newInstance);
                return Constructor.this.constructObjectNoCheck(node);
            }
            return Constructor.this.constructObject(node);
        }

        protected Property getProperty(Class<? extends Object> type, String name) {
            return Constructor.this.getPropertyUtils().getProperty(type, name);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/constructor/Constructor$ConstructYamlObject.class */
    protected class ConstructYamlObject implements Construct {
        protected ConstructYamlObject() {
        }

        /* JADX WARN: Multi-variable type inference failed */
        private Construct getConstructor(Node node) {
            Class<?> cl = Constructor.this.getClassForNode(node);
            node.setType(cl);
            Construct constructor = Constructor.this.yamlClassConstructors.get(node.getNodeId());
            return constructor;
        }

        @Override // org.yaml.snakeyaml.constructor.Construct
        public Object construct(Node node) {
            try {
                return getConstructor(node).construct(node);
            } catch (ConstructorException e) {
                throw e;
            } catch (Exception e2) {
                throw new ConstructorException(null, null, "Can't construct a java object for " + node.getTag() + "; exception=" + e2.getMessage(), node.getStartMark(), e2);
            }
        }

        @Override // org.yaml.snakeyaml.constructor.Construct
        public void construct2ndStep(Node node, Object object) {
            try {
                getConstructor(node).construct2ndStep(node, object);
            } catch (Exception e) {
                throw new ConstructorException(null, null, "Can't construct a second step for a java object for " + node.getTag() + "; exception=" + e.getMessage(), node.getStartMark(), e);
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/constructor/Constructor$ConstructScalar.class */
    protected class ConstructScalar extends AbstractConstruct {
        protected ConstructScalar() {
        }

        @Override // org.yaml.snakeyaml.constructor.Construct
        public Object construct(Node nnode) {
            Object result;
            Object argument;
            ScalarNode node = (ScalarNode) nnode;
            Class<?> type = node.getType();
            try {
                return Constructor.this.newInstance(type, node, false);
            } catch (InstantiationException e) {
                if (type.isPrimitive() || type == String.class || Number.class.isAssignableFrom(type) || type == Boolean.class || Date.class.isAssignableFrom(type) || type == Character.class || type == BigInteger.class || type == BigDecimal.class || Enum.class.isAssignableFrom(type) || Tag.BINARY.equals(node.getTag()) || Calendar.class.isAssignableFrom(type) || type == UUID.class) {
                    result = constructStandardJavaInstance(type, node);
                } else {
                    java.lang.reflect.Constructor<?>[] javaConstructors = type.getDeclaredConstructors();
                    int oneArgCount = 0;
                    java.lang.reflect.Constructor<?> javaConstructor = null;
                    for (java.lang.reflect.Constructor<?> c : javaConstructors) {
                        if (c.getParameterTypes().length == 1) {
                            oneArgCount++;
                            javaConstructor = c;
                        }
                    }
                    if (javaConstructor == null) {
                        try {
                            return Constructor.this.newInstance(type, node, false);
                        } catch (InstantiationException ie) {
                            throw new YAMLException("No single argument constructor found for " + type + " : " + ie.getMessage());
                        }
                    }
                    if (oneArgCount == 1) {
                        argument = constructStandardJavaInstance(javaConstructor.getParameterTypes()[0], node);
                    } else {
                        argument = Constructor.this.constructScalar(node);
                        try {
                            javaConstructor = type.getDeclaredConstructor(String.class);
                        } catch (Exception e2) {
                            throw new YAMLException("Can't construct a java object for scalar " + node.getTag() + "; No String constructor found. Exception=" + e2.getMessage(), e2);
                        }
                    }
                    try {
                        javaConstructor.setAccessible(true);
                        result = javaConstructor.newInstance(argument);
                    } catch (Exception e3) {
                        throw new ConstructorException(null, null, "Can't construct a java object for scalar " + node.getTag() + "; exception=" + e3.getMessage(), node.getStartMark(), e3);
                    }
                }
                return result;
            }
        }

        private Object constructStandardJavaInstance(Class type, ScalarNode node) {
            Object result;
            if (type == String.class) {
                Construct stringConstructor = Constructor.this.yamlConstructors.get(Tag.STR);
                result = stringConstructor.construct(node);
            } else if (type == Boolean.class || type == Boolean.TYPE) {
                Construct boolConstructor = Constructor.this.yamlConstructors.get(Tag.BOOL);
                result = boolConstructor.construct(node);
            } else if (type == Character.class || type == Character.TYPE) {
                Construct charConstructor = Constructor.this.yamlConstructors.get(Tag.STR);
                String ch2 = (String) charConstructor.construct(node);
                if (ch2.length() == 0) {
                    result = null;
                } else if (ch2.length() != 1) {
                    throw new YAMLException("Invalid node Character: '" + ch2 + "'; length: " + ch2.length());
                } else {
                    result = Character.valueOf(ch2.charAt(0));
                }
            } else if (Date.class.isAssignableFrom(type)) {
                Construct dateConstructor = Constructor.this.yamlConstructors.get(Tag.TIMESTAMP);
                Date date = (Date) dateConstructor.construct(node);
                if (type == Date.class) {
                    result = date;
                } else {
                    try {
                        java.lang.reflect.Constructor<?> constr = type.getConstructor(Long.TYPE);
                        result = constr.newInstance(Long.valueOf(date.getTime()));
                    } catch (RuntimeException e) {
                        throw e;
                    } catch (Exception e2) {
                        throw new YAMLException("Cannot construct: '" + type + "'");
                    }
                }
            } else if (type == Float.class || type == Double.class || type == Float.TYPE || type == Double.TYPE || type == BigDecimal.class) {
                if (type == BigDecimal.class) {
                    result = new BigDecimal(node.getValue());
                } else {
                    Construct doubleConstructor = Constructor.this.yamlConstructors.get(Tag.FLOAT);
                    result = doubleConstructor.construct(node);
                    if (type == Float.class || type == Float.TYPE) {
                        result = Float.valueOf(((Double) result).floatValue());
                    }
                }
            } else if (type == Byte.class || type == Short.class || type == Integer.class || type == Long.class || type == BigInteger.class || type == Byte.TYPE || type == Short.TYPE || type == Integer.TYPE || type == Long.TYPE) {
                Construct intConstructor = Constructor.this.yamlConstructors.get(Tag.INT);
                Object result2 = intConstructor.construct(node);
                if (type == Byte.class || type == Byte.TYPE) {
                    result = Byte.valueOf(result2.toString());
                } else if (type == Short.class || type == Short.TYPE) {
                    result = Short.valueOf(result2.toString());
                } else if (type == Integer.class || type == Integer.TYPE) {
                    result = Integer.valueOf(Integer.parseInt(result2.toString()));
                } else if (type == Long.class || type == Long.TYPE) {
                    result = Long.valueOf(result2.toString());
                } else {
                    result = new BigInteger(result2.toString());
                }
            } else if (Enum.class.isAssignableFrom(type)) {
                String enumValueName = node.getValue();
                try {
                    result = Enum.valueOf(type, enumValueName);
                } catch (Exception e3) {
                    throw new YAMLException("Unable to find enum value '" + enumValueName + "' for enum class: " + type.getName());
                }
            } else if (Calendar.class.isAssignableFrom(type)) {
                SafeConstructor.ConstructYamlTimestamp contr = new SafeConstructor.ConstructYamlTimestamp();
                contr.construct(node);
                result = contr.getCalendar();
            } else if (Number.class.isAssignableFrom(type)) {
                result = new SafeConstructor.ConstructYamlFloat().construct(node);
            } else if (UUID.class == type) {
                result = UUID.fromString(node.getValue());
            } else if (Constructor.this.yamlConstructors.containsKey(node.getTag())) {
                result = Constructor.this.yamlConstructors.get(node.getTag()).construct(node);
            } else {
                throw new YAMLException("Unsupported class: " + type);
            }
            return result;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/constructor/Constructor$ConstructSequence.class */
    protected class ConstructSequence implements Construct {
        protected ConstructSequence() {
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // org.yaml.snakeyaml.constructor.Construct
        public Object construct(Node node) {
            java.lang.reflect.Constructor<?>[] declaredConstructors;
            SequenceNode snode = (SequenceNode) node;
            if (Set.class.isAssignableFrom(node.getType())) {
                if (node.isTwoStepsConstruction()) {
                    throw new YAMLException("Set cannot be recursive.");
                }
                return Constructor.this.constructSet(snode);
            } else if (Collection.class.isAssignableFrom(node.getType())) {
                if (node.isTwoStepsConstruction()) {
                    return Constructor.this.newList(snode);
                }
                return Constructor.this.constructSequence(snode);
            } else if (node.getType().isArray()) {
                if (node.isTwoStepsConstruction()) {
                    return Constructor.this.createArray(node.getType(), snode.getValue().size());
                }
                return Constructor.this.constructArray(snode);
            } else {
                List<java.lang.reflect.Constructor<?>> possibleConstructors = new ArrayList<>(snode.getValue().size());
                for (java.lang.reflect.Constructor<?> constructor : node.getType().getDeclaredConstructors()) {
                    if (snode.getValue().size() == constructor.getParameterTypes().length) {
                        possibleConstructors.add(constructor);
                    }
                }
                if (!possibleConstructors.isEmpty()) {
                    if (possibleConstructors.size() == 1) {
                        Object[] argumentList = new Object[snode.getValue().size()];
                        java.lang.reflect.Constructor<?> c = possibleConstructors.get(0);
                        int index = 0;
                        for (Node argumentNode : snode.getValue()) {
                            Class<?> type = c.getParameterTypes()[index];
                            argumentNode.setType(type);
                            int i = index;
                            index++;
                            argumentList[i] = Constructor.this.constructObject(argumentNode);
                        }
                        try {
                            c.setAccessible(true);
                            return c.newInstance(argumentList);
                        } catch (Exception e) {
                            throw new YAMLException(e);
                        }
                    }
                    List<Object> argumentList2 = Constructor.this.constructSequence(snode);
                    Class<?>[] parameterTypes = new Class[argumentList2.size()];
                    int index2 = 0;
                    for (Object parameter : argumentList2) {
                        parameterTypes[index2] = parameter.getClass();
                        index2++;
                    }
                    for (java.lang.reflect.Constructor<?> c2 : possibleConstructors) {
                        Class<?>[] argTypes = c2.getParameterTypes();
                        boolean foundConstructor = true;
                        int i2 = 0;
                        while (true) {
                            if (i2 < argTypes.length) {
                                if (wrapIfPrimitive(argTypes[i2]).isAssignableFrom(parameterTypes[i2])) {
                                    i2++;
                                } else {
                                    foundConstructor = false;
                                    break;
                                }
                            } else {
                                break;
                            }
                        }
                        if (foundConstructor) {
                            try {
                                c2.setAccessible(true);
                                return c2.newInstance(argumentList2.toArray());
                            } catch (Exception e2) {
                                throw new YAMLException(e2);
                            }
                        }
                    }
                }
                throw new YAMLException("No suitable constructor with " + String.valueOf(snode.getValue().size()) + " arguments found for " + node.getType());
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        private final Class<? extends Object> wrapIfPrimitive(Class<?> clazz) {
            if (!clazz.isPrimitive()) {
                return clazz;
            }
            if (clazz == Integer.TYPE) {
                return Integer.class;
            }
            if (clazz == Float.TYPE) {
                return Float.class;
            }
            if (clazz == Double.TYPE) {
                return Double.class;
            }
            if (clazz == Boolean.TYPE) {
                return Boolean.class;
            }
            if (clazz == Long.TYPE) {
                return Long.class;
            }
            if (clazz == Character.TYPE) {
                return Character.class;
            }
            if (clazz == Short.TYPE) {
                return Short.class;
            }
            if (clazz == Byte.TYPE) {
                return Byte.class;
            }
            throw new YAMLException("Unexpected primitive " + clazz);
        }

        @Override // org.yaml.snakeyaml.constructor.Construct
        public void construct2ndStep(Node node, Object object) {
            SequenceNode snode = (SequenceNode) node;
            if (List.class.isAssignableFrom(node.getType())) {
                List<Object> list = (List) object;
                Constructor.this.constructSequenceStep2(snode, list);
            } else if (node.getType().isArray()) {
                Constructor.this.constructArrayStep2(snode, object);
            } else {
                throw new YAMLException("Immutable objects cannot be recursive.");
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected Class<?> getClassForNode(Node node) {
        Class<? extends Object> classForTag = this.typeTags.get(node.getTag());
        if (classForTag == null) {
            String name = node.getTag().getClassName();
            try {
                Class<?> cl = getClassForName(name);
                this.typeTags.put(node.getTag(), cl);
                return cl;
            } catch (ClassNotFoundException e) {
                throw new YAMLException("Class not found: " + name);
            }
        }
        return classForTag;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Class<?> getClassForName(String name) throws ClassNotFoundException {
        try {
            return Class.forName(name, true, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
            return Class.forName(name);
        }
    }
}