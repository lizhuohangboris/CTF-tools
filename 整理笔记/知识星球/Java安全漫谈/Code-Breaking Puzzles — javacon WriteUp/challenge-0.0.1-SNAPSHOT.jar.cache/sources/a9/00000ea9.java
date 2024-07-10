package org.attoparser.simple;

import java.io.Reader;
import org.attoparser.ParseException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/simple/ISimpleMarkupParser.class */
public interface ISimpleMarkupParser {
    void parse(String str, ISimpleMarkupHandler iSimpleMarkupHandler) throws ParseException;

    void parse(char[] cArr, ISimpleMarkupHandler iSimpleMarkupHandler) throws ParseException;

    void parse(char[] cArr, int i, int i2, ISimpleMarkupHandler iSimpleMarkupHandler) throws ParseException;

    void parse(Reader reader, ISimpleMarkupHandler iSimpleMarkupHandler) throws ParseException;
}