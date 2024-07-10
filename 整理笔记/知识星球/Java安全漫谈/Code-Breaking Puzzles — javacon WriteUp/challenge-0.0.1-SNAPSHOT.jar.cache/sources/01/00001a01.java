package org.springframework.boot.loader.jar;

import org.springframework.boot.loader.data.RandomAccessData;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:org/springframework/boot/loader/jar/CentralDirectoryVisitor.class */
public interface CentralDirectoryVisitor {
    void visitStart(CentralDirectoryEndRecord endRecord, RandomAccessData centralDirectoryData);

    void visitFileHeader(CentralDirectoryFileHeader fileHeader, int dataOffset);

    void visitEnd();
}