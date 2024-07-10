package org.apache.tomcat.websocket;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/Authenticator.class */
public abstract class Authenticator {
    private static final Pattern pattern = Pattern.compile("(\\w+)\\s*=\\s*(\"([^\"]+)\"|([^,=\"]+))\\s*,?");

    public abstract String getAuthorization(String str, String str2, Map<String, Object> map) throws AuthenticationException;

    public abstract String getSchemeName();

    public Map<String, String> parseWWWAuthenticateHeader(String WWWAuthenticate) {
        Matcher m = pattern.matcher(WWWAuthenticate);
        Map<String, String> challenge = new HashMap<>();
        while (m.find()) {
            String key = m.group(1);
            String qtedValue = m.group(3);
            String value = m.group(4);
            challenge.put(key, qtedValue != null ? qtedValue : value);
        }
        return challenge;
    }
}