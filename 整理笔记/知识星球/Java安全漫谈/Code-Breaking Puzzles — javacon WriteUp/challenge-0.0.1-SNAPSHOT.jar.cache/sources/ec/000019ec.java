package org.springframework.boot.loader.archive;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.jar.Manifest;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:org/springframework/boot/loader/archive/Archive.class */
public interface Archive extends Iterable<Entry> {

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:org/springframework/boot/loader/archive/Archive$Entry.class */
    public interface Entry {
        boolean isDirectory();

        String getName();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:org/springframework/boot/loader/archive/Archive$EntryFilter.class */
    public interface EntryFilter {
        boolean matches(Entry entry);
    }

    URL getUrl() throws MalformedURLException;

    Manifest getManifest() throws IOException;

    List<Archive> getNestedArchives(EntryFilter filter) throws IOException;
}