package org.hibernate.validator.internal.engine.path;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.validation.ElementKind;
import javax.validation.Path;
import org.hibernate.validator.internal.metadata.aggregated.ExecutableMetaData;
import org.hibernate.validator.internal.util.Contracts;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.internal.util.logging.Messages;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/path/PathImpl.class */
public final class PathImpl implements Path, Serializable {
    private static final long serialVersionUID = 7564511574909882392L;
    private static final String PROPERTY_PATH_SEPARATOR = ".";
    private static final String LEADING_PROPERTY_GROUP = "[^\\[\\.]+";
    private static final String OPTIONAL_INDEX_GROUP = "\\[(\\w*)\\]";
    private static final String REMAINING_PROPERTY_STRING = "\\.(.*)";
    private static final int PROPERTY_NAME_GROUP = 1;
    private static final int INDEXED_GROUP = 2;
    private static final int INDEX_GROUP = 3;
    private static final int REMAINING_STRING_GROUP = 5;
    private List<Path.Node> nodeList;
    private boolean nodeListRequiresCopy;
    private NodeImpl currentLeafNode;
    private int hashCode;
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private static final Pattern PATH_PATTERN = Pattern.compile("([^\\[\\.]+)(\\[(\\w*)\\])?(\\.(.*))*");

    public static PathImpl createPathFromString(String propertyPath) {
        Contracts.assertNotNull(propertyPath, Messages.MESSAGES.propertyPathCannotBeNull());
        if (propertyPath.length() == 0) {
            return createRootPath();
        }
        return parseProperty(propertyPath);
    }

    public static PathImpl createPathForExecutable(ExecutableMetaData executable) {
        Contracts.assertNotNull(executable, "A method is required to create a method return value path.");
        PathImpl path = createRootPath();
        if (executable.getKind() == ElementKind.CONSTRUCTOR) {
            path.addConstructorNode(executable.getName(), executable.getParameterTypes());
        } else {
            path.addMethodNode(executable.getName(), executable.getParameterTypes());
        }
        return path;
    }

    public static PathImpl createRootPath() {
        PathImpl path = new PathImpl();
        path.addBeanNode();
        return path;
    }

    public static PathImpl createCopy(PathImpl path) {
        return new PathImpl(path);
    }

    public boolean isRootPath() {
        return this.nodeList.size() == 1 && this.nodeList.get(0).getName() == null;
    }

    public PathImpl getPathWithoutLeafNode() {
        return new PathImpl(this.nodeList.subList(0, this.nodeList.size() - 1));
    }

    public NodeImpl addPropertyNode(String nodeName) {
        requiresWriteableNodeList();
        NodeImpl parent = this.currentLeafNode;
        this.currentLeafNode = NodeImpl.createPropertyNode(nodeName, parent);
        this.nodeList.add(this.currentLeafNode);
        resetHashCode();
        return this.currentLeafNode;
    }

    public NodeImpl addContainerElementNode(String nodeName) {
        requiresWriteableNodeList();
        NodeImpl parent = this.currentLeafNode;
        this.currentLeafNode = NodeImpl.createContainerElementNode(nodeName, parent);
        this.nodeList.add(this.currentLeafNode);
        resetHashCode();
        return this.currentLeafNode;
    }

    public NodeImpl addParameterNode(String nodeName, int index) {
        requiresWriteableNodeList();
        NodeImpl parent = this.currentLeafNode;
        this.currentLeafNode = NodeImpl.createParameterNode(nodeName, parent, index);
        this.nodeList.add(this.currentLeafNode);
        resetHashCode();
        return this.currentLeafNode;
    }

    public NodeImpl addCrossParameterNode() {
        requiresWriteableNodeList();
        NodeImpl parent = this.currentLeafNode;
        this.currentLeafNode = NodeImpl.createCrossParameterNode(parent);
        this.nodeList.add(this.currentLeafNode);
        resetHashCode();
        return this.currentLeafNode;
    }

    public NodeImpl addBeanNode() {
        requiresWriteableNodeList();
        NodeImpl parent = this.currentLeafNode;
        this.currentLeafNode = NodeImpl.createBeanNode(parent);
        this.nodeList.add(this.currentLeafNode);
        resetHashCode();
        return this.currentLeafNode;
    }

    public NodeImpl addReturnValueNode() {
        requiresWriteableNodeList();
        NodeImpl parent = this.currentLeafNode;
        this.currentLeafNode = NodeImpl.createReturnValue(parent);
        this.nodeList.add(this.currentLeafNode);
        resetHashCode();
        return this.currentLeafNode;
    }

    private NodeImpl addConstructorNode(String name, Class<?>[] parameterTypes) {
        requiresWriteableNodeList();
        NodeImpl parent = this.currentLeafNode;
        this.currentLeafNode = NodeImpl.createConstructorNode(name, parent, parameterTypes);
        this.nodeList.add(this.currentLeafNode);
        resetHashCode();
        return this.currentLeafNode;
    }

    private NodeImpl addMethodNode(String name, Class<?>[] parameterTypes) {
        requiresWriteableNodeList();
        NodeImpl parent = this.currentLeafNode;
        this.currentLeafNode = NodeImpl.createMethodNode(name, parent, parameterTypes);
        this.nodeList.add(this.currentLeafNode);
        resetHashCode();
        return this.currentLeafNode;
    }

    public NodeImpl makeLeafNodeIterable() {
        requiresWriteableNodeList();
        this.currentLeafNode = NodeImpl.makeIterable(this.currentLeafNode);
        this.nodeList.set(this.nodeList.size() - 1, this.currentLeafNode);
        resetHashCode();
        return this.currentLeafNode;
    }

    public NodeImpl makeLeafNodeIterableAndSetIndex(Integer index) {
        requiresWriteableNodeList();
        this.currentLeafNode = NodeImpl.makeIterableAndSetIndex(this.currentLeafNode, index);
        this.nodeList.set(this.nodeList.size() - 1, this.currentLeafNode);
        resetHashCode();
        return this.currentLeafNode;
    }

    public NodeImpl makeLeafNodeIterableAndSetMapKey(Object key) {
        requiresWriteableNodeList();
        this.currentLeafNode = NodeImpl.makeIterableAndSetMapKey(this.currentLeafNode, key);
        this.nodeList.set(this.nodeList.size() - 1, this.currentLeafNode);
        resetHashCode();
        return this.currentLeafNode;
    }

    public NodeImpl setLeafNodeValueIfRequired(Object value) {
        if (this.currentLeafNode.getKind() == ElementKind.PROPERTY || this.currentLeafNode.getKind() == ElementKind.CONTAINER_ELEMENT) {
            requiresWriteableNodeList();
            this.currentLeafNode = NodeImpl.setPropertyValue(this.currentLeafNode, value);
            this.nodeList.set(this.nodeList.size() - 1, this.currentLeafNode);
        }
        return this.currentLeafNode;
    }

    public NodeImpl setLeafNodeTypeParameter(Class<?> containerClass, Integer typeArgumentIndex) {
        requiresWriteableNodeList();
        this.currentLeafNode = NodeImpl.setTypeParameter(this.currentLeafNode, containerClass, typeArgumentIndex);
        this.nodeList.set(this.nodeList.size() - 1, this.currentLeafNode);
        resetHashCode();
        return this.currentLeafNode;
    }

    public void removeLeafNode() {
        if (!this.nodeList.isEmpty()) {
            requiresWriteableNodeList();
            this.nodeList.remove(this.nodeList.size() - 1);
            this.currentLeafNode = this.nodeList.isEmpty() ? null : (NodeImpl) this.nodeList.get(this.nodeList.size() - 1);
            resetHashCode();
        }
    }

    public NodeImpl getLeafNode() {
        return this.currentLeafNode;
    }

    @Override // java.lang.Iterable
    public Iterator<Path.Node> iterator() {
        if (this.nodeList.size() == 0) {
            return Collections.emptyList().iterator();
        }
        if (this.nodeList.size() == 1) {
            return this.nodeList.iterator();
        }
        return this.nodeList.subList(1, this.nodeList.size()).iterator();
    }

    public String asString() {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (int i = 1; i < this.nodeList.size(); i++) {
            NodeImpl nodeImpl = (NodeImpl) this.nodeList.get(i);
            String name = nodeImpl.asString();
            if (!name.isEmpty()) {
                if (!first) {
                    builder.append(".");
                }
                builder.append(nodeImpl.asString());
                first = false;
            }
        }
        return builder.toString();
    }

    private void requiresWriteableNodeList() {
        if (!this.nodeListRequiresCopy) {
            return;
        }
        List<Path.Node> newNodeList = new ArrayList<>(this.nodeList.size() + 1);
        newNodeList.addAll(this.nodeList);
        this.nodeList = newNodeList;
        this.nodeListRequiresCopy = false;
    }

    @Override // javax.validation.Path
    public String toString() {
        return asString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PathImpl other = (PathImpl) obj;
        if (this.nodeList == null) {
            if (other.nodeList != null) {
                return false;
            }
            return true;
        } else if (!this.nodeList.equals(other.nodeList)) {
            return false;
        } else {
            return true;
        }
    }

    public int hashCode() {
        if (this.hashCode == -1) {
            this.hashCode = buildHashCode();
        }
        return this.hashCode;
    }

    private int buildHashCode() {
        int result = (31 * 1) + (this.nodeList == null ? 0 : this.nodeList.hashCode());
        return result;
    }

    private PathImpl(PathImpl path) {
        this.nodeList = path.nodeList;
        this.currentLeafNode = path.currentLeafNode;
        this.hashCode = path.hashCode;
        this.nodeListRequiresCopy = true;
    }

    private PathImpl() {
        this.nodeList = new ArrayList(1);
        this.hashCode = -1;
        this.nodeListRequiresCopy = false;
    }

    private PathImpl(List<Path.Node> nodeList) {
        this.nodeList = nodeList;
        this.currentLeafNode = (NodeImpl) nodeList.get(nodeList.size() - 1);
        this.hashCode = -1;
        this.nodeListRequiresCopy = true;
    }

    private void resetHashCode() {
        this.hashCode = -1;
    }

    private static PathImpl parseProperty(String propertyName) {
        PathImpl path = createRootPath();
        String tmp = propertyName;
        do {
            Matcher matcher = PATH_PATTERN.matcher(tmp);
            if (matcher.matches()) {
                String value = matcher.group(1);
                if (!isValidJavaIdentifier(value)) {
                    throw LOG.getInvalidJavaIdentifierException(value);
                }
                path.addPropertyNode(value);
                if (matcher.group(2) != null) {
                    path.makeLeafNodeIterable();
                }
                String indexOrKey = matcher.group(3);
                if (indexOrKey != null && indexOrKey.length() > 0) {
                    try {
                        Integer i = Integer.valueOf(Integer.parseInt(indexOrKey));
                        path.makeLeafNodeIterableAndSetIndex(i);
                    } catch (NumberFormatException e) {
                        path.makeLeafNodeIterableAndSetMapKey(indexOrKey);
                    }
                }
                tmp = matcher.group(5);
            } else {
                throw LOG.getUnableToParsePropertyPathException(propertyName);
            }
        } while (tmp != null);
        if (path.getLeafNode().isIterable()) {
            path.addBeanNode();
        }
        return path;
    }

    private static boolean isValidJavaIdentifier(String identifier) {
        Contracts.assertNotNull(identifier, "identifier param cannot be null");
        if (identifier.length() == 0 || !Character.isJavaIdentifierStart((int) identifier.charAt(0))) {
            return false;
        }
        for (int i = 1; i < identifier.length(); i++) {
            if (!Character.isJavaIdentifierPart((int) identifier.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}