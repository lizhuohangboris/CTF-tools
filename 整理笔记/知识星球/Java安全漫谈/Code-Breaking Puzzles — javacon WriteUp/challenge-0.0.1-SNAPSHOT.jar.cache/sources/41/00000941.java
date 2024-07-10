package org.apache.catalina.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import org.apache.catalina.Context;
import org.apache.catalina.WebResource;
import org.apache.catalina.WebResourceRoot;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/util/ExtensionValidator.class */
public final class ExtensionValidator {
    private static final Log log = LogFactory.getLog(ExtensionValidator.class);
    private static final StringManager sm = StringManager.getManager("org.apache.catalina.util");
    private static volatile List<Extension> containerAvailableExtensions = null;
    private static final List<ManifestResource> containerManifestResources = new ArrayList();

    static {
        String systemClasspath = System.getProperty("java.class.path");
        StringTokenizer strTok = new StringTokenizer(systemClasspath, File.pathSeparator);
        while (strTok.hasMoreTokens()) {
            String classpathItem = strTok.nextToken();
            if (classpathItem.toLowerCase(Locale.ENGLISH).endsWith(".jar")) {
                File item = new File(classpathItem);
                if (item.isFile()) {
                    try {
                        addSystemResource(item);
                    } catch (IOException e) {
                        log.error(sm.getString("extensionValidator.failload", item), e);
                    }
                }
            }
        }
        addFolderList("java.ext.dirs");
    }

    public static synchronized boolean validateApplication(WebResourceRoot resources, Context context) throws IOException {
        String appName = context.getName();
        List<ManifestResource> appManifestResources = new ArrayList<>();
        WebResource resource = resources.getResource("/META-INF/MANIFEST.MF");
        if (resource.isFile()) {
            InputStream inputStream = resource.getInputStream();
            Throwable th = null;
            try {
                Manifest manifest = new Manifest(inputStream);
                ManifestResource mre = new ManifestResource(sm.getString("extensionValidator.web-application-manifest"), manifest, 2);
                appManifestResources.add(mre);
                if (inputStream != null) {
                    if (0 != 0) {
                        try {
                            inputStream.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        inputStream.close();
                    }
                }
            } finally {
            }
        }
        WebResource[] manifestResources = resources.getClassLoaderResources("/META-INF/MANIFEST.MF");
        for (WebResource manifestResource : manifestResources) {
            if (manifestResource.isFile()) {
                String jarName = manifestResource.getURL().toExternalForm();
                Manifest jmanifest = manifestResource.getManifest();
                if (jmanifest != null) {
                    ManifestResource mre2 = new ManifestResource(jarName, jmanifest, 3);
                    appManifestResources.add(mre2);
                }
            }
        }
        return validateManifestResources(appName, appManifestResources);
    }

    public static void addSystemResource(File jarFile) throws IOException {
        InputStream is = new FileInputStream(jarFile);
        Throwable th = null;
        try {
            Manifest manifest = getManifest(is);
            if (manifest != null) {
                ManifestResource mre = new ManifestResource(jarFile.getAbsolutePath(), manifest, 1);
                containerManifestResources.add(mre);
            }
            if (is != null) {
                if (0 != 0) {
                    try {
                        is.close();
                        return;
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                        return;
                    }
                }
                is.close();
            }
        } catch (Throwable th3) {
            try {
                throw th3;
            } catch (Throwable th4) {
                if (is != null) {
                    if (th3 != null) {
                        try {
                            is.close();
                        } catch (Throwable th5) {
                            th3.addSuppressed(th5);
                        }
                    } else {
                        is.close();
                    }
                }
                throw th4;
            }
        }
    }

    private static boolean validateManifestResources(String appName, List<ManifestResource> resources) {
        boolean passes = true;
        int failureCount = 0;
        List<Extension> availableExtensions = null;
        for (ManifestResource mre : resources) {
            ArrayList<Extension> requiredList = mre.getRequiredExtensions();
            if (requiredList != null) {
                if (availableExtensions == null) {
                    availableExtensions = buildAvailableExtensionsList(resources);
                }
                if (containerAvailableExtensions == null) {
                    containerAvailableExtensions = buildAvailableExtensionsList(containerManifestResources);
                }
                Iterator<Extension> it = requiredList.iterator();
                while (it.hasNext()) {
                    Extension requiredExt = it.next();
                    boolean found = false;
                    if (availableExtensions != null) {
                        Iterator<Extension> it2 = availableExtensions.iterator();
                        while (true) {
                            if (!it2.hasNext()) {
                                break;
                            }
                            Extension targetExt = it2.next();
                            if (targetExt.isCompatibleWith(requiredExt)) {
                                requiredExt.setFulfilled(true);
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found && containerAvailableExtensions != null) {
                        Iterator<Extension> it3 = containerAvailableExtensions.iterator();
                        while (true) {
                            if (!it3.hasNext()) {
                                break;
                            }
                            Extension targetExt2 = it3.next();
                            if (targetExt2.isCompatibleWith(requiredExt)) {
                                requiredExt.setFulfilled(true);
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        log.info(sm.getString("extensionValidator.extension-not-found-error", appName, mre.getResourceName(), requiredExt.getExtensionName()));
                        passes = false;
                        failureCount++;
                    }
                }
            }
        }
        if (!passes) {
            log.info(sm.getString("extensionValidator.extension-validation-error", appName, failureCount + ""));
        }
        return passes;
    }

    private static List<Extension> buildAvailableExtensionsList(List<ManifestResource> resources) {
        List<Extension> availableList = null;
        for (ManifestResource mre : resources) {
            ArrayList<Extension> list = mre.getAvailableExtensions();
            if (list != null) {
                Iterator<Extension> it = list.iterator();
                while (it.hasNext()) {
                    Extension ext = it.next();
                    if (availableList == null) {
                        availableList = new ArrayList<>();
                        availableList.add(ext);
                    } else {
                        availableList.add(ext);
                    }
                }
            }
        }
        return availableList;
    }

    private static Manifest getManifest(InputStream inStream) throws IOException {
        JarInputStream jin = new JarInputStream(inStream);
        Throwable th = null;
        try {
            Manifest manifest = jin.getManifest();
            if (jin != null) {
                if (0 != 0) {
                    try {
                        jin.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                } else {
                    jin.close();
                }
            }
            return manifest;
        } finally {
        }
    }

    private static void addFolderList(String property) {
        File[] files;
        String extensionsDir = System.getProperty(property);
        if (extensionsDir != null) {
            StringTokenizer extensionsTok = new StringTokenizer(extensionsDir, File.pathSeparator);
            while (extensionsTok.hasMoreTokens()) {
                File targetDir = new File(extensionsTok.nextToken());
                if (targetDir.isDirectory() && (files = targetDir.listFiles()) != null) {
                    for (int i = 0; i < files.length; i++) {
                        if (files[i].getName().toLowerCase(Locale.ENGLISH).endsWith(".jar") && files[i].isFile()) {
                            try {
                                addSystemResource(files[i]);
                            } catch (IOException e) {
                                log.error(sm.getString("extensionValidator.failload", files[i]), e);
                            }
                        }
                    }
                }
            }
        }
    }
}