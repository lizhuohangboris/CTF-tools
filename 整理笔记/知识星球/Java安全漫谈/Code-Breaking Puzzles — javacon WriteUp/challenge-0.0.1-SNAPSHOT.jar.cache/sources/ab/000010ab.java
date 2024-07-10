package org.hibernate.validator.internal.engine.messageinterpolation.parser;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/messageinterpolation/parser/ParserState.class */
public interface ParserState {
    void terminate(TokenCollector tokenCollector) throws MessageDescriptorFormatException;

    void handleNonMetaCharacter(char c, TokenCollector tokenCollector) throws MessageDescriptorFormatException;

    void handleBeginTerm(char c, TokenCollector tokenCollector) throws MessageDescriptorFormatException;

    void handleEndTerm(char c, TokenCollector tokenCollector) throws MessageDescriptorFormatException;

    void handleEscapeCharacter(char c, TokenCollector tokenCollector) throws MessageDescriptorFormatException;

    void handleELDesignator(char c, TokenCollector tokenCollector) throws MessageDescriptorFormatException;
}