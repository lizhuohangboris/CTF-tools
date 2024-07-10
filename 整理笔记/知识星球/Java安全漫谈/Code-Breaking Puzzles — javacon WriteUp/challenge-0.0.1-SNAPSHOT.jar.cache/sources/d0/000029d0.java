package org.thymeleaf.templateparser.text;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateparser/text/TextParseStatus.class */
final class TextParseStatus {
    int offset;
    int line;
    int col;
    boolean inStructure;
    boolean inCommentLine;
    char literalMarker;
}