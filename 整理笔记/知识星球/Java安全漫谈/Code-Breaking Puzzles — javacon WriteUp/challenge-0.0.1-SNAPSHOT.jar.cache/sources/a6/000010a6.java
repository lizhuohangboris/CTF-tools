package org.hibernate.validator.internal.engine.messageinterpolation.parser;

import java.lang.invoke.MethodHandles;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/messageinterpolation/parser/ELState.class */
public class ELState implements ParserState {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());

    @Override // org.hibernate.validator.internal.engine.messageinterpolation.parser.ParserState
    public void terminate(TokenCollector tokenCollector) throws MessageDescriptorFormatException {
        tokenCollector.appendToToken('$');
        tokenCollector.terminateToken();
    }

    @Override // org.hibernate.validator.internal.engine.messageinterpolation.parser.ParserState
    public void handleNonMetaCharacter(char character, TokenCollector tokenCollector) throws MessageDescriptorFormatException {
        tokenCollector.appendToToken('$');
        tokenCollector.appendToToken(character);
        tokenCollector.terminateToken();
        tokenCollector.transitionState(new MessageState());
    }

    @Override // org.hibernate.validator.internal.engine.messageinterpolation.parser.ParserState
    public void handleBeginTerm(char character, TokenCollector tokenCollector) throws MessageDescriptorFormatException {
        tokenCollector.terminateToken();
        tokenCollector.appendToToken('$');
        tokenCollector.appendToToken(character);
        tokenCollector.makeELToken();
        tokenCollector.transitionState(new InterpolationTermState());
    }

    @Override // org.hibernate.validator.internal.engine.messageinterpolation.parser.ParserState
    public void handleEndTerm(char character, TokenCollector tokenCollector) throws MessageDescriptorFormatException {
        throw LOG.getNonTerminatedParameterException(tokenCollector.getOriginalMessageDescriptor(), character);
    }

    @Override // org.hibernate.validator.internal.engine.messageinterpolation.parser.ParserState
    public void handleEscapeCharacter(char character, TokenCollector tokenCollector) throws MessageDescriptorFormatException {
        tokenCollector.transitionState(new EscapedState(this));
    }

    @Override // org.hibernate.validator.internal.engine.messageinterpolation.parser.ParserState
    public void handleELDesignator(char character, TokenCollector tokenCollector) throws MessageDescriptorFormatException {
        handleNonMetaCharacter(character, tokenCollector);
    }
}