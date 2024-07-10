package org.apache.logging.log4j.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/util/PropertyFilePropertySource.class */
public class PropertyFilePropertySource extends PropertiesPropertySource {
    public PropertyFilePropertySource(String fileName) {
        super(loadPropertiesFile(fileName));
    }

    private static Properties loadPropertiesFile(String fileName) {
        Properties props = new Properties();
        for (URL url : LoaderUtil.findResources(fileName)) {
            try {
                InputStream in = url.openStream();
                props.load(in);
                if (in != null) {
                    if (0 != 0) {
                        in.close();
                    } else {
                        in.close();
                    }
                }
            } catch (IOException e) {
                LowLevelLogUtil.logException("Unable to read " + url, e);
            }
        }
        return props;
    }

    @Override // org.apache.logging.log4j.util.PropertiesPropertySource, org.apache.logging.log4j.util.PropertySource
    public int getPriority() {
        return 0;
    }
}