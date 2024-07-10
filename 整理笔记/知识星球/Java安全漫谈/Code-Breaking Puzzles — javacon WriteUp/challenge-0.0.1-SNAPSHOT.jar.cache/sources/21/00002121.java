package org.springframework.http.server;

import java.util.List;
import org.springframework.util.MultiValueMap;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/PathContainer.class */
public interface PathContainer {

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/PathContainer$Element.class */
    public interface Element {
        String value();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/PathContainer$PathSegment.class */
    public interface PathSegment extends Element {
        String valueToMatch();

        char[] valueToMatchAsChars();

        MultiValueMap<String, String> parameters();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/PathContainer$Separator.class */
    public interface Separator extends Element {
    }

    String value();

    List<Element> elements();

    default PathContainer subPath(int index) {
        return subPath(index, elements().size());
    }

    default PathContainer subPath(int startIndex, int endIndex) {
        return DefaultPathContainer.subPath(this, startIndex, endIndex);
    }

    static PathContainer parsePath(String path) {
        return DefaultPathContainer.createFromUrlPath(path);
    }
}