package org.apache.catalina.startup;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipException;
import org.apache.catalina.Host;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/startup/ExpandWar.class */
public class ExpandWar {
    private static final Log log = LogFactory.getLog(ExpandWar.class);
    protected static final StringManager sm = StringManager.getManager(Constants.Package);

    public static String expand(Host host, URL war, String pathname) throws IOException {
        JarURLConnection juc = (JarURLConnection) war.openConnection();
        juc.setUseCaches(false);
        URL jarFileUrl = juc.getJarFileURL();
        URLConnection jfuc = jarFileUrl.openConnection();
        boolean success = false;
        File docBase = new File(host.getAppBaseFile(), pathname);
        File warTracker = new File(host.getAppBaseFile(), pathname + Constants.WarTracker);
        InputStream is = jfuc.getInputStream();
        Throwable th = null;
        try {
            long warLastModified = jfuc.getLastModified();
            if (is != null) {
                if (0 != 0) {
                    try {
                        is.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                } else {
                    is.close();
                }
            }
            if (docBase.exists()) {
                if (!warTracker.exists() || warTracker.lastModified() == warLastModified) {
                    return docBase.getAbsolutePath();
                }
                log.info(sm.getString("expandWar.deleteOld", docBase));
                if (!delete(docBase)) {
                    throw new IOException(sm.getString("expandWar.deleteFailed", docBase));
                }
            }
            if (!docBase.mkdir() && !docBase.isDirectory()) {
                throw new IOException(sm.getString("expandWar.createFailed", docBase));
            }
            String canonicalDocBasePrefix = docBase.getCanonicalPath();
            if (!canonicalDocBasePrefix.endsWith(File.separator)) {
                canonicalDocBasePrefix = canonicalDocBasePrefix + File.separator;
            }
            File warTrackerParent = warTracker.getParentFile();
            if (!warTrackerParent.isDirectory() && !warTrackerParent.mkdirs()) {
                throw new IOException(sm.getString("expandWar.createFailed", warTrackerParent.getAbsolutePath()));
            }
            try {
                try {
                    JarFile jarFile = juc.getJarFile();
                    Throwable th3 = null;
                    try {
                        Enumeration<JarEntry> jarEntries = jarFile.entries();
                        while (jarEntries.hasMoreElements()) {
                            JarEntry jarEntry = jarEntries.nextElement();
                            String name = jarEntry.getName();
                            File expandedFile = new File(docBase, name);
                            if (!expandedFile.getCanonicalPath().startsWith(canonicalDocBasePrefix)) {
                                throw new IllegalArgumentException(sm.getString("expandWar.illegalPath", war, name, expandedFile.getCanonicalPath(), canonicalDocBasePrefix));
                            }
                            int last = name.lastIndexOf(47);
                            if (last >= 0) {
                                File parent = new File(docBase, name.substring(0, last));
                                if (!parent.mkdirs() && !parent.isDirectory()) {
                                    throw new IOException(sm.getString("expandWar.createFailed", parent));
                                }
                            }
                            if (!name.endsWith("/")) {
                                InputStream input = jarFile.getInputStream(jarEntry);
                                Throwable th4 = null;
                                if (null == input) {
                                    throw new ZipException(sm.getString("expandWar.missingJarEntry", jarEntry.getName()));
                                }
                                try {
                                    expand(input, expandedFile);
                                    long lastModified = jarEntry.getTime();
                                    if (lastModified != -1 && lastModified != 0 && !expandedFile.setLastModified(lastModified)) {
                                        throw new IOException(sm.getString("expandWar.lastModifiedFailed", expandedFile));
                                    }
                                    if (input != null) {
                                        if (0 != 0) {
                                            try {
                                                input.close();
                                            } catch (Throwable th5) {
                                                th4.addSuppressed(th5);
                                            }
                                        } else {
                                            input.close();
                                        }
                                    }
                                } finally {
                                }
                            }
                        }
                        if (!warTracker.createNewFile()) {
                            throw new IOException(sm.getString("expandWar.createFileFailed", warTracker));
                        }
                        if (!warTracker.setLastModified(warLastModified)) {
                            throw new IOException(sm.getString("expandWar.lastModifiedFailed", warTracker));
                        }
                        success = true;
                        if (jarFile != 0) {
                            if (0 != 0) {
                                try {
                                    jarFile.close();
                                } catch (Throwable th6) {
                                    th3.addSuppressed(th6);
                                }
                            } else {
                                jarFile.close();
                            }
                        }
                        return docBase.getAbsolutePath();
                    } finally {
                    }
                } catch (IOException e) {
                    throw e;
                }
            } finally {
                if (!success) {
                    deleteDir(docBase);
                }
            }
        } finally {
        }
    }

    public static void validate(Host host, URL war, String pathname) throws IOException {
        File docBase = new File(host.getAppBaseFile(), pathname);
        String canonicalDocBasePrefix = docBase.getCanonicalPath();
        if (!canonicalDocBasePrefix.endsWith(File.separator)) {
            canonicalDocBasePrefix = canonicalDocBasePrefix + File.separator;
        }
        JarURLConnection juc = (JarURLConnection) war.openConnection();
        juc.setUseCaches(false);
        try {
            JarFile jarFile = juc.getJarFile();
            Enumeration<JarEntry> jarEntries = jarFile.entries();
            while (jarEntries.hasMoreElements()) {
                JarEntry jarEntry = jarEntries.nextElement();
                String name = jarEntry.getName();
                File expandedFile = new File(docBase, name);
                if (!expandedFile.getCanonicalPath().startsWith(canonicalDocBasePrefix)) {
                    throw new IllegalArgumentException(sm.getString("expandWar.illegalPath", war, name, expandedFile.getCanonicalPath(), canonicalDocBasePrefix));
                }
            }
            if (jarFile != null) {
                if (0 != 0) {
                    jarFile.close();
                } else {
                    jarFile.close();
                }
            }
        } catch (IOException e) {
            throw e;
        }
    }

    public static boolean copy(File src, File dest) {
        String[] files;
        boolean result = true;
        if (src.isDirectory()) {
            files = src.list();
            result = dest.mkdir();
        } else {
            files = new String[]{""};
        }
        if (files == null) {
            files = new String[0];
        }
        for (int i = 0; i < files.length && result; i++) {
            File fileSrc = new File(src, files[i]);
            File fileDest = new File(dest, files[i]);
            if (fileSrc.isDirectory()) {
                result = copy(fileSrc, fileDest);
            } else {
                try {
                    FileChannel ic = new FileInputStream(fileSrc).getChannel();
                    FileChannel oc = new FileOutputStream(fileDest).getChannel();
                    Throwable th = null;
                    try {
                        ic.transferTo(0L, ic.size(), oc);
                        if (oc != null) {
                            if (0 != 0) {
                                try {
                                    oc.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            } else {
                                oc.close();
                            }
                        }
                        if (ic != 0) {
                            if (0 != 0) {
                                ic.close();
                            } else {
                                ic.close();
                            }
                        }
                    } catch (Throwable th3) {
                        try {
                            throw th3;
                            break;
                        } catch (Throwable th4) {
                            if (oc != null) {
                                if (th3 != null) {
                                    try {
                                        oc.close();
                                    } catch (Throwable th5) {
                                        th3.addSuppressed(th5);
                                    }
                                } else {
                                    oc.close();
                                }
                            }
                            throw th4;
                            break;
                        }
                    }
                } catch (IOException e) {
                    log.error(sm.getString("expandWar.copy", fileSrc, fileDest), e);
                    result = false;
                }
            }
        }
        return result;
    }

    public static boolean delete(File dir) {
        return delete(dir, true);
    }

    public static boolean delete(File dir, boolean logFailure) {
        boolean result;
        if (dir.isDirectory()) {
            result = deleteDir(dir, logFailure);
        } else if (dir.exists()) {
            result = dir.delete();
        } else {
            result = true;
        }
        if (logFailure && !result) {
            log.error(sm.getString("expandWar.deleteFailed", dir.getAbsolutePath()));
        }
        return result;
    }

    public static boolean deleteDir(File dir) {
        return deleteDir(dir, true);
    }

    public static boolean deleteDir(File dir, boolean logFailure) {
        boolean result;
        String[] files = dir.list();
        if (files == null) {
            files = new String[0];
        }
        for (String str : files) {
            File file = new File(dir, str);
            if (file.isDirectory()) {
                deleteDir(file, logFailure);
            } else {
                file.delete();
            }
        }
        if (dir.exists()) {
            result = dir.delete();
        } else {
            result = true;
        }
        if (logFailure && !result) {
            log.error(sm.getString("expandWar.deleteFailed", dir.getAbsolutePath()));
        }
        return result;
    }

    private static void expand(InputStream input, File file) throws IOException {
        BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file));
        Throwable th = null;
        try {
            byte[] buffer = new byte[2048];
            while (true) {
                int n = input.read(buffer);
                if (n <= 0) {
                    break;
                }
                output.write(buffer, 0, n);
            }
            if (output != null) {
                if (0 != 0) {
                    try {
                        output.close();
                        return;
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                        return;
                    }
                }
                output.close();
            }
        } catch (Throwable th3) {
            try {
                throw th3;
            } catch (Throwable th4) {
                if (output != null) {
                    if (th3 != null) {
                        try {
                            output.close();
                        } catch (Throwable th5) {
                            th3.addSuppressed(th5);
                        }
                    } else {
                        output.close();
                    }
                }
                throw th4;
            }
        }
    }
}