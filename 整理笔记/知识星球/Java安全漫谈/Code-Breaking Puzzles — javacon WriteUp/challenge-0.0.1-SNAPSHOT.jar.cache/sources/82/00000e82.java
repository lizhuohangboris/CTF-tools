package org.attoparser.dom;

import java.io.Reader;
import org.attoparser.ParseException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/dom/IDOMMarkupParser.class */
public interface IDOMMarkupParser {
    Document parse(String str) throws ParseException;

    Document parse(char[] cArr) throws ParseException;

    Document parse(char[] cArr, int i, int i2) throws ParseException;

    Document parse(Reader reader) throws ParseException;

    Document parse(String str, String str2) throws ParseException;

    Document parse(String str, char[] cArr) throws ParseException;

    Document parse(String str, char[] cArr, int i, int i2) throws ParseException;

    Document parse(String str, Reader reader) throws ParseException;
}