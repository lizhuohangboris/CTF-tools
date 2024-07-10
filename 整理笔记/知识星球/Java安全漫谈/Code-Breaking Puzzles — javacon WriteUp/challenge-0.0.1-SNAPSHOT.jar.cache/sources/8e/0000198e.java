package org.springframework.boot.env;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.PropertyAccessor;
import org.springframework.boot.origin.Origin;
import org.springframework.boot.origin.OriginTrackedValue;
import org.springframework.boot.origin.TextResourceOrigin;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/env/OriginTrackedPropertiesLoader.class */
class OriginTrackedPropertiesLoader {
    private final Resource resource;

    /* JADX INFO: Access modifiers changed from: package-private */
    public OriginTrackedPropertiesLoader(Resource resource) {
        Assert.notNull(resource, "Resource must not be null");
        this.resource = resource;
    }

    public Map<String, OriginTrackedValue> load() throws IOException {
        return load(true);
    }

    public Map<String, OriginTrackedValue> load(boolean expandLists) throws IOException {
        CharacterReader reader = new CharacterReader(this.resource);
        Throwable th = null;
        try {
            Map<String, OriginTrackedValue> result = new LinkedHashMap<>();
            StringBuilder buffer = new StringBuilder();
            while (reader.read()) {
                String key = loadKey(buffer, reader).trim();
                if (expandLists && key.endsWith(ClassUtils.ARRAY_SUFFIX)) {
                    String key2 = key.substring(0, key.length() - 2);
                    int index = 0;
                    do {
                        OriginTrackedValue value = loadValue(buffer, reader, true);
                        int i = index;
                        index++;
                        put(result, key2 + PropertyAccessor.PROPERTY_KEY_PREFIX + i + "]", value);
                        if (!reader.isEndOfLine()) {
                            reader.read();
                        }
                    } while (!reader.isEndOfLine());
                } else {
                    OriginTrackedValue value2 = loadValue(buffer, reader, false);
                    put(result, key, value2);
                }
            }
            if (reader != null) {
                if (0 != 0) {
                    try {
                        reader.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                } else {
                    reader.close();
                }
            }
            return result;
        } finally {
        }
    }

    private void put(Map<String, OriginTrackedValue> result, String key, OriginTrackedValue value) {
        if (!key.isEmpty()) {
            result.put(key, value);
        }
    }

    private String loadKey(StringBuilder buffer, CharacterReader reader) throws IOException {
        buffer.setLength(0);
        boolean previousWhitespace = false;
        while (!reader.isEndOfLine()) {
            if (reader.isPropertyDelimiter()) {
                reader.read();
                return buffer.toString();
            } else if (!reader.isWhiteSpace() && previousWhitespace) {
                return buffer.toString();
            } else {
                previousWhitespace = reader.isWhiteSpace();
                buffer.append(reader.getCharacter());
                reader.read();
            }
        }
        return buffer.toString();
    }

    private OriginTrackedValue loadValue(StringBuilder buffer, CharacterReader reader, boolean splitLists) throws IOException {
        buffer.setLength(0);
        while (reader.isWhiteSpace() && !reader.isEndOfLine()) {
            reader.read();
        }
        TextResourceOrigin.Location location = reader.getLocation();
        while (!reader.isEndOfLine() && (!splitLists || !reader.isListDelimiter())) {
            buffer.append(reader.getCharacter());
            reader.read();
        }
        Origin origin = new TextResourceOrigin(this.resource, location);
        return OriginTrackedValue.of(buffer.toString(), origin);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/env/OriginTrackedPropertiesLoader$CharacterReader.class */
    public static class CharacterReader implements Closeable {
        private static final String[] ESCAPES = {"trnf", "\t\r\n\f"};
        private final LineNumberReader reader;
        private int columnNumber = -1;
        private boolean escaped;
        private int character;

        CharacterReader(Resource resource) throws IOException {
            this.reader = new LineNumberReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.ISO_8859_1));
        }

        @Override // java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            this.reader.close();
        }

        public boolean read() throws IOException {
            return read(false);
        }

        public boolean read(boolean wrappedLine) throws IOException {
            this.escaped = false;
            this.character = this.reader.read();
            this.columnNumber++;
            if (this.columnNumber == 0) {
                skipLeadingWhitespace();
                if (!wrappedLine) {
                    skipComment();
                }
            }
            if (this.character == 92) {
                this.escaped = true;
                readEscaped();
            } else if (this.character == 10) {
                this.columnNumber = -1;
            }
            return !isEndOfFile();
        }

        private void skipLeadingWhitespace() throws IOException {
            while (isWhiteSpace()) {
                this.character = this.reader.read();
                this.columnNumber++;
            }
        }

        private void skipComment() throws IOException {
            if (this.character == 35 || this.character == 33) {
                while (this.character != 10 && this.character != -1) {
                    this.character = this.reader.read();
                }
                this.columnNumber = -1;
                read();
            }
        }

        private void readEscaped() throws IOException {
            this.character = this.reader.read();
            int escapeIndex = ESCAPES[0].indexOf(this.character);
            if (escapeIndex != -1) {
                this.character = ESCAPES[1].charAt(escapeIndex);
            } else if (this.character == 10) {
                this.columnNumber = -1;
                read(true);
            } else if (this.character == 117) {
                readUnicode();
            }
        }

        private void readUnicode() throws IOException {
            this.character = 0;
            for (int i = 0; i < 4; i++) {
                int digit = this.reader.read();
                if (digit >= 48 && digit <= 57) {
                    this.character = ((this.character << 4) + digit) - 48;
                } else if (digit >= 97 && digit <= 102) {
                    this.character = (((this.character << 4) + digit) - 97) + 10;
                } else if (digit >= 65 && digit <= 70) {
                    this.character = (((this.character << 4) + digit) - 65) + 10;
                } else {
                    throw new IllegalStateException("Malformed \\uxxxx encoding.");
                }
            }
        }

        public boolean isWhiteSpace() {
            return !this.escaped && (this.character == 32 || this.character == 9 || this.character == 12);
        }

        public boolean isEndOfFile() {
            return this.character == -1;
        }

        public boolean isEndOfLine() {
            return this.character == -1 || (!this.escaped && this.character == 10);
        }

        public boolean isListDelimiter() {
            return !this.escaped && this.character == 44;
        }

        public boolean isPropertyDelimiter() {
            return !this.escaped && (this.character == 61 || this.character == 58);
        }

        public char getCharacter() {
            return (char) this.character;
        }

        public TextResourceOrigin.Location getLocation() {
            return new TextResourceOrigin.Location(this.reader.getLineNumber(), this.columnNumber);
        }
    }
}