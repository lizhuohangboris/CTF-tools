package org.thymeleaf.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.thymeleaf.standard.processor.StandardXmlNsTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.TextUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/ElementNames.class */
public class ElementNames {
    private static final ElementNamesRepository htmlElementNamesRepository = new ElementNamesRepository(TemplateMode.HTML);
    private static final ElementNamesRepository xmlElementNamesRepository = new ElementNamesRepository(TemplateMode.XML);
    private static final ElementNamesRepository textElementNamesRepository = new ElementNamesRepository(TemplateMode.TEXT);

    /* JADX INFO: Access modifiers changed from: private */
    public static TextElementName buildTextElementName(char[] elementNameBuffer, int elementNameOffset, int elementNameLen) {
        if (elementNameBuffer == null) {
            throw new IllegalArgumentException("Element name buffer cannot be null or empty");
        }
        if (elementNameOffset < 0 || elementNameLen < 0) {
            throw new IllegalArgumentException("Element name offset and len must be equal or greater than zero");
        }
        int i = elementNameOffset;
        int n = elementNameLen;
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                int i3 = i;
                i++;
                char c = elementNameBuffer[i3];
                if (c == ':' && c == ':') {
                    if (i == elementNameOffset + 1) {
                        return TextElementName.forName(null, new String(elementNameBuffer, elementNameOffset, elementNameLen));
                    }
                    return TextElementName.forName(new String(elementNameBuffer, elementNameOffset, i - (elementNameOffset + 1)), new String(elementNameBuffer, i, (elementNameOffset + elementNameLen) - i));
                }
            } else {
                return TextElementName.forName(null, new String(elementNameBuffer, elementNameOffset, elementNameLen));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static XMLElementName buildXMLElementName(char[] elementNameBuffer, int elementNameOffset, int elementNameLen) {
        if (elementNameBuffer == null || elementNameLen == 0) {
            throw new IllegalArgumentException("Element name buffer cannot be null or empty");
        }
        if (elementNameOffset < 0 || elementNameLen < 0) {
            throw new IllegalArgumentException("Element name offset and len must be equal or greater than zero");
        }
        int i = elementNameOffset;
        int n = elementNameLen;
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                int i3 = i;
                i++;
                char c = elementNameBuffer[i3];
                if (c == ':' && c == ':') {
                    if (i == elementNameOffset + 1) {
                        return XMLElementName.forName(null, new String(elementNameBuffer, elementNameOffset, elementNameLen));
                    }
                    return XMLElementName.forName(new String(elementNameBuffer, elementNameOffset, i - (elementNameOffset + 1)), new String(elementNameBuffer, i, (elementNameOffset + elementNameLen) - i));
                }
            } else {
                return XMLElementName.forName(null, new String(elementNameBuffer, elementNameOffset, elementNameLen));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static HTMLElementName buildHTMLElementName(char[] elementNameBuffer, int elementNameOffset, int elementNameLen) {
        if (elementNameBuffer == null || elementNameLen == 0) {
            throw new IllegalArgumentException("Element name buffer cannot be null or empty");
        }
        if (elementNameOffset < 0 || elementNameLen < 0) {
            throw new IllegalArgumentException("Element name offset and len must be equal or greater than zero");
        }
        int i = elementNameOffset;
        int n = elementNameLen;
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                int i3 = i;
                i++;
                char c = elementNameBuffer[i3];
                if (c == ':' || c == '-') {
                    if (c == ':') {
                        if (i == elementNameOffset + 1) {
                            return HTMLElementName.forName(null, new String(elementNameBuffer, elementNameOffset, elementNameLen));
                        }
                        if (TextUtils.equals(TemplateMode.HTML.isCaseSensitive(), "xml:", 0, 4, elementNameBuffer, elementNameOffset, i - elementNameOffset) || TextUtils.equals(TemplateMode.HTML.isCaseSensitive(), StandardXmlNsTagProcessor.ATTR_NAME_PREFIX, 0, 6, elementNameBuffer, elementNameOffset, i - elementNameOffset)) {
                            return HTMLElementName.forName(null, new String(elementNameBuffer, elementNameOffset, elementNameLen));
                        }
                        return HTMLElementName.forName(new String(elementNameBuffer, elementNameOffset, i - (elementNameOffset + 1)), new String(elementNameBuffer, i, (elementNameOffset + elementNameLen) - i));
                    } else if (c == '-') {
                        if (i == elementNameOffset + 1) {
                            return HTMLElementName.forName(null, new String(elementNameBuffer, elementNameOffset, elementNameLen));
                        }
                        return HTMLElementName.forName(new String(elementNameBuffer, elementNameOffset, i - (elementNameOffset + 1)), new String(elementNameBuffer, i, (elementNameOffset + elementNameLen) - i));
                    }
                }
            } else {
                return HTMLElementName.forName(null, new String(elementNameBuffer, elementNameOffset, elementNameLen));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static TextElementName buildTextElementName(String elementName) {
        if (elementName == null) {
            throw new IllegalArgumentException("Element name cannot be null");
        }
        int i = 0;
        int n = elementName.length();
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                int i3 = i;
                i++;
                char c = elementName.charAt(i3);
                if (c == ':' && c == ':') {
                    if (i == 1) {
                        return TextElementName.forName(null, elementName);
                    }
                    return TextElementName.forName(elementName.substring(0, i - 1), elementName.substring(i, elementName.length()));
                }
            } else {
                return TextElementName.forName(null, elementName);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static XMLElementName buildXMLElementName(String elementName) {
        if (elementName == null || elementName.length() == 0) {
            throw new IllegalArgumentException("Element name cannot be null or empty");
        }
        int i = 0;
        int n = elementName.length();
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                int i3 = i;
                i++;
                char c = elementName.charAt(i3);
                if (c == ':' && c == ':') {
                    if (i == 1) {
                        return XMLElementName.forName(null, elementName);
                    }
                    return XMLElementName.forName(elementName.substring(0, i - 1), elementName.substring(i, elementName.length()));
                }
            } else {
                return XMLElementName.forName(null, elementName);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static HTMLElementName buildHTMLElementName(String elementName) {
        if (elementName == null || elementName.length() == 0) {
            throw new IllegalArgumentException("Element name cannot be null or empty");
        }
        int i = 0;
        int n = elementName.length();
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                int i3 = i;
                i++;
                char c = elementName.charAt(i3);
                if (c == ':' || c == '-') {
                    if (c == ':') {
                        if (i == 1) {
                            return HTMLElementName.forName(null, elementName);
                        }
                        if (TextUtils.equals(TemplateMode.HTML.isCaseSensitive(), "xml:", 0, 4, elementName, 0, i) || TextUtils.equals(TemplateMode.HTML.isCaseSensitive(), StandardXmlNsTagProcessor.ATTR_NAME_PREFIX, 0, 6, elementName, 0, i)) {
                            return HTMLElementName.forName(null, elementName);
                        }
                        return HTMLElementName.forName(elementName.substring(0, i - 1), elementName.substring(i, elementName.length()));
                    } else if (c == '-') {
                        if (i == 1) {
                            return HTMLElementName.forName(null, elementName);
                        }
                        return HTMLElementName.forName(elementName.substring(0, i - 1), elementName.substring(i, elementName.length()));
                    }
                }
            } else {
                return HTMLElementName.forName(null, elementName);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static TextElementName buildTextElementName(String prefix, String elementName) {
        if (elementName == null) {
            throw new IllegalArgumentException("Element name cannot be null or empty");
        }
        if (prefix == null || prefix.trim().length() == 0) {
            return buildTextElementName(elementName);
        }
        return TextElementName.forName(prefix, elementName);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static XMLElementName buildXMLElementName(String prefix, String elementName) {
        if (elementName == null || elementName.length() == 0) {
            throw new IllegalArgumentException("Element name cannot be null or empty");
        }
        if (prefix == null || prefix.trim().length() == 0) {
            return buildXMLElementName(elementName);
        }
        return XMLElementName.forName(prefix, elementName);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static HTMLElementName buildHTMLElementName(String prefix, String elementName) {
        if (elementName == null || elementName.length() == 0) {
            throw new IllegalArgumentException("Element name cannot be null or empty");
        }
        if (prefix == null || prefix.trim().length() == 0) {
            return buildHTMLElementName(elementName);
        }
        return HTMLElementName.forName(prefix, elementName);
    }

    public static ElementName forName(TemplateMode templateMode, char[] elementNameBuffer, int elementNameOffset, int elementNameLen) {
        if (templateMode == null) {
            throw new IllegalArgumentException("Template Mode cannot be null");
        }
        if (templateMode == TemplateMode.HTML) {
            return forHTMLName(elementNameBuffer, elementNameOffset, elementNameLen);
        }
        if (templateMode == TemplateMode.XML) {
            return forXMLName(elementNameBuffer, elementNameOffset, elementNameLen);
        }
        if (templateMode.isText()) {
            return forTextName(elementNameBuffer, elementNameOffset, elementNameLen);
        }
        throw new IllegalArgumentException("Unknown template mode '" + templateMode + "'");
    }

    public static ElementName forName(TemplateMode templateMode, String elementName) {
        if (templateMode == null) {
            throw new IllegalArgumentException("Template Mode cannot be null");
        }
        if (templateMode == TemplateMode.HTML) {
            return forHTMLName(elementName);
        }
        if (templateMode == TemplateMode.XML) {
            return forXMLName(elementName);
        }
        if (templateMode.isText()) {
            return forTextName(elementName);
        }
        throw new IllegalArgumentException("Unknown template mode '" + templateMode + "'");
    }

    public static ElementName forName(TemplateMode templateMode, String prefix, String elementName) {
        if (templateMode == null) {
            throw new IllegalArgumentException("Template Mode cannot be null");
        }
        if (templateMode == TemplateMode.HTML) {
            return forHTMLName(prefix, elementName);
        }
        if (templateMode == TemplateMode.XML) {
            return forXMLName(prefix, elementName);
        }
        if (templateMode.isText()) {
            return forTextName(prefix, elementName);
        }
        throw new IllegalArgumentException("Unknown template mode '" + templateMode + "'");
    }

    public static TextElementName forTextName(char[] elementNameBuffer, int elementNameOffset, int elementNameLen) {
        if (elementNameBuffer == null) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (elementNameOffset < 0 || elementNameLen < 0) {
            throw new IllegalArgumentException("Both name offset and length must be equal to or greater than zero");
        }
        return (TextElementName) textElementNamesRepository.getElement(elementNameBuffer, elementNameOffset, elementNameLen);
    }

    public static XMLElementName forXMLName(char[] elementNameBuffer, int elementNameOffset, int elementNameLen) {
        if (elementNameBuffer == null || elementNameLen == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (elementNameOffset < 0 || elementNameLen < 0) {
            throw new IllegalArgumentException("Both name offset and length must be equal to or greater than zero");
        }
        return (XMLElementName) xmlElementNamesRepository.getElement(elementNameBuffer, elementNameOffset, elementNameLen);
    }

    public static HTMLElementName forHTMLName(char[] elementNameBuffer, int elementNameOffset, int elementNameLen) {
        if (elementNameBuffer == null || elementNameLen == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (elementNameOffset < 0 || elementNameLen < 0) {
            throw new IllegalArgumentException("Both name offset and length must be equal to or greater than zero");
        }
        return (HTMLElementName) htmlElementNamesRepository.getElement(elementNameBuffer, elementNameOffset, elementNameLen);
    }

    public static TextElementName forTextName(String elementName) {
        if (elementName == null) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (TextElementName) textElementNamesRepository.getElement(elementName);
    }

    public static XMLElementName forXMLName(String elementName) {
        if (elementName == null || elementName.trim().length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (XMLElementName) xmlElementNamesRepository.getElement(elementName);
    }

    public static HTMLElementName forHTMLName(String elementName) {
        if (elementName == null || elementName.trim().length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (HTMLElementName) htmlElementNamesRepository.getElement(elementName);
    }

    public static TextElementName forTextName(String prefix, String elementName) {
        if (elementName == null || (elementName.trim().length() == 0 && prefix != null && prefix.trim().length() > 0)) {
            throw new IllegalArgumentException("Name cannot be null (nor empty if prefix is not empty)");
        }
        return (TextElementName) textElementNamesRepository.getElement(prefix, elementName);
    }

    public static XMLElementName forXMLName(String prefix, String elementName) {
        if (elementName == null || elementName.trim().length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (XMLElementName) xmlElementNamesRepository.getElement(prefix, elementName);
    }

    public static HTMLElementName forHTMLName(String prefix, String elementName) {
        if (elementName == null || elementName.trim().length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (HTMLElementName) htmlElementNamesRepository.getElement(prefix, elementName);
    }

    private ElementNames() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/ElementNames$ElementNamesRepository.class */
    public static final class ElementNamesRepository {
        private final TemplateMode templateMode;
        private final ReadWriteLock lock = new ReentrantReadWriteLock(true);
        private final Lock readLock = this.lock.readLock();
        private final Lock writeLock = this.lock.writeLock();
        private final List<String> repositoryNames = new ArrayList(500);
        private final List<ElementName> repository = new ArrayList(500);

        ElementNamesRepository(TemplateMode templateMode) {
            this.templateMode = templateMode;
        }

        ElementName getElement(char[] text, int offset, int len) {
            this.readLock.lock();
            try {
                int index = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, text, offset, len);
                if (index >= 0) {
                    ElementName elementName = this.repository.get(index);
                    this.readLock.unlock();
                    return elementName;
                }
                this.readLock.unlock();
                this.writeLock.lock();
                try {
                    ElementName storeElement = storeElement(text, offset, len);
                    this.writeLock.unlock();
                    return storeElement;
                } catch (Throwable th) {
                    this.writeLock.unlock();
                    throw th;
                }
            } catch (Throwable th2) {
                this.readLock.unlock();
                throw th2;
            }
        }

        ElementName getElement(String completeElementName) {
            this.readLock.lock();
            try {
                int index = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, completeElementName);
                if (index >= 0) {
                    ElementName elementName = this.repository.get(index);
                    this.readLock.unlock();
                    return elementName;
                }
                this.readLock.unlock();
                this.writeLock.lock();
                try {
                    ElementName storeElement = storeElement(completeElementName);
                    this.writeLock.unlock();
                    return storeElement;
                } catch (Throwable th) {
                    this.writeLock.unlock();
                    throw th;
                }
            } catch (Throwable th2) {
                this.readLock.unlock();
                throw th2;
            }
        }

        ElementName getElement(String prefix, String elementName) {
            this.readLock.lock();
            try {
                int index = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, prefix, elementName);
                if (index >= 0) {
                    ElementName elementName2 = this.repository.get(index);
                    this.readLock.unlock();
                    return elementName2;
                }
                this.readLock.unlock();
                this.writeLock.lock();
                try {
                    ElementName storeElement = storeElement(prefix, elementName);
                    this.writeLock.unlock();
                    return storeElement;
                } catch (Throwable th) {
                    this.writeLock.unlock();
                    throw th;
                }
            } catch (Throwable th2) {
                this.readLock.unlock();
                throw th2;
            }
        }

        private ElementName storeElement(char[] text, int offset, int len) {
            ElementName name;
            int index = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, text, offset, len);
            if (index >= 0) {
                return this.repository.get(index);
            }
            if (this.templateMode == TemplateMode.HTML) {
                name = ElementNames.buildHTMLElementName(text, offset, len);
            } else {
                name = this.templateMode == TemplateMode.XML ? ElementNames.buildXMLElementName(text, offset, len) : ElementNames.buildTextElementName(text, offset, len);
            }
            String[] completeElementNames = name.completeElementNames;
            for (String completeElementName : completeElementNames) {
                int index2 = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, completeElementName);
                this.repositoryNames.add((index2 + 1) * (-1), completeElementName);
                this.repository.add((index2 + 1) * (-1), name);
            }
            return name;
        }

        private ElementName storeElement(String elementName) {
            ElementName name;
            int index = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, elementName);
            if (index >= 0) {
                return this.repository.get(index);
            }
            if (this.templateMode == TemplateMode.HTML) {
                name = ElementNames.buildHTMLElementName(elementName);
            } else {
                name = this.templateMode == TemplateMode.XML ? ElementNames.buildXMLElementName(elementName) : ElementNames.buildTextElementName(elementName);
            }
            String[] completeElementNames = name.completeElementNames;
            for (String completeElementName : completeElementNames) {
                int index2 = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, completeElementName);
                this.repositoryNames.add((index2 + 1) * (-1), completeElementName);
                this.repository.add((index2 + 1) * (-1), name);
            }
            return name;
        }

        private ElementName storeElement(String prefix, String elementName) {
            ElementName name;
            int index = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, prefix, elementName);
            if (index >= 0) {
                return this.repository.get(index);
            }
            if (this.templateMode == TemplateMode.HTML) {
                name = ElementNames.buildHTMLElementName(prefix, elementName);
            } else {
                name = this.templateMode == TemplateMode.XML ? ElementNames.buildXMLElementName(prefix, elementName) : ElementNames.buildTextElementName(prefix, elementName);
            }
            String[] completeElementNames = name.completeElementNames;
            for (String completeElementName : completeElementNames) {
                int index2 = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, completeElementName);
                this.repositoryNames.add((index2 + 1) * (-1), completeElementName);
                this.repository.add((index2 + 1) * (-1), name);
            }
            return name;
        }

        private static int binarySearch(boolean caseSensitive, List<String> values, char[] text, int offset, int len) {
            int low = 0;
            int high = values.size() - 1;
            while (low <= high) {
                int mid = (low + high) >>> 1;
                String midVal = values.get(mid);
                int cmp = TextUtils.compareTo(caseSensitive, midVal, 0, midVal.length(), text, offset, len);
                if (cmp < 0) {
                    low = mid + 1;
                } else if (cmp > 0) {
                    high = mid - 1;
                } else {
                    return mid;
                }
            }
            return -(low + 1);
        }

        private static int binarySearch(boolean caseSensitive, List<String> values, String text) {
            int low = 0;
            int high = values.size() - 1;
            while (low <= high) {
                int mid = (low + high) >>> 1;
                String midVal = values.get(mid);
                int cmp = TextUtils.compareTo(caseSensitive, midVal, text);
                if (cmp < 0) {
                    low = mid + 1;
                } else if (cmp > 0) {
                    high = mid - 1;
                } else {
                    return mid;
                }
            }
            return -(low + 1);
        }

        private static int binarySearch(boolean caseSensitive, List<String> values, String prefix, String elementName) {
            if (prefix == null || prefix.trim().length() == 0) {
                return binarySearch(caseSensitive, values, elementName);
            }
            int prefixLen = prefix.length();
            int elementNameLen = elementName.length();
            int low = 0;
            int high = values.size() - 1;
            while (low <= high) {
                int mid = (low + high) >>> 1;
                String midVal = values.get(mid);
                int midValLen = midVal.length();
                if (TextUtils.startsWith(caseSensitive, midVal, prefix)) {
                    if (midValLen <= prefixLen) {
                        low = mid + 1;
                    } else {
                        int cmp = midVal.charAt(prefixLen) - ':';
                        if (cmp < 0) {
                            low = mid + 1;
                        } else if (cmp > 0) {
                            high = mid - 1;
                        } else {
                            int cmp2 = TextUtils.compareTo(caseSensitive, midVal, prefixLen + 1, midValLen - (prefixLen + 1), elementName, 0, elementNameLen);
                            if (cmp2 < 0) {
                                low = mid + 1;
                            } else if (cmp2 > 0) {
                                high = mid - 1;
                            } else {
                                return mid;
                            }
                        }
                    }
                } else {
                    int cmp3 = TextUtils.compareTo(caseSensitive, midVal, prefix);
                    if (cmp3 < 0) {
                        low = mid + 1;
                    } else if (cmp3 > 0) {
                        high = mid - 1;
                    } else {
                        throw new IllegalStateException("Bad comparison of midVal \"" + midVal + "\" and prefix \"" + prefix + "\"");
                    }
                }
            }
            return -(low + 1);
        }
    }
}