package org.thymeleaf.templateparser.reader;

import java.io.IOException;
import java.io.Reader;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateparser/reader/BlockAwareReader.class */
abstract class BlockAwareReader extends Reader {
    private final Reader reader;
    private final BlockAction action;
    private final char[] prefix;
    private final char[] suffix;
    private final char p0;
    private final char s0;
    private char[] overflowBuffer = null;
    private int overflowBufferLen = 0;
    private boolean insideComment = false;
    private int index = 0;
    private int discardFrom = -1;

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateparser/reader/BlockAwareReader$BlockAction.class */
    public enum BlockAction {
        DISCARD_ALL,
        DISCARD_CONTAINER
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public BlockAwareReader(Reader reader, BlockAction action, char[] prefix, char[] suffix) {
        this.reader = reader;
        this.action = action;
        this.prefix = prefix;
        this.suffix = suffix;
        this.p0 = this.prefix[0];
        this.s0 = this.suffix[0];
    }

    @Override // java.io.Reader
    public int read(char[] cbuf, int off, int len) throws IOException {
        int read = readBytes(cbuf, off, len);
        if (read <= 0) {
            if (read < 0 && this.insideComment) {
                throw new IOException("Unfinished block structure " + new String(this.prefix) + "..." + new String(this.suffix));
            }
            return read;
        }
        this.discardFrom = this.discardFrom < 0 ? this.discardFrom : Math.max(off, this.discardFrom);
        int maxi = off + read;
        int i = off;
        while (i < maxi) {
            int i2 = i;
            i++;
            char c = cbuf[i2];
            if (this.index != 0 || c == this.p0 || c == this.s0) {
                if (!this.insideComment) {
                    if (c == this.prefix[this.index]) {
                        this.index++;
                        if (this.index == this.prefix.length) {
                            if (i < maxi) {
                                System.arraycopy(cbuf, i, cbuf, i - this.prefix.length, maxi - i);
                            }
                            this.insideComment = true;
                            this.index = 0;
                            read -= this.prefix.length;
                            maxi -= this.prefix.length;
                            i -= this.prefix.length;
                            this.discardFrom = this.action == BlockAction.DISCARD_ALL ? i : -1;
                        }
                    } else {
                        if (this.index > 0) {
                            i -= this.index;
                        }
                        this.index = 0;
                    }
                } else if (c == this.suffix[this.index]) {
                    this.index++;
                    if (this.index == this.suffix.length) {
                        if (i < maxi) {
                            System.arraycopy(cbuf, i, cbuf, i - this.suffix.length, maxi - i);
                        }
                        this.insideComment = false;
                        this.index = 0;
                        read -= this.suffix.length;
                        maxi -= this.suffix.length;
                        i -= this.suffix.length;
                        if (this.discardFrom >= 0) {
                            if (i < maxi) {
                                System.arraycopy(cbuf, i, cbuf, this.discardFrom, maxi - i);
                            }
                            read -= i - this.discardFrom;
                            maxi -= i - this.discardFrom;
                            i = this.discardFrom;
                            this.discardFrom = -1;
                        }
                    }
                } else {
                    if (this.index > 0) {
                        i -= this.index;
                    }
                    this.index = 0;
                }
            }
        }
        if (this.index > 0) {
            overflowLastBytes(cbuf, maxi, this.index);
            read -= this.index;
            maxi -= this.index;
            char[] structure = this.insideComment ? this.suffix : this.prefix;
            if (matchOverflow(structure)) {
                this.insideComment = !this.insideComment;
                this.overflowBufferLen -= structure.length;
                this.index = 0;
            } else {
                System.arraycopy(this.overflowBuffer, 0, cbuf, maxi, 1);
                read++;
                maxi++;
                System.arraycopy(this.overflowBuffer, 1, this.overflowBuffer, 0, this.overflowBufferLen - 1);
                this.overflowBufferLen--;
                this.index = 0;
            }
        }
        if (this.discardFrom >= 0) {
            read -= maxi - this.discardFrom;
            this.discardFrom = 0;
        }
        this.discardFrom = (this.insideComment && this.action == BlockAction.DISCARD_ALL) ? 0 : -1;
        return read;
    }

    private int readBytes(char[] buffer, int off, int len) throws IOException {
        int delegateRead;
        if (len == 0) {
            return 0;
        }
        if (this.overflowBufferLen == 0) {
            return this.reader.read(buffer, off, len);
        }
        if (this.overflowBufferLen <= len) {
            System.arraycopy(this.overflowBuffer, 0, buffer, off, this.overflowBufferLen);
            int read = this.overflowBufferLen;
            this.overflowBufferLen = 0;
            if (read < len && (delegateRead = this.reader.read(buffer, off + read, len - read)) > 0) {
                read += delegateRead;
            }
            return read;
        }
        System.arraycopy(this.overflowBuffer, 0, buffer, off, len);
        if (len < this.overflowBufferLen) {
            System.arraycopy(this.overflowBuffer, len, this.overflowBuffer, 0, this.overflowBufferLen - len);
        }
        this.overflowBufferLen -= len;
        return len;
    }

    private void overflowLastBytes(char[] buffer, int maxi, int overflowCount) {
        if (this.overflowBuffer == null) {
            this.overflowBuffer = new char[Math.max(this.prefix.length, this.suffix.length)];
        }
        if (this.overflowBufferLen > 0) {
            System.arraycopy(this.overflowBuffer, 0, this.overflowBuffer, overflowCount, this.overflowBufferLen);
        }
        System.arraycopy(buffer, maxi - overflowCount, this.overflowBuffer, 0, overflowCount);
        this.overflowBufferLen += overflowCount;
    }

    private boolean matchOverflow(char[] structure) throws IOException {
        if (this.overflowBufferLen > 0) {
            for (int i = 0; i < this.overflowBufferLen; i++) {
                if (this.overflowBuffer[i] != structure[i]) {
                    return false;
                }
            }
        }
        int overflowRead = 0;
        while (overflowRead >= 0 && this.overflowBufferLen < structure.length) {
            overflowRead = this.reader.read(this.overflowBuffer, this.overflowBufferLen, 1);
            if (overflowRead > 0) {
                this.overflowBufferLen++;
                if (this.overflowBuffer[this.overflowBufferLen - 1] != structure[this.overflowBufferLen - 1]) {
                    return false;
                }
            }
        }
        return this.overflowBufferLen == structure.length;
    }

    @Override // java.io.Reader, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.reader.close();
    }
}