package org.apache.el.util;

import java.text.FieldPosition;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:org/apache/el/util/MessageFactory.class */
public final class MessageFactory {
    static final ResourceBundle bundle = ResourceBundle.getBundle("org.apache.el.Messages");

    public static String get(String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
    }

    public static String get(String key, Object... args) {
        String value = get(key);
        MessageFormat mf = new MessageFormat(value);
        return mf.format(args, new StringBuffer(), (FieldPosition) null).toString();
    }
}