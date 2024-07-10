package org.thymeleaf.spring5.context;

import java.util.Map;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/context/IThymeleafRequestDataValueProcessor.class */
public interface IThymeleafRequestDataValueProcessor {
    String processAction(String str, String str2);

    String processFormFieldValue(String str, String str2, String str3);

    Map<String, String> getExtraHiddenFields();

    String processUrl(String str);
}