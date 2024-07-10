package org.springframework.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/util/DefaultPropertiesPersister.class */
public class DefaultPropertiesPersister implements PropertiesPersister {
    @Override // org.springframework.util.PropertiesPersister
    public void load(Properties props, InputStream is) throws IOException {
        props.load(is);
    }

    @Override // org.springframework.util.PropertiesPersister
    public void load(Properties props, Reader reader) throws IOException {
        props.load(reader);
    }

    @Override // org.springframework.util.PropertiesPersister
    public void store(Properties props, OutputStream os, String header) throws IOException {
        props.store(os, header);
    }

    @Override // org.springframework.util.PropertiesPersister
    public void store(Properties props, Writer writer, String header) throws IOException {
        props.store(writer, header);
    }

    @Override // org.springframework.util.PropertiesPersister
    public void loadFromXml(Properties props, InputStream is) throws IOException {
        props.loadFromXML(is);
    }

    @Override // org.springframework.util.PropertiesPersister
    public void storeToXml(Properties props, OutputStream os, String header) throws IOException {
        props.storeToXML(os, header);
    }

    @Override // org.springframework.util.PropertiesPersister
    public void storeToXml(Properties props, OutputStream os, String header, String encoding) throws IOException {
        props.storeToXML(os, header, encoding);
    }
}