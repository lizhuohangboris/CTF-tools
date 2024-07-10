package org.apache.tomcat.jni;

import org.apache.catalina.Globals;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/jni/Library.class */
public final class Library {
    private static final String[] NAMES = {"tcnative-1", "libtcnative-1"};
    private static Library _instance = null;
    public static int TCN_MAJOR_VERSION = 0;
    public static int TCN_MINOR_VERSION = 0;
    public static int TCN_PATCH_VERSION = 0;
    public static int TCN_IS_DEV_VERSION = 0;
    public static int APR_MAJOR_VERSION = 0;
    public static int APR_MINOR_VERSION = 0;
    public static int APR_PATCH_VERSION = 0;
    public static int APR_IS_DEV_VERSION = 0;
    public static boolean APR_HAVE_IPV6 = false;
    public static boolean APR_HAS_SHARED_MEMORY = false;
    public static boolean APR_HAS_THREADS = false;
    public static boolean APR_HAS_SENDFILE = false;
    public static boolean APR_HAS_MMAP = false;
    public static boolean APR_HAS_FORK = false;
    public static boolean APR_HAS_RANDOM = false;
    public static boolean APR_HAS_OTHER_CHILD = false;
    public static boolean APR_HAS_DSO = false;
    public static boolean APR_HAS_SO_ACCEPTFILTER = false;
    public static boolean APR_HAS_UNICODE_FS = false;
    public static boolean APR_HAS_PROC_INVOKED = false;
    public static boolean APR_HAS_USER = false;
    public static boolean APR_HAS_LARGE_FILES = false;
    public static boolean APR_HAS_XTHREAD_FILES = false;
    public static boolean APR_HAS_OS_UUID = false;
    public static boolean APR_IS_BIGENDIAN = false;
    public static boolean APR_FILES_AS_SOCKETS = false;
    public static boolean APR_CHARSET_EBCDIC = false;
    public static boolean APR_TCP_NODELAY_INHERITED = false;
    public static boolean APR_O_NONBLOCK_INHERITED = false;
    public static int APR_SIZEOF_VOIDP;
    public static int APR_PATH_MAX;
    public static int APRMAXHOSTLEN;
    public static int APR_MAX_IOVEC_SIZE;
    public static int APR_MAX_SECS_TO_LINGER;
    public static int APR_MMAP_THRESHOLD;
    public static int APR_MMAP_LIMIT;

    private static native boolean initialize();

    public static native void terminate();

    private static native boolean has(int i);

    private static native int version(int i);

    private static native int size(int i);

    public static native String versionString();

    public static native String aprVersionString();

    public static native long globalPool();

    private Library() throws Exception {
        String[] strArr;
        boolean loaded = false;
        StringBuilder err = new StringBuilder();
        java.io.File binLib = new java.io.File(System.getProperty(Globals.CATALINA_HOME_PROP), "bin");
        for (int i = 0; i < NAMES.length; i++) {
            java.io.File library = new java.io.File(binLib, System.mapLibraryName(NAMES[i]));
            try {
                System.load(library.getPath());
                loaded = true;
            } catch (ThreadDeath t) {
                throw t;
            } catch (VirtualMachineError t2) {
                throw t2;
            } catch (Throwable t3) {
                if (library.exists()) {
                    throw t3;
                }
                if (i > 0) {
                    err.append(", ");
                }
                err.append(t3.getMessage());
            }
            if (loaded) {
                break;
            }
        }
        if (!loaded) {
            String path = System.getProperty("java.library.path");
            String[] paths = path.split(java.io.File.pathSeparator);
            for (int i2 = 0; i2 < NAMES.length; i2++) {
                try {
                    System.loadLibrary(NAMES[i2]);
                    loaded = true;
                } catch (ThreadDeath t4) {
                    throw t4;
                } catch (VirtualMachineError t5) {
                    throw t5;
                } catch (Throwable t6) {
                    String name = System.mapLibraryName(NAMES[i2]);
                    for (String str : paths) {
                        java.io.File fd = new java.io.File(str, name);
                        if (fd.exists()) {
                            throw t6;
                        }
                    }
                    if (i2 > 0) {
                        err.append(", ");
                    }
                    err.append(t6.getMessage());
                }
                if (loaded) {
                    break;
                }
            }
        }
        if (!loaded) {
            StringBuilder names = new StringBuilder();
            for (String name2 : NAMES) {
                names.append(name2);
                names.append(", ");
            }
            throw new LibraryNotFoundError(names.substring(0, names.length() - 2), err.toString());
        }
    }

    private Library(String libraryName) {
        System.loadLibrary(libraryName);
    }

    public static synchronized boolean initialize(String libraryName) throws Exception {
        if (_instance == null) {
            if (libraryName == null) {
                _instance = new Library();
            } else {
                _instance = new Library(libraryName);
            }
            TCN_MAJOR_VERSION = version(1);
            TCN_MINOR_VERSION = version(2);
            TCN_PATCH_VERSION = version(3);
            TCN_IS_DEV_VERSION = version(4);
            APR_MAJOR_VERSION = version(17);
            APR_MINOR_VERSION = version(18);
            APR_PATCH_VERSION = version(19);
            APR_IS_DEV_VERSION = version(20);
            APR_SIZEOF_VOIDP = size(1);
            APR_PATH_MAX = size(2);
            APRMAXHOSTLEN = size(3);
            APR_MAX_IOVEC_SIZE = size(4);
            APR_MAX_SECS_TO_LINGER = size(5);
            APR_MMAP_THRESHOLD = size(6);
            APR_MMAP_LIMIT = size(7);
            APR_HAVE_IPV6 = has(0);
            APR_HAS_SHARED_MEMORY = has(1);
            APR_HAS_THREADS = has(2);
            APR_HAS_SENDFILE = has(3);
            APR_HAS_MMAP = has(4);
            APR_HAS_FORK = has(5);
            APR_HAS_RANDOM = has(6);
            APR_HAS_OTHER_CHILD = has(7);
            APR_HAS_DSO = has(8);
            APR_HAS_SO_ACCEPTFILTER = has(9);
            APR_HAS_UNICODE_FS = has(10);
            APR_HAS_PROC_INVOKED = has(11);
            APR_HAS_USER = has(12);
            APR_HAS_LARGE_FILES = has(13);
            APR_HAS_XTHREAD_FILES = has(14);
            APR_HAS_OS_UUID = has(15);
            APR_IS_BIGENDIAN = has(16);
            APR_FILES_AS_SOCKETS = has(17);
            APR_CHARSET_EBCDIC = has(18);
            APR_TCP_NODELAY_INHERITED = has(19);
            APR_O_NONBLOCK_INHERITED = has(20);
            if (APR_MAJOR_VERSION < 1) {
                throw new UnsatisfiedLinkError("Unsupported APR Version (" + aprVersionString() + ")");
            }
            if (!APR_HAS_THREADS) {
                throw new UnsatisfiedLinkError("Missing APR_HAS_THREADS");
            }
        }
        return initialize();
    }
}