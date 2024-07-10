package org.thymeleaf.expression;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/expression/Messages.class */
public class Messages {
    private static final String[] NO_PARAMETERS = new String[0];
    private final ITemplateContext context;

    public String msg(String messageKey) {
        return msgWithParams(messageKey, NO_PARAMETERS);
    }

    public String msg(String messageKey, Object messageParameter0) {
        return msgWithParams(messageKey, new Object[]{messageParameter0});
    }

    public String msg(String messageKey, Object messageParameter0, Object messageParameter1) {
        return msgWithParams(messageKey, new Object[]{messageParameter0, messageParameter1});
    }

    public String msg(String messageKey, Object messageParameter0, Object messageParameter1, Object messageParameter2) {
        return msgWithParams(messageKey, new Object[]{messageParameter0, messageParameter1, messageParameter2});
    }

    public String msgWithParams(String messageKey, Object[] messageParameters) {
        return this.context.getMessage(null, messageKey, messageParameters, true);
    }

    public String msgOrNull(String messageKey) {
        return msgOrNullWithParams(messageKey, NO_PARAMETERS);
    }

    public String msgOrNull(String messageKey, Object messageParameter0) {
        return msgOrNullWithParams(messageKey, new Object[]{messageParameter0});
    }

    public String msgOrNull(String messageKey, Object messageParameter0, Object messageParameter1) {
        return msgOrNullWithParams(messageKey, new Object[]{messageParameter0, messageParameter1});
    }

    public String msgOrNull(String messageKey, Object messageParameter0, Object messageParameter1, Object messageParameter2) {
        return msgOrNullWithParams(messageKey, new Object[]{messageParameter0, messageParameter1, messageParameter2});
    }

    public String msgOrNullWithParams(String messageKey, Object[] messageParameters) {
        return this.context.getMessage(null, messageKey, messageParameters, false);
    }

    public String[] arrayMsg(Object[] messageKeys) {
        return arrayMsgWithParams(messageKeys, NO_PARAMETERS);
    }

    public String[] arrayMsg(Object[] messageKeys, Object messageParameter0) {
        return arrayMsgWithParams(messageKeys, new Object[]{messageParameter0});
    }

    public String[] arrayMsg(Object[] messageKeys, Object messageParameter0, Object messageParameter1) {
        return arrayMsgWithParams(messageKeys, new Object[]{messageParameter0, messageParameter1});
    }

    public String[] arrayMsg(Object[] messageKeys, Object messageParameter0, Object messageParameter1, Object messageParameter2) {
        return arrayMsgWithParams(messageKeys, new Object[]{messageParameter0, messageParameter1, messageParameter2});
    }

    public String[] arrayMsgWithParams(Object[] messageKeys, Object[] messageParameters) {
        Validate.notNull(messageKeys, "Message keys cannot be null");
        String[] result = new String[messageKeys.length];
        for (int i = 0; i < messageKeys.length; i++) {
            result[i] = this.context.getMessage(null, (String) messageKeys[i], messageParameters, true);
        }
        return result;
    }

    public String[] arrayMsgOrNull(Object[] messageKeys) {
        return arrayMsgOrNullWithParams(messageKeys, NO_PARAMETERS);
    }

    public String[] arrayMsgOrNull(Object[] messageKeys, Object messageParameter0) {
        return arrayMsgOrNullWithParams(messageKeys, new Object[]{messageParameter0});
    }

    public String[] arrayMsgOrNull(Object[] messageKeys, Object messageParameter0, Object messageParameter1) {
        return arrayMsgOrNullWithParams(messageKeys, new Object[]{messageParameter0, messageParameter1});
    }

    public String[] arrayMsgOrNull(Object[] messageKeys, Object messageParameter0, Object messageParameter1, Object messageParameter2) {
        return arrayMsgOrNullWithParams(messageKeys, new Object[]{messageParameter0, messageParameter1, messageParameter2});
    }

    public String[] arrayMsgOrNullWithParams(Object[] messageKeys, Object[] messageParameters) {
        Validate.notNull(messageKeys, "Message keys cannot be null");
        String[] result = new String[messageKeys.length];
        for (int i = 0; i < messageKeys.length; i++) {
            result[i] = this.context.getMessage(null, (String) messageKeys[i], messageParameters, false);
        }
        return result;
    }

    public List<String> listMsg(List<String> messageKeys) {
        return listMsgWithParams(messageKeys, NO_PARAMETERS);
    }

    public List<String> listMsg(List<String> messageKeys, Object messageParameter0) {
        return listMsgWithParams(messageKeys, new Object[]{messageParameter0});
    }

    public List<String> listMsg(List<String> messageKeys, Object messageParameter0, Object messageParameter1) {
        return listMsgWithParams(messageKeys, new Object[]{messageParameter0, messageParameter1});
    }

    public List<String> listMsg(List<String> messageKeys, Object messageParameter0, Object messageParameter1, Object messageParameter2) {
        return listMsgWithParams(messageKeys, new Object[]{messageParameter0, messageParameter1, messageParameter2});
    }

    public List<String> listMsgWithParams(List<String> messageKeys, Object[] messageParameters) {
        Validate.notNull(messageKeys, "Message keys cannot be null");
        return doMsg(true, messageKeys, messageParameters);
    }

    public List<String> listMsgOrNull(List<String> messageKeys) {
        return listMsgOrNullWithParams(messageKeys, NO_PARAMETERS);
    }

    public List<String> listMsgOrNull(List<String> messageKeys, Object messageParameter0) {
        return listMsgOrNullWithParams(messageKeys, new Object[]{messageParameter0});
    }

    public List<String> listMsgOrNull(List<String> messageKeys, Object messageParameter0, Object messageParameter1) {
        return listMsgOrNullWithParams(messageKeys, new Object[]{messageParameter0, messageParameter1});
    }

    public List<String> listMsgOrNull(List<String> messageKeys, Object messageParameter0, Object messageParameter1, Object messageParameter2) {
        return listMsgOrNullWithParams(messageKeys, new Object[]{messageParameter0, messageParameter1, messageParameter2});
    }

    public List<String> listMsgOrNullWithParams(List<String> messageKeys, Object[] messageParameters) {
        Validate.notNull(messageKeys, "Message keys cannot be null");
        return doMsg(false, messageKeys, messageParameters);
    }

    public Set<String> setMsg(Set<String> messageKeys) {
        return setMsgWithParams(messageKeys, NO_PARAMETERS);
    }

    public Set<String> setMsg(Set<String> messageKeys, Object messageParameter0) {
        return setMsgWithParams(messageKeys, new Object[]{messageParameter0});
    }

    public Set<String> setMsg(Set<String> messageKeys, Object messageParameter0, Object messageParameter1) {
        return setMsgWithParams(messageKeys, new Object[]{messageParameter0, messageParameter1});
    }

    public Set<String> setMsg(Set<String> messageKeys, Object messageParameter0, Object messageParameter1, Object messageParameter2) {
        return setMsgWithParams(messageKeys, new Object[]{messageParameter0, messageParameter1, messageParameter2});
    }

    public Set<String> setMsgWithParams(Set<String> messageKeys, Object[] messageParameters) {
        Validate.notNull(messageKeys, "Message keys cannot be null");
        return new LinkedHashSet(doMsg(true, messageKeys, messageParameters));
    }

    public Set<String> setMsgOrNull(Set<String> messageKeys) {
        return setMsgOrNullWithParams(messageKeys, NO_PARAMETERS);
    }

    public Set<String> setMsgOrNull(Set<String> messageKeys, Object messageParameter0) {
        return setMsgOrNullWithParams(messageKeys, new Object[]{messageParameter0});
    }

    public Set<String> setMsgOrNull(Set<String> messageKeys, Object messageParameter0, Object messageParameter1) {
        return setMsgOrNullWithParams(messageKeys, new Object[]{messageParameter0, messageParameter1});
    }

    public Set<String> setMsgOrNull(Set<String> messageKeys, Object messageParameter0, Object messageParameter1, Object messageParameter2) {
        return setMsgOrNullWithParams(messageKeys, new Object[]{messageParameter0, messageParameter1, messageParameter2});
    }

    public Set<String> setMsgOrNullWithParams(Set<String> messageKeys, Object[] messageParameters) {
        Validate.notNull(messageKeys, "Message keys cannot be null");
        return new LinkedHashSet(doMsg(false, messageKeys, messageParameters));
    }

    private List<String> doMsg(boolean useAbsentMessageRepresentation, Iterable<String> messageKeys, Object... messageParameters) {
        List<String> result = new ArrayList<>(5);
        for (String messageKey : messageKeys) {
            result.add(this.context.getMessage(null, messageKey, messageParameters, useAbsentMessageRepresentation));
        }
        return result;
    }

    public Messages(ITemplateContext context) {
        Validate.notNull(context, "Context cannot be null");
        this.context = context;
    }
}