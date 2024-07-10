package org.apache.logging.log4j.message;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/message/MessageFactory2.class */
public interface MessageFactory2 extends MessageFactory {
    Message newMessage(CharSequence charSequence);

    Message newMessage(String str, Object obj);

    Message newMessage(String str, Object obj, Object obj2);

    Message newMessage(String str, Object obj, Object obj2, Object obj3);

    Message newMessage(String str, Object obj, Object obj2, Object obj3, Object obj4);

    Message newMessage(String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5);

    Message newMessage(String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6);

    Message newMessage(String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7);

    Message newMessage(String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8);

    Message newMessage(String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9);

    Message newMessage(String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10);
}