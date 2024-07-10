package org.apache.logging.log4j.message;

import java.io.Serializable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/message/AbstractMessageFactory.class */
public abstract class AbstractMessageFactory implements MessageFactory2, Serializable {
    private static final long serialVersionUID = -1307891137684031187L;

    @Override // org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(CharSequence message) {
        return new SimpleMessage(message);
    }

    @Override // org.apache.logging.log4j.message.MessageFactory
    public Message newMessage(Object message) {
        return new ObjectMessage(message);
    }

    @Override // org.apache.logging.log4j.message.MessageFactory
    public Message newMessage(String message) {
        return new SimpleMessage(message);
    }

    @Override // org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(String message, Object p0) {
        return newMessage(message, p0);
    }

    @Override // org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(String message, Object p0, Object p1) {
        return newMessage(message, p0, p1);
    }

    @Override // org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(String message, Object p0, Object p1, Object p2) {
        return newMessage(message, p0, p1, p2);
    }

    @Override // org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(String message, Object p0, Object p1, Object p2, Object p3) {
        return newMessage(message, p0, p1, p2, p3);
    }

    @Override // org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
        return newMessage(message, p0, p1, p2, p3, p4);
    }

    @Override // org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        return newMessage(message, p0, p1, p2, p3, p4, p5);
    }

    @Override // org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        return newMessage(message, p0, p1, p2, p3, p4, p5, p6);
    }

    @Override // org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        return newMessage(message, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    @Override // org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        return newMessage(message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    @Override // org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        return newMessage(message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }
}