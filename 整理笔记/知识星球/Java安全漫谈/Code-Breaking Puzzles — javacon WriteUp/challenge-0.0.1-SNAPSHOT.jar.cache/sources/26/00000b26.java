package org.apache.logging.log4j.message;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/message/MessageFormatMessageFactory.class */
public class MessageFormatMessageFactory extends AbstractMessageFactory {
    private static final long serialVersionUID = 3584821740584192453L;

    @Override // org.apache.logging.log4j.message.MessageFactory
    public Message newMessage(String message, Object... params) {
        return new MessageFormatMessage(message, params);
    }

    @Override // org.apache.logging.log4j.message.AbstractMessageFactory, org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(String message, Object p0) {
        return new MessageFormatMessage(message, p0);
    }

    @Override // org.apache.logging.log4j.message.AbstractMessageFactory, org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(String message, Object p0, Object p1) {
        return new MessageFormatMessage(message, p0, p1);
    }

    @Override // org.apache.logging.log4j.message.AbstractMessageFactory, org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(String message, Object p0, Object p1, Object p2) {
        return new MessageFormatMessage(message, p0, p1, p2);
    }

    @Override // org.apache.logging.log4j.message.AbstractMessageFactory, org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(String message, Object p0, Object p1, Object p2, Object p3) {
        return new MessageFormatMessage(message, p0, p1, p2, p3);
    }

    @Override // org.apache.logging.log4j.message.AbstractMessageFactory, org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
        return new MessageFormatMessage(message, p0, p1, p2, p3, p4);
    }

    @Override // org.apache.logging.log4j.message.AbstractMessageFactory, org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        return new MessageFormatMessage(message, p0, p1, p2, p3, p4, p5);
    }

    @Override // org.apache.logging.log4j.message.AbstractMessageFactory, org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        return new MessageFormatMessage(message, p0, p1, p2, p3, p4, p5, p6);
    }

    @Override // org.apache.logging.log4j.message.AbstractMessageFactory, org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        return new MessageFormatMessage(message, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    @Override // org.apache.logging.log4j.message.AbstractMessageFactory, org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        return new MessageFormatMessage(message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    @Override // org.apache.logging.log4j.message.AbstractMessageFactory, org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        return new MessageFormatMessage(message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }
}