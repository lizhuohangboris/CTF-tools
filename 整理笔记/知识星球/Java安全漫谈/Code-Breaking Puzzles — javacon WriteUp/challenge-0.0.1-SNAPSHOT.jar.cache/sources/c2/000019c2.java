package org.springframework.boot.json;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.PropertyAccessor;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/json/BasicJsonParser.class */
public class BasicJsonParser extends AbstractJsonParser {
    @Override // org.springframework.boot.json.JsonParser
    public Map<String, Object> parseMap(String json) {
        return parseMap(json, this::parseMapInternal);
    }

    @Override // org.springframework.boot.json.JsonParser
    public List<Object> parseList(String json) {
        return parseList(json, this::parseListInternal);
    }

    private List<Object> parseListInternal(String json) {
        List<Object> list = new ArrayList<>();
        for (String value : tokenize(trimLeadingCharacter(trimTrailingCharacter(json, ']'), '['))) {
            list.add(parseInternal(value));
        }
        return list;
    }

    private Object parseInternal(String json) {
        if (json.startsWith(PropertyAccessor.PROPERTY_KEY_PREFIX)) {
            return parseListInternal(json);
        }
        if (json.startsWith("{")) {
            return parseMapInternal(json);
        }
        if (json.startsWith("\"")) {
            return trimTrailingCharacter(trimLeadingCharacter(json, '\"'), '\"');
        }
        try {
            return Long.valueOf(json);
        } catch (NumberFormatException e) {
            try {
                return Double.valueOf(json);
            } catch (NumberFormatException e2) {
                return json;
            }
        }
    }

    private static String trimTrailingCharacter(String string, char c) {
        if (!string.isEmpty() && string.charAt(string.length() - 1) == c) {
            return string.substring(0, string.length() - 1);
        }
        return string;
    }

    private static String trimLeadingCharacter(String string, char c) {
        if (!string.isEmpty() && string.charAt(0) == c) {
            return string.substring(1);
        }
        return string;
    }

    private Map<String, Object> parseMapInternal(String json) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (String pair : tokenize(trimLeadingCharacter(trimTrailingCharacter(json, '}'), '{'))) {
            String[] values = StringUtils.trimArrayElements(StringUtils.split(pair, ":"));
            String key = trimLeadingCharacter(trimTrailingCharacter(values[0], '\"'), '\"');
            Object value = parseInternal(values[1]);
            map.put(key, value);
        }
        return map;
    }

    private List<String> tokenize(String json) {
        List<String> list = new ArrayList<>();
        int index = 0;
        int inObject = 0;
        int inList = 0;
        boolean inValue = false;
        boolean inEscape = false;
        StringBuilder build = new StringBuilder();
        while (index < json.length()) {
            char current = json.charAt(index);
            if (inEscape) {
                build.append(current);
                index++;
                inEscape = false;
            } else {
                if (current == '{') {
                    inObject++;
                }
                if (current == '}') {
                    inObject--;
                }
                if (current == '[') {
                    inList++;
                }
                if (current == ']') {
                    inList--;
                }
                if (current == '\"') {
                    inValue = !inValue;
                }
                if (current == ',' && inObject == 0 && inList == 0 && !inValue) {
                    list.add(build.toString());
                    build.setLength(0);
                } else if (current == '\\') {
                    inEscape = true;
                } else {
                    build.append(current);
                }
                index++;
            }
        }
        if (build.length() > 0) {
            list.add(build.toString());
        }
        return list;
    }
}