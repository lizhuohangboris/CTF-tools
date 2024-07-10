package org.springframework.core.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.NoSuchFileException;
import java.nio.file.StandardOpenOption;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.support.WebContentGenerator;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/io/AbstractFileResolvingResource.class */
public abstract class AbstractFileResolvingResource extends AbstractResource {
    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public boolean exists() {
        try {
            URL url = getURL();
            if (ResourceUtils.isFileURL(url)) {
                return getFile().exists();
            }
            URLConnection con = url.openConnection();
            customizeConnection(con);
            HttpURLConnection httpCon = con instanceof HttpURLConnection ? (HttpURLConnection) con : null;
            if (httpCon != null) {
                int code = httpCon.getResponseCode();
                if (code == 200) {
                    return true;
                }
                if (code == 404) {
                    return false;
                }
            }
            if (con.getContentLengthLong() > 0) {
                return true;
            }
            if (httpCon != null) {
                httpCon.disconnect();
                return false;
            }
            getInputStream().close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public boolean isReadable() {
        try {
            URL url = getURL();
            if (ResourceUtils.isFileURL(url)) {
                File file = getFile();
                return file.canRead() && !file.isDirectory();
            }
            URLConnection con = url.openConnection();
            customizeConnection(con);
            if (con instanceof HttpURLConnection) {
                HttpURLConnection httpCon = (HttpURLConnection) con;
                int code = httpCon.getResponseCode();
                if (code != 200) {
                    httpCon.disconnect();
                    return false;
                }
            }
            long contentLength = con.getContentLengthLong();
            if (contentLength > 0) {
                return true;
            }
            if (contentLength == 0) {
                return false;
            }
            getInputStream().close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public boolean isFile() {
        try {
            URL url = getURL();
            if (url.getProtocol().startsWith(ResourceUtils.URL_PROTOCOL_VFS)) {
                return VfsResourceDelegate.getResource(url).isFile();
            }
            return "file".equals(url.getProtocol());
        } catch (IOException e) {
            return false;
        }
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public File getFile() throws IOException {
        URL url = getURL();
        if (url.getProtocol().startsWith(ResourceUtils.URL_PROTOCOL_VFS)) {
            return VfsResourceDelegate.getResource(url).getFile();
        }
        return ResourceUtils.getFile(url, getDescription());
    }

    @Override // org.springframework.core.io.AbstractResource
    protected File getFileForLastModifiedCheck() throws IOException {
        URL url = getURL();
        if (ResourceUtils.isJarURL(url)) {
            URL actualUrl = ResourceUtils.extractArchiveURL(url);
            if (actualUrl.getProtocol().startsWith(ResourceUtils.URL_PROTOCOL_VFS)) {
                return VfsResourceDelegate.getResource(actualUrl).getFile();
            }
            return ResourceUtils.getFile(actualUrl, "Jar URL");
        }
        return getFile();
    }

    public boolean isFile(URI uri) {
        try {
            if (uri.getScheme().startsWith(ResourceUtils.URL_PROTOCOL_VFS)) {
                return VfsResourceDelegate.getResource(uri).isFile();
            }
            return "file".equals(uri.getScheme());
        } catch (IOException e) {
            return false;
        }
    }

    public File getFile(URI uri) throws IOException {
        if (uri.getScheme().startsWith(ResourceUtils.URL_PROTOCOL_VFS)) {
            return VfsResourceDelegate.getResource(uri).getFile();
        }
        return ResourceUtils.getFile(uri, getDescription());
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public ReadableByteChannel readableChannel() throws IOException {
        try {
            return FileChannel.open(getFile().toPath(), StandardOpenOption.READ);
        } catch (FileNotFoundException | NoSuchFileException e) {
            return super.readableChannel();
        }
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public long contentLength() throws IOException {
        URL url = getURL();
        if (ResourceUtils.isFileURL(url)) {
            File file = getFile();
            long length = file.length();
            if (length == 0 && !file.exists()) {
                throw new FileNotFoundException(getDescription() + " cannot be resolved in the file system for checking its content length");
            }
            return length;
        }
        URLConnection con = url.openConnection();
        customizeConnection(con);
        return con.getContentLengthLong();
    }

    /* JADX WARN: Code restructure failed: missing block: B:37:0x002d, code lost:
        if (r0.exists() != false) goto L23;
     */
    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public long lastModified() throws java.io.IOException {
        /*
            r5 = this;
            r0 = r5
            java.net.URL r0 = r0.getURL()
            r6 = r0
            r0 = 0
            r7 = r0
            r0 = r6
            boolean r0 = org.springframework.util.ResourceUtils.isFileURL(r0)
            if (r0 != 0) goto L15
            r0 = r6
            boolean r0 = org.springframework.util.ResourceUtils.isJarURL(r0)
            if (r0 == 0) goto L37
        L15:
            r0 = 1
            r7 = r0
            r0 = r5
            java.io.File r0 = r0.getFileForLastModifiedCheck()     // Catch: java.io.FileNotFoundException -> L36
            r8 = r0
            r0 = r8
            long r0 = r0.lastModified()     // Catch: java.io.FileNotFoundException -> L36
            r9 = r0
            r0 = r9
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 > 0) goto L30
            r0 = r8
            boolean r0 = r0.exists()     // Catch: java.io.FileNotFoundException -> L36
            if (r0 == 0) goto L33
        L30:
            r0 = r9
            return r0
        L33:
            goto L37
        L36:
            r8 = move-exception
        L37:
            r0 = r6
            java.net.URLConnection r0 = r0.openConnection()
            r8 = r0
            r0 = r5
            r1 = r8
            r0.customizeConnection(r1)
            r0 = r8
            long r0 = r0.getLastModified()
            r9 = r0
            r0 = r7
            if (r0 == 0) goto L79
            r0 = r9
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 != 0) goto L79
            r0 = r8
            long r0 = r0.getContentLengthLong()
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 > 0) goto L79
            java.io.FileNotFoundException r0 = new java.io.FileNotFoundException
            r1 = r0
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r3 = r2
            r3.<init>()
            r3 = r5
            java.lang.String r3 = r3.getDescription()
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.String r3 = " cannot be resolved in the file system for checking its last-modified timestamp"
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.String r2 = r2.toString()
            r1.<init>(r2)
            throw r0
        L79:
            r0 = r9
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.core.io.AbstractFileResolvingResource.lastModified():long");
    }

    protected void customizeConnection(URLConnection con) throws IOException {
        ResourceUtils.useCachesIfNecessary(con);
        if (con instanceof HttpURLConnection) {
            customizeConnection((HttpURLConnection) con);
        }
    }

    protected void customizeConnection(HttpURLConnection con) throws IOException {
        con.setRequestMethod(WebContentGenerator.METHOD_HEAD);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/io/AbstractFileResolvingResource$VfsResourceDelegate.class */
    public static class VfsResourceDelegate {
        private VfsResourceDelegate() {
        }

        public static Resource getResource(URL url) throws IOException {
            return new VfsResource(VfsUtils.getRoot(url));
        }

        public static Resource getResource(URI uri) throws IOException {
            return new VfsResource(VfsUtils.getRoot(uri));
        }
    }
}