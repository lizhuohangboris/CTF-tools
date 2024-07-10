package ch.qos.logback.core.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.ArrayList;
import java.util.List;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/net/HardenedObjectInputStream.class */
public class HardenedObjectInputStream extends ObjectInputStream {
    final List<String> whitelistedClassNames;
    static final String[] JAVA_PACKAGES = {"java.lang", "java.util"};

    public HardenedObjectInputStream(InputStream in, String[] whilelist) throws IOException {
        super(in);
        this.whitelistedClassNames = new ArrayList();
        if (whilelist != null) {
            for (String str : whilelist) {
                this.whitelistedClassNames.add(str);
            }
        }
    }

    public HardenedObjectInputStream(InputStream in, List<String> whitelist) throws IOException {
        super(in);
        this.whitelistedClassNames = new ArrayList();
        this.whitelistedClassNames.addAll(whitelist);
    }

    @Override // java.io.ObjectInputStream
    protected Class<?> resolveClass(ObjectStreamClass anObjectStreamClass) throws IOException, ClassNotFoundException {
        String incomingClassName = anObjectStreamClass.getName();
        if (!isWhitelisted(incomingClassName)) {
            throw new InvalidClassException("Unauthorized deserialization attempt", anObjectStreamClass.getName());
        }
        return super.resolveClass(anObjectStreamClass);
    }

    private boolean isWhitelisted(String incomingClassName) {
        for (int i = 0; i < JAVA_PACKAGES.length; i++) {
            if (incomingClassName.startsWith(JAVA_PACKAGES[i])) {
                return true;
            }
        }
        for (String whiteListed : this.whitelistedClassNames) {
            if (incomingClassName.equals(whiteListed)) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void addToWhitelist(List<String> additionalAuthorizedClasses) {
        this.whitelistedClassNames.addAll(additionalAuthorizedClasses);
    }
}