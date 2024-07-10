package com.fasterxml.jackson.databind.ext;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.Serializable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/ext/OptionalHandlerFactory.class */
public class OptionalHandlerFactory implements Serializable {
    private static final long serialVersionUID = 1;
    private static final String PACKAGE_PREFIX_JAVAX_XML = "javax.xml.";
    private static final String SERIALIZERS_FOR_JAVAX_XML = "com.fasterxml.jackson.databind.ext.CoreXMLSerializers";
    private static final String DESERIALIZERS_FOR_JAVAX_XML = "com.fasterxml.jackson.databind.ext.CoreXMLDeserializers";
    private static final String SERIALIZER_FOR_DOM_NODE = "com.fasterxml.jackson.databind.ext.DOMSerializer";
    private static final String DESERIALIZER_FOR_DOM_DOCUMENT = "com.fasterxml.jackson.databind.ext.DOMDeserializer$DocumentDeserializer";
    private static final String DESERIALIZER_FOR_DOM_NODE = "com.fasterxml.jackson.databind.ext.DOMDeserializer$NodeDeserializer";
    private static final Class<?> CLASS_DOM_NODE;
    private static final Class<?> CLASS_DOM_DOCUMENT;
    private static final Java7Support _jdk7Helper;
    public static final OptionalHandlerFactory instance;

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Unreachable block: B:4:0x000f
        	at jadx.core.dex.visitors.blocks.BlockProcessor.checkForUnreachableBlocks(BlockProcessor.java:81)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.processBlocksTree(BlockProcessor.java:47)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.visit(BlockProcessor.java:39)
        */
    static {
        /*
            r0 = 0
            r4 = r0
            r0 = 0
            r5 = r0
            java.lang.Class<org.w3c.dom.Node> r0 = org.w3c.dom.Node.class
            r5 = r0
            java.lang.Class<org.w3c.dom.Document> r0 = org.w3c.dom.Document.class
            r4 = r0
            goto L21
        Lf:
            r6 = move-exception
            java.lang.Class<com.fasterxml.jackson.databind.ext.OptionalHandlerFactory> r0 = com.fasterxml.jackson.databind.ext.OptionalHandlerFactory.class
            java.lang.String r0 = r0.getName()
            java.util.logging.Logger r0 = java.util.logging.Logger.getLogger(r0)
            java.util.logging.Level r1 = java.util.logging.Level.INFO
            java.lang.String r2 = "Could not load DOM `Node` and/or `Document` classes: no DOM support"
            r0.log(r1, r2)
        L21:
            r0 = r5
            com.fasterxml.jackson.databind.ext.OptionalHandlerFactory.CLASS_DOM_NODE = r0
            r0 = r4
            com.fasterxml.jackson.databind.ext.OptionalHandlerFactory.CLASS_DOM_DOCUMENT = r0
            r0 = 0
            r4 = r0
            com.fasterxml.jackson.databind.ext.Java7Support r0 = com.fasterxml.jackson.databind.ext.Java7Support.instance()     // Catch: java.lang.Throwable -> L32
            r4 = r0
            goto L33
        L32:
            r5 = move-exception
        L33:
            r0 = r4
            com.fasterxml.jackson.databind.ext.OptionalHandlerFactory._jdk7Helper = r0
            com.fasterxml.jackson.databind.ext.OptionalHandlerFactory r0 = new com.fasterxml.jackson.databind.ext.OptionalHandlerFactory
            r1 = r0
            r1.<init>()
            com.fasterxml.jackson.databind.ext.OptionalHandlerFactory.instance = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.databind.ext.OptionalHandlerFactory.m231clinit():void");
    }

    protected OptionalHandlerFactory() {
    }

    public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc) {
        Object ob;
        JsonSerializer<?> ser;
        Class<?> rawType = type.getRawClass();
        if (_jdk7Helper != null && (ser = _jdk7Helper.getSerializerForJavaNioFilePath(rawType)) != null) {
            return ser;
        }
        if (CLASS_DOM_NODE != null && CLASS_DOM_NODE.isAssignableFrom(rawType)) {
            return (JsonSerializer) instantiate(SERIALIZER_FOR_DOM_NODE);
        }
        String className = rawType.getName();
        if ((!className.startsWith(PACKAGE_PREFIX_JAVAX_XML) && !hasSuperClassStartingWith(rawType, PACKAGE_PREFIX_JAVAX_XML)) || (ob = instantiate(SERIALIZERS_FOR_JAVAX_XML)) == null) {
            return null;
        }
        return ((Serializers) ob).findSerializer(config, type, beanDesc);
    }

    public JsonDeserializer<?> findDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
        Object ob;
        JsonDeserializer<?> deser;
        Class<?> rawType = type.getRawClass();
        if (_jdk7Helper != null && (deser = _jdk7Helper.getDeserializerForJavaNioFilePath(rawType)) != null) {
            return deser;
        }
        if (CLASS_DOM_NODE != null && CLASS_DOM_NODE.isAssignableFrom(rawType)) {
            return (JsonDeserializer) instantiate(DESERIALIZER_FOR_DOM_NODE);
        }
        if (CLASS_DOM_DOCUMENT != null && CLASS_DOM_DOCUMENT.isAssignableFrom(rawType)) {
            return (JsonDeserializer) instantiate(DESERIALIZER_FOR_DOM_DOCUMENT);
        }
        String className = rawType.getName();
        if ((!className.startsWith(PACKAGE_PREFIX_JAVAX_XML) && !hasSuperClassStartingWith(rawType, PACKAGE_PREFIX_JAVAX_XML)) || (ob = instantiate(DESERIALIZERS_FOR_JAVAX_XML)) == null) {
            return null;
        }
        return ((Deserializers) ob).findBeanDeserializer(type, config, beanDesc);
    }

    private Object instantiate(String className) {
        try {
            return ClassUtil.createInstance(Class.forName(className), false);
        } catch (Exception | LinkageError e) {
            return null;
        }
    }

    private boolean hasSuperClassStartingWith(Class<?> rawType, String prefix) {
        Class<?> superclass = rawType.getSuperclass();
        while (true) {
            Class<?> supertype = superclass;
            if (supertype == null || supertype == Object.class) {
                return false;
            }
            if (!supertype.getName().startsWith(prefix)) {
                superclass = supertype.getSuperclass();
            } else {
                return true;
            }
        }
    }
}