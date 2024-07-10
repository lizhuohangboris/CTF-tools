package org.attoparser;

import java.io.Reader;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/IMarkupParser.class */
public interface IMarkupParser {
    void parse(String str, IMarkupHandler iMarkupHandler) throws ParseException;

    void parse(char[] cArr, IMarkupHandler iMarkupHandler) throws ParseException;

    void parse(char[] cArr, int i, int i2, IMarkupHandler iMarkupHandler) throws ParseException;

    void parse(Reader reader, IMarkupHandler iMarkupHandler) throws ParseException;
}