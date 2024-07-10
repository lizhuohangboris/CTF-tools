package org.springframework.boot.loader.jar;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:org/springframework/boot/loader/jar/JarEntryFilter.class */
interface JarEntryFilter {
    AsciiBytes apply(AsciiBytes name);
}