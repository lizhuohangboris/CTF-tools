package org.hibernate.validator.internal.engine.messageinterpolation.parser;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/messageinterpolation/parser/EscapedState.class */
public class EscapedState implements ParserState {
    ParserState previousState;

    public EscapedState(ParserState previousState) {
        this.previousState = previousState;
    }

    @Override // org.hibernate.validator.internal.engine.messageinterpolation.parser.ParserState
    public void terminate(TokenCollector tokenCollector) throws MessageDescriptorFormatException {
        tokenCollector.terminateToken();
    }

    @Override // org.hibernate.validator.internal.engine.messageinterpolation.parser.ParserState
    public void handleNonMetaCharacter(char character, TokenCollector tokenCollector) throws MessageDescriptorFormatException {
        handleEscapedCharacter(character, tokenCollector);
    }

    @Override // org.hibernate.validator.internal.engine.messageinterpolation.parser.ParserState
    public void handleBeginTerm(char character, TokenCollector tokenCollector) throws MessageDescriptorFormatException {
        handleEscapedCharacter(character, tokenCollector);
    }

    @Override // org.hibernate.validator.internal.engine.messageinterpolation.parser.ParserState
    public void handleEndTerm(char character, TokenCollector tokenCollector) throws MessageDescriptorFormatException {
        handleEscapedCharacter(character, tokenCollector);
    }

    @Override // org.hibernate.validator.internal.engine.messageinterpolation.parser.ParserState
    public void handleEscapeCharacter(char character, TokenCollector tokenCollector) throws MessageDescriptorFormatException {
        handleEscapedCharacter(character, tokenCollector);
    }

    @Override // org.hibernate.validator.internal.engine.messageinterpolation.parser.ParserState
    public void handleELDesignator(char character, TokenCollector tokenCollector) throws MessageDescriptorFormatException {
        handleEscapedCharacter(character, tokenCollector);
    }

    private void handleEscapedCharacter(char character, TokenCollector tokenCollector) throws MessageDescriptorFormatException {
        tokenCollector.appendToToken(character);
        tokenCollector.transitionState(this.previousState);
    }
}