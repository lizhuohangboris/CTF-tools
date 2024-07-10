package org.springframework.expression.spel.ast;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypeConverter;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.spel.support.ReflectivePropertyAccessor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/ast/Indexer.class */
public class Indexer extends SpelNodeImpl {
    @Nullable
    private String cachedReadName;
    @Nullable
    private Class<?> cachedReadTargetType;
    @Nullable
    private PropertyAccessor cachedReadAccessor;
    @Nullable
    private String cachedWriteName;
    @Nullable
    private Class<?> cachedWriteTargetType;
    @Nullable
    private PropertyAccessor cachedWriteAccessor;
    @Nullable
    private IndexedType indexedType;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/ast/Indexer$IndexedType.class */
    public enum IndexedType {
        ARRAY,
        LIST,
        MAP,
        STRING,
        OBJECT
    }

    public Indexer(int pos, SpelNodeImpl expr) {
        super(pos, expr);
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
        return getValueRef(state).getValue();
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl, org.springframework.expression.spel.SpelNode
    public void setValue(ExpressionState state, @Nullable Object newValue) throws EvaluationException {
        getValueRef(state).setValue(newValue);
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl, org.springframework.expression.spel.SpelNode
    public boolean isWritable(ExpressionState expressionState) throws SpelEvaluationException {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public ValueRef getValueRef(ExpressionState state) throws EvaluationException {
        TypedValue indexValue;
        Object index;
        TypedValue context = state.getActiveContextObject();
        Object target = context.getValue();
        TypeDescriptor targetDescriptor = context.getTypeDescriptor();
        if ((target instanceof Map) && (this.children[0] instanceof PropertyOrFieldReference)) {
            PropertyOrFieldReference reference = (PropertyOrFieldReference) this.children[0];
            index = reference.getName();
            indexValue = new TypedValue(index);
        } else {
            try {
                state.pushActiveContextObject(state.getRootContextObject());
                indexValue = this.children[0].getValueInternal(state);
                index = indexValue.getValue();
                Assert.state(index != null, "No index");
                state.popActiveContextObject();
            } catch (Throwable th) {
                state.popActiveContextObject();
                throw th;
            }
        }
        if (target == null) {
            throw new SpelEvaluationException(getStartPosition(), SpelMessage.CANNOT_INDEX_INTO_NULL_VALUE, new Object[0]);
        }
        Assert.state(targetDescriptor != null, "No type descriptor");
        if (target instanceof Map) {
            Object key = index;
            if (targetDescriptor.getMapKeyTypeDescriptor() != null) {
                key = state.convertValue(key, targetDescriptor.getMapKeyTypeDescriptor());
            }
            this.indexedType = IndexedType.MAP;
            return new MapIndexingValueRef(state.getTypeConverter(), (Map) target, key, targetDescriptor);
        } else if (target.getClass().isArray() || (target instanceof Collection) || (target instanceof String)) {
            int idx = ((Integer) state.convertValue(index, TypeDescriptor.valueOf(Integer.class))).intValue();
            if (target.getClass().isArray()) {
                this.indexedType = IndexedType.ARRAY;
                return new ArrayIndexingValueRef(state.getTypeConverter(), target, idx, targetDescriptor);
            } else if (target instanceof Collection) {
                if (target instanceof List) {
                    this.indexedType = IndexedType.LIST;
                }
                return new CollectionIndexingValueRef((Collection) target, idx, targetDescriptor, state.getTypeConverter(), state.getConfiguration().isAutoGrowCollections(), state.getConfiguration().getMaximumAutoGrowSize());
            } else {
                this.indexedType = IndexedType.STRING;
                return new StringIndexingLValue((String) target, idx, targetDescriptor);
            }
        } else {
            TypeDescriptor valueType = indexValue.getTypeDescriptor();
            if (valueType != null && String.class == valueType.getType()) {
                this.indexedType = IndexedType.OBJECT;
                return new PropertyIndexingValueRef(target, (String) index, state.getEvaluationContext(), targetDescriptor);
            }
            throw new SpelEvaluationException(getStartPosition(), SpelMessage.INDEXING_NOT_SUPPORTED_FOR_TYPE, targetDescriptor);
        }
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public boolean isCompilable() {
        if (this.indexedType == IndexedType.ARRAY) {
            return this.exitTypeDescriptor != null;
        } else if (this.indexedType == IndexedType.LIST) {
            return this.children[0].isCompilable();
        } else {
            return this.indexedType == IndexedType.MAP ? (this.children[0] instanceof PropertyOrFieldReference) || this.children[0].isCompilable() : this.indexedType == IndexedType.OBJECT && this.cachedReadAccessor != null && (this.cachedReadAccessor instanceof ReflectivePropertyAccessor.OptimalPropertyAccessor) && (getChild(0) instanceof StringLiteral);
        }
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public void generateCode(MethodVisitor mv, CodeFlow cf) {
        int insn;
        String descriptor = cf.lastDescriptor();
        if (descriptor == null) {
            cf.loadTarget(mv);
        }
        if (this.indexedType == IndexedType.ARRAY) {
            if ("D".equals(this.exitTypeDescriptor)) {
                mv.visitTypeInsn(Opcodes.CHECKCAST, "[D");
                insn = 49;
            } else if ("F".equals(this.exitTypeDescriptor)) {
                mv.visitTypeInsn(Opcodes.CHECKCAST, "[F");
                insn = 48;
            } else if ("J".equals(this.exitTypeDescriptor)) {
                mv.visitTypeInsn(Opcodes.CHECKCAST, "[J");
                insn = 47;
            } else if ("I".equals(this.exitTypeDescriptor)) {
                mv.visitTypeInsn(Opcodes.CHECKCAST, "[I");
                insn = 46;
            } else if ("S".equals(this.exitTypeDescriptor)) {
                mv.visitTypeInsn(Opcodes.CHECKCAST, "[S");
                insn = 53;
            } else if ("B".equals(this.exitTypeDescriptor)) {
                mv.visitTypeInsn(Opcodes.CHECKCAST, "[B");
                insn = 51;
            } else if ("C".equals(this.exitTypeDescriptor)) {
                mv.visitTypeInsn(Opcodes.CHECKCAST, "[C");
                insn = 52;
            } else {
                mv.visitTypeInsn(Opcodes.CHECKCAST, org.springframework.beans.PropertyAccessor.PROPERTY_KEY_PREFIX + this.exitTypeDescriptor + (CodeFlow.isPrimitiveArray(this.exitTypeDescriptor) ? "" : ";"));
                insn = 50;
            }
            SpelNodeImpl index = this.children[0];
            cf.enterCompilationScope();
            index.generateCode(mv, cf);
            cf.exitCompilationScope();
            mv.visitInsn(insn);
        } else if (this.indexedType == IndexedType.LIST) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/util/List");
            cf.enterCompilationScope();
            this.children[0].generateCode(mv, cf);
            cf.exitCompilationScope();
            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", BeanUtil.PREFIX_GETTER_GET, "(I)Ljava/lang/Object;", true);
        } else if (this.indexedType == IndexedType.MAP) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/util/Map");
            if (this.children[0] instanceof PropertyOrFieldReference) {
                PropertyOrFieldReference reference = (PropertyOrFieldReference) this.children[0];
                String mapKeyName = reference.getName();
                mv.visitLdcInsn(mapKeyName);
            } else {
                cf.enterCompilationScope();
                this.children[0].generateCode(mv, cf);
                cf.exitCompilationScope();
            }
            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Map", BeanUtil.PREFIX_GETTER_GET, "(Ljava/lang/Object;)Ljava/lang/Object;", true);
        } else if (this.indexedType == IndexedType.OBJECT) {
            ReflectivePropertyAccessor.OptimalPropertyAccessor accessor = (ReflectivePropertyAccessor.OptimalPropertyAccessor) this.cachedReadAccessor;
            Assert.state(accessor != null, "No cached read accessor");
            Member member = accessor.member;
            boolean isStatic = Modifier.isStatic(member.getModifiers());
            String classDesc = member.getDeclaringClass().getName().replace('.', '/');
            if (!isStatic) {
                if (descriptor == null) {
                    cf.loadTarget(mv);
                }
                if (descriptor == null || !classDesc.equals(descriptor.substring(1))) {
                    mv.visitTypeInsn(Opcodes.CHECKCAST, classDesc);
                }
            }
            if (member instanceof Method) {
                mv.visitMethodInsn(isStatic ? 184 : Opcodes.INVOKEVIRTUAL, classDesc, member.getName(), CodeFlow.createSignatureDescriptor((Method) member), false);
            } else {
                mv.visitFieldInsn(isStatic ? Opcodes.GETSTATIC : Opcodes.GETFIELD, classDesc, member.getName(), CodeFlow.toJvmDescriptor(((Field) member).getType()));
            }
        }
        cf.pushDescriptor(this.exitTypeDescriptor);
    }

    @Override // org.springframework.expression.spel.SpelNode
    public String toStringAST() {
        StringBuilder sb = new StringBuilder(org.springframework.beans.PropertyAccessor.PROPERTY_KEY_PREFIX);
        for (int i = 0; i < getChildCount(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(getChild(i).toStringAST());
        }
        sb.append("]");
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setArrayElement(TypeConverter converter, Object ctx, int idx, @Nullable Object newValue, Class<?> arrayComponentType) throws EvaluationException {
        if (arrayComponentType == Boolean.TYPE) {
            boolean[] array = (boolean[]) ctx;
            checkAccess(array.length, idx);
            array[idx] = ((Boolean) convertValue(converter, newValue, Boolean.class)).booleanValue();
        } else if (arrayComponentType == Byte.TYPE) {
            byte[] array2 = (byte[]) ctx;
            checkAccess(array2.length, idx);
            array2[idx] = ((Byte) convertValue(converter, newValue, Byte.class)).byteValue();
        } else if (arrayComponentType == Character.TYPE) {
            char[] array3 = (char[]) ctx;
            checkAccess(array3.length, idx);
            array3[idx] = ((Character) convertValue(converter, newValue, Character.class)).charValue();
        } else if (arrayComponentType == Double.TYPE) {
            double[] array4 = (double[]) ctx;
            checkAccess(array4.length, idx);
            array4[idx] = ((Double) convertValue(converter, newValue, Double.class)).doubleValue();
        } else if (arrayComponentType == Float.TYPE) {
            float[] array5 = (float[]) ctx;
            checkAccess(array5.length, idx);
            array5[idx] = ((Float) convertValue(converter, newValue, Float.class)).floatValue();
        } else if (arrayComponentType == Integer.TYPE) {
            int[] array6 = (int[]) ctx;
            checkAccess(array6.length, idx);
            array6[idx] = ((Integer) convertValue(converter, newValue, Integer.class)).intValue();
        } else if (arrayComponentType == Long.TYPE) {
            long[] array7 = (long[]) ctx;
            checkAccess(array7.length, idx);
            array7[idx] = ((Long) convertValue(converter, newValue, Long.class)).longValue();
        } else if (arrayComponentType == Short.TYPE) {
            short[] array8 = (short[]) ctx;
            checkAccess(array8.length, idx);
            array8[idx] = ((Short) convertValue(converter, newValue, Short.class)).shortValue();
        } else {
            Object[] array9 = (Object[]) ctx;
            checkAccess(array9.length, idx);
            array9[idx] = convertValue(converter, newValue, arrayComponentType);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Object accessArrayElement(Object ctx, int idx) throws SpelEvaluationException {
        Class<?> arrayComponentType = ctx.getClass().getComponentType();
        if (arrayComponentType == Boolean.TYPE) {
            boolean[] array = (boolean[]) ctx;
            checkAccess(array.length, idx);
            this.exitTypeDescriptor = "Z";
            return Boolean.valueOf(array[idx]);
        } else if (arrayComponentType == Byte.TYPE) {
            byte[] array2 = (byte[]) ctx;
            checkAccess(array2.length, idx);
            this.exitTypeDescriptor = "B";
            return Byte.valueOf(array2[idx]);
        } else if (arrayComponentType == Character.TYPE) {
            char[] array3 = (char[]) ctx;
            checkAccess(array3.length, idx);
            this.exitTypeDescriptor = "C";
            return Character.valueOf(array3[idx]);
        } else if (arrayComponentType == Double.TYPE) {
            double[] array4 = (double[]) ctx;
            checkAccess(array4.length, idx);
            this.exitTypeDescriptor = "D";
            return Double.valueOf(array4[idx]);
        } else if (arrayComponentType == Float.TYPE) {
            float[] array5 = (float[]) ctx;
            checkAccess(array5.length, idx);
            this.exitTypeDescriptor = "F";
            return Float.valueOf(array5[idx]);
        } else if (arrayComponentType == Integer.TYPE) {
            int[] array6 = (int[]) ctx;
            checkAccess(array6.length, idx);
            this.exitTypeDescriptor = "I";
            return Integer.valueOf(array6[idx]);
        } else if (arrayComponentType == Long.TYPE) {
            long[] array7 = (long[]) ctx;
            checkAccess(array7.length, idx);
            this.exitTypeDescriptor = "J";
            return Long.valueOf(array7[idx]);
        } else if (arrayComponentType == Short.TYPE) {
            short[] array8 = (short[]) ctx;
            checkAccess(array8.length, idx);
            this.exitTypeDescriptor = "S";
            return Short.valueOf(array8[idx]);
        } else {
            Object[] array9 = (Object[]) ctx;
            checkAccess(array9.length, idx);
            Object retValue = array9[idx];
            this.exitTypeDescriptor = CodeFlow.toDescriptor(arrayComponentType);
            return retValue;
        }
    }

    private void checkAccess(int arrayLength, int index) throws SpelEvaluationException {
        if (index > arrayLength) {
            throw new SpelEvaluationException(getStartPosition(), SpelMessage.ARRAY_INDEX_OUT_OF_BOUNDS, Integer.valueOf(arrayLength), Integer.valueOf(index));
        }
    }

    private <T> T convertValue(TypeConverter converter, @Nullable Object value, Class<T> targetType) {
        T result = (T) converter.convertValue(value, TypeDescriptor.forObject(value), TypeDescriptor.valueOf(targetType));
        if (result == null) {
            throw new IllegalStateException("Null conversion result for index [" + value + "]");
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/ast/Indexer$ArrayIndexingValueRef.class */
    public class ArrayIndexingValueRef implements ValueRef {
        private final TypeConverter typeConverter;
        private final Object array;
        private final int index;
        private final TypeDescriptor typeDescriptor;

        ArrayIndexingValueRef(TypeConverter typeConverter, Object array, int index, TypeDescriptor typeDescriptor) {
            this.typeConverter = typeConverter;
            this.array = array;
            this.index = index;
            this.typeDescriptor = typeDescriptor;
        }

        @Override // org.springframework.expression.spel.ast.ValueRef
        public TypedValue getValue() {
            Object arrayElement = Indexer.this.accessArrayElement(this.array, this.index);
            return new TypedValue(arrayElement, this.typeDescriptor.elementTypeDescriptor(arrayElement));
        }

        @Override // org.springframework.expression.spel.ast.ValueRef
        public void setValue(@Nullable Object newValue) {
            TypeDescriptor elementType = this.typeDescriptor.getElementTypeDescriptor();
            Assert.state(elementType != null, "No element type");
            Indexer.this.setArrayElement(this.typeConverter, this.array, this.index, newValue, elementType.getType());
        }

        @Override // org.springframework.expression.spel.ast.ValueRef
        public boolean isWritable() {
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/ast/Indexer$MapIndexingValueRef.class */
    public class MapIndexingValueRef implements ValueRef {
        private final TypeConverter typeConverter;
        private final Map map;
        @Nullable
        private final Object key;
        private final TypeDescriptor mapEntryDescriptor;

        public MapIndexingValueRef(TypeConverter typeConverter, Map map, @Nullable Object key, TypeDescriptor mapEntryDescriptor) {
            this.typeConverter = typeConverter;
            this.map = map;
            this.key = key;
            this.mapEntryDescriptor = mapEntryDescriptor;
        }

        @Override // org.springframework.expression.spel.ast.ValueRef
        public TypedValue getValue() {
            Object value = this.map.get(this.key);
            Indexer.this.exitTypeDescriptor = CodeFlow.toDescriptor(Object.class);
            return new TypedValue(value, this.mapEntryDescriptor.getMapValueTypeDescriptor(value));
        }

        @Override // org.springframework.expression.spel.ast.ValueRef
        public void setValue(@Nullable Object newValue) {
            if (this.mapEntryDescriptor.getMapValueTypeDescriptor() != null) {
                newValue = this.typeConverter.convertValue(newValue, TypeDescriptor.forObject(newValue), this.mapEntryDescriptor.getMapValueTypeDescriptor());
            }
            this.map.put(this.key, newValue);
        }

        @Override // org.springframework.expression.spel.ast.ValueRef
        public boolean isWritable() {
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/ast/Indexer$PropertyIndexingValueRef.class */
    public class PropertyIndexingValueRef implements ValueRef {
        private final Object targetObject;
        private final String name;
        private final EvaluationContext evaluationContext;
        private final TypeDescriptor targetObjectTypeDescriptor;

        public PropertyIndexingValueRef(Object targetObject, String value, EvaluationContext evaluationContext, TypeDescriptor targetObjectTypeDescriptor) {
            this.targetObject = targetObject;
            this.name = value;
            this.evaluationContext = evaluationContext;
            this.targetObjectTypeDescriptor = targetObjectTypeDescriptor;
        }

        @Override // org.springframework.expression.spel.ast.ValueRef
        public TypedValue getValue() {
            Class<?> targetObjectRuntimeClass = Indexer.this.getObjectClass(this.targetObject);
            try {
                if (Indexer.this.cachedReadName != null && Indexer.this.cachedReadName.equals(this.name) && Indexer.this.cachedReadTargetType != null && Indexer.this.cachedReadTargetType.equals(targetObjectRuntimeClass)) {
                    PropertyAccessor accessor = Indexer.this.cachedReadAccessor;
                    Assert.state(accessor != null, "No cached read accessor");
                    return accessor.read(this.evaluationContext, this.targetObject, this.name);
                }
                List<PropertyAccessor> accessorsToTry = AstUtils.getPropertyAccessorsToTry(targetObjectRuntimeClass, this.evaluationContext.getPropertyAccessors());
                for (PropertyAccessor accessor2 : accessorsToTry) {
                    if (accessor2.canRead(this.evaluationContext, this.targetObject, this.name)) {
                        if (accessor2 instanceof ReflectivePropertyAccessor) {
                            accessor2 = ((ReflectivePropertyAccessor) accessor2).createOptimalAccessor(this.evaluationContext, this.targetObject, this.name);
                        }
                        Indexer.this.cachedReadAccessor = accessor2;
                        Indexer.this.cachedReadName = this.name;
                        Indexer.this.cachedReadTargetType = targetObjectRuntimeClass;
                        if (accessor2 instanceof ReflectivePropertyAccessor.OptimalPropertyAccessor) {
                            ReflectivePropertyAccessor.OptimalPropertyAccessor optimalAccessor = (ReflectivePropertyAccessor.OptimalPropertyAccessor) accessor2;
                            Member member = optimalAccessor.member;
                            Indexer.this.exitTypeDescriptor = CodeFlow.toDescriptor(member instanceof Method ? ((Method) member).getReturnType() : ((Field) member).getType());
                        }
                        return accessor2.read(this.evaluationContext, this.targetObject, this.name);
                    }
                }
                throw new SpelEvaluationException(Indexer.this.getStartPosition(), SpelMessage.INDEXING_NOT_SUPPORTED_FOR_TYPE, this.targetObjectTypeDescriptor.toString());
            } catch (AccessException ex) {
                throw new SpelEvaluationException(Indexer.this.getStartPosition(), ex, SpelMessage.INDEXING_NOT_SUPPORTED_FOR_TYPE, this.targetObjectTypeDescriptor.toString());
            }
        }

        @Override // org.springframework.expression.spel.ast.ValueRef
        public void setValue(@Nullable Object newValue) {
            Class<?> contextObjectClass = Indexer.this.getObjectClass(this.targetObject);
            try {
                if (Indexer.this.cachedWriteName != null && Indexer.this.cachedWriteName.equals(this.name) && Indexer.this.cachedWriteTargetType != null && Indexer.this.cachedWriteTargetType.equals(contextObjectClass)) {
                    PropertyAccessor accessor = Indexer.this.cachedWriteAccessor;
                    Assert.state(accessor != null, "No cached write accessor");
                    accessor.write(this.evaluationContext, this.targetObject, this.name, newValue);
                    return;
                }
                List<PropertyAccessor> accessorsToTry = AstUtils.getPropertyAccessorsToTry(contextObjectClass, this.evaluationContext.getPropertyAccessors());
                for (PropertyAccessor accessor2 : accessorsToTry) {
                    if (accessor2.canWrite(this.evaluationContext, this.targetObject, this.name)) {
                        Indexer.this.cachedWriteName = this.name;
                        Indexer.this.cachedWriteTargetType = contextObjectClass;
                        Indexer.this.cachedWriteAccessor = accessor2;
                        accessor2.write(this.evaluationContext, this.targetObject, this.name, newValue);
                        return;
                    }
                }
            } catch (AccessException ex) {
                throw new SpelEvaluationException(Indexer.this.getStartPosition(), ex, SpelMessage.EXCEPTION_DURING_PROPERTY_WRITE, this.name, ex.getMessage());
            }
        }

        @Override // org.springframework.expression.spel.ast.ValueRef
        public boolean isWritable() {
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/ast/Indexer$CollectionIndexingValueRef.class */
    public class CollectionIndexingValueRef implements ValueRef {
        private final Collection collection;
        private final int index;
        private final TypeDescriptor collectionEntryDescriptor;
        private final TypeConverter typeConverter;
        private final boolean growCollection;
        private final int maximumSize;

        public CollectionIndexingValueRef(Collection collection, int index, TypeDescriptor collectionEntryDescriptor, TypeConverter typeConverter, boolean growCollection, int maximumSize) {
            this.collection = collection;
            this.index = index;
            this.collectionEntryDescriptor = collectionEntryDescriptor;
            this.typeConverter = typeConverter;
            this.growCollection = growCollection;
            this.maximumSize = maximumSize;
        }

        @Override // org.springframework.expression.spel.ast.ValueRef
        public TypedValue getValue() {
            growCollectionIfNecessary();
            if (this.collection instanceof List) {
                Object o = ((List) this.collection).get(this.index);
                Indexer.this.exitTypeDescriptor = CodeFlow.toDescriptor(Object.class);
                return new TypedValue(o, this.collectionEntryDescriptor.elementTypeDescriptor(o));
            }
            int pos = 0;
            for (Object o2 : this.collection) {
                if (pos == this.index) {
                    return new TypedValue(o2, this.collectionEntryDescriptor.elementTypeDescriptor(o2));
                }
                pos++;
            }
            throw new IllegalStateException("Failed to find indexed element " + this.index + ": " + this.collection);
        }

        @Override // org.springframework.expression.spel.ast.ValueRef
        public void setValue(@Nullable Object newValue) {
            growCollectionIfNecessary();
            if (this.collection instanceof List) {
                List list = (List) this.collection;
                if (this.collectionEntryDescriptor.getElementTypeDescriptor() != null) {
                    newValue = this.typeConverter.convertValue(newValue, TypeDescriptor.forObject(newValue), this.collectionEntryDescriptor.getElementTypeDescriptor());
                }
                list.set(this.index, newValue);
                return;
            }
            throw new SpelEvaluationException(Indexer.this.getStartPosition(), SpelMessage.INDEXING_NOT_SUPPORTED_FOR_TYPE, this.collectionEntryDescriptor.toString());
        }

        private void growCollectionIfNecessary() {
            if (this.index >= this.collection.size()) {
                if (!this.growCollection) {
                    throw new SpelEvaluationException(Indexer.this.getStartPosition(), SpelMessage.COLLECTION_INDEX_OUT_OF_BOUNDS, Integer.valueOf(this.collection.size()), Integer.valueOf(this.index));
                }
                if (this.index >= this.maximumSize) {
                    throw new SpelEvaluationException(Indexer.this.getStartPosition(), SpelMessage.UNABLE_TO_GROW_COLLECTION, new Object[0]);
                }
                if (this.collectionEntryDescriptor.getElementTypeDescriptor() == null) {
                    throw new SpelEvaluationException(Indexer.this.getStartPosition(), SpelMessage.UNABLE_TO_GROW_COLLECTION_UNKNOWN_ELEMENT_TYPE, new Object[0]);
                }
                TypeDescriptor elementType = this.collectionEntryDescriptor.getElementTypeDescriptor();
                try {
                    Constructor<?> ctor = ReflectionUtils.accessibleConstructor(elementType.getType(), new Class[0]);
                    for (int newElements = this.index - this.collection.size(); newElements >= 0; newElements--) {
                        this.collection.add(ctor.newInstance(new Object[0]));
                    }
                } catch (Throwable ex) {
                    throw new SpelEvaluationException(Indexer.this.getStartPosition(), ex, SpelMessage.UNABLE_TO_GROW_COLLECTION, new Object[0]);
                }
            }
        }

        @Override // org.springframework.expression.spel.ast.ValueRef
        public boolean isWritable() {
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/ast/Indexer$StringIndexingLValue.class */
    public class StringIndexingLValue implements ValueRef {
        private final String target;
        private final int index;
        private final TypeDescriptor typeDescriptor;

        public StringIndexingLValue(String target, int index, TypeDescriptor typeDescriptor) {
            this.target = target;
            this.index = index;
            this.typeDescriptor = typeDescriptor;
        }

        @Override // org.springframework.expression.spel.ast.ValueRef
        public TypedValue getValue() {
            if (this.index >= this.target.length()) {
                throw new SpelEvaluationException(Indexer.this.getStartPosition(), SpelMessage.STRING_INDEX_OUT_OF_BOUNDS, Integer.valueOf(this.target.length()), Integer.valueOf(this.index));
            }
            return new TypedValue(String.valueOf(this.target.charAt(this.index)));
        }

        @Override // org.springframework.expression.spel.ast.ValueRef
        public void setValue(@Nullable Object newValue) {
            throw new SpelEvaluationException(Indexer.this.getStartPosition(), SpelMessage.INDEXING_NOT_SUPPORTED_FOR_TYPE, this.typeDescriptor.toString());
        }

        @Override // org.springframework.expression.spel.ast.ValueRef
        public boolean isWritable() {
            return true;
        }
    }
}