package org.springframework.http.codec.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.async.ByteArrayFeeder;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.springframework.core.codec.DecodingException;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/codec/json/Jackson2Tokenizer.class */
final class Jackson2Tokenizer {
    private final JsonParser parser;
    private final boolean tokenizeArrayElements;
    private TokenBuffer tokenBuffer;
    private int objectDepth;
    private int arrayDepth;
    private final ByteArrayFeeder inputFeeder;

    private Jackson2Tokenizer(JsonParser parser, boolean tokenizeArrayElements) {
        Assert.notNull(parser, "'parser' must not be null");
        this.parser = parser;
        this.tokenizeArrayElements = tokenizeArrayElements;
        this.tokenBuffer = new TokenBuffer(parser);
        this.inputFeeder = (ByteArrayFeeder) this.parser.getNonBlockingInputFeeder();
    }

    public static Flux<TokenBuffer> tokenize(Flux<DataBuffer> dataBuffers, JsonFactory jsonFactory, boolean tokenizeArrayElements) {
        try {
            JsonParser parser = jsonFactory.createNonBlockingByteArrayParser();
            Jackson2Tokenizer tokenizer = new Jackson2Tokenizer(parser, tokenizeArrayElements);
            tokenizer.getClass();
            Function function = this::tokenize;
            Function function2 = Flux::error;
            tokenizer.getClass();
            return dataBuffers.flatMap(function, function2, this::endOfInput);
        } catch (IOException ex) {
            return Flux.error(ex);
        }
    }

    private Flux<TokenBuffer> tokenize(DataBuffer dataBuffer) {
        byte[] bytes = new byte[dataBuffer.readableByteCount()];
        dataBuffer.read(bytes);
        DataBufferUtils.release(dataBuffer);
        try {
            this.inputFeeder.feedInput(bytes, 0, bytes.length);
            return parseTokenBufferFlux();
        } catch (JsonProcessingException ex) {
            return Flux.error(new DecodingException("JSON decoding error: " + ex.getOriginalMessage(), ex));
        } catch (IOException ex2) {
            return Flux.error(ex2);
        }
    }

    private Flux<TokenBuffer> endOfInput() {
        this.inputFeeder.endOfInput();
        try {
            return parseTokenBufferFlux();
        } catch (JsonProcessingException ex) {
            return Flux.error(new DecodingException("JSON decoding error: " + ex.getOriginalMessage(), ex));
        } catch (IOException ex2) {
            return Flux.error(ex2);
        }
    }

    private Flux<TokenBuffer> parseTokenBufferFlux() throws IOException {
        List<TokenBuffer> result = new ArrayList<>();
        while (true) {
            JsonToken token = this.parser.nextToken();
            if (token == JsonToken.NOT_AVAILABLE) {
                break;
            }
            if (token == null) {
                JsonToken nextToken = this.parser.nextToken();
                token = nextToken;
                if (nextToken == null) {
                    break;
                }
            }
            updateDepth(token);
            if (!this.tokenizeArrayElements) {
                processTokenNormal(token, result);
            } else {
                processTokenArray(token, result);
            }
        }
        return Flux.fromIterable(result);
    }

    private void updateDepth(JsonToken token) {
        switch (token) {
            case START_OBJECT:
                this.objectDepth++;
                return;
            case END_OBJECT:
                this.objectDepth--;
                return;
            case START_ARRAY:
                this.arrayDepth++;
                return;
            case END_ARRAY:
                this.arrayDepth--;
                return;
            default:
                return;
        }
    }

    private void processTokenNormal(JsonToken token, List<TokenBuffer> result) throws IOException {
        this.tokenBuffer.copyCurrentEvent(this.parser);
        if ((token.isStructEnd() || token.isScalarValue()) && this.objectDepth == 0 && this.arrayDepth == 0) {
            result.add(this.tokenBuffer);
            this.tokenBuffer = new TokenBuffer(this.parser);
        }
    }

    private void processTokenArray(JsonToken token, List<TokenBuffer> result) throws IOException {
        if (!isTopLevelArrayToken(token)) {
            this.tokenBuffer.copyCurrentEvent(this.parser);
        }
        if (this.objectDepth == 0) {
            if (this.arrayDepth == 0 || this.arrayDepth == 1) {
                if (token == JsonToken.END_OBJECT || token.isScalarValue()) {
                    result.add(this.tokenBuffer);
                    this.tokenBuffer = new TokenBuffer(this.parser);
                }
            }
        }
    }

    private boolean isTopLevelArrayToken(JsonToken token) {
        return this.objectDepth == 0 && ((token == JsonToken.START_ARRAY && this.arrayDepth == 1) || (token == JsonToken.END_ARRAY && this.arrayDepth == 0));
    }
}