package org.thymeleaf.templateparser.reader;

import java.io.IOException;
import java.io.Reader;
import org.thymeleaf.templateparser.reader.BlockAwareReader;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateparser/reader/ParserLevelCommentMarkupReader.class */
public final class ParserLevelCommentMarkupReader extends BlockAwareReader {
    private static final char[] PREFIX = "<!--/*".toCharArray();
    private static final char[] SUFFIX = "*/-->".toCharArray();

    @Override // org.thymeleaf.templateparser.reader.BlockAwareReader, java.io.Reader, java.io.Closeable, java.lang.AutoCloseable
    public /* bridge */ /* synthetic */ void close() throws IOException {
        super.close();
    }

    @Override // org.thymeleaf.templateparser.reader.BlockAwareReader, java.io.Reader
    public /* bridge */ /* synthetic */ int read(char[] cArr, int i, int i2) throws IOException {
        return super.read(cArr, i, i2);
    }

    public ParserLevelCommentMarkupReader(Reader reader) {
        super(reader, BlockAwareReader.BlockAction.DISCARD_ALL, PREFIX, SUFFIX);
    }
}