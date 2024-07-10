package org.yaml.snakeyaml;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertySubstitute;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/TypeDescription.class */
public class TypeDescription {
    private static final Logger log = Logger.getLogger(TypeDescription.class.getPackage().getName());
    private final Class<? extends Object> type;
    private Class<?> impl;
    private Tag tag;
    private transient Set<Property> dumpProperties;
    private transient PropertyUtils propertyUtils;
    private transient boolean delegatesChecked;
    private Map<String, PropertySubstitute> properties;
    protected Set<String> excludes;
    protected String[] includes;
    protected BeanAccess beanAccess;

    public TypeDescription(Class<? extends Object> clazz, Tag tag) {
        this(clazz, tag, null);
    }

    public TypeDescription(Class<? extends Object> clazz, Tag tag, Class<?> impl) {
        this.properties = Collections.emptyMap();
        this.excludes = Collections.emptySet();
        this.includes = null;
        this.type = clazz;
        this.tag = tag;
        this.impl = impl;
        this.beanAccess = null;
    }

    public TypeDescription(Class<? extends Object> clazz, String tag) {
        this(clazz, new Tag(tag), null);
    }

    public TypeDescription(Class<? extends Object> clazz) {
        this(clazz, null, null);
    }

    public TypeDescription(Class<? extends Object> clazz, Class<?> impl) {
        this(clazz, null, impl);
    }

    public Tag getTag() {
        return this.tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public void setTag(String tag) {
        setTag(new Tag(tag));
    }

    public Class<? extends Object> getType() {
        return this.type;
    }

    @Deprecated
    public void putListPropertyType(String property, Class<? extends Object> type) {
        addPropertyParameters(property, type);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Deprecated
    public Class<? extends Object> getListPropertyType(String property) {
        Class<?>[] typeArguments;
        if (this.properties.containsKey(property) && (typeArguments = this.properties.get(property).getActualTypeArguments()) != null && typeArguments.length > 0) {
            return typeArguments[0];
        }
        return null;
    }

    @Deprecated
    public void putMapPropertyType(String property, Class<? extends Object> key, Class<? extends Object> value) {
        addPropertyParameters(property, key, value);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Deprecated
    public Class<? extends Object> getMapKeyType(String property) {
        Class<?>[] typeArguments;
        if (this.properties.containsKey(property) && (typeArguments = this.properties.get(property).getActualTypeArguments()) != null && typeArguments.length > 0) {
            return typeArguments[0];
        }
        return null;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Deprecated
    public Class<? extends Object> getMapValueType(String property) {
        Class<?>[] typeArguments;
        if (this.properties.containsKey(property) && (typeArguments = this.properties.get(property).getActualTypeArguments()) != null && typeArguments.length > 1) {
            return typeArguments[1];
        }
        return null;
    }

    public void addPropertyParameters(String pName, Class<?>... classes) {
        if (!this.properties.containsKey(pName)) {
            substituteProperty(pName, null, null, null, classes);
            return;
        }
        PropertySubstitute pr = this.properties.get(pName);
        pr.setActualTypeArguments(classes);
    }

    public String toString() {
        return "TypeDescription for " + getType() + " (tag='" + getTag() + "')";
    }

    private void checkDelegates() {
        Collection<PropertySubstitute> values = this.properties.values();
        for (PropertySubstitute p : values) {
            try {
                p.setDelegate(discoverProperty(p.getName()));
            } catch (YAMLException e) {
            }
        }
        this.delegatesChecked = true;
    }

    private Property discoverProperty(String name) {
        if (this.propertyUtils != null) {
            if (this.beanAccess == null) {
                return this.propertyUtils.getProperty(this.type, name);
            }
            return this.propertyUtils.getProperty(this.type, name, this.beanAccess);
        }
        return null;
    }

    public Property getProperty(String name) {
        if (!this.delegatesChecked) {
            checkDelegates();
        }
        return this.properties.containsKey(name) ? this.properties.get(name) : discoverProperty(name);
    }

    public void substituteProperty(String pName, Class<?> pType, String getter, String setter, Class<?>... argParams) {
        substituteProperty(new PropertySubstitute(pName, pType, getter, setter, argParams));
    }

    public void substituteProperty(PropertySubstitute substitute) {
        if (Collections.EMPTY_MAP == this.properties) {
            this.properties = new LinkedHashMap();
        }
        substitute.setTargetType(this.type);
        this.properties.put(substitute.getName(), substitute);
    }

    public void setPropertyUtils(PropertyUtils propertyUtils) {
        this.propertyUtils = propertyUtils;
    }

    public void setIncludes(String... propNames) {
        this.includes = (propNames == null || propNames.length <= 0) ? null : propNames;
    }

    public void setExcludes(String... propNames) {
        if (propNames != null && propNames.length > 0) {
            this.excludes = new HashSet();
            for (String name : propNames) {
                this.excludes.add(name);
            }
            return;
        }
        this.excludes = Collections.emptySet();
    }

    public Set<Property> getProperties() {
        Set<Property> properties;
        String[] strArr;
        if (this.dumpProperties != null) {
            return this.dumpProperties;
        }
        if (this.propertyUtils != null) {
            if (this.includes != null) {
                this.dumpProperties = new LinkedHashSet();
                for (String propertyName : this.includes) {
                    if (!this.excludes.contains(propertyName)) {
                        this.dumpProperties.add(getProperty(propertyName));
                    }
                }
                return this.dumpProperties;
            }
            if (this.beanAccess == null) {
                properties = this.propertyUtils.getProperties(this.type);
            } else {
                properties = this.propertyUtils.getProperties(this.type, this.beanAccess);
            }
            Set<Property> readableProps = properties;
            if (this.properties.isEmpty()) {
                if (this.excludes.isEmpty()) {
                    this.dumpProperties = readableProps;
                    return readableProps;
                }
                this.dumpProperties = new LinkedHashSet();
                for (Property property : readableProps) {
                    if (!this.excludes.contains(property.getName())) {
                        this.dumpProperties.add(property);
                    }
                }
                return this.dumpProperties;
            }
            if (!this.delegatesChecked) {
                checkDelegates();
            }
            this.dumpProperties = new LinkedHashSet();
            for (Property property2 : this.properties.values()) {
                if (!this.excludes.contains(property2.getName()) && property2.isReadable()) {
                    this.dumpProperties.add(property2);
                }
            }
            for (Property property3 : readableProps) {
                if (!this.excludes.contains(property3.getName())) {
                    this.dumpProperties.add(property3);
                }
            }
            return this.dumpProperties;
        }
        return null;
    }

    public boolean setupPropertyType(String key, Node valueNode) {
        return false;
    }

    public boolean setProperty(Object targetBean, String propertyName, Object value) throws Exception {
        return false;
    }

    public Object newInstance(Node node) {
        if (this.impl != null) {
            try {
                Constructor<?> c = this.impl.getDeclaredConstructor(new Class[0]);
                c.setAccessible(true);
                return c.newInstance(new Object[0]);
            } catch (Exception e) {
                log.fine(e.getLocalizedMessage());
                this.impl = null;
                return null;
            }
        }
        return null;
    }

    public Object newInstance(String propertyName, Node node) {
        return null;
    }

    public Object finalizeConstruction(Object obj) {
        return obj;
    }
}