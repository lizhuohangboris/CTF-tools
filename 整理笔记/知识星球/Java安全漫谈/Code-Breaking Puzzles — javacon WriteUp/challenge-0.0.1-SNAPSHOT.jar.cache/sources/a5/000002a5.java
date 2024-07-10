package com.fasterxml.jackson.core;

import java.io.IOException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-core-2.9.7.jar:com/fasterxml/jackson/core/TreeCodec.class */
public abstract class TreeCodec {
    public abstract <T extends TreeNode> T readTree(JsonParser jsonParser) throws IOException, JsonProcessingException;

    public abstract void writeTree(JsonGenerator jsonGenerator, TreeNode treeNode) throws IOException, JsonProcessingException;

    public abstract TreeNode createArrayNode();

    public abstract TreeNode createObjectNode();

    public abstract JsonParser treeAsTokens(TreeNode treeNode);
}