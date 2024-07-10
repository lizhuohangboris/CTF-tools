package org.hibernate.validator.internal.engine.messageinterpolation.parser;

import java.lang.invoke.MethodHandles;
import org.hibernate.validator.internal.engine.messageinterpolation.InterpolationTermType;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/messageinterpolation/parser/MessageState.class */
public class MessageState implements ParserState {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());

    @Override // org.hibernate.validator.internal.engine.messageinterpolation.parser.ParserState
    public void terminate(TokenCollector tokenCollector) throws MessageDescriptorFormatException {
        tokenCollector.terminateToken();
    }

    @Override // org.hibernate.validator.internal.engine.messageinterpolation.parser.ParserState
    public void handleNonMetaCharacter(char character, TokenCollector tokenCollector) throws MessageDescriptorFormatException {
        tokenCollector.appendToToken(character);
    }

    @Override // org.hibernate.validator.internal.engine.messageinterpolation.parser.ParserState
    public void handleBeginTerm(char character, TokenCollector tokenCollector) throws MessageDescriptorFormatException {
        tokenCollector.terminateToken();
        tokenCollector.appendToToken(character);
        if (tokenCollector.getInterpolationType().equals(InterpolationTermType.PARAMETER)) {
            tokenCollector.makeParameterToken();
        }
        tokenCollector.transitionState(new InterpolationTermState());
    }

    @Override // org.hibernate.validator.internal.engine.messageinterpolation.parser.ParserState
    public void handleEndTerm(char character, TokenCollector tokenCollector) throws MessageDescriptorFormatException {
        throw LOG.getNonTerminatedParameterException(tokenCollector.getOriginalMessageDescriptor(), character);
    }

    @Override // org.hibernate.validator.internal.engine.messageinterpolation.parser.ParserState
    public void handleEscapeCharacter(char character, TokenCollector tokenCollector) throws MessageDescriptorFormatException {
        tokenCollector.appendToToken(character);
        tokenCollector.transitionState(new EscapedState(this));
    }

    @Override // org.hibernate.validator.internal.engine.messageinterpolation.parser.ParserState
    public void handleELDesignator(char character, TokenCollector tokenCollector) throws MessageDescriptorFormatException {
        if (tokenCollector.getInterpolationType().equals(InterpolationTermType.PARAMETER)) {
            handleNonMetaCharacter(character, tokenCollector);
        } else {
            tokenCollector.transitionState(new ELState());
        }
    }
}