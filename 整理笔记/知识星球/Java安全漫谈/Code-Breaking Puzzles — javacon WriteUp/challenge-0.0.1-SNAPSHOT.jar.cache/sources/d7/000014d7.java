package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceEditor;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/propertyeditors/PathEditor.class */
public class PathEditor extends PropertyEditorSupport {
    private final ResourceEditor resourceEditor;

    public PathEditor() {
        this.resourceEditor = new ResourceEditor();
    }

    public PathEditor(ResourceEditor resourceEditor) {
        Assert.notNull(resourceEditor, "ResourceEditor must not be null");
        this.resourceEditor = resourceEditor;
    }

    public void setAsText(String text) throws IllegalArgumentException {
        boolean nioPathCandidate = !text.startsWith("classpath:");
        if (nioPathCandidate && !text.startsWith("/")) {
            try {
                URI uri = new URI(text);
                if (uri.getScheme() != null) {
                    nioPathCandidate = false;
                    setValue(Paths.get(uri).normalize());
                    return;
                }
            } catch (URISyntaxException | FileSystemNotFoundException e) {
            }
        }
        this.resourceEditor.setAsText(text);
        Resource resource = (Resource) this.resourceEditor.getValue();
        if (resource == null) {
            setValue(null);
        } else if (!resource.exists() && nioPathCandidate) {
            setValue(Paths.get(text, new String[0]).normalize());
        } else {
            try {
                setValue(resource.getFile().toPath());
            } catch (IOException ex) {
                throw new IllegalArgumentException("Failed to retrieve file for " + resource, ex);
            }
        }
    }

    public String getAsText() {
        Path value = (Path) getValue();
        return value != null ? value.toString() : "";
    }
}