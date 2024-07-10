package org.thymeleaf.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.thymeleaf.standard.processor.StandardXmlNsTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.TextUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/AttributeNames.class */
public class AttributeNames {
    private static final AttributeNamesRepository htmlAttributeNamesRepository = new AttributeNamesRepository(TemplateMode.HTML);
    private static final AttributeNamesRepository xmlAttributeNamesRepository = new AttributeNamesRepository(TemplateMode.XML);
    private static final AttributeNamesRepository textAttributeNamesRepository = new AttributeNamesRepository(TemplateMode.TEXT);

    /* JADX INFO: Access modifiers changed from: private */
    public static TextAttributeName buildTextAttributeName(char[] attributeNameBuffer, int attributeNameOffset, int attributeNameLen) {
        if (attributeNameBuffer == null || attributeNameLen == 0) {
            throw new IllegalArgumentException("Attribute name buffer cannot be null or empty");
        }
        if (attributeNameOffset < 0 || attributeNameLen < 0) {
            throw new IllegalArgumentException("Attribute name offset and len must be equal or greater than zero");
        }
        int i = attributeNameOffset;
        int n = attributeNameLen;
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                int i3 = i;
                i++;
                char c = attributeNameBuffer[i3];
                if (c == ':' && c == ':') {
                    if (i == attributeNameOffset + 1) {
                        return TextAttributeName.forName(null, new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));
                    }
                    return TextAttributeName.forName(new String(attributeNameBuffer, attributeNameOffset, i - (attributeNameOffset + 1)), new String(attributeNameBuffer, i, (attributeNameOffset + attributeNameLen) - i));
                }
            } else {
                return TextAttributeName.forName(null, new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static XMLAttributeName buildXMLAttributeName(char[] attributeNameBuffer, int attributeNameOffset, int attributeNameLen) {
        if (attributeNameBuffer == null || attributeNameLen == 0) {
            throw new IllegalArgumentException("Attribute name buffer cannot be null or empty");
        }
        if (attributeNameOffset < 0 || attributeNameLen < 0) {
            throw new IllegalArgumentException("Attribute name offset and len must be equal or greater than zero");
        }
        int i = attributeNameOffset;
        int n = attributeNameLen;
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                int i3 = i;
                i++;
                char c = attributeNameBuffer[i3];
                if (c == ':' && c == ':') {
                    if (i == attributeNameOffset + 1) {
                        return XMLAttributeName.forName(null, new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));
                    }
                    return XMLAttributeName.forName(new String(attributeNameBuffer, attributeNameOffset, i - (attributeNameOffset + 1)), new String(attributeNameBuffer, i, (attributeNameOffset + attributeNameLen) - i));
                }
            } else {
                return XMLAttributeName.forName(null, new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static HTMLAttributeName buildHTMLAttributeName(char[] attributeNameBuffer, int attributeNameOffset, int attributeNameLen) {
        if (attributeNameBuffer == null || attributeNameLen == 0) {
            throw new IllegalArgumentException("Attribute name buffer cannot be null or empty");
        }
        if (attributeNameOffset < 0 || attributeNameLen < 0) {
            throw new IllegalArgumentException("Attribute name offset and len must be equal or greater than zero");
        }
        int i = attributeNameOffset;
        int n = attributeNameLen;
        boolean inData = false;
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                int i3 = i;
                i++;
                char c = attributeNameBuffer[i3];
                if (c == ':' || c == '-') {
                    if (!inData && c == ':') {
                        if (i == attributeNameOffset + 1) {
                            return HTMLAttributeName.forName(null, new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));
                        }
                        if (TextUtils.equals(TemplateMode.HTML.isCaseSensitive(), "xml:", 0, 4, attributeNameBuffer, attributeNameOffset, i - attributeNameOffset) || TextUtils.equals(TemplateMode.HTML.isCaseSensitive(), StandardXmlNsTagProcessor.ATTR_NAME_PREFIX, 0, 6, attributeNameBuffer, attributeNameOffset, i - attributeNameOffset)) {
                            return HTMLAttributeName.forName(null, new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));
                        }
                        return HTMLAttributeName.forName(new String(attributeNameBuffer, attributeNameOffset, i - (attributeNameOffset + 1)), new String(attributeNameBuffer, i, (attributeNameOffset + attributeNameLen) - i));
                    } else if (!inData && c == '-') {
                        if (i != attributeNameOffset + 5 || !TextUtils.equals(TemplateMode.HTML.isCaseSensitive(), "data", 0, 4, attributeNameBuffer, attributeNameOffset, i - (attributeNameOffset + 1))) {
                            break;
                        }
                        inData = true;
                    } else if (inData && c == '-') {
                        if (i == attributeNameOffset + 6) {
                            return HTMLAttributeName.forName(null, new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));
                        }
                        return HTMLAttributeName.forName(new String(attributeNameBuffer, attributeNameOffset + 5, i - (attributeNameOffset + 6)), new String(attributeNameBuffer, i, (attributeNameOffset + attributeNameLen) - i));
                    }
                }
            } else {
                return HTMLAttributeName.forName(null, new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));
            }
        }
        return HTMLAttributeName.forName(null, new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static TextAttributeName buildTextAttributeName(String attributeName) {
        if (attributeName == null || attributeName.length() == 0) {
            throw new IllegalArgumentException("Attribute name cannot be null or empty");
        }
        int i = 0;
        int n = attributeName.length();
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                int i3 = i;
                i++;
                char c = attributeName.charAt(i3);
                if (c == ':' && c == ':') {
                    if (i == 1) {
                        return TextAttributeName.forName(null, attributeName);
                    }
                    return TextAttributeName.forName(attributeName.substring(0, i - 1), attributeName.substring(i, attributeName.length()));
                }
            } else {
                return TextAttributeName.forName(null, attributeName);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static XMLAttributeName buildXMLAttributeName(String attributeName) {
        if (attributeName == null || attributeName.length() == 0) {
            throw new IllegalArgumentException("Attribute name cannot be null or empty");
        }
        int i = 0;
        int n = attributeName.length();
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                int i3 = i;
                i++;
                char c = attributeName.charAt(i3);
                if (c == ':' && c == ':') {
                    if (i == 1) {
                        return XMLAttributeName.forName(null, attributeName);
                    }
                    return XMLAttributeName.forName(attributeName.substring(0, i - 1), attributeName.substring(i, attributeName.length()));
                }
            } else {
                return XMLAttributeName.forName(null, attributeName);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static HTMLAttributeName buildHTMLAttributeName(String attributeName) {
        if (attributeName == null || attributeName.length() == 0) {
            throw new IllegalArgumentException("Attribute name cannot be null or empty");
        }
        int i = 0;
        int n = attributeName.length();
        boolean inData = false;
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                int i3 = i;
                i++;
                char c = attributeName.charAt(i3);
                if (c == ':' || c == '-') {
                    if (!inData && c == ':') {
                        if (i == 1) {
                            return HTMLAttributeName.forName(null, attributeName);
                        }
                        if (TextUtils.equals(TemplateMode.HTML.isCaseSensitive(), "xml:", 0, 4, attributeName, 0, i) || TextUtils.equals(TemplateMode.HTML.isCaseSensitive(), StandardXmlNsTagProcessor.ATTR_NAME_PREFIX, 0, 6, attributeName, 0, i)) {
                            return HTMLAttributeName.forName(null, attributeName);
                        }
                        return HTMLAttributeName.forName(attributeName.substring(0, i - 1), attributeName.substring(i, attributeName.length()));
                    } else if (!inData && c == '-') {
                        if (i != 5 || !TextUtils.equals(TemplateMode.HTML.isCaseSensitive(), "data", 0, 4, attributeName, 0, 4)) {
                            break;
                        }
                        inData = true;
                    } else if (inData && c == '-') {
                        if (i == 6) {
                            return HTMLAttributeName.forName(null, attributeName);
                        }
                        return HTMLAttributeName.forName(attributeName.substring(5, i - 1), attributeName.substring(i, attributeName.length()));
                    }
                }
            } else {
                return HTMLAttributeName.forName(null, attributeName);
            }
        }
        return HTMLAttributeName.forName(null, attributeName);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static TextAttributeName buildTextAttributeName(String prefix, String attributeName) {
        if (attributeName == null || attributeName.length() == 0) {
            throw new IllegalArgumentException("Attribute name cannot be null or empty");
        }
        if (prefix == null || prefix.trim().length() == 0) {
            return buildTextAttributeName(attributeName);
        }
        return TextAttributeName.forName(prefix, attributeName);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static XMLAttributeName buildXMLAttributeName(String prefix, String attributeName) {
        if (attributeName == null || attributeName.length() == 0) {
            throw new IllegalArgumentException("Attribute name cannot be null or empty");
        }
        if (prefix == null || prefix.trim().length() == 0) {
            return buildXMLAttributeName(attributeName);
        }
        return XMLAttributeName.forName(prefix, attributeName);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static HTMLAttributeName buildHTMLAttributeName(String prefix, String attributeName) {
        if (attributeName == null || attributeName.length() == 0) {
            throw new IllegalArgumentException("Attribute name cannot be null or empty");
        }
        if (prefix == null || prefix.trim().length() == 0) {
            return buildHTMLAttributeName(attributeName);
        }
        return HTMLAttributeName.forName(prefix, attributeName);
    }

    public static AttributeName forName(TemplateMode templateMode, char[] attributeNameBuffer, int attributeNameOffset, int attributeNameLen) {
        if (templateMode == null) {
            throw new IllegalArgumentException("Template Mode cannot be null");
        }
        if (templateMode == TemplateMode.HTML) {
            return forHTMLName(attributeNameBuffer, attributeNameOffset, attributeNameLen);
        }
        if (templateMode == TemplateMode.XML) {
            return forXMLName(attributeNameBuffer, attributeNameOffset, attributeNameLen);
        }
        if (templateMode.isText()) {
            return forTextName(attributeNameBuffer, attributeNameOffset, attributeNameLen);
        }
        throw new IllegalArgumentException("Unknown template mode '" + templateMode + "'");
    }

    public static AttributeName forName(TemplateMode templateMode, String attributeName) {
        if (templateMode == null) {
            throw new IllegalArgumentException("Template Mode cannot be null");
        }
        if (templateMode == TemplateMode.HTML) {
            return forHTMLName(attributeName);
        }
        if (templateMode == TemplateMode.XML) {
            return forXMLName(attributeName);
        }
        if (templateMode.isText()) {
            return forTextName(attributeName);
        }
        throw new IllegalArgumentException("Unknown template mode '" + templateMode + "'");
    }

    public static AttributeName forName(TemplateMode templateMode, String prefix, String attributeName) {
        if (templateMode == null) {
            throw new IllegalArgumentException("Template Mode cannot be null");
        }
        if (templateMode == TemplateMode.HTML) {
            return forHTMLName(prefix, attributeName);
        }
        if (templateMode == TemplateMode.XML) {
            return forXMLName(prefix, attributeName);
        }
        if (templateMode.isText()) {
            return forTextName(prefix, attributeName);
        }
        throw new IllegalArgumentException("Unknown template mode '" + templateMode + "'");
    }

    public static TextAttributeName forTextName(char[] attributeNameBuffer, int attributeNameOffset, int attributeNameLen) {
        if (attributeNameBuffer == null || attributeNameLen == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (attributeNameOffset < 0 || attributeNameLen < 0) {
            throw new IllegalArgumentException("Both name offset and length must be equal to or greater than zero");
        }
        return (TextAttributeName) textAttributeNamesRepository.getAttribute(attributeNameBuffer, attributeNameOffset, attributeNameLen);
    }

    public static XMLAttributeName forXMLName(char[] attributeNameBuffer, int attributeNameOffset, int attributeNameLen) {
        if (attributeNameBuffer == null || attributeNameLen == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (attributeNameOffset < 0 || attributeNameLen < 0) {
            throw new IllegalArgumentException("Both name offset and length must be equal to or greater than zero");
        }
        return (XMLAttributeName) xmlAttributeNamesRepository.getAttribute(attributeNameBuffer, attributeNameOffset, attributeNameLen);
    }

    public static HTMLAttributeName forHTMLName(char[] attributeNameBuffer, int attributeNameOffset, int attributeNameLen) {
        if (attributeNameBuffer == null || attributeNameLen == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (attributeNameOffset < 0 || attributeNameLen < 0) {
            throw new IllegalArgumentException("Both name offset and length must be equal to or greater than zero");
        }
        return (HTMLAttributeName) htmlAttributeNamesRepository.getAttribute(attributeNameBuffer, attributeNameOffset, attributeNameLen);
    }

    public static TextAttributeName forTextName(String attributeName) {
        if (attributeName == null || attributeName.trim().length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (TextAttributeName) textAttributeNamesRepository.getAttribute(attributeName);
    }

    public static XMLAttributeName forXMLName(String attributeName) {
        if (attributeName == null || attributeName.trim().length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (XMLAttributeName) xmlAttributeNamesRepository.getAttribute(attributeName);
    }

    public static HTMLAttributeName forHTMLName(String attributeName) {
        if (attributeName == null || attributeName.trim().length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (HTMLAttributeName) htmlAttributeNamesRepository.getAttribute(attributeName);
    }

    public static TextAttributeName forTextName(String prefix, String attributeName) {
        if (attributeName == null || attributeName.trim().length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (TextAttributeName) textAttributeNamesRepository.getAttribute(prefix, attributeName);
    }

    public static XMLAttributeName forXMLName(String prefix, String attributeName) {
        if (attributeName == null || attributeName.trim().length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (XMLAttributeName) xmlAttributeNamesRepository.getAttribute(prefix, attributeName);
    }

    public static HTMLAttributeName forHTMLName(String prefix, String attributeName) {
        if (attributeName == null || attributeName.trim().length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return (HTMLAttributeName) htmlAttributeNamesRepository.getAttribute(prefix, attributeName);
    }

    private AttributeNames() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/AttributeNames$AttributeNamesRepository.class */
    public static final class AttributeNamesRepository {
        private final TemplateMode templateMode;
        private final ReadWriteLock lock = new ReentrantReadWriteLock(true);
        private final Lock readLock = this.lock.readLock();
        private final Lock writeLock = this.lock.writeLock();
        private final List<String> repositoryNames = new ArrayList(500);
        private final List<AttributeName> repository = new ArrayList(500);

        AttributeNamesRepository(TemplateMode templateMode) {
            this.templateMode = templateMode;
        }

        AttributeName getAttribute(char[] text, int offset, int len) {
            this.readLock.lock();
            try {
                int index = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, text, offset, len);
                if (index >= 0) {
                    AttributeName attributeName = this.repository.get(index);
                    this.readLock.unlock();
                    return attributeName;
                }
                this.readLock.unlock();
                this.writeLock.lock();
                try {
                    AttributeName storeAttribute = storeAttribute(text, offset, len);
                    this.writeLock.unlock();
                    return storeAttribute;
                } catch (Throwable th) {
                    this.writeLock.unlock();
                    throw th;
                }
            } catch (Throwable th2) {
                this.readLock.unlock();
                throw th2;
            }
        }

        AttributeName getAttribute(String completeAttributeName) {
            this.readLock.lock();
            try {
                int index = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, completeAttributeName);
                if (index >= 0) {
                    AttributeName attributeName = this.repository.get(index);
                    this.readLock.unlock();
                    return attributeName;
                }
                this.readLock.unlock();
                this.writeLock.lock();
                try {
                    AttributeName storeAttribute = storeAttribute(completeAttributeName);
                    this.writeLock.unlock();
                    return storeAttribute;
                } catch (Throwable th) {
                    this.writeLock.unlock();
                    throw th;
                }
            } catch (Throwable th2) {
                this.readLock.unlock();
                throw th2;
            }
        }

        AttributeName getAttribute(String prefix, String attributeName) {
            this.readLock.lock();
            try {
                int index = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, prefix, attributeName);
                if (index >= 0) {
                    AttributeName attributeName2 = this.repository.get(index);
                    this.readLock.unlock();
                    return attributeName2;
                }
                this.readLock.unlock();
                this.writeLock.lock();
                try {
                    AttributeName storeAttribute = storeAttribute(prefix, attributeName);
                    this.writeLock.unlock();
                    return storeAttribute;
                } catch (Throwable th) {
                    this.writeLock.unlock();
                    throw th;
                }
            } catch (Throwable th2) {
                this.readLock.unlock();
                throw th2;
            }
        }

        private AttributeName storeAttribute(char[] text, int offset, int len) {
            AttributeName name;
            int index = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, text, offset, len);
            if (index >= 0) {
                return this.repository.get(index);
            }
            if (this.templateMode == TemplateMode.HTML) {
                name = AttributeNames.buildHTMLAttributeName(text, offset, len);
            } else {
                name = this.templateMode == TemplateMode.XML ? AttributeNames.buildXMLAttributeName(text, offset, len) : AttributeNames.buildTextAttributeName(text, offset, len);
            }
            String[] completeAttributeNames = name.completeAttributeNames;
            for (String completeAttributeName : completeAttributeNames) {
                int index2 = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, completeAttributeName);
                this.repositoryNames.add((index2 + 1) * (-1), completeAttributeName);
                this.repository.add((index2 + 1) * (-1), name);
            }
            return name;
        }

        private AttributeName storeAttribute(String attributeName) {
            AttributeName name;
            int index = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, attributeName);
            if (index >= 0) {
                return this.repository.get(index);
            }
            if (this.templateMode == TemplateMode.HTML) {
                name = AttributeNames.buildHTMLAttributeName(attributeName);
            } else {
                name = this.templateMode == TemplateMode.XML ? AttributeNames.buildXMLAttributeName(attributeName) : AttributeNames.buildTextAttributeName(attributeName);
            }
            String[] completeAttributeNames = name.completeAttributeNames;
            for (String completeAttributeName : completeAttributeNames) {
                int index2 = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, completeAttributeName);
                this.repositoryNames.add((index2 + 1) * (-1), completeAttributeName);
                this.repository.add((index2 + 1) * (-1), name);
            }
            return name;
        }

        private AttributeName storeAttribute(String prefix, String attributeName) {
            AttributeName name;
            int index = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, prefix, attributeName);
            if (index >= 0) {
                return this.repository.get(index);
            }
            if (this.templateMode == TemplateMode.HTML) {
                name = AttributeNames.buildHTMLAttributeName(prefix, attributeName);
            } else {
                name = this.templateMode == TemplateMode.XML ? AttributeNames.buildXMLAttributeName(prefix, attributeName) : AttributeNames.buildTextAttributeName(prefix, attributeName);
            }
            String[] completeAttributeNames = name.completeAttributeNames;
            for (String completeAttributeName : completeAttributeNames) {
                int index2 = binarySearch(this.templateMode.isCaseSensitive(), this.repositoryNames, completeAttributeName);
                this.repositoryNames.add((index2 + 1) * (-1), completeAttributeName);
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

        private static int binarySearch(boolean caseSensitive, List<String> values, String prefix, String attributeName) {
            if (prefix == null || prefix.trim().length() == 0) {
                return binarySearch(caseSensitive, values, attributeName);
            }
            int prefixLen = prefix.length();
            int attributeNameLen = attributeName.length();
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
                            int cmp2 = TextUtils.compareTo(caseSensitive, midVal, prefixLen + 1, midValLen - (prefixLen + 1), attributeName, 0, attributeNameLen);
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