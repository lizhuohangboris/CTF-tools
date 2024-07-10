package org.springframework.http.codec.xml;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/codec/xml/JaxbContextContainer.class */
final class JaxbContextContainer {
    private final ConcurrentMap<Class<?>, JAXBContext> jaxbContexts = new ConcurrentHashMap(64);

    public Marshaller createMarshaller(Class<?> clazz) throws JAXBException {
        JAXBContext jaxbContext = getJaxbContext(clazz);
        return jaxbContext.createMarshaller();
    }

    public Unmarshaller createUnmarshaller(Class<?> clazz) throws JAXBException {
        JAXBContext jaxbContext = getJaxbContext(clazz);
        return jaxbContext.createUnmarshaller();
    }

    private JAXBContext getJaxbContext(Class<?> clazz) throws JAXBException {
        Assert.notNull(clazz, "Class must not be null");
        JAXBContext jaxbContext = this.jaxbContexts.get(clazz);
        if (jaxbContext == null) {
            jaxbContext = JAXBContext.newInstance(new Class[]{clazz});
            this.jaxbContexts.putIfAbsent(clazz, jaxbContext);
        }
        return jaxbContext;
    }
}