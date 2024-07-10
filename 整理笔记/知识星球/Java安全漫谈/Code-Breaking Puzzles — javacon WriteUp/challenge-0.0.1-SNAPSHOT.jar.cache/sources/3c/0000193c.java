package org.springframework.boot.context.properties.source;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import org.springframework.beans.PropertyAccessor;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/source/ConfigurationPropertyName.class */
public final class ConfigurationPropertyName implements Comparable<ConfigurationPropertyName> {
    private static final String EMPTY_STRING = "";
    public static final ConfigurationPropertyName EMPTY = new ConfigurationPropertyName(Elements.EMPTY);
    private Elements elements;
    private final CharSequence[] uniformElements;
    private String string;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/source/ConfigurationPropertyName$ElementCharPredicate.class */
    public interface ElementCharPredicate {
        boolean test(char ch2, int index);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/source/ConfigurationPropertyName$Form.class */
    public enum Form {
        ORIGINAL,
        DASHED,
        UNIFORM
    }

    private ConfigurationPropertyName(Elements elements) {
        this.elements = elements;
        this.uniformElements = new CharSequence[elements.getSize()];
    }

    public boolean isEmpty() {
        return this.elements.getSize() == 0;
    }

    public boolean isLastElementIndexed() {
        int size = getNumberOfElements();
        return size > 0 && isIndexed(size - 1);
    }

    boolean isIndexed(int elementIndex) {
        return this.elements.getType(elementIndex).isIndexed();
    }

    public boolean isNumericIndex(int elementIndex) {
        return this.elements.getType(elementIndex) == ElementType.NUMERICALLY_INDEXED;
    }

    public String getLastElement(Form form) {
        int size = getNumberOfElements();
        return size != 0 ? getElement(size - 1, form) : "";
    }

    public String getElement(int elementIndex, Form form) {
        CharSequence element = this.elements.get(elementIndex);
        ElementType type = this.elements.getType(elementIndex);
        if (type.isIndexed()) {
            return element.toString();
        }
        if (form == Form.ORIGINAL) {
            if (type != ElementType.NON_UNIFORM) {
                return element.toString();
            }
            return convertToOriginalForm(element).toString();
        } else if (form == Form.DASHED) {
            if (type == ElementType.UNIFORM || type == ElementType.DASHED) {
                return element.toString();
            }
            return convertToDashedElement(element).toString();
        } else {
            CharSequence uniformElement = this.uniformElements[elementIndex];
            if (uniformElement == null) {
                uniformElement = type != ElementType.UNIFORM ? convertToUniformElement(element) : element;
                this.uniformElements[elementIndex] = uniformElement.toString();
            }
            return uniformElement.toString();
        }
    }

    private CharSequence convertToOriginalForm(CharSequence element) {
        return convertElement(element, false, ch2, i -> {
            return ch2 == '_' || ElementsParser.isValidChar(Character.toLowerCase(ch2), i);
        });
    }

    private CharSequence convertToDashedElement(CharSequence element) {
        return convertElement(element, true, ElementsParser::isValidChar);
    }

    private CharSequence convertToUniformElement(CharSequence element) {
        return convertElement(element, true, ch2, i -> {
            return ElementsParser.isAlphaNumeric(ch2);
        });
    }

    private CharSequence convertElement(CharSequence element, boolean lowercase, ElementCharPredicate filter) {
        StringBuilder result = new StringBuilder(element.length());
        for (int i = 0; i < element.length(); i++) {
            char ch2 = lowercase ? Character.toLowerCase(element.charAt(i)) : element.charAt(i);
            if (filter.test(ch2, i)) {
                result.append(ch2);
            }
        }
        return result;
    }

    public int getNumberOfElements() {
        return this.elements.getSize();
    }

    public ConfigurationPropertyName append(String elementValue) {
        if (elementValue == null) {
            return this;
        }
        Elements additionalElements = of(elementValue).elements;
        return new ConfigurationPropertyName(this.elements.append(additionalElements));
    }

    public ConfigurationPropertyName chop(int size) {
        if (size >= getNumberOfElements()) {
            return this;
        }
        return new ConfigurationPropertyName(this.elements.chop(size));
    }

    public boolean isParentOf(ConfigurationPropertyName name) {
        Assert.notNull(name, "Name must not be null");
        if (getNumberOfElements() != name.getNumberOfElements() - 1) {
            return false;
        }
        return isAncestorOf(name);
    }

    public boolean isAncestorOf(ConfigurationPropertyName name) {
        Assert.notNull(name, "Name must not be null");
        if (getNumberOfElements() >= name.getNumberOfElements()) {
            return false;
        }
        for (int i = 0; i < this.elements.getSize(); i++) {
            if (!elementEquals(this.elements, name.elements, i)) {
                return false;
            }
        }
        return true;
    }

    @Override // java.lang.Comparable
    public int compareTo(ConfigurationPropertyName other) {
        return compare(this, other);
    }

    private int compare(ConfigurationPropertyName n1, ConfigurationPropertyName n2) {
        ElementType type;
        String str;
        String str2;
        int result;
        int l1 = n1.getNumberOfElements();
        int l2 = n2.getNumberOfElements();
        int i1 = 0;
        int i2 = 0;
        do {
            if (i1 < l1 || i2 < l2) {
                if (i1 < l1) {
                    try {
                        type = n1.elements.getType(i1);
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    type = null;
                }
                ElementType type1 = type;
                ElementType type2 = i2 < l2 ? n2.elements.getType(i2) : null;
                if (i1 < l1) {
                    int i = i1;
                    i1++;
                    str = n1.getElement(i, Form.UNIFORM);
                } else {
                    str = null;
                }
                String e1 = str;
                if (i2 < l2) {
                    int i3 = i2;
                    i2++;
                    str2 = n2.getElement(i3, Form.UNIFORM);
                } else {
                    str2 = null;
                }
                String e2 = str2;
                result = compare(e1, type1, e2, type2);
            } else {
                return 0;
            }
        } while (result == 0);
        return result;
    }

    private int compare(String e1, ElementType type1, String e2, ElementType type2) {
        if (e1 == null) {
            return -1;
        }
        if (e2 == null) {
            return 1;
        }
        int result = Boolean.compare(type2.isIndexed(), type1.isIndexed());
        if (result != 0) {
            return result;
        }
        if (type1 == ElementType.NUMERICALLY_INDEXED && type2 == ElementType.NUMERICALLY_INDEXED) {
            long v1 = Long.parseLong(e1);
            long v2 = Long.parseLong(e2);
            return Long.compare(v1, v2);
        }
        return e1.compareTo(e2);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        ConfigurationPropertyName other = (ConfigurationPropertyName) obj;
        if (getNumberOfElements() != other.getNumberOfElements()) {
            return false;
        }
        if (this.elements.canShortcutWithSource(ElementType.UNIFORM) && other.elements.canShortcutWithSource(ElementType.UNIFORM)) {
            return toString().equals(other.toString());
        }
        for (int i = 0; i < this.elements.getSize(); i++) {
            if (!elementEquals(this.elements, other.elements, i)) {
                return false;
            }
        }
        return true;
    }

    /* JADX WARN: Removed duplicated region for block: B:35:0x00ad  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private boolean elementEquals(org.springframework.boot.context.properties.source.ConfigurationPropertyName.Elements r5, org.springframework.boot.context.properties.source.ConfigurationPropertyName.Elements r6, int r7) {
        /*
            r4 = this;
            r0 = r5
            r1 = r7
            int r0 = r0.getLength(r1)
            r8 = r0
            r0 = r6
            r1 = r7
            int r0 = r0.getLength(r1)
            r9 = r0
            r0 = r5
            r1 = r7
            org.springframework.boot.context.properties.source.ConfigurationPropertyName$ElementType r0 = r0.getType(r1)
            boolean r0 = r0.isIndexed()
            r10 = r0
            r0 = r6
            r1 = r7
            org.springframework.boot.context.properties.source.ConfigurationPropertyName$ElementType r0 = r0.getType(r1)
            boolean r0 = r0.isIndexed()
            r11 = r0
            r0 = 0
            r12 = r0
            r0 = 0
            r13 = r0
        L28:
            r0 = r12
            r1 = r8
            if (r0 >= r1) goto La6
            r0 = r13
            r1 = r9
            if (r0 < r1) goto L38
            r0 = 0
            return r0
        L38:
            r0 = r10
            if (r0 == 0) goto L47
            r0 = r5
            r1 = r7
            r2 = r12
            char r0 = r0.charAt(r1, r2)
            goto L51
        L47:
            r0 = r5
            r1 = r7
            r2 = r12
            char r0 = r0.charAt(r1, r2)
            char r0 = java.lang.Character.toLowerCase(r0)
        L51:
            r14 = r0
            r0 = r11
            if (r0 == 0) goto L62
            r0 = r6
            r1 = r7
            r2 = r13
            char r0 = r0.charAt(r1, r2)
            goto L6c
        L62:
            r0 = r6
            r1 = r7
            r2 = r13
            char r0 = r0.charAt(r1, r2)
            char r0 = java.lang.Character.toLowerCase(r0)
        L6c:
            r15 = r0
            r0 = r10
            if (r0 != 0) goto L81
            r0 = r14
            boolean r0 = org.springframework.boot.context.properties.source.ConfigurationPropertyName.ElementsParser.isAlphaNumeric(r0)
            if (r0 != 0) goto L81
            int r12 = r12 + 1
            goto La3
        L81:
            r0 = r11
            if (r0 != 0) goto L94
            r0 = r15
            boolean r0 = org.springframework.boot.context.properties.source.ConfigurationPropertyName.ElementsParser.isAlphaNumeric(r0)
            if (r0 != 0) goto L94
            int r13 = r13 + 1
            goto La3
        L94:
            r0 = r14
            r1 = r15
            if (r0 == r1) goto L9d
            r0 = 0
            return r0
        L9d:
            int r12 = r12 + 1
            int r13 = r13 + 1
        La3:
            goto L28
        La6:
            r0 = r13
            r1 = r9
            if (r0 >= r1) goto Lcb
            r0 = r6
            r1 = r7
            r2 = r13
            int r13 = r13 + 1
            char r0 = r0.charAt(r1, r2)
            r14 = r0
            r0 = r11
            if (r0 != 0) goto Lc6
            r0 = r14
            boolean r0 = org.springframework.boot.context.properties.source.ConfigurationPropertyName.ElementsParser.isAlphaNumeric(r0)
            if (r0 == 0) goto Lc8
        Lc6:
            r0 = 0
            return r0
        Lc8:
            goto La6
        Lcb:
            r0 = 1
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.boot.context.properties.source.ConfigurationPropertyName.elementEquals(org.springframework.boot.context.properties.source.ConfigurationPropertyName$Elements, org.springframework.boot.context.properties.source.ConfigurationPropertyName$Elements, int):boolean");
    }

    public int hashCode() {
        return 0;
    }

    public String toString() {
        if (this.string == null) {
            this.string = buildToString();
        }
        return this.string;
    }

    private String buildToString() {
        if (this.elements.canShortcutWithSource(ElementType.UNIFORM, ElementType.DASHED)) {
            return this.elements.getSource().toString();
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < getNumberOfElements(); i++) {
            boolean indexed = isIndexed(i);
            if (result.length() > 0 && !indexed) {
                result.append('.');
            }
            if (indexed) {
                result.append(PropertyAccessor.PROPERTY_KEY_PREFIX);
                result.append(getElement(i, Form.ORIGINAL));
                result.append("]");
            } else {
                result.append(getElement(i, Form.DASHED));
            }
        }
        return result.toString();
    }

    public static boolean isValid(CharSequence name) {
        return of(name, true) != null;
    }

    public static ConfigurationPropertyName of(CharSequence name) {
        return of(name, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ConfigurationPropertyName of(CharSequence name, boolean returnNullIfInvalid) {
        if (name == null) {
            Assert.isTrue(returnNullIfInvalid, "Name must not be null");
            return null;
        } else if (name.length() == 0) {
            return EMPTY;
        } else {
            if (name.charAt(0) == '.' || name.charAt(name.length() - 1) == '.') {
                if (returnNullIfInvalid) {
                    return null;
                }
                throw new InvalidConfigurationPropertyNameException(name, Collections.singletonList('.'));
            }
            Elements elements = new ElementsParser(name, '.').parse();
            for (int i = 0; i < elements.getSize(); i++) {
                if (elements.getType(i) == ElementType.NON_UNIFORM) {
                    if (returnNullIfInvalid) {
                        return null;
                    } else {
                        throw new InvalidConfigurationPropertyNameException(name, getInvalidChars(elements, i));
                    }
                }
            }
            return new ConfigurationPropertyName(elements);
        }
    }

    private static List<Character> getInvalidChars(Elements elements, int index) {
        List<Character> invalidChars = new ArrayList<>();
        for (int charIndex = 0; charIndex < elements.getLength(index); charIndex++) {
            char ch2 = elements.charAt(index, charIndex);
            if (!ElementsParser.isValidChar(ch2, charIndex)) {
                invalidChars.add(Character.valueOf(ch2));
            }
        }
        return invalidChars;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ConfigurationPropertyName adapt(CharSequence name, char separator) {
        return adapt(name, separator, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ConfigurationPropertyName adapt(CharSequence name, char separator, Function<CharSequence, CharSequence> elementValueProcessor) {
        Assert.notNull(name, "Name must not be null");
        if (name.length() == 0) {
            return EMPTY;
        }
        Elements elements = new ElementsParser(name, separator).parse(elementValueProcessor);
        if (elements.getSize() == 0) {
            return EMPTY;
        }
        return new ConfigurationPropertyName(elements);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/source/ConfigurationPropertyName$Elements.class */
    public static class Elements {
        private static final int[] NO_POSITION = new int[0];
        private static final ElementType[] NO_TYPE = new ElementType[0];
        public static final Elements EMPTY = new Elements("", 0, NO_POSITION, NO_POSITION, NO_TYPE, null);
        private final CharSequence source;
        private final int size;
        private final int[] start;
        private final int[] end;
        private final ElementType[] type;
        private final CharSequence[] resolved;

        Elements(CharSequence source, int size, int[] start, int[] end, ElementType[] type, CharSequence[] resolved) {
            this.source = source;
            this.size = size;
            this.start = start;
            this.end = end;
            this.type = type;
            this.resolved = resolved;
        }

        public Elements append(Elements additional) {
            Assert.isTrue(additional.getSize() == 1, () -> {
                return "Element value '" + ((Object) additional.getSource()) + "' must be a single item";
            });
            ElementType[] type = new ElementType[this.size + 1];
            System.arraycopy(this.type, 0, type, 0, this.size);
            type[this.size] = additional.type[0];
            CharSequence[] resolved = newResolved(this.size + 1);
            resolved[this.size] = additional.get(0);
            return new Elements(this.source, this.size + 1, this.start, this.end, type, resolved);
        }

        public Elements chop(int size) {
            CharSequence[] resolved = newResolved(size);
            return new Elements(this.source, size, this.start, this.end, this.type, resolved);
        }

        private CharSequence[] newResolved(int size) {
            CharSequence[] resolved = new CharSequence[size];
            if (this.resolved != null) {
                System.arraycopy(this.resolved, 0, resolved, 0, Math.min(size, this.size));
            }
            return resolved;
        }

        public int getSize() {
            return this.size;
        }

        public CharSequence get(int index) {
            if (this.resolved != null && this.resolved[index] != null) {
                return this.resolved[index];
            }
            int start = this.start[index];
            int end = this.end[index];
            return this.source.subSequence(start, end);
        }

        public int getLength(int index) {
            if (this.resolved != null && this.resolved[index] != null) {
                return this.resolved[index].length();
            }
            int start = this.start[index];
            int end = this.end[index];
            return end - start;
        }

        public char charAt(int index, int charIndex) {
            if (this.resolved != null && this.resolved[index] != null) {
                return this.resolved[index].charAt(charIndex);
            }
            int start = this.start[index];
            return this.source.charAt(start + charIndex);
        }

        public ElementType getType(int index) {
            return this.type[index];
        }

        public CharSequence getSource() {
            return this.source;
        }

        public boolean canShortcutWithSource(ElementType requiredType) {
            return canShortcutWithSource(requiredType, requiredType);
        }

        public boolean canShortcutWithSource(ElementType requiredType, ElementType alternativeType) {
            if (this.resolved != null) {
                return false;
            }
            for (int i = 0; i < this.size; i++) {
                ElementType type = this.type[i];
                if (type != requiredType && type != alternativeType) {
                    return false;
                }
                if (i > 0 && this.end[i - 1] + 1 != this.start[i]) {
                    return false;
                }
            }
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/source/ConfigurationPropertyName$ElementsParser.class */
    public static class ElementsParser {
        private static final int DEFAULT_CAPACITY = 6;
        private final CharSequence source;
        private final char separator;
        private int size;
        private int[] start;
        private int[] end;
        private ElementType[] type;
        private CharSequence[] resolved;

        ElementsParser(CharSequence source, char separator) {
            this(source, separator, 6);
        }

        ElementsParser(CharSequence source, char separator, int capacity) {
            this.source = source;
            this.separator = separator;
            this.start = new int[capacity];
            this.end = new int[capacity];
            this.type = new ElementType[capacity];
        }

        public Elements parse() {
            return parse(null);
        }

        public Elements parse(Function<CharSequence, CharSequence> valueProcessor) {
            int length = this.source.length();
            int openBracketCount = 0;
            int start = 0;
            ElementType type = ElementType.EMPTY;
            for (int i = 0; i < length; i++) {
                char ch2 = this.source.charAt(i);
                if (ch2 == '[') {
                    if (openBracketCount == 0) {
                        add(start, i, type, valueProcessor);
                        start = i + 1;
                        type = ElementType.NUMERICALLY_INDEXED;
                    }
                    openBracketCount++;
                } else if (ch2 == ']') {
                    openBracketCount--;
                    if (openBracketCount == 0) {
                        add(start, i, type, valueProcessor);
                        start = i + 1;
                        type = ElementType.EMPTY;
                    }
                } else if (!type.isIndexed() && ch2 == this.separator) {
                    add(start, i, type, valueProcessor);
                    start = i + 1;
                    type = ElementType.EMPTY;
                } else {
                    type = updateType(type, ch2, i - start);
                }
            }
            if (openBracketCount != 0) {
                type = ElementType.NON_UNIFORM;
            }
            add(start, length, type, valueProcessor);
            return new Elements(this.source, this.size, this.start, this.end, this.type, this.resolved);
        }

        private ElementType updateType(ElementType existingType, char ch2, int index) {
            if (existingType.isIndexed()) {
                if (existingType == ElementType.NUMERICALLY_INDEXED && !isNumeric(ch2)) {
                    return ElementType.INDEXED;
                }
                return existingType;
            } else if (existingType == ElementType.EMPTY && isValidChar(ch2, index)) {
                return index == 0 ? ElementType.UNIFORM : ElementType.NON_UNIFORM;
            } else if (existingType == ElementType.UNIFORM && ch2 == '-') {
                return ElementType.DASHED;
            } else {
                if (!isValidChar(ch2, index)) {
                    if (existingType == ElementType.EMPTY && !isValidChar(Character.toLowerCase(ch2), index)) {
                        return ElementType.EMPTY;
                    }
                    return ElementType.NON_UNIFORM;
                }
                return existingType;
            }
        }

        private void add(int start, int end, ElementType type, Function<CharSequence, CharSequence> valueProcessor) {
            if (end - start < 1 || type == ElementType.EMPTY) {
                return;
            }
            if (this.start.length <= end) {
                this.start = expand(this.start);
                this.end = expand(this.end);
                this.type = expand(this.type);
                this.resolved = expand(this.resolved);
            }
            if (valueProcessor != null) {
                if (this.resolved == null) {
                    this.resolved = new CharSequence[this.start.length];
                }
                CharSequence resolved = valueProcessor.apply(this.source.subSequence(start, end));
                Elements resolvedElements = new ElementsParser(resolved, '.').parse();
                Assert.state(resolvedElements.getSize() == 1, "Resolved element must not contain multiple elements");
                this.resolved[this.size] = resolvedElements.get(0);
                type = resolvedElements.getType(0);
            }
            this.start[this.size] = start;
            this.end[this.size] = end;
            this.type[this.size] = type;
            this.size++;
        }

        private int[] expand(int[] src) {
            int[] dest = new int[src.length + 6];
            System.arraycopy(src, 0, dest, 0, src.length);
            return dest;
        }

        private ElementType[] expand(ElementType[] src) {
            ElementType[] dest = new ElementType[src.length + 6];
            System.arraycopy(src, 0, dest, 0, src.length);
            return dest;
        }

        private CharSequence[] expand(CharSequence[] src) {
            if (src == null) {
                return null;
            }
            CharSequence[] dest = new CharSequence[src.length + 6];
            System.arraycopy(src, 0, dest, 0, src.length);
            return dest;
        }

        public static boolean isValidChar(char ch2, int index) {
            return isAlpha(ch2) || isNumeric(ch2) || (index != 0 && ch2 == '-');
        }

        public static boolean isAlphaNumeric(char ch2) {
            return isAlpha(ch2) || isNumeric(ch2);
        }

        private static boolean isAlpha(char ch2) {
            return ch2 >= 'a' && ch2 <= 'z';
        }

        private static boolean isNumeric(char ch2) {
            return ch2 >= '0' && ch2 <= '9';
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/source/ConfigurationPropertyName$ElementType.class */
    public enum ElementType {
        EMPTY(false),
        UNIFORM(false),
        DASHED(false),
        NON_UNIFORM(false),
        INDEXED(true),
        NUMERICALLY_INDEXED(true);
        
        private final boolean indexed;

        ElementType(boolean indexed) {
            this.indexed = indexed;
        }

        public boolean isIndexed() {
            return this.indexed;
        }
    }
}