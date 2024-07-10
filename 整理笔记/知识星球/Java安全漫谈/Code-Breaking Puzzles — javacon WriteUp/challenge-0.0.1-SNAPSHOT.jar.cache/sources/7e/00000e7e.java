package org.attoparser.dom;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/dom/DOMWriter.class */
public final class DOMWriter {
    public static void write(INode node, Writer writer) throws IOException {
        if (node == null) {
            return;
        }
        if (node instanceof Text) {
            writeText((Text) node, writer);
        } else if (node instanceof Element) {
            writeElement((Element) node, writer);
        } else if (node instanceof Comment) {
            writeComment((Comment) node, writer);
        } else if (node instanceof CDATASection) {
            writeCDATASection((CDATASection) node, writer);
        } else if (node instanceof DocType) {
            writeDocType((DocType) node, writer);
        } else if (node instanceof Document) {
            writeDocument((Document) node, writer);
        } else if (node instanceof XmlDeclaration) {
            writeXmlDeclaration((XmlDeclaration) node, writer);
        } else if (node instanceof ProcessingInstruction) {
            writeProcessingInstruction((ProcessingInstruction) node, writer);
        }
    }

    public static void writeCDATASection(CDATASection cdataSection, Writer writer) throws IOException {
        writer.write("<![CDATA[");
        writer.write(cdataSection.getContent());
        writer.write("]]>");
    }

    public static void writeComment(Comment comment, Writer writer) throws IOException {
        writer.write("<!--");
        writer.write(comment.getContent());
        writer.write("-->");
    }

    public static void writeDocType(DocType docType, Writer writer) throws IOException {
        writer.write("<!DOCTYPE ");
        writer.write(docType.getRootElementName());
        String publicId = docType.getPublicId();
        String systemId = docType.getSystemId();
        String internalSubset = docType.getInternalSubset();
        if (publicId != null || systemId != null) {
            String type = publicId == null ? org.thymeleaf.engine.DocType.DEFAULT_TYPE_SYSTEM : org.thymeleaf.engine.DocType.DEFAULT_TYPE_PUBLIC;
            writer.write(32);
            writer.write(type);
            if (publicId != null) {
                writer.write(32);
                writer.write(34);
                writer.write(publicId);
                writer.write(34);
            }
            if (systemId != null) {
                writer.write(32);
                writer.write(34);
                writer.write(systemId);
                writer.write(34);
            }
        }
        if (internalSubset != null) {
            writer.write(32);
            writer.write(91);
            writer.write(internalSubset);
            writer.write(93);
        }
        writer.write(62);
    }

    public static void writeDocument(Document document, Writer writer) throws IOException {
        if (!document.hasChildren()) {
            return;
        }
        for (INode child : document.getChildren()) {
            write(child, writer);
        }
    }

    public static void writeElement(Element element, Writer writer) throws IOException {
        writer.write(60);
        writer.write(element.getElementName());
        if (element.hasAttributes()) {
            Map<String, String> attributes = element.getAttributeMap();
            for (Map.Entry<String, String> attributeEntry : attributes.entrySet()) {
                writer.write(32);
                writer.write(attributeEntry.getKey());
                writer.write(61);
                writer.write(34);
                writer.write(attributeEntry.getValue());
                writer.write(34);
            }
        }
        if (!element.hasChildren()) {
            writer.write(47);
            writer.write(62);
            return;
        }
        writer.write(62);
        for (INode child : element.getChildren()) {
            write(child, writer);
        }
        writer.write(60);
        writer.write(47);
        writer.write(element.getElementName());
        writer.write(62);
    }

    public static void writeProcessingInstruction(ProcessingInstruction processingInstruction, Writer writer) throws IOException {
        writer.write(60);
        writer.write(63);
        writer.write(processingInstruction.getTarget());
        String content = processingInstruction.getContent();
        if (content != null) {
            writer.write(32);
            writer.write(content);
        }
        writer.write(63);
        writer.write(62);
    }

    public static void writeText(Text text, Writer writer) throws IOException {
        validateNotNull(text, "Text node cannot be null");
        validateNotNull(writer, "Writer cannot be null");
        writer.write(text.getContent());
    }

    public static void writeXmlDeclaration(XmlDeclaration xmlDeclaration, Writer writer) throws IOException {
        validateNotNull(xmlDeclaration, "XML declaration cannot be null");
        validateNotNull(writer, "Writer cannot be null");
        writer.write("<?xml version=\"");
        writer.write(xmlDeclaration.getVersion());
        writer.write(34);
        String encoding = xmlDeclaration.getEncoding();
        if (encoding != null) {
            writer.write(" encoding=\"");
            writer.write(encoding);
            writer.write(34);
        }
        String standalone = xmlDeclaration.getStandalone();
        if (standalone != null) {
            writer.write(" standalone=\"");
            writer.write(standalone);
            writer.write(34);
        }
        writer.write(63);
        writer.write(62);
    }

    private static void validateNotNull(Object obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }
    }

    private DOMWriter() {
    }
}