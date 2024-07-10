package org.thymeleaf.messageresolver;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/messageresolver/StandardMessageResolutionUtils.class */
final class StandardMessageResolutionUtils {
    private static final String PROPERTIES_FILE_EXTENSION = ".properties";
    private static final Map<String, String> EMPTY_MESSAGES = Collections.emptyMap();
    private static final Object[] EMPTY_MESSAGE_PARAMETERS = new Object[0];

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Map<String, String> resolveMessagesForTemplate(ITemplateResource templateResource, Locale locale) {
        Properties messageProperties;
        String resourceBaseName = templateResource.getBaseName();
        if (resourceBaseName == null || resourceBaseName.length() == 0) {
            return EMPTY_MESSAGES;
        }
        List<String> messageResourceNames = computeMessageResourceNamesFromBase(resourceBaseName, locale);
        Map<String, String> combinedMessages = null;
        for (String messageResourceName : messageResourceNames) {
            try {
                ITemplateResource messageResource = templateResource.relative(messageResourceName);
                Reader messageResourceReader = messageResource.reader();
                if (messageResourceReader != null && (messageProperties = readMessagesResource(messageResourceReader)) != null && !messageProperties.isEmpty()) {
                    if (combinedMessages == null) {
                        combinedMessages = new HashMap<>(20);
                    }
                    for (Map.Entry<Object, Object> propertyEntry : messageProperties.entrySet()) {
                        combinedMessages.put((String) propertyEntry.getKey(), (String) propertyEntry.getValue());
                    }
                }
            } catch (IOException e) {
            }
        }
        if (combinedMessages == null) {
            return EMPTY_MESSAGES;
        }
        return Collections.unmodifiableMap(combinedMessages);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Map<String, String> resolveMessagesForOrigin(Class<?> origin, Locale locale) {
        Map<String, String> combinedMessages = new HashMap<>(20);
        Class<?> currentClass = origin;
        combinedMessages.putAll(resolveMessagesForSpecificClass(currentClass, locale));
        while (!currentClass.getSuperclass().equals(Object.class)) {
            currentClass = currentClass.getSuperclass();
            Map<String, String> messagesForCurrentClass = resolveMessagesForSpecificClass(currentClass, locale);
            for (String messageKey : messagesForCurrentClass.keySet()) {
                if (!combinedMessages.containsKey(messageKey)) {
                    combinedMessages.put(messageKey, messagesForCurrentClass.get(messageKey));
                }
            }
        }
        return Collections.unmodifiableMap(combinedMessages);
    }

    private static Map<String, String> resolveMessagesForSpecificClass(Class<?> originClass, Locale locale) {
        ClassLoader originClassLoader = originClass.getClassLoader();
        String originClassName = originClass.getName();
        String resourceBaseName = StringUtils.replace(originClassName, ".", "/");
        List<String> messageResourceNames = computeMessageResourceNamesFromBase(resourceBaseName, locale);
        Map<String, String> combinedMessages = null;
        for (String messageResourceName : messageResourceNames) {
            InputStream inputStream = originClassLoader.getResourceAsStream(messageResourceName);
            if (inputStream != null) {
                InputStreamReader messageResourceReader = new InputStreamReader(inputStream);
                Properties messageProperties = readMessagesResource(messageResourceReader);
                if (messageProperties != null && !messageProperties.isEmpty()) {
                    if (combinedMessages == null) {
                        combinedMessages = new HashMap<>(20);
                    }
                    for (Map.Entry<Object, Object> propertyEntry : messageProperties.entrySet()) {
                        combinedMessages.put((String) propertyEntry.getKey(), (String) propertyEntry.getValue());
                    }
                }
            }
        }
        if (combinedMessages == null) {
            return EMPTY_MESSAGES;
        }
        return Collections.unmodifiableMap(combinedMessages);
    }

    private static List<String> computeMessageResourceNamesFromBase(String resourceBaseName, Locale locale) {
        List<String> resourceNames = new ArrayList<>(5);
        if (StringUtils.isEmptyOrWhitespace(locale.getLanguage())) {
            throw new TemplateProcessingException("Locale \"" + locale.toString() + "\" cannot be used as it does not specify a language.");
        }
        resourceNames.add(resourceBaseName + PROPERTIES_FILE_EXTENSION);
        resourceNames.add(resourceBaseName + "_" + locale.getLanguage() + PROPERTIES_FILE_EXTENSION);
        if (!StringUtils.isEmptyOrWhitespace(locale.getCountry())) {
            resourceNames.add(resourceBaseName + "_" + locale.getLanguage() + "_" + locale.getCountry() + PROPERTIES_FILE_EXTENSION);
        }
        if (!StringUtils.isEmptyOrWhitespace(locale.getVariant())) {
            resourceNames.add(resourceBaseName + "_" + locale.getLanguage() + "_" + locale.getCountry() + "-" + locale.getVariant() + PROPERTIES_FILE_EXTENSION);
        }
        return resourceNames;
    }

    private static Properties readMessagesResource(Reader propertiesReader) {
        if (propertiesReader == null) {
            return null;
        }
        Properties properties = new Properties();
        try {
            try {
                properties.load(propertiesReader);
                return properties;
            } finally {
                try {
                    propertiesReader.close();
                } catch (Throwable th) {
                }
            }
        } catch (Exception e) {
            throw new TemplateInputException("Exception loading messages file", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String formatMessage(Locale locale, String message, Object[] messageParameters) {
        if (message == null) {
            return null;
        }
        if (!isFormatCandidate(message)) {
            return message;
        }
        MessageFormat messageFormat = new MessageFormat(message, locale);
        return messageFormat.format(messageParameters != null ? messageParameters : EMPTY_MESSAGE_PARAMETERS);
    }

    private static boolean isFormatCandidate(String message) {
        char c;
        int n = message.length();
        do {
            int i = n;
            n--;
            if (i != 0) {
                c = message.charAt(n);
                if (c == '}') {
                    return true;
                }
            } else {
                return false;
            }
        } while (c != '\'');
        return true;
    }

    private StandardMessageResolutionUtils() {
    }
}