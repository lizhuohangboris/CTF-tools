package org.thymeleaf.expression;

import org.unbescape.uri.UriEscape;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/expression/Uris.class */
public final class Uris {
    public String escapePath(String text) {
        return UriEscape.escapeUriPath(text);
    }

    public String unescapePath(String text) {
        return UriEscape.unescapeUriPath(text);
    }

    public String escapePath(String text, String encoding) {
        return UriEscape.escapeUriPath(text, encoding);
    }

    public String unescapePath(String text, String encoding) {
        return UriEscape.unescapeUriPath(text, encoding);
    }

    public String escapePathSegment(String text) {
        return UriEscape.escapeUriPathSegment(text);
    }

    public String unescapePathSegment(String text) {
        return UriEscape.unescapeUriPathSegment(text);
    }

    public String escapePathSegment(String text, String encoding) {
        return UriEscape.escapeUriPathSegment(text, encoding);
    }

    public String unescapePathSegment(String text, String encoding) {
        return UriEscape.unescapeUriPathSegment(text, encoding);
    }

    public String escapeFragmentId(String text) {
        return UriEscape.escapeUriFragmentId(text);
    }

    public String unescapeFragmentId(String text) {
        return UriEscape.unescapeUriFragmentId(text);
    }

    public String escapeFragmentId(String text, String encoding) {
        return UriEscape.escapeUriFragmentId(text, encoding);
    }

    public String unescapeFragmentId(String text, String encoding) {
        return UriEscape.unescapeUriFragmentId(text, encoding);
    }

    public String escapeQueryParam(String text) {
        return UriEscape.escapeUriQueryParam(text);
    }

    public String unescapeQueryParam(String text) {
        return UriEscape.unescapeUriQueryParam(text);
    }

    public String escapeQueryParam(String text, String encoding) {
        return UriEscape.escapeUriQueryParam(text, encoding);
    }

    public String unescapeQueryParam(String text, String encoding) {
        return UriEscape.unescapeUriQueryParam(text, encoding);
    }
}