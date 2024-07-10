package org.hibernate.validator.internal.engine.messageinterpolation.parser;

import java.util.Collections;
import java.util.List;
import org.hibernate.validator.internal.engine.messageinterpolation.InterpolationTermType;
import org.hibernate.validator.internal.util.CollectionHelper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/messageinterpolation/parser/TokenCollector.class */
public class TokenCollector {
    private final String originalMessageDescriptor;
    private final InterpolationTermType interpolationTermType;
    private int currentPosition;
    private Token currentToken;
    private ParserState currentParserState = new MessageState();
    private final List<Token> tokenList = CollectionHelper.newArrayList();

    public TokenCollector(String originalMessageDescriptor, InterpolationTermType interpolationTermType) throws MessageDescriptorFormatException {
        this.originalMessageDescriptor = originalMessageDescriptor;
        this.interpolationTermType = interpolationTermType;
        parse();
    }

    public void terminateToken() {
        if (this.currentToken == null) {
            return;
        }
        this.currentToken.terminate();
        this.tokenList.add(this.currentToken);
        this.currentToken = null;
    }

    public void appendToToken(char character) {
        if (this.currentToken == null) {
            this.currentToken = new Token(character);
        } else {
            this.currentToken.append(character);
        }
    }

    public void makeParameterToken() {
        this.currentToken.makeParameterToken();
    }

    public void makeELToken() {
        this.currentToken.makeELToken();
    }

    private void next() throws MessageDescriptorFormatException {
        if (this.currentPosition == this.originalMessageDescriptor.length()) {
            this.currentParserState.terminate(this);
            this.currentPosition++;
            return;
        }
        char currentCharacter = this.originalMessageDescriptor.charAt(this.currentPosition);
        this.currentPosition++;
        switch (currentCharacter) {
            case '$':
                this.currentParserState.handleELDesignator(currentCharacter, this);
                return;
            case '\\':
                this.currentParserState.handleEscapeCharacter(currentCharacter, this);
                return;
            case '{':
                this.currentParserState.handleBeginTerm(currentCharacter, this);
                return;
            case '}':
                this.currentParserState.handleEndTerm(currentCharacter, this);
                return;
            default:
                this.currentParserState.handleNonMetaCharacter(currentCharacter, this);
                return;
        }
    }

    public final void parse() throws MessageDescriptorFormatException {
        while (this.currentPosition <= this.originalMessageDescriptor.length()) {
            next();
        }
    }

    public void transitionState(ParserState newState) {
        this.currentParserState = newState;
    }

    public InterpolationTermType getInterpolationType() {
        return this.interpolationTermType;
    }

    public List<Token> getTokenList() {
        return Collections.unmodifiableList(this.tokenList);
    }

    public String getOriginalMessageDescriptor() {
        return this.originalMessageDescriptor;
    }
}