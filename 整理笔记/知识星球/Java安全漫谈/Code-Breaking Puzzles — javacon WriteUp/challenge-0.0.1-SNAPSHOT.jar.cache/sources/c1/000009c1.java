package org.apache.catalina.webresources;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.catalina.LifecycleException;
import org.apache.tomcat.util.compat.JrePlatform;
import org.apache.tomcat.util.http.RequestUtil;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/webresources/AbstractFileResourceSet.class */
public abstract class AbstractFileResourceSet extends AbstractResourceSet {
    protected static final String[] EMPTY_STRING_ARRAY = new String[0];
    private File fileBase;
    private String absoluteBase;
    private String canonicalBase;
    private boolean readOnly = false;

    protected abstract void checkType(File file);

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractFileResourceSet(String internalPath) {
        setInternalPath(internalPath);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final File getFileBase() {
        return this.fileBase;
    }

    @Override // org.apache.catalina.WebResourceSet
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    @Override // org.apache.catalina.WebResourceSet
    public boolean isReadOnly() {
        return this.readOnly;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final File file(String name, boolean mustExist) {
        if (name.equals("/")) {
            name = "";
        }
        File file = new File(this.fileBase, name);
        if (name.endsWith("/") && file.isFile()) {
            return null;
        }
        if (mustExist && !file.canRead()) {
            return null;
        }
        if (getRoot().getAllowLinking()) {
            return file;
        }
        if (JrePlatform.IS_WINDOWS && isInvalidWindowsFilename(name)) {
            return null;
        }
        String canPath = null;
        try {
            canPath = file.getCanonicalPath();
        } catch (IOException e) {
        }
        if (canPath == null || !canPath.startsWith(this.canonicalBase)) {
            return null;
        }
        String absPath = normalize(file.getAbsolutePath());
        if (this.absoluteBase.length() > absPath.length()) {
            return null;
        }
        String absPath2 = absPath.substring(this.absoluteBase.length());
        String canPath2 = canPath.substring(this.canonicalBase.length());
        if (canPath2.length() > 0) {
            canPath2 = normalize(canPath2);
        }
        if (!canPath2.equals(absPath2)) {
            return null;
        }
        return file;
    }

    private boolean isInvalidWindowsFilename(String name) {
        int len = name.length();
        if (len == 0) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            char c = name.charAt(i);
            if (c == '\"' || c == '<' || c == '>') {
                return true;
            }
        }
        if (name.charAt(len - 1) == ' ') {
            return true;
        }
        return false;
    }

    private String normalize(String path) {
        return RequestUtil.normalize(path, File.separatorChar == '\\');
    }

    @Override // org.apache.catalina.WebResourceSet
    public URL getBaseUrl() {
        try {
            return getFileBase().toURI().toURL();
        } catch (MalformedURLException e) {
            return null;
        }
    }

    @Override // org.apache.catalina.WebResourceSet
    public void gc() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.util.LifecycleBase
    public void initInternal() throws LifecycleException {
        this.fileBase = new File(getBase(), getInternalPath());
        checkType(this.fileBase);
        this.absoluteBase = normalize(this.fileBase.getAbsolutePath());
        try {
            this.canonicalBase = this.fileBase.getCanonicalPath();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}